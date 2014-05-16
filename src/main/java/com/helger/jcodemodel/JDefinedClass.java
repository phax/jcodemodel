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

import com.helger.jcodemodel.JTypeVar.EBoundMode;

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
   * Name of this class. Null if anonymous.
   */
  private final String _name;

  /**
   * Modifiers for the class declaration
   */
  private JMods _mods;

  /**
   * Name of the super class of this class.
   */
  private AbstractJClass _superClass;

  /**
   * List of interfaces that this class implements
   */
  private final Set <AbstractJClass> _interfaces = new TreeSet <AbstractJClass> ();

  /**
   * Fields keyed by their names.
   */
  /* package */final Map <String, JFieldVar> fields = new LinkedHashMap <String, JFieldVar> ();

  /**
   * Static initializer, if this class has one
   */
  private JBlock _staticInit;

  /**
   * Instance initializer, if this class has one
   */
  private JBlock _instanceInit;

  /**
   * class javadoc
   */
  private JDocComment _jdoc;

  /**
   * Set of constructors for this class, if any
   */
  private final List <JMethod> _constructors = new ArrayList <JMethod> ();

  /**
   * Set of methods that are members of this class
   */
  private final List <JMethod> _methods = new ArrayList <JMethod> ();

  /**
   * Nested classes as a map from name to JDefinedClass. The name is all
   * capitalized in a case sensitive file system (
   * {@link JCodeModel#isCaseSensitiveFileSystem}) to avoid conflicts. Lazily
   * created to save footprint.
   * 
   * @see #_getClasses()
   */
  private Map <String, JDefinedClass> _classes;

  /**
   * Flag that controls whether this class should be really generated or not.
   * Sometimes it is useful to generate code that refers to class X, without
   * actually generating the code of X. This flag is used to suppress X.java
   * file in the output.
   */
  private boolean _hideFile = false;

  /**
   * Client-app specific metadata associated with this user-created class.
   */
  public Object metadata;

  /**
   * String that will be put directly inside the generated code. Can be null.
   */
  private String _directBlock;

  /**
   * If this is a package-member class, this is {@link JPackage}. If this is a
   * nested class, this is {@link JDefinedClass}. If this is an anonymous class,
   * this constructor shouldn't be used.
   */
  private final IJClassContainer _outer;

  /**
   * Default value is class or interface or annotationTypeDeclaration or enum
   */
  private final EClassType _classType;

  /**
   * List containing the enum value declarations
   */
  // private List enumValues = new ArrayList();

  /**
   * Set of enum constants that are keyed by names. In Java, enum constant order
   * is actually significant, because of order ID they get. So let's preserve
   * the order.
   */
  private final Map <String, JEnumConstant> _enumConstantsByName = new LinkedHashMap <String, JEnumConstant> ();

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> _annotations;

  /**
   * Helper class to implement {@link IJGenerifiable}.
   */
  private final AbstractJGenerifiableImpl _generifiable = new AbstractJGenerifiableImpl ()
  {
    @Nonnull
    public JCodeModel owner ()
    {
      // The owner is same as the owner of this defined class's owner
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

    _classType = classTypeVal;
    if (isInterface ())
      _mods = JMods.forInterface (mods);
    else
      _mods = JMods.forClass (mods);

    _name = name;
    _outer = parent;
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
    if (_classType == EClassType.INTERFACE)
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
                                            _name +
                                            " may not subclass from inner class: " +
                                            o.name ());
      }
    }

    _superClass = superClass;
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
    _interfaces.add (iface);
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
    return _interfaces.iterator ();
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
    JEnumConstant ec = _enumConstantsByName.get (name);
    if (null == ec)
    {
      ec = new JEnumConstant (this, name);
      _enumConstantsByName.put (name, ec);
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
    if (_outer instanceof JDefinedClass)
      return ((JDefinedClass) _outer).fullName () + '.' + name ();

    final JPackage p = _package ();
    if (p.isUnnamed ())
      return name ();
    return p.name () + '.' + name ();
  }

  @Override
  public String binaryName ()
  {
    if (_outer instanceof JDefinedClass)
      return ((JDefinedClass) _outer).binaryName () + '$' + name ();
    return fullName ();
  }

  @Override
  public boolean isInterface ()
  {
    return _classType == EClassType.INTERFACE;
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
    return _classType == EClassType.ANNOTATION_TYPE_DECL;
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
    return _classType;
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
    if (_staticInit == null)
      _staticInit = new JBlock ();
    return _staticInit;
  }

  /**
   * Creates, if necessary, and returns the instance initializer for this class.
   * 
   * @return JBlock containing initialization statements for this class
   */
  @Nonnull
  public JBlock instanceInit ()
  {
    if (_instanceInit == null)
      _instanceInit = new JBlock ();
    return _instanceInit;
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
    _constructors.add (c);
    return c;
  }

  /**
   * Returns an iterator that walks the constructors defined in this class.
   */
  @Nonnull
  public Iterator <JMethod> constructors ()
  {
    return _constructors.iterator ();
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   * 
   * @return null if not found.
   */
  @Nullable
  public JMethod getConstructor (@Nonnull final AbstractJType [] argTypes)
  {
    for (final JMethod m : _constructors)
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
    _methods.add (m);
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
    return _methods;
  }

  /**
   * Looks for a method that has the specified method signature and return it.
   * 
   * @return null if not found.
   */
  @Nullable
  public JMethod getMethod (final String name, final AbstractJType [] argTypes)
  {
    for (final JMethod m : _methods)
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
    if (_jdoc == null)
      _jdoc = new JDocComment (owner ());
    return _jdoc;
  }

  /**
   * Mark this file as hidden, so that this file won't be generated.
   * <p>
   * This feature could be used to generate code that refers to class X, without
   * actually generating X.java.
   */
  public void hide ()
  {
    _hideFile = true;
  }

  public boolean isHidden ()
  {
    return _hideFile;
  }

  /**
   * Returns an iterator that walks the nested classes defined in this class.
   */
  @Nonnull
  public final Iterator <JDefinedClass> classes ()
  {
    if (_classes == null)
      return Collections.<JDefinedClass> emptyList ().iterator ();
    return _classes.values ().iterator ();
  }

  @Nonnull
  private Map <String, JDefinedClass> _getClasses ()
  {
    if (_classes == null)
      _classes = new TreeMap <String, JDefinedClass> ();
    return _classes;
  }

  /**
   * Returns all the nested classes defined in this class.
   */
  @Nonnull
  public final AbstractJClass [] listClasses ()
  {
    if (_classes == null)
      return new AbstractJClass [0];
    return _classes.values ().toArray (new AbstractJClass [_classes.values ().size ()]);
  }

  @Override
  @Nullable
  public AbstractJClass outer ()
  {
    if (_outer.isClass ())
      return (AbstractJClass) _outer;
    return null;
  }

  public void declare (@Nonnull final JFormatter f)
  {
    // Java docs
    if (_jdoc != null)
      f.newline ().generable (_jdoc);

    // Class annotations
    if (_annotations != null)
      for (final JAnnotationUse aAnnotation : _annotations)
        f.generable (aAnnotation).newline ();

    // Modifiers (private, protected, public)
    // Type of class (class, interface, enum, @interface)
    // Name of the class
    // Class wildcards
    f.generable (_mods).print (_classType.declarationToken ()).id (_name).declaration (_generifiable);

    // If a super class is defined and is not "Object"
    boolean bHasSuperClass = false;
    if (_superClass != null && _superClass != owner ().ref (Object.class))
    {
      bHasSuperClass = true;
      f.newline ().indent ().print ("extends").generable (_superClass).newline ().outdent ();
    }

    // Add all interfaces
    if (!_interfaces.isEmpty ())
    {
      if (!bHasSuperClass)
        f.newline ();
      f.indent ().print (_classType == EClassType.INTERFACE ? "extends" : "implements");
      f.generable (_interfaces);
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
    boolean first = true;

    if (!_enumConstantsByName.isEmpty ())
    {
      for (final JEnumConstant c : _enumConstantsByName.values ())
      {
        if (!first)
          f.print (',').newline ();
        f.declaration (c);
        first = false;
      }
      f.print (';').newline ();
    }

    // All fields
    for (final JFieldVar field : fields.values ())
      f.declaration (field);

    // Static init
    if (_staticInit != null)
      f.newline ().print ("static").statement (_staticInit);

    // Instance init
    if (_instanceInit != null)
      f.newline ().statement (_instanceInit);

    // All constructors
    for (final JMethod m : _constructors)
      f.newline ().declaration (m);

    // All regular methods
    for (final JMethod m : _methods)
      f.newline ().declaration (m);

    // All inner classes
    if (_classes != null)
      for (final JDefinedClass dc : _classes.values ())
        f.newline ().declaration (dc);

    // Hacks...
    if (_directBlock != null)
      f.print (_directBlock);

    f.outdent ().print ('}').newline ();
  }

  /**
   * Places the given string directly inside the generated class. This method
   * can be used to add methods/fields that are not generated by CodeModel. This
   * method should be used only as the last resort.
   */
  public void direct (@Nullable final String string)
  {
    if (_directBlock == null)
      _directBlock = string;
    else
      if (string != null)
        _directBlock += string;
  }

  @Override
  public final JPackage _package ()
  {
    IJClassContainer p = _outer;
    while (!(p instanceof JPackage))
      p = p.parentContainer ();
    return (JPackage) p;
  }

  @Nonnull
  public final IJClassContainer parentContainer ()
  {
    return _outer;
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name)
  {
    return _generifiable.generify (name);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final Class <?> bound)
  {
    return _generifiable.generify (name, bound);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final Class <?> bound, @Nonnull final EBoundMode eMode)
  {
    return _generifiable.generify (name, bound, eMode);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final AbstractJClass bound)
  {
    return _generifiable.generify (name, bound);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name,
                            @Nonnull final AbstractJClass bound,
                            @Nonnull final EBoundMode eMode)
  {
    return _generifiable.generify (name, bound, eMode);
  }

  @Override
  @Nonnull
  public JTypeVar [] typeParams ()
  {
    return _generifiable.typeParams ();
  }

  @Nonnull
  public List <JTypeVar> typeParamList ()
  {
    return _generifiable.typeParamList ();
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
    if (_annotations == null)
      _annotations = new ArrayList <JAnnotationUse> ();
    final JAnnotationUse a = new JAnnotationUse (clazz);
    _annotations.add (a);
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
    if (_annotations == null)
      _annotations = new ArrayList <JAnnotationUse> ();
    return Collections.unmodifiableCollection (_annotations);
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
