package com.helger.jcodemodel.plugin.generators.expressions.building;

import java.util.List;

import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression.StringArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.*;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression.BoolArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.BytExpression.ByteArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression.CharArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.DblExpression.DoubleArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.FltExpression.FloatArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression.IntArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.LngExpression.LongArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.ShrtExpression.ShortArrExp;

/// hardcoded source.
public record Source (Class <?> target, Class <?> returnType, Class <?> paramType)
{

  public Source (Class <?> target, Class <?> returnType)
  {
    this (target, returnType, returnType);
  }

  public static final List <Source> HARDCODED_SOURCES = List.of (new Source (boolean.class, BoolExpression.class),
                                                                 new Source (boolean [].class, BoolArrExp.class),
                                                                 new Source (byte.class, BytExpression.class),
                                                                 new Source (byte [].class, ByteArrExp.class),
                                                                 new Source (char.class, CharExpression.class),
                                                                 new Source (char [].class, CharArrExp.class),
                                                                 new Source (double.class,
                                                                             DblExpression.class,
                                                                             ANumericExpression.class),
                                                                 new Source (double [].class, DoubleArrExp.class),
                                                                 new Source (float.class,
                                                                             FltExpression.class,
                                                                             ASubFloatExpression.class),
                                                                 new Source (float [].class, FloatArrExp.class),
                                                                 new Source (int.class,
                                                                             IntExpression.class,
                                                                             ASubIntExpression.class),
                                                                 new Source (int [].class, IntArrExp.class),
                                                                 new Source (long.class,
                                                                             LngExpression.class,
                                                                             ASubLongExpression.class),
                                                                 new Source (long [].class, LongArrExp.class),
                                                                 new Source (short.class, ShrtExpression.class),
                                                                 new Source (short [].class, ShortArrExp.class),
                                                                 new Source (String.class, StringExpression.class),
                                                                 new Source (String [].class, StringArrExp.class),
                                                                 new Source (void.class, VoidStatExpression.class));
}