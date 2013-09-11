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

import java.util.Iterator;
import java.util.List;

/**
 * Type variable used to declare generics.
 * 
 * @see JGenerifiable
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JTypeVar extends AbstractJClass implements JDeclaration
{

  private final String name;

  private AbstractJClass bound;

  protected JTypeVar (final JCodeModel owner, final String _name)
  {
    super (owner);
    this.name = _name;
  }

  @Override
  public String name ()
  {
    return name;
  }

  @Override
  public String fullName ()
  {
    return name;
  }

  @Override
  public JPackage _package ()
  {
    return null;
  }

  /**
   * Adds a bound to this variable.
   * 
   * @return this
   */
  public JTypeVar bound (final AbstractJClass c)
  {
    if (bound != null)
      throw new IllegalArgumentException ("type variable has an existing class bound " + bound);
    bound = c;
    return this;
  }

  /**
   * Returns the class bound of this variable.
   * <p>
   * If no bound is given, this method returns {@link Object}.
   */
  @Override
  public AbstractJClass _extends ()
  {
    if (bound != null)
      return bound;
    else
      return owner ().ref (Object.class);
  }

  /**
   * Returns the interface bounds of this variable.
   */
  @Override
  public Iterator <AbstractJClass> _implements ()
  {
    return bound._implements ();
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

  /**
   * Prints out the declaration of the variable.
   */
  public void declare (final JFormatter f)
  {
    f.id (name);
    if (bound != null)
      f.print ("extends").generable (bound);
  }

  @Override
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <AbstractJClass> bindings)
  {
    for (int i = 0; i < variables.length; i++)
      if (variables[i] == this)
        return bindings.get (i);
    return this;
  }

  @Override
  public void generate (final JFormatter f)
  {
    f.id (name);
  }
}
