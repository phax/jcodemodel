package com.helger.jcodemodel;

import javax.annotation.Nonnull;

public class JOpTernary extends AbstractJExpressionImpl
{
  private final IJExpression _e1;
  private final String _op1;
  private final IJExpression _e2;
  private final String _op2;
  private final IJExpression _e3;

  protected JOpTernary (@Nonnull final IJExpression e1,
                        @Nonnull final String op1,
                        @Nonnull final IJExpression e2,
                        @Nonnull final String op2,
                        @Nonnull final IJExpression e3)
  {
    this._e1 = e1;
    this._op1 = op1;
    this._e2 = e2;
    this._op2 = op2;
    this._e3 = e3;
  }

  @Nonnull
  public IJExpression expr1 ()
  {
    return _e1;
  }

  @Nonnull
  public String op1 ()
  {
    return _op1;
  }

  @Nonnull
  public IJGenerable expr2 ()
  {
    return _e2;
  }

  @Nonnull
  public String op2 ()
  {
    return _op2;
  }

  @Nonnull
  public IJGenerable expr3 ()
  {
    return _e3;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print ('(').generable (_e1).print (_op1).generable (_e2).print (_op2).generable (_e3).print (')');
  }
}
