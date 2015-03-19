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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
  private final AbstractJClass _basis;
  /**
   * Arguments to those parameters.
   */
  private final List <? extends AbstractJClass> _args;

  public JNarrowedClass (@Nonnull final AbstractJClass basis, @Nonnull final AbstractJClass arg)
  {
    this (basis, Collections.singletonList (arg));
  }

  public JNarrowedClass (@Nonnull final AbstractJClass basis, @Nonnull final List <? extends AbstractJClass> args)
  {
    super (basis.owner ());
    if (basis instanceof JNarrowedClass)
      throw new IllegalArgumentException ("basis may not be a narrowed class: " + basis);
    if (args == null || args.isEmpty ())
      throw new IllegalArgumentException ("Arguments are missing");
    _basis = basis;
    _args = args;
  }

  @Nonnull
  public AbstractJClass basis ()
  {
    return _basis;
  }

  @Override
  public JNarrowedClass narrow (@Nonnull final AbstractJClass clazz)
  {
    if (clazz == null)
      throw new IllegalArgumentException ("Narrowing class is missing");

    final List <AbstractJClass> newArgs = new ArrayList <AbstractJClass> (_args);
    newArgs.add (clazz);
    return new JNarrowedClass (_basis, newArgs);
  }

  @Override
  public JNarrowedClass narrow (@Nonnull final AbstractJClass... clazz)
  {
    if (clazz == null || clazz.length == 0)
      throw new IllegalArgumentException ("Narrowing classes are missing");

    final List <AbstractJClass> newArgs = new ArrayList <AbstractJClass> (_args);
    for (final AbstractJClass aClass : clazz)
      newArgs.add (aClass);
    return new JNarrowedClass (_basis, newArgs);
  }

  @Override
  public String name ()
  {
    final StringBuilder buf = new StringBuilder ();
    buf.append (_basis.name ()).append ('<');
    boolean first = true;
    for (final AbstractJClass c : _args)
    {
      if (first)
        first = false;
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
    buf.append (_basis.fullName ());
    buf.append ('<');
    boolean first = true;
    for (final AbstractJClass c : _args)
    {
      if (first)
        first = false;
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
    buf.append (_basis.binaryName ());
    buf.append ('<');
    boolean first = true;
    for (final AbstractJClass c : _args)
    {
      if (first)
        first = false;
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
    f.type (_basis).print ('<').generable (_args).print (JFormatter.CLOSE_TYPE_ARGS);
  }

  @Override
  void printLink (final JFormatter f)
  {
    _basis.printLink (f);
    f.print ("{@code <}");
    boolean first = true;
    for (final AbstractJClass c : _args)
    {
      if (first)
        first = false;
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
    return _basis._package ();
  }

  @Override
  @Nullable
  public AbstractJClass _extends ()
  {
    final AbstractJClass base = _basis._extends ();
    if (base == null)
      return base;
    return base.substituteParams (_basis.typeParams (), _args);
  }

  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    return new Iterator <AbstractJClass> ()
    {
      private final Iterator <AbstractJClass> core = _basis._implements ();

      public void remove ()
      {
        core.remove ();
      }

      public AbstractJClass next ()
      {
        return core.next ().substituteParams (_basis.typeParams (), _args);
      }

      public boolean hasNext ()
      {
        return core.hasNext ();
      }
    };
  }

  @Override
  @Nonnull
  public AbstractJClass erasure ()
  {
    return _basis;
  }

  @Override
  public boolean isInterface ()
  {
    return _basis.isInterface ();
  }

  @Override
  public boolean isAbstract ()
  {
    return _basis.isAbstract ();
  }

  @Override
  public boolean isArray ()
  {
    return false;
  }

  @Override
  public boolean isError ()
  {
    if (_basis.isError ())
      return true;
    for (final AbstractJClass aClass : _args)
    {
      if (aClass.isError ())
        return true;
    }
    return false;
  }

  @Override
  public List <? extends AbstractJClass> getTypeParameters ()
  {
    return _args;
  }

  @Override
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <? extends AbstractJClass> bindings)
  {
    final AbstractJClass b = _basis.substituteParams (variables, bindings);
    boolean different = b != _basis;

    final List <AbstractJClass> clazz = new ArrayList <AbstractJClass> (_args.size ());
    for (final AbstractJClass aClass : _args)
    {
      final AbstractJClass c = aClass.substituteParams (variables, bindings);
      clazz.add (c);
      different |= c != aClass;
    }

    if (different)
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
    return _basis.equals (that._basis) && _args.equals (that._args);
  }

  @Override
  public int hashCode ()
  {
    return _basis.hashCode () * 37 + _args.hashCode ();
  }
}
