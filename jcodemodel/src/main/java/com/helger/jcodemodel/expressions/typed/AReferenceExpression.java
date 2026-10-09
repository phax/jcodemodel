package com.helger.jcodemodel.expressions.typed;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.JInstanceOfVar;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubLongExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

/// mother class of expression that resolves to a reference type (as opposed to primitive types)
/// 
/// the equals, hashcode, ==null, etc. methods that are available for non-primitive types
/// 
/// It's abstract because you want people to return an ObjectExpression instead of this one, only acceptable as param type.
public abstract class AReferenceExpression <T> extends ATypedExpressionWrapper <T> implements NonVoidExpression <T>
{

  public AReferenceExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // mirroring
  //

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

  /// Sadly we can't extract the class nor the jcm from the generics
  ///
  /// @param jcm required to convert the type
  /// @param VarType the static class
  /// @param varName the name of the newly created variable.
  /// @return `that instanceof VarType varname`
  public <VarType> InstanceOfExpression <? extends VarType, ? extends ObjectExpression <VarType>> instanceOf (JCodeModel jcm,
                                                                                                              Class <? extends VarType> varClass,
                                                                                                              String varName)
  {
    JInstanceOfVar io = JExpr.instanceOf (raw (), jcm.ref (varClass), varName);
    InstanceOfExpression <VarType, ObjectExpression <VarType>> ret = new InstanceOfExpression <> (io);
    return ret.setTypedVar (new ObjectExpression <> (io.var ()));
  }

  /// @param ExpressionType an objectexpression because we can't cast Object to primitive. `( new
  /// Integer(5) instanceof int i) ` fails.
  /// @return `that instanceof VarType varname`
  @SuppressWarnings ("unchecked")
  public <VarType, ExpressionType extends AReferenceExpression <VarType>> InstanceOfExpression <VarType, ExpressionType> instanceOf (JCodeModel jcm,
                                                                                                                                     Class <? extends VarType> varClass,
                                                                                                                                     String varName,
                                                                                                                                     Class <? extends ExpressionType> retClass)
  {
    try
    {
      JInstanceOfVar io = JExpr.instanceOf (raw (), jcm.ref (varClass), varName);
      MethodHandles.Lookup publicLookup = MethodHandles.publicLookup ();
      MethodType mt = MethodType.methodType (void.class, IJExpression.class);
      MethodHandle constructor = publicLookup.findConstructor (retClass, mt);
      return new InstanceOfExpression <VarType, ExpressionType> (io).setTypedVar ((ExpressionType) constructor.invoke (io.var ()));
    }
    catch (Throwable e)
    {
      throw new RuntimeException (e);
    }
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
