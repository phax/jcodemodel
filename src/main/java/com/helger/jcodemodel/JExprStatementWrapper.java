package com.helger.jcodemodel;

import javax.annotation.Nonnull;

public class JExprStatementWrapper <T extends IJExpression> extends AbstractJExpressionImpl implements IJExpressionStatement
{
  private final T _expr;

  public JExprStatementWrapper (@Nonnull final T expression)
  {
    this._expr = expression;
  }

  @Nonnull
  public T expr ()
  {
    return _expr;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    _expr.generate (f);
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (_expr).print (';').newline ();
  }

  @Nonnull
  public static <U extends IJExpression> JExprStatementWrapper <U> create (@Nonnull final U expr)
  {
    return new JExprStatementWrapper <U> (expr);
  }
}
