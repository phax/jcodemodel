/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.io.file.FilenameHelper;
import com.helger.jcodemodel.exceptions.JClassAlreadyExistsException;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.exceptions.JInvalidFileNameException;
import com.helger.jcodemodel.exceptions.JResourceAlreadyExistsException;
import com.helger.jcodemodel.fmt.AbstractJResourceFile;
import com.helger.jcodemodel.util.FSName;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A Java resource directory - complementary to a {@link JPackage}.
 *
 * @since 3.3.1
 */
public class JResourceDir implements IJOwned, Serializable
{
  public static final char SEPARATOR = FilenameHelper.UNIX_SEPARATOR;
  public static final String SEPARATOR_STR = Character.toString (SEPARATOR);

  private final JCodeModel m_aOwner;

  /**
   * The optional parent directory.
   */
  private final JResourceDir m_aParentDir;

  /**
   * Name of the package. May be the empty string for the root package.
   */
  private final String m_sName;

  /**
   * Map of resources files inside this package.
   */
  private final Map <FSName, AbstractJResourceFile> m_aResources = new TreeMap <> ();

  /**
   * Constructor
   *
   * @param aOwner
   *        The code writer being used to create this package. May not be <code>null</code>.
   * @param aParentDir
   *        The parent directory. May only be <code>null</code> for the target root resource
   *        directory. In that case the name must be "".
   * @param sName
   *        Name of directory. May not be <code>null</code> but empty. No absolute paths are allowed
   *        and only Linux forward slashes may be used as path separators.
   * @throws JInvalidFileNameException
   *         If a part of the package name is not a valid filename part.
   */
  protected JResourceDir (@Nonnull final JCodeModel aOwner,
                          @Nullable final JResourceDir aParentDir,
                          @Nonnull final String sName) throws JInvalidFileNameException
  {
    ValueEnforcer.notNull (sName, "Name");
    ValueEnforcer.notNull (aOwner, "CodeModel");
    if (aParentDir == null)
      ValueEnforcer.isTrue (sName.length () == 0, "If no parent directory is provided, the name must be empty");
    if (sName.length () == 0)
      ValueEnforcer.isNull (aParentDir, "If no name is provided, the parent directory must be null");

    m_aOwner = aOwner;
    m_aParentDir = aParentDir;
    m_sName = sName;

    // An empty directory name is okay
    if (sName.length () > 0)
      for (final String sPart : StringHelper.getExplodedArray (JResourceDir.SEPARATOR, sName))
        if (!aOwner.getFileSystemConvention ().isValidDirectoryName (sPart))
          throw new JInvalidFileNameException (sName, sPart);
  }

  /**
   * @return the code model root object being used to create this resource directory.
   */
  @Override
  @Nonnull
  public final JCodeModel owner ()
  {
    return m_aOwner;
  }

  /**
   * Get the name of this resource directory. This name is never an absolute path. This name never
   * ends with a slash. This name always uses the forward slash (/) as a separator and never the
   * Windows backslash.
   *
   * @return The name of this resource directory, or the empty string if this is the root directory.
   *         For example, this method returns strings like <code>"dir1/dir2/dir3"</code>
   */
  @Nonnull
  public final String name ()
  {
    return m_sName;
  }

  /**
   * @return the parent resource directory, or <code>null</code> if this is the root resource
   *         directory.
   */
  @Nullable
  public JResourceDir parent ()
  {
    return m_aParentDir;
  }

  @Nonnull
  private FSName _createFSName (@Nonnull final String sName)
  {
    if (m_aOwner.getFileSystemConvention ().isCaseSensistive ())
      return FSName.createCaseSensitive (sName);
    return FSName.createCaseInsensitive (sName);
  }

  @Nonnull
  private JPackage _getMatchingPackage ()
  {
    return owner ()._package (StringReplace.replaceAll (m_sName, SEPARATOR, JPackage.SEPARATOR));
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
    ValueEnforcer.notNull (aResFile, "ResourceFile");

    final String sName = aResFile.name ();

    if (!m_aOwner.getFileSystemConvention ().isValidFilename (sName))
      throw new IllegalArgumentException ("Resource filename '" +
                                          sName +
                                          "' is invalid according to the current file system conventions");

    // Check if a sub directory already exists with the same name
    if (m_aOwner.containsResourceDir (fullChildName (sName)))
      throw new JResourceAlreadyExistsException (fullChildName (sName));

    // Check filename uniqueness
    final FSName aKey = _createFSName (sName);
    if (m_aResources.containsKey (aKey))
      throw new JResourceAlreadyExistsException (fullChildName (sName));

    // Check if a Java class with the same name already exists
    if (StringHelper.endsWithIgnoreCase (sName, ".java"))
    {
      // Cut trailing ".java"
      final JDefinedClass aDC = _getMatchingPackage ()._getClass (sName.substring (0, sName.length () - 5));
      if (aDC != null)
        throw new JClassAlreadyExistsException (aDC);
    }

    // All checks good - add to map
    m_aResources.put (aKey, aResFile);

    return aResFile;
  }

  /**
   * Checks if a resource of the given name exists. This method does not consider file system
   * conventions.
   *
   * @param sName
   *        Filename to check. May be <code>null</code>.
   * @return <code>true</code> if contained
   */
  public boolean hasResourceFile (@Nullable final String sName)
  {
    final FSName aKey = _createFSName (sName);
    return m_aResources.containsKey (aKey);
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
   * @throws JCodeModelException
   *         In case a resource file with the specified name is already present
   */
  @Nonnull
  public JResourceDir subDir (@Nonnull final String sSubDirName) throws JCodeModelException
  {
    // Check if a file with the same name already exists
    if (hasResourceFile (sSubDirName))
      throw new JResourceAlreadyExistsException (fullChildName (sSubDirName));

    // Check if a Java class with the same name already exists
    if (StringHelper.endsWithIgnoreCase (sSubDirName, ".java"))
    {
      // Cut trailing ".java"
      final JDefinedClass aDC = _getMatchingPackage ()._getClass (sSubDirName.substring (0, sSubDirName.length () - 5));
      if (aDC != null)
        throw new JClassAlreadyExistsException (aDC);
    }

    return owner ().resourceDir (isUnnamed () ? sSubDirName : m_sName + SEPARATOR + sSubDirName);
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
    return isUnnamed () ? aDir : new File (aDir, m_sName);
  }

  @Nonnegative
  int countArtifacts ()
  {
    return m_aResources.size ();
  }

  @Nonnull
  String fullChildName (@Nonnull final String sChildName)
  {
    return isUnnamed () ? sChildName : m_sName + SEPARATOR + sChildName;
  }

  @Nonnull
  static JResourceDir root (@Nonnull final JCodeModel aOwner)
  {
    try
    {
      return new JResourceDir (aOwner, null, "");
    }
    catch (final JInvalidFileNameException e)
    {
      // should not happen
      throw new UnsupportedOperationException (e);
    }
  }
}
