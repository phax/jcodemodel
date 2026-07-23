/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.modifiers;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JAnnotatedClass;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JNarrowedClass;
import com.helger.jcodemodel.JReferencedClass;

/**
 * Modifiers applied to declared/defined elements, ordered by name ascending.
 * <p>
 * The JMod part refers to the {@link JMod} int enumeration. The modifier field refers to the enum
 * value in the reflect api {@link Modifier}, if avail. 0 if not, consistent with bitwise
 * operations.
 * <p>
 * This class should replace JMod in the long term, but they both should coexist for now.
 * Intermediate step will be, to use this class internally by mapping the JMod to it.
 */
public enum EMod
{
  ABSTRACT (JMod.ABSTRACT, Modifier.ABSTRACT, "abstract"),
  /**
   * default does not exist in reflect : it's a public non-abstract non-static method in an
   * interface
   */
  DEFAULT (JMod.DEFAULT, 0, "default"),
  FINAL (JMod.FINAL, Modifier.FINAL, "final"),
  NATIVE (JMod.NATIVE, Modifier.NATIVE, "native"),
  NONE (JMod.NONE, 0, ""),
  /**
   * non-sealed is not present at runtime, indicates allowed inheritance
   */
  NONSEALED (JMod.NONSEALED, 0, "non-sealed"),
  PRIVATE (JMod.PRIVATE, Modifier.PRIVATE, "private"),
  PROTECTED (JMod.PROTECTED, Modifier.PROTECTED, "protected"),
  PUBLIC (JMod.PUBLIC, Modifier.PUBLIC, "public"),
  /**
   * sealed is not present at runtime, indicates allowed inheritance
   */
  SEALED (JMod.SEALED, 0, "sealed"),
  STATIC (JMod.STATIC, Modifier.STATIC, "static"),
  STRICTFP (JMod.STRICTFP, Modifier.STRICT, "strictftp"),
  SYNCHRONIZED (JMod.SYNCHRONIZED, Modifier.SYNCHRONIZED, "synchronized"),
  TRANSIENT (JMod.TRANSIENT, Modifier.TRANSIENT, "transient"),
  VOLATILE (JMod.VOLATILE, Modifier.VOLATILE, "volatile");

  /** The {@link JMod} corresponding int value **/
  public final int m_nJMod;
  /** The {@link Modifier} corresponding int value, if any ; can be 0 if none. */
  public final int m_nModifier;
  public final String m_sFormat;

  /**
   * since we can't generate the list in the constructor, this is the same as field per enum.
   */
  private static final Map <EMod, Set <EMod>> EXCLUDES_CACHE = new HashMap <> ();

  // cached map of JMod -> Emod
  private static final Map <Integer, EMod> JMOD_CACHE = Stream.of (values ())
                                                              .collect (Collectors.toMap (em -> Integer.valueOf (em.m_nJMod),
                                                                                          Function.identity ()));

  /**
   * modifiers allowed on a class definition
   */
  public static final Set <EMod> ALLOWED_CLASS = Collections.unmodifiableSet (EnumSet.of (ABSTRACT,
                                                                                          FINAL,
                                                                                          NONSEALED,
                                                                                          PUBLIC,
                                                                                          PRIVATE,
                                                                                          PROTECTED,
                                                                                          SEALED,
                                                                                          STATIC));

  /**
   * modifiers allowed on a field declaration
   */
  public static final Set <EMod> ALLOWED_FIELD = Collections.unmodifiableSet (EnumSet.of (FINAL,
                                                                                          PUBLIC,
                                                                                          PRIVATE,
                                                                                          PROTECTED,
                                                                                          STATIC,
                                                                                          TRANSIENT,
                                                                                          VOLATILE));

  /**
   * modifiers allowed on an interface definition
   */
  public static final Set <EMod> ALLOWED_INTERFACE = Collections.unmodifiableSet (EnumSet.of (NONSEALED,
                                                                                              PUBLIC,
                                                                                              PRIVATE,
                                                                                              PROTECTED,
                                                                                              SEALED));

  /**
   * modifiers allowed on a method declaration
   */
  public static final Set <EMod> ALLOWED_METHOD = Collections.unmodifiableSet (EnumSet.of (ABSTRACT,
                                                                                           DEFAULT,
                                                                                           FINAL,
                                                                                           NATIVE,
                                                                                           PUBLIC,
                                                                                           PRIVATE,
                                                                                           PROTECTED,
                                                                                           STATIC,
                                                                                           SYNCHRONIZED));

