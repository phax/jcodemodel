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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.fmt.AbstractJResourceFile;
import com.helger.jcodemodel.util.JCFilenameHelper;
import com.helger.jcodemodel.util.JCStringHelper;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * A Java resource directory - complementary to a {@link JPackage}.
 *
 * @since 3.3.1
 */
public class JResourceDir implements IJOwned
{
  public static final char SEPARATOR = JCFilenameHelper.UNIX_SEPARATOR;

  /**
   * Check if the resource directory name part is valid or not.
   *
   * @param sName
   *        The name part to check
   * @return <code>true</code> if it is invalid, <code>false</code> if it is
   *         valid
   */
  public static boolean isForbiddenDirectoryNamePart (@Nonnull final String sName)
  {
    // Empty is not allowed
    if (sName == null || sName.length () == 0)
      return true;

    // Java keywords are now allowed
    if (!JCFilenameHelper.isValidFilename (sName))
      return true;

    // not forbidden -> allowed
    return false;
  }

  /**
   * Name of the package. May be the empty string for the root package.
   */
  private final String m_sName;

  private final JCodeModel m_aOwner;

  /**
   * Map of resources files inside this package.
   */
  private final Map <String, AbstractJResourceFile> m_aResources = new TreeMap <> ();

  /**
   * Map of upper case resource names, if the underlying file system is not case
   * sensitive (e.g. Windows).
   */
  private final Map <String, AbstractJResourceFile> m_aUpperCaseResources;

  /**
   * Constructor
   *
   * @param sName
   *        Name of directory. May not be <code>null</code> but empty.
   * @param aOwner
   *        The code writer being used to create this package
   * @throws IllegalArgumentException
   *         If each part of the package name is not a valid filename part.
   */
  protected JResourceDir (@Nonnull final String sName, @Nonnull final JCodeModel aOwner)
  {
    JCValueEnforcer.notNull (sName, "Name");
    JCValueEnforcer.notNull (aOwner, "CodeModel");

    m_aOwner = aOwner;

    // An empty directory name is okay
    if (sName.length () > 0)
    {
      // Convert "\" to "/"
      String sCleanPath = JCFilenameHelper.getPathUsingUnixSeparator (sName);
      // Ensure last part is not a "/"
      sCleanPath = JCFilenameHelper.ensurePathEndingWithoutSeparator (sCleanPath);

      if (sCleanPath.startsWith ("/"))
        throw new IllegalArgumentException ("A resource directory may not be an absolute path: '" + sName + "'");

      final String [] aParts = JCStringHelper.getExplodedArray (SEPARATOR, sCleanPath);
      for (final String sPart : aParts)
        if (isForbiddenDirectoryNamePart (sPart))
          throw new IllegalArgumentException ("Part '" +
                                              sPart +
                                              "' of the resource directory name '" +
                                              sName +
                                              "' is invalid");
      m_sName = sCleanPath;
    }
    else
      m_sName = "";

    if (JCodeModel.isFileSystemCaseSensitive ())
      m_aUpperCaseResources = null;
    else
      m_aUpperCaseResources = new TreeMap <> ();
  }

  /**
   * @return the code model root object being used to create this resource
   *         directory.
   */
  @Nonnull
  public final JCodeModel owner ()
  {
    return m_aOwner;
  }

  /**
   * Get the name of this resource directory
   *
   * @return The name of this resource directory, or the empty string if this is
   *         the root directory. For example, this method returns strings like
   *         <code>"dir1/dir2/dir3"</code>
   */
  @Nonnull
  public final String name ()
  {
    return m_sName;
  }

  /**
   * @return the parent package, or <code>null</code> if this class is the root
   *         package.
   */
  @Nullable
  public JResourceDir parent ()
  {
    if (isUnnamed ())
      return null;

    final int idx = m_sName.lastIndexOf ('/');
    if (idx < 0)
      return m_aOwner.rootResourceDir ();
    return m_aOwner.resourceDir (m_sName.substring (0, idx));
  }

  /**
   * Adds a new resource file to this package.
   *
   * @param aResFile
   *        Resource file to add
   * @return Parameter resource file
   * @param <T>
   *        The implementation type used
   * @throws JCodeModelException
   *         if another resource with the same name already exists
   */
  @Nonnull
  public <T extends AbstractJResourceFile> T addResourceFile (@Nonnull final T aResFile) throws JCodeModelException
  {
    JCValueEnforcer.notNull (aResFile, "ResourceFile");

    // Check uniqueness (case sensitive)
    final String sName = aResFile.name ();
    if (m_aResources.containsKey (sName))
      throw new JResourceAlreadyExistsException (fullChildName (sName));

    // Check uniqueness (case insensitive)
    final String sUpperName = sName.toUpperCase (Locale.ROOT);
    if (m_aUpperCaseResources != null)
    {
      if (m_aUpperCaseResources.containsKey (sUpperName))
        throw new JResourceAlreadyExistsException (fullChildName (sName));
    }

    // Check if a Java class with the same name already exists
    final boolean bIsPotentiallyJavaSrcFile;
    if (m_aUpperCaseResources != null)
      bIsPotentiallyJavaSrcFile = sUpperName.endsWith (".JAVA");
    else
      bIsPotentiallyJavaSrcFile = sName.endsWith (".java");
    if (bIsPotentiallyJavaSrcFile)
    {
      final JPackage aPackage = owner ()._package (m_sName.replace ('/', '.'));
      final JDefinedClass aDC = aPackage.getClassResource (sName.substring (0, sName.length () - 5));
      if (aDC != null)
        throw new JClassAlreadyExistsException (aDC);
    }

    // All checks good - add to map
    m_aResources.put (sName, aResFile);
    if (m_aUpperCaseResources != null)
      m_aUpperCaseResources.put (sUpperName, aResFile);

    return aResFile;
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
    if (m_aResources.containsKey (sName))
      return true;
    return false;
  }

  /**
   * Iterates all resource files in this package.
   *
   * @return Iterator
   */
  @Nonnull
  public Iterator <AbstractJResourceFile> resourceFiles ()
  {
    return m_aResources.values ().iterator ();
  }

  /**
   * @return A copy of all contained resource files and never <code>null</code>.
   */
  @Nonnull
  public List <AbstractJResourceFile> getAllResourceFiles ()
  {
    return new ArrayList <> (m_aResources.values ());
  }

  /**
   * Gets a reference to a sub directory of this package.
   *
   * @param sSubDirName
   *        Name of the sub-directory
   * @return New sub-directory
   */
  @Nonnull
  public JResourceDir subDir (@Nonnull final String sSubDirName)
  {
    if (isUnnamed ())
      return owner ().resourceDir (sSubDirName);
    return owner ().resourceDir (m_sName + '/' + sSubDirName);
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
   * Convert the package name to directory path equivalent
   */
  @Nonnull
  File toPath (@Nonnull final File aDir)
  {
    if (isUnnamed ())
      return aDir;
    return new File (aDir, m_sName);
  }

  @Nonnegative
  int countArtifacts ()
  {
    return m_aResources.size ();
  }

  @Nonnull
  String fullChildName (@Nonnull final String sChildName)
  {
    return isUnnamed () ? sChildName : m_sName + '/' + sChildName;
  }
}
