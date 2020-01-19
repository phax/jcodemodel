package com.helger.jcodemodel.util;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation of {@link IFileSystemConvention}
 *
 * @author guiguilechat
 * @author Philip Helger
 * @since 3.4.0
 */
public enum EFileSystemConvention implements IFileSystemConvention
{
  LINUX (true, JCFilenameHelper::isValidLinuxFilename),
  WINDOWS (false, JCFilenameHelper::isValidWindowsFilename);

  /**
   * The default file system convention follows the known rules.
   */
  public static final EFileSystemConvention DEFAULT = JCFilenameHelper.isFileSystemCaseSensitive () ? LINUX : WINDOWS;

  private final boolean m_bIsCaseSensitive;
  private final Predicate <String> m_aCheck;

  private EFileSystemConvention (final boolean bCaseSensitive, @Nonnull final Predicate <String> aCheck)
  {
    m_bIsCaseSensitive = bCaseSensitive;
    m_aCheck = aCheck;
  }

  public boolean isCaseSensistive ()
  {
    return m_bIsCaseSensitive;
  }

  public boolean isValidDirectoryName (@Nullable final String sPath)
  {
    return m_aCheck.test (sPath);
  }

  public boolean isValidFilename (@Nullable final String sPath)
  {
    return m_aCheck.test (sPath);
  }
}
