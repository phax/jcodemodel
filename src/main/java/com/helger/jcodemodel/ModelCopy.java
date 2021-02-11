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
@SuppressWarnings("serial")
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

  //
  // translate objects. Delegation through the class. The intefaces are doing delegation with instanceof or throw.
  // The classes start with he highest one implements IJObject ; non-abstract classes start with a class()== , then do
  // the same as abstract and interfaces (series of instanceof).
  //

  //
  // translate interfaces graph
  //

  protected IJObject translate (IJObject obj)
  {
    if (obj instanceof IJDeclaration)
      return translate ((IJDeclaration) obj);
    if (obj instanceof IJGenerable)
      return translate ((IJGenerable) obj);
    if (obj instanceof IJStatement)
      return translate ((IJStatement) obj);
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJDeclaration translate (IJDeclaration obj)
  {
    if (obj instanceof JEnumConstant)
      return translate ((JEnumConstant) obj);
    if (obj instanceof JLambdaParam)
      return translate ((JLambdaParam) obj);
    if (obj instanceof JPackage)
      return translate ((JPackage) obj);
    if (obj instanceof JReferencedClass)
      return translate ((JReferencedClass) obj);
    if (obj instanceof JTypeVar)
      return translate ((JTypeVar) obj);
    if (obj instanceof JVar)
      return translate ((JVar) obj);
    if (obj instanceof IJGenerifiable)
      return translate ((IJGenerifiable) obj);
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJGenerifiable translate (IJGenerifiable obj)
  {
    if (obj instanceof AbstractJGenerifiableImpl)
      return translate ((AbstractJGenerifiableImpl) obj);
    if (obj instanceof JDefinedClass)
      return translate ((JDefinedClass) obj);
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJGenerable translate (IJGenerable obj)
  {
    if (obj instanceof AbstractJAnnotationValue)
      return translate ((AbstractJAnnotationValue) obj);
    if (obj instanceof AbstractJType)
      return translate ((AbstractJType) obj);
    if (obj instanceof JBlock)
      return translate ((JBlock) obj);
    if (obj instanceof JCatchBlock)
      return translate ((JCatchBlock) obj);
    if (obj instanceof JDocComment)
      return translate ((JDocComment) obj);
    if (obj instanceof JMods)
      return translate ((JMods) obj);
    if (obj instanceof JPackage)
      return translate ((JPackage) obj);
    if (obj instanceof JTryResource)
      return translate ((JTryResource) obj);
    if (obj instanceof IJExpression)
      return translate ((IJExpression) obj);
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  protected IJStatement translate (IJStatement obj)
  {
    if (obj instanceof JBlock)
      return translate ((JBlock) obj);
    if (obj instanceof JBreak)
      return translate ((JBreak) obj);
    if (obj instanceof JCase)
      return translate ((JCase) obj);
    if (obj instanceof JConditional)
      return translate ((JConditional) obj);
    if (obj instanceof JContinue)
      return translate ((JContinue) obj);
    if (obj instanceof JDirectStatement)
      return translate ((JDirectStatement) obj);
    if (obj instanceof JDoLoop)
      return translate ((JDoLoop) obj);
    if (obj instanceof JForEach)
      return translate ((JForEach) obj);
    if (obj instanceof JForLoop)
      return translate ((JForLoop) obj);
    if (obj instanceof JLabel)
      return translate ((JLabel) obj);
    if (obj instanceof JReturn)
      return translate ((JReturn) obj);
    if (obj instanceof JSingleLineCommentStatement)
      return translate ((JSingleLineCommentStatement) obj);
    if (obj instanceof JSwitch)
      return translate ((JSwitch) obj);
    if (obj instanceof JSynchronizedBlock)
      return translate ((JSynchronizedBlock) obj);
    if (obj instanceof JThrow)
      return translate ((JThrow) obj);
    if (obj instanceof JTryBlock)
      return translate ((JTryBlock) obj);
    if (obj instanceof JWhileLoop)
      return translate ((JWhileLoop) obj);
    if (obj instanceof IJExpressionStatement)
      return translate ((IJExpressionStatement) obj);
    throw new UnsupportedOperationException ("not done " + obj.getClass ());
  }

  //
  // translate classes
  //

  //
  // AbstractJType tree
  //

  public AbstractJType translate (AbstractJType type)
  {
    if (type instanceof AbstractJClass)
      return translate ((AbstractJClass) type);
    if (type instanceof JPrimitiveType)
      return translate ((JPrimitiveType) type);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  public AbstractJClass translate (AbstractJClass type)
  {
    if (type instanceof AbstractJClassContainer <?>)
      return translate ((AbstractJClassContainer <?>) type);
    if (type instanceof JArrayClass)
      return translate ((JArrayClass) type);
    if (type instanceof JErrorClass)
      return translate ((JErrorClass) type);
    if (type instanceof JNarrowedClass)
      return translate ((JNarrowedClass) type);
    if (type instanceof JNullType)
      return translate ((JNullType) type);
    if (type instanceof JReferencedClass)
      return translate ((JReferencedClass) type);
    if (type instanceof JTypeVar)
      return translate ((JTypeVar) type);
    if (type instanceof JTypeWildcard)
      return translate ((JTypeWildcard) type);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  public AbstractJClassContainer <?> translate (AbstractJClassContainer <?> type)
  {
    if (type instanceof JDefinedClass)
      return translate ((JDefinedClass) type);
    if (type instanceof JDirectClass)
      return translate ((JDirectClass) type);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  private HashMap <JDefinedClass, JDefinedClass> translatedJDefinedClass = new HashMap <> ();

  public JDefinedClass translate (JDefinedClass type)
  {
    if (type instanceof JAnonymousClass)
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
    return ret;
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

  public JDirectClass translate (JDirectClass type)
  {
    ensureClass (type, JDirectClass.class);
    return new JDirectClass (this, translate (type.getOuter ()), type.getClassType (), type.fullName ());
  }

  // end AbstractJClassContainer

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
      args.add (translate (arg));
    return new JNarrowedClass (translate (type.erasure ()), args);
  }

  public JNullType translate (JNullType type)
  {
    ensureClass (type, JNullType.class);
    return new JNullType (this);
  }

  public JReferencedClass translate (JReferencedClass type)
  {
    ensureClass (type, JReferencedClass.class);
    return new JReferencedClass (this, type.getReferencedClass ());
  }

  public JTypeVar translate (JTypeVar type)
  {
    if (type instanceof JTypeVarClass)
      return translate ((JTypeVarClass) type);
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

  public JPrimitiveType translate (JPrimitiveType type)
  {
    ensureClass (type, JPrimitiveType.class);
    return new JPrimitiveType (this, type.fullName (), ((JReferencedClass) type.boxify ()).getReferencedClass (),
        type.useValueOf ());
  }

  //
  // IJclassContainer
  //

  private IJClassContainer <?> translate (IJClassContainer <?> type)
  {
    if (type instanceof AbstractJClassContainer <?>)
      return translate ((AbstractJClassContainer <?>) type);
    if (type instanceof JPackage)
      return translate ((JPackage) type);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  //
  // IJDeclaration subclasses that have not been translated before
  //

  private JEnumConstant translate (JEnumConstant type)
  {
    ensureClass (type, JEnumConstant.class);
    return new JEnumConstant (translate (type.type ()), type.name ());
  }

  private JLambdaParam translate (JLambdaParam type)
  {
    ensureClass (type, JLambdaParam.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  private HashMap <JPackage, JPackage> translatedPackages = new HashMap <> ();

  public JPackage translate (JPackage pack)
  {
    ensureClass (pack, JPackage.class);
    return translatedPackages.computeIfAbsent (pack, o -> _package (pack.name ()));
  }

  protected JVar translate (JVar var)
  {
    if (var instanceof JFieldVar)
      return translate ((JFieldVar) var);
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
      copyAnnot (annot, retAnnot);
    }
  }

  protected void copyAnnot (JAnnotationUse use, JAnnotationUse ret)
  {
    for (Entry <String, AbstractJAnnotationValue> e : use.getAnnotationMembers ().entrySet ())
      ret.annotationMembersMutable ().put (e.getKey (), translate (e.getValue ()));
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

  protected AbstractJGenerifiableImpl translate (AbstractJGenerifiableImpl type)
  {
    if (type instanceof JMethod)
      return translate ((JMethod) type);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  protected JMethod translate (JMethod type)
  {
    ensureClass (type, JMethod.class);
    throw new UnsupportedOperationException ("not done " + type.getClass ());
  }

  //
  // IGenerable subclasses that have not been translated before
  //

  protected AbstractJAnnotationValue translate (AbstractJAnnotationValue value)
  {
    if (value instanceof AbstractJAnnotationValueOwned)
      return translate ((AbstractJAnnotationValueOwned) value);
    if (value instanceof JAnnotationStringValue)
      return translate ((JAnnotationStringValue) value);
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

  protected AbstractJAnnotationValueOwned translate (AbstractJAnnotationValueOwned value)
  {
    if (value instanceof JAnnotationArrayMember)
      return translate ((JAnnotationArrayMember) value);
    if (value instanceof JAnnotationUse)
      return translate ((JAnnotationUse) value);
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

  protected JAnnotationArrayMember translate (JAnnotationArrayMember value)
  {
    ensureClass (value, JAnnotationArrayMember.class);
    JAnnotationArrayMember ret = new JAnnotationArrayMember (this);
    for (AbstractJAnnotationValue ajv : value.annotationsMutable ())
      ret.annotationsMutable ().add (translate (ajv));
    return ret;
  }

  protected JAnnotationUse translate (JAnnotationUse value)
  {
    ensureClass (value, JAnnotationUse.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

  protected JAnnotationStringValue translate (JAnnotationStringValue value)
  {
    ensureClass (value, JAnnotationStringValue.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

  protected JBlock translate (JBlock block)
  {
    if (block instanceof JLambdaBlock)
      return translate ((JLambdaBlock) block);
    ensureClass (block, JBlock.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected JLambdaBlock translate (JLambdaBlock block)
  {
    ensureClass (block, JBlock.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected JCatchBlock translate (JCatchBlock block)
  {
    ensureClass (block, JCatchBlock.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected JDocComment translate (JDocComment comm)
  {
    ensureClass (comm, JDocComment.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + comm.getClass ());
  }

  protected JMods translate (JMods mods)
  {
    ensureClass (mods, JMods.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + mods.getClass ());
  }

  protected JTryResource translate (JTryResource block)
  {
    ensureClass (block, JTryResource.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected IJExpression translate (IJExpression expr)
  {
    if (expr instanceof JArray)
      return translate ((JArray) expr);
    if (expr instanceof JAtom)
      return translate ((JAtom) expr);
    if (expr instanceof JAtomDouble)
      return translate ((JAtomDouble) expr);
    if (expr instanceof JAtomFloat)
      return translate ((JAtomFloat) expr);
    if (expr instanceof JAtomInt)
      return translate ((JAtomInt) expr);
    if (expr instanceof JAtomLong)
      return translate ((JAtomLong) expr);
    if (expr instanceof JCast)
      return translate ((JCast) expr);
    if (expr instanceof JEnumConstant)
      return translate ((JEnumConstant) expr);
    if (expr instanceof JEnumConstantExpr)
      return translate ((JEnumConstantExpr) expr);
    if (expr instanceof JEnumConstantRef)
      return translate ((JEnumConstantRef) expr);
    if (expr instanceof JLambda)
      return translate ((JLambda) expr);
    if (expr instanceof JLambdaMethodRef)
      return translate ((JLambdaMethodRef) expr);
    if (expr instanceof JOpBinary)
      return translate ((JOpBinary) expr);
    if (expr instanceof JOpTernary)
      return translate ((JOpTernary) expr);
    if (expr instanceof JOpUnary)
      return translate ((JOpUnary) expr);
    if (expr instanceof JStringLiteral)
      return translate ((JStringLiteral) expr);
    if (expr instanceof IJAssignmentTarget)
      return translate ((IJAssignmentTarget) expr);
    if (expr instanceof IJExpressionStatement)
      return translate ((IJExpressionStatement) expr);
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JArray translate (JArray expr)
  {
    ensureClass (expr, JArray.class);
    return JExpr.newArray (translate (expr.type ()), translate (expr.size ()));
  }

  protected JAtom translate (JAtom expr)
  {
    ensureClass (expr, JAtom.class);
    return expr;
  }

  protected JAtomDouble translate (JAtomDouble expr)
  {
    ensureClass (expr, JAtomDouble.class);
    return expr;
  }

  protected JAtomFloat translate (JAtomFloat expr)
  {
    ensureClass (expr, JAtomFloat.class);
    return expr;
  }

  protected JAtomInt translate (JAtomInt expr)
  {
    ensureClass (expr, JAtomInt.class);
    return expr;
  }

  protected JAtomLong translate (JAtomLong expr)
  {
    ensureClass (expr, JAtomLong.class);
    return expr;
  }

  protected JCast translate (JCast expr)
  {
    ensureClass (expr, JCast.class);
    return new JCast (translate (expr.type ()), translate (expr.object ()));
  }

  protected JEnumConstantExpr translate (JEnumConstantExpr expr)
  {
    ensureClass (expr, JEnumConstantExpr.class);
    // TODO how to handle ? it requires an enclosing object.
    // return new JEnumConstantExpr (expr.getEnumConstant ());
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JEnumConstantRef translate (JEnumConstantRef expr)
  {
    ensureClass (expr, JEnumConstantRef.class);
    return new JEnumConstantRef (translate (expr.type ()), expr.name ());
  }

  protected JLambda translate (JLambda expr)
  {
    ensureClass (expr, JLambda.class);
    JLambda ret = new JLambda ();
    for (JLambdaParam param : expr.params ())
      ret.addParam (translate (param.type ()), param.name ());
    copyBlock (expr.body (), ret.body ());
    return ret;
  }

  protected void copyBlock (JBlock source, JBlock copy)
  {
    copy.bracesRequired (source.bracesRequired ());
    copy.indentRequired (source.indentRequired ());
    copy.pos (source.pos ());
    copy.virtual (source.virtual ());
    for (IJObject obj : source.contentsMutable ())
      copy.contentsMutable ().add (translate (obj));
  }

  protected JLambdaMethodRef translate (JLambdaMethodRef expr)
  {
    ensureClass (expr, JLambdaMethodRef.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpBinary translate (JOpBinary expr)
  {
    ensureClass (expr, JOpBinary.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpTernary translate (JOpTernary expr)
  {
    ensureClass (expr, JOpTernary.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpUnary translate (JOpUnary expr)
  {
    if (expr instanceof JOpUnaryTight)
      return translate ((JOpUnaryTight) expr);
    ensureClass (expr, JOpUnary.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpUnaryTight translate (JOpUnaryTight expr)
  {
    ensureClass (expr, JOpUnaryTight.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JStringLiteral translate (JStringLiteral expr)
  {
    ensureClass (expr, JStringLiteral.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected IJAssignmentTarget translate (IJAssignmentTarget expr)
  {
    if (expr instanceof JArrayCompRef)
      return translate ((JArrayCompRef) expr);
    if (expr instanceof JFieldRef)
      return translate ((JFieldRef) expr);
    if (expr instanceof JLambdaParam)
      return translate ((JLambdaParam) expr);
    if (expr instanceof JVar)
      return translate ((JVar) expr);
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JArrayCompRef translate (JArrayCompRef expr)
  {
    ensureClass (expr, JArrayCompRef.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JFieldRef translate (JFieldRef expr)
  {
    ensureClass (expr, JFieldRef.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected IJExpressionStatement translate (IJExpressionStatement expr)
  {
    if (expr instanceof JAssignment)
      return translate ((JAssignment) expr);
    if (expr instanceof JInvocation)
      return translate ((JInvocation) expr);
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JAssignment translate (JAssignment expr)
  {
    ensureClass (expr, JAssignment.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JInvocation translate (JInvocation expr)
  {
    ensureClass (expr, JInvocation.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  //
  // IJStatement subclasses that have not been translated before
  //

  // JBlock already done

  protected JBreak translate (JBreak stt)
  {
    ensureClass (stt, JBreak.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JCase translate (JCase stt)
  {
    ensureClass (stt, JCase.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JConditional translate (JConditional stt)
  {
    ensureClass (stt, JConditional.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JContinue translate (JContinue stt)
  {
    ensureClass (stt, JBreak.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JDirectStatement translate (JDirectStatement stt)
  {
    ensureClass (stt, JDirectStatement.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JDoLoop translate (JDoLoop stt)
  {
    ensureClass (stt, JDoLoop.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JForEach translate (JForEach stt)
  {
    ensureClass (stt, JForEach.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JForLoop translate (JForLoop stt)
  {
    ensureClass (stt, JForLoop.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JLabel translate (JLabel stt)
  {
    ensureClass (stt, JLabel.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JReturn translate (JReturn stt)
  {
    ensureClass (stt, JReturn.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JSingleLineCommentStatement translate (JSingleLineCommentStatement stt)
  {
    ensureClass (stt, JSingleLineCommentStatement.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JSwitch translate (JSwitch stt)
  {
    ensureClass (stt, JSwitch.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JSynchronizedBlock translate (JSynchronizedBlock stt)
  {
    ensureClass (stt, JSynchronizedBlock.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JThrow translate (JThrow stt)
  {
    ensureClass (stt, JThrow.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JTryBlock translate (JTryBlock stt)
  {
    ensureClass (stt, JTryBlock.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JWhileLoop translate (JWhileLoop stt)
  {
    ensureClass (stt, JWhileLoop.class);
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  // IJExpressionStatement already done

}
