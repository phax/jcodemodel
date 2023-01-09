/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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
package com.helger.jcodemodel.writer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJDeclaration;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJGenerable;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JAnonymousClass;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JNarrowedClass;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.SourcePrintWriter;
import com.helger.jcodemodel.util.ClassNameComparator;
import com.helger.jcodemodel.util.JCValueEnforcer;
import com.helger.jcodemodel.util.NullWriter;

/**
 * This is a utility class for managing indentation and other basic formatting
 * for PrintWriter.
 */
@NotThreadSafe
public class JFormatter implements IJFormatter
{
  /**
   * Used during the optimization of class imports. List of
   * {@link AbstractJClass}es whose short name is the same.
   *
   * @author Ryan.Shoemaker@Sun.COM
   */
  private final class NameUsage
  {
    private final String m_sName;

    private final List <AbstractJClass> m_aReferencedClasses = new ArrayList <> ();

    /** true if this name is used as an identifier (like a variable name.) **/
    private boolean m_bIsVariableName;

    public NameUsage (@Nonnull final String sName)
    {
      m_sName = sName;
    }

    /**
     * @param aEnclosingClass
     *        the class to check
     * @return <code>true</code> if the short name is ambiguous in context of
     *         enclosingClass and classes with this name can't be imported.
     */
    public boolean isAmbiguousIn (@Nonnull final JDefinedClass aEnclosingClass)
    {
      // more than one type with the same name
      if (m_aReferencedClasses.size () > 1)
        return true;

      // an id and (at least one) type with the same name
      if (m_bIsVariableName && !m_aReferencedClasses.isEmpty ())
        return true;

      // no references is always unambiguous
      if (m_aReferencedClasses.isEmpty ())
        return false;

      // we have exactly one reference
      AbstractJClass aSingleRef = m_aReferencedClasses.get (0);
      if (aSingleRef instanceof JAnonymousClass)
      {
        aSingleRef = ((JAnonymousClass) aSingleRef).base ();
      }

      // special case where a generated type collides with a type in package
      // java.lang
      if (aSingleRef._package () == JFormatter.this.m_aPckJavaLang)
      {
        // make sure that there's no other class with this name within the
        // same package
        for (final JDefinedClass aClass : aEnclosingClass._package ().classes ())
        {
          // even if this is the only "String" class we use,
          // if the class called "String" is in the same package,
          // we still need to import it.
          if (aClass.name ().equals (aSingleRef.name ()))
          {
            // collision -> ambiguous
            return true;
          }
        }
      }

      return false;
    }

    public boolean addReferencedType (@Nonnull final AbstractJClass aClazz)
    {
      if (false)
        System.out.println ("Adding referenced type[" + m_sName + "]: " + aClazz.fullName ());
      if (m_aReferencedClasses.contains (aClazz))
        return false;
      return m_aReferencedClasses.add (aClazz);
    }

    public boolean containsReferencedType (@Nullable final AbstractJClass aClazz)
    {
      return m_aReferencedClasses.contains (aClazz);
    }

    @Nonnull
    public AbstractJClass getSingleReferencedType ()
    {
      assert m_aReferencedClasses.size () == 1;
      return m_aReferencedClasses.get (0);
    }

    @Nonnull
    public List <AbstractJClass> getReferencedTypes ()
    {
      return m_aReferencedClasses;
    }

    public void setVariableName ()
    {
      // Check if something can be a variable or a type
      for (final AbstractJClass aRefedType : m_aReferencedClasses)
        if (aRefedType.outer () != null)
        {
          m_bIsVariableName = false;
          return;
        }
      m_bIsVariableName = true;
    }

    /**
     * @return true if this name is used as an identifier (like a variable
     *         name.).
     */
    public boolean isVariableName ()
    {
      return m_bIsVariableName;
    }

    /**
     * @return true if this name is used as an type name (like class name.)
     */
    public boolean isTypeName ()
    {
      return !m_aReferencedClasses.isEmpty ();
    }

    @Override
    public String toString ()
    {
      final StringBuilder aSB = new StringBuilder ("Usages[").append (m_sName).append ("]");
      aSB.append ("; isVarName=").append (m_bIsVariableName);
      aSB.append ("; refedClasses=").append (m_aReferencedClasses);
      return aSB.toString ();
    }
  }

