/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.ClassNameComparator;

/**
 * Java method.
 */
public class JMethod extends AbstractJGenerifiableImpl implements IJAnnotatable, IJDocCommentable
{
  /**
   * Modifiers for this method
   */
  private final JMods m_aMods;

  /**
   * Return type for this method
   */
  private AbstractJType m_aType;

  /**
   * Name of this method
   */
  private String m_sName;

  /**
   * List of parameters for this method's declaration
   */
  private final List <JVar> m_aParams = new ArrayList <JVar> ();

  /**
   * Set of exceptions that this method may throw. A set instance lazily
   * created.
   */
  private Set <AbstractJClass> m_aThrows;

  /**
   * JBlock of statements that makes up the body this method
   */
  private JBlock m_aBody;

  private final JDefinedClass m_aOuter;

  /**
   * javadoc comments for this JMethod
   */
  private JDocComment m_aJDoc;

  /**
   * Variable parameter for this method's varargs declaration introduced in J2SE
   * 1.5
   */
  private JVar m_aVarParam;

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> m_aAnnotations;
  /**
   * To set the default value for the annotation member
   */
  private IJExpression m_aDefaultValue;

  /**
   * Constructor for regular methods
   *
   * @param mods
   *        Modifiers for this method's declaration
   * @param type
   *        Return type for the method
   * @param name
   *        Name of this method
   */
  protected JMethod (@Nonnull final JDefinedClass outer, final int mods, final AbstractJType type, final String name)
  {
    m_aMods = JMods.forMethod (mods);
    m_aType = type;
    m_sName = name;
    m_aOuter = outer;
  }

  /**
   * Constructor for constructors
   *
   * @param mods
   *        Modifiers for this constructor's declaration
   * @param aClass
   *        JClass containing this constructor
   */
  protected JMethod (final int mods, @Nonnull final JDefinedClass aClass)
  {
    m_aMods = JMods.forMethod (mods);
    m_aType = null;
    m_sName = aClass.name ();
    m_aOuter = aClass;
  }

  public boolean isConstructor ()
  {
    return m_aType == null;
  }

  @Nonnull
  public Collection <AbstractJClass> getThrows ()
  {
    if (m_aThrows == null)
      return Collections.emptySet ();
    return Collections.unmodifiableSet (m_aThrows);
  }

  /**
   * Add an exception to the list of exceptions that this method may throw.
   *
   * @param exception
   *        Name of an exception that this method may throw
   * @return this
   */
  @Nonnull
  public JMethod _throws (@Nonnull final AbstractJClass exception)
  {
    if (m_aThrows == null)
      m_aThrows = new TreeSet <AbstractJClass> (ClassNameComparator.getInstance ());
    m_aThrows.add (exception);
    return this;
  }

  @Nonnull
  public JMethod _throws (@Nonnull final Class <? extends Throwable> exception)
  {
    return _throws (m_aOuter.owner ().ref (exception));
  }

  /**
   * Returns the list of variable of this method.
   *
   * @return List of parameters of this method. This list is not modifiable.
   */
  @Nonnull
  public List <JVar> params ()
  {
    return Collections.unmodifiableList (m_aParams);
  }

  @Nonnull
  public JVar paramAtIndex (@Nonnegative final int index)
  {
    return m_aParams.get (index);
  }

  /**
   * Add the specified variable to the list of parameters for this method
   * signature.
   *
   * @param type
   *        JType of the parameter being added
   * @param name
   *        Name of the parameter being added
   * @return New parameter variable
   */
  @Nonnull
  public JVar param (final int mods, @Nonnull final AbstractJType type, @Nonnull final String name)
  {
    final JVar aVar = new JVar (JMods.forVar (mods), type, name, null);
    m_aParams.add (aVar);
    return aVar;
  }

  @Nonnull
  public JVar param (@Nonnull final AbstractJType type, @Nonnull final String name)
  {
    return param (JMod.NONE, type, name);
  }

