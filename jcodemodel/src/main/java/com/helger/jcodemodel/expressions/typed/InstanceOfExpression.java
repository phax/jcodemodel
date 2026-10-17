package com.helger.jcodemodel.expressions.typed;

import java.util.function.Function;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;

/// An expression that is an `X instanceof Y Z` , eg `myObject instanceof String s`
/// @param VarType the runtime type of the new var created, for example String
/// @param ExpressionType the expressionType of the new variable, for example StringExpression
/// 
/// Note : though it makes little sense to pass Void type, this is not technically forbidden by the specs.
/// So the ExpressionType is not `extends NonVoidExpression`, as you may be able to create and work with void types.
/// For example, unmarshaling allows to bypass the absence of constructor. Reflect also works.
/// And of course, you can also `if(null instanceof Void) {}`.
///
public class InstanceOfExpression <VarType, ExpressionType extends ITypedExpression <VarType>> extends BoolExpression
{

  public InstanceOfExpression (IJExpression raw)
  {
    super (raw);
  }

  private ExpressionType typedVar = null;

  public InstanceOfExpression <VarType, ExpressionType> setTypedVar (ExpressionType typedVar)
  {
    this.typedVar = typedVar;
    return this;
  }

  public ExpressionType getTypedVar ()
  {
    return typedVar;
  }

  /// Also apply a check on the new var. This is useful when writing for example
  /// ```java
  /// if( myvar instanceof String s && !s.isBlank() ){}
  /// ```
  ///
  /// @return `that && check(newvar)`
  public BoolExpression and (Function <ExpressionType, BoolExpression> checkMaker)
  {
    return and (checkMaker.apply (getTypedVar ()));
  }

}
