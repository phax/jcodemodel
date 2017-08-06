/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Represents X&lt;Y&gt;. TODO: consider separating the decl and the use.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JNarrowedClass extends AbstractJClass
{
  /**
   * A generic class with type parameters.
   */
  private final AbstractJClass m_aBasis;
  /**
   * Arguments to those parameters.
   */
  private final List <? extends AbstractJClass> m_aArgs;

  public JNarrowedClass (@Nonnull final AbstractJClass aBasis, @Nonnull final AbstractJClass aArg)
  {
    this (aBasis, Collections.singletonList (aArg));
  }

  public JNarrowedClass (@Nonnull final AbstractJClass aBasis, @Nonnull final AbstractJClass [] aArgs)
  {
    this (aBasis, Arrays.asList (aArgs));
  }

  public JNarrowedClass (@Nonnull final AbstractJClass aBasis, @Nonnull final List <? extends AbstractJClass> aArgs)
  {
    super (aBasis.owner ());
    JCValueEnforcer.isFalse (aBasis instanceof JNarrowedClass, () -> "aBasis may not be a narrowed class: " + aBasis);
    JCValueEnforcer.notNull (aArgs, "NarrowingClasses");
    m_aBasis = aBasis;
    m_aArgs = aArgs;
  }

  @Override
  public boolean containsTypeVar (@Nullable final JTypeVar aVar)
  {
    if (m_aBasis.containsTypeVar (aVar))
      return true;
    for (final AbstractJClass aArg : m_aArgs)
      if (aArg.containsTypeVar (aVar))
        return true;
    return false;
  }

  @Nonnull
  public AbstractJClass basis ()
  {
    return m_aBasis;
  }

  @Override
  public JNarrowedClass narrow (@Nonnull final AbstractJClass aClazz)
  {
    JCValueEnforcer.notNull (aClazz, "NarrowingClass");

    final List <AbstractJClass> newArgs = new ArrayList <> (m_aArgs);
    newArgs.add (aClazz);
    return new JNarrowedClass (m_aBasis, newArgs);
  }

  @Override
  public JNarrowedClass narrow (@Nonnull final AbstractJClass... aClazz)
  {
    JCValueEnforcer.notNull (aClazz, "NarrowingClass");

    final List <AbstractJClass> newArgs = new ArrayList <> (m_aArgs);
    for (final AbstractJClass aClass : aClazz)
      newArgs.add (aClass);
    return new JNarrowedClass (m_aBasis, newArgs);
  }

  @Override
  public String name ()
  {
    final StringBuilder buf = new StringBuilder ();
    buf.append (m_aBasis.name ()).append ('<');
    boolean bFirst = true;
    for (final AbstractJClass c : m_aArgs)
    {
      if (bFirst)
        bFirst = false;
      else
        buf.append (',');
      buf.append (c.name ());
    }
    buf.append ('>');
    return buf.toString ();
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    final StringBuilder buf = new StringBuilder ();
    buf.append (m_aBasis.fullName ());
    buf.append ('<');
    boolean bFirst = true;
    for (final AbstractJClass c : m_aArgs)
    {
      if (bFirst)
        bFirst = false;
      else
        buf.append (',');
      buf.append (c.fullName ());
    }
    buf.append ('>');
    return buf.toString ();
  }

  @Override
  public String binaryName ()
  {
    final StringBuilder buf = new StringBuilder ();
    buf.append (m_aBasis.binaryName ());
    buf.append ('<');
    boolean bFirst = true;
    for (final AbstractJClass c : m_aArgs)
    {
      if (bFirst)
        bFirst = false;
      else
        buf.append (',');
      buf.append (c.binaryName ());
    }
    buf.append ('>');
    return buf.toString ();
  }

  @Override
  public void generate (final JFormatter f)
  {
    f.type (m_aBasis).print ('<').generable (m_aArgs).print (JFormatter.CLOSE_TYPE_ARGS);
  }

  @Override
  void printLink (final JFormatter f)
  {
    m_aBasis.printLink (f);
    f.print ("{@code <}");
    boolean bFirst = true;
    for (final AbstractJClass c : m_aArgs)
    {
      if (bFirst)
        bFirst = false;
      else
        f.print (',');
      c.printLink (f);
    }
    f.print ("{@code >}");
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    return m_aBasis._package ();
  }

  @Override
  @Nullable
  public AbstractJClass _extends ()
  {
    final AbstractJClass base = m_aBasis._extends ();
    if (base == null)
      return base;
    return base.substituteParams (m_aBasis.typeParams (), m_aArgs);
  }

  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    return new Iterator <AbstractJClass> ()
    {
      private final Iterator <AbstractJClass> m_aCore = m_aBasis._implements ();

      public void remove ()
      {
        m_aCore.remove ();
      }

      public AbstractJClass next ()
      {
        return m_aCore.next ().substituteParams (m_aBasis.typeParams (), m_aArgs);
      }

      public boolean hasNext ()
      {
        return m_aCore.hasNext ();
      }
    };
  }

  @Override
  @Nonnull
  public AbstractJClass erasure ()
  {
    return m_aBasis;
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
    return false;
  }

  @Override
  public boolean isError ()
  {
    if (m_aBasis.isError ())
      return true;
    for (final AbstractJClass aClass : m_aArgs)
    {
      if (aClass.isError ())
        return true;
    }
    return false;
  }

  @Override
  public List <? extends AbstractJClass> getTypeParameters ()
  {
    return m_aArgs;
  }

  @Override
  protected AbstractJClass substituteParams (@Nonnull final JTypeVar [] aVariables,
                                             @Nonnull final List <? extends AbstractJClass> aBindings)
  {
    final AbstractJClass b = m_aBasis.substituteParams (aVariables, aBindings);
    boolean bDifferent = b != m_aBasis;

    final List <AbstractJClass> clazz = new ArrayList <> (m_aArgs.size ());
    for (final AbstractJClass aClass : m_aArgs)
    {
      final AbstractJClass c = aClass.substituteParams (aVariables, aBindings);
      clazz.add (c);
      bDifferent |= c != aClass;
    }

    if (bDifferent)
      return new JNarrowedClass (b, clazz);
    return this;
  }

  //
  // Equality is based on value
  //

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == this)
      return true;
    // This is important so that a JNarrowedClass class and a potential subclass
    // are per-definitionem not identical!
    if (obj == null || !getClass ().equals (obj.getClass ()))
      return false;
    final JNarrowedClass that = (JNarrowedClass) obj;
    return m_aBasis.equals (that.m_aBasis) && m_aArgs.equals (that.m_aArgs);
  }

  @Override
  public int hashCode ()
  {
    return m_aBasis.hashCode () * 37 + m_aArgs.hashCode ();
  }
}
