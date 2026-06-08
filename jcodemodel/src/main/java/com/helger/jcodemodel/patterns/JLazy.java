package com.helger.jcodemodel.patterns;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.*;


/// Create an expression to initialize at most once a value from another
/// expression.
///
/// For example, you want `getString()` to cache the result of the method `String
/// longMethod() ` :
/// ```java
/// var lazy = new JLazy(myJDefinedClass, String.class, "getString")
///    .static()
///    .init(Jexpr.invoke("longMethod"));
///
/// ```
///
/// Upon creation, this class adds itself in the target [JDefinedClass] as an
/// extra declaration
///
@SuppressWarnings("serial")
public class JLazy implements IJDeclaration, IJOwned {

  /// the class this is inserted into.
  private final JDefinedClass clazz;

  private final AbstractJClass expressionType;

  /// the name of the method to call
  private final String methodName;

  /// the expression to call the lazy init, typically the method name
  private final IJExpressionStatement expr;

  /// when static, the field and methods are made static
  private boolean _static = false;

  /// when synchronized (default), the init is synchronized to avoid
  /// multiple calls.
  private boolean sync = true;

  /// initialization of the value
  private IJExpression init = null;

  private static final String GETTER_PREFIX = "get";

  static String extractFieldName(String methodName) {
    if (methodName == null || methodName.isBlank()) {
      return methodName;
    }
    if (methodName.startsWith(GETTER_PREFIX) && methodName.length() > GETTER_PREFIX.length()) {
      methodName =
          Character.toString(Character.toLowerCase(methodName.charAt(GETTER_PREFIX.length())))
              + methodName.substring(GETTER_PREFIX.length() + 1);
    }
    if (!JJavaName.isJavaIdentifier(methodName)) {
      methodName += "_";
    }
    return methodName;
  }

  public JLazy(JDefinedClass clazz, AbstractJClass expressionType, String methodName) {
    ValueEnforcer.isTrue(JJavaName.isJavaIdentifier(methodName), () -> "Illegal variable name '" + methodName + "'");
    this.clazz = clazz;
    this.expressionType = expressionType;
    this.methodName = methodName;
    expr = JExpr.invoke(methodName);
    clazz.getExtraDeclarations().add(this);
  }

  public JLazy(JDefinedClass clazz, Class<?> expressionType, String methodName) {
    this(clazz, clazz.owner().ref(expressionType), methodName);
  }

  @Override
  public @NonNull JCodeModel owner() {
    return clazz.owner();
  }

  public IJExpression expr() {
    return expr;
  }

  /// @return this for chaining
  public JLazy _static() {
    return _static(true);
  }

  /// @return this for chaining
  public JLazy _static(boolean value) {
    _static = value;
    return this;
  }

  public boolean isStatic() {
    return _static;
  }

  /// @return this for chaining
  public JLazy sync(boolean value) {
    sync = value;
    return this;
  }

  /// @return this for chaining
  public JLazy async() {
    return sync(false);
  }

  public boolean sync() {
    return sync;
  }

  /// @return this for chaining
  public JLazy init(IJExpression value) {
    init = value;
    return this;
  }

  public IJExpression init() {
    return init;
  }

  @Override
  public String toString() {
    return "lazy init " + methodName;
  }

  @Override
  public void declare(@NonNull IJFormatter f) {
    if (init == null) {
      throw new NullPointerException("init of " + this + " is null");
    }


    int fieldMods = JMod.PRIVATE|JMod.VOLATILE;
    if (_static) {
      fieldMods |= JMod.STATIC;
    }
    JFieldVar jfv =
        new JFieldVar(clazz, JMods.forField(fieldMods), expressionType, extractFieldName(methodName), null);
    f.declaration(jfv);
    f.newline();

    int methodMods = JMod.PUBLIC;
    if (_static) {
      methodMods |= JMod.STATIC;
    }
    JMethod jm = new JMethod(clazz, methodMods, expressionType, methodName);
    JBlock methodBody = jm.body();
    String varName = methodName;
    if(varName.equals(jfv.name())) {
      varName+="_";
    }
    JVar jv = methodBody.decl(expressionType, varName).init(jfv);
    JBlock initBlock = methodBody._if(jv.eqNull())._then();
    if (sync) {
      initBlock =
          initBlock.synchronizedBlock(
              _static
                  ? clazz.dotclass()
                  : JExpr._this())
              .body();
    }
    initBlock._if(jv.eqNull())._then()
        .assign(jv, init)
        .assign(jfv, jv);
    methodBody._return(jv);

    f.declaration(jm);
  }

}
