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
  public static final JAtom TRUE = new JAtom ("true");

  /**
   * Boolean constant that represents <code>false</code>
   */
  @Nonnull
  public static final JAtom FALSE = new JAtom ("false");

  private static final String CHAR_ESCAPE = "\b\t\n\f\r\"\'\\";
  private static final String CHAR_MACRO = "btnfr\"'\\";
  private static final JAtom THIS = new JAtom ("this");
  private static final JAtom SUPER = new JAtom ("super");
  private static final JAtom NULL = new JAtom ("null");

  /**
   * This class is not instanciable.
   */
  private JExpr ()
  {}

  @Nonnull
  public static JAssignment assign (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs);
  }

  @Nonnull
  public static JAssignment assignPlus (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "+");
  }

  @Nonnull
  public static JAssignment assignMinus (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "-");
  }

  @Nonnull
  public static JAssignment assignTimes (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "*");
  }

  @Nonnull
  public static JAssignment assignDivide (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "/");
  }

  @Nonnull
  public static JAssignment assignShl (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "<<");
  }

  @Nonnull
  public static JAssignment assignShr (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, ">>");
  }

  @Nonnull
  public static JAssignment assignShrz (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, ">>>");
  }

  @Nonnull
  public static JAssignment assignBand (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "&");
  }

  @Nonnull
  public static JAssignment assignXor (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "^");
  }

  @Nonnull
  public static JAssignment assignBor (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    return new JAssignment (lhs, rhs, "|");
  }

  @Nonnull
  public static JExprStatementWrapper <JOpUnaryTight> incr (@Nonnull final IJExpression expression)
  {
    return JExprStatementWrapper.create (JOp.incr (expression));
  }

  @Nonnull
  public static JExprStatementWrapper <JOpUnaryTight> preincr (@Nonnull final IJExpression expression)
  {
    return JExprStatementWrapper.create (JOp.preincr (expression));
  }

  @Nonnull
  public static JExprStatementWrapper <JOpUnaryTight> decr (@Nonnull final IJExpression expression)
  {
    return JExprStatementWrapper.create (JOp.decr (expression));
  }

  @Nonnull
  public static JExprStatementWrapper <JOpUnaryTight> predecr (@Nonnull final IJExpression expression)
  {
    return JExprStatementWrapper.create (JOp.predecr (expression));
  }

  @Nonnull
  public static JInvocation _new (@Nonnull final AbstractJClass c)
  {
    return new JInvocation (c);
  }

  @Nonnull
  public static JInvocation _new (@Nonnull final AbstractJType t)
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
  public static JFieldRef ref (@Nonnull final JVar field)
  {
    return new JFieldRef ((IJExpression) null, field);
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

  /**
   * @param field
   *        field name to reference
   * @return [this].[field]
   */
  @Nonnull
  public static JFieldRef refthis (@Nonnull final JVar field)
  {
    return new JFieldRef (null, field, true);
  }

  /**
   * @param field
   *        field name to reference
   * @return [this].[field]
   */
  @Nonnull
  public static JFieldRef refthis (@Nonnull final String field)
  {
    return new JFieldRef (null, field, true);
  }

  @Nonnull
  public static JFieldRef refthis (@Nullable final IJExpression lhs, @Nonnull final JVar field)
  {
    return new JFieldRef (lhs, field, true);
  }

  @Nonnull
  public static JFieldRef refthis (@Nullable final IJExpression lhs, @Nonnull final String field)
  {
    return new JFieldRef (lhs, field, true);
  }

  @Nonnull
  public static AbstractJExpressionImpl dotclass (@Nonnull final AbstractJClass cl)
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
  public static JAtom _this ()
  {
    return THIS;
  }

  /**
   * Returns a reference to "super", an implicit reference to the super class.
   */
  @Nonnull
  public static JAtom _super ()
  {
    return SUPER;
  }

  /* -- Literals -- */

  @Nonnull
  public static JAtom _null ()
  {
    return NULL;
  }

  @Nonnull
  public static JAtom lit (final boolean b)
  {
    return b ? TRUE : FALSE;
  }

  @Nonnull
  public static JAtomInt lit (final int n)
  {
    return new JAtomInt (n);
  }

  @Nonnull
  public static JAtomLong lit (final long n)
  {
    return new JAtomLong (n);
  }

  @Nonnull
  public static JAtomFloat lit (final float f)
  {
    return new JAtomFloat (f);
  }

  @Nonnull
  public static JAtomDouble lit (final double d)
  {
    return new JAtomDouble (d);
  }

  /**
   * Escapes the given string, then surrounds it by the specified quotation
   * mark.
   */
  @Nonnull
  public static String quotify (final char quote, @Nonnull final String s)
  {
    final int n = s.length ();
    final StringBuilder sb = new StringBuilder (n + 2);
    sb.append (quote);
    for (int i = 0; i < n; i++)
    {
      final char c = s.charAt (i);
      final int j = CHAR_ESCAPE.indexOf (c);
      if (j >= 0)
      {
        if ((quote == '"' && c == '\'') || (quote == '\'' && c == '"'))
        {
          sb.append (c);
        }
        else
        {
          sb.append ('\\');
          sb.append (CHAR_MACRO.charAt (j));
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
  public static JAtom lit (final char c)
  {
    return new JAtom (quotify ('\'', Character.toString (c)));
  }

  @Nonnull
  public static JStringLiteral lit (@Nonnull final String s)
  {
    return new JStringLiteral (s);
  }

  /**
   * Creates an expression directly from a source code fragment.
   * <p>
   * This method can be used as a short-cut to create a JExpression. For
   * example, instead of <code>_a.gt(_b)</code>, you can write it as:
   * <code>JExpr.direct("a&gt;b")</code>.
   * <p>
   * Be warned that there is a danger in using this method, as it obfuscates the
   * object model.
   */
  @Nonnull
  public static AbstractJExpressionImpl direct (@Nonnull final String source)
  {
    return new AbstractJExpressionImpl ()
    {
      public void generate (final JFormatter f)
      {
        f.print ('(').print (source).print (')');
      }
    };
  }

  /**
   * Just a sanity wrapper around
   * {@link JOp#cond(IJExpression, IJExpression, IJExpression)} for easier
   * finding.
   *
   * @param cond
   *        Condition
   * @param ifTrue
   *        True condition
   * @param ifFalse
   *        False condition
   * @return The created expression
   */
  @Nonnull
  public static JOpTernary cond (@Nonnull final IJExpression cond,
                                 @Nonnull final IJExpression ifTrue,
                                 @Nonnull final IJExpression ifFalse)
  {
    return JOp.cond (cond, ifTrue, ifFalse);
  }
}
