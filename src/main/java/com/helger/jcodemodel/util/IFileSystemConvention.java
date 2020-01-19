package com.helger.jcodemodel.util;

import javax.annotation.Nullable;

/**
 * This abstract interface defines the rules
 *
 * @author guiguilechat
 * @author Philip Helger
 * @since 3.4.0
 */
public interface IFileSystemConvention
{
  /**
   * @return <code>true</code> if the represented file system is case sensitive
   *         (e.g. Linux), <code>false</code> if it case insensitive (e.g.
   *         Windows)
   */
  boolean isCaseSensistive ();

  /**
   * Check if the passed name is valid for a directory according to the
   * underlying specifications. The names passed in to this method may not
   * contain a path separator.
   *
   * @param sPath
   *        The directory name to check.
   * @return <code>true</code> if the directory name is valid,
   *         <code>false</code> if not
   */
  boolean isValidDirectoryName (@Nullable String sPath);

  /**
   * Check if the passed name is valid for a file according to the underlying
   * specifications.
   *
   * @param sPath
   *        The filename to check.
   * @return <code>true</code> if the filename is valid, <code>false</code> if
   *         not
   */
  boolean isValidFilename (@Nullable String sPath);
}
