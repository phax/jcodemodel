/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.expressions.typed;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJGenerable;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JLambdaMethodRef;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JReferencedClass;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.JFormatter;
import com.helger.jcodemodel.writer.SourcePrintWriter;

/**
 * Derives a method reference from a typed call, so that the method reference has exactly the type
 * of the equivalent lambda. The call is created on a placeholder for the lambda parameter and must
 * have one of these shapes:
 * <ul>
 * <li><code>p -&gt; p.m()</code> becomes <code>Type::m</code></li>
 * <li><code>p -&gt; Type.m(p)</code> becomes <code>Type::m</code></li>
 * <li><code>p -&gt; expr.m(p)</code> becomes <code>expr::m</code></li>
 * <li><code>p -&gt; new Type(p)</code> becomes <code>Type::new</code></li>
 * </ul>
 * and the same without <code>p</code> for functions without parameter.
 */
final class MethodRefHelper
{
  /** Stands for the lambda parameter while the typed call is created */
  private static final class Probe implements IJExpression
  {
    private boolean m_bGenerated;

    public void generate (@NonNull final IJFormatter f)
    {
      m_bGenerated = true;
      f.print ("p");
    }
  }

  private MethodRefHelper ()
  {}

  private static boolean _contains (@NonNull final IJGenerable aGenerable, @NonNull final Probe aProbe)
  {
    aProbe.m_bGenerated = false;
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
         final JFormatter aFormatter = new JFormatter (new SourcePrintWriter (aSW, JCMWriter.DEFAULT_NEW_LINE),
                                                       new FormatterSettings ()))
    {
      aFormatter.generable (aGenerable);
    }
    return aProbe.m_bGenerated;
  }

  private static boolean _fits (final int nParams, final boolean bVarArgs, final int nArgs)
  {
    return bVarArgs ? nArgs >= nParams - 1 : nArgs == nParams;
  }

  /**
   * @return <code>true</code> if the class may declare or inherit a method with the passed name,
   *         staticness and argument count. Classes that cannot be inspected may have any method.
   */
  private static boolean _mayHaveMethodRecursive (@Nullable final AbstractJClass aClass,
                                                  @NonNull final String sName,
                                                  final boolean bStatic,
                                                  final int nArgs)
  {
    if (aClass == null)
      return false;
    if (aClass.isArray ())
    {
      // Arrays have no static methods and all instance methods of Object
      return !bStatic;
    }

    final AbstractJClass aErasure = aClass.erasure ();
    if (aErasure instanceof final JReferencedClass aRefClass)
    {
      for (final Method aMethod : aRefClass.getReferencedClass ().getDeclaredMethods ())
        if (aMethod.getName ().equals (sName) &&
            Modifier.isStatic (aMethod.getModifiers ()) == bStatic &&
            _fits (aMethod.getParameterCount (), aMethod.isVarArgs (), nArgs))
          return true;
    }
    else
      if (aErasure instanceof final JDefinedClass aDefClass)
      {
        for (final JMethod aMethod : aDefClass.methods ())
          if (aMethod.name ().equals (sName) &&
              aMethod.mods ().isStatic () == bStatic &&
              _fits (aMethod.params ().size () + (aMethod.hasVarArgs () ? 1 : 0), aMethod.hasVarArgs (), nArgs))
            return true;
      }
      else
        return true;

    if (_mayHaveMethodRecursive (aErasure._extends (), sName, bStatic, nArgs))
      return true;
    final Iterator <AbstractJClass> aIt = aErasure._implements ();
    while (aIt.hasNext ())
      if (_mayHaveMethodRecursive (aIt.next (), sName, bStatic, nArgs))
        return true;
    return false;
  }

  @NonNull
  private static JLambdaMethodRef _fromCall (@Nullable final AbstractJType aParamType,
                                             @Nullable final Probe aProbe,
                                             @NonNull final IJExpression aCall)
  {
    if (!(aCall instanceof final JInvocation aInv))
      throw new IllegalArgumentException ("A method reference needs a method or constructor invocation, not " +
                                          aCall.getClass ().getName ());
    if (!aInv.typeParamList ().isEmpty ())
      throw new IllegalArgumentException ("A method reference cannot have explicit type arguments");

    final List <IJExpression> aArgs = aInv.args ();
    // Without parameter there are no arguments, otherwise the parameter is the only argument
    final boolean bArgsArePassed = aProbe == null ? aArgs.isEmpty () : aArgs.size () == 1 && aArgs.get (0) == aProbe;

    if (aInv.isConstructor ())
    {
      final AbstractJType aType = aInv.constructorType ();
      if (aType.isArray () || (aType instanceof final JDefinedClass aDefClass && aDefClass.isAnonymous ()))
        throw new IllegalArgumentException ("Only a class instance creation can become 'Type::new'");
      if (!bArgsArePassed)
        throw new IllegalArgumentException ("The arguments of a constructor reference must be the lambda parameters");
      // Type arguments are inferred, like with the diamond
      return JLambdaMethodRef.createForNew (aType.erasure ());
    }

    final String sName = aInv.methodName ();
    final IJGenerable aObject = aInv.object ();
    if (aObject == null)
      throw new IllegalArgumentException ("The unqualified invocation of '" + sName + "' has no method reference");

    if (aObject instanceof final AbstractJClass aClass)
    {
      // Static method: with a parameter, javac also searches the instance methods without
      // arguments and rejects the method reference if both searches find something
      if (!bArgsArePassed)
        throw new IllegalArgumentException ("The arguments of '" + sName + "' must be the lambda parameters");
      if (aProbe != null && _mayHaveMethodRecursive (aClass, sName, false, 0))
        throw new IllegalArgumentException ("The method reference to '" + sName + "' may be ambiguous");
      return new JLambdaMethodRef (aClass, sName);
    }

    if (aProbe != null && aObject == aProbe && aArgs.isEmpty ())
    {
      // Instance method of the parameter: javac also searches the static methods with the
      // parameter as argument and rejects the method reference if both searches find something
      if (!(aParamType instanceof final AbstractJClass aParamClass) ||
          _mayHaveMethodRecursive (aParamClass, sName, true, 1))
        throw new IllegalArgumentException ("The method reference to '" + sName + "' may be ambiguous");
      return new JLambdaMethodRef (aParamType, sName);
    }

    // Bound receiver, which is evaluated once when the method reference is created
    if (!(aObject instanceof final IJExpression aReceiver) || (aProbe != null && _contains (aReceiver, aProbe)))
      throw new IllegalArgumentException ("The receiver of '" + sName + "' may not use the lambda parameter");
    if (!bArgsArePassed)
      throw new IllegalArgumentException ("The arguments of '" + sName + "' must be the lambda parameters");
    return new JLambdaMethodRef (aReceiver, sName);
  }

  /**
   * @param aCall
   *        The call without lambda parameter. May not be <code>null</code>.
   * @return The method reference with the same meaning as <code>() -&gt; call</code>. Never
   *         <code>null</code>.
   * @throws IllegalArgumentException
   *         If the call has no equivalent method reference.
   */
  @NonNull
  static JLambdaMethodRef fromCall (@NonNull final IJExpression aCall)
  {
    return _fromCall (null, null, aCall);
  }

  /**
   * @param <M>
   *        The typed mirror of the lambda parameter.
   * @param aCM
   *        The code model to resolve the parameter type in. May not be <code>null</code>.
   * @param aParamType
   *        The lambda parameter type. May not be <code>null</code>.
   * @param aCall
   *        Creates the call from the typed lambda parameter. May not be <code>null</code>.
   * @return The method reference with the same meaning as <code>p -&gt; call</code>. Never
   *         <code>null</code>.
   * @throws IllegalArgumentException
   *         If the call has no equivalent method reference.
   */
  @NonNull
  static <M extends ITypedExpression <?>> JLambdaMethodRef fromCall (@NonNull final JCodeModel aCM,
                                                                     @NonNull final TypedType <M, ?> aParamType,
                                                                     @NonNull final Function <? super M, ? extends IJExpression> aCall)
  {
    final Probe aProbe = new Probe ();
    return _fromCall (aParamType.type (aCM), aProbe, aCall.apply (aParamType.wrap (aProbe)));
  }
}
