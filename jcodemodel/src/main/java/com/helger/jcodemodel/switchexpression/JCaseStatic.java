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

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JEnumConstant;
import com.helger.jcodemodel.JEnumConstantRef;
import com.helger.jcodemodel.JSwitchExpression;

///
/// Switch case using static value(s) for selection
/// ```
/// case 1,3 ->
/// case MyEnum.OPT1 ->
/// ```
public class JCaseStatic extends JCaseArrow <JCaseStatic>
{
  private final List <IJExpression> m_aLabels;

  public JCaseStatic (@NonNull final JSwitchExpression parent, final IJExpression aLabel)
  {
    super (parent);
    m_aLabels = new ArrayList <> (List.of (aLabel));
  }

  /// add a label to the list of existing ones
  public JCaseStatic or (final IJExpression aLabel)
  {
    m_aLabels.add (aLabel);
    return this;
  }

  /// alias for [#or]
  public JCaseStatic _case (final IJExpression aLabel)
  {
    return or (aLabel);
  }

  /// copy of [JCase]
  @Override
  public void state (@NonNull final IJFormatter f)
  {
    f.indent ();
    f.print ("case ");
    boolean first = true;
    for (final IJExpression aLabel : m_aLabels)
    {
      final IJExpression aLabelName;
      // Hack for #41 :)
      if (aLabel instanceof final JEnumConstant jec)
      {
        // Just use the name, but not the type of the enum
        aLabelName = f1 -> f1.print (jec.name ());
      }
      else
        if (aLabel instanceof final JEnumConstantRef jecr)
        {
          // Just use the name, but not the type of the enum
          aLabelName = f1 -> f1.print (jecr.name ());
        }
        else
        {
          aLabelName = aLabel;
        }

      if (!first)
      {
        f.print (", ");
      }
      f.generable (aLabelName);
      first = false;
    }
    stateBody (f);
    f.outdent ();
  }

}
