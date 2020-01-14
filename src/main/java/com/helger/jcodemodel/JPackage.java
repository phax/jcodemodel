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

import java.io.File;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.fmt.AbstractJResourceFile;
import com.helger.jcodemodel.util.JCStringHelper;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * A Java package.
 */
public class JPackage implements
                      IJDeclaration,
                      IJGenerable,
                      IJClassContainer <JDefinedClass>,
                      IJAnnotatable,
                      IJDocCommentable
{
  public static final Pattern VALID_PACKAGE_NAME_ANYCASE = Pattern.compile ("[A-Za-z_][A-Za-z0-9_]*");
  public static final Pattern VALID_PACKAGE_NAME_LOWERCASE = Pattern.compile ("[a-z_][a-z0-9_]*");
  private static final AtomicBoolean FORCE_PACKAGE_NAME_LOWERCASE = new AtomicBoolean (false);

  /**
   * @return <code>true</code> if only lower case package names should be
   *         allowed, <code>false</code> if also upper case characters are
   *         allowed. For backwards compatibility upper case characters are
   *         allowed so this method returns <code>false</code>.
   * @since 3.2.5
   */
  public static boolean isForcePackageNameLowercase ()
  {
    return FORCE_PACKAGE_NAME_LOWERCASE.get ();
  }

  /**
   * Only allow lower case package names
   *
   * @param bForcePackageNameLowercase
   *        <code>true</code> to force lower case package names are recommended
   *        by
   *        https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html
   */
  public static void setForcePackageNameLowercase (final boolean bForcePackageNameLowercase)
  {
    FORCE_PACKAGE_NAME_LOWERCASE.set (bForcePackageNameLowercase);
  }

  /**
   * Check if the package name part is valid or not.
   *
   * @param sName
   *        The name part to check
   * @return <code>true</code> if it is invalid, <code>false</code> if it is
   *         valid
   */
  public static boolean isForbiddenPackageNamePart (@Nonnull final String sName)
  {
    // Empty is not allowed
    if (sName == null || sName.length () == 0)
      return true;

    // Java keywords are now allowed
    if (JJavaName.isJavaReservedKeyword (sName))
      return true;

    if (isForcePackageNameLowercase ())
    {
      // Lowercase check required?
      if (!VALID_PACKAGE_NAME_LOWERCASE.matcher (sName).matches ())
        return true;
    }
    else
    {
      // Mixed case possible
      if (!VALID_PACKAGE_NAME_ANYCASE.matcher (sName).matches ())
        return true;
    }

    // not forbidden -> allowed
    return false;
  }

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
    JCValueEnforcer.notNull (aOwner, "CodeModel");

    // An empty package name is okay
    if (sName.length () > 0)
    {
      final String [] aParts = JCStringHelper.getExplodedArray ('.', sName);
      for (final String sPart : aParts)
        if (isForbiddenPackageNamePart (sPart))
          throw new IllegalArgumentException ("Part '" + sPart + "' of the package name '" + sName + "' is invalid");
    }

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
   * Adds a new resource file to this package.
   *
   * @param rsrc
   *        Resource file to add
   * @return Parameter resource file
   * @deprecated Use the API from {@link JResourceDir} instead. Deprecated since
   *             v3.3.1
   */
  @Nonnull
  @Deprecated
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
   * @deprecated Use the API from {@link JResourceDir} instead. Deprecated since
   *             v3.3.1
   */
  @Deprecated
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
   * @deprecated Use {@link #resourceFiles()} instead. Deprecated since v3.3.1
   */
  @Deprecated
  @Nonnull
  public Iterator <AbstractJResourceFile> propertyFiles ()
  {
    return resourceFiles ();
  }

  /**
   * Iterates all resource files in this package.
   *
   * @return Iterator
   * @since 3.2.0
   * @deprecated Use the API from {@link JResourceDir} instead. Deprecated since
   *             v3.3.1
   */
  @Deprecated
  @Nonnull
  public Iterator <AbstractJResourceFile> resourceFiles ()
  {
    return m_aResources.iterator ();
  }

  /**
   * @return A copy of all contained resource files. Never <code>null</code>.
   * @deprecated Use the API from {@link JResourceDir} instead. Deprecated since
   *             v3.3.1
   */
  @Deprecated
  @Nonnull
  public List <AbstractJResourceFile> getAllResourceFiles ()
  {
    return new ArrayList <> (m_aResources);
  }

  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJavaDoc == null)
      m_aJavaDoc = new JDocComment (owner ());
    return m_aJavaDoc;
  }

  @Nullable
  public JDocComment javadocOrNull ()
  {
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
    if (isUnnamed ())
      return aDir;
    return new File (aDir, m_sName.replace ('.', File.separatorChar));
  }

  public void declare (@Nonnull final IJFormatter f)
  {
    if (!isUnnamed ())
      f.print ("package").print (m_sName).print (';').newline ();
  }

  public void generate (@Nonnull final IJFormatter f)
  {
    f.print (m_sName);
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
