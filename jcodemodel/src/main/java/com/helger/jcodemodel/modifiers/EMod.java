package com.helger.jcodemodel.modifiers;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JAnnotatedClass;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JNarrowedClass;
import com.helger.jcodemodel.JReferencedClass;

/// Modifiers applied to declared/defined elements, ordered by name ascending.
///
/// The JMod part refers to the [JMod] int enumeration.
/// The modifier field refers to the enum value in the reflect api [Modifier], if avail. 0 if not, consistent with bitwise operations.
///
///
/// This class should replace JMod in the long term, but they both should coexist for now.
/// Intermediate step will be, to use this class internally by mapping the JMod to it.
///
public enum EMod
{
  ABSTRACT(JMod.ABSTRACT, Modifier.ABSTRACT, "abstract"),
  /// default does not exist in reflect : it's a public non-abstrct non-static
  /// method in an interface
  DEFAULT(JMod.DEFAULT, 0, "default"),
  FINAL(JMod.FINAL, Modifier.FINAL, "final"),
  NATIVE(JMod.NATIVE, Modifier.NATIVE, "native"),
  NONE(JMod.NONE, 0, ""),
  /// nopn-sealed is not present at runtime, indicates allowed inheritance
  NONSEALED(JMod.NONSEALED, 0, "non-sealed"),
  PRIVATE(JMod.PRIVATE, Modifier.PRIVATE, "private"),
  PROTECTED(JMod.PROTECTED, Modifier.PROTECTED, "protected"),
  PUBLIC(JMod.PUBLIC, Modifier.PUBLIC, "public"),
  /// sealed is not present at runtime, indicates allowed inheritance
  SEALED(JMod.SEALED, 0, "sealed"),
  STATIC(JMod.STATIC, Modifier.STATIC, "static"),
  STRICTFP(JMod.STRICTFP, Modifier.STRICT, "strictftp"),
  SYNCHRONIZED(JMod.SYNCHRONIZED, Modifier.SYNCHRONIZED, "synchronized"),
  TRANSIENT(JMod.TRANSIENT, Modifier.TRANSIENT, "transient"),
  VOLATILE(JMod.VOLATILE, Modifier.VOLATILE, "volatile");

  public final int m_nJMod;
  public final int m_nModifier;
  public final String m_sFormat;

  /// since we can't generate the list in the constructor, this is the same as
  /// field per enum.
  private static final Map<EMod, Set<EMod>> EXCLUDES_CACHE = new HashMap<>();

  // cached map of JMod -> Emod
  private static final Map<Integer, EMod> JMOD_CACHE =
      Stream.of(values()).collect(Collectors.toMap(em -> em.m_nJMod, em -> em));

  /// modifiers allowed on a class definition
  public static final Set<EMod> ALLOWED_CLASS =
      Collections.unmodifiableSet(EnumSet.of(
          ABSTRACT,
          FINAL,
          NONSEALED,
          PUBLIC,
          PRIVATE,
          PROTECTED,
          SEALED,
          STATIC
      ));

  /// modifiers allowed on a field declaration
  public static final Set<EMod> ALLOWED_FIELD =
      Collections.unmodifiableSet(EnumSet.of(
          FINAL,
          PUBLIC,
          PRIVATE,
          PROTECTED,
          STATIC,
          TRANSIENT,
          VOLATILE));

  /// modifiers allowed on an interface definition
  public static final Set<EMod> ALLOWED_INTERFACE =
      Collections.unmodifiableSet(EnumSet.of(
          NONSEALED,
          PUBLIC,
          PRIVATE,
          PROTECTED,
          SEALED
      ));

  /// modifiers allowed on a method declaration
  public static final Set<EMod> ALLOWED_METHOD =
      Collections.unmodifiableSet(EnumSet.of(
          ABSTRACT,
          DEFAULT,
          FINAL,
          NATIVE,
          PUBLIC,
          PRIVATE,
          PROTECTED,
          STATIC,
          SYNCHRONIZED
      ));

  /// modifiers allowed on a variable declaration
  public static final Set<EMod> ALLOWED_VAR =
      Collections.unmodifiableSet(EnumSet.of(
          FINAL
      ));

  /// list of the modifiers that are mutually exclusive
  public static final List<Set<EMod>> MUTUAL_EXCLUSIONS =
      List.of(
          Collections.unmodifiableSet(EnumSet.of(
              PUBLIC,
              PRIVATE,
              PROTECTED)),
          Collections.unmodifiableSet(EnumSet.of(
              NONSEALED,
              SEALED))
      );

  EMod(final int jmod, final int modifier, String format)
  {
    m_nJMod = jmod;
    m_nModifier = modifier;
    m_sFormat = format;
  }

  //
  // tooling
  //

  /// find the corresponding value for given JMod int value.
  /// @return null if none matches.
  public static EMod ofJMod(int jmodValue) {
    return JMOD_CACHE.get(jmodValue);
  }

  /// find the list of elements present in a JMods int value
  /// @return a new, possibly empty, modifiable enum set.
  public static EnumSet<EMod> ofJMods(int jmodValue) {
    EnumSet<EMod> ret = EnumSet.noneOf(EMod.class);
    for (EMod em : values()) {
      if (em.isPresentJMod(jmodValue)) {
        ret.add(em);
      }
    }
    return ret;
  }

  /// transforms a set, typically an enumset, into a JMod bits-int.
  public static int toJMod(Set<EMod> set) {
    return set.stream()
        .map(em -> em.m_nJMod)
        .collect(Collectors
            .reducing(0, (l, r) -> l | r));
  }

  /// the list of modifiers this one excludes.
  public Set<EMod> excludes() {
    Set<EMod> ret = EXCLUDES_CACHE.get(this);
    if (ret == null) {
      synchronized (EXCLUDES_CACHE) {
        ret =
            EXCLUDES_CACHE.computeIfAbsent(this,
                e -> MUTUAL_EXCLUSIONS.stream()
                    .filter(s -> s.contains(e))
                    .flatMap(Set::stream)
                    .filter(e2 -> e2 != e)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(EMod.class))));
      }
    }
    return ret;
  }

  public boolean isPresentJMod (final int jmods)
  {
    return (m_nJMod & jmods) != 0;
  }

  public int addJMod(final int jmods) {
    return jmods | m_nJMod;
  }

  public boolean isPresentModifiers (final int modifiers)
  {
    return m_nModifier != 0 && (m_nModifier & modifiers) != 0;
  }

  /// test whether this mod is present. The [AbstractJClass] is tested as instance
  /// of defined, referenced, annotated, narrowed class.
  ///
  /// Otherwise returns false.
  public boolean isPresent(AbstractJClass ajc)
  {
    if (ajc instanceof JDefinedClass jdc) {
      return isPresentJMod(jdc.mods().getValue());
    } else if (ajc instanceof JReferencedClass jrc) {
      return isPresentModifiers(jrc.getReferencedClass().getModifiers());
    } else if( ajc instanceof JAnnotatedClass jac) {
      return isPresent(jac.basis());
    } else if (ajc instanceof JNarrowedClass jnc) {
      return isPresent(jnc.basis());
    }
    return false;
  }

}