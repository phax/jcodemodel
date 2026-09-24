package com.helger.jcodemodel.plugin.generators.expressions;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.StringHelper;
import com.helger.jcodemodel.*;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.plugin.generators.expressions.MirroringClass.FinalTargetMirror;
import com.helger.jcodemodel.plugin.generators.expressions.MirroringClass.GenericMirror;
import com.helger.jcodemodel.plugin.generators.expressions.MirroringClass.NonFinalTargetMirror;
import com.helger.jcodemodel.plugin.generators.expressions.MirroringClass.TargetMirror;

public class ExpressionsBuildingProcess
{

  private static final Logger log = LoggerFactory.getLogger (ExpressionsBuildingProcess.class);

  public final JCodeModel jcm;
  private final JPackage rootPackage;

  // mapping for returned types
  private final HashMap <Class <?>, MirroringClass> resolved = new HashMap <> ();

  // the classes we want to generate a mirror of
  private final Set <Class <?>> targetClasses = new HashSet <> ();

  /// when not null, will be added as each class' header comment.
  private String classHeader = null;

  public ExpressionsBuildingProcess(JCodeModel jcm, String rootPackage) {
    this.jcm = jcm;
    this.rootPackage = jcm._package(rootPackage);
    MirroringClass.stream(jcm).forEach(rs -> resolved.put(rs.target(), rs));
  }

  public void setClassHeader(@Nullable String classHeader) {
    this.classHeader = classHeader;
  }

  protected void addHeader(JDefinedClass jdc) {
    if (!StringHelper.isBlank (classHeader))
      jdc.headerComment ().add (classHeader);
  }

  /// add a new class as a target, create the raw JCM classes. inheritance is only partial, and need
  /// to be complete with addHierarchy after all the targets are added.
  public void addTargetClass (Class <?> targetClass) throws JCodeModelException
  {
    if (targetClass == null || targetClass.isPrimitive ())
    {
      log.warn ("can't build expression for class " + targetClass);
      return;
    }
    if (targetClasses.add (targetClass))
    {
      JPackage pckg = rootPackage.subPackage (targetClass.getPackageName ());
      if ((targetClass.getModifiers () & Modifier.FINAL) > 0)
      {
        // only a concrete class, for both param and return.
        JDefinedClass bothTypes = pckg._class (JMod.PUBLIC | JMod.FINAL, targetClass.getSimpleName () + "Expr");
        JMethod cs = bothTypes.constructor (JMod.PUBLIC);
        JVar param = cs.param (IJExpression.class, "raw");
        cs.body ().add (JInvocation._super ().arg (param));
        copyParams (targetClass, bothTypes);
        resolved.put (targetClass, new FinalTargetMirror (targetClass, bothTypes));
      }
      else
      {
        /// for example, a target HashMap<K, V> would have param and return types :
        /// - `ASubHashMapExpr<K, V, T extends HashMap<K, V>> extends ASubObjectExpression<T>`
        /// - `HashMapExpr<K, V> extends ASubHashMapExpr<K, V, HashMap<K, V>`
        ///
        /// If we also have the Map interface as a target, instead param type :
        /// - `ASubHashMapExpression<K, V, T extends HashMap<K, V>> extends ASubMapExpression<T>`
        /// so the inheritance of the param must be done at a later step, addHierarchy
        ///

        // param type
        JDefinedClass paramType = pckg._class (JMod.PUBLIC | JMod.ABSTRACT,
                                               "ASub" + targetClass.getSimpleName () + "Expr");
        copyParams (targetClass, paramType);

        // return type
        JDefinedClass returnType = pckg._class (JMod.PUBLIC | JMod.FINAL, targetClass.getSimpleName () + "Expr");
        copyParams (targetClass, returnType);
        List <AbstractJClass> narrows = new ArrayList <> ();
        for (JTypeVar jtv : returnType.typeParams ())
        {
          narrows.add (jtv);
        }
        narrows.add (referenceWithBounds (targetClass, jcm));
        returnType._extends (paramType.narrow (narrows));

        // add constructor calling super in both
        for (JDefinedClass jdc : new JDefinedClass [] { returnType, paramType })
        {
          JMethod cs = jdc.constructor (JMod.PUBLIC);
          JVar param = cs.param (IJExpression.class, "raw");
          cs.body ().add (JInvocation._super ().arg (param));
        }
        resolved.put (targetClass, new NonFinalTargetMirror (targetClass, returnType, paramType));
      }
    }
  }

