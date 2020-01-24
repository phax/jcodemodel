package com.helger.jcodemodel.preprocess;

import com.helger.jcodemodel.JCodeModelException;

@SuppressWarnings("serial")
public class PreprocessException extends JCodeModelException
{

  public PreprocessException (String sMsg)
  {
    super (sMsg);
  }

  public PreprocessException (String sMsg, Throwable aCause)
  {
    super (sMsg, aCause);
  }

}
