package com.helger.jcodemodel;

import javax.annotation.Nonnull;

public class JOpUnaryTight extends JOpUnary
{
  protected JOpUnaryTight (@Nonnull final IJExpression e, @Nonnull final String op)
  {
    super (e, op);
  }

  protected JOpUnaryTight (@Nonnull final String op, @Nonnull final IJExpression e)
  {
    super (op, e);
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
