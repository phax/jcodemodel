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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;

/**
 * Represents a type with type-use annotations (Java 8+, JSR 308).
 * <p>
 * This class wraps an {@link AbstractJClass} and adds type-use annotations that are rendered
 * directly before the type name. This enables generating code like:
 * <pre>
 * List&lt;@Valid Item&gt;
 * Map&lt;@NonNull String, @Nullable Object&gt;
 * </pre>
 * <p>
 * <b>Type-use annotations vs declaration annotations:</b>
 * <ul>
 * <li>{@link IJAnnotatable#annotate(Class)} adds annotations to <em>declarations</em> (the field,
 * method, or class itself). Example: {@code @NotNull private List<String> items;}</li>
 * <li>{@link AbstractJClass#annotated(Class)} creates a new <em>type</em> with embedded annotations.
 * Example: {@code private List<@NotNull String> items;}</li>
 * </ul>
 * <p>
 * <b>Annotations with parameters:</b>
 * <p>
 * For annotations that require parameters, use {@link AbstractJClass#annotated(JAnnotationUse)}:
 * <pre>
 * JAnnotationUse sizeAnnotation = new JAnnotationUse(codeModel.ref(Size.class));
 * sizeAnnotation.param("min", 1).param("max", 10);
 * AbstractJClass annotatedString = stringClass.annotated(sizeAnnotation);
 * // Generates: @Size(min = 1, max = 10) String
 * </pre>
 * <p>
 * Use {@link AbstractJClass#annotated(Class)}, {@link AbstractJClass#annotated(AbstractJClass)},
 * or {@link AbstractJClass#annotated(JAnnotationUse)} to create instances of this class.
 *
 * @since 4.2.0
 */
public class JAnnotatedClass extends AbstractJClass
{
  /**
   * The wrapped class that this annotation applies to.
   */
  private final AbstractJClass m_aBasis;

  /**
   * The type-use annotations to be rendered before the type.
   */
  private final List <JAnnotationUse> m_aAnnotations;

  /**
   * Creates a new annotated class with a single annotation.
   *
   * @param aBasis
   *        The class to annotate. May not be <code>null</code>.
   * @param aAnnotation
   *        The annotation to apply. May not be <code>null</code>.
   */
  public JAnnotatedClass (@NonNull final AbstractJClass aBasis, @NonNull final JAnnotationUse aAnnotation)
  {
    this (aBasis, Collections.singletonList (aAnnotation));
  }

  /**
   * Creates a new annotated class with multiple annotations.
   *
   * @param aBasis
   *        The class to annotate. May not be <code>null</code>.
   * @param aAnnotations
   *        The annotations to apply. May not be <code>null</code> or empty.
   */
  public JAnnotatedClass (@NonNull final AbstractJClass aBasis, @NonNull final List <JAnnotationUse> aAnnotations)
  {
    super (aBasis.owner ());
    ValueEnforcer.notNull (aBasis, "Basis");
    ValueEnforcer.notEmpty (aAnnotations, "Annotations");
    m_aBasis = aBasis;
    m_aAnnotations = new ArrayList <> (aAnnotations);
  }

  /**
   * @return The underlying class without annotations.
   */
  @NonNull
  public AbstractJClass basis ()
  {
    return m_aBasis;
  }

  /**
   * @return An unmodifiable list of annotations on this type.
   */
  @NonNull
  public List <JAnnotationUse> annotations ()
  {
    return Collections.unmodifiableList (m_aAnnotations);
  }

  @Override
  @NonNull
  public JAnnotatedClass annotated (@NonNull final Class <? extends Annotation> aClazz)
  {
    return annotated (owner ().ref (aClazz));
  }

  @Override
  @NonNull
  public JAnnotatedClass annotated (@NonNull final AbstractJClass aClazz)
  {
    final List <JAnnotationUse> newAnnotations = new ArrayList <> (m_aAnnotations);
    newAnnotations.add (new JAnnotationUse (aClazz));
    return new JAnnotatedClass (m_aBasis, newAnnotations);
  }

  @Override
  @NonNull
  public JAnnotatedClass annotated (@NonNull final JAnnotationUse aAnnotation)
  {
    final List <JAnnotationUse> newAnnotations = new ArrayList <> (m_aAnnotations);
    newAnnotations.add (aAnnotation);
    return new JAnnotatedClass (m_aBasis, newAnnotations);
  }

  @Override
  public String name ()
  {
    return m_aBasis.name ();
  }

  @Override
  @NonNull
  public String fullName ()
  {
    return m_aBasis.fullName ();
  }

  @Override
  public String binaryName ()
  {
    return m_aBasis.binaryName ();
  }

  @Override
  public JPackage _package ()
  {
    return m_aBasis._package ();
  }

  @Override
  @Nullable
  public AbstractJClass _extends ()
  {
    return m_aBasis._extends ();
  }

  @Override
  @NonNull
  public Iterator <AbstractJClass> _implements ()
  {
    return m_aBasis._implements ();
  }

  @Override
  public boolean isInterface ()
  {
    return m_aBasis.isInterface ();
  }

  @Override
  public boolean isAbstract ()
  {
    return m_aBasis.isAbstract ();
  }

  @Override
  public boolean isArray ()
  {
    return m_aBasis.isArray ();
  }

  @Override
  public boolean isError ()
  {
    return m_aBasis.isError ();
  }

  @Override
  @Nullable
  public JPrimitiveType getPrimitiveType ()
  {
    return m_aBasis.getPrimitiveType ();
  }

  @Override
  @NonNull
  public AbstractJClass erasure ()
  {
    return m_aBasis.erasure ();
  }

  @Override
  public boolean containsTypeVar (@Nullable final JTypeVar aVar)
  {
    return m_aBasis.containsTypeVar (aVar);
  }

  @Override
  @NonNull
  public List <? extends AbstractJClass> getTypeParameters ()
  {
    return m_aBasis.getTypeParameters ();
  }

  @Override
  @NonNull
  public JTypeVar [] typeParams ()
  {
    return m_aBasis.typeParams ();
  }

  @Override
  @Nullable
  public AbstractJClass outer ()
  {
    return m_aBasis.outer ();
  }

  @Override
  protected AbstractJClass substituteParams (@NonNull final JTypeVar [] aVariables,
                                             @NonNull final List <? extends AbstractJClass> aBindings)
  {
    final AbstractJClass newBasis = m_aBasis.substituteParams (aVariables, aBindings);
    if (newBasis == m_aBasis)
      return this;
    return new JAnnotatedClass (newBasis, m_aAnnotations);
  }

  @Override
  public void generate (@NonNull final IJFormatter f)
  {
    for (final JAnnotationUse annotation : m_aAnnotations)
    {
      f.generable (annotation).print (' ');
    }
    f.type (m_aBasis);
  }

  @Override
  void printLink (@NonNull final IJFormatter f)
  {
    m_aBasis.printLink (f);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == this)
      return true;
    if (obj == null || !getClass ().equals (obj.getClass ()))
      return false;
    final JAnnotatedClass that = (JAnnotatedClass) obj;
    return m_aBasis.equals (that.m_aBasis) && m_aAnnotations.equals (that.m_aAnnotations);
  }

  @Override
  public int hashCode ()
  {
    return m_aBasis.hashCode () * 37 + m_aAnnotations.hashCode ();
  }
}
