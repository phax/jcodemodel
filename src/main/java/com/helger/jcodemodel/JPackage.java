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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
import javax.annotation.WillNotClose;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * A Java package.
 */
public class JPackage implements
                      IJDeclaration,
                      IJGenerable,
                      IJClassContainer <JDefinedClass>,
                      IJAnnotatable,
                      Comparable <JPackage>,
                      IJDocCommentable
{
  /**
   * Name of the package. May be the empty string for the root package.
   */
  private final String m_sName;

  private final JCodeModel m_aOwner;

  /**
   * List of classes contained within this package keyed by their name.
   */
  private final Map <String, JDefinedClass> m_aClasses = new TreeMap <> ();

  /**
   * List of resources files inside this package.
   */
  private final Set <AbstractJResourceFile> m_aResources = new HashSet <> ();

  /**
   * All {@link AbstractJClass}s in this package keyed the upper case class
   * name. This field is non-null only on Windows, to detect "Foo" and "foo" as
   * a collision.
   */
  private final Map <String, JDefinedClass> m_aUpperCaseClassMap;

  /**
   * Lazily created list of package annotations.
   */
  private List <JAnnotationUse> m_aAnnotations;

  /**
   * package javadoc.
   */
  private JDocComment m_aJavaDoc;

  /**
   * JPackage constructor
   *
   * @param sName
   *        Name of package. May not be <code>null</code> but empty.
   * @param aOwner
   *        The code writer being used to create this package
   * @throws IllegalArgumentException
   *         If each part of the package name is not a valid identifier
   */
  protected JPackage (@Nonnull final String sName, @Nonnull final JCodeModel aOwner)
  {
    JCValueEnforcer.notNull (sName, "Name");
    JCValueEnforcer.isFalse (sName.equals ("."), "Package name . is not allowed");
    JCValueEnforcer.notNull (aOwner, "CodeModel");

    m_aOwner = aOwner;
    m_sName = sName;
    if (JCodeModel.isFileSystemCaseSensitive ())
      m_aUpperCaseClassMap = null;
    else
      m_aUpperCaseClassMap = new HashMap <> ();
  }

  @Nullable
  public IJClassContainer <?> parentContainer ()
  {
    return parent ();
  }

  /**
   * @return the parent package, or <code>null</code> if this class is the root
   *         package.
   */
  @Nullable
  public JPackage parent ()
  {
    if (isUnnamed ())
      return null;

    final int idx = m_sName.lastIndexOf ('.');
    if (idx < 0)
      return m_aOwner.rootPackage ();
    return m_aOwner._package (m_sName.substring (0, idx));
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

  @Nonnull
  public JDefinedClass _class (final int nMods,
                               @Nonnull final String sName,
                               @Nonnull final EClassType eClassType) throws JClassAlreadyExistsException
  {
    if (m_aClasses.containsKey (sName))
      throw new JClassAlreadyExistsException (m_aClasses.get (sName));

    // XXX problems caught in the NC constructor
    final JDefinedClass c = new JDefinedClass (this, nMods, sName, eClassType);

    if (m_aUpperCaseClassMap != null)
    {
      final String sUpperName = sName.toUpperCase ();
      final JDefinedClass dc = m_aUpperCaseClassMap.get (sUpperName);
      if (dc != null)
        throw new JClassAlreadyExistsException (dc);
      m_aUpperCaseClassMap.put (sUpperName, c);
    }
    m_aClasses.put (sName, c);
    return c;
  }

  /**
   * Gets a reference to the already created {@link JDefinedClass}.
   *
   * @param sName
   *        Class name to search
   * @return <code>null</code> if the class is not yet created.
   */
  @Nullable
  public JDefinedClass _getClass (@Nullable final String sName)
  {
    return m_aClasses.get (sName);
  }

  /**
   * Order is based on the lexicographic order of the package name.
   *
   * @param aOther
   *        Other package to compare to
   */
  public int compareTo (@Nonnull final JPackage aOther)
  {
    return m_sName.compareTo (aOther.m_sName);
  }

  /**
   * Adds a new resource file to this package.
   *
   * @param rsrc
   *        Resource file to add
   * @return Parameter resource file
   */
  @Nonnull
  public AbstractJResourceFile addResourceFile (@Nonnull final AbstractJResourceFile rsrc)
  {
    JCValueEnforcer.notNull (rsrc, "ResourceFile");
    m_aResources.add (rsrc);
    return rsrc;
  }

  /**
   * Checks if a resource of the given name exists.
   *
   * @param sName
   *        Filename to check
   * @return <code>true</code> if contained
   */
  public boolean hasResourceFile (@Nullable final String sName)
  {
    for (final AbstractJResourceFile r : m_aResources)
      if (r.name ().equals (sName))
        return true;
    return false;
  }

  /**
   * Iterates all resource files in this package.
   *
   * @return Iterator
   */
  @Nonnull
  public Iterator <AbstractJResourceFile> propertyFiles ()
  {
    return m_aResources.iterator ();
  }

  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJavaDoc == null)
      m_aJavaDoc = new JDocComment (owner ());
    return m_aJavaDoc;
  }

  /**
   * Removes a class from this package.
   *
   * @param aClass
   *        Class to be removed. May not be <code>null</code>.
   */
  public void remove (@Nonnull final AbstractJClass aClass)
  {
    JCValueEnforcer.isTrue (aClass._package () == this,
                            () -> "the specified class (" +
                                  aClass.fullName () +
                                  ") is not a member of this package (" +
                                  name () +
                                  "), or it is a referenced class");

    // note that c may not be a member of classes.
    // this happens when someone is trying to remove a non generated class
    m_aClasses.remove (aClass.name ());
    if (m_aUpperCaseClassMap != null)
      m_aUpperCaseClassMap.remove (aClass.name ().toUpperCase ());
  }

  /**
   * Reference a class within this package.
   *
   * @param sClassLocalName
   *        Local class name to reference
   * @return The referenced class
   * @throws ClassNotFoundException
   *         If the provided class does not exist
   */
  @Nonnull
  public AbstractJClass ref (@Nonnull final String sClassLocalName) throws ClassNotFoundException
  {
    JCValueEnforcer.isTrue (sClassLocalName.indexOf ('.') < 0, () -> "JClass name contains '.': " + sClassLocalName);

    String sFQCN;
    if (isUnnamed ())
      sFQCN = "";
    else
      sFQCN = m_sName + '.';
    sFQCN += sClassLocalName;

    return m_aOwner.ref (Class.forName (sFQCN));
  }

  /**
   * Gets a reference to a sub package of this package.
   *
   * @param sSubPackageName
   *        Name of the sub-package
   * @return New sub-package
   */
  @Nonnull
  public JPackage subPackage (@Nonnull final String sSubPackageName)
  {
    if (isUnnamed ())
      return owner ()._package (sSubPackageName);
    return owner ()._package (m_sName + '.' + sSubPackageName);
  }

  /**
   * @return the top-level classes defined in this package.
   */
  @Nonnull
  public Collection <JDefinedClass> classes ()
  {
    return m_aClasses.values ();
  }

  /**
   * Checks if a given name is already defined as a class/interface
   *
   * @param sClassLocalName
   *        Class local name
   * @return <code>true</code> if contained in this package
   */
  public boolean isDefined (@Nullable final String sClassLocalName)
  {
    for (final JDefinedClass clazz : m_aClasses.values ())
      if (clazz.name ().equals (sClassLocalName))
        return true;
    return false;
  }

  /**
   * Checks if this package is the root, unnamed package.
   *
   * @return <code>true</code> if this is the root package
   */
  public final boolean isUnnamed ()
  {
    return m_sName.length () == 0;
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
    return m_sName;
  }

  /**
   * @return the code model root object being used to create this package.
   */
  @Nonnull
  public final JCodeModel owner ()
  {
    return m_aOwner;
  }

  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass aClazz)
  {
    JCValueEnforcer.isFalse (isUnnamed (), "the root package cannot be annotated");

    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();

    final JAnnotationUse a = new JAnnotationUse (aClazz);
    m_aAnnotations.add (a);
    return a;
  }

  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> aClazz)
  {
    return annotate (m_aOwner.ref (aClazz));
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    return Collections.unmodifiableList (m_aAnnotations);
  }

  /**
   * Convert the package name to directory path equivalent
   */
  @Nonnull
  File toPath (@Nonnull final File aDir)
  {
    if (m_sName == null)
      return aDir;
    return new File (aDir, m_sName.replace ('.', File.separatorChar));
  }

  public void declare (@Nonnull final JFormatter f)
  {
    if (m_sName.length () != 0)
      f.print ("package").print (m_sName).print (';').newline ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print (m_sName);
  }

  @Nonnull
  private JFormatter _createJavaSourceFileWriter (@Nonnull final AbstractCodeWriter aSrc,
                                                  @Nonnull final String sClassName) throws IOException
  {
    final SourcePrintWriter aWriter = aSrc.openSource (this, sClassName + ".java");
    final JFormatter ret = new JFormatter (aWriter);
    // Add all classes to not be imported (may be empty)
    ret.addDontImportClasses (m_aOwner.getAllDontImportClasses ());
    return ret;
  }

  void build (@Nonnull @WillNotClose final AbstractCodeWriter aSrcWriter,
              @Nonnull @WillNotClose final AbstractCodeWriter aResWriter) throws IOException
  {
    // write classes
    for (final JDefinedClass c : m_aClasses.values ())
    {
      if (c.isHidden ())
      {
        // don't generate this file
        continue;
      }

      try (final JFormatter f = _createJavaSourceFileWriter (aSrcWriter, c.name ()))
      {
        f.write (c);
      }
    }

    // write package annotations
    if (m_aAnnotations != null || m_aJavaDoc != null)
    {
      try (final JFormatter f = _createJavaSourceFileWriter (aSrcWriter, "package-info"))
      {
        if (m_aJavaDoc != null)
          f.generable (m_aJavaDoc);

        // TODO: think about importing
        if (m_aAnnotations != null)
        {
          for (final JAnnotationUse a : m_aAnnotations)
            f.generable (a).newline ();
        }
        f.declaration (this);
      }
    }

    // write resources
    for (final AbstractJResourceFile rsrc : m_aResources)
    {
      final AbstractCodeWriter cw = rsrc.isResource () ? aResWriter : aSrcWriter;
      try (final OutputStream os = new BufferedOutputStream (cw.openBinary (this, rsrc.name ())))
      {
        rsrc.build (os);
      }
    }
  }

  boolean buildsErrorTypeRefs ()
  {
    // check classes
    for (final JDefinedClass c : m_aClasses.values ())
    {
      if (c.isHidden ())
      {
        // don't check this file
        continue;
      }

      if (c.containsErrorTypes ())
        return true;
    }
    return false;
  }

  /* package */int countArtifacts ()
  {
    int ret = 0;
    for (final JDefinedClass c : m_aClasses.values ())
    {
      if (c.isHidden ())
      {
        // don't generate this file
        continue;
      }
      ret++;
    }

    if (m_aAnnotations != null || m_aJavaDoc != null)
    {
      // package-info
      ret++;
    }

    ret += m_aResources.size ();

    return ret;
  }
}
