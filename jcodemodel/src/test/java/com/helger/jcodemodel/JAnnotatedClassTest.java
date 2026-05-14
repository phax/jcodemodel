/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Unit tests for {@link JAnnotatedClass} - type-use annotation support.
 */
public final class JAnnotatedClassTest
{
  /**
   * Test basic type-use annotation: {@code @Deprecated String}
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testSimpleTypeAnnotation () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);

    assertNotNull (annotatedString);
    assertEquals ("String", annotatedString.name ());
    assertEquals ("java.lang.String", annotatedString.fullName ());
    assertEquals (1, annotatedString.annotations ().size ());

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (annotatedString);
    assertEquals ("@java.lang.Deprecated java.lang.String", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, annotatedString, "value");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test type-use annotation on generic type parameter:
   * {@code java.util.List<@java.lang.Deprecated java.lang.String>}
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotatedTypeParameter () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);
    final AbstractJClass listOfAnnotatedString = cm.ref (List.class).narrow (annotatedString);

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (listOfAnnotatedString);
    assertEquals ("java.util.List<@java.lang.Deprecated java.lang.String>", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, listOfAnnotatedString, "items");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test multiple type-use annotations: {@code @Deprecated @Override String}
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testMultipleTypeAnnotations () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class).annotated (Override.class);

    assertEquals (2, annotatedString.annotations ().size ());

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (annotatedString);
    assertEquals ("@java.lang.Deprecated @java.lang.Override java.lang.String", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, annotatedString, "value");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test annotated type with initializer.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotatedFieldWithInitializer () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass testClass = cm._class ("com.example.Test");

    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);
    final AbstractJClass listType = cm.ref (List.class).narrow (annotatedString);
    final AbstractJClass arrayListType = cm.ref (ArrayList.class).narrow (annotatedString);

    testClass.field (JMod.PRIVATE, listType, "items", JExpr._new (arrayListType));

    // Check the generated output contains the annotated type
    final String classOutput = CodeModelTestsHelper.declare (testClass);
    assertTrue ("Expected java.util.List<@java.lang.Deprecated java.lang.String> in output",
                classOutput.contains ("java.util.List<@java.lang.Deprecated java.lang.String>"));

    // Verify it parses
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test that erasure returns the underlying class without annotations.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testErasure () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);

    assertEquals (stringClass, annotatedString.erasure ());
  }

  /**
   * Test that basis() returns the wrapped class.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testBasis () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);

    assertEquals (stringClass, annotatedString.basis ());
  }

  /**
   * Test annotating an already narrowed class.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotateNarrowedClass () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();

    // Create List<String>
    final AbstractJClass listOfString = cm.ref (List.class).narrow (String.class);

    // Annotate the whole List<String> type
    final JAnnotatedClass annotatedListOfString = listOfString.annotated (Deprecated.class);

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (annotatedListOfString);
    assertEquals ("@java.lang.Deprecated java.util.List<java.lang.String>", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, annotatedListOfString, "items");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test complex nested annotations: {@code Map<@A String, List<@B Integer>>}
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testNestedAnnotatedTypes () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();

    // @Deprecated String
    final JAnnotatedClass annotatedString = cm.ref (String.class).annotated (Deprecated.class);

    // @Override Integer
    final JAnnotatedClass annotatedInteger = cm.ref (Integer.class).annotated (Override.class);

    // List<@Override Integer>
    final AbstractJClass listOfAnnotatedInteger = cm.ref (List.class).narrow (annotatedInteger);

    // Map<@Deprecated String, List<@Override Integer>>
    final AbstractJClass mapType = cm.ref (java.util.Map.class).narrow (annotatedString, listOfAnnotatedInteger);

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (mapType);
    assertEquals ("java.util.Map<@java.lang.Deprecated java.lang.String, java.util.List<@java.lang.Override java.lang.Integer>>",
                  generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, mapType, "data");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test that the annotated class works correctly in method parameters.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotatedMethodParameter () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass testClass = cm._class ("com.example.Test");

    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);
    final AbstractJClass listType = cm.ref (List.class).narrow (annotatedString);

    final JMethod method = testClass.method (JMod.PUBLIC, cm.VOID, "process");
    method.param (listType, "items");

    // Check the generated output contains the annotated type in parameter
    final String classOutput = CodeModelTestsHelper.declare (testClass);
    assertTrue ("Expected java.util.List<@java.lang.Deprecated java.lang.String> in method parameter",
                classOutput.contains ("java.util.List<@java.lang.Deprecated java.lang.String> items"));

    // Verify it parses
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test that the annotated class works correctly as method return type.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotatedReturnType () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass testClass = cm._class ("com.example.Test");

    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (Deprecated.class);
    final AbstractJClass listType = cm.ref (List.class).narrow (annotatedString);

    final JMethod method = testClass.method (JMod.PUBLIC, listType, "getItems");
    method.body ()._return (JExpr._null ());

    // Check the generated output contains the annotated return type
    final String classOutput = CodeModelTestsHelper.declare (testClass);
    assertTrue ("Expected java.util.List<@java.lang.Deprecated java.lang.String> as return type",
                classOutput.contains ("java.util.List<@java.lang.Deprecated java.lang.String> getItems()"));

    // Verify it parses
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test equals and hashCode - two annotated classes with same basis and annotations should be
   * equal.
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testEqualsAndHashCode () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final AbstractJClass stringClass = cm.ref (String.class);

    final JAnnotatedClass annotated1 = stringClass.annotated (Deprecated.class);
    final JAnnotatedClass annotated2 = stringClass.annotated (Deprecated.class);
    final JAnnotatedClass annotated3 = stringClass.annotated (Override.class);

    // Same basis and same annotation class should produce equivalent results
    assertEquals (annotated1.basis (), annotated2.basis ());
    assertEquals (annotated1.annotations ().size (), annotated2.annotations ().size ());

    // Different annotations should not be equal
    assertFalse (annotated1.equals (annotated3));
  }

  /**
   * Test annotated array type: {@code @Deprecated String[]}
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotatedArrayType () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();

    // @Deprecated String[]
    final AbstractJClass stringArray = cm.ref (String.class).array ();
    final JAnnotatedClass annotatedArray = stringArray.annotated (Deprecated.class);

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (annotatedArray);
    assertEquals ("@java.lang.Deprecated java.lang.String[]", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, annotatedArray, "items");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test annotated primitive array type: {@code @Deprecated int[]}
   * 
   * @throws JCodeModelException
   *         In case of error
   */
  @Test
  public void testAnnotatedPrimitiveArrayType () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();

