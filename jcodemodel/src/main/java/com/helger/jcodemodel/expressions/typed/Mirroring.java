package com.helger.jcodemodel.expressions.typed;

/// Indicates that the method declared in a ITypedExpression mirrors an existing method in a mirrored class.
/// 
/// This is only required if the mirroring class has a name different from the mirrored one.
/// 
/// Example : if you mimic the Object class, then you can't create a mirroring method named `equals` as this would clash with `Object.equals`
/// So instead you underscore it :
/// ```java
/// @Mirroring("equals")
/// public BoolExpression equals_(AObjectExpression <?> other)
/// ```
/// 
public @interface Mirroring
{
  
  String value();

}