  @Nonnull
  public JVar param (final int mods, @Nonnull final Class <?> type, @Nonnull final String name)
  {
    return param (mods, m_aOuter.owner ()._ref (type), name);
  }

  @Nonnull
  public JVar param (@Nonnull final Class <?> type, @Nonnull final String name)
  {
    return param (m_aOuter.owner ()._ref (type), name);
  }

  /**
   * @see #varParam(AbstractJType, String)
   */
  @Nonnull
  public JVar varParam (@Nonnull final Class <?> type, @Nonnull final String name)
  {
    return varParam (m_aOuter.owner ()._ref (type), name);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   *
   * @param type
   *        Type of the parameter being added.
   * @param name
   *        Name of the parameter being added
   * @return the variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  @Nonnull
  public JVar varParam (@Nonnull final AbstractJType type, @Nonnull final String name)
  {
    return varParam (JMod.NONE, type, name);
  }

  /**
   * @see #varParam(int, AbstractJType, String)
   */
  @Nonnull
  public JVar varParam (final int mods, @Nonnull final Class <?> type, @Nonnull final String name)
  {
    return varParam (mods, m_aOuter.owner ()._ref (type), name);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   *
   * @param mods
   *        mods to use
   * @param type
   *        Type of the parameter being added. Is automatically converted to an
   *        array.
   * @param name
   *        Name of the parameter being added
   * @return the created variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  @Nonnull
  public JVar varParam (final int mods, @Nonnull final AbstractJType type, @Nonnull final String name)
  {
    if (hasVarArgs ())
      throw new IllegalStateException ("Cannot have two varargs in a method,\n"
                                       + "Check if varParam method of JMethod is"
                                       + " invoked more than once");

    m_aVarParam = new JVar (JMods.forVar (mods), type.array (), name, null);
    return m_aVarParam;
  }

  @Nullable
  public JVar varParam ()
  {
    return m_aVarParam;
  }

  /**
   * Check if there are any varargs declared for this method signature.
   */
  public boolean hasVarArgs ()
  {
    return m_aVarParam != null;
  }

  /**
   * Returns the varags parameter type.
   *
   * @return If there's no vararg parameter type, null will be returned.
   */
  @Nullable
  public AbstractJType listVarParamType ()
  {
    return m_aVarParam != null ? m_aVarParam.type () : null;
  }

  /**
   * Adds an annotation to this variable.
   *
   * @param clazz
   *        The annotation class to annotate the field with
   * @return The created object. Never <code>null</code>.
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass clazz)
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <JAnnotationUse> ();
    final JAnnotationUse a = new JAnnotationUse (clazz);
    m_aAnnotations.add (a);
    return a;
  }

  /**
   * Adds an annotation to this variable.
   *
   * @param clazz
   *        The annotation class to annotate the field with
   * @return The created object. Never <code>null</code>.
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> clazz)
  {
    return annotate (owner ().ref (clazz));
  }

  @Nonnull
  public <W extends IJAnnotationWriter <?>> W annotate2 (@Nonnull final Class <W> clazz)
  {
    return TypedAnnotationWriter.create (clazz, this);
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (m_aAnnotations == null)
      return Collections.emptyList ();
    return Collections.unmodifiableList (m_aAnnotations);
  }

  public String name ()
  {
    return m_sName;
  }

  /**
   * Changes the name of the method.
   */
  public void name (final String n)
  {
    m_sName = n;
  }

  /**
   * Returns the return type. Is <code>null</code> for constructors.
   */
  @Nullable
  public AbstractJType type ()
  {
    return m_aType;
  }

  /**
   * Overrides the return type.
   *
   * @param t
   *        The type to set. Set to <code>null</code> to make this method a
   *        constructor.
   */
  public void type (@Nullable final AbstractJType t)
  {
    m_aType = t;
  }

  /**
   * Returns all the parameter types in an array.
   *
   * @return If there's no parameter, an empty array will be returned.
   */
  @Nonnull
  public AbstractJType [] listParamTypes ()
  {
    final AbstractJType [] r = new AbstractJType [m_aParams.size ()];
    for (int i = 0; i < r.length; i++)
      r[i] = m_aParams.get (i).type ();
    return r;
  }

  /**
   * Returns all the parameters in an array.
   *
   * @return If there's no parameter, an empty array will be returned.
   */
  @Nonnull
  public JVar [] listParams ()
  {
    return m_aParams.toArray (new JVar [m_aParams.size ()]);
  }

  /**
   * Returns the variable parameter
   *
   * @return If there's no parameter, null will be returned.
   * @deprecated Use {@link #varParam()} instead.
   */
  @Nullable
  @Deprecated
  public JVar listVarParam ()
  {
    return varParam ();
  }

  /**
   * Returns true if the method has the specified signature.
   */
  public boolean hasSignature (@Nonnull final AbstractJType [] argTypes)
  {
    final JVar [] aParams = listParams ();
    if (aParams.length != argTypes.length)
      return false;

    for (int i = 0; i < aParams.length; i++)
      if (!aParams[i].type ().equals (argTypes[i]))
        return false;

    return true;
  }

  /**
   * Get the block that makes up body of this method
   *
   * @return Body of method. Never <code>null</code>.
   */
  @Nonnull
  public JBlock body ()
  {
    if (m_aBody == null)
      m_aBody = new JBlock ();
    return m_aBody;
  }

  /**
   * Specify the default value for this method
   *
   * @param aDefaultValue
   *        Default value for the method
   */
  public void declareDefaultValue (@Nullable final IJExpression aDefaultValue)
  {
    m_aDefaultValue = aDefaultValue;
  }

  /**
   * Creates, if necessary, and returns the class javadoc for this
   * {@link JMethod}.
   *
   * @return JDocComment containing javadocs for this class. Never
   *         <code>null</code>.
   */
  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJDoc == null)
      m_aJDoc = new JDocComment (owner ());
    return m_aJDoc;
  }

