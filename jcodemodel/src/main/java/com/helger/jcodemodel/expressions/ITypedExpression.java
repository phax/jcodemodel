package com.helger.jcodemodel.expressions;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// A typed expression statically generates an IJExpression (which is usually an internal field)
/// 
/// Specific implementations can therefore provide methods based on a specific class they mirror.
/// For example, the IJExpression for Object, the ObjectExpression, provides an equals_ method to generate a Boolean TypedExpression,
/// mirroring the signature of `Object::equals`.
/// 
/// This allows to use those mirroring methods to generate corresponding calls.
/// For example, `StringExpression.of("test").length().gt(StringExpression.of("bla"))`
/// would generate a boolean typed expression for `"test".length()>"bla".length()`.
/// That you can use for example in an if or in the test of a for.
/// 
/// To shorten explanations, we refer to the underlying IJExpression as `that`
/// 
/// ## Implementation notes 
/// 
/// Most Typed Expression implementation for a mirrored non-final class should have 
///  - an abstract implementation, parameterized with `<T extends MirroredClass>` that mirrors methods of the mirrored class,
///  - a single final concrete implementation, extending the abstract one with the `<MirroredClass>`, to create corresponding expressions.
/// 
/// Typed Expression mirroring a final classes should only have a concrete class mirroring the methods.
/// 
/// This is important because we want to create expressions easily yet a HashMapExpression should extend a MapExpression,
/// but java does not let us re-specify the implements with a sub type.
/// Exceptions to this are ObjectExpression (for sub object) and TypedExpressionWrapper (for unknown type). 
/// 
/// The concrete mirror should also have static methods to create an instance from 
///  - an IJExpression
///  - a TypedExpression<?>
/// 
/// @param RunTimeType the type we know that expression will resolve to at runtime. also the type of `that`.
/// 
public interface ITypedExpression <RunTimeType>
{
  @NonNull
  IJExpression raw ();

}
