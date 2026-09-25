package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubLongExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

/// base class for the equals, hashcode, ==null etc. methods that are available for non-primitive types
public class ASubObjectExpression <T> extends TypedExpressionWrapper <T>
{

  public ASubObjectExpression (IJExpression raw)
  {
    super (raw);
  }

  // the name is underscored to avoid being called in eg hashmap.
  /// @return `that.equals(anObject)`
  public BoolExpression equals_ (TypedExpressionWrapper <?> anObject)
  {
    return new BoolExpression (raw.invoke ("equals").arg (anObject));
  }

  /// @return `that.getClass()`
  public ASubObjectExpression <Class <?>> getClass_ ()
  {
    return new ASubObjectExpression <> (raw.invoke ("getClass"));
  }

  /// @return `that.hashCode()`
  public IntExpression hashCode_ ()
  {
    return new IntExpression (raw.invoke ("hashCode"));
  }

  /// @return `that.notify()`
  public VoidStatExpression notify_ ()
  {
    return new VoidStatExpression (raw.invoke ("notify"));
  }

  /// @return `that.notifyAll()`
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
  public StringExpression toString_ ()
  {
    return new StringExpression (raw.invoke ("toString"));
  }

  /// @return `that.wait()`
  public VoidStatExpression wait_ ()
  {
    return new VoidStatExpression (raw.invoke ("wait"));
  }

  /// @return `that.wait(timeoutMillis)`
  public VoidStatExpression wait_ (ASubLongExpression <?, ?, ?> timeoutMillis)
  {
    return new VoidStatExpression (raw.invoke ("wait").arg (timeoutMillis));
  }

  /// @return `that.wait(timeoutMillis, nanos)`
  public VoidStatExpression wait_ (ASubLongExpression <?, ?, ?> timeoutMillis, ASubIntExpression <?, ?, ?> nanos)
  {
    return new VoidStatExpression (raw.invoke ("wait").arg (timeoutMillis).arg (nanos));
  }

}
