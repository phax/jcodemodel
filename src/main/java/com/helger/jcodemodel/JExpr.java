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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Factory methods that generate various {@link IJExpression}s.
 */
public final class JExpr
{
  /**
   * Boolean constant that represents <code>true</code>
   */
  @Nonnull
  public static final IJExpression TRUE = new JAtom ("true");

  /**
   * Boolean constant that represents <code>false</code>
   */
  @Nonnull
  public static final IJExpression FALSE = new JAtom ("false");

  private static final String charEscape = "\b\t\n\f\r\"\'\\";
  private static final String charMacro = "btnfr\"'\\";
  private static final IJExpression __this = new JAtom ("this");
  private static final IJExpression __super = new JAtom ("super");
  private static final IJExpression __null = new JAtom ("null");

  /**
   * This class is not instanciable.
   */
  private JExpr ()
  {}

  @Nonnull
  public static IJExpressionStatement assign (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs);
  }

  @Nonnull
  public static IJExpressionStatement assignPlus (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "+");
  }

  @Nonnull
  public static IJExpressionStatement assignMinus (@Nonnull final IJAssignmentTarget lhs,
                                                   @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "-");
  }

  @Nonnull
  public static IJExpressionStatement assignTimes (@Nonnull final IJAssignmentTarget lhs,
                                                   @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "*");
  }

  @Nonnull
  public static IJExpressionStatement assignDivide (@Nonnull final IJAssignmentTarget lhs,
                                                    @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "/");
  }

  @Nonnull
  public static IJExpressionStatement incr (@Nonnull final IJExpression expression)
  {
    return new JExpressionStatementWrapper (JOp.incr (expression));
  }

  @Nonnull
  public static IJStatement preincr (@Nonnull final IJExpression expression)
  {
    return new JExpressionStatementWrapper (JOp.preincr (expression));
  }

  @Nonnull
  public static IJStatement decr (@Nonnull final IJExpression expression)
  {
    return new JExpressionStatementWrapper (JOp.decr (expression));
  }

  @Nonnull
  public static IJStatement predecr (@Nonnull final IJExpression expression)
  {
    return new JExpressionStatementWrapper (JOp.predecr (expression));
  }

  @Nonnull
  public static JInvocation _new (@Nonnull final AbstractJClass c)
  {
    return new JInvocation (c);
  }

  @Nonnull
  public static JInvocation _new (final AbstractJType t)
  {
    return new JInvocation (t);
  }

  @Nonnull
  public static JInvocation invoke (@Nonnull final String method)
  {
    return new JInvocation ((IJExpression) null, method);
  }

  @Nonnull
  public static JInvocation invoke (@Nonnull final JMethod method)
  {
    return new JInvocation ((IJExpression) null, method);
  }

  @Nonnull
  public static JInvocation invoke (@Nullable final IJExpression lhs, @Nonnull final JMethod method)
  {
    return new JInvocation (lhs, method);
  }

  @Nonnull
  public static JInvocation invoke (@Nullable final IJExpression lhs, @Nonnull final String method)
  {
    return new JInvocation (lhs, method);
  }

  @Nonnull
  public static JFieldRef ref (@Nonnull final String field)
  {
    return new JFieldRef ((IJExpression) null, field);
  }

  @Nonnull
  public static JFieldRef ref (@Nullable final IJExpression lhs, @Nonnull final JVar field)
  {
    return new JFieldRef (lhs, field);
  }

  @Nonnull
  public static JFieldRef ref (@Nullable final IJExpression lhs, @Nonnull final String field)
  {
    return new JFieldRef (lhs, field);
  }

  @Nonnull
  public static JFieldRef refthis (@Nonnull final String field)
  {
    return new JFieldRef (null, field, true);
  }

  @Nonnull
  public static IJExpression dotclass (@Nonnull final AbstractJClass cl)
  {
    return new AbstractJExpressionImpl ()
    {
      public void generate (@Nonnull final JFormatter f)
      {
        AbstractJClass c;
        if (cl instanceof JNarrowedClass)
          c = ((JNarrowedClass) cl).basis ();
        else
          c = cl;
        f.generable (c).print (".class");
      }
    };
  }

  @Nonnull
  public static JArrayCompRef component (@Nonnull final IJExpression lhs, @Nonnull final IJExpression index)
  {
    return new JArrayCompRef (lhs, index);
  }

  @Nonnull
  public static JCast cast (@Nonnull final AbstractJType type, @Nonnull final IJExpression expr)
  {
    return new JCast (type, expr);
  }

  @Nonnull
  public static JArray newArray (@Nonnull final AbstractJType type)
  {
    return newArray (type, null);
  }

  /**
   * Generates {@code new T[size]}.
   *
   * @param type
   *        The type of the array component. 'T' or {@code new T[size]}.
   */
  @Nonnull
  public static JArray newArray (@Nonnull final AbstractJType type, @Nullable final IJExpression size)
  {
    // you cannot create an array whose component type is a generic
    return new JArray (type.erasure (), size);
  }

  /**
   * Generates {@code new T[size]}.
   *
   * @param type
   *        The type of the array component. 'T' or {@code new T[size]}.
   */
  @Nonnull
  public static JArray newArray (@Nonnull final AbstractJType type, @Nonnegative final int size)
  {
    return newArray (type, lit (size));
  }

  /**
   * Returns a reference to "this", an implicit reference to the current object.
   */
  @Nonnull
  public static IJExpression _this ()
  {
    return __this;
  }

  /**
   * Returns a reference to "super", an implicit reference to the super class.
   */
  @Nonnull
  public static IJExpression _super ()
  {
    return __super;
  }

  /* -- Literals -- */

  @Nonnull
  public static IJExpression _null ()
  {
    return __null;
  }

  @Nonnull
  public static IJExpression lit (final boolean b)
  {
    return b ? TRUE : FALSE;
  }

  @Nonnull
  public static IJExpression lit (final int n)
  {
    return new JAtomInt (n);
  }

  @Nonnull
  public static IJExpression lit (final long n)
  {
    return new JAtomLong (n);
  }

  @Nonnull
  public static IJExpression lit (final float f)
  {
    return new JAtomFloat (f);
  }

  @Nonnull
  public static IJExpression lit (final double d)
  {
    return new JAtomDouble (d);
  }

  /**
   * Escapes the given string, then surrounds it by the specified quotation
   * mark.
   */
  public static String quotify (final char quote, @Nonnull final String s)
  {
    final int n = s.length ();
    final StringBuilder sb = new StringBuilder (n + 2);
    sb.append (quote);
    for (int i = 0; i < n; i++)
    {
      final char c = s.charAt (i);
      final int j = charEscape.indexOf (c);
      if (j >= 0)
      {
        if ((quote == '"' && c == '\'') || (quote == '\'' && c == '"'))
        {
          sb.append (c);
        }
        else
        {
          sb.append ('\\');
          sb.append (charMacro.charAt (j));
        }
      }
      else
      {
        // technically Unicode escape shouldn't be done here,
        // for it's a lexical level handling.
        //
        // However, various tools are so broken around this area,
        // so just to be on the safe side, it's better to do
        // the escaping here (regardless of the actual file encoding)
        //
        // see bug
        if (c < 0x20 || 0x7E < c)
        {
          // not printable. use Unicode escape
          sb.append ("\\u");
          final String hex = Integer.toHexString ((c) & 0xFFFF);
          for (int k = hex.length (); k < 4; k++)
            sb.append ('0');
          sb.append (hex);
        }
        else
        {
          sb.append (c);
        }
      }
    }
    sb.append (quote);
    return sb.toString ();
  }

  @Nonnull
  public static IJExpression lit (final char c)
  {
    return new JAtom (quotify ('\'', Character.toString (c)));
  }

  @Nonnull
  public static IJExpression lit (@Nonnull final String s)
  {
    return new JStringLiteral (s);
  }

  /**
   * Creates an expression directly from a source code fragment.
   * <p>
   * This method can be used as a short-cut to create a JExpression. For
   * example, instead of <code>_a.gt(_b)</code>, you can write it as:
   * <code>JExpr.direct("a>b")</code>.
   * <p>
   * Be warned that there is a danger in using this method, as it obfuscates the
   * object model.
   */
  @Nonnull
  public static IJExpression direct (@Nonnull final String source)
  {
    return new AbstractJExpressionImpl ()
    {
      public void generate (final JFormatter f)
      {
        f.print ('(').print (source).print (')');
      }
    };
  }

  public static class JExpressionStatementWrapper extends AbstractJExpressionImpl implements IJExpressionStatement
  {
    final IJExpression expr;

    public JExpressionStatementWrapper (@Nonnull final IJExpression expression)
    {
      this.expr = expression;
    }

    @Nonnull
    public IJExpression expr ()
    {
      return expr;
    }

    public void generate (@Nonnull final JFormatter f)
    {
      expr.generate (f);
    }

    public void state (@Nonnull final JFormatter f)
    {
      f.generable (expr).print (';').newline ();
    }
  }
}
