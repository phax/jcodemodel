/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCSecureLoader;

/**
 * Dynamically implements the typed annotation writer interfaces.
 *
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class TypedAnnotationWriter <A extends Annotation, W extends IJAnnotationWriter <A>> implements
                                   InvocationHandler,
                                   IJAnnotationWriter <A>
{
  /**
   * This is what we are writing to.
   */
  private final JAnnotationUse m_aUse;

  /**
   * The annotation that we are writing.
   */
  private final Class <A> m_aAnnotationType;

  /**
   * The type of the writer.
   */
  private final Class <W> m_aWriterType;

  /**
   * Keeps track of writers for array members. Lazily created.
   */
  private Map <String, JAnnotationArrayMember> m_aArrays;

  protected TypedAnnotationWriter (final Class <A> aAnnotation, final Class <W> aWriterType, final JAnnotationUse aUse)
  {
    m_aAnnotationType = aAnnotation;
    m_aWriterType = aWriterType;
    m_aUse = aUse;
  }

  public JAnnotationUse getAnnotationUse ()
  {
    return m_aUse;
  }

  public Class <A> getAnnotationType ()
  {
    return m_aAnnotationType;
  }

  public Object invoke (final Object aProxy, final Method aMethod, final Object [] aArgs) throws Throwable
  {
    if (aMethod.getDeclaringClass () == IJAnnotationWriter.class)
    {
      try
      {
        return aMethod.invoke (this, aArgs);
      }
      catch (final InvocationTargetException e)
      {
        throw e.getTargetException ();
      }
    }

    final String name = aMethod.getName ();
    Object arg = null;
    if (aArgs != null && aArgs.length > 0)
      arg = aArgs[0];

    // check how it's defined on the annotation
    final Method m = m_aAnnotationType.getDeclaredMethod (name);
    final Class <?> rt = m.getReturnType ();

    // array value
    if (rt.isArray ())
    {
      return _addArrayValue (aProxy, name, rt.getComponentType (), aMethod.getReturnType (), arg);
    }

    // sub annotation
    if (Annotation.class.isAssignableFrom (rt))
    {
      final Class <? extends Annotation> r = (Class <? extends Annotation>) rt;
      return new TypedAnnotationWriter (r, aMethod.getReturnType (), m_aUse.annotationParam (name, r))._createProxy ();
    }

    // scalar value

    if (arg instanceof AbstractJType)
    {
      final AbstractJType targ = (AbstractJType) arg;
      _checkType (Class.class, rt);
      if (m.getDefaultValue () != null)
      {
        // check the default
        if (targ.equals (targ.owner ().ref ((Class <?>) m.getDefaultValue ())))
          return aProxy; // defaulted
      }
      m_aUse.param (name, targ);
      return aProxy;
    }

    // other Java built-in types
    _checkType (arg.getClass (), rt);
    if (m.getDefaultValue () != null && m.getDefaultValue ().equals (arg))
    {
      /*
       * defaulted. no need to write out.
       */
      return aProxy;
    }
    if (arg instanceof String)
    {
      m_aUse.param (name, (String) arg);
      return aProxy;
    }
    if (arg instanceof Boolean)
    {
      m_aUse.param (name, ((Boolean) arg).booleanValue ());
      return aProxy;
    }
    if (arg instanceof Integer)
    {
      m_aUse.param (name, ((Integer) arg).intValue ());
      return aProxy;
    }
    if (arg instanceof Class <?>)
    {
      m_aUse.param (name, (Class <?>) arg);
      return aProxy;
    }
    if (arg instanceof Enum <?>)
    {
      m_aUse.param (name, (Enum <?>) arg);
      return aProxy;
    }

    throw new IllegalArgumentException ("Unable to handle this method call " + aMethod.toString ());
  }

  private Object _addArrayValue (final Object aProxy,
                                 final String sName,
                                 final Class <?> aItemType,
                                 final Class <?> aExpectedReturnType,
                                 final Object arg)
  {
    if (m_aArrays == null)
      m_aArrays = new HashMap <> ();
    final JAnnotationArrayMember m = m_aArrays.computeIfAbsent (sName, k -> m_aUse.paramArray (k));

    // sub annotation
    if (Annotation.class.isAssignableFrom (aItemType))
    {
      final Class <? extends Annotation> r = (Class <? extends Annotation>) aItemType;
      if (!IJAnnotationWriter.class.isAssignableFrom (aExpectedReturnType))
        throw new IllegalArgumentException ("Unexpected return type " + aExpectedReturnType);
      return new TypedAnnotationWriter (r, aExpectedReturnType, m.annotate (r))._createProxy ();
    }

    // primitive
    if (arg instanceof AbstractJType)
    {
      _checkType (Class.class, aItemType);
      m.param ((AbstractJType) arg);
      return aProxy;
    }
    _checkType (arg.getClass (), aItemType);
    if (arg instanceof String)
    {
      m.param ((String) arg);
      return aProxy;
    }
    if (arg instanceof Boolean)
    {
      m.param (((Boolean) arg).booleanValue ());
      return aProxy;
    }
    if (arg instanceof Integer)
    {
      m.param (((Integer) arg).intValue ());
      return aProxy;
    }
    if (arg instanceof Class <?>)
    {
      m.param ((Class <?>) arg);
      return aProxy;
    }
    // TODO: enum constant. how should we handle it?

    throw new IllegalArgumentException ("Unable to handle this method call ");
  }

  /**
   * Check if the type of the argument matches our expectation. If not, report
   * an error.
   */
  private void _checkType (final Class <?> aActual, final Class <?> aExpected)
  {
    if (aExpected == aActual || aExpected.isAssignableFrom (aActual))
      return; // no problem

    if (aExpected == JCodeModel.boxToPrimitive.get (aActual))
      return; // no problem

    throw new IllegalArgumentException ("Expected " + aExpected + " but found " + aActual);
  }

  /**
   * Creates a proxy and returns it.
   */
  private W _createProxy ()
  {
    return (W) Proxy.newProxyInstance (JCSecureLoader.getClassClassLoader (m_aWriterType),
                                       new Class [] { m_aWriterType },
                                       this);
  }

  /**
   * Creates a new typed annotation writer.
   */
  @Nonnull
  static <W extends IJAnnotationWriter <?>> W create (@Nonnull final Class <W> aWriterType,
                                                      @Nonnull final IJAnnotatable aAnnotatable)
  {
    final Class <? extends Annotation> a = _findAnnotationType (aWriterType);
    return (W) new TypedAnnotationWriter (a, aWriterType, aAnnotatable.annotate (a))._createProxy ();
  }

  @Nullable
  private static Class <? extends Annotation> _findAnnotationType (@Nonnull final Class <?> aClazz)
  {
    for (final Type t : aClazz.getGenericInterfaces ())
    {
      if (t instanceof ParameterizedType)
      {
        final ParameterizedType p = (ParameterizedType) t;
        if (p.getRawType () == IJAnnotationWriter.class)
          return (Class <? extends Annotation>) p.getActualTypeArguments ()[0];
      }
      if (t instanceof Class <?>)
      {
        // recursive search
        final Class <? extends Annotation> r = _findAnnotationType ((Class <?>) t);
        if (r != null)
          return r;
      }
    }
    return null;
  }
}
