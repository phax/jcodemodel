/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents an arrays as annotation members
 * <p>
 * This class implements {@link JAnnotatable} to allow new annotations to be
 * added as a member of the array.
 * 
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public class JAnnotationArrayMember extends AbstractJAnnotationValue implements JAnnotatable
{
  private final List <AbstractJAnnotationValue> values = new ArrayList <AbstractJAnnotationValue> ();
  private final JCodeModel owner;

  protected JAnnotationArrayMember (final JCodeModel owner)
  {
    this.owner = owner;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a string value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final String value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a boolean value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final boolean value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a byte value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final byte value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a char value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final char value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a double value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final double value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a long value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final long value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a short value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final short value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds an int value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final int value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an array member to this annotation
   * 
   * @param value
   *        Adds a float value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final float value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (JExpr.lit (value));
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds a enum array member to this annotation
   * 
   * @param value
   *        Adds a enum value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final Enum <?> value)
  {
    final AbstractJAnnotationValue annotationValue = new AbstractJAnnotationValue ()
    {
      public void generate (final JFormatter f)
      {
        f.type (owner.ref (value.getDeclaringClass ())).print ('.').print (value.name ());
      }
    };
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds a enum array member to this annotation
   * 
   * @param value
   *        Adds a enum value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final JEnumConstant value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (value);
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds an expression array member to this annotation
   * 
   * @param value
   *        Adds an expression value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final JExpression value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (value);
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds a class array member to this annotation
   * 
   * @param value
   *        Adds a class value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  public JAnnotationArrayMember param (final Class <?> value)
  {
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (new AbstractJExpressionImpl ()
    {
      public void generate (final JFormatter f)
      {
        f.print (value.getName ().replace ('$', '.'));
        f.print (".class");
      }
    });
    values.add (annotationValue);
    return this;
  }

  public JAnnotationArrayMember param (final AbstractJType type)
  {
    final AbstractJClass clazz = type.boxify ();
    final AbstractJAnnotationValue annotationValue = new JAnnotationStringValue (clazz.dotclass ());
    values.add (annotationValue);
    return this;
  }

  /**
   * Adds a new annotation to the array.
   */
  public JAnnotationUse annotate (final Class <? extends Annotation> clazz)
  {
    return annotate (owner.ref (clazz));
  }

  /**
   * Adds a new annotation to the array.
   */
  public JAnnotationUse annotate (final AbstractJClass clazz)
  {
    final JAnnotationUse a = new JAnnotationUse (clazz);
    values.add (a);
    return a;
  }

  public <W extends JAnnotationWriter <?>> W annotate2 (final Class <W> clazz)
  {
    return TypedAnnotationWriter.create (clazz, this);
  }

  /**
   * {@link JAnnotatable#annotations()}
   */
  @SuppressWarnings ({ "unchecked", "rawtypes" })
  public Collection <JAnnotationUse> annotations ()
  {
    // this invocation is invalid if the caller isn't adding annotations into an
    // array
    // so this potentially type-unsafe conversion would be justified.
    return Collections.<JAnnotationUse> unmodifiableList ((List) values);
  }

  public void generate (final JFormatter f)
  {
    f.print ('{').newline ().indent ();

    boolean first = true;
    for (final AbstractJAnnotationValue aValue : values)
    {
      if (!first)
        f.print (',').newline ();
      f.generable (aValue);
      first = false;
    }
    f.newline ().outdent ().print ('}');
  }
}
