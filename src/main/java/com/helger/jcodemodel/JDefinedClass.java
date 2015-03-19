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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.ClassNameComparator;

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
public class JDefinedClass extends AbstractJClass implements IJDeclaration, IJClassContainer, IJGenerifiable, IJAnnotatable, IJDocCommentable
{
  /**
   * Name of this class. <code>null</code> if anonymous.
   */
  private final String m_sName;

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
  private final Set <AbstractJClass> m_aInterfaces = new TreeSet <AbstractJClass> (ClassNameComparator.getInstance ());

  /**
   * Fields keyed by their names.
   */
  /* package */final Map <String, JFieldVar> fields = new LinkedHashMap <String, JFieldVar> ();

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
  private final List <JMethod> m_aConstructors = new ArrayList <JMethod> ();

  /**
   * Set of methods that are members of this class
   */
  private final List <JMethod> m_aMethods = new ArrayList <JMethod> ();

  /**
   * Nested classes as a map from name to JDefinedClass. The name is all
   * capitalized in a case sensitive file system (
   * {@link JCodeModel#isCaseSensitiveFileSystem}) to avoid conflicts. Lazily
   * created to save footprint.
   *
   * @see #_getClasses()
   */
  private Map <String, JDefinedClass> m_aClasses;

  /**
   * Flag that controls whether this class should be really generated or not.
   * Sometimes it is useful to generate code that refers to class X, without
   * actually generating the code of X. This flag is used to suppress X.java
   * file in the output.
   */
  private boolean m_bHideFile = false;

  /**
   * Client-app specific metadata associated with this user-created class.
   */
  public Object metadata;

  /**
   * String that will be put directly inside the generated code. Can be null.
   */
  private String m_sDirectBlock;

  /**
   * If this is a package-member class, this is {@link JPackage}. If this is a
   * nested class, this is {@link JDefinedClass}. If this is an anonymous class,
   * this constructor shouldn't be used.
   */
  private final IJClassContainer m_aOuter;

  /**
   * Default value is class or interface or annotationTypeDeclaration or enum
   */
  private final EClassType m_eClassType;

  /**
   * List containing the enum value declarations
   */
  // private List enumValues = new ArrayList();

  /**
   * Set of enum constants that are keyed by names. In Java, enum constant order
   * is actually significant, because of order ID they get. So let's preserve
   * the order.
   */
  private final Map <String, JEnumConstant> m_aEnumConstantsByName = new LinkedHashMap <String, JEnumConstant> ();

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> m_aAnnotations;

  /**
   * Helper class to implement {@link IJGenerifiable}.
   */
  private final AbstractJGenerifiableImpl m_aGenerifiable = new AbstractJGenerifiableImpl ()
  {
    @Nonnull
    public JCodeModel owner ()
    {
      // The owner is same as the owner of this defined class's owner
      return JDefinedClass.this.owner ();
    }
  };

  protected JDefinedClass (@Nonnull final IJClassContainer aParent,
                           final int nMods,
                           @Nullable final String sName,
                           @Nonnull final EClassType eClassType)
  {
    this (aParent.owner (), aParent, nMods, eClassType, sName);
  }

  /**
   * Constructor for creating anonymous inner class.
   */
  protected JDefinedClass (@Nonnull final JCodeModel aOwner, final int nMods, @Nullable final String sName)
  {
    this (aOwner, null, nMods, EClassType.CLASS, sName);
  }

  /**
   * JClass constructor
   *
   * @param nMods
   *        Modifiers for this class declaration
   * @param sName
   *        Name of this class
   */
  private JDefinedClass (@Nonnull final JCodeModel aOwner,
                         @Nullable final IJClassContainer aOuter,
                         final int nMods,
                         @Nonnull final EClassType eClassTypeVal,
                         @Nullable final String sName)
  {
    super (aOwner);

    if (sName != null)
    {
      if (sName.trim ().length () == 0)
        throw new IllegalArgumentException ("JClass name empty");

      if (!Character.isJavaIdentifierStart (sName.charAt (0)))
      {
        final String msg = "JClass name " +
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
          final String msg = "JClass name " + sName + " contains illegal character " + c;
          throw new IllegalArgumentException (msg);
        }
      }
    }

    m_eClassType = eClassTypeVal;
    if (isInterface ())
      m_aMods = JMods.forInterface (nMods);
    else
      m_aMods = JMods.forClass (nMods);

