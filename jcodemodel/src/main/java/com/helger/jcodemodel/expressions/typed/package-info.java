/**
 * Experimental: typed wrappers around {@link com.helger.jcodemodel.IJExpression} so that the Java
 * compiler checks the generator code against the Java type rules of the generated code.
 * <p>
 * Design in a nutshell:
 * <ul>
 * <li>{@link com.helger.jcodemodel.expressions.typed.ITypedExpression ITypedExpression&lt;T&gt;}
 * carries the <b>boxed</b> runtime type <code>T</code>. Because of that, boxing (<code>int</code>
 * &rarr; <code>Integer</code> &rarr; <code>Number</code> &rarr; <code>Object</code>) works by
 * plain generics: an <code>IntExpression</code> is an <code>ITypedExpression&lt;Integer&gt;</code>
 * and is accepted by a parameter mirror <code>ITypedExpression&lt;? extends Number&gt;</code>.</li>
 * <li>"Can be passed to a parameter of primitive type X" is a separate interface hierarchy
 * (<code>IDoubleConvertible</code> &larr; <code>IFloatConvertible</code> &larr;
 * <code>ILongConvertible</code> &larr; <code>IIntConvertible</code> &larr;
 * <code>IShortConvertible</code> &larr; <code>IByteConvertible</code>, plus
 * <code>ICharConvertible</code> &rarr; <code>IIntConvertible</code>). Primitive mirrors and
 * boxed mirrors (<code>IntegerExpression</code>, <code>CharacterExpression</code>) both implement
 * it, so unboxing followed by widening needs no explicit call. Since the convertibility
 * interfaces and <code>T</code> are independent, <code>Character</code> is still not accepted
 * where <code>Integer</code> is expected.</li>
 * <li>Binary numeric promotion is overload resolution: each convertibility level declares the
 * operators returning its own type, and javac picks the most specific applicable one.</li>
 * <li>Operators whose result type depends on the left operand only (unary minus, complement,
 * shifts) are declared on the concrete types or on <code>IIntPromotedOperators</code>.</li>
 * <li>Arrays: <code>ArrayExpression&lt;A, M, P&gt;</code> carries the array type <code>A</code> as runtime
 * type, so reference array covariance and the invariance of primitive arrays follow from the
 * generics. <code>ArrayType</code> keeps <code>A</code> and the component mirror <code>M</code>
 * consistent.</li>
 * <li>Variables: <code>TypedVariable&lt;M, P&gt;</code> reads as the mirror <code>M</code> and
 * only accepts the parameter mirror <code>P</code> on assignment. <code>TypedType</code> describes
 * a type as such a pair, and arrays are built on top of it.</li>
 * </ul>
 * Nothing in the core API depends on this package. Pass {@link ITypedExpression#raw()} wherever
 * an {@link com.helger.jcodemodel.IJExpression} is needed.
 */
package com.helger.jcodemodel.expressions.typed;