  /**
   * modifiers allowed on a variable declaration
   */
  public static final Set <EMod> ALLOWED_VAR = Collections.unmodifiableSet (EnumSet.of (FINAL));

  /**
   * list of the modifiers that are mutually exclusive
   */
  public static final List <Set <EMod>> MUTUAL_EXCLUSIONS = List.of (Collections.unmodifiableSet (EnumSet.of (PUBLIC,
                                                                                                              PRIVATE,
                                                                                                              PROTECTED)),
                                                                     Collections.unmodifiableSet (EnumSet.of (NONSEALED,
                                                                                                              SEALED)));

  EMod (final int jmod, final int modifier, final String format)
  {
    m_nJMod = jmod;
    m_nModifier = modifier;
    m_sFormat = format;
  }

  //
  // tooling
  //

  /**
   * find the corresponding value for given JMod int value.
   *
   * @param jmodValue
   *        int value
   * @return null if none matches.
   */
  public static @Nullable EMod ofJMod (final int jmodValue)
  {
    return JMOD_CACHE.get (Integer.valueOf (jmodValue));
  }

  /**
   * find the list of elements present in a JMods int value
   *
   * @param jmodValue
   *        int value
   * @return a new, possibly empty, modifiable enum set.
   */
  public static @NonNull EnumSet <@NonNull EMod> ofJMods (final int jmodValue)
  {
    final EnumSet <EMod> ret = EnumSet.noneOf (EMod.class);
    for (final EMod em : values ())
      if (em.isPresentJMod (jmodValue))
        ret.add (em);
    return ret;
  }

  /**
   * transforms a set, typically an enumset, into a JMod bits-int.
   *
   * @param set
   *        mod set
   * @return int value
   */
  public static int toJMod (final @NonNull Set <EMod> set)
  {
    return set.stream ()
              .map (em -> Integer.valueOf (em.m_nJMod))
              .collect (Collectors.reducing (Integer.valueOf (0),
                                             (l, r) -> Integer.valueOf (l.intValue () | r.intValue ())))
              .intValue ();
  }

  /**
   * @return the list of modifiers this one excludes.
   */
  @NonNull
  public Set <EMod> excludes ()
  {
    Set <EMod> ret = EXCLUDES_CACHE.get (this);
    if (ret == null)
    {
      synchronized (EXCLUDES_CACHE)
      {
        ret = EXCLUDES_CACHE.computeIfAbsent (this,
                                              e -> MUTUAL_EXCLUSIONS.stream ()
                                                                    .filter (s -> s.contains (e))
                                                                    .flatMap (Set::stream)
                                                                    .filter (e2 -> e2 != e)
                                                                    .collect (Collectors.toCollection (() -> EnumSet.noneOf (EMod.class))));
      }
    }
    return ret;
  }

  public boolean isPresentJMod (final int jmods)
  {
    return (m_nJMod & jmods) != 0;
  }

  public int addJMod (final int jmods)
  {
    return jmods | m_nJMod;
  }

  public boolean isPresentModifiers (final int modifiers)
  {
    return m_nModifier != 0 && (m_nModifier & modifiers) != 0;
  }

  /**
   * test whether this mod is present. The {@link AbstractJClass} is tested as instance of defined,
   * referenced, annotated, narrowed class.
   *
   * @param ajc
   *        Abstract class
   * @return Otherwise returns false.
   */
  public boolean isPresent (final @Nullable AbstractJClass ajc)
  {
    if (ajc instanceof final JDefinedClass jdc)
    {
      return isPresentJMod (jdc.mods ().getValue ());
    }
    if (ajc instanceof final JReferencedClass jrc)
    {
      return isPresentModifiers (jrc.getReferencedClass ().getModifiers ());
    }
    if (ajc instanceof final JAnnotatedClass jac)
    {
      return isPresent (jac.basis ());
    }
    if (ajc instanceof final JNarrowedClass jnc)
    {
      return isPresent (jnc.basis ());
    }
    return false;
  }

  //
  // static tools for the [IJModified] implementations
  //

  public static void addEmod (final Set <EMod> allowed, final Set <EMod> emodifiers, final EMod... emods)
  {
    if (emods != null)
    {
      for (final EMod emod : emods)
      {
        if (allowed.contains (emod))
        {
          emodifiers.removeAll (emod.excludes ());
          emodifiers.add (emod);
        }
      }
    }
  }

  public static boolean isEmod (final Set <EMod> emodifiers, final EMod... emods)
  {
    if (emods != null)
    {
      return Stream.of (emods).filter (em -> !emodifiers.contains (em)).findAny ().isEmpty ();
    }
    return true;
  }

}