  /// copy each type param of a source class into the created JDC.
  protected void copyParams (Class <?> source, JDefinedClass created)
  {
    for (TypeVariable <?> tv : source.getTypeParameters ())
    {
      if (tv.getBounds ()[0].equals (Object.class))
      {
        created.generify (tv.getName ());
      }
      else
      {
        created.generify (tv.getName (), jcm.ref (tv.getBounds ()[0]));
      }
    }
  }

  public MirroringClass mirroringClass (Class <?> cl)
  {
    return resolved.computeIfAbsent (cl, this::makeMissingMirror);
  }

  // resolve a class that we don't already have resolved : this is not a target, do not create
  // JDefinedClass for it.
  protected MirroringClass makeMissingMirror (Class <?> unresolvedClass)
  {
    if ((unresolvedClass.getModifiers () & Modifier.FINAL) > 0)
    {
      if (unresolvedClass.isArray ())
      {
        return new GenericMirror (unresolvedClass,
                                       jcm.ref (ArrayExpression.class).narrow (unresolvedClass.componentType ()));
      }
      else
      {
        return new GenericMirror (unresolvedClass, jcm.ref (ASubObjectExpression.class).narrow (unresolvedClass));
      }
    }
    else
    {
      JNarrowedClass paramType = jcm.ref (ASubObjectExpression.class)
                                    .narrow (jcm.ref (unresolvedClass).wildcardExtends ());
      JNarrowedClass retType = jcm.ref (ASubObjectExpression.class).narrow (unresolvedClass);
      return new GenericMirror (unresolvedClass, paramType, retType);
    }
  }

  public AbstractJClass mirrorReturn (Type type)
  {
    if (type instanceof Class <?> cl)
    {
      return mirroringClass (cl).asReturn ();
    }
    if (type instanceof GenericArrayType gat)
    {
      if (gat.getGenericComponentType () instanceof Class <?> cl)
      {
        // in that case we can convert the Type to a Class : ArrayType<String> = String[].class
        return mirroringClass (cl.arrayType ()).asReturn ();
      }
      // here we can't, so we create the return type for Object[] and change its generics with the
      // component type
      return mirroringClass (Object [].class).asReturn ().erasure ().narrow (jcm.ref (gat.getGenericComponentType ()));
    }
    if (type instanceof ParameterizedType pt)
    {
      MirroringClass mirroring = mirroringClass ((Class <?>) pt.getRawType ());
      if (mirroring.returnFullyGenerified ())
      {
        return mirroring.asReturn ().erasure ().narrow (jcm.ref (pt));
      }
      else
      {
        // we got a return for the specific class, so we generify only with that class' par
        List <AbstractJClass> narrows = new ArrayList <> ();
        for (Type ata : pt.getActualTypeArguments ())
        {
          narrows.add (jcm.ref (ata));
        }
        return mirroring.asReturn ().erasure ().narrow (narrows);
      }
    }
    if (type instanceof TypeVariable <?> tv)
    {
      // Object return type, but we use the variable type instead of object.
      return mirroringClass (Object.class).asReturn ().erasure ().narrow (jcm.ref (tv));
    }
    throw new IllegalArgumentException ("can't mirror return type " + type + " class " + type.getClass ());
  }

  public AbstractJClass mirrorParam (Type type)
  {
    if (type instanceof Class <?> cl)
    {
      return mirroringClass (cl).asParam ();
    }
    if (type instanceof GenericArrayType gat)
    {
      if (gat.getGenericComponentType () instanceof Class <?> cl)
      {
        return mirroringClass (cl.arrayType ()).asParam ();
      }
      return mirroringClass (Object [].class).asParam ().erasure ().narrow (jcm.ref (gat.getGenericComponentType ()));
    }
    if (type instanceof ParameterizedType pt)
    {
      MirroringClass mirroring = mirroringClass ((Class <?>) pt.getRawType ());
      return mirroring.generifiedParam (pt, jcm);
    }
    if (type instanceof TypeVariable <?> tv)
    {
      // Object return type, but we use the variable type instead of object.
      return mirroringClass (Object.class).asParam ().erasure ().narrow (jcm.ref (tv));
    }
    throw new IllegalArgumentException ("can't mirror return type " + type + " class " + type.getClass ());
  }

  ///
  public void processTargets ()
  {
    // copy in a list to not have processing concurrentmodificationexception
    for (MirroringClass mc : new ArrayList <> (resolved.values ()))
    {
      if (mc instanceof TargetMirror tm)
      {
        addHierarchy (tm);
        addMethods (tm);
      }
    }
  }

