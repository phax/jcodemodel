package com.helger.jcodemodel.exceptions;

import com.helger.jcodemodel.JCodeModelException;

/**
 * <p>
 * Exception thrown when trying to create a new resource (folder, file or class
 * file) with a name that is not accepted by target platform.
 * </p>
 * <p>
 * {@link #fullName} , if not null, contains the full name of the directory that
 * was invalid.
 * </p>
 * </p>
 * {@link #partName}, if not null, contains the part of the directory that was
 * invalid.
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
 *
 */
@SuppressWarnings("serial")
public class JInvalidFileNameException extends JCodeModelException
{

  /**
   * create an exception, from an invalid relative part in a file name
   *
   * @param part
   */
  public JInvalidFileNameException (String part)
  {
    super ("invalid file name : " + part);
    fullName = null;
    partName = part;
  }

  /** full name of the file that failed, or null */
  private final String fullName;

  /**
   *
   * @return the {@link #fullName}
   */
  public String getFullName ()
  {
    return fullName;
  }

  /** partial name of the file that failed, or null. */
  private final String partName;

  /**
   *
   * @return the {@link #partName}
   */
  public String getPartName ()
  {
    return partName;
  }

  /**
   * create an exception, from an invalid relative part and or the invalid
   * global file name
   */
  public JInvalidFileNameException (String fullName, String part)
  {
    super ("Resource name '" + fullName + "' contains the the invalid part '" + part
        + "' according to the current file system conventions");
    this.fullName = fullName;
    partName = part;
  }

}
