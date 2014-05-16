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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A field that can have a {@link JDocComment} associated with it
 */
public class JFieldVar extends JVar implements IJDocCommentable
{
  private final JDefinedClass _owner;

  /**
   * javadoc comments for this JFieldVar
   */
  private JDocComment _jdoc;

  /**
   * JFieldVar constructor
   * 
   * @param type
   *        Datatype of this variable
   * @param name
   *        Name of this variable
   * @param init
   *        Value to initialize this variable to
   */
  protected JFieldVar (@Nonnull final JDefinedClass owner,
                       @Nonnull final JMods mods,
                       @Nonnull final AbstractJType type,
                       @Nonnull final String name,
                       @Nullable final IJExpression init)
  {
    super (mods, type, name, init);
    this._owner = owner;
  }

  @Nonnull
  public JDefinedClass owner ()
  {
    return _owner;
  }

  @Override
  public void name (final String name)
  {
    // make sure that the new name is available
    if (_owner.fields.containsKey (name))
      throw new IllegalArgumentException ("name " + name + " is already in use");
    final String oldName = name ();
    super.name (name);
    _owner.fields.remove (oldName);
    _owner.fields.put (name, this);
  }

  /**
   * Creates, if necessary, and returns the class javadoc for this JDefinedClass
   * 
   * @return JDocComment containing javadocs for this class
   */
  @Nonnull
  public JDocComment javadoc ()
  {
    if (_jdoc == null)
      _jdoc = new JDocComment (_owner.owner ());
    return _jdoc;
  }

  @Override
  public void declare (@Nonnull final JFormatter f)
  {
    if (_jdoc != null)
      f.generable (_jdoc);
    super.declare (f);
  }
}
