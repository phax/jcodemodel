/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A Java package.
 */
public class JPackage implements IJDeclaration, IJGenerable, IJClassContainer, IJAnnotatable, Comparable <JPackage>, IJDocCommentable
{
  /**
   * Name of the package. May be the empty string for the root package.
   */
  private final String _name;

  private final JCodeModel _owner;

  /**
   * List of classes contained within this package keyed by their name.
   */
  private final Map <String, JDefinedClass> classes = new TreeMap <String, JDefinedClass> ();

  /**
   * List of resources files inside this package.
   */
  private final Set <AbstractJResourceFile> resources = new HashSet <AbstractJResourceFile> ();

  /**
   * All {@link AbstractJClass}s in this package keyed the upper case class
   * name. This field is non-null only on Windows, to detect "Foo" and "foo" as
   * a collision.
   */
  private final Map <String, JDefinedClass> upperCaseClassMap;

  /**
   * Lazily created list of package annotations.
   */
  private List <JAnnotationUse> annotations;

  /**
   * package javadoc.
   */
  private JDocComment jdoc;

  /**
   * JPackage constructor
   * 
   * @param name
   *        Name of package. May not be <code>null</code> but empty.
   * @param cw
   *        The code writer being used to create this package
   * @throws IllegalArgumentException
   *         If each part of the package name is not a valid identifier
   */
  protected JPackage (@Nonnull final String name, @Nonnull final JCodeModel cw)
  {
    if (name == null)
      throw new NullPointerException ("name");
    if (name.equals ("."))
      throw new IllegalArgumentException ("Package name . is not allowed");
    if (cw == null)
      throw new NullPointerException ("codeModel");

    this._owner = cw;

    if (_owner.isCaseSensitiveFileSystem)
      upperCaseClassMap = null;
    else
      upperCaseClassMap = new HashMap <String, JDefinedClass> ();

    this._name = name;
  }

  @Nullable
  public IJClassContainer parentContainer ()
  {
    return parent ();
  }

  /**
   * Gets the parent package, or null if this class is the root package.
   */
  @Nullable
  public JPackage parent ()
  {
    if (_name.length () == 0)
      return null;

    final int idx = _name.lastIndexOf ('.');
    return _owner._package (_name.substring (0, idx));
  }

  public boolean isClass ()
  {
    return false;
  }

  public boolean isPackage ()
  {
    return true;
  }

  @Nonnull
  public JPackage getPackage ()
  {
    return this;
  }

