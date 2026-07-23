package com.helger.jcodemodel.patterns;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJDeclaration;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJExpressionStatement;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJOwned;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JJavaName;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

/// Create an expression initialized at most once from another expression.
///
/// For example, you want `getString()` to cache the result of the method `String
/// longMethod() ` , then check that thjs value if ≥ 5:
/// ```java
/// var lazy = new JLazy(myJDefinedClass, String.class, "getString")
///    .static()
///    .init(Jexpr.invoke("longMethod"));
/// lazy.expr().ge(5);
///
/// ```
///
/// Upon creation, this class adds itself in the target [JDefinedClass] as an
/// extra declaration
///
public class JLazy implements IJDeclaration, IJOwned
{
  /// the class this is inserted into.
  private final JDefinedClass m_aClazz;

  private final AbstractJClass m_aExpressionType;

  /// the name of the method to call
  private final String m_sMethodName;

  /// the expression to call the lazy init, typically the method name
  private final IJExpressionStatement m_aExpr;

  /// when static, the field and methods are made static
  private boolean m_bStatic = false;

  /// when synchronized (default), the init is synchronized to avoid
  /// multiple calls.
  private boolean m_bSync = true;

  /// initialization of the value
  private IJExpression m_aInit;

  private static final String GETTER_PREFIX = "get";

  @Nullable
  static String extractFieldName (@Nullable final String methodName)
  {
    if (methodName == null)
    {
      return methodName;
    }

    String sMethodName = methodName.trim ();
    if (sMethodName.isEmpty ())
      return null;

    if (sMethodName.startsWith (GETTER_PREFIX) && sMethodName.length () > GETTER_PREFIX.length ())
    {
      sMethodName = Character.toString (Character.toLowerCase (sMethodName.charAt (GETTER_PREFIX.length ()))) +
                    sMethodName.substring (GETTER_PREFIX.length () + 1);
    }
    if (!JJavaName.isJavaIdentifier (sMethodName))
    {
      sMethodName = "_" + sMethodName;
    }
    return sMethodName;
  }

  public JLazy (final JDefinedClass clazz, final AbstractJClass expressionType, final String methodName)
  {
    ValueEnforcer.isTrue (JJavaName.isJavaIdentifier (methodName), () -> "Illegal variable name '" + methodName + "'");
    m_aClazz = clazz;
    m_aExpressionType = expressionType;
    m_sMethodName = methodName;
    m_aExpr = JExpr.invoke (methodName);
    clazz.getExtraDeclarations ().add (this);
  }

  public JLazy (final JDefinedClass clazz, final Class <?> expressionType, final String methodName)
  {
    this (clazz, clazz.owner ().ref (expressionType), methodName);
  }

  @Override
  public @NonNull JCodeModel owner ()
  {
    return m_aClazz.owner ();
  }

  public IJExpression expr ()
  {
    return m_aExpr;
  }

  /// @return this for chaining
  public JLazy _static ()
  {
    return _static (true);
  }

  /// @return this for chaining
  public JLazy _static (final boolean value)
  {
    m_bStatic = value;
    return this;
  }

  public boolean isStatic ()
  {
    return m_bStatic;
  }

  /// @return this for chaining
  public JLazy sync (final boolean value)
  {
    m_bSync = value;
    return this;
  }

  /// @return this for chaining
  public JLazy async ()
  {
    return sync (false);
  }

  public boolean sync ()
  {
    return m_bSync;
  }

  /// @return this for chaining
  public JLazy init (final IJExpression value)
  {
    m_aInit = value;
    return this;
  }

  public IJExpression init ()
  {
    return m_aInit;
  }

  @Override
  public String toString ()
  {
    return "lazy init " + m_sMethodName;
  }

  @Override
  public void declare (@NonNull final IJFormatter f)
  {
    if (m_aInit == null)
    {
      throw new NullPointerException ("init of " + this + " is null");
    }

    int fieldMods = JMod.PRIVATE | JMod.VOLATILE;
    if (m_bStatic)
    {
      fieldMods |= JMod.STATIC;
    }
    final JFieldVar jfv = new JFieldVar (m_aClazz,
                                         JMods.forField (fieldMods),
                                         m_aExpressionType,
                                         extractFieldName (m_sMethodName),
                                         null);
    f.declaration (jfv);
    f.newline ();
    final JFieldRef fieldRef = m_bStatic ? m_aClazz.staticRef (jfv) : JExpr.refthis (jfv);

    int methodMods = JMod.PUBLIC;
    if (m_bStatic)
    {
      methodMods |= JMod.STATIC;
    }
    final JMethod jm = new JMethod (m_aClazz, methodMods, m_aExpressionType, m_sMethodName);
    final JBlock methodBody = jm.body ();
    final JVar jv = methodBody.decl (m_aExpressionType, "ret").init (fieldRef);
    JBlock initBlock = methodBody._if (jv.eqNull ())._then ();
    if (m_bSync)
    {
      initBlock = initBlock.synchronizedBlock (m_bStatic ? m_aClazz.dotclass () : JExpr._this ()).body ();
    }
    initBlock.assign (jv, fieldRef);
    initBlock._if (jv.eqNull ())._then ().assign (jv, m_aInit).assign (fieldRef, jv);
    methodBody._return (jv);

    f.declaration (jm);
  }
}