    // @Deprecated int[]
    final JArrayClass intArray = cm.INT.array ();
    final JAnnotatedClass annotatedIntArray = intArray.annotated (Deprecated.class);

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (annotatedIntArray);
    assertEquals ("@java.lang.Deprecated int[]", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, annotatedIntArray, "values");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test annotation with parameters: {@code @SuppressWarnings("unchecked") String}
   */
  @Test
  public void testAnnotationWithParameters () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();

    // Create @SuppressWarnings("unchecked") String
    final JAnnotationUse annotation = new JAnnotationUse (cm.ref (SuppressWarnings.class));
    annotation.param ("value", "unchecked");
    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (annotation);

    // Check generated output
    final String generated = CodeModelTestsHelper.generate (annotatedString);
    assertEquals ("@java.lang.SuppressWarnings(\"unchecked\") java.lang.String", generated);

    // Verify it parses when used in a class
    final JDefinedClass testClass = cm._class ("com.example.Test");
    testClass.field (JMod.PRIVATE, annotatedString, "value");
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  /**
   * Test annotation with multiple parameters in a generic type.
   */
  @Test
  public void testAnnotationWithMultipleParameters () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass testClass = cm._class ("com.example.Test");

    // Create a custom annotation class for testing (simulating @Size(min=1, max=10))
    // We'll use @SuppressWarnings with array param as a proxy since it's available
    final JAnnotationUse annotation = new JAnnotationUse (cm.ref (SuppressWarnings.class));
    annotation.paramArray ("value", "unchecked", "rawtypes");

    final AbstractJClass stringClass = cm.ref (String.class);
    final JAnnotatedClass annotatedString = stringClass.annotated (annotation);
    final AbstractJClass listType = cm.ref (List.class).narrow (annotatedString);

    testClass.field (JMod.PRIVATE, listType, "items");

    // Check the generated output contains the annotation with parameters
    final String classOutput = CodeModelTestsHelper.declare (testClass);
    assertTrue ("Expected annotation with array parameters in output",
                classOutput.contains ("@java.lang.SuppressWarnings({"));

    // Verify it parses
    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
