/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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

import com.helger.commons.ValueEnforcer;
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
  private AbstractJType m_aReturnType;

  /**
   * Name of this method
   */
  private String m_sName;

  /**
   * List of parameters for this method's declaration
   */
  private final List <JVar> m_aParams = new ArrayList <> ();

  /**
   * Set of exceptions that this method may throw. A set instance lazily
   * created.
   */
  private final Set <AbstractJClass> m_aThrows = new TreeSet <> (ClassNameComparator.getInstance ());

  /**
   * JBlock of statements that makes up the body this method
   */
  private JBlock m_aBody;

  private final JDefinedClass m_aOwningClass;

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
   * @param aOwningClass
   *        Outer class. May not be <code>null</code>.
   * @param nMods
   *        Modifiers for this method's declaration
   * @param aReturnType
   *        Return type for the method. May not be <code>null</code>.
   * @param sName
   *        Name of this method. May neither be <code>null</code> nor empty.
   */
  protected JMethod (@Nonnull final JDefinedClass aOwningClass,
                     final int nMods,
                     @Nonnull final AbstractJType aReturnType,
                     @Nonnull final String sName)
  {
    ValueEnforcer.notNull (aOwningClass, "OwningClass");
    ValueEnforcer.notNull (aReturnType, "ReturnType");
    ValueEnforcer.notEmpty (sName, "Name");
    m_aMods = JMods.forMethod (nMods);
    m_aReturnType = aReturnType;
    m_sName = sName;
    m_aOwningClass = aOwningClass;
  }

  /**
   * Constructor for constructors
   *
   * @param nMods
   *        Modifiers for this constructor's declaration
   * @param aClass
   *        Class containing this constructor. May not be <code>null</code>.
   */
  protected JMethod (final int nMods, @Nonnull final JDefinedClass aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");
    m_aMods = JMods.forMethod (nMods);
    m_aReturnType = null;
    m_sName = aClass.name ();
    m_aOwningClass = aClass;
  }

  public boolean isConstructor ()
  {
    return m_aReturnType == null;
  }

  @Nonnull
  public Set <AbstractJClass> throwsMutable ()
  {
    return m_aThrows;
  }

  @Nonnull
  public Collection <AbstractJClass> getThrows ()
  {
    return Collections.unmodifiableSet (throwsMutable ());
  }

  /**
   * Add an exception to the list of exceptions that this method may throw.
   *
   * @param aException
   *        Name of an exception that this method may throw
   * @return this
   */
  @Nonnull
  public JMethod _throws (@Nonnull final AbstractJClass aException)
  {
    m_aThrows.add (aException);
    return this;
  }

  @Nonnull
  public JMethod _throws (@Nonnull final Class <? extends Throwable> aException)
  {
    return _throws (m_aOwningClass.owner ().ref (aException));
  }

  /**
   * Returns the list of variable of this method.
   *
   * @return List of parameters of this method. This list is modifiable.
   */
  @Nonnull
  public List <JVar> paramsMutable ()
  {
    return m_aParams;
  }

  /**
   * Returns the list of variable of this method.
   *
   * @return List of parameters of this method. This list is not modifiable.
   */
  @Nonnull
  public List <JVar> params ()
  {
    return Collections.unmodifiableList (paramsMutable ());
  }

  @Nonnull
  public JVar paramAtIndex (@Nonnegative final int nIndex) throws IndexOutOfBoundsException
  {
    return m_aParams.get (nIndex);
  }

  /**
   * Add the specified variable to the list of parameters for this method
   * signature.
   *
   * @param nMods
   *        Java modifiers to be used
   * @param aType
   *        JType of the parameter being added
   * @param sName
   *        Name of the parameter being added
   * @return New parameter variable of type {@link JVar}
   */
  @Nonnull
  public JVar param (final int nMods, @Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    final JVar aVar = new JVar (JMods.forVar (nMods), aType, sName, null);
    m_aParams.add (aVar);
    return aVar;
  }

  @Nonnull
  public JVar param (@Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    return param (JMod.NONE, aType, sName);
  }

  @Nonnull
  public JVar param (final int nMods, @Nonnull final Class <?> aType, @Nonnull final String sName)
  {
    return param (nMods, m_aOwningClass.owner ()._ref (aType), sName);
  }

  @Nonnull
  public JVar param (@Nonnull final Class <?> aType, @Nonnull final String sName)
  {
    return param (m_aOwningClass.owner ()._ref (aType), sName);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   *
   * @param aType
   *        Type of the parameter being added.
   * @param sName
   *        Name of the parameter being added
   * @return the variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  @Nonnull
  public JVar varParam (@Nonnull final Class <?> aType, @Nonnull final String sName)
  {
    return varParam (m_aOwningClass.owner ()._ref (aType), sName);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   *
   * @param aType
   *        Type of the parameter being added.
   * @param sName
   *        Name of the parameter being added
   * @return the variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  @Nonnull
  public JVar varParam (@Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    return varParam (JMod.NONE, aType, sName);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   *
   * @param nMods
   *        nMods to use
   * @param aType
   *        Type of the parameter being added. Is automatically converted to an
   *        array.
   * @param sName
   *        Name of the parameter being added
   * @return the created variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  @Nonnull
  public JVar varParam (final int nMods, @Nonnull final Class <?> aType, @Nonnull final String sName)
  {
    return varParam (nMods, m_aOwningClass.owner ()._ref (aType), sName);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   *
   * @param nMods
   *        nMods to use
   * @param aType
   *        Type of the parameter being added. Is automatically converted to an
   *        array.
   * @param sName
   *        Name of the parameter being added
   * @return the created variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  @Nonnull
  public JVar varParam (final int nMods, @Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    ValueEnforcer.isFalse (hasVarArgs (),
                           "Cannot have two varargs in a method,\n" + "Check if varParam method of JMethod is" + " invoked more than once");

    m_aVarParam = new JVar (JMods.forVar (nMods), aType.array (), sName, null);
    return m_aVarParam;
  }

  @Nullable
  public JVar varParam ()
  {
    return m_aVarParam;
  }

  /**
   * @return <code>true</code> if there are any varargs declared for this method
   *         signature.
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
   * @param aClazz
   *        The annotation class to annotate the field with
   * @return The created object. Never <code>null</code>.
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass aClazz)
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    final JAnnotationUse a = new JAnnotationUse (aClazz);
    m_aAnnotations.add (a);
    return a;
  }

  /**
   * Adds an annotation to this variable.
   *
   * @param aClazz
   *        The annotation class to annotate the field with
   * @return The created object. Never <code>null</code>.
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> aClazz)
  {
    return annotate (owner ().ref (aClazz));
  }

  @Nonnull
  public List <JAnnotationUse> annotationsMutable ()
  {
    if (m_aAnnotations == null)
      return Collections.emptyList ();
    return m_aAnnotations;
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    return Collections.unmodifiableList (annotationsMutable ());
  }

  public String name ()
  {
    return m_sName;
  }

  /**
   * Changes the name of the method.
   *
   * @param sName
   *        New name
   */
  public void name (@Nonnull final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    m_sName = sName;
  }

  /**
   * @return the return type. Is <code>null</code> for constructors.
   */
  @Nullable
  public AbstractJType type ()
  {
    return m_aReturnType;
  }

  /**
   * Overrides the return type.
   *
   * @param aReturnType
   *        The type to set. Set to <code>null</code> to make this method a
   *        constructor.
   */
  public void type (@Nullable final AbstractJType aReturnType)
  {
    m_aReturnType = aReturnType;
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
   * Returns true if the method has the specified signature.
   *
   * @param argTypes
   *        Signature to check
   * @return <code>true</code> if this method has the provided signature
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

  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJDoc == null)
      m_aJDoc = new JDocComment (owner ());
    return m_aJDoc;
  }

  @Override
  public void declare (@Nonnull final IJFormatter f)
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
      f.generable (m_aReturnType);
    f.id (m_sName).print ('(').indent ();
    // when parameters are printed in new lines, we want them to be indented.
    // there's a good chance no newlines happen, too, but just in case it does.

    boolean bFirst = true;
    // break only if more than 3 variables are present
    final boolean bNewLineAfterParam = (m_aParams.size () + (hasVarArgs () ? 1 : 0)) > 3;
    for (final JVar var : m_aParams)
    {
      if (bFirst)
        bFirst = false;
      else
      {
        f.print (',');
        if (bNewLineAfterParam)
          f.newline ();
      }
      f.var (var);
    }
    if (hasVarArgs ())
    {
      if (!bFirst)
      {
        f.print (',');
        if (bNewLineAfterParam)
          f.newline ();
      }
      for (final JAnnotationUse annotation : m_aVarParam.annotations ())
        f.generable (annotation).print (' ');
      f.generable (m_aVarParam.mods ()).generable (m_aVarParam.type ().elementType ()).print ("... ").id (m_aVarParam.name ());
    }

    f.outdent ().print (')');
    if (!m_aThrows.isEmpty ())
    {
      f.newline ().indent ().print ("throws").generable (m_aThrows).newline ().outdent ();
    }

    if (m_aDefaultValue != null)
    {
      // For annotation values
      f.print ("default ");
      f.generable (m_aDefaultValue);
    }
    if (m_aBody != null)
    {
      f.statement (m_aBody);
    }
    else
    {
      final boolean bIsDeclarationOnly = (m_aOwningClass.isInterface () && !m_aMods.isDefault ()) ||
                                         m_aOwningClass.isAnnotationTypeDeclaration () ||
                                         m_aMods.isAbstract () ||
                                         m_aMods.isNative ();

      if (bIsDeclarationOnly)
      {
        f.print (';').newline ();
      }
      else
      {
        // Print an empty body for non-native, non-abstract methods
        f.statement (new JBlock ());
      }
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

  /**
   * @return The {@link JDefinedClass} to which this methods belongs. Never
   *         <code>null</code>.
   */
  @Nonnull
  public JDefinedClass owningClass ()
  {
    return m_aOwningClass;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aOwningClass.owner ();
  }
}