  @Override
  public void declare (@Nonnull final JFormatter f)
  {
    if (m_aJDoc != null)
      f.generable (m_aJDoc);

    if (m_aAnnotations != null)
      for (final JAnnotationUse a : m_aAnnotations)
        f.generable (a).newline ();

    f.generable (m_aMods);

    // declare the generics parameters
    super.declare (f);

    if (!isConstructor ())
      f.generable (m_aType);
    f.id (m_sName).print ('(').indent ();
    // when parameters are printed in new lines, we want them to be indented.
    // there's a good chance no newlines happen, too, but just in case it does.
    boolean first = true;
    for (final JVar var : m_aParams)
    {
      if (!first)
        f.print (',');
      if (var.isAnnotated ())
        f.newline ();
      f.var (var);
      first = false;
    }
    if (hasVarArgs ())
    {
      if (!first)
        f.print (',');
      for (final JAnnotationUse annotation : m_aVarParam.annotations ())
        f.generable (annotation).newline ();
      f.generable (m_aVarParam.mods ()).generable (m_aVarParam.type ().elementType ());
      f.print ("... ");
      f.id (m_aVarParam.name ());
    }

    f.outdent ().print (')');
    if (m_aThrows != null && !m_aThrows.isEmpty ())
    {
      f.newline ().indent ().print ("throws").generable (m_aThrows).newline ().outdent ();
    }

    if (m_aDefaultValue != null)
    {
      f.print ("default ");
      f.generable (m_aDefaultValue);
    }
    if (m_aBody != null)
    {
      f.statement (m_aBody);
    }
    else
      if (!m_aOuter.isInterface () &&
          !m_aOuter.isAnnotationTypeDeclaration () &&
          !m_aMods.isAbstract () &&
          !m_aMods.isNative ())
      {
        // Print an empty body for non-native, non-abstract methods
        f.statement (new JBlock ());
      }
      else
      {
        f.print (';').newline ();
      }
  }

  /**
   * @return the current modifiers of this method. Always return non-null valid
   *         object.
   */
  @Nonnull
  public JMods mods ()
  {
    return m_aMods;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aOuter.owner ();
  }
}
