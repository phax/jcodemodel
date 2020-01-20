package com.helger.jcodemodel.exceptions;

import com.helger.jcodemodel.JCodeModelException;

/**
 * called when trying to create a new file with a name that is not accepted by
 * target platform.
 *
 */
public class JInvalidFileNameException extends JCodeModelException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public JInvalidFileNameException(String fileName) {
    super("invalid file name : " + fileName);
  }

  public JInvalidFileNameException(String fullName, String part) {
    super("Resource name '" + fullName + "' contains the the invalid part '" + part
        + "' according to the current file system conventions");
  }

}
