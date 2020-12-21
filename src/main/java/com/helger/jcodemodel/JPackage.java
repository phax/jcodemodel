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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.StringHelper;
import com.helger.jcodemodel.exceptions.JClassAlreadyExistsException;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.exceptions.JResourceAlreadyExistsException;
import com.helger.jcodemodel.util.FSName;

/**
 * A Java package.
 */
public class JPackage implements IJDeclaration, IJGenerable, IJClassContainer <JDefinedClass>, IJAnnotatable, IJDocCommentable
{
  public static final char SEPARATOR = '.';
  public static final Pattern VALID_PACKAGE_NAME_ANYCASE = Pattern.compile ("[A-Za-z_][A-Za-z0-9_]*");
  public static final Pattern VALID_PACKAGE_NAME_LOWERCASE = Pattern.compile ("[a-z_][a-z0-9_]*");
  /**
   * By default package names are not forced to lowercase. <br>
   * TODO v4: remove option and change to true
   */
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
  private final Map <FSName, JDefinedClass> m_aClasses = new TreeMap <> ();

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
    ValueEnforcer.notNull (sName, "Name");
    ValueEnforcer.notNull (aOwner, "CodeModel");

    // An empty package name is okay
    if (sName.length () > 0)
    {
      final String [] aParts = StringHelper.getExplodedArray (SEPARATOR, sName);
      for (final String sPart : aParts)
        if (isForbiddenPackageNamePart (sPart))
          throw new IllegalArgumentException ("Part '" + sPart + "' of the package name '" + sName + "' is invalid");
    }

    m_aOwner = aOwner;
    m_sName = sName;
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

    final int idx = m_sName.lastIndexOf (SEPARATOR);
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
  private FSName _createFSName (@Nonnull final String sName)
  {
    if (m_aOwner.getFileSystemConvention ().isCaseSensistive ())
      return FSName.createCaseSensitive (sName);
    return FSName.createCaseInsensitive (sName);
  }

  @Nonnull
  public JDefinedClass _class (final int nMods,
                               @Nonnull final String sClassName,
                               @Nonnull final EClassType eClassType) throws JCodeModelException
  {
    final FSName aKey = _createFSName (sClassName);

    // Is the class name unique in this package?
    JDefinedClass aDC = m_aClasses.get (aKey);
    if (aDC != null)
      throw new JClassAlreadyExistsException (aDC);

    final String sResDirName = m_sName.replace (SEPARATOR, JResourceDir.SEPARATOR);
    final JResourceDir aRD = m_aOwner.resourceDir (sResDirName);

    // Check if a resource file with the same name already exists
    final String sClassFilename = sClassName + ".java";
    if (aRD.hasResourceFile (sClassFilename))
      throw new JResourceAlreadyExistsException (aRD.fullChildName (sClassFilename));

    // CHeck if a sub-directory with the same name already exists (mind the "."
    // in filename - don't convert to '/' :D)
    if (m_aOwner.containsResourceDir (aRD.fullChildName (sClassFilename)))
      throw new JResourceAlreadyExistsException (aRD.fullChildName (sClassFilename));

    // Create a new class
    aDC = new JDefinedClass (this, nMods, sClassName, eClassType);
    m_aClasses.put (aKey, aDC);

    return aDC;
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
    final FSName aKey = _createFSName (sName);
    return m_aClasses.get (aKey);
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
    ValueEnforcer.isTrue (aClass._package () == this,
                          () -> "the specified class (" +
                                aClass.fullName () +
                                ") is not a member of this package (" +
                                name () +
                                "), or it is a referenced class");

    // note that c may not be a member of classes.
    // this happens when someone is trying to remove a non generated class
    final FSName aKey = _createFSName (aClass.name ());
    m_aClasses.remove (aKey);
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
    ValueEnforcer.isTrue (sClassLocalName.indexOf (SEPARATOR) < 0, () -> "JClass name contains '.': " + sClassLocalName);

    final String sFQCN = isUnnamed () ? sClassLocalName : m_sName + SEPARATOR + sClassLocalName;
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
    return owner ()._package (isUnnamed () ? sSubPackageName : m_sName + SEPARATOR + sSubPackageName);
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
    if (sClassLocalName != null)
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
    ValueEnforcer.isFalse (isUnnamed (), "the root package cannot be annotated");

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
  public List <JAnnotationUse> annotationsMutable ()
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    return m_aAnnotations;
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    return Collections.unmodifiableList (annotationsMutable ());
  }

  /**
   * Convert the package name to directory path equivalent
   */
  @Nonnull
  File toPath (@Nonnull final File aDir)
  {
    if (isUnnamed ())
      return aDir;
    return new File (aDir, m_sName.replace (SEPARATOR, File.separatorChar));
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

    return ret;
  }
}
