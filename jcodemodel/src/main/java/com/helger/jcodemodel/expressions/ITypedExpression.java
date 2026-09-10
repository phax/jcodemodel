package com.helger.jcodemodel.expressions;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// A typed expression can resolve to an IJExpression (which is usually an internal field) and knows (at design time) the class of the expression produced by the code at compile and run time.
/// 
/// Specific implementations can therefore provide specific methods, based on the manipulation of the specific class.
/// 
/// @param RunTimeType the type we know that expression will resolve to at runtime.
/// 
public interface ITypedExpression <RunTimeType>
{
  @NonNull
  public IJExpression raw ();

}
