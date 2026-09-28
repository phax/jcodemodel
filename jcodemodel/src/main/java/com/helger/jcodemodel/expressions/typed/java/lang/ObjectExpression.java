package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.Mirroring;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubLongExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

/// base class for the equals, hashcode, ==null etc. methods that are available for non-primitive types
/// 
/// There is no abstract/concrete couple because the concrete class would also be used as default return mirror.
/// For example, a method returning a `MyClass` would be mirrored into a `return new ObjectExpression<MyClass>(raw…)`.
/// Therefore adding two classes would complexify the hierarchy without a benefit.
public class ObjectExpression <T> extends TypedExpressionWrapper <T>
{

  public ObjectExpression (IJExpression raw)
  {
    super (raw);
  }

  /// @return `that.equals(anObject)`
  @Mirroring ("equals")
  public BoolExpression equals_ (ITypedExpression <?> anObject)
  {
    return new BoolExpression (raw.invoke ("equals").arg (anObject));
  }

  /// @return `that.getClass()`
  @Mirroring ("getClass")
  public ObjectExpression <Class <?>> getClass_ ()
  {
    return new ObjectExpression <> (raw.invoke ("getClass"));
  }

  /// @return `that.hashCode()`
  @Mirroring ("hashCode")
  public IntExpression hashCode_ ()
  {
    return new IntExpression (raw.invoke ("hashCode"));
  }

  /// @return `that.notify()`
  @Mirroring ("notify")
  public VoidStatExpression notify_ ()
  {
    return new VoidStatExpression (raw.invoke ("notify"));
  }

  /// @return `that.notifyAll()`
  @Mirroring ("notifyAll")
  public VoidStatExpression notifyAll_ ()
  {
    return new VoidStatExpression (raw.invoke ("notifyAll"));
  }

  /// @return `that != null`
  public BoolExpression isNotNull ()
  {
    return new BoolExpression (raw.neNull ());
  }

  /// @return `that == null`
  public BoolExpression isNull ()
  {
    return new BoolExpression (raw.eqNull ());
  }

  /// @return `that.toString()`
  @Mirroring ("toString")
  public StringExpression toString_ ()
  {
    return new StringExpression (raw.invoke ("toString"));
  }

  /// @return `that.wait()`
  @Mirroring ("wait")
  public VoidStatExpression wait_ ()
  {
    return new VoidStatExpression (raw.invoke ("wait"));
  }

  /// @return `that.wait(timeoutMillis)`
  @Mirroring ("wait")
  public VoidStatExpression wait_ (ASubLongExpression <?, ?, ?> timeoutMillis)
  {
    return new VoidStatExpression (raw.invoke ("wait").arg (timeoutMillis));
  }

  /// @return `that.wait(timeoutMillis, nanos)`
  @Mirroring ("wait")
  public VoidStatExpression wait_ (ASubLongExpression <?, ?, ?> timeoutMillis, ASubIntExpression <?, ?, ?> nanos)
  {
    return new VoidStatExpression (raw.invoke ("wait").arg (timeoutMillis).arg (nanos));
  }

}