    m_sName = sName;
    m_aOuter = aOuter;
  }

  /**
   * @return <code>true</code> if this is an anonymous class.
   */
  public final boolean isAnonymous ()
  {
    return m_sName == null;
  }

  /**
   * This class extends the specified class.
   *
   * @param superClass
   *        Superclass for this class
   * @return This class
   */
  @Nonnull
  public JDefinedClass _extends (@Nonnull final AbstractJClass superClass)
  {
    if (m_eClassType == EClassType.INTERFACE)
    {
      if (superClass.isInterface ())
        return _implements (superClass);
      throw new IllegalArgumentException ("unable to set the super class for an interface");
    }
    if (superClass == null)
      throw new NullPointerException ();

    for (AbstractJClass o = superClass.outer (); o != null; o = o.outer ())
    {
      if (this == o)
      {
        throw new IllegalArgumentException ("Illegal class inheritance loop." +
                                            "  Outer class " +
                                            m_sName +
                                            " may not subclass from inner class: " +
                                            o.name ());
      }
    }

    m_aSuperClass = superClass;
    return this;
  }

  @Nonnull
  public JDefinedClass _extends (@Nonnull final Class <?> superClass)
  {
    return _extends (owner ().ref (superClass));
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
   * @param iface
   *        Interface that this class implements
   * @return This class
   */
  @Nonnull
  public JDefinedClass _implements (@Nonnull final AbstractJClass iface)
  {
    m_aInterfaces.add (iface);
    return this;
  }

  @Nonnull
  public JDefinedClass _implements (@Nonnull final Class <?> iface)
  {
    return _implements (owner ().ref (iface));
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
   * JClass name accessor.
   * <p>
   * For example, for <code>java.util.List</code>, this method returns
   * <code>"List"</code>"
   *
   * @return Name of this class
   */
  @Override
  @Nullable
  public String name ()
  {
    return m_sName;
  }

  /**
   * If the named enum already exists, the reference to it is returned.
   * Otherwise this method generates a new enum reference with the given name
   * and returns it.
   *
   * @param name
   *        The name of the constant.
   * @return The generated type-safe enum constant.
   */
  @Nonnull
  public JEnumConstant enumConstant (@Nonnull final String name)
  {
    JEnumConstant ec = m_aEnumConstantsByName.get (name);
    if (null == ec)
    {
      ec = new JEnumConstant (this, name);
      m_aEnumConstantsByName.put (name, ec);
    }
    return ec;
  }

  /**
   * Gets the fully qualified name of this class.
   */
  @Override
  @Nullable
  public String fullName ()
  {
    if (m_aOuter instanceof JDefinedClass)
      return ((JDefinedClass) m_aOuter).fullName () + '.' + name ();

    final JPackage p = _package ();
    if (p.isUnnamed ())
      return name ();
    return p.name () + '.' + name ();
  }

  @Override
  public String binaryName ()
  {
    if (m_aOuter instanceof JDefinedClass)
      return ((JDefinedClass) m_aOuter).binaryName () + '$' + name ();

    // FIXME This is incorrect, e.g. for anonymous classes!
    return fullName ();
  }

  @Override
  public boolean isInterface ()
  {
    return m_eClassType == EClassType.INTERFACE;
  }

  @Override
  public boolean isAbstract ()
  {
    return m_aMods.isAbstract ();
  }

  /**
   * Adds a field to the list of field members of this JDefinedClass.
   *
   * @param mods
   *        Modifiers for this field
   * @param type
   *        JType of this field
   * @param name
   *        Name of this field
   * @return Newly generated field
   */
  public JFieldVar field (final int mods, final AbstractJType type, final String name)
  {
    return field (mods, type, name, null);
  }

  public JFieldVar field (final int mods, final Class <?> type, final String name)
  {
    return field (mods, owner ()._ref (type), name);
  }

  /**
   * Adds a field to the list of field members of this JDefinedClass.
   *
   * @param mods
   *        Modifiers for this field.
   * @param type
   *        JType of this field.
   * @param name
   *        Name of this field.
   * @param init
   *        Initial value of this field.
   * @return Newly generated field
   */
  @Nonnull
  public JFieldVar field (final int mods, final AbstractJType type, final String name, final IJExpression init)
  {
    final JFieldVar f = new JFieldVar (this, JMods.forField (mods), type, name, init);
    if (fields.containsKey (name))
      throw new IllegalArgumentException ("trying to create the same field twice: " + name);

    fields.put (name, f);
    return f;
  }

  /**
   * This method indicates if the interface is an annotationTypeDeclaration
   */
  public boolean isAnnotationTypeDeclaration ()
  {
    return m_eClassType == EClassType.ANNOTATION_TYPE_DECL;
  }

  /**
   * Add an annotationType Declaration to this package
   *
   * @param name
   *        Name of the annotation Type declaration to be added to this package
   * @return newly created Annotation Type Declaration
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _annotationTypeDeclaration (final String name) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, name, EClassType.ANNOTATION_TYPE_DECL);
  }

  /**
   * Add a public enum to this package
   *
   * @param name
   *        Name of the enum to be added to this package
   * @return newly created Enum
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _enum (final String name) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, name, EClassType.ENUM);
  }

  /**
   * Add a public enum to this package
   *
   * @param name
   *        Name of the enum to be added to this package
   * @param mods
   *        Modifiers for this enum declaration
   * @return newly created Enum
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _enum (final int mods, final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.ENUM);
  }

  @Nonnull
  public EClassType getClassType ()
  {
    return m_eClassType;
  }

  @Nonnull
  public JFieldVar field (final int mods, final Class <?> type, final String name, final IJExpression init)
  {
    return field (mods, owner ()._ref (type), name, init);
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
    return Collections.unmodifiableMap (fields);
  }

  /**
   * Removes a {@link JFieldVar} from this class.
   *
   * @throws IllegalArgumentException
   *         if the given field is not a field on this class.
   */
  public void removeField (@Nonnull final JFieldVar field)
  {
    if (fields.remove (field.name ()) != field)
      throw new IllegalArgumentException ();
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
   * @param mods
   *        Modifiers for this constructor
   */
  @Nonnull
  public JMethod constructor (final int mods)
  {
    final JMethod c = new JMethod (mods, this);
    m_aConstructors.add (c);
    return c;
  }

  /**
   * Returns an iterator that walks the constructors defined in this class.
   */
  @Nonnull
  public Iterator <JMethod> constructors ()
  {
    return m_aConstructors.iterator ();
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   *
   * @return null if not found.
   */
  @Nullable
  public JMethod getConstructor (@Nonnull final AbstractJType [] argTypes)
  {
    for (final JMethod m : m_aConstructors)
      if (m.hasSignature (argTypes))
        return m;
    return null;
  }

  /**
   * Add a method to the list of method members of this JDefinedClass instance.
   *
   * @param mods
   *        Modifiers for this method
   * @param type
   *        Return type for this method
   * @param name
   *        Name of the method
   * @return Newly generated JMethod
   */
  @Nonnull
  public JMethod method (final int mods, final AbstractJType type, final String name)
  {
    // XXX problems caught in M constructor
    final JMethod m = new JMethod (this, mods, type, name);
    m_aMethods.add (m);
    return m;
  }

  @Nonnull
  public JMethod method (final int mods, final Class <?> type, final String name)
  {
    return method (mods, owner ()._ref (type), name);
  }

  /**
   * Returns the set of methods defined in this class.
   */
  @Nonnull
  public Collection <JMethod> methods ()
  {
    return m_aMethods;
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   *
   * @return null if not found.
   */
  @Nullable
  public JMethod getMethod (final String name, final AbstractJType [] argTypes)
  {
    for (final JMethod m : m_aMethods)
    {
      if (!m.name ().equals (name))
        continue;

      if (m.hasSignature (argTypes))
        return m;
    }
    return null;
  }

  public boolean isClass ()
  {
    return true;
  }

  public boolean isPackage ()
  {
    return false;
  }

  public JPackage getPackage ()
  {
    return parentContainer ().getPackage ();
  }

  /**
   * Add a new nested class to this class.
   *
   * @param mods
   *        Modifiers for this class declaration
   * @param name
   *        Name of class to be added to this package
   * @return Newly generated class
   */
  @Nonnull
  public JDefinedClass _class (final int mods, @Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.CLASS);
  }

  @Nonnull
  public JDefinedClass _class (final int mods, @Nonnull final String name, @Nonnull final EClassType classTypeVal) throws JClassAlreadyExistsException
  {
    String sRealName;
    if (owner ().isCaseSensitiveFileSystem)
      sRealName = name.toUpperCase ();
    else
      sRealName = name;

    final JDefinedClass aExistingClass = _getClasses ().get (sRealName);
    if (aExistingClass != null)
      throw new JClassAlreadyExistsException (aExistingClass);

    // XXX problems caught in the NC constructor
    final JDefinedClass c = new JDefinedClass (this, mods, name, classTypeVal);
    _getClasses ().put (sRealName, c);
    return c;
  }

  /**
   * Add a new public nested class to this class.
   */
  @Nonnull
  public JDefinedClass _class (@Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, name);
  }

  /**
   * Add an interface to this package.
   *
   * @param mods
   *        Modifiers for this interface declaration
   * @param name
   *        Name of interface to be added to this package
   * @return Newly generated interface
   */
  @Nonnull
  public JDefinedClass _interface (final int mods, @Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.INTERFACE);
  }

  /**
   * Adds a public interface to this package.
   */
  @Nonnull
  public JDefinedClass _interface (@Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _interface (JMod.PUBLIC, name);
  }

  /**
   * Creates, if necessary, and returns the class javadoc for this JDefinedClass
   *
   * @return JDocComment containing javadocs for this class
   */
  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJDoc == null)
      m_aJDoc = new JDocComment (owner ());
    return m_aJDoc;
  }

  /**
   * Mark this file as hidden, so that this file won't be generated.
   * <p>
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

  /**
   * Returns an iterator that walks the nested classes defined in this class.
   */
  @Nonnull
  public final Collection <JDefinedClass> classes ()
  {
    if (m_aClasses == null)
      return Collections.<JDefinedClass> emptyList ();
    return m_aClasses.values ();
  }

  @Nonnull
  private Map <String, JDefinedClass> _getClasses ()
  {
    if (m_aClasses == null)
      m_aClasses = new TreeMap <String, JDefinedClass> ();
    return m_aClasses;
  }

  /**
   * Returns all the nested classes defined in this class.
   */
  @Nonnull
  public final AbstractJClass [] listClasses ()
  {
    if (m_aClasses == null)
      return new AbstractJClass [0];
    return m_aClasses.values ().toArray (new AbstractJClass [m_aClasses.values ().size ()]);
  }

  @Override
  @Nullable
  public AbstractJClass outer ()
  {
    if (m_aOuter.isClass ())
      return (AbstractJClass) m_aOuter;
    return null;
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
    f.generable (m_aMods).print (m_eClassType.declarationToken ()).id (m_sName).declaration (m_aGenerifiable);

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
      f.indent ().print (m_eClassType == EClassType.INTERFACE ? "extends" : "implements");
      f.generable (m_aInterfaces);
      f.newline ().outdent ();
    }

    declareBody (f);
  }

  /**
   * prints the body of a class.
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
    for (final JFieldVar field : fields.values ())
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
    IJClassContainer p = m_aOuter;
    while (!(p instanceof JPackage))
      p = p.parentContainer ();
    return (JPackage) p;
  }

  @Nonnull
  public final IJClassContainer parentContainer ()
  {
    return m_aOuter;
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name)
  {
    return m_aGenerifiable.generify (name);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final Class <?> bound)
  {
    return m_aGenerifiable.generify (name, bound);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final AbstractJClass bound)
  {
    return m_aGenerifiable.generify (name, bound);
  }

  @Override
  @Nonnull
  public JTypeVar [] typeParams ()
  {
    return m_aGenerifiable.typeParams ();
  }

  @Nonnull
  public List <JTypeVar> typeParamList ()
  {
    return m_aGenerifiable.typeParamList ();
  }

  @Override
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <? extends AbstractJClass> bindings)
  {
    return this;
  }

  /**
   * Adding ability to annotate a class
   *
   * @param clazz
   *        The annotation class to annotate the class with
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> clazz)
  {
    return annotate (owner ().ref (clazz));
  }

  /**
   * Adding ability to annotate a class
   *
   * @param clazz
   *        The annotation class to annotate the class with
   * @return The created annotation use. Never <code>null</code>.
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

  @Nonnull
  public <W extends IJAnnotationWriter <?>> W annotate2 (@Nonnull final Class <W> clazz)
  {
    return TypedAnnotationWriter.create (clazz, this);
  }

  /**
   * {@link IJAnnotatable#annotations()}
   */
  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <JAnnotationUse> ();
    return Collections.unmodifiableCollection (m_aAnnotations);
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
}
