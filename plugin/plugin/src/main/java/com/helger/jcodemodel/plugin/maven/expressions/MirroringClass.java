package com.helger.jcodemodel.plugin.maven.expressions;


import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression.StringArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.*;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression.BoolArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.ByteExpression.ByteArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression.CharArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.DblExpression.DoubleArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.FltExpression.FloatArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression.IntArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.LngExpression.LongArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.ShortExpression.ShortArrExp;
import com.helger.jcodemodel.plugin.maven.expressions.MirroringClass.HardcodedMirror.Source;

// the resolution of a runtime class  to the mirroring expression. 
public sealed interface MirroringClass
{

  /// the runtime class we mirror
  public Class <?> target ();

  /// the returned types in a mirrored function. Still needs to be parametrized.
  public AbstractJClass asReturn ();

  /// when true, the return must be generified using the method return type. Otherwise, only apply
  /// the return type's parameter.
  ///
  /// for example, a returned `List<E>` can be mirrored into a `ObjectExpression<List<E>>`, using
  /// the full generification, or a `ListExpression<E>`, only generified using the type's own
  /// parameters (`E`)
  default boolean returnFullyGenerified ()
  {
    return false;
  }

  /// The param types in a mirrored function. Still needs parameters.
  public AbstractJClass asParam ();

  default AbstractJClass generifiedParam (ParameterizedType pt, JCodeModel jcm)
  {
    if (isFinal ())
    {
      return asParam ().erasure ().narrow (jcm.ref (pt));
    }
    else
    {
      List <AbstractJClass> narrows = new ArrayList <> ();
      for (Type ata : pt.getActualTypeArguments ())
      {
        narrows.add (jcm.ref (ata));
      }
      narrows.add (jcm.ref (pt));
      return asParam ().erasure ().narrow (narrows);
    }
  }

  public default boolean isFinal ()
  {
    return (target ().getModifiers () & Modifier.FINAL) > 0;
  }

  // update a jdc mirroring a subclass to keep the inheritance. if this' resolved class is final,
  // throws an exception.
  public default void parentOf (TargetMirror t)
  {
    if (isFinal ())
      throw new IllegalArgumentException ("class " + target () + " is final and can't be extended by another class");
    t.mainClass ()._extends (asParam ().erasure ().narrow (t.superRefParam ()));
  }

  //
  // implementations
  //

  /// when the mirroring is hardcoded.
  public static record HardcodedMirror (Class <?> target, AbstractJClass asReturn, AbstractJClass asParam) implements
                                       MirroringClass
  {


    // usual case, when the class is not final it has one param type that is extended by the
    // returned type. eg ASubIntExpression param type, and its IntExpression returned type.
    public HardcodedMirror (JCodeModel jcm, Class <?> target, Class <?> returnType, Class <?> paramType)
    {
      this (target, jcm.ref (returnType), wildcardedRef (jcm, paramType));
    }

    // typically when the class is final, eg short/byte
    public HardcodedMirror (JCodeModel jcm, Class <?> target, Class <?> bothTypes)
    {
      this (target, jcm.ref (bothTypes), wildcardedRef (jcm, bothTypes));
    }

    /// reference a harcoded mirroring class with its type generics replaced with `?`
    static AbstractJClass wildcardedRef (JCodeModel jcm, Class <?> paramType)
    {
      AbstractJClass ret = jcm.ref (paramType);
      if (paramType.getTypeParameters ().length > 0)
      {
        List <AbstractJClass> narrows = new ArrayList <> ();
        for (@SuppressWarnings ("unused")
        TypeVariable <?> tv : paramType.getTypeParameters ())
        {
          narrows.add (jcm.wildcard ());
        }
        ret = ret.narrow (narrows);
      }
      return ret;
    }

    /// hardcoded source.
    public static record Source (Class <?> target, Class <?> returnType, Class <?> paramType)
    {

      public Source (Class <?> target, Class <?> returnType)
      {
        this (target, returnType, returnType);
      }
    }

    public static HardcodedMirror of (JCodeModel jcm, Source source)
    {
      return source.paramType == null ? new HardcodedMirror (jcm, source.target (), source.returnType ())
                                      : new HardcodedMirror (jcm,
                                                             source.target (),
                                                             source.returnType (),
                                                             source.paramType ());
    }

  }

