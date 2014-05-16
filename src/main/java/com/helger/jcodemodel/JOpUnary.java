package com.helger.jcodemodel;

import javax.annotation.Nonnull;

public class JOpUnary extends AbstractJExpressionImpl
{
  private final String _op;
  private final IJExpression _e;
  private final boolean opFirst;

  protected JOpUnary (@Nonnull final String op, @Nonnull final IJExpression e)
  {
    this._op = op;
    this._e = e;
    opFirst = false;
  }

  protected JOpUnary (@Nonnull final IJExpression e, @Nonnull final String op)
  {
    this._op = op;
    this._e = e;
    opFirst = false;
  }

  @Nonnull
  public String op ()
  {
    return _op;
  }

  @Nonnull
  public IJExpression expr ()
  {
    return _e;
  }

  public boolean opFirst ()
  {
    return opFirst;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if (opFirst)
      f.print ('(').print (_op).generable (_e).print (')');
    else
      f.print ('(').generable (_e).print (_op).print (')');
  }
}