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

import javax.annotation.Nonnull;

/**
 * Things that can be values of an annotation element.
 *
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public abstract class AbstractJAnnotationValue implements IJGenerable
{
  @Nonnull
  public static JAnnotationStringValue wrap (final boolean bValue)
  {
    return new JAnnotationStringValue (JExpr.lit (bValue), Boolean.valueOf (bValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final byte nValue)
  {
    return new JAnnotationStringValue (JExpr.lit (nValue), Byte.valueOf (nValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final char cValue)
  {
    return new JAnnotationStringValue (JExpr.lit (cValue), Character.valueOf (cValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final double dValue)
  {
    return new JAnnotationStringValue (JExpr.lit (dValue), Double.valueOf (dValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final float fValue)
  {
    return new JAnnotationStringValue (JExpr.lit (fValue), Float.valueOf (fValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final int nValue)
  {
    return new JAnnotationStringValue (JExpr.lit (nValue), Integer.valueOf (nValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final long nValue)
  {
    return new JAnnotationStringValue (JExpr.lit (nValue), Long.valueOf (nValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final short nValue)
  {
    return new JAnnotationStringValue (JExpr.lit (nValue), Short.valueOf (nValue));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final String sValue)
  {
    // Escape string values with quotes so that they can
    // be generated accordingly
    return new JAnnotationStringValue (JExpr.lit (sValue), sValue);
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final JEnumConstant aValue)
  {
    return new JAnnotationStringValue (aValue);
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final AbstractJType aType)
  {
    return new JAnnotationStringValue (aType.boxify ().dotclass (), aType);
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final IJExpression aExpr)
  {
    return new JAnnotationStringValue (aExpr);
  }
}
