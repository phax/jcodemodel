/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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

import javax.annotation.Nonnull;

import static com.helger.jcodemodel.util.EqualsUtils.isEqual;
import static com.helger.jcodemodel.util.HashCodeGenerator.getHashCode;

/**
 * Enum Constant. When used as an {@link IJExpression}, this object represents a
 * reference to the enum constant.
 * 
 * @author Bhakti Mehta (Bhakti.Mehta@sun.com)
 */
public class JEnumConstant extends AbstractJExpressionImpl implements IJDeclaration, IJAnnotatable, IJDocCommentable
{
  /**
   * The enum class.
   */
  private final JDefinedClass _type;

  /**
   * The constant.
   */
  private final String _name;

  /**
   * javadoc comments, if any.
   */
  private JDocComment _jdoc;

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> _annotations;

  /**
   * List of the constructor argument expressions. Lazily constructed.
   */
  private List <IJExpression> _args;

  protected JEnumConstant (@Nonnull final JDefinedClass type, @Nonnull final String name)
  {
    if (type == null)
      throw new NullPointerException ("type");
    if (name == null)
      throw new NullPointerException ("name");
    _type = type;
    _name = name;
  }

  @Nonnull
  public JDefinedClass type ()
  {
    return _type;
  }

  /**
   * @return The plain name of the enum constant, without any type prefix
   */
  @Nonnull
  public String name ()
  {
    return _name;
  }

  /**
   * Add an expression to this constructor's argument list
   * 
   * @param arg
   *        Argument to add to argument list
   */
  @Nonnull
  public JEnumConstant arg (@Nonnull final IJExpression arg)
  {
    if (arg == null)
      throw new IllegalArgumentException ("arg");
    if (_args == null)
      _args = new ArrayList <IJExpression> ();
    _args.add (arg);
    return this;
  }

  @Nonnull
  public List <IJExpression> args ()
  {
    if (_args == null)
      _args = new ArrayList <IJExpression> ();
    return Collections.unmodifiableList (_args);
  }

  public boolean hasArgs ()
  {
    return _args != null && !_args.isEmpty ();
  }

  /**
   * Returns the name of this constant including the type name
   * 
   * @return never null.
   */
  @Nonnull
  public String getName ()
  {
    return _type.fullName () + '.' + _name;
  }

  /**
   * Creates, if necessary, and returns the enum constant javadoc.
   * 
   * @return JDocComment containing javadocs for this constant.
   */
  @Nonnull
  public JDocComment javadoc ()
  {
    if (_jdoc == null)
      _jdoc = new JDocComment (_type.owner ());
    return _jdoc;
  }

  /**
   * Adds an annotation to this variable.
   * 
   * @param clazz
   *        The annotation class to annotate the field with
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass clazz)
  {
    if (_annotations == null)
      _annotations = new ArrayList <JAnnotationUse> ();
    final JAnnotationUse a = new JAnnotationUse (clazz);
    _annotations.add (a);
    return a;
  }

  /**
   * Adds an annotation to this variable.
   * 
   * @param clazz
   *        The annotation class to annotate the field with
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> clazz)
  {
    return annotate (_type.owner ().ref (clazz));
  }

  @Nonnull
  public <W extends IJAnnotationWriter <?>> W annotate2 (@Nonnull final Class <W> clazz)
  {
    return TypedAnnotationWriter.create (clazz, this);
  }

  /**
   * {@link IJAnnotatable#annotations()}
   */
  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (_annotations == null)
      _annotations = new ArrayList <JAnnotationUse> ();
    return Collections.unmodifiableList (_annotations);
  }

  public void declare (@Nonnull final JFormatter f)
  {
    if (_jdoc != null)
      f.newline ().generable (_jdoc);
    if (_annotations != null)
      for (final JAnnotationUse annotation : _annotations)
        f.generable (annotation).newline ();
    f.id (_name);
    if (_args != null)
      f.print ('(').generable (_args).print (')');
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.type (_type).print ('.').print (_name);
  }

  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof IJExpression))
      return false;
    o = ((IJExpression) o).unwrapped ();
    if (o == null || getClass () != o.getClass ())
      return false;
    JEnumConstant rhs = (JEnumConstant) o;
    return isEqual (_type.fullName (), rhs._type.fullName ()) &&
        isEqual (_name, rhs._name);
  }

  public int hashCode ()
  {
    return getHashCode (this, _type.fullName (), _name);
  }
}
