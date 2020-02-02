package com.helger.jcodemodel.preprocessors.exceptions;

import com.helger.jcodemodel.preprocessors.PreprocessException;

@SuppressWarnings("serial")
public class BeanProcessorException extends PreprocessException
{

  public BeanProcessorException (String sMsg)
  {
    super (sMsg);
  }

  public BeanProcessorException (String sMsg, Throwable aCause)
  {
    super (sMsg, aCause);
  }

}
