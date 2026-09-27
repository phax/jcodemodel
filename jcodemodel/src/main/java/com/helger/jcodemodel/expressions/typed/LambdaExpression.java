package com.helger.jcodemodel.expressions.typed;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JLambdaMethodRef;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;

/// transform several lambda calls to matching expressions.
/// 
/// we create functional interface with serializable for all the interfaces we want to mirror.
/// 
public class LambdaExpression
{

  @FunctionalInterface
  public interface SerializablePredicate <T> extends Predicate <T>, Serializable
  {}

  @FunctionalInterface
  public interface SerializableFunction <T, R> extends Function <T, R>, Serializable
  {}

  /// transforms a static predicate. eg Map::isEmpty .
  /// We need a jcm to static link the class, eg Map.
  ///
  public static <T> ObjectExpression <Predicate <T>> predicate (JCodeModel jcm,
                                                                SerializablePredicate <? extends T> pred)
  {
    try
    {
      Method writeReplace = pred.getClass ().getDeclaredMethod ("writeReplace");
      writeReplace.setAccessible (true);
      SerializedLambda sl = (SerializedLambda) writeReplace.invoke (pred);
      // we need a static method call, so no object.
      if (sl.getCapturedArgCount () > 0)
      {
        throw new UnsupportedOperationException ();
      }
      JLambdaMethodRef ref = new JLambdaMethodRef (jcm.ref (sl.getImplClass ().replace ('/', '.')),
                                                   sl.getImplMethodName ());
      return new ObjectExpression <> (ref);
    }
    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
           InvocationTargetException e)
    {
      throw new RuntimeException (e);
    }
  }

  /// transforms a TypeExpression-based instance predicate. eg myStringExpression::isblank .
  /// we dont need a JCM as the ITypedExpression instance is transmitted as SerializedLambda
  /// captured arg, we retrieve the underlying IJExpression from it.
  ///
  public static <T> ObjectExpression <Predicate <T>> predicate (SerializableFunction <? extends ITypedExpression <T>, BoolExpression> pred)
  {
    try
    {
      Method writeReplace = pred.getClass ().getDeclaredMethod ("writeReplace");
      writeReplace.setAccessible (true);
      SerializedLambda sl = (SerializedLambda) writeReplace.invoke (pred);
      // we need exactly one param transmitted, in the for of myObject::method
      if (sl.getCapturedArgCount () != 1)
      {
        throw new UnsupportedOperationException ();
      }

      String methodName = sl.getImplMethodName ();
      JLambdaMethodRef ref = new JLambdaMethodRef (((ITypedExpression <?>) sl.getCapturedArg (0)).raw (),
                                                   methodName);
      return new ObjectExpression <> (ref);
    }
    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
           InvocationTargetException e)
    {
      throw new RuntimeException (e);
    }
  }

}
