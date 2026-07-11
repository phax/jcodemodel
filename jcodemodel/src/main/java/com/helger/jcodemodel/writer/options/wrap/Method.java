package com.helger.jcodemodel.writer.options.wrap;

import com.helger.jcodemodel.writer.options.Wrap.WrapList;
import com.helger.jcodemodel.writer.options.Wrap.WrapList.EWrapListStrategy;
import com.helger.jcodemodel.writer.options.Wrap.WrapWord;
import com.helger.jcodemodel.writer.options.Wrap.WrapWord.EWrapWordStrategy;

public class Method {

  /// wrapping of the method's return type
  public final WrapWord type =
      new WrapWord()
          .condition(EWrapWordStrategy.NEVER)
          .indent(1);

  /// wrapping of the method's name when declaring it
  public final WrapWord name =
      new WrapWord()
          .condition(EWrapWordStrategy.NEVER)
          .indent(1);

  /// declaration of the method's params
  public final WrapList params =
      new WrapList()
          .condition(EWrapListStrategy.PAST3)
          .indent(1);

  /// wrapping of opening bracket ( '{' ) when declaring
  public final WrapWord bracket =
      new WrapWord()
          .condition(EWrapWordStrategy.NEVER)
          .indent(0);

  /// wrapping of the arguments when calling the method
  public final WrapList args =
      new WrapList()
          .condition(EWrapListStrategy.PAST3)
          .indent(1);

}
