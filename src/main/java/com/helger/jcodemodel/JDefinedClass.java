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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.ClassNameComparator;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * A generated Java class/interface/enum/....
 * <p>
 * This class models a declaration, and since a declaration can be always used
 * as a reference, it inherits {@link AbstractJClass}.
 * <h2>Where to go from here?</h2>
 * <p>
 * You'd want to generate fields and methods on a class. See
 * {@link #method(int, AbstractJType, String)} and
 * {@link #field(int, AbstractJType, String)}.
 */
public class JDefinedClass extends AbstractJClassContainer <JDefinedClass> implements
                           IJGenerifiable,
                           IJAnnotatable,
                           IJDocCommentable
{
  /**
   * The optional header that is emitted prior to the package (Issue #47)
   */
  private JDocComment m_aHeaderComment;

  /**
   * Modifiers for the class declaration
   */
  private JMods m_aMods;

  /**
   * Name of the super class of this class.
   */
  private AbstractJClass m_aSuperClass;

  /**
   * List of interfaces that this class implements
   */
  private final Set <AbstractJClass> m_aInterfaces = new TreeSet <> (ClassNameComparator.getInstance ());

  /**
   * Fields keyed by their names.
   */
  private final Map <String, JFieldVar> m_aFields = new LinkedHashMap <> ();

  /**
   * Static initializer, if this class has one
   */
  private JBlock m_aStaticInit;

  /**
   * Instance initializer, if this class has one
   */
  private JBlock m_aInstanceInit;

  /**
   * class javadoc
   */
  private JDocComment m_aJDoc;

  /**
   * Set of constructors for this class, if any
   */
  private final List <JMethod> m_aConstructors = new ArrayList <> ();

  /**
   * Set of methods that are members of this class
   */
  private final List <JMethod> m_aMethods = new ArrayList <> ();

  /**
   * Flag that controls whether this class should be really generated or not.
   * Sometimes it is useful to generate code that refers to class X, without
   * actually generating the code of X. This flag is used to suppress X.java
   * file in the output.
   */
  private boolean m_bHideFile = false;

  /**
   * String that will be put directly inside the generated code. Can be null.
   */
  private String m_sDirectBlock;

  /**
   * Set of enum constants that are keyed by names. In Java, enum constant order
   * is actually significant, because of order ID they get. So let's preserve
   * the order.
   */
  private final Map <String, JEnumConstant> m_aEnumConstantsByName = new LinkedHashMap <> ();

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> m_aAnnotations;

  /**
   * Helper class to implement {@link IJGenerifiable}.
   */
  private final IJGenerifiable m_aGenerifiable = new AbstractJGenerifiableImpl ()
  {
    @Nonnull
    public JCodeModel owner ()
    {
      // The owner is same as the owner of this defined class's owner
      return JDefinedClass.this.owner ();
    }
  };

  protected JDefinedClass (@Nonnull final IJClassContainer <?> aParent,
                           final int nMods,
                           @Nullable final String sName,
                           @Nonnull final EClassType eClassType)
  {
    this (aParent.owner (), aParent, nMods, eClassType, sName);
  }

  /**
   * Constructor for creating anonymous inner class.
   *
   * @param aOwner
   *        Owning code model
   * @param nMods
   *        Java modifier
   * @param sName
   *        Name of this class
   */
  protected JDefinedClass (@Nonnull final JCodeModel aOwner, final int nMods, @Nullable final String sName)
  {
    this (aOwner, null, nMods, EClassType.CLASS, sName);
  }

  /**
   * JClass constructor
   *
   * @param aOwner
   *        Owning code model
   * @param aOuter
   *        Outer class or package
   * @param nMods
   *        Modifiers for this class declaration
   * @param eClassType
   *        Class type to use
   * @param sName
   *        Name of this class
   */
  private JDefinedClass (@Nonnull final JCodeModel aOwner,
                         @Nullable final IJClassContainer <?> aOuter,
                         final int nMods,
                         @Nonnull final EClassType eClassType,
                         @Nullable final String sName)
  {
    super (aOwner, aOuter, eClassType, sName);

    if (sName != null)
    {
      JCValueEnforcer.notEmpty (sName, "Name");

      if (!Character.isJavaIdentifierStart (sName.charAt (0)))
      {
        final String msg = "JDefinedClass name " +
                           sName +
                           " contains illegal character" +
                           " for beginning of identifier: " +
                           sName.charAt (0);
        throw new IllegalArgumentException (msg);
      }
      for (int i = 1; i < sName.length (); i++)
      {
        final char c = sName.charAt (i);
        if (!Character.isJavaIdentifierPart (c))
        {
          final String msg = "JDefinedClass name " + sName + " contains illegal character " + c;
          throw new IllegalArgumentException (msg);
        }
      }
    }

    if (isInterface ())
      m_aMods = JMods.forInterface (nMods);
    else
      m_aMods = JMods.forClass (nMods);
  }

  /**
   * @return the current modifiers of this class. Always return non-null valid
   *         object.
   */
  @Nonnull
  public JMods mods ()
  {
    return m_aMods;
  }

  /**
   * This class extends the specified class.
   *
   * @param aSuperClass
   *        Superclass for this class
   * @return This class
   */
  @Nonnull
  public JDefinedClass _extends (@Nonnull final AbstractJClass aSuperClass)
  {
    JCValueEnforcer.notNull (aSuperClass, "SuperClass");
    if (isInterface ())
    {
      if (aSuperClass.isInterface ())
        return _implements (aSuperClass);
      throw new IllegalArgumentException ("unable to set the super class for an interface");
    }

    for (AbstractJClass o = aSuperClass.outer (); o != null; o = o.outer ())
    {
      if (this == o)
      {
        throw new IllegalArgumentException ("Illegal class inheritance loop." +
                                            "  Outer class " +
                                            name () +
                                            " may not subclass from inner class: " +
                                            o.name ());
      }
    }

    m_aSuperClass = aSuperClass;
    return this;
  }

  @Nonnull
  public JDefinedClass _extends (@Nonnull final Class <?> aSuperClass)
  {
    return _extends (owner ().ref (aSuperClass));
  }

  /**
   * Returns the class extended by this class.
   */
  @Override
  @Nonnull
  public AbstractJClass _extends ()
  {
    if (m_aSuperClass == null)
      m_aSuperClass = owner ().ref (Object.class);
    return m_aSuperClass;
  }

  /**
   * This class implements the specifed interface.
   *
   * @param aInterface
   *        Interface that this class implements
   * @return This class
   */
  @Nonnull
  public JDefinedClass _implements (@Nonnull final AbstractJClass aInterface)
  {
    m_aInterfaces.add (aInterface);
    return this;
  }

  @Nonnull
  public JDefinedClass _implements (@Nonnull final Class <?> aInterface)
  {
    return _implements (owner ().ref (aInterface));
  }

  /**
   * Returns an iterator that walks the nested classes defined in this class.
   */
  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    return m_aInterfaces.iterator ();
  }

  /**
   * If the named enum already exists, the reference to it is returned.
   * Otherwise this method generates a new enum reference with the given name
   * and returns it.
   *
   * @param sName
   *        The name of the constant.
   * @return The generated type-safe enum constant.
   */
  @Nonnull
  public JEnumConstant enumConstant (@Nonnull final String sName)
  {
    return m_aEnumConstantsByName.computeIfAbsent (sName, k -> new JEnumConstant (this, k));
  }

  @Override
  public String binaryName ()
  {
    if (getOuter () instanceof AbstractJClassContainer <?>)
      return ((AbstractJClassContainer <?>) getOuter ()).binaryName () + '$' + name ();

    // FIXME This is incorrect, e.g. for anonymous classes!
    return fullName ();
  }

  @Override
  public boolean isAbstract ()
  {
    return m_aMods.isAbstract ();
  }

  /**
   * Adds a field to the list of field members of this JDefinedClass.
   *
   * @param nMods
   *        Modifiers for this field
   * @param aType
   *        JType of this field
   * @param sName
   *        Name of this field
   * @return Newly generated field
   */
  public JFieldVar field (final int nMods, final AbstractJType aType, final String sName)
  {
    return field (nMods, aType, sName, null);
  }

  public JFieldVar field (final int nMods, final Class <?> aType, final String sName)
  {
    return field (nMods, owner ()._ref (aType), sName);
  }

  /**
   * Adds a field to the list of field members of this JDefinedClass.
   *
   * @param nMods
   *        Modifiers for this field.
   * @param aType
   *        JType of this field.
   * @param sName
   *        Name of this field.
   * @param aInit
   *        Initial value of this field.
   * @return Newly generated field
   */
  @Nonnull
  public JFieldVar field (final int nMods,
                          @Nonnull final AbstractJType aType,
                          @Nonnull final String sName,
                          @Nullable final IJExpression aInit)
  {
    JCValueEnforcer.isFalse (m_aFields.containsKey (sName), () -> "trying to create the same field twice: " + sName);

    final JFieldVar f = new JFieldVar (this, JMods.forField (nMods), aType, sName, aInit);
    m_aFields.put (sName, f);
    return f;
  }

  @Nonnull
  public JFieldVar field (final int nMods, final Class <?> aType, final String sName, final IJExpression aInit)
  {
    return field (nMods, owner ()._ref (aType), sName, aInit);
  }

  /**
   * Returns all the fields declared in this class. The returned {@link Map} is
   * a read-only live view.
   *
   * @return always non-null.
   */
  @Nonnull
  public Map <String, JFieldVar> fields ()
  {
    return Collections.unmodifiableMap (m_aFields);
  }

  /**
   * Removes a {@link JFieldVar} from this class.
   *
   * @param aField
   *        Field to be removed
   * @throws IllegalArgumentException
   *         if the given field is not a field on this class.
   */
  public void removeField (@Nonnull final JFieldVar aField)
  {
    if (m_aFields.remove (aField.name ()) != aField)
      throw new IllegalArgumentException ("Failed to remove field " + aField);
  }

  /**
   * @param sName
   *        Field name to check. May be <code>null</code>.
   * @return <code>true</code> if such a field is contained, <code>false</code>
   *         otherwise.
   */
  public boolean containsField (@Nullable final String sName)
  {
    return sName != null && m_aFields.containsKey (sName);
  }

  void internalRenameField (@Nonnull final String sOldName,
                            @Nonnull final String sNewName,
                            @Nonnull final JFieldVar aField)
  {
    if (m_aFields.remove (sOldName) == null)
      throw new IllegalArgumentException ("Failed to remove field with name '" +
                                          sOldName +
                                          "' for replacement with field with name '" +
                                          sNewName +
                                          "'");
    m_aFields.put (sNewName, aField);
  }

  /**
   * Creates, if necessary, and returns the static initializer for this class.
   *
   * @return JBlock containing initialization statements for this class
   */
  @Nonnull
  public JBlock init ()
  {
    if (m_aStaticInit == null)
      m_aStaticInit = new JBlock ();
    return m_aStaticInit;
  }

  /**
   * Creates, if necessary, and returns the instance initializer for this class.
   *
   * @return JBlock containing initialization statements for this class
   */
  @Nonnull
  public JBlock instanceInit ()
  {
    if (m_aInstanceInit == null)
      m_aInstanceInit = new JBlock ();
    return m_aInstanceInit;
  }

  /**
   * Adds a constructor to this class.
   *
   * @param nMods
   *        Modifiers for this constructor
   * @return Newly created {@link JMethod}
   */
  @Nonnull
  public JMethod constructor (final int nMods)
  {
    final JMethod c = new JMethod (nMods, this);
    m_aConstructors.add (c);
    return c;
  }

  /**
   * @return an iterator that walks the constructors defined in this class.
   */
  @Nonnull
  public Iterator <JMethod> constructors ()
  {
    return m_aConstructors.iterator ();
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   *
   * @param aArgTypes
   *        Signature to search
   * @return <code>null</code> if not found.
   */
  @Nullable
  public JMethod getConstructor (@Nonnull final AbstractJType [] aArgTypes)
  {
    for (final JMethod m : m_aConstructors)
      if (m.hasSignature (aArgTypes))
        return m;
    return null;
  }

  /**
   * Add a method to the list of method members of this JDefinedClass instance.
   *
   * @param nMods
   *        Modifiers for this method
   * @param aType
   *        Return type for this method
   * @param sName
   *        Name of the method
   * @return Newly generated {@link JMethod}
   */
  @Nonnull
  public JMethod method (final int nMods, @Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    // XXX problems caught in M constructor
    final JMethod m = new JMethod (this, nMods, aType, sName);
    m_aMethods.add (m);
    return m;
  }

  @Nonnull
  public JMethod method (final int nMods, final Class <?> aType, final String sName)
  {
    return method (nMods, owner ()._ref (aType), sName);
  }

  /**
   * @return the set of methods defined in this class.
   */
  @Nonnull
  public Collection <JMethod> methods ()
  {
    return m_aMethods;
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   *
   * @param sName
   *        Method name to search
   * @param aArgTypes
   *        Signature to search
   * @return <code>null</code> if not found.
   */
  @Nullable
  public JMethod getMethod (final String sName, final AbstractJType [] aArgTypes)
  {
    for (final JMethod m : m_aMethods)
      if (m.name ().equals (sName))
        if (m.hasSignature (aArgTypes))
          return m;
    return null;
  }

  /**
   * @return <code>true</code> if a header comment (before the package) is
   *         present, <code>false</code> if not.
   */
  public boolean hasHeaderComment ()
  {
    return m_aHeaderComment != null;
  }

  /**
   * @return The optional header comment that is emitted BEFORE the package (so
   *         e.g. for license headers). Never <code>null</code>.
   */
  @Nonnull
  public JDocComment headerComment ()
  {
    if (m_aHeaderComment == null)
      m_aHeaderComment = new JDocComment (owner ());
    return m_aHeaderComment;
  }

  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJDoc == null)
      m_aJDoc = new JDocComment (owner ());
    return m_aJDoc;
  }

  /**
   * Mark this file as hidden, so that this file won't be generated. <br>
   * This feature could be used to generate code that refers to class X, without
   * actually generating X.java.
   */
  public void hide ()
  {
    m_bHideFile = true;
  }

  public boolean isHidden ()
  {
    return m_bHideFile;
  }

  public void declare (@Nonnull final JFormatter f)
  {
    // Java docs
    if (m_aJDoc != null)
      f.newline ().generable (m_aJDoc);

    // Class annotations
    if (m_aAnnotations != null)
      for (final JAnnotationUse aAnnotation : m_aAnnotations)
        f.generable (aAnnotation).newline ();

    // Modifiers (private, protected, public)
    // Type of class (class, interface, enum, @interface)
    // Name of the class
    // Class wildcards
    f.generable (m_aMods).print (getClassType ().declarationToken ()).id (name ()).declaration (m_aGenerifiable);

    // If a super class is defined and is not "Object"
    boolean bHasSuperClass = false;
    if (m_aSuperClass != null && m_aSuperClass != owner ().ref (Object.class))
    {
      bHasSuperClass = true;
      f.newline ().indent ().print ("extends").generable (m_aSuperClass).newline ().outdent ();
    }

    // Add all interfaces
    if (!m_aInterfaces.isEmpty ())
    {
      if (!bHasSuperClass)
        f.newline ();
      f.indent ().print (isInterface () ? "extends" : "implements");
      f.generable (m_aInterfaces);
      f.newline ().outdent ();
    }

    declareBody (f);
  }

  /**
   * prints the body of a class.
   *
   * @param f
   *        Formatter to use
   */
  protected void declareBody (@Nonnull final JFormatter f)
  {
    f.print ('{').newline ().indent ();
    boolean bFirst = true;

    if (!m_aEnumConstantsByName.isEmpty ())
    {
      for (final JEnumConstant c : m_aEnumConstantsByName.values ())
      {
        if (bFirst)
          bFirst = false;
        else
          f.print (',').newline ();
        f.declaration (c);
      }
      f.print (';').newline ();
    }

    // All fields
    for (final JFieldVar field : m_aFields.values ())
      f.declaration (field);

    // Static init
    if (m_aStaticInit != null)
      f.newline ().print ("static").statement (m_aStaticInit);

    // Instance init
    if (m_aInstanceInit != null)
      f.newline ().statement (m_aInstanceInit);

    // All constructors
    for (final JMethod m : m_aConstructors)
      f.newline ().declaration (m);

    // All regular methods
    for (final JMethod m : m_aMethods)
      f.newline ().declaration (m);

    // All inner classes
    if (m_aClasses != null)
      for (final JDefinedClass dc : m_aClasses.values ())
        f.newline ().declaration (dc);

    // Hacks...
    if (m_sDirectBlock != null)
      f.print (m_sDirectBlock);

    f.outdent ().print ('}').newline ();
  }

  /**
   * Places the given string directly inside the generated class. This method
   * can be used to add methods/fields that are not generated by CodeModel. This
   * method should be used only as the last resort.
   *
   * @param string
   *        Direct code block
   */
  public void direct (@Nullable final String string)
  {
    if (m_sDirectBlock == null)
      m_sDirectBlock = string;
    else
      if (string != null)
        m_sDirectBlock += string;
  }

  @Override
  @Nonnull
  public final JPackage _package ()
  {
    IJClassContainer <?> p = getOuter ();
    while (!(p instanceof JPackage))
      p = p.parentContainer ();
    return (JPackage) p;
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String sName)
  {
    return m_aGenerifiable.generify (sName);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String sName, @Nonnull final Class <?> aBoundClass)
  {
    return m_aGenerifiable.generify (sName, aBoundClass);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String sName, @Nonnull final AbstractJClass aBoundClass)
  {
    return m_aGenerifiable.generify (sName, aBoundClass);
  }

  @Override
  @Nonnull
  public JTypeVar [] typeParams ()
  {
    return m_aGenerifiable.typeParams ();
  }

  @Override
  protected AbstractJClass substituteParams (final JTypeVar [] aVariables,
                                             final List <? extends AbstractJClass> aBindings)
  {
    return this;
  }

  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> aClazz)
  {
    return annotate (owner ().ref (aClazz));
  }

  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass aClazz)
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    final JAnnotationUse a = new JAnnotationUse (aClazz);
    m_aAnnotations.add (a);
    return a;
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    return Collections.unmodifiableCollection (m_aAnnotations);
  }

  @Nullable
  public JAnnotationUse getAnnotation (final Class <?> aAnnotationClass)
  {
    for (final JAnnotationUse jannotation : m_aAnnotations)
    {
      final AbstractJClass jannotationClass = jannotation.getAnnotationClass ();
      if (!jannotationClass.isError ())
      {
        final String sQualifiedName = jannotationClass.fullName ();
        if (sQualifiedName != null && sQualifiedName.equals (aAnnotationClass.getName ()))
        {
          return jannotation;
        }
      }
    }
    return null;
  }

  @Override
  @Nonnull
  protected JDefinedClass createInnerClass (final int nMods, final EClassType eClassType, final String sName)
  {
    return new JDefinedClass (this, nMods, sName, eClassType);
  }

  /**
   * Returns true if this class or it's inner classes contains references to
   * error-types.
   *
   * @return <code>true</code> if an error type is contained, <code>false</code>
   *         otherwise
   * @see JErrorClass
   */
  public boolean containsErrorTypes ()
  {
    return JFormatter.containsErrorTypes (this);
  }
}
