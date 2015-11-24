/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.jcodemodel.meta;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Defines policy to use with error-types.
 * <p>
 * {@code tryBind} parameter provides access to (re-)binding of references to
 * error-types.
 * <p>
 * We may use elements provided by Java compiler during jcodemodel code
 * generation. Existing java source code may already have references to not yet
 * generated classes. In such scenario Java-compiler will give us error-types.
 * When this occures we may try to rebind error-types to classes defined in
 * jcodemodel, but missing in existing Java source code accessible to
 * Java-compiler.
 * <p>
 * When {@code tryBind} parameter is true, we try to rebind error-types to
 * classes defined in jcodemodel. When {@code tryBind} parameter is false,
 * error-types are returned as is.
 *
 * @author vir
 */
public class ErrorTypePolicy
{
  public static enum EAction
  {
   THROW_EXCEPTION,
   CREATE_ERROR_TYPE
  }

  private final EAction _action;
  private final boolean _tryBind;

  /**
   * @see ErrorTypePolicy
   * @param aAction
   *        action to perform if any error-type is found.
   * @param tryBind
   *        if true try to (re-)bind references to error-types to existing
   *        types.
   */
  public ErrorTypePolicy (@Nonnull final EAction aAction, final boolean tryBind)
  {
    _action = JCValueEnforcer.notNull (aAction, "Action");
    _tryBind = tryBind;
  }

  /**
   * Action to perform if any error-type is found.
   */
  @Nonnull
  EAction action ()
  {
    return _action;
  }

  /**
   * Try to rebind error-types to classes defined in jcodemodel.
   * <p>
   * We may use elements provided by Java compiler during jcodemodel code
   * generation. Existing java source code may already have references to not
   * yet generated classes. In such scenario Java-compiler will give us
   * error-types. When this occures we may try to rebind error-types to classes
   * defined in jcodemodel, but missing in existing Java source code accessible
   * to Java-compiler.
   * <p>
   * When {@code tryBind} parameter is true, we try to rebind error-types to
   * classes defined in jcodemodel. When {@code tryBind} parameter is false,
   * error-types are returned as is. Action to perform if any error-type is
   * found.
   */
  boolean tryBind ()
  {
    return _tryBind;
  }
}
