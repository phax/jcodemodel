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

/**
 * JClass for generating expressions containing operators
 */

public final class JOp
{
  private JOp ()
  {}

  /**
   * Determine whether the top level of an expression involves an operator.
   */
  static boolean hasTopOp (final JExpression e)
  {
    return (e instanceof UnaryOp) || (e instanceof BinaryOp);
  }

  /* -- Unary operators -- */

  public static class UnaryOp extends AbstractJExpressionImpl
  {
    private final String op;
    private final JExpression e;
    private final boolean opFirst;

    protected UnaryOp (final String op, final JExpression e)
    {
      this.op = op;
      this.e = e;
      opFirst = false;
    }

    protected UnaryOp (final JExpression e, final String op)
    {
      this.op = op;
      this.e = e;
      opFirst = false;
    }

    public String op ()
    {
      return op;
    }

    public JExpression expr ()
    {
      return e;
    }

    public boolean opFirst ()
    {
      return opFirst;
    }

    public void generate (final JFormatter f)
    {
      if (opFirst)
        f.p ('(').p (op).g (e).p (')');
      else
        f.p ('(').g (e).p (op).p (')');
    }

  }

  public static JExpression minus (final JExpression e)
  {
    return new UnaryOp ("-", e);
  }

  /**
   * Logical not <tt>'!x'</tt>.
   */
  public static JExpression not (final JExpression e)
  {
    if (e == JExpr.TRUE)
      return JExpr.FALSE;
    if (e == JExpr.FALSE)
      return JExpr.TRUE;
    return new UnaryOp ("!", e);
  }

  public static JExpression complement (final JExpression e)
  {
    return new UnaryOp ("~", e);
  }

  public static class TightUnaryOp extends UnaryOp
  {
    protected TightUnaryOp (final JExpression e, final String op)
    {
      super (e, op);
    }

    @Override
    public void generate (final JFormatter f)
    {
      if (opFirst ())
        f.p (op ()).g (expr ());
      else
        f.g (expr ()).p (op ());
    }
  }

  public static JExpression incr (final JExpression e)
  {
    return new TightUnaryOp (e, "++");
  }

  public static JExpression decr (final JExpression e)
  {
    return new TightUnaryOp (e, "--");
  }

  /* -- Binary operators -- */

  public static class BinaryOp extends AbstractJExpressionImpl
  {
    private final JExpression left;
    private final String op;
    private final JGenerable right;

    protected BinaryOp (final JExpression left, final String op, final JGenerable right)
    {
      this.left = left;
      this.op = op;
      this.right = right;
    }

    public JExpression left ()
    {
      return left;
    }

    public String op ()
    {
      return op;
    }

    public JGenerable right ()
    {
      return right;
    }

    public void generate (final JFormatter f)
    {
      f.p ('(').g (left).p (op).g (right).p (')');
    }

  }

  public static JExpression plus (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "+", right);
  }

  public static JExpression minus (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "-", right);
  }

  public static JExpression mul (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "*", right);
  }

  public static JExpression div (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "/", right);
  }

  public static JExpression mod (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "%", right);
  }

  public static JExpression shl (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "<<", right);
  }

  public static JExpression shr (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, ">>", right);
  }

  public static JExpression shrz (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, ">>>", right);
  }

  public static JExpression band (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "&", right);
  }

  public static JExpression bor (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "|", right);
  }

  public static JExpression cand (final JExpression left, final JExpression right)
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

  public static JExpression cor (final JExpression left, final JExpression right)
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

  public static JExpression xor (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "^", right);
  }

  public static JExpression lt (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "<", right);
  }

  public static JExpression lte (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "<=", right);
  }

  public static JExpression gt (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, ">", right);
  }

  public static JExpression gte (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, ">=", right);
  }

  public static JExpression eq (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "==", right);
  }

  public static JExpression ne (final JExpression left, final JExpression right)
  {
    return new BinaryOp (left, "!=", right);
  }

  public static JExpression _instanceof (final JExpression left, final AbstractJType right)
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

    protected TernaryOp (final JExpression e1,
                         final String op1,
                         final JExpression e2,
                         final String op2,
                         final JExpression e3)
    {
      this.e1 = e1;
      this.op1 = op1;
      this.e2 = e2;
      this.op2 = op2;
      this.e3 = e3;
    }

    public JExpression expr1 ()
    {
      return e1;
    }

    public String op1 ()
    {
      return op1;
    }

    public JGenerable expr2 ()
    {
      return e2;
    }

    public String op2 ()
    {
      return op2;
    }

    public JGenerable expr3 ()
    {
      return e3;
    }

    public void generate (final JFormatter f)
    {
      f.p ('(').g (e1).p (op1).g (e2).p (op2).g (e3).p (')');
    }

  }

  public static JExpression cond (final JExpression cond, final JExpression ifTrue, final JExpression ifFalse)
  {
    return new TernaryOp (cond, "?", ifTrue, ":", ifFalse);
  }
}
