package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// Concrete class to default when mirroring a returned non-primitive type, including Object.
/// 
/// Mirroring a returned String to a `ObjectExpression<String>` is correct,
/// but returning a `StringExpression` instead would allow more specific methods,
/// as the latter mirrors the `String::length` for example.
public class ObjectExpression <T> extends AReferenceExpression <T>
{

  public ObjectExpression (IJExpression raw)
  {
    super (raw);
  }

  //
  // static tooling
  //

  private static final ObjectExpression <Object> NULL = of (JExpr._null ());

  @SuppressWarnings ("unchecked")
  public static <T> ObjectExpression <T> null_ ()
  {
    return (ObjectExpression <T>) NULL;
  }

  public static <T> ObjectExpression <T> of (IJExpression raw)
  {
    return new ObjectExpression <> (raw);
  }

  public static <T> ObjectExpression <T> of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  /// only works for non generic classes.
  public static <T> ObjectExpression <T> of (JMethod m, Class <T> cl, String name)
  {
    return m.paramTyped (name, cl, ObjectExpression::of);
  }

}
