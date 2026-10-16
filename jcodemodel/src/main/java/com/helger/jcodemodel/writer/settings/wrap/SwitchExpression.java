package com.helger.jcodemodel.writer.settings.wrap;

import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping.EWordWrapStrategy;

public class SwitchExpression
{

  /// wrapping of the switch's labels
  public final ListWrapping labels = new ListWrapping ().condition (EListWrapStrategy.REQUIRED).indent (1);

  /// wrapping of single-statement line eg case 1 -> 1;
  public final WordWrapping singleLine = new WordWrapping ().condition (EWordWrapStrategy.ALWAYS);

}
