package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
 * assume the class if not inherited, and thus use {@link #
 * return cacheCopy (type, JTypeVarClass.class, src -> new JTypeVarClass (translate (type.getRefClass ())), null);
 * ensureClass(Object, Class)} on the object which will throw an
 * exception if that class is later inherited.</li>
 * </p>
 *
 * @author glelouet
 *
 */
@SuppressWarnings("serial")
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

  //
  // translate objects. Delegation through the class. The intefaces are doing delegation with instanceof or throw.
  // The classes start with he highest one implements IJObject ; non-abstract classes start with a class()== , then do
  // the same as abstract and interfaces (series of instanceof). Class that have effective code should test the
  // existence of the object in the cache first.
  //

  // caching

  /**
   * internal memory of the source model items we translated, into their copy.
   */
  private HashMap <IJObject, IJObject> translatedCache = new HashMap <> ();

  /**
   * if an {@link IJObject} is not copied yet, copies and store the source-copy link.
   *
   * @param <T>
   *        exact type we produce
   * @param source
   *        the item we need to copy in this.
   * @param expectedClass
   *        the exact class we want.
   * @param creator
   *        function to create a shallow copy of the object. This method should only call a constructor, not deep copy
   *        the source, to avoid recursive call to this method. Therefore the only restriction, is that this method does
   *        not try to translate, directly or not, the same class we re using for T.
   * @param deepCopy
   *        function to actually deep copy the source into the target. Since this method is called after storing the
   *        target in the cache, this avoids having recursive calls. Can be set to null if there is no deep copy
   *        required.
   * @return a new fully copied item.
   */
  @SuppressWarnings("unchecked")
  protected <T extends IJObject> T cacheCopy (
      T source,
      Class <T> expectedClass,
      Function <T, T> creator,
      BiConsumer <T, T> deepCopy)
  {
    return (T) translatedCache.computeIfAbsent (source, o ->
    {
      if (o.getClass () != expectedClass)
        throw new UnsupportedOperationException (
            "bad class, filter using instanceof. Expected " + expectedClass + " got " + o.getClass ());
      T ret = creator.apply (source);
      translatedCache.put (o, ret);
      if (deepCopy != null)
        deepCopy.accept (source, ret);
      return ret;
    });
  }

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

  public JDefinedClass translate (JDefinedClass type)
  {
    if (type instanceof JAnonymousClass)
      return translate ((JAnonymousClass) type);
    return cacheCopy (type, JDefinedClass.class, cl ->
    {
      try
      {
        return _class (type.mods ().getValue (), type.fullName (), type.getClassType ());
      }
      catch (JCodeModelException e)
      {
        throw new UnsupportedOperationException ("catch this", e);
      }
    }, this::copyClass);
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
    return cacheCopy (type, JAnonymousClass.class, src -> new JAnonymousClass (translate (type.base ())),
        this::copyClass);
  }

  public JDirectClass translate (JDirectClass type)
  {
    return cacheCopy (type, JDirectClass.class,
        src -> new JDirectClass (this, translate (type.getOuter ()), type.getClassType (), type.fullName ()), null);
  }

  // end AbstractJClassContainer

  public JArrayClass translate (JArrayClass type)
  {
    return cacheCopy (type, JArrayClass.class, src -> new JArrayClass (this, translate (type.elementType ())), null);
  }

  public JErrorClass translate (JErrorClass type)
  {
    return cacheCopy (type, JErrorClass.class, src -> new JErrorClass (this, type.getMessage ()), null);
  }

  public JNarrowedClass translate (JNarrowedClass type)
  {
    List <AbstractJClass> args = new ArrayList <> ();
    for (AbstractJClass arg : type.getTypeParameters ())
      args.add (translate (arg));
    return cacheCopy (type, JNarrowedClass.class, src -> new JNarrowedClass (translate (type.erasure ()), args), null);
  }

  public JNullType translate (JNullType type)
  {
    return cacheCopy (type, JNullType.class, src -> new JNullType (this), null);
  }

  public JReferencedClass translate (JReferencedClass type)
  {
    return cacheCopy (type, JReferencedClass.class, src -> new JReferencedClass (this, type.getReferencedClass ()),
        null);
  }

  public JTypeVar translate (JTypeVar type)
  {
    if (type instanceof JTypeVarClass)
      return translate ((JTypeVarClass) type);
    return cacheCopy (type, JTypeVar.class, src -> new JTypeVar (this, type.name ()), null);
  }

  public JTypeVarClass translate (JTypeVarClass type)
  {
    return cacheCopy (type, JTypeVarClass.class, src -> new JTypeVarClass (translate (type.getRefClass ())), null);
  }

  public JTypeWildcard translate (JTypeWildcard type)
  {
    return cacheCopy (type, JTypeWildcard.class,
        src -> new JTypeWildcard (translate (type.bound ()), type.boundMode ()), null);
  }

  public JPrimitiveType translate (JPrimitiveType type)
  {
    return cacheCopy (type, JPrimitiveType.class, src -> new JPrimitiveType (this, type.fullName (),
        ((JReferencedClass) type.boxify ()).getReferencedClass (), type.useValueOf ()), null);
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
    return cacheCopy (type, JEnumConstant.class, src -> new JEnumConstant (translate (type.type ()), type.name ()),
        null);
  }

  private JLambdaParam translate (JLambdaParam type)
  {
    return cacheCopy (type, JLambdaParam.class, src -> new JLambdaParam (translate (type.type ()), type.name ()), null);
  }

  public JPackage translate (JPackage pack)
  {
    return cacheCopy (pack, JPackage.class, src -> _package (pack.name ()), null);
  }

  protected JVar translate (JVar var)
  {
    if (var instanceof JFieldVar)
      return translate ((JFieldVar) var);
    return cacheCopy (var, JVar.class,
        src -> new JVar (var.mods (), translate (var.type ()), var.name (), translate (var.init ())), this::copyVar);
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
    return cacheCopy (var, JFieldVar.class, src -> new JFieldVar (translate (var.owner ()), var.mods (), translate (var.type ()), var.name (),
        translate (var.init ())), this::copyFieldVar);
  }

  protected void copyFieldVar (JFieldVar source, JFieldVar copy)
  {
    copyJavadoc (source.javadoc (), copy.javadoc ());
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
    // TODO
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
    return cacheCopy (value, JAnnotationArrayMember.class, src -> new JAnnotationArrayMember (this),
        this::copyJAnnotationArrayMember);
  }

  protected void copyJAnnotationArrayMember (JAnnotationArrayMember s, JAnnotationArrayMember c)
  {
    for (AbstractJAnnotationValue ajv : s.annotationsMutable ())
      c.annotationsMutable ().add (translate (ajv));
  }

  protected JAnnotationUse translate (JAnnotationUse value)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

  protected JAnnotationStringValue translate (JAnnotationStringValue value)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + value.getClass ());
  }

  protected JBlock translate (JBlock block)
  {
    if (block instanceof JLambdaBlock)
      return translate ((JLambdaBlock) block);

    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected JLambdaBlock translate (JLambdaBlock block)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected JCatchBlock translate (JCatchBlock block)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + block.getClass ());
  }

  protected JDocComment translate (JDocComment comm)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + comm.getClass ());
  }

  protected JMods translate (JMods mods)
  {
    return cacheCopy (mods, JMods.class, src -> new JMods (mods.getValue ()), null);
  }

  protected JTryResource translate (JTryResource block)
  {
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
    return cacheCopy (expr, JArray.class, src -> JExpr.newArray (translate (expr.type ()), translate (expr.size ())),
        null);
  }

  protected JAtom translate (JAtom expr)
  {
    return cacheCopy (expr, JAtom.class, src -> new JAtom (expr.what ()), null);
  }

  protected JAtomDouble translate (JAtomDouble expr)
  {
    return cacheCopy (expr, JAtomDouble.class, src -> new JAtomDouble (expr.what ()), null);
  }

  protected JAtomFloat translate (JAtomFloat expr)
  {
    return cacheCopy (expr, JAtomFloat.class, src -> new JAtomFloat (expr.what ()), null);
  }

  protected JAtomInt translate (JAtomInt expr)
  {
    return cacheCopy (expr, JAtomInt.class, src -> new JAtomInt (expr.what ()), null);
  }

  protected JAtomLong translate (JAtomLong expr)
  {
    return cacheCopy (expr, JAtomLong.class, src -> new JAtomLong (expr.what ()), null);
  }

  protected JCast translate (JCast expr)
  {
    return cacheCopy (expr, JCast.class, src -> new JCast (translate (expr.type ()), translate (expr.object ())), null);
  }

  protected JEnumConstantExpr translate (JEnumConstantExpr expr)
  {
    // TODO how to handle ? it requires an enclosing object.
    // return new JEnumConstantExpr (expr.getEnumConstant ());
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JEnumConstantRef translate (JEnumConstantRef expr)
  {
    return cacheCopy (expr, JEnumConstantRef.class,
        src -> new JEnumConstantRef (translate (expr.type ()), expr.name ()), null);
  }

  protected JLambda translate (JLambda expr)
  {
    return cacheCopy (expr, JLambda.class, src -> new JLambda (), this::copyJLambda);
  }

  protected void copyJLambda (JLambda s, JLambda t)
  {
    for (JLambdaParam param : s.params ())
      t.addParam (translate (param.type ()), param.name ());
    copyJBlock (s.body (), t.body ());
  }

  protected void copyJBlock (JBlock source, JBlock copy)
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
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpBinary translate (JOpBinary expr)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpTernary translate (JOpTernary expr)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpUnary translate (JOpUnary expr)
  {
    if (expr instanceof JOpUnaryTight)
      return translate ((JOpUnaryTight) expr);

    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JOpUnaryTight translate (JOpUnaryTight expr)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JStringLiteral translate (JStringLiteral expr)
  {
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
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JFieldRef translate (JFieldRef expr)
  {
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
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  protected JInvocation translate (JInvocation expr)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + expr.getClass ());
  }

  //
  // IJStatement subclasses that have not been translated before
  //

  // JBlock already done

  protected JBreak translate (JBreak stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JCase translate (JCase stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JConditional translate (JConditional stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JContinue translate (JContinue stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JDirectStatement translate (JDirectStatement stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JDoLoop translate (JDoLoop stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JForEach translate (JForEach stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JForLoop translate (JForLoop stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JLabel translate (JLabel stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JReturn translate (JReturn stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JSingleLineCommentStatement translate (JSingleLineCommentStatement stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JSwitch translate (JSwitch stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JSynchronizedBlock translate (JSynchronizedBlock stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JThrow translate (JThrow stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JTryBlock translate (JTryBlock stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  protected JWhileLoop translate (JWhileLoop stt)
  {
    // TODO
    throw new UnsupportedOperationException ("not done " + stt.getClass ());
  }

  // IJExpressionStatement already done

}