  private static enum EMode
  {
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

  private static final Map <String, Boolean> RESERVERD_JAVA_LANG_NAME = new HashMap <> ();

  /**
   * check if a name already represents a class in the "java.lang" package. e.g.
   * "String" should return <code>true</code>, but "blub" should return
   * <code>false</code>. See https://github.com/phax/jcodemodel/issues/71
   *
   * @param sSimpleClassName
   *        simple class name to test (without the package).
   * @return the existence of such a class in the java.lang (cached)
   */
  static boolean isJavaLangClass (final String sSimpleClassName)
  {
    if (sSimpleClassName == null)
      return false;

    final Boolean ret = RESERVERD_JAVA_LANG_NAME.get (sSimpleClassName);
    if (ret != null)
      return ret.booleanValue ();

    boolean bIsJavaLang = true;
    try
    {
      Class.forName ("java.lang." + sSimpleClassName);
    }
    catch (final Exception ex)
    {
      bIsJavaLang = false;
    }
    RESERVERD_JAVA_LANG_NAME.put (sSimpleClassName, Boolean.valueOf (bIsJavaLang));
    return bIsJavaLang;
  }

  private final class ImportedClasses
  {
    private final Set <AbstractJClass> m_aDontImportClasses = new HashSet <> ();
    private final Set <AbstractJClass> m_aClasses = new HashSet <> ();
    private final Set <String> m_aNames = new HashSet <> ();

    public ImportedClasses ()
    {}

    @Nullable
    private AbstractJClass _getClassForImport (@Nullable final AbstractJClass aClass)
    {
      AbstractJClass aRealClass = aClass;
      if (aRealClass instanceof JAnonymousClass)
      {
        // get the super class of the anonymous class
        return _getClassForImport (((JAnonymousClass) aRealClass).base ());
      }
      if (aRealClass instanceof JNarrowedClass)
      {
        // Never imported narrowed class but the erasure only
        aRealClass = aRealClass.erasure ();
      }
      return aRealClass;
    }

    public void addDontImportClass (@Nonnull final AbstractJClass aClass)
    {
      final AbstractJClass aRealClass = _getClassForImport (aClass);
      m_aDontImportClasses.add (aRealClass);
    }

    public boolean add (@Nonnull final AbstractJClass aClass)
    {
      final AbstractJClass aRealClass = _getClassForImport (aClass);
      final String sSimpleName = aRealClass.name ();

      if (m_aDontImportClasses.contains (aRealClass))
      {
        if (m_bDebugImport)
          System.out.println ("The class '" + aRealClass.fullName () + "' should not be imported!");
        return false;
      }

      // Avoid importing 2 classes with the same class name
      if (!m_aNames.add (sSimpleName))
      {
        if (m_bDebugImport)
          System.out.println ("A class with local name '" + sSimpleName + "' is already in the import list.");
        return false;
      }

      if (!m_aClasses.add (aRealClass))
      {
        if (m_bDebugImport)
          System.out.println ("The class '" + aRealClass.fullName () + "' is already in the import list.");
        return false;
      }

      if (m_bDebugImport)
        System.out.println ("Added import class '" + aClass.fullName () + "'");
      return true;
    }

    public boolean contains (@Nullable final AbstractJClass aClass)
    {
      final AbstractJClass aRealClass = _getClassForImport (aClass);

      return m_aClasses.contains (aRealClass);
    }

    public void clear ()
    {
      m_aClasses.clear ();
      m_aNames.clear ();
    }

    @Nonnull
    public List <AbstractJClass> getAllSorted ()
    {
      // Copy and sort
      final List <AbstractJClass> aImports = new ArrayList <> (m_aClasses);
      aImports.sort (ClassNameComparator.getInstance ());
      return aImports;
    }
  }

  /**
   * all classes and ids encountered during the collection mode.<br>
   * map from short type name to {@link NameUsage} (list of
   * {@link AbstractJClass} and ids sharing that name)
   **/
  private final Map <String, NameUsage> m_aCollectedReferences = new HashMap <> ();

  /**
   * set of imported types (including package java types, even though we won't
   * generate imports for them)
   */
  private final ImportedClasses m_aImportedClasses = new ImportedClasses ();

