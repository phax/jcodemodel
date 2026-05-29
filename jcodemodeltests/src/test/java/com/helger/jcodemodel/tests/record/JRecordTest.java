/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.tests.record;

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.tests.record.JRecordTestGen.RecordAnnotationExample;
import com.helger.jcodemodel.tests.record.Outer.Inner;

/**
 * Test class for Java record support. Java records (JEP 395, Java 16+) are a special kind of class
 * that acts as a transparent carrier for immutable data. Records automatically provide: - A
 * canonical constructor - Private final fields for each component - Public accessor methods for
 * each component (same name as component) - equals(), hashCode(), and toString() implementations
 */
@SuppressWarnings ("cast")
public final class JRecordTest
{
  /**
   * tests {@link JRecordTestGen#testBasicRecord()}
   */
  @Test
  public void testBasicRecord ()
  {
    final BasicPoint test = new BasicPoint (2, 3);
    Assert.assertTrue (test instanceof Record);
    Assert.assertEquals (2, test.x ());
    Assert.assertEquals (3, test.y ());
  }

  /**
   * tests {@link JRecordTestGen#testEmptyRecord()}
   */
  @Test
  public void testEmptyRecord ()
  {
    final Empty test = new Empty ();
    Assert.assertTrue (test instanceof Record);
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithObjectComponents()}
   */
  @Test
  public void testRecordWithObjectComponents ()
  {
    final Person test = new Person ("John", Integer.valueOf (42));
    Assert.assertTrue (test instanceof Record);
    Assert.assertEquals (Integer.valueOf (42), test.age ());
    Assert.assertEquals ("John", test.name ());
  }

  /**
   * tests {@link JRecordTestGen#testRecordImplementsInterface()}
   */
  @Test
  public void testRecordImplementsInterface ()
  {
    final NamedPoint test = new NamedPoint (5, 10, "15");
    Assert.assertTrue (test instanceof Record);
    Assert.assertTrue (test instanceof Comparable <NamedPoint>);
    Assert.assertEquals ("15", test.name ());
    Assert.assertEquals (5, test.x ());
    Assert.assertEquals (10, test.y ());
  }

  /**
   * tests {@link JRecordTestGen#testGenericRecord()}
   */
  @Test
  public void testGenericRecord ()
  {
    final Pair <Integer, String> test = new Pair <> (Integer.valueOf (666), "BE NOT AFRAID");
    Assert.assertTrue (test instanceof Record);
    Assert.assertEquals (Integer.valueOf (666), test.first ());
    Assert.assertEquals ("BE NOT AFRAID", test.second ());
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithBoundedTypeParameter()}
   */
  @Test
  public void testRecordWithBoundedTypeParameter ()
  {
    final PairNumber <BigDecimal> test = new PairNumber <> (BigDecimal.ONE, BigDecimal.ZERO);
    Assert.assertTrue (test instanceof Record);
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithAnnotatedComponent()}
   *
   * @throws SecurityException
   *         in case of error
   */
  @Test
  public void testRecordWithAnnotatedComponent ()
  {
    final AnnotatedPerson test = new AnnotatedPerson ("Salomon", 2000);
    Assert.assertTrue (test instanceof Record);

    final RecordComponent fieldComponent = Stream.of (test.getClass ().getRecordComponents ())
                                                 .filter (rc -> rc.getName ().equals ("name"))
                                                 .findFirst ()
                                                 .get ();
    Assert.assertTrue (fieldComponent.isAnnotationPresent (RecordAnnotationExample.class));
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithCompactConstructor()}
   */
  @Test
  public void testRecordWithCompactConstructor ()
  {
    final Range test = new Range (1, 5);
    Assert.assertTrue (test instanceof Record);
    Assert.assertThrows (IllegalArgumentException.class, () -> new Range (5, 1));
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithCanonicalConstructor()}
   */
  @Test
  public void testRecordWithCanonicalConstructor ()
  {
    final RangeCanonical test = new RangeCanonical (1, 5);
    Assert.assertTrue (test instanceof Record);
    Assert.assertThrows (IllegalArgumentException.class, () -> new RangeCanonical (5, 1));
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithMethod()}
   */
  @Test
  public void testRecordWithMethod ()
  {
    final PointDistance test = new PointDistance (0, 0);
    Assert.assertTrue (test instanceof Record);
    Assert.assertEquals (0, test.distance (), 0.0);
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithStaticMembers()}
   */
  @Test
  public void testRecordWithStaticMembers ()
  {
    PointStatic test = PointStatic.ORIGIN;
    Assert.assertTrue (test instanceof Record);
    test = PointStatic.of (6, 78);
    Assert.assertEquals (6, test.x ());
    Assert.assertEquals (78, test.y ());
  }

  /**
   * tests {@link JRecordTestGen#testNestedRecord()}
   */
  @Test
  public void testNestedRecord ()
  {
    final Inner test = new Inner ("NaN");
    Assert.assertTrue (test instanceof Record);
    Assert.assertEquals ("NaN", test.value ());
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithJavadoc()}
   */
  @Test
  public void testRecordWithJavadoc ()
  {
    // nothing to do to test javadoc ??
    final PointJavadoc test = new PointJavadoc (0, 0);
    Assert.assertTrue (test instanceof Record);
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithVarargsComponent()}
   */
  @Test
  public void testRecordWithVarargsComponent ()
  {
    final SeriesVarArgs test = new SeriesVarArgs ("Fibonacci", 1, 1, 3, 4);
    Assert.assertTrue (test instanceof Record);
    Assert.assertArrayEquals (test.values (), new int [] { 1, 1, 3, 4 });
  }

  /**
   * tests {@link JRecordTestGen#testRecordWithArrayComponent()}
   */
  @Test
  public void testRecordWithArrayComponent ()
  {
    final ArrayRecord test = new ArrayRecord (new String [] { "id" }, new int [] [] { { 1, 2 } });
    Assert.assertTrue (test instanceof Record);
    Assert.assertArrayEquals (test.names (), new String [] { "id" });
    Assert.assertArrayEquals (test.matrix ()[0], new int [] { 1, 2 });
  }

}
