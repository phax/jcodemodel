package com.helger.jcodemodel.exceptions;

import com.helger.jcodemodel.JCodeModelException;

public class JCaseSensitivityChangeException extends JCodeModelException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public JCaseSensitivityChangeException() {
    super(
        "The FileSystem convention cannot be changed for one with a different case sensitivity if a package or a resource directory already exists.");
  }

}
