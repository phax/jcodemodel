/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt. See the License for the specific
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
 * Version 2] license." If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above. However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel.writer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.WillCloseWhenClosed;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.*;
import com.helger.jcodemodel.util.ClassNameComparator;
import com.helger.jcodemodel.util.NullWriter;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

/**
 * This is a utility class for managing indentation and other basic formatting
 * for PrintWriter.
 */
@NotThreadSafe
public class JFormatter implements IJFormatter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JFormatter.NameUsage.class);

  /**
   * Used during the optimization of class imports. List of
   * {@link AbstractJClass}es whose short name is the same.
   *
   * @author Ryan.Shoemaker@Sun.COM
   */
  private final class NameUsage {
    private final String m_sName;

    private final List<AbstractJClass> m_aReferencedClasses = new ArrayList<>();

    /** true if this name is used as an identifier (like a variable name.) **/
    private boolean m_bIsVariableName;

    public NameUsage(@NonNull final String sName) {
      m_sName = sName;
    }

    /**
     * @param aEnclosingClass
     *                        the class to check
     * @return <code>true</code> if the short name is ambiguous in context of
     *         enclosingClass and classes with this name can't be imported.
     */
    public boolean isAmbiguousIn(@NonNull final JDefinedClass aEnclosingClass) {
      // more than one type with the same name
      if (m_aReferencedClasses.size() > 1) {
        return true;
      }

      // an id and (at least one) type with the same name
      if (m_bIsVariableName && !m_aReferencedClasses.isEmpty()) {
        return true;
      }

      // no references is always unambiguous
      if (m_aReferencedClasses.isEmpty()) {
        return false;
      }

      // we have exactly one reference
      AbstractJClass aSingleRef = m_aReferencedClasses.get(0);
      if (aSingleRef instanceof JAnonymousClass) {
        aSingleRef = ((JAnonymousClass) aSingleRef).base();
      }

      // special case where a generated type collides with a type in package
      // java.lang
      if (aSingleRef._package() == m_aPckJavaLang) {
        // make sure that there's no other class with this name within the
        // same package
        for (final JDefinedClass aClass : aEnclosingClass._package().classes()) {
          // even if this is the only "String" class we use,
          // if the class called "String" is in the same package,
          // we still need to import it.
          if (aClass.name().equals(aSingleRef.name())) {
            // collision -> ambiguous
            return true;
          }
        }
      }

      return false;
    }

    public boolean addReferencedType(@NonNull final AbstractJClass aClazz) {
      if (false) {
        LOGGER.info("Adding referenced type[" + m_sName + "]: " + aClazz.fullName());
      }
      if (m_aReferencedClasses.contains(aClazz)) {
        return false;
      }
      return m_aReferencedClasses.add(aClazz);
    }

    public boolean containsReferencedType(@Nullable final AbstractJClass aClazz) {
      return m_aReferencedClasses.contains(aClazz);
    }

    @NonNull
    public AbstractJClass getSingleReferencedType() {
      assert m_aReferencedClasses.size() == 1;
      return m_aReferencedClasses.get(0);
    }

    @NonNull
    public List<AbstractJClass> getReferencedTypes() {
      return m_aReferencedClasses;
    }

    public void setVariableName() {
      // Check if something can be a variable or a type
      for (final AbstractJClass aRefedType : m_aReferencedClasses) {
        if (aRefedType.outer() != null) {
          m_bIsVariableName = false;
          return;
        }
      }
      m_bIsVariableName = true;
    }

    /**
     * @return true if this name is used as an identifier (like a variable
     *         name.).
     */
    public boolean isVariableName() {
      return m_bIsVariableName;
    }

    /**
     * @return true if this name is used as an type name (like class name.)
     */
    public boolean isTypeName() {
      return !m_aReferencedClasses.isEmpty();
    }

    @Override
    public String toString() {
      final StringBuilder aSB = new StringBuilder("Usages[").append(m_sName).append("]");
      aSB.append("; isVarName=").append(m_bIsVariableName);
      aSB.append("; refedClasses=").append(m_aReferencedClasses);
      return aSB.toString();
    }
  }

  private enum EMode {
    /**
     * Collect all the type names and identifiers. In this mode we don't
     * actually generate anything.
     */
    COLLECTING,
    /**
     * Print the actual source code.
     */
    PRINTING,

    /**
     * Find any error types in output code. In this mode we don't actually
     * generate anything. <br/>
     * Only used by {@link JFormatter#containsErrorTypes(JDefinedClass)
     * containsErrorTypes} method
     */
    FIND_ERROR_TYPES
  }

  private static final Map<String, Boolean> RESERVERD_JAVA_LANG_NAME = new HashMap<>();

  /**
   * check if a name already represents a class in the "java.lang" package. e.g.
   * "String" should return <code>true</code>, but "blub" should return
   * <code>false</code>. See https://github.com/phax/jcodemodel/issues/71
   *
   * @param sSimpleClassName
   *                         simple class name to test (without the package).
   * @return the existence of such a class in the java.lang (cached)
   */
  static boolean isJavaLangClass(final String sSimpleClassName) {
    if (sSimpleClassName == null) {
      return false;
    }

    final Boolean ret = RESERVERD_JAVA_LANG_NAME.get(sSimpleClassName);
    if (ret != null) {
      return ret;
    }

    boolean bIsJavaLang = true;
    try {
      Class.forName("java.lang." + sSimpleClassName);
    } catch (final Exception ex) {
      bIsJavaLang = false;
    }
    RESERVERD_JAVA_LANG_NAME.put(sSimpleClassName, bIsJavaLang);
    return bIsJavaLang;
  }

  private final class ImportedClasses {
    private final Set<AbstractJClass> m_aDontImportClasses = new HashSet<>();
    private final Set<AbstractJClass> m_aClasses = new HashSet<>();
    private final Set<String> m_aNames = new HashSet<>();

    public ImportedClasses() {
    }

    @Nullable
    private AbstractJClass _getClassForImport(@Nullable final AbstractJClass aClass) {
      AbstractJClass aRealClass = aClass;
      if (aRealClass instanceof JAnonymousClass) {
        // get the super class of the anonymous class
        return _getClassForImport(((JAnonymousClass) aRealClass).base());
      }
      if (aRealClass instanceof JNarrowedClass) {
        // Never imported narrowed class but the erasure only
        aRealClass = aRealClass.erasure();
      }
      return aRealClass;
    }

    public void addDontImportClass(@NonNull final AbstractJClass aClass) {
      final AbstractJClass aRealClass = _getClassForImport(aClass);
      m_aDontImportClasses.add(aRealClass);
    }

    public boolean add(@NonNull final AbstractJClass aClass) {
      final AbstractJClass aRealClass = _getClassForImport(aClass);
      final String sSimpleName = aRealClass.name();

      if (m_aDontImportClasses.contains(aRealClass)) {
        if (m_bDebugImport) {
          LOGGER.info("The class '" + aRealClass.fullName() + "' should not be imported!");
        }
        return false;
      }

      // Avoid importing 2 classes with the same class name
      if (!m_aNames.add(sSimpleName)) {
        if (m_bDebugImport) {
          LOGGER.info("A class with local name '" + sSimpleName + "' is already in the import list.");
        }
        return false;
      }

      if (!m_aClasses.add(aRealClass)) {
        if (m_bDebugImport) {
          LOGGER.info("The class '" + aRealClass.fullName() + "' is already in the import list.");
        }
        return false;
      }

      if (m_bDebugImport) {
        LOGGER.info("Added import class '" + aClass.fullName() + "'");
      }
      return true;
    }

    public boolean contains(@Nullable final AbstractJClass aClass) {
      final AbstractJClass aRealClass = _getClassForImport(aClass);

      return m_aClasses.contains(aRealClass);
    }

    public void clear() {
      m_aClasses.clear();
      m_aNames.clear();
    }

    @NonNull
    public List<AbstractJClass> getAllSorted() {
      // Copy and sort
      final List<AbstractJClass> aImports = new ArrayList<>(m_aClasses);
      aImports.sort(ClassNameComparator.getInstance());
      return aImports;
    }
  }

  /**
   * all classes and ids encountered during the collection mode.<br>
   * map from short type name to {@link NameUsage} (list of
   * {@link AbstractJClass} and ids sharing that name)
   **/
  private final Map<String, NameUsage> m_aCollectedReferences = new HashMap<>();

  /**
   * set of imported types (including package java types, even though we won't
   * generate imports for them)
   */
  private final ImportedClasses m_aImportedClasses = new ImportedClasses();

  /**
   * The current running mode. Set to PRINTING so that a casual client can use a
   * formatter just like before.
   */
  private EMode m_eMode = EMode.PRINTING;

  private final FormatterSettings m_oSettings;

  /**
   * Writer associated with this {@link IJFormatter}
   */
  private final SourcePrintWriter m_aPW;

  /// contexts to write into instead of the printwriter.
  ///
  /// No context should be kept here after closed.
  private final List<FormatterContext> contextLayers = new ArrayList<>();

  /// internal field to have this as a writer context.
  private final WriteContext asContext = new WriteContext() {

    /**
     * Current number of indentation strings to print
     */
    private int m_nIndentLevel;

    private char m_cLastChar = 0;

    @NonNull
    private StringBuilder currentLine = new StringBuilder();

    @Override
    public int getIndentLevel() {
      return m_nIndentLevel;
    }

    @Override
    public void setIndentLevel(int nb) {
      m_nIndentLevel = nb;
    }

    @Override
    public char getLastChar() {
      return m_cLastChar;
    }

    @Override
    public String getCurrentLine() {
      return currentLine.toString();
    }

    @Override
    public String getNewLine() {
      return JFormatter.this.getNewLine();
    }

    @Override
    public void append(String fullString, boolean resetLine, String appendLine, char lastChar) {
      m_aPW.print(fullString);
      m_cLastChar = lastChar;
      if (resetLine) {
        currentLine = new StringBuilder();
      }
      currentLine.append(appendLine);
    }

    @Override
    public void append(char c) {
      char printedChar = c == CLOSE_TYPE_ARGS ? '>' : c;
      m_aPW.print(printedChar);
      currentLine.append(printedChar);
      m_cLastChar = c;
    }
  };

  private JPackage m_aPckJavaLang;

  /**
   * Only used by {@link IJFormatter#containsErrorTypes(JDefinedClass)
   * containsErrorTypes} method
   */
  private boolean m_bContainsErrorTypes;

  private boolean m_bDebugImport = false;

  private int m_nJavaFeature = JCMWriter.DEFAULT_JAVA_FEATURE;

  /**
   * Constructor
   *
   * @param aPW
   *                      {@link PrintWriter} to {@link IJFormatter} to use. May
   *                      not be
   *                      <code>null</code>. Is closed when this object is closed.
   * @param sIndentString
   *                      Incremental indentation string, similar to tab value.
   *                      May not be
   *                      <code>null</code>.
   */
  public JFormatter(@NonNull @WillCloseWhenClosed final SourcePrintWriter aPW,
      @NonNull final FormatterSettings formatterSettings) {
    ValueEnforcer.notNull(aPW, "PrintWriter");
    ValueEnforcer.notNull(formatterSettings, "formatterOptions");

    m_aPW = aPW;
    m_oSettings = formatterSettings;
  }

  /**
   * Closes this formatter.
   */
  @Override
  public void close() {
    m_aPW.close();
  }

  public boolean isDebugImports() {
    return m_bDebugImport;
  }

  public void setDebugImports(final boolean bDebug) {
    m_bDebugImport = bDebug;
  }

  /**
   * @return The Java feature (major release version) the generated code is
   *         targeted at. Defaults to
   *         {@link JCMWriter#DEFAULT_JAVA_FEATURE}.
   */
  public int getJavaFeature() {
    return m_nJavaFeature;
  }

  /**
   * Set the Java feature (major release version) the generated code is targeted
   * at.
   *
   * @param nJavaFeature
   *                     The Java feature to be used.
   * @return this for chaining
   */
  @NonNull
  public JFormatter setJavaFeature(final int nJavaFeature) {
    m_nJavaFeature = nJavaFeature;
    return this;
  }

  @Override
  public String getNewLine() {
    return m_aPW.getNewLine();
  }

  @Override
  public FormatterSettings settings() {
    return m_oSettings;
  }

  @Override
  public boolean isPrinting() {
    return m_eMode == EMode.PRINTING;
  }

  @Override
  @NonNull
  public JFormatter indent(int nb) {
    topContext().indent(nb);
    return this;
  }

  @Override
  @NonNull
  public JFormatter outdent(int nb) {
    topContext().outdent(nb);
    return this;
  }

  private static boolean _needSpace(final char c1, final char c2) {
    if (c1 == ']' && c2 == '{') {
      return true;
    }
    if (c1 == ';') {
      return true;
    }
    if (c1 == CLOSE_TYPE_ARGS) {
      // e.g., "public Foo<Bar> test;"
      if (c2 == '(') {
        // but not "new Foo<Bar>()"
        return false;
      }
      return true;
    }
    if (c1 == ')' && c2 == '{') {
      return true;
    }
    if (c1 == ',' || c1 == '=') {
      return true;
    }
    if (c2 == '=') {
      return true;
    }
    if (Character.isDigit(c1)) {
      if (c2 == '(' || c2 == ')' || c2 == ';' || c2 == ',') {
        return false;
      }
      return true;
    }
    if (Character.isJavaIdentifierPart(c1)) {
      switch (c2) {
      case '{':
      case '}':
      case '+':
      case '-':
      case '>':
      case '@':
        return true;
      default:
        return Character.isJavaIdentifierStart(c2);
      }
    }
    if (Character.isJavaIdentifierStart(c2)) {
      switch (c1) {
      case ']':
      case ')':
      case '}':
      case '+':
        return true;
      default:
        return false;
      }
    }
    if (Character.isDigit(c2)) {
      if (c1 == '(') {
        return false;
      }
      return true;
    }
    return false;
  }

  private void _spaceIfNeeded(final char c) {
    if (atBeginningOfLine()) {
      for (int i = 0; i < indentLevel(); i++) {
        topContext().append(m_oSettings.indent.string());
      }
    } else if (lastChar() != 0 && _needSpace(lastChar(), c)) {
      topContext().append(' ');
    }
  }

  @Override
  @NonNull
  public JFormatter print(final char c) {
    if (m_eMode == EMode.PRINTING) {
      if (c != CLOSE_TYPE_ARGS) {
        _spaceIfNeeded(c);
      }
      topContext().append(c);
    }
    return this;
  }

  @Override
  @NonNull
  public JFormatter print(@NonNull final String sStr) {
    if (m_eMode == EMode.PRINTING && sStr.length() > 0) {
      _spaceIfNeeded(sStr.charAt(0));
      topContext().append(sStr);
    }
    return this;
  }

  @Override
  @NonNull
  public JFormatter type(@NonNull final AbstractJClass aType) {
    switch (m_eMode) {
    case COLLECTING:
      if (!aType.isError()) {
        final String sShortName = aType.name();
        m_aCollectedReferences.computeIfAbsent(sShortName, k -> new NameUsage(sShortName)).addReferencedType(aType);
      }
      break;
    case PRINTING:
      if (aType.isError()) {
        print("Object");
      } else {
        // many of the JTypes in this list are either primitive or belong to
        // package java so we don't need a FQCN
        final boolean bCanUseShortName;
        if (m_aImportedClasses.contains(aType)) {
          bCanUseShortName = true;
        } else if (aType._package() == m_aPckJavaLang) {
          bCanUseShortName = _isUnambiguousJavaLangImport(aType);
        } else {
          bCanUseShortName = false;
        }

        if (bCanUseShortName) {
          // FQCN imported or not necessary, so generate short name
          print(aType.name());
        } else {
          final AbstractJClass aOuter = aType.outer();
          if (aOuter != null) {
            type(aOuter).print('.').print(aType.name());
          } else {
            // collision was detected, so generate FQCN
            print(aType.fullName());
          }
        }
      }
      break;
    case FIND_ERROR_TYPES:
      if (aType.isError()) {
        m_bContainsErrorTypes = true;
        }
      break;
    }
    return this;
  }

  @Override
  @NonNull
  public JFormatter id(@NonNull final String sID) {
    switch (m_eMode) {
    case COLLECTING:
      // see if there is a type name that collides with this id
      // not a type, but we need to create a place holder to
      // see if there might be a collision with a type
      m_aCollectedReferences.computeIfAbsent(sID, NameUsage::new).setVariableName();
      break;
    case PRINTING:
      print(sID);
      break;
    }
    return this;
  }

  @Override
  @NonNull
  public JFormatter newline() {
    if (m_eMode == EMode.PRINTING) {
      topContext().append(getNewLine());
    }
    return this;
  }

  @Override
  @NonNull
  public JFormatter generable(@NonNull final IJGenerable g) {
    g.generate(this);
    return this;
  }

  @NonNull
  public JFormatter generableLegacy(@NonNull final Collection<? extends IJGenerable> list, String separator) {
    if (!list.isEmpty()) {
      boolean bFirst = true;
      for (final IJGenerable item : list) {
        if (!bFirst) {
          print(separator);
        }
        generable(item);
        bFirst = false;
      }
    }
    return this;
  }

  @Override
  public @NonNull IJFormatter
      generable(@NonNull Collection<? extends IJGenerable> aList, String separator, ListWrapping wrapping) {
    if (settings().wrap.disabled) {
      return generableLegacy(aList, separator);
    }
    EListWrapStrategy selectedWrap = EListWrapStrategy.NEVER;
    if (wrapping != null) {
      selectedWrap = wrapping.condition;
    }
    int indentParam = 1;
    if (wrapping != null) {
      indentParam = wrapping.indent;
    }
    boolean wrapAfterSep = true;
    if (wrapping != null) {
      wrapAfterSep = wrapping.wrapAfterSep;
    }
    genericPrints(aList, selectedWrap, wrapAfterSep, o -> separator, indentParam, this::generable);
    return this;
  }

  @Override
  @NonNull
  public JFormatter declaration(@NonNull final IJDeclaration d) {
    d.declare(this);
    return this;
  }

  @Override
  @NonNull
  public JFormatter statement(@NonNull final IJStatement aStmt) {
    aStmt.state(this);
    return this;
  }

  @Override
  @NonNull
  public JFormatter var(@NonNull final JVar aVar) {
    aVar.bind(this);
    return this;
  }

  @Override
  public IJFormatter vars(@NonNull final Collection<? extends JVar> aList, ListWrapping wrapping) {
    EListWrapStrategy wrapCondition = EListWrapStrategy.PAST3;
    if (wrapping != null) {
      wrapCondition = wrapping.condition;
    }
    int indentParam = 1;
    if (wrapping != null) {
      indentParam = wrapping.indent;
    }
    boolean wrapAfterSep = true;
    if (wrapping != null) {
      wrapAfterSep = wrapping.wrapAfterSep;
    }

    genericPrints(aList, wrapCondition, wrapAfterSep,
        JVar::separator,
        indentParam,
        this::var);
    return this;
  }

  /// print a collection with wrapping strategy
  protected <T> JFormatter genericPrints(@NonNull final Collection<? extends T> aList,
      @NonNull EListWrapStrategy selectedWrap,
      boolean wrapAFterSep,
      Function<T, String> separator,
      int indentValue,
      Consumer<T> elementPrinter) {
    // if PAST3 and less equal 3 params, replace with NEVER.
    if (selectedWrap == EListWrapStrategy.PAST3
        && aList.size() <= 3) {
      selectedWrap = EListWrapStrategy.NEVER;
    }
    // for BINARY, try NEVER ; then if the produced line size is too big or
    // has newline, rollback and use ALWAYS instead.
    if (selectedWrap == EListWrapStrategy.BINARY) {
      try (IContextCloser o = addContextLayer().persistOnClose()) {
        genericPrintsStatic(aList, EListWrapStrategy.NEVER, wrapAFterSep,
            separator, indentValue,
            elementPrinter);
        if (o.value().contains(getNewLine())
            || currentLineSize() > settings().wrap.lineWidth) {
          o.rollback();
          selectedWrap = EListWrapStrategy.ALWAYS;
        } else {
          return this;
        }
      }
    }
    genericPrintsStatic(aList, selectedWrap, wrapAFterSep,
        separator, indentValue,
        elementPrinter);
    return this;
  }

  /// print several items with one-pass strategy.
  /// @param elementPrinter what should we do with each element
  protected <T> JFormatter genericPrintsStatic(@NonNull final Iterable<? extends T> aList,
      @NonNull EListWrapStrategy selectedWrap,
      boolean wrapAFterSep,
      Function<T, String> separator,
      int indentValue,
      Consumer<T> elementPrinter) {
    if (selectedWrap.twoPasses) {
      throw new RuntimeException("this method can't accept two-passes config " + selectedWrap);
    }
    T last = null;
    boolean indented = false;
    for (final T element : aList) {
      if (last == null) {
        last = element;
        if (selectedWrap == EListWrapStrategy.ALWAYS && !currentLine().isBlank()) {
          newline();
          indented = true;
          indent(indentValue);
        }
        if (selectedWrap == EListWrapStrategy.PAST3) {
          selectedWrap = EListWrapStrategy.ALWAYS;
        }
      } else {
        String sep = separator.apply(last);
        if (wrapAFterSep) {
          print(sep);
        }
        if (selectedWrap == EListWrapStrategy.REQUIRED) {
          try (IContextCloser o = addContextLayer().persistOnClose()) {
            elementPrinter.accept(element);
            if (o.value().contains(getNewLine())
                || currentLineSize() > settings().wrap.lineWidth) {
              o.rollback();
              newline();
            } else {
              continue;
            }
          }
        } else if (selectedWrap == EListWrapStrategy.ALWAYS) {
          newline();
        }
        if (!wrapAFterSep) {
          print(sep);
        }
      }
      elementPrinter.accept(element);
      if (!indented) {
        indented = true;
        indent(indentValue);
      }
    }
    if (indented) {
      outdent(indentValue);
    }
    return this;
  }

  private boolean _collectCausesNoAmbiguities(@NonNull final AbstractJClass aReference,
      @NonNull final JDefinedClass aClassToBeWritten) {
    if (m_bDebugImport) {
      LOGGER.info("_collectCausesNoAmbiguities(" + aReference.fullName() + ", " + aClassToBeWritten.fullName() + ")");
    }

    final NameUsage aUsages = m_aCollectedReferences.get(aReference.name());
    if (aUsages == null) {
      return true;
    }
    return !aUsages.isAmbiguousIn(aClassToBeWritten) && aUsages.containsReferencedType(aReference);
  }

  /**
   * determine if an import statement should be used for given class. This is a
   * matter of style and convention
   *
   * @param aReference
   *                          {@link AbstractJClass} referenced class
   * @param aClassToBeWritten
   *                          {@link AbstractJClass} currently generated class
   * @return <code>true</code> if an import statement can be used to shorten
   *         references to referenced class
   */
  private boolean _collectShouldBeImported(@NonNull final AbstractJClass aReference,
      @NonNull final JDefinedClass aClassToBeWritten) {
    if (m_bDebugImport) {
      LOGGER.info("_collectShouldBeImported(" + aReference.fullName() + ", " + aClassToBeWritten.fullName() + ")");
    }

    AbstractJClass aRealReference = aReference;
    if (aRealReference instanceof JAnonymousClass) {
      // get the super class of the anonymous class
      aRealReference = ((JAnonymousClass) aRealReference).base();
    }
    if (aRealReference instanceof JNarrowedClass) {
      // Remove the generic arguments
      aRealReference = aRealReference.erasure();
    }

    // Is it an inner class?
    final AbstractJClass aOuter = aRealReference.outer();
    if (aOuter != null) {
      // Import inner class only when it's name contain a name of enclosing
      // class.
      // In such case no information is lost when we refer to inner class
      // without mentioning it's enclosing class
      if (aRealReference.name().contains(aOuter.name())) {
        // Recurse
        if (_collectShouldBeImported(aOuter, aClassToBeWritten)) {
          return true;
        }
      }

      // Do not import inner classes in all other cases to aid
      // understandability/readability.
      return false;
    }
    return true;
  }

  /**
   * If reference is inner-class adds some outer class to the list of imported
   * classes if it
   *
   * @param aClassToBeWritten
   *                          {@link AbstractJClass} that may or may not have an
   *                          import
   * @param aClassToBeWritten
   *                          {@link AbstractJClass} that is the current class
   *                          being processed
   */
  private void _collectImportOuterClassIfCausesNoAmbiguities(@NonNull final AbstractJClass aReference,
      @NonNull final JDefinedClass aClassToBeWritten) {
    if (m_bDebugImport) {
      LOGGER.info("_collectImportOuterClassIfCausesNoAmbiguities(" + aReference.fullName() + ", "
          + aClassToBeWritten.fullName() + ")");
    }

    final AbstractJClass aOuter = aReference.outer();
    if (aOuter != null) {
      if (_collectCausesNoAmbiguities(aOuter, aClassToBeWritten)
          && _collectShouldBeImported(aOuter, aClassToBeWritten)) {
        m_aImportedClasses.add(aOuter);
      } else {
        // Recursive call
        _collectImportOuterClassIfCausesNoAmbiguities(aOuter, aClassToBeWritten);
      }
    }
  }

  private boolean _isUnambiguousJavaLangImport(@NonNull final AbstractJClass aJavaLangReference) {
    final NameUsage aNU = m_aCollectedReferences.get(aJavaLangReference.name());
    if (aNU == null) {
      return true;
    }
    final List<AbstractJClass> aRefs = aNU.getReferencedTypes();
    if (aRefs.size() > 1) {
      return false;
    }
    if (aRefs.isEmpty()) {
      return true;
    }
    // refs.size == 1
    return aRefs.get(0).equals(aJavaLangReference);
  }

  /**
   * determine if an import statement should be suppressed
   *
   * @param aReference
   *                          {@link AbstractJClass} that may or may not have an
   *                          import
   * @param aClassToBeWrittem
   *                          {@link AbstractJClass} that is the current class
   *                          being processed
   * @return true if an import statement should be suppressed, false otherwise
   */
  private boolean _printIsImplicitlyImported(@NonNull final AbstractJClass aReference,
      @NonNull final AbstractJClass aClassToBeWrittem) {
    if (m_bDebugImport) {
      LOGGER.info("_printIsImplicitlyImported(" + aReference.fullName() + ", " + aClassToBeWrittem.fullName() + ")");
    }

    AbstractJClass aRealReference = aReference;
    if (aRealReference instanceof JAnonymousClass) {
      // Get the super class of the anonymous class
      aRealReference = ((JAnonymousClass) aRealReference).base();
    }
    if (aRealReference instanceof JNarrowedClass) {
      // Remove generic type arguments
      aRealReference = aRealReference.erasure();
    }

    final JPackage aPackage = aRealReference._package();
    if (aPackage == null) {
      // May be null for JTypeVar and JTypeWildcard
      return true;
    }

    if (aPackage.isUnnamed()) {
      // Root package - no need to import something
      return true;
    }

    if (aPackage == m_aPckJavaLang) {
      // no need to explicitly import java.lang classes
      return true;
    }

    // All pkg local classes do not need an
    // import stmt for ref, except for inner classes
    if (aPackage == aClassToBeWrittem._package()) {
      AbstractJClass aOuter = aRealReference.outer();
      if (aOuter == null) // top-level class
      {
        // top-level package-local class needs no explicit import
        return true;
      }

      // inner-class
      AbstractJClass aTopLevelClass = aOuter;
      aOuter = aTopLevelClass.outer();
      while (aOuter != null) {
        aTopLevelClass = aOuter;
        aOuter = aTopLevelClass.outer();
      }

      // if reference is inner-class and
      // reference's top-level class is generated clazz,
      // i. e. reference is enclosed in generated clazz,
      // then it needs no explicit import statement.
      return aTopLevelClass == aClassToBeWrittem;
    }
    return false;
  }

  /**
   * Generates the whole source code out of the specified class.
   *
   * @param aClassToBeWritten
   *                          Class to be written
   */
  void writeClassFull(@NonNull final JDefinedClass aClassToBeWritten) {
    m_aPckJavaLang = aClassToBeWritten.owner()._package("java.lang");

    // first collect all the types and identifiers
    m_eMode = EMode.COLLECTING;
    m_aCollectedReferences.clear();
    m_aImportedClasses.clear();
    declaration(aClassToBeWritten);

    if (m_bDebugImport) {
      LOGGER.info("***Start collecting***");
    }

    // the class itself that we will be generating is always accessible and must
    // be the first import
    m_aImportedClasses.add(aClassToBeWritten);

    // collate type names and identifiers to determine which types can be
    // imported
    for (final NameUsage aUsage : m_aCollectedReferences.values()) {
      if (!aUsage.isAmbiguousIn(aClassToBeWritten) && !aUsage.isVariableName()) {
        final AbstractJClass aReferencedClass = aUsage.getSingleReferencedType();

        if (_collectShouldBeImported(aReferencedClass, aClassToBeWritten)) {
          m_aImportedClasses.add(aReferencedClass);
        } else {
          _collectImportOuterClassIfCausesNoAmbiguities(aReferencedClass, aClassToBeWritten);
        }
      } else if (aUsage.isTypeName()) {
        for (final AbstractJClass reference : aUsage.getReferencedTypes()) {
          _collectImportOuterClassIfCausesNoAmbiguities(reference, aClassToBeWritten);
        }
      }
    }

    if (m_bDebugImport) {
      LOGGER.info("***Finished collecting***");
    }

    // then print the declaration
    m_eMode = EMode.PRINTING;

    assert aClassToBeWritten.parentContainer().isPackage() : "this method is only for a pacakge-level class";

    // Header before package
    if (aClassToBeWritten.hasHeaderComment()) {
      generable(aClassToBeWritten.headerComment());
    }

    // Emit the package name (if not empty)
    final JPackage aPackage = (JPackage) aClassToBeWritten.parentContainer();
    if (!aPackage.isUnnamed()) {
      declaration(aPackage).newline();
    }

    // generate import statements
    boolean bAnyImport = false;
    for (final AbstractJClass aImportClass : m_aImportedClasses.getAllSorted()) {
      // suppress import statements for primitive types, built-in types,
      // types in the root package, and types in
      // the same package as the current type
      if (!_printIsImplicitlyImported(aImportClass, aClassToBeWritten)) {
        print("import").print(aImportClass.fullName()).print(';').newline();
        bAnyImport = true;

        if (m_bDebugImport) {
          LOGGER.info("  import " + aImportClass.fullName());
        }
      }
    }

    if (bAnyImport) {
      newline();
    }

    declaration(aClassToBeWritten);
  }

  /**
   * Add classes that should not be imported.
   *
   * @param aClasses
   *                 The classes to not be used in "import" statements. May be
   *                 <code>null</code>.
   */
  void addDontImportClasses(@Nullable final Iterable<? extends AbstractJClass> aClasses) {
    if (aClasses != null) {
      for (final AbstractJClass aClass : aClasses) {
        m_aImportedClasses.addDontImportClass(aClass);
      }
    }
  }

  public static boolean containsErrorTypes(@NonNull final JDefinedClass aClass) {
    try (final JFormatter aFormatter =
        new JFormatter(new SourcePrintWriter(NullWriter.getInstance(), "\n"),
            new FormatterSettings().configure(o -> o.indent.useTabs()))) {
      aFormatter.m_eMode = EMode.FIND_ERROR_TYPES;
      aFormatter.m_bContainsErrorTypes = false;
      aFormatter.declaration(aClass);
      return aFormatter.m_bContainsErrorTypes;
    }
  }

  /// @return the current line being written, in a buffer or in the printstream
  public String currentLine() {
    return topContext().getCurrentLine();
  }

  public char lastChar() {
    return topContext().getLastChar();
  }

  public int indentLevel() {
    return topContext().getIndentLevel();
  }


  public boolean atBeginningOfLine() {
    return currentLine().isEmpty();
  }

  @Override
  public int currentLineSize() {
    return sizeWithTabsExpanded(currentLine(), m_oSettings.indent.tabSize);
  }

  /// computes the size of a string when tabs are expanded to match column size.
  /// @param s the string to expand
  /// @param tabSize size of a column a tab expands into.
  // package-protected for tests
  static int sizeWithTabsExpanded(String s, int columnSize) {
    if (s == null || s.isEmpty()) {
      return 0;
    }
    int tabIndent = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '\t') {
        int modtab = (i + 1 + tabIndent) % columnSize;
        if (modtab > 0) {
          tabIndent += columnSize - modtab;
        }
      }
    }
    return s.length() + tabIndent;

  }

  //
  // contexts management
  //

  /// internal notion of writer context
  private interface WriteContext {

    int getIndentLevel();

    void setIndentLevel(int nb);

    char getLastChar();

    default void indent(int nb) {
      setIndentLevel(getIndentLevel() + nb);
    }

    default void outdent(int nb) {
      setIndentLevel(getIndentLevel() - nb);
    }

    String getCurrentLine();

    String getNewLine();

    default void append(String sStr) {
      if (sStr == null || sStr.isEmpty()) {
        return;
      }
      char lastChar = 0;
      boolean resetLine = false;
      String appendLine = null;
      if (sStr.contains(getNewLine())) {
        resetLine = true;
        int offset = sStr.lastIndexOf(getNewLine()) + getNewLine().length();
        appendLine = sStr.substring(offset);
        lastChar = appendLine.isEmpty() ? 0 : appendLine.charAt(appendLine.length() - 1);
      } else {
        appendLine = sStr;
        lastChar = sStr.charAt(sStr.length() - 1);
      }

      append(sStr, resetLine, appendLine, lastChar);
    }

    void append(String fullString, boolean resetLine, String appendLine, char lastChar);

    void append(char c);
  }

  /// Represents a context layer that *should* be automatically removed outside of
  /// its declaration scope, using try-with-resource syntax.
  ///
  /// Defaults to a discarding context, so any data added to the formatter while
  /// context active is discarded once the resource is closed. This can be changed
  /// with #persistOnClose or simply committing the data wit #commit
  ///
  /// The #close method does not throw exception to have it simpler in the
  /// try-declaration. It also only applies once, as encouraged by
  /// [AutoCloseable#close] .
  ///
  /// This also contains a #currentLine that is copied from the underlying buffer
  /// (or JFormatter if none), then updated like the JFormater is ; same for
  /// lastchar and beginning of line
  ///
  /// This class is not thread safe.
  // not static to directly access the fields and methods
  public class FormatterContext implements IContextCloser, WriteContext {

    // public final, so no getter/setter
    public final StringBuilder buffer;

    StringBuilder currentLine = new StringBuilder(currentLine());

    private char m_cLastChar = lastChar();

    private int m_nIndentLevel = indentLevel();

    /// when set to true, upon first #close, the context is re written into the
    /// formatter, or into the next context if any.
    boolean persistOnClose = false;

    boolean closed = false;

    // construct

    public FormatterContext(StringBuilder buffer) {
      this.buffer = buffer;
    }

    public FormatterContext() {
      this(new StringBuilder());
    }

    // getters/setters

    @Override
    public String value() {
      return buffer.toString();
    }

    @Override
    public String getCurrentLine() {
      return currentLine.toString();
    }

    @Override
    public char getLastChar() {
      return m_cLastChar;
    }

    @Override
    public int getIndentLevel() {
      return m_nIndentLevel;
    }

    @Override
    public void setIndentLevel(int nb) {
      m_nIndentLevel = nb;
    }

    /// @return true after #close has been called at least once.
    @Override
    public boolean isClosed() {
      return closed;
    }

    @Override
    public String getNewLine() {
      return JFormatter.this.getNewLine();
    }

    // other methods

    @Override
    public FormatterContext persistOnClose(boolean b) {
      persistOnClose = b;
      return this;
    }

    @Override
    public void close() {
      if (closed) {
        return;
      }
      contextLayers.remove(this);
      if (persistOnClose) {
        // printing the buffer will update the last char and current line in the next
        // context.
        topContext().append(buffer.toString());
        topContext().setIndentLevel(getIndentLevel());
      }
      closed = true;
    }

    @Override
    public void append(String fullString, boolean resetLine, String lastLine, char lastChar) {
      buffer.append(fullString);
      m_cLastChar = lastChar;
      if (resetLine) {
        currentLine = new StringBuilder();
      }
      currentLine.append(lastLine);
    }

    @Override
    public void append(char c) {
      char printedChar = c == CLOSE_TYPE_ARGS ? '>' : c;
      buffer.append(printedChar);
      currentLine.append(printedChar);
      m_cLastChar = c;
    }
  }

  /// @return the top (last) context, if any ; or this as a writer context if
  /// none.
  protected WriteContext topContext() {
    return contextLayers.isEmpty() ? asContext : contextLayers.get(0);
  }

  ///
  /// @return a new [FormatterContext], put on top of the previous ones to receive
  /// incoming writes.
  @Override
  public IContextCloser addContextLayer() {
    FormatterContext ret = new FormatterContext();
    contextLayers.add(0, ret);
    return ret;
  }
}
