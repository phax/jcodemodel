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
  public static JAssignment assign (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs);
  }

  @Nonnull
  public static JAssignment assignPlus (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "+");
  }

  @Nonnull
  public static JAssignment assignMinus (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "-");
  }

  @Nonnull
  public static JAssignment assignTimes (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "*");
  }

  @Nonnull
  public static JAssignment assignDivide (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "/");
  }

  @Nonnull
  public static JAssignment assignShl (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "<<");
  }

  @Nonnull
  public static JAssignment assignShr (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, ">>");
  }

  @Nonnull
  public static JAssignment assignShrz (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, ">>>");
  }

  @Nonnull
  public static JAssignment assignBand (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "&");
  }

  @Nonnull
  public static JAssignment assignXor (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "^");
  }

  @Nonnull
  public static JAssignment assignBor (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JAssignment (aLhs, aRhs, "|");
  }

  @Nonnull
  public static JOpUnaryTight incr (@Nonnull final IJExpression aExpr)
  {
    return JOp.postincr (aExpr);
  }

  @Nonnull
  public static JOpUnaryTight preincr (@Nonnull final IJExpression aExpr)
  {
    return JOp.preincr (aExpr);
  }

  @Nonnull
  public static JOpUnaryTight decr (@Nonnull final IJExpression aExpr)
  {
    return JOp.postdecr (aExpr);
  }

  @Nonnull
  public static JOpUnaryTight predecr (@Nonnull final IJExpression aExpr)
  {
    return JOp.predecr (aExpr);
  }

  @Nonnull
  public static JInvocation _new (@Nonnull final AbstractJClass aClass)
  {
    return new JInvocation (aClass);
  }

  @Nonnull
  public static JInvocation _new (@Nonnull final AbstractJType aType)
  {
    return new JInvocation (aType);
  }

  @Nonnull
  public static JInvocation invoke (@Nonnull final String sMethod)
  {
    return invoke (null, sMethod);
  }

  @Nonnull
  public static JInvocation invoke (@Nonnull final JMethod aMethod)
  {
    return invoke (null, aMethod);
  }

  @Nonnull
  public static JInvocation invoke (@Nullable final IJExpression aLhs, @Nonnull final JMethod aMethod)
  {
    return new JInvocation (aLhs, aMethod);
  }

  @Nonnull
  public static JInvocation invoke (@Nullable final IJExpression aLhs, @Nonnull final String sMethod)
  {
    return new JInvocation (aLhs, sMethod);
  }

  @Nonnull
  public static JFieldRef ref (@Nonnull final JVar aField)
  {
    return ref (null, aField);
  }

  @Nonnull
  public static JFieldRef ref (@Nonnull final String sField)
  {
    return ref (null, sField);
  }

  @Nonnull
  public static JFieldRef ref (@Nullable final IJExpression aLhs, @Nonnull final JVar aField)
  {
    return new JFieldRef (aLhs, aField);
  }

  @Nonnull
  public static JFieldRef ref (@Nullable final IJExpression aLhs, @Nonnull final String sField)
  {
    return new JFieldRef (aLhs, sField);
  }

  @Nonnull
  public static JEnumConstantRef enumConstantRef (@Nonnull final AbstractJClass aType, @Nonnull final String sName)
  {
    return new JEnumConstantRef (aType, sName);
  }

  /**
   * @param aField
   *        field name to reference
   * @return [this].[field]
   */
  @Nonnull
  public static JFieldRef refthis (@Nonnull final JVar aField)
  {
    return new JFieldRef (null, aField, true);
  }

  /**
   * @param sField
   *        field name to reference
   * @return [this].[field]
   */
  @Nonnull
  public static JFieldRef refthis (@Nonnull final String sField)
  {
    return new JFieldRef (null, sField, true);
  }

  @Nonnull
  public static JFieldRef refthis (@Nullable final IJExpression aLhs, @Nonnull final JVar aField)
  {
    return new JFieldRef (aLhs, aField, true);
  }

  @Nonnull
  public static JFieldRef refthis (@Nullable final IJExpression aLhs, @Nonnull final String sField)
  {
    return new JFieldRef (aLhs, sField, true);
  }

  @Nonnull
  public static IJExpression dotclass (@Nonnull final AbstractJClass aClass)
  {
    return (@Nonnull final JFormatter f) -> {
      final AbstractJClass c = aClass instanceof JNarrowedClass ? ((JNarrowedClass) aClass).basis () : aClass;
      f.generable (c).print (".class");
    };
  }

  @Nonnull
  public static JArrayCompRef component (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aIndex)
  {
    return new JArrayCompRef (aLhs, aIndex);
  }

  @Nonnull
  public static JCast cast (@Nonnull final AbstractJType aType, @Nonnull final IJExpression aExpr)
  {
    return new JCast (aType, aExpr);
  }

  @Nonnull
  public static JArray newArray (@Nonnull final AbstractJType aType)
  {
    return newArray (aType, null);
  }

  /**
   * Generates {@code new T[size]}.
   *
   * @param aType
   *        The aType of the array component. 'T' or {@code new T[size]}.
   * @param aSize
   *        Size of the array
   * @return New {@link JArray}
   */
  @Nonnull
  public static JArray newArray (@Nonnull final AbstractJType aType, @Nullable final IJExpression aSize)
  {
    // you cannot create an array whose component aType is a generic
    return new JArray (aType.erasure (), aSize);
  }

  /**
   * Generates {@code new T[size]}.
   *
   * @param aType
   *        The aType of the array component. 'T' or {@code new T[size]}.
   * @param nSize
   *        Size of the array
   * @return New {@link JArray}
   */
  @Nonnull
  public static JArray newArray (@Nonnull final AbstractJType aType, @Nonnegative final int nSize)
  {
    return newArray (aType, lit (nSize));
  }

  /**
   * @return a reference to "this", an implicit reference to the current object.
   */
  @Nonnull
  public static JAtom _this ()
  {
    return THIS;
  }

  /**
   * @return a reference to "super", an implicit reference to the super class.
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
   *
   * @param cQuote
   *        Quote char. Either single quote (') or double quote (")
   * @param sStr
   *        Source string to quote
   * @return Qutoed string
   */
  @Nonnull
  public static String quotify (final char cQuote, @Nonnull final String sStr)
  {
    final int n = sStr.length ();
    final StringBuilder sb = new StringBuilder (n + 2);
    sb.append (cQuote);
    for (int i = 0; i < n; i++)
    {
      final char c = sStr.charAt (i);
      final int j = CHAR_ESCAPE.indexOf (c);
      if (j >= 0)
      {
        if ((cQuote == '"' && c == '\'') || (cQuote == '\'' && c == '"'))
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
        if (c < 0x20 || c > 0x7E)
        {
          // not printable. use Unicode escape
          sb.append ("\\u");
          final String hex = Integer.toHexString (c & 0xFFFF);
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
    sb.append (cQuote);
    return sb.toString ();
  }

  @Nonnull
  public static JAtom lit (final char c)
  {
    return new JAtom (quotify ('\'', Character.toString (c)));
  }

  @Nonnull
  public static JStringLiteral lit (@Nonnull final String sStr)
  {
    return new JStringLiteral (sStr);
  }

  /**
   * Creates an aExpr directly from a source code fragment.
   * <p>
   * This method can be used as a short-cut to create a JExpression. For
   * example, instead of <code>_a.gt(_b)</code>, you can write it as:
   * <code>JExpr.direct("a&gt;b")</code>.
   * <p>
   * Be warned that there is a danger in using this method, as it obfuscates the
   * object model.
   *
   * @param sSourceCode
   *        Java source code
   * @return Direct aExpr
   */
  @Nonnull
  public static IJExpression direct (@Nonnull final String sSourceCode)
  {
    return f -> f.print ('(').print (sSourceCode).print (')');
  }

  /**
   * Just a sanity wrapper around
   * {@link JOp#cond(IJExpression, IJExpression, IJExpression)} for easier
   * finding.
   *
   * @param aCond
   *        Condition
   * @param aIfTrue
   *        True condition
   * @param aIfFalse
   *        False condition
   * @return The created aExpr
   */
  @Nonnull
  public static JOpTernary cond (@Nonnull final IJExpression aCond,
                                 @Nonnull final IJExpression aIfTrue,
                                 @Nonnull final IJExpression aIfFalse)
  {
    return JOp.cond (aCond, aIfTrue, aIfFalse);
  }
}