  public static final List <Source> HARDCODED_SOURCES = List.of (new Source (boolean.class, BoolExpression.class),
                                                                 new Source (boolean [].class, BoolArrExp.class),
                                                                 new Source (byte.class, ByteExpression.class),
                                                                 new Source (byte [].class, ByteArrExp.class),
                                                                 new Source (char.class, CharExpression.class),
                                                                 new Source (char [].class, CharArrExp.class),
                                                                 new Source (double.class,
                                                                             DblExpression.class,
                                                                             ANumericExpression.class),
                                                                 new Source (double [].class, DoubleArrExp.class),
                                                                 new Source (float.class,
                                                                             FltExpression.class,
                                                                             ASubFloatExpression.class),
                                                                 new Source (float [].class, FloatArrExp.class),
                                                                 new Source (int.class,
                                                                             IntExpression.class,
                                                                             ASubIntExpression.class),
                                                                 new Source (int [].class, IntArrExp.class),
                                                                 new Source (long.class,
                                                                             LngExpression.class,
                                                                             ASubLongExpression.class),
                                                                 new Source (long [].class, LongArrExp.class),
                                                                 new Source (short.class, ShortExpression.class),
                                                                 new Source (short [].class, ShortArrExp.class),
                                                                 new Source (String.class, StringExpression.class),
                                                                 new Source (String [].class, StringArrExp.class),
                                                                 new Source (void.class, VoidStatExpression.class));

  public static Stream <HardcodedMirror> stream (JCodeModel jcm)
  {
    return HARDCODED_SOURCES.stream ().map (s -> HardcodedMirror.of (jcm, s));
  }

  
  public static sealed interface TargetMirror extends MirroringClass
  {

    /**
     * @return the jdefinedclass that shall effectively mirror the target's public methods/fields,
     *         and be extended if non final
     */
    public JDefinedClass mainClass ();

    /**
     * @return how to parameter the super class, typically &lt;X extends target()&gt;.
     */
    public AbstractJClass superRefParam ();

  }

  // a target (a class we need to mirror) that is final, so only one type for both param and return.
  public static record FinalTargetMirror (Class <?> target, JDefinedClass bothType, AbstractJClass superRefParam)
                                         implements
                                         TargetMirror
  {
    public FinalTargetMirror (Class <?> target, JDefinedClass bothType)
    {
      this (target, bothType, ExpressionsBuildingProcess.referenceWithBounds (target, bothType.owner ()));
    }

    @Override
    public AbstractJClass asParam ()
    {
      return bothType ();
    }

    @Override
    public AbstractJClass asReturn ()
    {
      return bothType ();
    }

    @Override
    public JDefinedClass mainClass ()
    {
      return bothType ();
    }
  }

  // a target that is not final, therefore a generic param type, and a return type that extends the
  // param type with the exact generic type.
  public static record NonFinalTargetMirror (Class <?> target,
                                             JDefinedClass asReturn,
                                             JDefinedClass asParam,
                                             AbstractJClass superRefParam) implements TargetMirror
  {

    NonFinalTargetMirror (Class <?> target, JDefinedClass asReturn, JDefinedClass asParam)
    {
      this (target,
            asReturn,
            asParam,
            asParam.generify ("Contained", ExpressionsBuildingProcess.referenceWithBounds (target, asReturn.owner ())));
    }

    @Override
    public JDefinedClass mainClass ()
    {
      return asParam ();
    }

  }

  /// if a class is not a target, not hardcoded, then we use generic to mirror it.
  ///
  /// if the target is final, then we have one single same type for parameters and return types.
  /// Otherwise we have a `GenericClass<T extends target>` for parameters, and a `GenericClass<T>`
  /// for return.
  public static record GenericMirror (Class <?> target, AbstractJClass asReturn, AbstractJClass asParam) implements
                                          MirroringClass
  {

    // when final, same both types.
    public GenericMirror (Class <?> target, AbstractJClass bothTypes)
    {
      this (target, bothTypes, bothTypes);
    }

    @Override
    public boolean returnFullyGenerified ()
    {
      return true;
    }

    public AbstractJClass generifiedParam (ParameterizedType pt, JCodeModel jcm)
    {
      if (isFinal ())
      {
        return asParam ().erasure ().narrow (jcm.ref (pt));
      }
      else
      {
        return asParam ().erasure ().narrow (jcm.ref (pt).wildcardExtends ());
      }
    }

  }

}
