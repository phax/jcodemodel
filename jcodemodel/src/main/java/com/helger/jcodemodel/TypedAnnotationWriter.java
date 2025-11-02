/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.reflection.GenericReflection;
import com.helger.jcodemodel.util.JCSecureLoader;

/**
 * Dynamically implements the typed annotation writer interfaces.
 *
 * @author Kohsuke Kawaguchi
 * @param <ANNOTYPE>
 *        Annotation type
 * @param <WRITERTYPE>
 *        Annotation writer type
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class TypedAnnotationWriter <ANNOTYPE extends Annotation, WRITERTYPE extends IJAnnotationWriter <? extends ANNOTYPE>>
                                   implements
                                   InvocationHandler,
                                   IJAnnotationWriter <ANNOTYPE>
{
  /**
   * The annotation that we are writing.
   */
  private final Class <ANNOTYPE> m_aAnnotationType;

  /**
   * The type of the writer.
   */
  private final Class <WRITERTYPE> m_aWriterType;

  /**
   * This is what we are writing to.
   */
  private final JAnnotationUse m_aUse;

  /**
   * Keeps track of writers for array members. Lazily created.
   */
  private Map <String, JAnnotationArrayMember> m_aArrays;

  protected TypedAnnotationWriter (@NonNull final Class <ANNOTYPE> aAnnotation,
                                   @NonNull final Class <WRITERTYPE> aWriterType,
                                   @NonNull final JAnnotationUse aUse)
  {
    m_aAnnotationType = aAnnotation;
    m_aWriterType = aWriterType;
    m_aUse = aUse;
  }

  @NonNull
  public JAnnotationUse getAnnotationUse ()
  {
    return m_aUse;
  }

  @NonNull
  public Class <ANNOTYPE> getAnnotationType ()
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
      final Class <? extends Annotation> r = GenericReflection.uncheckedCast (rt);
      return new TypedAnnotationWriter (r, aMethod.getReturnType (), m_aUse.annotationParam (name, r))._createProxy ();
    }

    // scalar value

    if (arg instanceof final AbstractJType targ)
    {
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
    if (arg != null)
      _checkType (arg.getClass (), rt);

    if (m.getDefaultValue () != null && m.getDefaultValue ().equals (arg))
    {
      /*
       * defaulted. no need to write out.
       */
      return aProxy;
    }
    if (arg instanceof final String x)
    {
      m_aUse.param (name, x);
      return aProxy;
    }
    if (arg instanceof final Boolean x)
    {
      m_aUse.param (name, x.booleanValue ());
      return aProxy;
    }
    if (arg instanceof final Integer x)
    {
      m_aUse.param (name, x.intValue ());
      return aProxy;
    }
    if (arg instanceof final Class <?> x)
    {
      m_aUse.param (name, x);
      return aProxy;
    }
    if (arg instanceof final Enum <?> x)
    {
      m_aUse.param (name, x);
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
    if (arg instanceof final AbstractJType x)
    {
      _checkType (Class.class, aItemType);
      m.param (x);
      return aProxy;
    }

    _checkType (arg.getClass (), aItemType);
    if (arg instanceof final String x)
    {
      m.param (x);
      return aProxy;
    }
    if (arg instanceof final Boolean x)
    {
      m.param (x.booleanValue ());
      return aProxy;
    }
    if (arg instanceof final Integer x)
    {
      m.param (x.intValue ());
      return aProxy;
    }
    if (arg instanceof final Class <?> x)
    {
      m.param (x);
      return aProxy;
    }
    // TODO: enum constant. how should we handle it?

    throw new IllegalArgumentException ("Unable to handle this method call ");
  }

  /**
   * Check if the type of the argument matches our expectation. If not, report an error.
   */
  private void _checkType (final Class <?> aActual, final Class <?> aExpected)
  {
    if (aExpected == aActual || aExpected.isAssignableFrom (aActual))
      return; // no problem

    if (aExpected == JCodeModel.BOX_TO_PRIMITIVE.get (aActual))
      return; // no problem

    throw new IllegalArgumentException ("Expected " + aExpected + " but found " + aActual);
  }

  /**
   * Creates a proxy and returns it.
   */
  private WRITERTYPE _createProxy ()
  {
    return GenericReflection.uncheckedCast (Proxy.newProxyInstance (JCSecureLoader.getClassClassLoader (m_aWriterType),
                                                                    new Class [] { m_aWriterType },
                                                                    this));
  }

  @Nullable
  private static Class <? extends Annotation> _findAnnotationTypeRecursive (@NonNull final Class <?> aClazz)
  {
    for (final Type t : aClazz.getGenericInterfaces ())
    {
      if (t instanceof final ParameterizedType p)
      {
        if (p.getRawType () == IJAnnotationWriter.class)
        {
          // Return the annotation class the IJAnnotationWriter is writing
          return GenericReflection.uncheckedCast (p.getActualTypeArguments ()[0]);
        }
      }

      if (t instanceof final Class <?> c)
      {
        // recursive search
        final Class <? extends Annotation> ret = _findAnnotationTypeRecursive (c);
        if (ret != null)
          return ret;
      }
    }
    return null;
  }

  /**
   * Creates a new typed annotation writer.
   */
  @NonNull
  static <W extends IJAnnotationWriter <? extends Annotation>> W create (@NonNull final Class <W> aWriterType,
                                                                         @NonNull final IJAnnotatable aAnnotatable)
  {
    final Class <? extends Annotation> a = _findAnnotationTypeRecursive (aWriterType);
    return (W) new TypedAnnotationWriter (a, aWriterType, aAnnotatable.annotate (a))._createProxy ();
  }
}