  /**
   * The current running mode. Set to PRINTING so that a casual client can use a
   * formatter just like before.
   */
  private EMode m_eMode = EMode.PRINTING;

  /**
   * Current number of indentation strings to print
   */
  private int m_nIndentLevel;

  /**
   * String to be used for each indentation. Defaults to four spaces.
   */
  private final String m_sIndentString;

  /**
   * Writer associated with this {@link IJFormatter}
   */
  private final SourcePrintWriter m_aPW;

  private char m_cLastChar = 0;
  private boolean m_bAtBeginningOfLine = true;
  private JPackage m_aPckJavaLang;

  /**
   * Only used by {@link IJFormatter#containsErrorTypes(JDefinedClass)
   * containsErrorTypes} method
   */
  private boolean m_bContainsErrorTypes;

  private boolean m_bDebugImport = false;

  /**
   * Constructor
   *
   * @param aPW
   *        {@link PrintWriter} to {@link IJFormatter} to use. May not be
   *        <code>null</code>.
   * @param sIndentString
   *        Incremental indentation string, similar to tab value. May not be
   *        <code>null</code>.
   */
  public JFormatter (@Nonnull final SourcePrintWriter aPW, @Nonnull final String sIndentString)
  {
    JCValueEnforcer.notNull (aPW, "PrintWriter");
    JCValueEnforcer.notNull (sIndentString, "IndentString");

    m_aPW = aPW;
    m_sIndentString = sIndentString;
  }

  public void setDebugImports (final boolean bDebug)
  {
    m_bDebugImport = bDebug;
  }

  public boolean isDebugImports ()
  {
    return m_bDebugImport;
  }

  /**
   * Closes this formatter.
   */
  public void close ()
  {
    m_aPW.close ();
  }

  public boolean isPrinting ()
  {
    return m_eMode == EMode.PRINTING;
  }

  @Nonnull
  public JFormatter indent ()
  {
    m_nIndentLevel++;
    return this;
  }

  @Nonnull
  public JFormatter outdent ()
  {
    m_nIndentLevel--;
    return this;
  }

  private static boolean _needSpace (final char c1, final char c2)
  {
    if ((c1 == ']') && (c2 == '{'))
      return true;
    if (c1 == ';')
      return true;
    if (c1 == CLOSE_TYPE_ARGS)
    {
      // e.g., "public Foo<Bar> test;"
      if (c2 == '(')
      {
        // but not "new Foo<Bar>()"
        return false;
      }
      return true;
    }
    if ((c1 == ')') && (c2 == '{'))
      return true;
    if ((c1 == ',') || (c1 == '='))
      return true;
    if (c2 == '=')
      return true;
    if (Character.isDigit (c1))
    {
      if ((c2 == '(') || (c2 == ')') || (c2 == ';') || (c2 == ','))
        return false;
      return true;
    }
    if (Character.isJavaIdentifierPart (c1))
    {
      switch (c2)
      {
        case '{':
        case '}':
        case '+':
        case '-':
        case '>':
        case '@':
          return true;
        default:
          return Character.isJavaIdentifierStart (c2);
      }
    }
    if (Character.isJavaIdentifierStart (c2))
    {
      switch (c1)
      {
        case ']':
        case ')':
        case '}':
        case '+':
          return true;
        default:
          return false;
      }
    }
    if (Character.isDigit (c2))
    {
      if (c1 == '(')
        return false;
      return true;
    }
    return false;
  }

  private void _spaceIfNeeded (final char c)
  {
    if (m_bAtBeginningOfLine)
    {
      for (int i = 0; i < m_nIndentLevel; i++)
        m_aPW.print (m_sIndentString);
      m_bAtBeginningOfLine = false;
    }
    else
      if (m_cLastChar != 0 && _needSpace (m_cLastChar, c))
        m_aPW.print (' ');
  }

  @Nonnull
  public JFormatter print (final char c)
  {
    if (m_eMode == EMode.PRINTING)
    {
      if (c == CLOSE_TYPE_ARGS)
      {
        m_aPW.print ('>');
      }
      else
      {
        _spaceIfNeeded (c);
        m_aPW.print (c);
      }
      m_cLastChar = c;
    }
    return this;
  }

