package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JVar;

/// vars that are the same as another one.
///
/// example
/// ```java
/// int i=0, j, k=1;
/// ```
/// Here i is a block variable, j and k are "same" var.
public class JSameVar extends JVar
{
  private final JVar m_aParent;

  /// number of additional array dimension on top of the parent.
  private final int m_nDim;

  public JSameVar (@NonNull final JVar parent,
                   final String sName,
                   final IVariableInitializer aInitExpr,
                   @Nonnegative final int nDim)
  {
    super (parent.mods (), typeArray (parent.type (), nDim), sName, aInitExpr);
    this.m_aParent = parent;
    this.m_nDim = nDim;
  }

  public JSameVar (final JVar parent, final String sName, final IVariableInitializer aInitExpr)
  {
    this (parent, sName, aInitExpr, 0);
  }

  public JVar parentVar ()
  {
    return m_aParent;
  }

  @Override
  public void bind (@NonNull final IJFormatter f)
  {
    f.id (name ());
    for (int i = 0; i < m_nDim; i++)
    {
      f.print ("[]");
    }
    if (init () != null)
    {
      f.print ('=').generable (init ());
    }
  }

  @Override
  public String separator ()
  {
    return ",";
  }

  @NonNull
  static AbstractJType typeArray (@NonNull final AbstractJType type, @Nonnegative final int dim)
  {
    var aCurType = type;
    int nRestDim = dim;
    while (nRestDim > 0)
    {
      aCurType = aCurType.array ();
      nRestDim--;
    }
    return aCurType;
  }
}
