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
package com.helger.jcodemodel.util;

import java.util.Comparator;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JErrorClass;

/**
 * Comparator object that sorts {@link AbstractJClass}es in the order of their
 * names.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class ClassNameComparator implements Comparator <AbstractJClass>
{
  private static final ClassNameComparator s_aInstance = new ClassNameComparator ();

  private ClassNameComparator ()
  {}

  @Nonnull
  public static ClassNameComparator getInstance ()
  {
    return s_aInstance;
  }

  /**
   * Compare two JTypes by FQCN, giving sorting precedence to types that belong
   * to packages java and javax over all others. This method is used to sort
   * generated import statments in a conventional way for readability.
   */
  public int compare (@Nonnull final AbstractJClass left, @Nonnull final AbstractJClass right)
  {
    if (left.isError () && right.isError ())
      return 0;
    if (left.isError ())
      return -1;
    if (right.isError ())
      return +1;

    final String lhs = left.fullName ();
    final String rhs = right.fullName ();
    final boolean bLeftJava = lhs.startsWith ("java");
    final boolean bRightJava = rhs.startsWith ("java");

    if (bLeftJava && !bRightJava)
      return -1;
    if (!bLeftJava && bRightJava)
      return +1;
    return lhs.compareTo (rhs);
  }
}
