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
package com.helger.jcodemodel.switchexpression;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJObject;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JLambdaBlock;
import com.helger.jcodemodel.JSwitchExpression;
import com.helger.jcodemodel.JThrow;
import com.helger.jcodemodel.JYield;

///
/// a switch case using arrow. The case select depends on the implementation
/// [BlockSelection] allows to chain them, but requires to provide a list of blocks
public abstract class AJCaseArrow <Self extends AJCaseArrow <?>> implements IJStatement, IBlockSelection <Self>
{
  private final JSwitchExpression m_aParent;
  private final JLambdaBlock m_aBlock = new JLambdaBlock ();
  private final List <JBlock> m_aBlocks = List.of (m_aBlock);

  public AJCaseArrow (final JSwitchExpression parent)
  {
    this.m_aParent = parent;
  }

  public JSwitchExpression up ()
  {
    return m_aParent;
  }

  public JLambdaBlock getBlock ()
  {
    return m_aBlock;
  }

  @Override
  public List <JBlock> blocks ()
  {
    return m_aBlocks;
  }

  /// generate the arrow and body in the formatter
  protected void stateBody (@NonNull final IJFormatter f)
  {
    stateBlock (f, getBlock ());
  }

  /// generate the arrow and body in the formatter. Checks for special cases to avoid printing
  /// one-line yield `i->yield i`
  ///
  /// This is a tooling method, public since called from two classes.
  public static void stateBlock (@NonNull final IJFormatter f, JLambdaBlock jlb)
  {
    // we let the formatter decide whether to whitespace or not the arrow
    f.print ("->");
    if (jlb.getContents ().size () == 1)
    {
      final IJObject firstContent = jlb.getContents ().get (0);
      if (firstContent instanceof final JYield jy)
      {
        printSingleStatement (f, () -> f.generable (jy.expr ()).print (";"));
      }
      else
        if (firstContent instanceof final JThrow jt)
        {
          printSingleStatement (f, () -> f.print ("throw").generable (jt.expr ()).print (';'));
        }
        else
        {
          printSingleStatement (f, () -> f.generable (jlb).print (';'));
        }
    }
    else
    {
      f.newline ();
      f.statement (jlb);
    }
  }

  protected static void printSingleStatement (@NonNull final IJFormatter f, Runnable internalPrint)
  {
    f.indent ()
     .printSingleLine (f.settings ().wrap.switchExpression.singleLine.condition, internalPrint)
     .newline ()
     .outdent ();
  }

}
