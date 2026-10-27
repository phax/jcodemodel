package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression.CharArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression.IntArrExp;

@TestJCM
public class TypeCheckTestGen
{

  ///
  /// Check that boxing / unboxing is correctly represented.
  ///
  /// Basically, this means an Integer is an int, and a char is an int, but a Character is not an
  /// Integer.
  ///
  /// checks that the passing of int/Integer is correct.
  public void boxedTypeTest (JPackage jp, JCodeModel jcm) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("BoxedTypeExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jcm.VOID, "test");
    JBlock body = meth.body ();
    StringExpression s = StringExpression.of (body.decl (jcm.ref (String.class), "s", JExpr.lit ("test")));

    // compiles, as we pass an int to an int param
    "test".charAt (0);
    body.add ((IJStatement) s.charAt (IntExpression.of (0)).raw ());

    // compiles, as an Integer can be boxed into an int.
    "test".charAt ((Integer) 0);
    // however we need to explicitly unbox it
    ObjectExpression <Integer> boxedInt = IntExpression.of (0).cast (jcm.ref (Integer.class));
    body.add ((IJStatement) s.charAt (IntExpression.unboxing (boxedInt)).raw ());

    // compiles, as a char is promoted to an int
    "test".charAt ('\0');
    body.add ((IJStatement) s.charAt (CharExpression.of ('\0')).raw ());

    // compiles, since a Character is unboxed into a char, which is an int.
    "test".charAt ((Character) '\0');
    // however we also need to explicit the unboxing
    ObjectExpression <Character> boxedChar = CharExpression.of ('\0').cast (jcm.ref (Character.class));
    body.add ((IJStatement) s.charAt (CharExpression.unboxing (boxedChar)).raw ());

    // does not compile since a Character is not an Integer
    // Integer i = Character.valueOf ('a');
  }

  ///
  /// arrays are strictly covariant. This means
  /// - `Object[] oarr = new String[] {};` : valid, String is child of Object
  /// - `String[] sarr = new Object[] {};` : *not* valid, Object is *not* child of String
  ///
  /// However they don't promote, nor box/unbox, the primitives. This means the following are
  /// illegal
  /// - `int [] iarr = new Integer[] {};` unboxing is not accepted
  /// - `Integer [] iarr = new int[] {};` boxing is not accepted
  /// - `int [] iarr = new char[] {};` promotion is not accepted
  ///
  public void covariantArrayTest (JPackage jp, JCodeModel jcm) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("CovariantArrayExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jcm.VOID, "test");
    JBlock body = meth.body ();
    // StringExpression s = StringExpression.of (body.decl (jcm.ref (String.class), "s", JExpr.lit
    // ("test")));

    // assign a char arr to a char arr.
    body.add ((IJStatement) StringExpression.new_ (jcm, CharArrExp.of (jcm, 't', 'e', 's', 't')).raw ());
    // generates
    new String (new char [] { 't', 'e', 's', 't' });

    //

    // assign a int[] to a char[]
    // fails as it must be the exact type
    IntArrExp intArray = IntArrExp.of (jcm, 't', 'e', 's', 't');
    body.add ((IJStatement) intArray.getClass_ ().raw ());
    // generates
    new int [] { 't', 'e', 's', 't' }.getClass ();
    // body.add ((IJStatement) StringExpression.new_ (jcm, intArray).raw ());
    // generates
    // new String (new int [] { 't', 'e', 's', 't' });

    //

    // assigns a Character[] to a char[]
    // fails as unboxing is not allowed.
    ArrayExpression <Character> charArray = ArrayExpression.of (jcm.ref (Character.class),
                                                                CharExpression.of ('t'),
                                                                CharExpression.of ('e'),
                                                                CharExpression.of ('s'),
                                                                CharExpression.of ('t'));
    // this works, it's the usage as param that fails
    body.add ((IJStatement) charArray.getClass_ ().raw ());
    // generates
    new Character [] { 't', 'e', 's', 't' }.getClass ();
    // new String (new Character [] { 't', 'e', 's', 't' });
    // fails as Character[] is not char[]
    // body.add ((IJStatement) StringExpression.new_ (jcm, charArray).raw ());
  }

}
