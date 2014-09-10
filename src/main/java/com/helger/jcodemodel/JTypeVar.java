/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2014 Philip Helger
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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Type variable used to declare generics.
 *
 * @see IJGenerifiable
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JTypeVar extends AbstractJClass implements IJDeclaration
{
  private final String _name;
  private final List <AbstractJClass> _bounds = new ArrayList <AbstractJClass> ();

  protected JTypeVar (@Nonnull final JCodeModel owner, @Nonnull final String name)
  {
    super (owner);
    if (name == null || name.length () == 0)
      throw new IllegalArgumentException ("Name may not be empty!");
    _name = name;
  }

  @Override
  @Nonnull
  public String name ()
  {
    return _name;
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return _name;
  }

  @Override
  @Nullable
  public JPackage _package ()
  {
    return null;
  }

  /**
   * Adds a bound to this variable.
   *
   * @return this
   */
  @Nonnull
  public JTypeVar bound (@Nonnull final AbstractJClass bound)
  {
    if (bound == null)
      throw new IllegalArgumentException ("bound may not be null");

    _bounds.add (bound);
    return this;
  }

  /**
   * Returns the class bound of this variable.
   * <p>
   * If no bound is given, this method returns {@link Object}.
   */
  @Override
  @Nonnull
  public AbstractJClass _extends ()
  {
    if (_bounds.isEmpty () || _bounds.get (0).isInterface ())
    {
      // implicit "extends Object"
      return owner ().ref (Object.class);
    }
    return _bounds.get (0);
  }

  /**
   * Returns the interface bounds of this variable.
   */
  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    if (_bounds.isEmpty () || _bounds.get (0).isInterface ())
      return _bounds.iterator ();
    return _bounds.subList (1, _bounds.size ()).iterator ();
  }

  @Override
  public boolean isInterface ()
  {
    return false;
  }

  @Override
  public boolean isAbstract ()
  {
    return false;
  }

  @Override
  @Nonnull
  protected AbstractJClass substituteParams (@Nonnull final JTypeVar [] variables,
                                             @Nonnull final List <? extends AbstractJClass> bindings)
  {
    for (int i = 0; i < variables.length; i++)
      if (variables[i] == this)
        return bindings.get (i);
    return this;
  }

  /**
   * Prints out the declaration of the variable.
   */
  public void declare (@Nonnull final JFormatter f)
  {
    f.id (name ());
    if (!_bounds.isEmpty ())
    {
      f.print ("extends").generable (_bounds.get (0));
      for (final AbstractJClass clazz : _bounds.subList (1, _bounds.size ()))
      {
        f.print ("& ").generable (clazz);
      }
    }
  }

  @Override
  public void generate (@Nonnull final JFormatter f)
  {
    f.id (_name);
  }
}
