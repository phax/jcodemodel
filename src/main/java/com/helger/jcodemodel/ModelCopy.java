package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.helger.jcodemodel.exceptions.JCodeModelException;

public class ModelCopy extends JCodeModel
{

  private final JCodeModel from;

  public ModelCopy (JCodeModel from)
  {
    this.from = from;
    // also translates classes
    translatePackages ();
    // TODO translate resources
  }

  private void translatePackages ()
  {
    for (JPackage pack : from.getAllPackages ())
    {
      translate (pack);
      for (JDefinedClass clas : pack.classes ())
        translate (clas);
    }
  }


  private HashMap <JPackage, JPackage> translatedPackages = new HashMap <> ();

  public JPackage translate (JPackage pack)
  {
    return translatedPackages.computeIfAbsent (pack, o -> _package (pack.name ()));
  }

  //
  // translate types
  //

  private HashMap <JDefinedClass, JDefinedClass> translatedJDefinedClass = new HashMap <> ();

  public JDefinedClass translate (JDefinedClass type)
  {
    JDefinedClass ret = translatedJDefinedClass.get (type);
    if (ret == null)
    {
      try
      {
        ret = _class (type.mods ().getValue (), type.fullName (), type.getClassType ());
      }
      catch (JCodeModelException e1)
      {
        throw new UnsupportedOperationException ("catch this", e1);
      }
      // put it in the map before building it, for recursive calls.
      translatedJDefinedClass.put (type, ret);
      copyClass (type, ret);
    }
    return type;
  }

  protected void copyClass (JDefinedClass type, JDefinedClass ret)
  {
    for (Entry <String, JFieldVar> e : type.fields ().entrySet ())
    {
      JFieldVar f = e.getValue ();
      ret.field (f.mods ().getValue (), f.type (), e.getKey (), translate (f.init ()));
    }
    // TODO what else ?
  }

  public JAnonymousClass translate (JAnonymousClass type)
  {
    JAnonymousClass ret = new JAnonymousClass (translate (type.base ()));
    copyClass (type, ret);
    return ret;
  }

  public AbstractJType translate (AbstractJType type)
  {
    if (type.getClass () == JPrimitiveType.class)
    {
      JPrimitiveType real = (JPrimitiveType) type;
      return new JPrimitiveType (this, real.fullName (), ((JReferencedClass) real.boxify ()).getReferencedClass (),
          real.useValueOf ());
    }
    if (type instanceof AbstractJClass)
      return translate ((AbstractJClass) type);

    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  public JReferencedClass translate (JReferencedClass type)
  {
    return new JReferencedClass (this, type.getReferencedClass ());
  }

  public AbstractJClass translate (AbstractJClass type)
  {
    if (type.getClass () == JDefinedClass.class)
      return translate ((JDefinedClass) type);

    if (type.getClass () == JAnonymousClass.class)
      return translate ((JAnonymousClass) type);

    if (type.getClass () == JDirectClass.class)
      return translate((JDirectClass) type);

    if (type.getClass () == JArrayClass.class)
      return translate((JArrayClass) type);

    if (type.getClass () == JErrorClass.class)
      return translate ((JErrorClass) type);

    if (type.getClass () == JNarrowedClass.class)
      return translate ((JNarrowedClass) type);

    if (type.getClass () == JNullType.class)
      return translate ((JNullType) type);

    if (type.getClass () == JReferencedClass.class)
      return translate ((JReferencedClass) type);

    if (type.getClass () == JTypeVar.class)
      return translate ((JTypeVar) type);

    if (type.getClass () == JTypeVarClass.class)
      return translate ((JTypeVarClass) type);

    if (type instanceof JTypeWildcard)
      return translate ((JTypeWildcard) type);

    throw new UnsupportedOperationException ("not done " + type.getClass ());

  }

  public JDirectClass translate (JDirectClass type)
  {
    return new JDirectClass (this, translate (type.getOuter ()), type.getClassType (), type.fullName ());
  }

  public JArrayClass translate (JArrayClass type)
  {
    return new JArrayClass (this, translate (type.elementType ()));
  }

  public JErrorClass translate (JErrorClass type)
  {
    return new JErrorClass (this, type.getMessage ());
  }

  public JNarrowedClass translate (JNarrowedClass type)
  {
    List <AbstractJClass> args = new ArrayList <> ();
    for (AbstractJClass arg : type.getTypeParameters ())
      args.add (translate(arg));
    return new JNarrowedClass (translate (type.erasure ()), args);
  }

  public JNullType translate (JNullType type)
  {
    return new JNullType (this);
  }

  public JTypeVar translate (JTypeVar type)
  {
    return new JTypeVar (this, type.name ());
  }

  public JTypeVarClass translate (JTypeVarClass type)
  {
    return new JTypeVarClass (translate (type.getRefClass ()));
  }

  public JTypeWildcard translate (JTypeWildcard type)
  {
    return new JTypeWildcard (translate (type.bound ()), type.boundMode ());
  }

  private IJClassContainer <?> translate (IJClassContainer <?> type)
  {
    if (type instanceof JPackage)
      return translate ((JPackage) type);
    if (type instanceof JDefinedClass)
      return translate ((JDefinedClass) type);
    if (type instanceof JDirectClass)
      return translate ((JDirectClass) type);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  //
  // translate expressions
  //

  protected IJExpression translate (IJExpression expr)
  {
    Class <? extends IJExpression> cl = expr.getClass ();
    if (cl == JArray.class)
    {
      JArray arr = (JArray) expr;
      return JExpr.newArray (arr.type (), arr.size ());
    }
    if (cl == JAtom.class)
    {
      JAtom atom = (JAtom) expr;
      return new JAtom (atom.what ());
    }
    if (cl == JAtomDouble.class)
    {
      JAtomDouble atom = (JAtomDouble) expr;
      return new JAtomDouble (atom.what ());
    }
    if (cl == JAtomFloat.class)
    {
      JAtomFloat atom = (JAtomFloat) expr;
      return new JAtomFloat (atom.what ());
    }
    if (cl == JAtomInt.class)
    {
      JAtomInt atom = (JAtomInt) expr;
      return new JAtomInt (atom.what ());
    }
    if (cl == JAtomLong.class)
    {
      JAtomLong atom = (JAtomLong) expr;
      return new JAtomLong (atom.what ());
    }
    if (cl == JCast.class)
    {
      JCast real = (JCast) expr;
      return new JCast (translate (real.type ()), translate (real.object ()));
    }
    // TODO
    throw new UnsupportedOperationException ("not done " + cl.getClass ());
  }

}
