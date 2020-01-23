package com.helger.jcodemodel.exceptions;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.util.IFileSystemConvention;

/**
 * Exception thrown when trying to replace the existing
 * {@link IFileSystemConvention} of a {@link JCodeModel} by another one with a
 * different case sensitivity
 *
 * @author glelouet
 *
 */
@SuppressWarnings("serial")
public class JCaseSensitivityChangeException extends JCodeModelException
{

  public JCaseSensitivityChangeException ()
  {
    super (
        "The FileSystem convention cannot be changed for one with a different case sensitivity if a package or a resource directory already exists.");
  }

}