  /// add the inheritance between the target's mainClass type and its parent class.
  protected void addHierarchy (TargetMirror tm)
  {
    Class <?> superClass = tm.target ().getSuperclass ();
    if (superClass != null && !superClass.equals (Object.class))
    {
      MirroringClass resolvedParent = mirroringClass (superClass);
      resolvedParent.parentOf (tm);
    }
    else
    {
      tm.mainClass ()._extends (jcm.ref (ASubObjectExpression.class).narrow (tm.superRefParam ()));
    }
  }

  protected void addMethods (TargetMirror tm)
  {
    List <Method> sortedMethods = new ArrayList <> ();
    // only the public instance methods declared by the class, excluding synthetic/brdiges
    for (Method m : tm.target ().getDeclaredMethods ())
    {
      if ((m.getModifiers () & Modifier.STATIC) > 0 ||
        (m.getModifiers () & Modifier.PUBLIC) == 0 ||
        m.isSynthetic () ||
        m.isBridge ()) {
        continue;
      }
      sortedMethods.add (m);
    }
    Collections.sort (sortedMethods,
                      Comparator.comparing (Method::getName)
                                .thenComparingInt (Method::getParameterCount)
                                .thenComparing (Method::toGenericString));
    JDefinedClass methodClass = tm.mainClass ();
    for (Method m : sortedMethods)
    {
      addMethod (methodClass, m);
    }
  }

  protected void addMethod (JDefinedClass methodClass, Method m)
  {
    // method name clashes avoidance :
    // - avoid names already present in Object
    // - if a method already exists with a name and same params size, increment i to use method_i
    //
    // That last part is because the same method with different arguments can lead to the same
    // erasure once mirrored,
    //
    // typically call(MyClass1) and call(MyClass2) will be mirror erased to
    // call(ASubObjectExpression)

    String methName = m.getName ();
    if (OBJECT_METHODS_ARGS.getOrDefault (methName, Set.of ()).contains (m.getParameterCount ())) {
      methName += '_';
    }
    for (int i = 0;; i++)
    {
      String tested = i == 0 ? methName : methName + '_' + i;
      if (methodClass.methods ()
                     .stream ()
                     .filter (jm -> jm.name ().equals (tested) && jm.params ().size () == m.getParameterCount ())
                     .findAny ()
                     .isEmpty ())
      {
        methName = tested;
        break;
      }
    }

    AbstractJClass retType = mirrorReturn (m.getGenericReturnType ());

    JMethod meth = methodClass.method (JMod.PUBLIC, retType, methName);
    for (TypeVariable <Method> tv : m.getTypeParameters ())
    {
      if (tv.getBounds ()[0].equals (Object.class))
      {
        meth.generify (tv.getName ());
      }
      else
      {
        meth.generify (tv.getName (), jcm.ref (tv.getBounds ()[0]));
      }
    }

    JInvocation rawinvoke = JExpr.invokeThis ("raw").invoke ("invoke").arg (m.getName ());
    for (Parameter p : m.getParameters ())
    {
      JVar mirroredParam = meth.param (mirrorParam (p.getParameterizedType ()), p.getName ());
      rawinvoke = rawinvoke.invoke ("arg").arg (mirroredParam);
    }
    JInvocation retnew = retType._new ().arg (rawinvoke);
    if (retType.typeParams ().length > 0 || retType.isParameterized ()) {
      retnew = retType.erasure ().narrowEmpty ()._new ().arg (rawinvoke);
    }
    meth.body ()._return (retnew);
  }

  private static final Map <String, Set <Integer>> OBJECT_METHODS_ARGS = Stream.of (Object.class.getMethods ())
                                                                         .filter (m -> ((m.getModifiers () &Modifier.STATIC) == 0))
                                                                               .collect (Collectors.groupingBy (Method::getName,
                                                                                                                Collectors.mapping (Method::getParameterCount,
                                                                                                                                    Collectors.toSet ())));

  //
  // tools
  //

  public static AbstractJClass referenceWithBounds (Class <?> source, JCodeModel jcm)
  {
    AbstractJClass baseref = jcm.ref (source);

    JNarrowedClass jnc = null;
    for (TypeVariable <?> tv : source.getTypeParameters ())
    {
      jnc = jnc == null ? baseref.narrow (jcm.directClass (tv.getTypeName ()))
                        : jnc.narrow (jcm.directClass (tv.getTypeName ()));
    }
    return jnc == null ? baseref : jnc;
  }

}
