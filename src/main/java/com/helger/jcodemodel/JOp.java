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
import javax.annotation.concurrent.Immutable;

/**
 * JClass for generating expressions containing operators
 */
@Immutable
public final class JOp
{
  private JOp ()
  {}

  /**
   * Determine whether the top level of an expression involves an operator.
   */
  static boolean hasTopOp (@Nullable final JExpression e)
  {
    return (e instanceof UnaryOp) || (e instanceof BinaryOp);
  }

  /* -- Unary operators -- */

  public static class UnaryOp extends AbstractJExpressionImpl
  {
    private final String op;
    private final JExpression e;
    private final boolean opFirst;

    protected UnaryOp (@Nonnull final String op, @Nonnull final JExpression e)
    {
      this.op = op;
      this.e = e;
      opFirst = false;
    }

    protected UnaryOp (@Nonnull final JExpression e, @Nonnull final String op)
    {
      this.op = op;
      this.e = e;
      opFirst = false;
    }

    @Nonnull
    public String op ()
    {
      return op;
    }

    @Nonnull
    public JExpression expr ()
    {
      return e;
    }

    public boolean opFirst ()
    {
      return opFirst;
    }

    public void generate (@Nonnull final JFormatter f)
    {
      if (opFirst)
        f.print ('(').print (op).generable (e).print (')');
      else
        f.print ('(').generable (e).print (op).print (')');
    }

  }

  @Nonnull
  public static JExpression minus (@Nonnull final JExpression e)
  {
    return new UnaryOp ("-", e);
  }

  /**
   * Logical not <tt>'!x'</tt>.
   */
  @Nonnull
  public static JExpression not (@Nonnull final JExpression e)
  {
    if (e == JExpr.TRUE)
      return JExpr.FALSE;
    if (e == JExpr.FALSE)
      return JExpr.TRUE;
    return new UnaryOp ("!", e);
  }

  @Nonnull
  public static JExpression complement (@Nonnull final JExpression e)
  {
    return new UnaryOp ("~", e);
  }

  public static class TightUnaryOp extends UnaryOp
  {
    protected TightUnaryOp (@Nonnull final JExpression e, @Nonnull final String op)
    {
      super (e, op);
    }

    @Override
    public void generate (@Nonnull final JFormatter f)
    {
      if (opFirst ())
        f.print (op ()).generable (expr ());
      else
        f.generable (expr ()).print (op ());
    }
  }

  @Nonnull
  public static JExpression incr (@Nonnull final JExpression e)
  {
    return new TightUnaryOp (e, "++");
  }

  @Nonnull
  public static JExpression decr (@Nonnull final JExpression e)
  {
    return new TightUnaryOp (e, "--");
  }

  /* -- Binary operators -- */

  public static class BinaryOp extends AbstractJExpressionImpl
  {
    private final JExpression left;
    private final String op;
    private final JGenerable right;

    protected BinaryOp (@Nonnull final JExpression left, @Nonnull final String op, @Nonnull final JGenerable right)
    {
      this.left = left;
      this.op = op;
      this.right = right;
    }

    @Nonnull
    public JExpression left ()
    {
      return left;
    }

    @Nonnull
    public String op ()
    {
      return op;
    }

    @Nonnull
    public JGenerable right ()
    {
      return right;
    }

    public void generate (@Nonnull final JFormatter f)
    {
      f.print ('(').generable (left).print (op).generable (right).print (')');
    }

  }

  @Nonnull
  public static JExpression plus (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "+", right);
  }

  @Nonnull
  public static JExpression minus (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "-", right);
  }

  @Nonnull
  public static JExpression mul (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "*", right);
  }

  @Nonnull
  public static JExpression div (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "/", right);
  }

  @Nonnull
  public static JExpression mod (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "%", right);
  }

  @Nonnull
  public static JExpression shl (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "<<", right);
  }

  @Nonnull
  public static JExpression shr (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, ">>", right);
  }

  @Nonnull
  public static JExpression shrz (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, ">>>", right);
  }

  @Nonnull
  public static JExpression band (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "&", right);
  }

  @Nonnull
  public static JExpression bor (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "|", right);
  }

  @Nonnull
  public static JExpression cand (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    if (left == JExpr.TRUE)
      return right;
    if (right == JExpr.TRUE)
      return left;
    if (left == JExpr.FALSE)
      return left; // JExpr.FALSE
    if (right == JExpr.FALSE)
      return right; // JExpr.FALSE
    return new BinaryOp (left, "&&", right);
  }

  @Nonnull
  public static JExpression cor (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    if (left == JExpr.TRUE)
      return left; // JExpr.TRUE
    if (right == JExpr.TRUE)
      return right; // JExpr.FALSE
    if (left == JExpr.FALSE)
      return right;
    if (right == JExpr.FALSE)
      return left;
    return new BinaryOp (left, "||", right);
  }

  @Nonnull
  public static JExpression xor (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "^", right);
  }

  @Nonnull
  public static JExpression lt (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "<", right);
  }

  @Nonnull
  public static JExpression lte (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "<=", right);
  }

  @Nonnull
  public static JExpression gt (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, ">", right);
  }

  @Nonnull
  public static JExpression gte (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, ">=", right);
  }

  @Nonnull
  public static JExpression eq (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "==", right);
  }

  @Nonnull
  public static JExpression ne (@Nonnull final JExpression left, @Nonnull final JExpression right)
  {
    return new BinaryOp (left, "!=", right);
  }

  @Nonnull
  public static JExpression _instanceof (@Nonnull final JExpression left, @Nonnull final AbstractJType right)
  {
    return new BinaryOp (left, "instanceof", right);
  }

  /* -- Ternary operators -- */

  public static class TernaryOp extends AbstractJExpressionImpl
  {
    private final JExpression e1;
    private final String op1;
    private final JExpression e2;
    private final String op2;
    private final JExpression e3;

    protected TernaryOp (@Nonnull final JExpression e1,
                         @Nonnull final String op1,
                         @Nonnull final JExpression e2,
                         @Nonnull final String op2,
                         @Nonnull final JExpression e3)
    {
      this.e1 = e1;
      this.op1 = op1;
      this.e2 = e2;
      this.op2 = op2;
      this.e3 = e3;
    }

    @Nonnull
    public JExpression expr1 ()
    {
      return e1;
    }

    @Nonnull
    public String op1 ()
    {
      return op1;
    }

    @Nonnull
    public JGenerable expr2 ()
    {
      return e2;
    }

    @Nonnull
    public String op2 ()
    {
      return op2;
    }

    @Nonnull
    public JGenerable expr3 ()
    {
      return e3;
    }

    public void generate (@Nonnull final JFormatter f)
    {
      f.print ('(').generable (e1).print (op1).generable (e2).print (op2).generable (e3).print (')');
    }

  }

  @Nonnull
  public static JExpression cond (@Nonnull final JExpression cond,
                                  @Nonnull final JExpression ifTrue,
                                  @Nonnull final JExpression ifFalse)
  {
    return new TernaryOp (cond, "?", ifTrue, ":", ifFalse);
  }
}
