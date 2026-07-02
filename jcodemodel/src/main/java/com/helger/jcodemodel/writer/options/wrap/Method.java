package com.helger.jcodemodel.writer.options.wrap;

import com.helger.jcodemodel.writer.options.Wrap.WrapList;
import com.helger.jcodemodel.writer.options.Wrap.WrapList.EWrapListStrategy;
import com.helger.jcodemodel.writer.options.Wrap.WrapWord;
import com.helger.jcodemodel.writer.options.Wrap.WrapWord.EWrapWordStrategy;

public class Method {

  /// declaration of the method's params
  public final WrapList params =
      new WrapList()
          .condition(EWrapListStrategy.PAST3)
          .indent(1);

  /// wrapping of opening bracket ( '{' )
  public final WrapWord bracket =
      new WrapWord()
          .condition(EWrapWordStrategy.NEVER)
          .indent(0);

}
