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

/**
 * Things that can be values of an annotation element.
 *
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public abstract class AbstractJAnnotationValue implements IJGenerable
{
  @Nonnull
  public static JAnnotationStringValue wrap (final boolean value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Boolean.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final byte value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Byte.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final char value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Character.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final double value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Double.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final float value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Float.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final int value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Integer.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final long value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Long.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (final short value)
  {
    return new JAnnotationStringValue (JExpr.lit (value), Short.valueOf (value));
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final String value)
  {
    // Escape string values with quotes so that they can
    // be generated accordingly
    return new JAnnotationStringValue (JExpr.lit (value), value);
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final JEnumConstant value)
  {
    return new JAnnotationStringValue (value);
  }

  @Nonnull
  public static JAnnotationStringValue wrap (@Nonnull final AbstractJType type)
  {
    return new JAnnotationStringValue (type.boxify ().dotclass (), type);
  }

  @Nonnull
  @Deprecated
  public static JAnnotationStringValue wrap (@Nonnull final IJExpression expr)
  {
    return new JAnnotationStringValue (expr);
  }
}