  @Nonnull
  public JFormatter print (@Nonnull final String sStr)
  {
    if (m_eMode == EMode.PRINTING && sStr.length () > 0)
    {
      _spaceIfNeeded (sStr.charAt (0));
      m_aPW.print (sStr);
      m_cLastChar = sStr.charAt (sStr.length () - 1);
    }
    return this;
  }

  @Nonnull
  public JFormatter type (@Nonnull final AbstractJClass aType)
  {
    switch (m_eMode)
    {
      case COLLECTING:
        if (!aType.isError ())
        {
          final String sShortName = aType.name ();
          m_aCollectedReferences.computeIfAbsent (sShortName, k -> new NameUsage (sShortName))
                                .addReferencedType (aType);
        }
        break;
      case PRINTING:
        if (aType.isError ())
        {
          print ("Object");
        }
        else
        {
          // many of the JTypes in this list are either primitive or belong to
          // package java so we don't need a FQCN
          final boolean bCanUseShortName;
          if (m_aImportedClasses.contains (aType))
            bCanUseShortName = true;
          else
            if (aType._package () == m_aPckJavaLang)
              bCanUseShortName = _isUnambiguousJavaLangImport (aType);
            else
              bCanUseShortName = false;

          if (bCanUseShortName)
          {
            // FQCN imported or not necessary, so generate short name
            print (aType.name ());
          }
          else
          {
            final AbstractJClass aOuter = aType.outer ();
            if (aOuter != null)
            {
              type (aOuter).print ('.').print (aType.name ());
            }
            else
            {
              // collision was detected, so generate FQCN
              print (aType.fullName ());
            }
          }
        }
        break;
      case FIND_ERROR_TYPES:
        if (aType.isError ())
          m_bContainsErrorTypes = true;
        break;
    }
    return this;
  }

  @Nonnull
  public JFormatter id (@Nonnull final String sID)
  {
    switch (m_eMode)
    {
      case COLLECTING:
        // see if there is a type name that collides with this id
        // not a type, but we need to create a place holder to
        // see if there might be a collision with a type
        m_aCollectedReferences.computeIfAbsent (sID, k -> new NameUsage (k)).setVariableName ();
        break;
      case PRINTING:
        print (sID);
        break;
    }
    return this;
  }

  @Nonnull
  public JFormatter newline ()
  {
    if (m_eMode == EMode.PRINTING)
    {
      m_aPW.println ();
      m_cLastChar = 0;
      m_bAtBeginningOfLine = true;
    }
    return this;
  }

  @Nonnull
  public JFormatter generable (@Nonnull final IJGenerable g)
  {
    g.generate (this);
    return this;
  }

  @Nonnull
  public JFormatter generable (@Nonnull final Collection <? extends IJGenerable> list)
  {
    if (!list.isEmpty ())
    {
      boolean bFirst = true;
      for (final IJGenerable item : list)
      {
        if (!bFirst)
          print (',');
        generable (item);
        bFirst = false;
      }
    }
    return this;
  }

  @Nonnull
  public JFormatter declaration (@Nonnull final IJDeclaration d)
  {
    d.declare (this);
    return this;
  }

  @Nonnull
  public JFormatter statement (@Nonnull final IJStatement aStmt)
  {
    aStmt.state (this);
    return this;
  }

  @Nonnull
  public JFormatter var (@Nonnull final JVar aVar)
  {
    aVar.bind (this);
    return this;
  }

  private boolean _collectCausesNoAmbiguities (@Nonnull final AbstractJClass aReference,
                                               @Nonnull final JDefinedClass aClassToBeWritten)
  {
    if (m_bDebugImport)
      System.out.println ("_collectCausesNoAmbiguities(" +
                          aReference.fullName () +
                          ", " +
                          aClassToBeWritten.fullName () +
                          ")");

    final NameUsage aUsages = m_aCollectedReferences.get (aReference.name ());
    if (aUsages == null)
      return true;
    return !aUsages.isAmbiguousIn (aClassToBeWritten) && aUsages.containsReferencedType (aReference);
  }