  /**
   * Add a class to this package.
   * 
   * @param mods
   *        Modifiers for this class declaration
   * @param name
   *        Name of class to be added to this package
   * @return Newly generated class
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _class (final int mods, @Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.CLASS);
  }

  @Nonnull
  public JDefinedClass _class (final int mods, @Nonnull final String name, @Nonnull final EClassType classTypeVal) throws JClassAlreadyExistsException
  {
    if (classes.containsKey (name))
      throw new JClassAlreadyExistsException (classes.get (name));

    // XXX problems caught in the NC constructor
    final JDefinedClass c = new JDefinedClass (this, mods, name, classTypeVal);

    if (upperCaseClassMap != null)
    {
      final String sUpperName = name.toUpperCase ();
      final JDefinedClass dc = upperCaseClassMap.get (sUpperName);
      if (dc != null)
        throw new JClassAlreadyExistsException (dc);
      upperCaseClassMap.put (sUpperName, c);
    }
    classes.put (name, c);
    return c;
  }

  /**
   * Adds a public class to this package.
   */
  @Nonnull
  public JDefinedClass _class (@Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, name);
  }

  /**
   * Gets a reference to the already created {@link JDefinedClass}.
   * 
   * @return null If the class is not yet created.
   */
  @Nullable
  public JDefinedClass _getClass (@Nullable final String name)
  {
    return classes.get (name);
  }

  /**
   * Order is based on the lexicological order of the package name.
   */
  public int compareTo (@Nonnull final JPackage that)
  {
    return this._name.compareTo (that._name);
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
   * Add a annotationType Declaration to this package
   * 
   * @param mods
   *        Modifiers for this annotationType declaration
   * @param name
   *        Name of the annotation Type declaration to be added to this package
   * @return newly created Annotation Type Declaration
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _annotationTypeDeclaration (final int mods, @Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.ANNOTATION_TYPE_DECL);
  }

  /**
   * Add a public annotationType Declaration to this package
   * 
   * @param name
   *        Name of the annotation Type declaration to be added to this package
   * @return newly created Annotation Type Declaration
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _annotationTypeDeclaration (@Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _annotationTypeDeclaration (JMod.PUBLIC, name);
  }

  /**
   * Add a enum to this package
   * 
   * @param mods
   *        Modifiers for this enum declaration
   * @param name
   *        Name of the enum to be added to this package
   * @return newly created Enum
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _enum (final int mods, @Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _class (mods, name, EClassType.ENUM);
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
  public JDefinedClass _enum (@Nonnull final String name) throws JClassAlreadyExistsException
  {
    return _enum (JMod.PUBLIC, name);
  }

  /**
   * Adds a new resource file to this package.
   */
  @Nonnull
  public AbstractJResourceFile addResourceFile (@Nonnull final AbstractJResourceFile rsrc)
  {
    resources.add (rsrc);
    return rsrc;
  }

  /**
   * Checks if a resource of the given name exists.
   */
  public boolean hasResourceFile (@Nullable final String name)
  {
    for (final AbstractJResourceFile r : resources)
      if (r.name ().equals (name))
        return true;
    return false;
  }

  /**
   * Iterates all resource files in this package.
   */
  @Nonnull
  public Iterator <AbstractJResourceFile> propertyFiles ()
  {
    return resources.iterator ();
  }

  /**
   * Creates, if necessary, and returns the package javadoc for this
   * JDefinedClass.
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
   * Removes a class from this package.
   */
  public void remove (@Nonnull final AbstractJClass c)
  {
    if (c._package () != this)
      throw new IllegalArgumentException ("the specified class is not a member of this package,"
                                          + " or it is a referenced class");

    // note that c may not be a member of classes.
    // this happens when someone is trying to remove a non generated class
    classes.remove (c.name ());
    if (upperCaseClassMap != null)
      upperCaseClassMap.remove (c.name ().toUpperCase ());
  }

  /**
   * Reference a class within this package.
   */
  @Nonnull
  public AbstractJClass ref (@Nonnull final String name) throws ClassNotFoundException
  {
    if (name.indexOf ('.') >= 0)
      throw new IllegalArgumentException ("JClass name contains '.': " + name);

    String n = "";
    if (!isUnnamed ())
      n = name + '.';
    n += name;

    return _owner.ref (Class.forName (n));
  }

  /**
   * Gets a reference to a sub package of this package.
   */
  @Nonnull
  public JPackage subPackage (@Nonnull final String pkg)
  {
    if (isUnnamed ())
      return owner ()._package (pkg);
    return owner ()._package (_name + '.' + pkg);
  }

  /**
   * Returns an iterator that walks the top-level classes defined in this
   * package.
   */
  @Nonnull
  public Iterator <JDefinedClass> classes ()
  {
    return classes.values ().iterator ();
  }

  /**
   * Checks if a given name is already defined as a class/interface
   */
  public boolean isDefined (@Nullable final String classLocalName)
  {
    for (final JDefinedClass clazz : classes.values ())
      if (clazz.name ().equals (classLocalName))
        return true;
    return false;
  }

  /**
   * Checks if this package is the root, unnamed package.
   */
  public final boolean isUnnamed ()
  {
    return _name.length () == 0;
  }

  /**
   * Get the name of this package
   * 
   * @return The name of this package, or the empty string if this is the null
   *         package. For example, this method returns strings like
   *         <code>"java.lang"</code>
   */
  @Nonnull
  public String name ()
  {
    return _name;
  }

  /**
   * Return the code model root object being used to create this package.
   */
  @Nonnull
  public final JCodeModel owner ()
  {
    return _owner;
  }

  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass clazz)
  {
    if (isUnnamed ())
      throw new IllegalArgumentException ("the root package cannot be annotated");

    if (annotations == null)
      annotations = new ArrayList <JAnnotationUse> ();

    final JAnnotationUse a = new JAnnotationUse (clazz);
    annotations.add (a);
    return a;
  }

  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> clazz)
  {
    return annotate (_owner.ref (clazz));
  }

  @Nonnull
  public <W extends IJAnnotationWriter <?>> W annotate2 (@Nonnull final Class <W> clazz)
  {
    return TypedAnnotationWriter.create (clazz, this);
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (annotations == null)
      annotations = new ArrayList <JAnnotationUse> ();
    return Collections.unmodifiableList (annotations);
  }

  /**
   * Convert the package name to directory path equivalent
   */
  @Nonnull
  File toPath (@Nonnull final File dir)
  {
    if (_name == null)
      return dir;
    return new File (dir, _name.replace ('.', File.separatorChar));
  }

  public void declare (@Nonnull final JFormatter f)
  {
    if (_name.length () != 0)
      f.print ("package").print (_name).print (';').newline ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print (_name);
  }

  void build (final AbstractCodeWriter src, final AbstractCodeWriter res) throws IOException
  {
    // write classes
    for (final JDefinedClass c : classes.values ())
    {
      if (c.isHidden ())
        continue; // don't generate this file

      final JFormatter f = _createJavaSourceFileWriter (src, c.name ());
      f.write (c);
      f.close ();
    }

    // write package annotations
    if (annotations != null || jdoc != null)
    {
      final JFormatter f = _createJavaSourceFileWriter (src, "package-info");

      if (jdoc != null)
        f.generable (jdoc);

      // TODO: think about importing
      if (annotations != null)
      {
        for (final JAnnotationUse a : annotations)
          f.generable (a).newline ();
      }
      f.declaration (this);

      f.close ();
    }

    // write resources
    for (final AbstractJResourceFile rsrc : resources)
    {
      @SuppressWarnings ("resource")
      final AbstractCodeWriter cw = rsrc.isResource () ? res : src;
      final OutputStream os = new BufferedOutputStream (cw.openBinary (this, rsrc.name ()));
      rsrc.build (os);
      os.close ();
    }
  }

  /* package */int countArtifacts ()
  {
    int r = 0;
    for (final JDefinedClass c : classes.values ())
    {
      if (c.isHidden ())
        continue; // don't generate this file
      r++;
    }

    if (annotations != null || jdoc != null)
    {
      r++;
    }

    r += resources.size ();

    return r;
  }

  @Nonnull
  private JFormatter _createJavaSourceFileWriter (@Nonnull final AbstractCodeWriter src, @Nonnull final String className) throws IOException
  {
    final Writer bw = new BufferedWriter (src.openSource (this, className + ".java"));
    return new JFormatter (new PrintWriter (bw));
  }
}
