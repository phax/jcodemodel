/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.jcodemodel.meta;

/**
 * Defines policy to use with error-types.
 * <p>
 * {@code tryBind} parameter provides access to
 * (re-)binding of references to error-types.
 * <p>
 * We may use elements provided by Java compiler
 * during jcodemodel code generation.
 * Existing java source code may already have references to not yet generated classes.
 * In such scenario Java-compiler will give us error-types.
 * When this occures we may try to rebind error-types to classes defined
 * in jcodemodel, but missing in existing Java source code accessible to Java-compiler.
 * <p>
 * When {@code tryBind} parameter is true, we try to rebind error-types to classes defined
 * in jcodemodel.
 * When {@code tryBind} parameter is false, error-types are returned as is.
 *
 * @author vir
 */
public class ErrorTypePolicy {
  private final Action action;
  private final boolean tryBind;

  /**
   * @see ErrorTypePolicy
   *
   * @param action
   *        action to perform if any error-type is found.
   * @param tryBind
   *        if true try to (re-)bind references to error-types
   *        to existing types.
   */
  public ErrorTypePolicy (Action action, boolean tryBind)
  {
    this.action = action;
    this.tryBind = tryBind;
  }

  /**
   * Action to perform if any error-type is found.
   */
  Action action ()
  {
    return action;
  }

  /**
   * Try to rebind error-types to classes defined
   * in jcodemodel.
   * <p>
   * We may use elements provided by Java compiler
   * during jcodemodel code generation.
   * Existing java source code may already have references to not yet generated classes.
   * In such scenario Java-compiler will give us error-types.
   * When this occures we may try to rebind error-types to classes defined
   * in jcodemodel, but missing in existing Java source code accessible to Java-compiler.
   * <p>
   * When {@code tryBind} parameter is true, we try to rebind error-types to classes defined
   * in jcodemodel.
   * When {@code tryBind} parameter is false, error-types are returned as is.
   * Action to perform if any error-type is found.
   */
  boolean tryBind ()
  {
    return tryBind;
  }

  public enum Action {
    THROW_EXCEPTION, CREATE_ERROR_TYPE
  }
}
