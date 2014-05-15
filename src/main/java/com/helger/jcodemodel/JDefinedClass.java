/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
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
  /** Name of this class. Null if anonymous. */
  private final String _name;

  /** Modifiers for the class declaration */
  private JMods _mods;

  /** Name of the super class of this class. */
  private AbstractJClass _superClass;

  /** List of interfaces that this class implements */
  private final Set <AbstractJClass> interfaces = new TreeSet <AbstractJClass> ();

  /** Fields keyed by their names. */
  /* package */final Map <String, JFieldVar> fields = new LinkedHashMap <String, JFieldVar> ();

  /** Static initializer, if this class has one */
  private JBlock _init;

  /** Instance initializer, if this class has one */
  private JBlock instanceInit;

  /** class javadoc */
  private JDocComment jdoc;

  /** Set of constructors for this class, if any */
  private final List <JMethod> constructors = new ArrayList <JMethod> ();

  /** Set of methods that are members of this class */
  private final List <JMethod> methods = new ArrayList <JMethod> ();

  /**
   * Nested classes as a map from name to JDefinedClass. The name is all
   * capitalized in a case sensitive file system (
   * {@link JCodeModel#isCaseSensitiveFileSystem}) to avoid conflicts. Lazily
   * created to save footprint.
   * 
   * @see #_getClasses()
   */
  private Map <String, JDefinedClass> classes;

  /**
   * Flag that controls whether this class should be really generated or not.
   * Sometimes it is useful to generate code that refers to class X, without
   * actually generating the code of X. This flag is used to surpress X.java
   * file in the output.
   */
  private boolean hideFile = false;

  /**
   * Client-app spcific metadata associated with this user-created class.
   */
  public Object metadata;

  /**
   * String that will be put directly inside the generated code. Can be null.
   */
  private String directBlock;

  /**
   * If this is a package-member class, this is {@link JPackage}. If this is a
   * nested class, this is {@link JDefinedClass}. If this is an anonymous class,
   * this constructor shouldn't be used.
   */
  private final IJClassContainer outer;

  /**
   * Default value is class or interface or annotationTypeDeclaration or enum
   */
  private final EClassType classType;

  /**
   * List containing the enum value declarations
   */
  // private List enumValues = new ArrayList();

  /**
   * Set of enum constants that are keyed by names. In Java, enum constant order
   * is actually significant, because of order ID they get. So let's preserve
   * the order.
   */
  private final Map <String, JEnumConstant> enumConstantsByName = new LinkedHashMap <String, JEnumConstant> ();

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> m_aAnnotations;

  /**
   * Helper class to implement {@link IJGenerifiable}.
   */
  private final AbstractJGenerifiableImpl generifiable = new AbstractJGenerifiableImpl ()
  {
    @Override
    protected JCodeModel owner ()
    {
      return JDefinedClass.this.owner ();
    }
  };

  protected JDefinedClass (@Nonnull final IJClassContainer parent,
                           final int mods,
                           final String name,
                           @Nonnull final EClassType classTypeval)
  {
    this (mods, name, parent, parent.owner (), classTypeval);
  }

  /**
   * Constructor for creating anonymous inner class.
   */
  protected JDefinedClass (@Nonnull final JCodeModel owner, final int mods, @Nullable final String name)
  {
    this (mods, name, null, owner);
  }

  private JDefinedClass (final int mods,
                         @Nullable final String name,
                         @Nonnull final IJClassContainer parent,
                         @Nonnull final JCodeModel owner)
  {
    this (mods, name, parent, owner, EClassType.CLASS);
  }

  /**
   * JClass constructor
   * 
   * @param mods
   *        Modifiers for this class declaration
   * @param name
   *        Name of this class
   */
  private JDefinedClass (final int mods,
                         @Nullable final String name,
                         @Nonnull final IJClassContainer parent,
                         @Nonnull final JCodeModel owner,
                         @Nonnull final EClassType classTypeVal)
  {
    super (owner);

    if (name != null)
    {
      if (name.trim ().length () == 0)
        throw new IllegalArgumentException ("JClass name empty");

      if (!Character.isJavaIdentifierStart (name.charAt (0)))
      {
        final String msg = "JClass name " +
                           name +
                           " contains illegal character" +
                           " for beginning of identifier: " +
                           name.charAt (0);
        throw new IllegalArgumentException (msg);
      }
      for (int i = 1; i < name.length (); i++)
      {
        if (!Character.isJavaIdentifierPart (name.charAt (i)))
        {
          final String msg = "JClass name " + name + " contains illegal character " + name.charAt (i);
          throw new IllegalArgumentException (msg);
        }
      }
    }

    this.classType = classTypeVal;
    if (isInterface ())
      this._mods = JMods.forInterface (mods);
    else
      this._mods = JMods.forClass (mods);

    this._name = name;

    this.outer = parent;
  }

  /**
   * Returns true if this is an anonymous class.
   */
  public final boolean isAnonymous ()
  {
    return _name == null;
  }

  /**
   * This class extends the specifed class.
   * 
   * @param superClass
   *        Superclass for this class
   * @return This class
   */
  @Nonnull
  public JDefinedClass _extends (@Nonnull final AbstractJClass superClass)
  {
    if (this.classType == EClassType.INTERFACE)
    {
      if (superClass.isInterface ())
        return this._implements (superClass);
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
                                            this._name +
                                            " may not subclass from inner class: " +
                                            o.name ());
      }
    }

    this._superClass = superClass;
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
    if (_superClass == null)
      _superClass = owner ().ref (Object.class);
    return _superClass;
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
    interfaces.add (iface);
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
    return interfaces.iterator ();
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
    return _name;
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
    JEnumConstant ec = enumConstantsByName.get (name);
    if (null == ec)
    {
      ec = new JEnumConstant (this, name);
      enumConstantsByName.put (name, ec);
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
    if (outer instanceof JDefinedClass)
      return ((JDefinedClass) outer).fullName () + '.' + name ();

    final JPackage p = _package ();
    if (p.isUnnamed ())
      return name ();
    return p.name () + '.' + name ();
  }

  @Override
  public String binaryName ()
  {
    if (outer instanceof JDefinedClass)
      return ((JDefinedClass) outer).binaryName () + '$' + name ();
    return fullName ();
  }

  @Override
  public boolean isInterface ()
  {
    return this.classType == EClassType.INTERFACE;
  }

  @Override
  public boolean isAbstract ()
  {
    return _mods.isAbstract ();
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
  public JFieldVar field (final int mods, final AbstractJType type, final String name, final IJExpression init)
  {
    final JFieldVar f = new JFieldVar (this, JMods.forField (mods), type, name, init);

    if (fields.containsKey (name))
    {
      throw new IllegalArgumentException ("trying to create the same field twice: " + name);
    }

    fields.put (name, f);
    return f;
  }

  /**
   * This method indicates if the interface is an annotationTypeDeclaration
   */
  public boolean isAnnotationTypeDeclaration ()
  {
    return this.classType == EClassType.ANNOTATION_TYPE_DECL;

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
  public JDefinedClass _enum (final int mods, final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.ENUM);
  }

  public EClassType getClassType ()
  {
    return this.classType;
  }

  public JFieldVar field (final int mods, final Class <?> type, final String name, final IJExpression init)
  {
    return field (mods, owner ()._ref (type), name, init);
  }

  /**
   * Returns all the fields declred in this class. The returned {@link Map} is a
   * read-only live view.
   * 
   * @return always non-null.
   */
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
  public void removeField (final JFieldVar field)
  {
    if (fields.remove (field.name ()) != field)
      throw new IllegalArgumentException ();
  }

  /**
   * Creates, if necessary, and returns the static initializer for this class.
   * 
   * @return JBlock containing initialization statements for this class
   */
  public JBlock init ()
  {
    if (_init == null)
      _init = new JBlock ();
    return _init;
  }

  /**
   * Creates, if necessary, and returns the instance initializer for this class.
   * 
   * @return JBlock containing initialization statements for this class
   */
  public JBlock instanceInit ()
  {
    if (instanceInit == null)
      instanceInit = new JBlock ();
    return instanceInit;
  }

  /**
   * Adds a constructor to this class.
   * 
   * @param mods
   *        Modifiers for this constructor
   */
  public JMethod constructor (final int mods)
  {
    final JMethod c = new JMethod (mods, this);
    constructors.add (c);
    return c;
  }

  /**
   * Returns an iterator that walks the constructors defined in this class.
   */
  public Iterator <JMethod> constructors ()
  {
    return constructors.iterator ();
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   * 
   * @return null if not found.
   */
  public JMethod getConstructor (final AbstractJType [] argTypes)
  {
    for (final JMethod m : constructors)
    {
      if (m.hasSignature (argTypes))
        return m;
    }
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
  public JMethod method (final int mods, final AbstractJType type, final String name)
  {
    // XXX problems caught in M constructor
    final JMethod m = new JMethod (this, mods, type, name);
    methods.add (m);
    return m;
  }

  public JMethod method (final int mods, final Class <?> type, final String name)
  {
    return method (mods, owner ()._ref (type), name);
  }

  /**
   * Returns the set of methods defined in this class.
   */
  public Collection <JMethod> methods ()
  {
    return methods;
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   * 
   * @return null if not found.
   */
  public JMethod getMethod (final String name, final AbstractJType [] argTypes)
  {
    for (final JMethod m : methods)
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
    if (jdoc == null)
      jdoc = new JDocComment (owner ());
    return jdoc;
  }

  /**
   * Mark this file as hidden, so that this file won't be generated.
   * <p>
   * This feature could be used to generate code that refers to class X, without
   * actually generating X.java.
   */
  public void hide ()
  {
    hideFile = true;
  }

  public boolean isHidden ()
  {
    return hideFile;
  }

  /**
   * Returns an iterator that walks the nested classes defined in this class.
   */
  @Nonnull
  public final Iterator <JDefinedClass> classes ()
  {
    if (classes == null)
      return Collections.<JDefinedClass> emptyList ().iterator ();
    return classes.values ().iterator ();
  }

  @Nonnull
  private Map <String, JDefinedClass> _getClasses ()
  {
    if (classes == null)
      classes = new TreeMap <String, JDefinedClass> ();
    return classes;
  }

  /**
   * Returns all the nested classes defined in this class.
   */
  @Nonnull
  public final AbstractJClass [] listClasses ()
  {
    if (classes == null)
      return new AbstractJClass [0];
    return classes.values ().toArray (new AbstractJClass [classes.values ().size ()]);
  }

  @Override
  @Nullable
  public AbstractJClass outer ()
  {
    if (outer.isClass ())
      return (AbstractJClass) outer;
    return null;
  }

  public void declare (@Nonnull final JFormatter f)
  {
    if (jdoc != null)
      f.newline ().generable (jdoc);

    if (m_aAnnotations != null)
    {
      for (final JAnnotationUse annotation : m_aAnnotations)
        f.generable (annotation).newline ();
    }

    f.generable (_mods).print (classType.declarationToken ()).id (_name).declaration (generifiable);

    if (_superClass != null && _superClass != owner ().ref (Object.class))
      f.newline ().indent ().print ("extends").generable (_superClass).newline ().outdent ();

    if (!interfaces.isEmpty ())
    {
      if (_superClass == null)
        f.newline ();
      f.indent ().print (classType == EClassType.INTERFACE ? "extends" : "implements");
      f.generable (interfaces);
      f.newline ().outdent ();
    }
    declareBody (f);
  }

  /**
   * prints the body of a class.
   */
  protected void declareBody (@Nonnull final JFormatter f)
  {
    f.print ('{').newline ().newline ().indent ();
    boolean first = true;

    if (!enumConstantsByName.isEmpty ())
    {
      for (final JEnumConstant c : enumConstantsByName.values ())
      {
        if (!first)
          f.print (',').newline ();
        f.declaration (c);
        first = false;
      }
      f.print (';').newline ();
    }

    for (final JFieldVar field : fields.values ())
      f.declaration (field);
    if (_init != null)
      f.newline ().print ("static").statement (_init);
    if (instanceInit != null)
      f.newline ().statement (instanceInit);
    for (final JMethod m : constructors)
    {
      f.newline ().declaration (m);
    }
    for (final JMethod m : methods)
    {
      f.newline ().declaration (m);
    }
    if (classes != null)
      for (final JDefinedClass dc : classes.values ())
        f.newline ().declaration (dc);

    if (directBlock != null)
      f.print (directBlock);
    f.newline ().outdent ().print ('}').newline ();
  }

  /**
   * Places the given string directly inside the generated class. This method
   * can be used to add methods/fields that are not generated by CodeModel. This
   * method should be used only as the last resort.
   */
  public void direct (final String string)
  {
    if (directBlock == null)
      directBlock = string;
    else
      directBlock += string;
  }

  @Override
  public final JPackage _package ()
  {
    IJClassContainer p = outer;
    while (!(p instanceof JPackage))
      p = p.parentContainer ();
    return (JPackage) p;
  }

  public final IJClassContainer parentContainer ()
  {
    return outer;
  }

  public JTypeVar generify (final String name)
  {
    return generifiable.generify (name);
  }

  public JTypeVar generify (final String name, final Class <?> bound)
  {
    return generifiable.generify (name, bound);
  }

  public JTypeVar generify (final String name, final AbstractJClass bound)
  {
    return generifiable.generify (name, bound);
  }

  @Override
  public JTypeVar [] typeParams ()
  {
    return generifiable.typeParams ();
  }

  @Override
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <AbstractJClass> bindings)
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
    return _mods;
  }
}