  /**
   * determine if an import statement should be used for given class. This is a
   * matter of style and convention
   *
   * @param aReference
   *        {@link AbstractJClass} referenced class
   * @param aClassToBeWritten
   *        {@link AbstractJClass} currently generated class
   * @return <code>true</code> if an import statement can be used to shorten
   *         references to referenced class
   */
  private boolean _collectShouldBeImported (@Nonnull final AbstractJClass aReference,
                                            @Nonnull final JDefinedClass aClassToBeWritten)
  {
    if (m_bDebugImport)
      System.out.println ("_collectShouldBeImported(" +
                          aReference.fullName () +
                          ", " +
                          aClassToBeWritten.fullName () +
                          ")");

    AbstractJClass aRealReference = aReference;
    if (aRealReference instanceof JAnonymousClass)
    {
      // get the super class of the anonymous class
      aRealReference = ((JAnonymousClass) aRealReference).base ();
    }
    if (aRealReference instanceof JNarrowedClass)
    {
      // Remove the generic arguments
      aRealReference = aRealReference.erasure ();
    }

    // Is it an inner class?
    final AbstractJClass aOuter = aRealReference.outer ();
    if (aOuter != null)
    {
      // Import inner class only when it's name contain a name of enclosing
      // class.
      // In such case no information is lost when we refer to inner class
      // without mentioning it's enclosing class
      if (aRealReference.name ().contains (aOuter.name ()))
      {
        // Recurse
        if (_collectShouldBeImported (aOuter, aClassToBeWritten))
          return true;
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
   *        {@link AbstractJClass} that may or may not have an import
   * @param aClassToBeWritten
   *        {@link AbstractJClass} that is the current class being processed
   */
  private void _collectImportOuterClassIfCausesNoAmbiguities (@Nonnull final AbstractJClass aReference,
                                                              @Nonnull final JDefinedClass aClassToBeWritten)
  {
    if (m_bDebugImport)
      System.out.println ("_collectImportOuterClassIfCausesNoAmbiguities(" +
                          aReference.fullName () +
                          ", " +
                          aClassToBeWritten.fullName () +
                          ")");

    final AbstractJClass aOuter = aReference.outer ();
    if (aOuter != null)
    {
      if (_collectCausesNoAmbiguities (aOuter, aClassToBeWritten) &&
          _collectShouldBeImported (aOuter, aClassToBeWritten))
      {
        m_aImportedClasses.add (aOuter);
      }
      else
      {
        // Recursive call
        _collectImportOuterClassIfCausesNoAmbiguities (aOuter, aClassToBeWritten);
      }
    }
  }

  private boolean _isUnambiguousJavaLangImport (@Nonnull final AbstractJClass aJavaLangReference)
  {
    final NameUsage aNU = m_aCollectedReferences.get (aJavaLangReference.name ());
    if (aNU == null)
      return true;
    final List <AbstractJClass> aRefs = aNU.getReferencedTypes ();
    if (aRefs.size () > 1)
      return false;
    if (aRefs.isEmpty ())
      return true;
    // refs.size == 1
    return aRefs.get (0).equals (aJavaLangReference);
  }

  /**
   * determine if an import statement should be suppressed
   *
   * @param aReference
   *        {@link AbstractJClass} that may or may not have an import
   * @param aClassToBeWrittem
   *        {@link AbstractJClass} that is the current class being processed
   * @return true if an import statement should be suppressed, false otherwise
   */
  private boolean _printIsImplicitlyImported (@Nonnull final AbstractJClass aReference,
                                              @Nonnull final AbstractJClass aClassToBeWrittem)
  {
    if (m_bDebugImport)
      System.out.println ("_printIsImplicitlyImported(" +
                          aReference.fullName () +
                          ", " +
                          aClassToBeWrittem.fullName () +
                          ")");

    AbstractJClass aRealReference = aReference;
    if (aRealReference instanceof JAnonymousClass)
    {
      // Get the super class of the anonymous class
      aRealReference = ((JAnonymousClass) aRealReference).base ();
    }
    if (aRealReference instanceof JNarrowedClass)
    {
      // Remove generic type arguments
      aRealReference = aRealReference.erasure ();
    }

    final JPackage aPackage = aRealReference._package ();
    if (aPackage == null)
    {
      // May be null for JTypeVar and JTypeWildcard
      return true;
    }

    if (aPackage.isUnnamed ())
    {
      // Root package - no need to import something
      return true;
    }

    if (aPackage == m_aPckJavaLang)
    {
      // no need to explicitly import java.lang classes
      return true;
    }

    // All pkg local classes do not need an
    // import stmt for ref, except for inner classes
    if (aPackage == aClassToBeWrittem._package ())
    {
      AbstractJClass aOuter = aRealReference.outer ();
      if (aOuter == null) // top-level class
      {
        // top-level package-local class needs no explicit import
        return true;
      }

      // inner-class
      AbstractJClass aTopLevelClass = aOuter;
      aOuter = aTopLevelClass.outer ();
      while (aOuter != null)
      {
        aTopLevelClass = aOuter;
        aOuter = aTopLevelClass.outer ();
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
   *        Class to be written
   */
  void writeClassFull (@Nonnull final JDefinedClass aClassToBeWritten)
  {
    m_aPckJavaLang = aClassToBeWritten.owner ()._package ("java.lang");

    // first collect all the types and identifiers
    m_eMode = EMode.COLLECTING;
    m_aCollectedReferences.clear ();
    m_aImportedClasses.clear ();
    declaration (aClassToBeWritten);

    if (m_bDebugImport)
      System.out.println ("***Start collecting***");

    // the class itself that we will be generating is always accessible and must
    // be the first import
    m_aImportedClasses.add (aClassToBeWritten);

    // collate type names and identifiers to determine which types can be
    // imported
    for (final NameUsage aUsage : m_aCollectedReferences.values ())
    {
      if (!aUsage.isAmbiguousIn (aClassToBeWritten) && !aUsage.isVariableName ())
      {
        final AbstractJClass aReferencedClass = aUsage.getSingleReferencedType ();

        if (_collectShouldBeImported (aReferencedClass, aClassToBeWritten))
        {
          m_aImportedClasses.add (aReferencedClass);
        }
        else
        {
          _collectImportOuterClassIfCausesNoAmbiguities (aReferencedClass, aClassToBeWritten);
        }
      }
      else
      {
        if (aUsage.isTypeName ())
          for (final AbstractJClass reference : aUsage.getReferencedTypes ())
          {
            _collectImportOuterClassIfCausesNoAmbiguities (reference, aClassToBeWritten);
          }
      }
    }

    if (m_bDebugImport)
      System.out.println ("***Finished collecting***");

    // then print the declaration
    m_eMode = EMode.PRINTING;

    assert aClassToBeWritten.parentContainer ().isPackage () : "this method is only for a pacakge-level class";

    // Header before package
    if (aClassToBeWritten.hasHeaderComment ())
      generable (aClassToBeWritten.headerComment ());

    // Emit the package name (if not empty)
    final JPackage aPackage = (JPackage) aClassToBeWritten.parentContainer ();
    if (!aPackage.isUnnamed ())
    {
      declaration (aPackage).newline ();
    }

    // generate import statements
    boolean bAnyImport = false;
    for (final AbstractJClass aImportClass : m_aImportedClasses.getAllSorted ())
    {
      // suppress import statements for primitive types, built-in types,
      // types in the root package, and types in
      // the same package as the current type
      if (!_printIsImplicitlyImported (aImportClass, aClassToBeWritten))
      {
        print ("import").print (aImportClass.fullName ()).print (';').newline ();
        bAnyImport = true;

        if (m_bDebugImport)
          System.out.println ("  import " + aImportClass.fullName ());
      }
    }

    if (bAnyImport)
      newline ();

    declaration (aClassToBeWritten);
  }

  /**
   * Add classes that should not be imported.
   *
   * @param aClasses
   *        The classes to not be used in "import" statements. May be
   *        <code>null</code>.
   */
  void addDontImportClasses (@Nullable final Iterable <? extends AbstractJClass> aClasses)
  {
    if (aClasses != null)
      for (final AbstractJClass aClass : aClasses)
        m_aImportedClasses.addDontImportClass (aClass);
  }

  public static boolean containsErrorTypes (@Nonnull final JDefinedClass aClass)
  {
    try (final JFormatter aFormatter = new JFormatter (new SourcePrintWriter (NullWriter.getInstance (), "\n"), "\t"))
    {
      aFormatter.m_eMode = EMode.FIND_ERROR_TYPES;
      aFormatter.m_bContainsErrorTypes = false;
      aFormatter.declaration (aClass);
      return aFormatter.m_bContainsErrorTypes;
    }
  }
}
