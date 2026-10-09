package com.helger.jcodemodel.expressions;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// A Typed Expression is an IJExpression (which can be an internal field) for which we know the runtime type resolution.
/// For example, if I call `Jexpr.lit("")` this will return an expression that will resolve to String at runtime.
/// 
/// Specific implementations can provide methods based on the specific class they mirror.
/// For example, the IJExpression for Object, the ObjectExpression, provides an equals_ method to generate a Boolean TypedExpression,
/// mirroring the signature of `Object::equals`.
/// 
/// This allows to use those mirroring methods to generate corresponding calls.
/// For example, `StringExpression.of("test").length().gt(StringExpression.of("bla"))`
/// would generate a boolean typed expression for `"test".length()>"bla".length()`.
/// You can use such an expression in an if test, or in a for test.
/// 
/// To shorten explanations, we refer to the underlying IJExpression as `that`
/// 
/// ## Implementation notes
/// 
/// ### Class hierarchy
/// 
/// Classes are organized based on the type their raw expression resolves to : 
///  - void type should be presented using VoidStatExpression. Example : `myObject.notify()`.
///  - primitive types should be presented using their primitive's class. Example : int should be a `IntExpression`
///  - Array primitive type should use the corresponding primitive's array type. Example : `boolean[]` should be a `BoolExpression.BoolArrExp`
/// 
/// ### Mirroring classes
/// 
/// Most Typed Expression implementation for a mirrored **non-final** class should have 
///  - an abstract implementation, parameterized with `<T extends MirroredClass>` that mirrors methods of the mirrored class,
///  - a single final concrete implementation, extending the abstract one with the `<MirroredClass>`, to create corresponding expressions.
/// 
/// This is important because a HashMapExpression should extend a MapExpression, but java does not let us re-specify the implements with a sub type.
/// We can't have `HashMapExpression extends MapExpression implements ITypedExpression<HashMap>`.
/// Instead we have a super abstract class, still templated, and we set the param only in the concrete class.
/// 
/// Typed Expression mirroring a **final** classes should only have a concrete class mirroring the methods.
/// That's because even if they can be extended, they can't resolve to  a sub class since the one resolved is final.
/// 
/// The concrete mirror should also have static methods to create an instance from 
///  - an IJExpression
///  - a TypedExpression<?>
///  - null
/// 
/// Setting a concrete class to final is a bad idea : those can be used for a different expression.
/// 
/// ### Mirroring methods
/// 
/// All methods m with a parameter named *p* and non-primitive type `P` should be mirrored with a parameter named *p* and type `NonVoidExpression<TP>`, 
/// with TP being
///  - `P` if P is final
///  - `? extends P` if P is not final
/// 
/// The primitive params (void, numeric, and arrays) should be mirrored by the matching abstract class.
/// 
/// Arrays are considered primitives since they are all final. TODO rework, they are covariant.
/// 
/// The return type should be the lowest mirrored concrete class returned, or `ObjectExpression<P>` if none present.
/// For example, if we have mirrors for Collections and Map, but not for HashMap, then a MapExpression should be returned.
/// If none of them are mirrored, then returning a `ObjectExpression<HashMap>` is the only possibility. 
/// 
/// For example,
/// ```java
/// // mirrors int meth(int intParam, Integer integerParam);
/// IntExpression meth(ASubIntExpression<?,?,?> intParam, ITypedExpression<Integer> integerParam);
/// // mirrors void meth2(Object objectParam);
/// VoidStatExpression meth2(ITypedExpression<? extends Object> objectParam);
/// ```
/// 
/// @param RunTimeType the type we know that expression will resolve to at runtime. Also the type of `that`.
/// 
public interface ITypedExpression <RunTimeType>
{
  @NonNull
  IJExpression raw ();

}
