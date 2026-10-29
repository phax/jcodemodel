package com.helger.jcodemodel.expressions.typed;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JLambdaMethodRef;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;

/// transform several lambda calls to matching expressions.
/// 
/// We define a functional interface extending serializable for all the lambda interfaces we want to mirror.
/// 
public class LambdaExpression
{

  @FunctionalInterface
  public interface SerializablePredicate <T> extends Predicate <T>, Serializable
  {}

  @FunctionalInterface
  public interface SerializableFunction <T, R> extends Function <T, R>, Serializable
  {}

  /// Transforms a static predicate. eg Map::isEmpty .
  /// We need a jcm to get a static link to the class, in that case Map.
  public static <T> ObjectExpression <Predicate <T>> predicate (JCodeModel jcm,
                                                                SerializablePredicate <? extends T> pred)
  {
    try
    {
      SerializedLambda sl = serializeLambda (pred);
      // we need a static method call, so no object.
      if (sl.getCapturedArgCount () > 0)
      {
        throw new IllegalArgumentException ("trying to create a static lambda with " +
                                            sl.getCapturedArgCount () +
                                            " captured arguments. Did you use an object reference ?");
      }
      String methodName = sl.getImplMethodName ();
      // TODO replace name if mirroring method is annotated with @Mirroring
      JLambdaMethodRef ref = new JLambdaMethodRef (jcm.ref (throwayReplace (sl.getImplClass (), '/', '.')), methodName);
      return new ObjectExpression <> (ref);
    }
    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
           InvocationTargetException e)
    {
      throw new IllegalStateException (e);
    }
  }

  /// Transforms a TypeExpression-based instance predicate.
  ///
  /// For example,
  /// ```java
  /// StringExpression se; // assume it's the param of a method
  /// ObjectExpression<Predicate<CharSequence>> pe = predicate(se::contains);
  /// ```
  ///
  /// The predicate is transmitted as a SerializedLambda, we use its first captured argument as the
  /// IJExpression (`se` in the example).
  public static <T> ObjectExpression <Predicate <T>> predicate (SerializableFunction <? extends ITypedExpression <T>, BoolExpression> pred)
  {
    try
    {
      SerializedLambda sl = serializeLambda (pred);
      // we need exactly one param transmitted, the myObject of the `myObject::method`
      if (sl.getCapturedArgCount () != 1)
      {
        throw new IllegalArgumentException ("creating an object lambda expression requires 1 captured arg, we received " +
                                            sl.getCapturedArgCount ());
      }
      ITypedExpression <?> typedExpression = ((ITypedExpression <?>) sl.getCapturedArg (0));
      IJExpression objectRef = typedExpression.raw ();
      String methodName = sl.getImplMethodName ();
      // TODO replace name if mirroring method is annotated with @Mirroring
      JLambdaMethodRef ref = new JLambdaMethodRef (objectRef, methodName);
      return new ObjectExpression <> (ref);
    }
    catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
           NoSuchMethodException e)
    {
      throw new IllegalStateException (e);
    }
  }

  /// need to bypass Method::setAccessible by using reflect otherwise build fails -.-'
  protected static SerializedLambda serializeLambda (Object lambda) throws NoSuchMethodException,
                                                                    SecurityException,
                                                                    IllegalAccessException,
                                                                    IllegalArgumentException,
                                                                    InvocationTargetException
  {
    Method writeReplace = lambda.getClass ().getDeclaredMethod ("writeReplace");
    Method setAccessible = Method.class.getMethod ("setAccessible", boolean.class);
    setAccessible.invoke (writeReplace, true);
    // writeReplace.setAccessible (true);
    return (SerializedLambda) writeReplace.invoke (lambda);
  }

  /// need to rewrite String.replace otherwise build fails -.-'
  /// TODO replace calls with StringHelper::replace when avail and remove this code.
  protected static String throwayReplace (String source, char oldChar, char newChar)
  {
    if (source == null || source.isEmpty ())
      return source;
    boolean changed = false;
    char [] arr = source.toCharArray ();
    for (int i = 0; i < arr.length; i++)
    {
      if (arr[i] == oldChar)
      {
        changed = true;
        arr[i] = newChar;
      }
    }
    return changed ? new String (arr) : source;
  }

}
