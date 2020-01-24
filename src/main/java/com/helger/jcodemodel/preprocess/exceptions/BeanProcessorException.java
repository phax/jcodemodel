package com.helger.jcodemodel.preprocess.exceptions;

import com.helger.jcodemodel.preprocess.PreprocessException;

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
