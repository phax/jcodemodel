/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Nonnull;

/**
 * Modifier groups.
 */
public class JMods implements IJGenerable
{
  //
  // mask
  //
  private static final int VAR = JMod.FINAL;
  private static final int FIELD = JMod.PUBLIC |
                                   JMod.PRIVATE |
                                   JMod.PROTECTED |
                                   JMod.STATIC |
                                   JMod.FINAL |
                                   JMod.TRANSIENT |
                                   JMod.VOLATILE;
  private static final int METHOD = JMod.PUBLIC |
                                    JMod.PRIVATE |
                                    JMod.PROTECTED |
                                    JMod.FINAL |
                                    JMod.ABSTRACT |
                                    JMod.STATIC |
                                    JMod.NATIVE |
                                    JMod.SYNCHRONIZED;
  private static final int CLASS = JMod.PUBLIC |
                                   JMod.PRIVATE |
                                   JMod.PROTECTED |
                                   JMod.STATIC |
                                   JMod.FINAL |
                                   JMod.ABSTRACT;
  private static final int INTERFACE = JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED;

  /** bit-packed representation of modifiers. */
  private int _mods;

  protected JMods (final int mods)
  {
    this._mods = mods;
  }

  /**
   * Gets the bit-packed representaion of modifiers.
   */
  public int getValue ()
  {
    return _mods;
  }

  private static void _check (final int mods, final int legal, final String what)
  {
    if ((mods & ~legal) != 0)
    {
      throw new IllegalArgumentException ("Illegal modifiers for " + what + ": " + new JMods (mods).toString ());
    }
    /* ## check for illegal combinations too */
  }

  @Nonnull
  public static JMods forVar (final int mods)
  {
    _check (mods, VAR, "variable");
    return new JMods (mods);
  }

  @Nonnull
  public static JMods forField (final int mods)
  {
    _check (mods, FIELD, "field");
    return new JMods (mods);
  }

  @Nonnull
  public static JMods forMethod (final int mods)
  {
    _check (mods, METHOD, "method");
    return new JMods (mods);
  }

  @Nonnull
  public static JMods forClass (final int mods)
  {
    _check (mods, CLASS, "class");
    return new JMods (mods);
  }

  @Nonnull
  public static JMods forInterface (final int mods)
  {
    _check (mods, INTERFACE, "class");
    return new JMods (mods);
  }

  public boolean isAbstract ()
  {
    return (_mods & JMod.ABSTRACT) != 0;
  }

  public boolean isNative ()
  {
    return (_mods & JMod.NATIVE) != 0;
  }

  public boolean isSynchronized ()
  {
    return (_mods & JMod.SYNCHRONIZED) != 0;
  }

  public void setSynchronized (final boolean newValue)
  {
    _setFlag (JMod.SYNCHRONIZED, newValue);
  }

  public void setPrivate ()
  {
    _setFlag (JMod.PUBLIC, false);
    _setFlag (JMod.PROTECTED, false);
    _setFlag (JMod.PRIVATE, true);
  }

  public void setPackagePrivate ()
  {
    _setFlag (JMod.PUBLIC, false);
    _setFlag (JMod.PROTECTED, false);
    _setFlag (JMod.PRIVATE, false);
  }

  public void setProtected ()
  {
    _setFlag (JMod.PUBLIC, false);
    _setFlag (JMod.PROTECTED, true);
    _setFlag (JMod.PRIVATE, false);
  }

  public void setPublic ()
  {
    _setFlag (JMod.PUBLIC, true);
    _setFlag (JMod.PROTECTED, false);
    _setFlag (JMod.PRIVATE, false);
  }

  public void setFinal (final boolean newValue)
  {
    _setFlag (JMod.FINAL, newValue);
  }

  private void _setFlag (final int bit, final boolean newValue)
  {
    _mods = (_mods & ~bit) | (newValue ? bit : 0);
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if ((_mods & JMod.PUBLIC) != 0)
      f.print ("public");
    if ((_mods & JMod.PROTECTED) != 0)
      f.print ("protected");
    if ((_mods & JMod.PRIVATE) != 0)
      f.print ("private");
    if ((_mods & JMod.FINAL) != 0)
      f.print ("final");
    if ((_mods & JMod.STATIC) != 0)
      f.print ("static");
    if ((_mods & JMod.ABSTRACT) != 0)
      f.print ("abstract");
    if ((_mods & JMod.NATIVE) != 0)
      f.print ("native");
    if ((_mods & JMod.SYNCHRONIZED) != 0)
      f.print ("synchronized");
    if ((_mods & JMod.TRANSIENT) != 0)
      f.print ("transient");
    if ((_mods & JMod.VOLATILE) != 0)
      f.print ("volatile");
  }

  @Override
  public String toString ()
  {
    final StringWriter s = new StringWriter ();
    final JFormatter f = new JFormatter (new PrintWriter (s));
    this.generate (f);
    return s.toString ();
  }
}
