package com.helger.jcodemodel.plugin.generators.expressions.building;


import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

/// the resolution of a runtime class  to the mirroring expression. 
/// the types are the one with no additional parameter. They need to be adapted.
public sealed interface MirroringClass
{

  /// the runtime class we mirror
  public Class <?> target ();

  /// the returned types in a mirrored function. Still needs to be parametrized.
  public AbstractJClass asReturn ();

  /// applies the narrows to param type
  ///
  /// - if no param empty, return the param type
  /// - if target is final, return the param type, parameterized with the ref to the target, itself
  /// narrowed to the narrows.
  /// - return the param type, parameterized with ? extends the ref to the target, itself narrowed
  /// to the narrows.
  ///
  /// @param narrows the mirrored types paremeterizing the param.
  /// For example, a method with a param `Map<String, Integer> m` would mirror the param type
  /// ```java
  /// var narrows = List.of(mirror(String.class), mirror(Integer.class));
  /// mType = mirror(Map.class).param(narrows);
  /// ```
  public default AbstractJClass param (@NonNull List <AbstractJClass> narrows)
  {
    if (narrows == null || narrows.isEmpty ())
      return asParam ();
    AbstractJClass containedClass = asParam ().owner ().ref (target ()).narrow (narrows);
    return asParam ().erasure ().narrow (isFinal () ? containedClass : containedClass.wildcardExtends ());
  }

  public default AbstractJClass param (AbstractJClass... narrows)
  {
    return param (narrows == null || narrows.length == 0 ? List.of () : List.of (narrows));
  }

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
    t.mainClass ()._extends (asReturn ().erasure ().narrow (t.superRefParam ()));
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

    public static HardcodedMirror of (JCodeModel jcm, Source source)
    {
      return source.paramType () == null ? new HardcodedMirror (jcm, source.target (), source.returnType ())
                                      : new HardcodedMirror (jcm,
                                                             source.target (),
                                                             source.returnType (),
                                                             source.paramType ());
    }

  }

  public static Stream <HardcodedMirror> stream (JCodeModel jcm)
  {
    return Source.HARDCODED_SOURCES.stream ().map (s -> HardcodedMirror.of (jcm, s));
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
  public static record FinalTargetMirror (Class <?> target,
                                          JDefinedClass asReturn,
                                          AbstractJClass asParam,
                                          AbstractJClass superRefParam)
                                         implements
                                         TargetMirror
  {
    public FinalTargetMirror (Class <?> target, JDefinedClass asReturn, AbstractJClass asParam)
    {
      this (target, asReturn, asParam, ExpressionsBuildingProcess.referenceWithBounds (target, asReturn.owner ()));
    }

    @Override
    public JDefinedClass mainClass ()
    {
      return asReturn ();
    }
  }

  // a target that is not final, therefore a generic-based param type, and a return type that
  // extends an abstract type with the exact generic type.
  public static record NonFinalTargetMirror (Class <?> target,
                                             JDefinedClass asReturn,
                                             JDefinedClass abstractType,
                                             AbstractJClass asParam,
                                             AbstractJClass superRefParam) implements TargetMirror
  {

    NonFinalTargetMirror (Class <?> target, JDefinedClass asReturn, JDefinedClass abstractType, AbstractJClass asParam)
    {
      this (target,
            asReturn,
            abstractType,
            asParam,
            abstractType.generify ("Contained",
                                   ExpressionsBuildingProcess.referenceWithBounds (target, asReturn.owner ())));
    }

    @Override
    public JDefinedClass mainClass ()
    {
      return abstractType ();
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

    @Override
    public boolean returnFullyGenerified ()
    {
      return true;
    }

  }

}
