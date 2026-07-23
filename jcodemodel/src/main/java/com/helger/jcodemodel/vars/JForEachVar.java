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
package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

///
/// a variable that is declared as part of a `for( type name : iterable)`
///
/// when declared, it uses the required expression with ':' instead of '=', and does not add a semicolon.
///
///  - only allowed mod is final.
///  - type can be null. In that case, "var" is used.
///  - name needs be provided. Can be relaxed later for "_"
///  - The init expression **must** be null
///  - a new **collection** property is used instead to store the iteration
///
public class JForEachVar extends JVar
{

  protected IJExpression collection;

  public JForEachVar (boolean isFinal,
                      @Nullable AbstractJType aType,
                      @NonNull String sName,
                      @NonNull IJExpression aCollection)
  {
    super (JMods.forVar (isFinal ? JMod.FINAL : JMod.NONE), aType, sName, null);
    collection = aCollection;
  }

  public IJExpression collection ()
  {
    return collection;
  }

  @Override
  public @NonNull JForEachVar init (@Nullable IJExpression aInitExpr)
  {
    if (aInitExpr != null)
    {
      throw new UnsupportedOperationException (getClass ().getSimpleName () + " can't receive a non-null init");
    }
    return this;
  }

  @Override
  public void bind (@NonNull final IJFormatter f)
  {
    for (final JAnnotationUse annotation : annotations ())
    {
      f.generable (annotation);
      f.print (' ');
    }
    f.generable (mods ());
    if (type () != null)
    {
      f.generable (type ());
    }
    else
    {
      f.print ("var");
    }
    f.id (name ()).print (':').generable (collection ());
  }

  @Override
  public String separator ()
  {
    throw new UnsupportedOperationException ("can't declare two vars in a foreach loop");
  }

}
