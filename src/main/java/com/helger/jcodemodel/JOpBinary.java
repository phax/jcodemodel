package com.helger.jcodemodel;

import javax.annotation.Nonnull;

public class JOpBinary extends AbstractJExpressionImpl
{
  private final IJExpression _left;
  private final String _op;
  private final IJGenerable _right;

  protected JOpBinary (@Nonnull final IJExpression left, @Nonnull final String op, @Nonnull final IJGenerable right)
  {
    this._left = left;
    this._op = op;
    this._right = right;
  }

  @Nonnull
  public IJExpression left ()
  {
    return _left;
  }

  @Nonnull
  public String op ()
  {
    return _op;
  }

  @Nonnull
  public IJGenerable right ()
  {
    return _right;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print ('(').generable (_left).print (_op).generable (_right).print (')');
  }
}
