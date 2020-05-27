package com.helger.jcodemodel.exceptions;

import javax.annotation.Nullable;

/**
 * <p>
 * Exception thrown when trying to create a new resource (folder, file or class
 * file) with a name that is not accepted by target platform.
 * </p>
 * <p>
 * full name, if not null, contains the full name of the directory that was
 * invalid.
 * </p>
 * </p>
 * part name, if not null, contains the part of the directory that was invalid.
 * </p>
 * <p>
 * If both are null, it means that the platform had a rejection based on
 * something else, eg a limit on the number of different files the platform can
 * accept //TODO should it go in another exception ?
 * </p>
 * <p>
 * Typically, if the platform does not accept resources with a name starting
 * with a "cr" , trying to create the file "crazy/cropped" would fail, with
 * fulName being "crazy/cropped" or null depending on the method that threw that
 * exception, and partName being "crazy" or "cropped" depending on the method
 * that threw that exception.
 * </p>
 *
 * @author glelouet
 */
public class JInvalidFileNameException extends JCodeModelException
{

  /** full name of the file that failed, or null */
  private final String m_sFullName;
  /** partial name of the file that failed, or null. */
  private final String m_sPartName;

  /**
   * create an exception, from an invalid relative part and or the invalid
   * global file name
   *
   * @param fullName
   *        full name
   * @param part
   *        part name
   */
  public JInvalidFileNameException (@Nullable final String fullName, @Nullable final String part)
  {
    super ("Resource name '" +
           fullName +
           "' contains the the invalid part '" +
           part +
           "' according to the current file system conventions");
    m_sFullName = fullName;
    m_sPartName = part;
  }

  /**
   * create an exception, from an invalid relative part in a file name
   *
   * @param part
   *        part name
   */
  public JInvalidFileNameException (@Nullable final String part)
  {
    super ("invalid file name : " + part);
    m_sFullName = null;
    m_sPartName = part;
  }

  /**
   * @return the full name
   */
  @Nullable
  public String getFullName ()
  {
    return m_sFullName;
  }

  /**
   * @return the part name
   */
  @Nullable
  public String getPartName ()
  {
    return m_sPartName;
  }
}
