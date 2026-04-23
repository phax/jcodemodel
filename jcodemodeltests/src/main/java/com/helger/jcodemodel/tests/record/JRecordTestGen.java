package com.helger.jcodemodel.tests.record;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JRecordComponent;
import com.helger.jcodemodel.JTypeVar;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class JRecordTestGen {

  public final String rootPackage=getClass().getPackageName();

  /**
   * Test: Basic record with two components Expected output:
   *
   * <pre>
   * package org.example;
   *
   * public record Point(int x, int y) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testBasicRecord() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass rec = cm._package (rootPackage)._record ("BasicPoint");
    rec.recordComponent (cm.INT, "x");
    rec.recordComponent (cm.INT, "y");
    return cm;
  }

  /**
   * Test: Empty record (no components) Expected output:
   *
   * <pre>
   * public record Empty ()
   * {}
   * </pre>
   *
   * @throws JCodeModelException
   *         In case of error
   */
  public JCodeModel testEmptyRecord() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    cm._package(rootPackage)._record("Empty");
    return cm;
  }

  /**
   * Test: Record with object type components Expected output:
   *
   * <pre>
   * public record Person (String name, Integer age)
   * {}
   * </pre>
   *
   * @throws JCodeModelException
   *         In case of error
   */
  public JCodeModel testRecordWithObjectComponents() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass rec = cm._package (rootPackage)._record ("Person");
    rec.recordComponent (cm.ref (String.class), "name");
    rec.recordComponent (cm.ref (Integer.class), "age");
    return cm;
  }

  /**
   * Test: Record implementing an interface Expected output:
   *
   * <pre>
   * public record NamedPoint (int x, int y, String name) implements Comparable &lt;NamedPoint&gt;
   * {}
   * </pre>
   *
   * @throws JCodeModelException
   *         In case of error
   */
  public JCodeModel testRecordImplementsInterface() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass rec = cm._package (rootPackage)._record ("NamedPoint");
    rec.recordComponent (cm.INT, "x");
    rec.recordComponent (cm.INT, "y");
    rec.recordComponent (cm.ref (String.class), "name");
    rec._implements (cm.ref (Comparable.class).narrow (rec));
    JMethod cmp = rec.method(JMod.PUBLIC, cm.INT, "compareTo");
    cmp.param(rec, "other");
    cmp.body()._return(JExpr.lit(0));
    return cm;
  }

  /**
   * Test: Generic record with type parameters Expected output:
   *
   * <pre>
   * public record Pair&lt;T, U&gt;(T first, U second) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testGenericRecord() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("Pair");
    final JTypeVar t = rec.generify("T");
    final JTypeVar u = rec.generify("U");
    rec.recordComponent(t, "first");
    rec.recordComponent(u, "second");
    return cm;
  }

  /**
   * Test: Record with bounded generic type parameter Expected output:
   *
   * <pre>
   * public record NumberPair&lt;T extends Number&gt;(T first, T second) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithBoundedTypeParameter() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("PairNumber");
    final JTypeVar t = rec.generify("T", Number.class);
    rec.recordComponent(t, "first");
    rec.recordComponent(t, "second");
    return cm;
  }

  /**
   * Test: Record with annotated component Expected output:
   *
   * <pre>
   * public record Person(@NonNull String name, int age) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithAnnotatedComponent() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("AnnotatedPerson");
    final JRecordComponent nameComponent = rec.recordComponent(cm.ref(String.class), "name");
    nameComponent.annotate(RecordAnnotationExample.class);
    rec.recordComponent(cm.INT, "age");
    return cm;
  }

  /**
   * we need a specific record annotation to be kept
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.RECORD_COMPONENT)
  public @interface RecordAnnotationExample
  {
  }

  /**
   * Test: Record with compact constructor (validation) Expected output:
   *
   * <pre>
   * public record Range(int lo, int hi) {
   * 	public Range {
   * 		if (lo > hi) {
   * 			throw new IllegalArgumentException();
   * 		}
   * 	}
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithCompactConstructor() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("Range");
    final JRecordComponent rcLo = rec.recordComponent(cm.INT, "lo");
    final JRecordComponent rcHi = rec.recordComponent(cm.INT, "hi");

    // Compact constructor - no parameter list, just validation logic
    final JMethod compactCtor = rec.compactConstructor(JMod.PUBLIC);
    compactCtor.body()
        ._if(JExpr.ref(rcLo).gt(JExpr.ref(rcHi)))
        ._then()
        ._throw(cm.ref(IllegalArgumentException.class), JExpr.lit("High must be greater or equal to Low"));
    return cm;
  }

  /**
   * Test: Record with explicit canonical constructor Expected output:
   *
   * <pre>
   * public record Range(int lo, int hi) {
   * 	public Range(int lo, int hi) {
   * 		if (lo > hi) {
   * 			throw new IllegalArgumentException();
   * 		}
   * 		this.lo = lo;
   * 		this.hi = hi;
   * 	}
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithCanonicalConstructor() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("RangeCanonical");
    final JRecordComponent rcLo = rec.recordComponent(cm.INT, "lo");
    final JRecordComponent rcHi = rec.recordComponent(cm.INT, "hi");

    // Canonical constructor - must have same parameters as record components
    final JMethod ctor = rec.constructor(JMod.PUBLIC);
    final JVar loParam = ctor.param(cm.INT, "lo");
    final JVar hiParam = ctor.param(cm.INT, "hi");
    ctor.body()._if(loParam.gt(hiParam))._then()
        ._throw(cm.ref(IllegalArgumentException.class),
            JExpr.lit("lo must be < hi"));
    ctor.body().assign(JExpr.refthis(rcLo), loParam);
    ctor.body().assign(JExpr.refthis(rcHi), hiParam);
    return cm;
  }

  /**
   * Test: Record with additional instance method Expected output:
   *
   * <pre>
   * public record Point (int x, int y)
   * {
   *   public double distance ()
   *   {
   *     return Math.sqrt ((x * x) + (y * y));
   *   }
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *         In case of error
   */
  public JCodeModel testRecordWithMethod () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass rec = cm._package(rootPackage)._record("PointDistance");
    final JRecordComponent rcX = rec.recordComponent (cm.INT, "x");
    final JRecordComponent rcY = rec.recordComponent (cm.INT, "y");

    final JMethod method = rec.method (JMod.PUBLIC, cm.DOUBLE, "distance");
    method.body ()
          ._return (cm.ref (Math.class)
                      .staticInvoke ("sqrt")
                      .arg (JExpr.ref (rcX).mul (JExpr.ref (rcX)).plus (JExpr.ref (rcY).mul (JExpr.ref (rcY)))));
    return cm;
  }

  /**
   * Test: Record with static field and method Expected output:
   *
   * <pre>
   * public record Point(int x, int y) {
   * 	public static final Point ORIGIN = new Point(0, 0);
   *
   * 	public static Point of(int x, int y) {
   * 		return new Point(x, y);
   * 	}
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithStaticMembers() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("PointStatic");
    rec.recordComponent(cm.INT, "x");
    rec.recordComponent(cm.INT, "y");

    // Static field
    rec.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
        rec,
        "ORIGIN",
        JExpr._new(rec).arg(JExpr.lit(0)).arg(JExpr.lit(0)));

    // Static factory method
    final JMethod factory = rec.method(JMod.PUBLIC | JMod.STATIC, rec, "of");
    final JVar xParam = factory.param(cm.INT, "x");
    final JVar yParam = factory.param(cm.INT, "y");
    factory.body()._return(JExpr._new(rec).arg(xParam).arg(yParam));
    return cm;
  }

  /**
   * Test: Nested record inside a class Expected output:
   *
   * <pre>
   * public class Outer {
   * 	public record Inner(String value) {
   * 	}
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testNestedRecord() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass outer = cm._package(rootPackage)._class("Outer");
    final JDefinedClass inner = outer._record(JMod.PUBLIC, "Inner");
    inner.recordComponent(cm.ref(String.class), "value");
    return cm;
  }

  /**
   * Test: Record with javadoc Expected output:
   *
   * <pre>
   * /**
   *  * Represents a 2D point.
   *  *
   *  * @param x the x coordinate
   *  * @param y the y coordinate
   *  *\/
   * public record Point(int x, int y) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithJavadoc() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("PointJavadoc");
    final JRecordComponent rcX = rec.recordComponent(cm.INT, "x");
    final JRecordComponent rcY = rec.recordComponent(cm.INT, "y");
    rec.javadoc().add("Represents a 2D point.");
    rec.javadoc().addParam(rcX).add("the x coordinate");
    rec.javadoc().addParam(rcY).add("the y coordinate");
    return cm;
  }

  /**
   * Test: Record with varargs component (last component can be varargs) Expected
   * output:
   *
   * <pre>
   * public record VarArgsRecord(String name, int... values) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithVarargsComponent() throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("SeriesVarArgs");
    rec.recordComponent(cm.ref(String.class), "name");
    rec.recordComponentVararg(cm.INT, "values");
    return cm;
  }

  /**
   * Test: Record with array component Expected output:
   *
   * <pre>
   * public record ArrayRecord(String[] names, int[][] matrix) {
   * }
   * </pre>
   *
   * @throws JCodeModelException
   *                             In case of error
   */
  public JCodeModel testRecordWithArrayComponent() throws JCodeModelException {
    final JCodeModel cm = new JCodeModel();
    final JDefinedClass rec = cm._package(rootPackage)._record("ArrayRecord");
    rec.recordComponent(cm.ref(String.class).array(), "names");
    rec.recordComponent(cm.INT.array().array(), "matrix");
    return cm;
  }

}
