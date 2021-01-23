package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.helger.jcodemodel.AbstractJAnnotationValueOwned.JEnumConstantExpr;
import com.helger.jcodemodel.exceptions.JCodeModelException;

/**
 * copying of a {@link JCodeModel} that allows to translate objects from the original model to this one. The copy is
 * made in depth, so there should be no element in common between the original and the copy, allowing to modify the copy
 * without altering the original.
 *
 * <p>
 * the translation is made through several methods, depending on the clas of the item to translate. Those methods should
 * each either<ul><li> make instanceof to delegate the call, and throw an exception in case no corresponding method
 * matches</li><li>
 * test the item class with getClass() and equality, and throw an exception if no class is corresponding</li><li>or
 * finally
 * assume the class if not inherited, and thus use {@link #ensureClass(Object, Class)} on the object which will throw an
 * exception if that class is later inherited.</li>
 * </p>
 *
 * @author glelouet
 *
 */
public class ModelCopy extends JCodeModel
{

  /**
   * ensure an object is of given class o
   *
   * @param o
   * @param cl
   */
  protected static void ensureClass (Object o, Class <?> cl)
  {
    if (o.getClass () != cl)
      throw new UnsupportedOperationException (
          "bad class, filter using instanceof. Expected " + cl + " got " + o.getClass ());
  }

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
    ensureClass (pack, JPackage.class);
    return translatedPackages.computeIfAbsent (pack, o -> _package (pack.name ()));
  }

  //
  // translate objects. Delegation through the class.
  //

  protected IJObject translate (IJObject obj)
  {
    if (obj instanceof IJDeclaration)
      return translate((IJDeclaration) obj);
    if (obj instanceof IJGenerable)
      return translate ((IJGenerable) obj);
    if (obj instanceof IJStatement)
      return translate ((IJStatement) obj);
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJDeclaration translate (IJDeclaration obj)
  {
    Class <? extends IJDeclaration> cl = obj.getClass ();
    if (cl == JEnumConstant.class)
    {
      JEnumConstant real = (JEnumConstant) obj;
      return new JEnumConstant (translate (real.type ()), real.name ());
    }
    if (cl == JLambdaParam.class)
    {
      JLambdaParam real = (JLambdaParam) obj;
      return new JLambdaParam (translate (real.type ()), real.name ());
    }
    if (cl == JPackage.class)
      return translate ((JPackage) obj);
    if (cl == JReferencedClass.class)
      return translate ((JReferencedClass) obj);

    if (cl == JTypeVar.class)
      return translate ((JTypeVar) obj);

    if (cl == JTypeVarClass.class)
      return translate ((JTypeVarClass) obj);

    if (cl == JVar.class)
      return translate ((JVar) obj);

    if (cl == JFieldVar.class)
      return translate ((JFieldVar) obj);

    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJGenerable translate (IJGenerable obj)
  {
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJStatement translate (IJStatement obj)
  {
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  //
  // translate types
  //

  private HashMap <JDefinedClass, JDefinedClass> translatedJDefinedClass = new HashMap <> ();

  public JDefinedClass translate (JDefinedClass type)
  {
    if (type.getClass () == JAnonymousClass.class)
      return translate ((JAnonymousClass) type);
    ensureClass (type, JDefinedClass.class);
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
    ensureClass (type, JAnonymousClass.class);
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
    ensureClass (type, JReferencedClass.class);
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
    ensureClass (type, JDirectClass.class);
    return new JDirectClass (this, translate (type.getOuter ()), type.getClassType (), type.fullName ());
  }

  public JArrayClass translate (JArrayClass type)
  {
    ensureClass (type, JArrayClass.class);
    return new JArrayClass (this, translate (type.elementType ()));
  }

  public JErrorClass translate (JErrorClass type)
  {
    ensureClass (type, JErrorClass.class);
    return new JErrorClass (this, type.getMessage ());
  }

  public JNarrowedClass translate (JNarrowedClass type)
  {
    ensureClass (type, JNarrowedClass.class);
    List <AbstractJClass> args = new ArrayList <> ();
    for (AbstractJClass arg : type.getTypeParameters ())
      args.add (translate(arg));
    return new JNarrowedClass (translate (type.erasure ()), args);
  }

  public JNullType translate (JNullType type)
  {
    ensureClass (type, JNullType.class);
    return new JNullType (this);
  }

  public JTypeVar translate (JTypeVar type)
  {
    ensureClass (type, JTypeVar.class);
    return new JTypeVar (this, type.name ());
  }

  public JTypeVarClass translate (JTypeVarClass type)
  {
    ensureClass (type, JTypeVarClass.class);
    return new JTypeVarClass (translate (type.getRefClass ()));
  }

  public JTypeWildcard translate (JTypeWildcard type)
  {
    ensureClass (type, JTypeWildcard.class);
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
    if (cl == JEnumConstant.class)
    {
      JEnumConstant real = (JEnumConstant) expr;
      return new JEnumConstant (translate (real.type ()), real.name ());
    }
    if (cl == JEnumConstantExpr.class) {
      JEnumConstantExpr real = (JEnumConstantExpr) expr;
      real.getEnumConstant (); // TODO remove
      // TODO how to handle ? it requires an enclosing object.
      // return new JEnumConstantExpr (real.getEnumConstant ());
    }
    if (cl == JEnumConstantRef.class)
    {
      JEnumConstantRef real = (JEnumConstantRef) expr;
      return new JEnumConstantRef (translate (real.type ()), real.name ());
    }
    if (cl == JLambda.class)
    {
      JLambda real = (JLambda) expr;
      JLambda ret = new JLambda ();
      for (JLambdaParam param : real.params ())
        ret.addParam (translate(param.type ()), param.name ());
      copyBlock (real.body (), ret.body ());
      return ret;
    }

    // TODO
    throw new UnsupportedOperationException ("not done " + cl.getClass ());
  }

  protected void copyBlock (JBlock source, JBlock copy)
  {
    copy.bracesRequired (source.bracesRequired ());
    copy.indentRequired (source.indentRequired ());
    copy.pos (source.pos ());
    copy.virtual (source.virtual ());
    for (IJObject obj : source.contentsMutable ())
      copy.contentsMutable ().add (translate(obj));
  }

  protected JVar translate (JVar var)
  {
    ensureClass (var, JVar.class);
    JVar ret = new JVar (var.mods (), translate (var.type ()), var.name (), translate (var.init ()));
    copyVar (var, ret);
    return ret;
  }

  protected void copyVar (JVar var, JVar ret)
  {
    for (JAnnotationUse annot : var.getAnnotations ())
    {
      JAnnotationUse retAnnot = ret.annotate (translate (annot.getAnnotationClass ()));
      copyAnnot(annot, retAnnot);
    }
  }

  protected void copyAnnot(JAnnotationUse use,JAnnotationUse ret ) {
    for (Entry <String, AbstractJAnnotationValue> e : use.getAnnotationMembers ().entrySet ())
      ret.annotationMembersMutable ().put (e.getKey (), translate(e.getValue ()));
  }

  protected JFieldVar translate (JFieldVar var)
  {
    ensureClass (var, JFieldVar.class);
    JFieldVar ret = new JFieldVar (translate (var.owner ()), var.mods (), translate (var.type ()), var.name (),
        translate (var.init ()));
    copyJavadoc (var.javadoc (), ret.javadoc ());
    return ret;
  }

  protected void copyJavadoc (JDocComment javadoc, JDocComment javadoc2)
  {
    // TODO
    throw new UnsupportedOperationException ("todo");
  }

  protected AbstractJAnnotationValue translate (AbstractJAnnotationValue value)
  {
    Class <? extends AbstractJAnnotationValue> cl = value.getClass ();
    if (cl == JAnnotationArrayMember.class)
    {
      JAnnotationArrayMember real = (JAnnotationArrayMember) value;
      JAnnotationArrayMember ret = new JAnnotationArrayMember (this);
      for (AbstractJAnnotationValue ajv : real.annotationsMutable ())
        ret.annotationsMutable ().add (translate (ajv));
      return ret;
    }
    if (cl == JAnnotationUse.class)
    {
      JAnnotationUse real = (JAnnotationUse) value;
      return real;
    }
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

}
