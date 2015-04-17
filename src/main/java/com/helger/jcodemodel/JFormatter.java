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

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.ClassNameComparator;
import com.helger.jcodemodel.util.NullWriter;

/**
 * This is a utility class for managing indentation and other basic formatting
 * for PrintWriter.
 */
public class JFormatter implements Closeable
{
  public static boolean containsErrorTypes (@Nonnull final JDefinedClass c)
  {
    final JFormatter formatter = new JFormatter (NullWriter.getInstance ());
    formatter.m_eMode = EMode.FIND_ERROR_TYPES;
    formatter.m_bContainsErrorTypes = false;
    formatter.declaration (c);
    return formatter.m_bContainsErrorTypes;
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
     * generate anything.
     * <p>
     * Only used by {@link JFormatter#containsErrorTypes(JDefinedClass)
     * containsErrorTypes} method
     */
    FIND_ERROR_TYPES
  }

  public static final String DEFAULT_INDENT_SPACE = "    ";

  /**
   * Special character token we use to differentiate '>' as an operator and '>'
   * as the end of the type arguments. The former uses '>' and it requires a
   * preceding whitespace. The latter uses this, and it does not have a
   * preceding whitespace.
   */
  /* package */static final char CLOSE_TYPE_ARGS = '\uFFFF';

  /** all classes and ids encountered during the collection mode **/
  /**
   * map from short type name to ReferenceList (list of JClass and ids sharing
   * that name)
   **/
  private final Map <String, Usages> m_aCollectedReferences = new HashMap <String, Usages> ();

  /**
   * set of imported types (including package java types, even though we won't
   * generate imports for them)
   */
  private final Set <AbstractJClass> m_aImportedClasses = new HashSet <AbstractJClass> ();

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
  private final String m_sIndentSpace;

  /**
   * Stream associated with this JFormatter
   */
  private final PrintWriter m_aPW;

  private char m_cLastChar = 0;
  private boolean m_bAtBeginningOfLine = true;
  private JPackage m_aPckJavaLang;

  /**
   * Only used by {@link JFormatter#containsErrorTypes(JDefinedClass)
   * containsErrorTypes} method
   */
  private boolean m_bContainsErrorTypes;

  /**
   * Creates a JFormatter.
   *
   * @param aPW
   *        {@link PrintWriter} to {@link JFormatter} to use. May not be
   *        <code>null</code>.
   * @param sIndentSpace
   *        Incremental indentation string, similar to tab value. May not be
   *        <code>null</code>.
   */
  public JFormatter (@Nonnull final PrintWriter aPW, @Nonnull final String sIndentSpace)
  {
    if (aPW == null)
      throw new NullPointerException ("PrintWriter");
    if (sIndentSpace == null)
      throw new NullPointerException ("Indent space");

    m_aPW = aPW;
    m_sIndentSpace = sIndentSpace;
  }

  /**
   * Creates a formatter with default incremental indentations of four spaces.
   */
  public JFormatter (@Nonnull final PrintWriter aPW)
  {
    this (aPW, DEFAULT_INDENT_SPACE);
  }

  /**
   * Creates a formatter with default incremental indentations of four spaces.
   */
  public JFormatter (@Nonnull final Writer aWriter)
  {
    this (aWriter instanceof PrintWriter ? (PrintWriter) aWriter : new PrintWriter (aWriter));
  }

  /**
   * Closes this formatter.
   */
  public void close ()
  {
    m_aPW.close ();
  }

  /**
   * Returns true if we are in the printing mode, where we actually produce
   * text. The other mode is the "collecting mode'
   */
  public boolean isPrinting ()
  {
    return m_eMode == EMode.PRINTING;
  }

  /**
   * Decrement the indentation level.
   */
  @Nonnull
  public JFormatter outdent ()
  {
    m_nIndentLevel--;
    return this;
  }

  /**
   * Increment the indentation level.
   */
  @Nonnull
  public JFormatter indent ()
  {
    m_nIndentLevel++;
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
        m_aPW.print (m_sIndentSpace);
      m_bAtBeginningOfLine = false;
    }
    else
      if ((m_cLastChar != 0) && _needSpace (m_cLastChar, c))
        m_aPW.print (' ');
  }

  /**
   * Print a char into the stream
   *
   * @param c
   *        the char
   * @return this
   */
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

  /**
   * Print a String into the stream
   *
   * @param s
   *        the String
   * @return this
   */
  @Nonnull
  public JFormatter print (@Nonnull final String s)
  {
    if (m_eMode == EMode.PRINTING && s.length () > 0)
    {
      _spaceIfNeeded (s.charAt (0));
      m_aPW.print (s);
      m_cLastChar = s.charAt (s.length () - 1);
    }
    return this;
  }

  @Nonnull
  public JFormatter type (@Nonnull final AbstractJType aType)
  {
    if (aType.isReference ())
      return type ((AbstractJClass) aType);
    return generable (aType);
  }

  /**
   * Print a type name.
   * <p>
   * In the collecting mode we use this information to decide what types to
   * import and what not to.
   */
  @Nonnull
  public JFormatter type (@Nonnull final AbstractJClass aType)
  {
    switch (m_eMode)
    {
      case FIND_ERROR_TYPES:
        if (aType.isError ())
          m_bContainsErrorTypes = true;
        break;
      case PRINTING:
        // many of the JTypes in this list are either primitive or belong to
        // package java so we don't need a FQCN
        if (m_aImportedClasses.contains (aType) || aType._package () == m_aPckJavaLang)
        {
          // FQCN imported or not necessary, so generate short name
          print (aType.name ());
        }
        else
        {
          final AbstractJClass aOuterClass = aType.outer ();
          if (aOuterClass != null)
            type (aOuterClass).print ('.').print (aType.name ());
          else
          {
            // collision was detected, so generate FQCN
            print (aType.fullName ());
          }
        }
        break;
      case COLLECTING:
        final String shortName = aType.name ();
        Usages usage = m_aCollectedReferences.get (shortName);
        if (usage == null)
        {
          usage = new Usages ();
          m_aCollectedReferences.put (shortName, usage);
        }
        usage.addReferencedType (aType);
        break;
    }
    return this;
  }

  /**
   * Print an identifier
   */
  @Nonnull
  public JFormatter id (@Nonnull final String id)
  {
    switch (m_eMode)
    {
      case PRINTING:
        print (id);
        break;
      case COLLECTING:
        // see if there is a type name that collides with this id
        Usages usage = m_aCollectedReferences.get (id);
        if (usage == null)
        {
          // not a type, but we need to create a place holder to
          // see if there might be a collision with a type
          usage = new Usages ();
          m_aCollectedReferences.put (id, usage);
        }
        usage.setVariableName ();
        break;
    }
    return this;
  }

  /**
   * Print a new line into the stream
   */
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

  /**
   * Cause the JGenerable object to generate source for iteself
   *
   * @param g
   *        the JGenerable object
   */
  @Nonnull
  public JFormatter generable (@Nonnull final IJGenerable g)
  {
    g.generate (this);
    return this;
  }

  /**
   * Produces {@link IJGenerable}s separated by ','
   */
  @Nonnull
  public JFormatter generable (@Nonnull final Collection <? extends IJGenerable> list)
  {
    boolean first = true;
    if (!list.isEmpty ())
    {
      for (final IJGenerable item : list)
      {
        if (!first)
          print (',');
        generable (item);
        first = false;
      }
    }
    return this;
  }

  /**
   * Cause the JDeclaration to generate source for itself
   *
   * @param d
   *        the JDeclaration object
   */
  @Nonnull
  public JFormatter declaration (@Nonnull final IJDeclaration d)
  {
    d.declare (this);
    return this;
  }

  /**
   * Cause the JStatement to generate source for itself
   *
   * @param s
   *        the JStatement object
   */
  @Nonnull
  public JFormatter statement (@Nonnull final IJStatement s)
  {
    s.state (this);
    return this;
  }

  /**
   * Cause the JVar to generate source for itself
   *
   * @param v
   *        the JVar object
   */
  @Nonnull
  public JFormatter var (@Nonnull final JVar v)
  {
    v.bind (this);
    return this;
  }

  /**
   * Generates the whole source code out of the specified class.
   */
  void write (@Nonnull final JDefinedClass c)
  {
    m_aPckJavaLang = c.owner ()._package ("java.lang");

    // first collect all the types and identifiers
    m_eMode = EMode.COLLECTING;
    declaration (c);

    // collate type names and identifiers to determine which types can be
    // imported
    for (final Usages usage : m_aCollectedReferences.values ())
    {
      if (!usage.isAmbiguousIn (c) && !usage.isVariableName ())
      {
        final AbstractJClass reference = usage.getSingleReferencedType ();

        if (_shouldBeImported (reference, c))
          m_aImportedClasses.add (reference);
        else
        {
          _importOuterClassIfCausesNoAmbiguities (reference, c);
        }
      }
      else
      {
        for (final AbstractJClass reference : usage.getReferencedTypes ())
        {
          _importOuterClassIfCausesNoAmbiguities (reference, c);
        }
      }
    }

    // the class itself that we will be generating is always accessible
    m_aImportedClasses.add (c);

    // then print the declaration
    m_eMode = EMode.PRINTING;

    assert c.parentContainer ().isPackage () : "this method is only for a pacakge-level class";
    final JPackage pkg = (JPackage) c.parentContainer ();
    if (!pkg.isUnnamed ())
    {
      newline ().declaration (pkg);
      newline ();
    }

    // generate import statements
    final AbstractJClass [] imports = m_aImportedClasses.toArray (new AbstractJClass [m_aImportedClasses.size ()]);
    Arrays.sort (imports, ClassNameComparator.getInstance ());
    boolean bAnyImport = false;
    for (AbstractJClass clazz : imports)
    {
      // suppress import statements for primitive types, built-in types,
      // types in the root package, and types in
      // the same package as the current type
      if (!_isImplicitlyImported (clazz, c))
      {
        if (clazz instanceof JNarrowedClass)
        {
          clazz = clazz.erasure ();
        }

        print ("import").print (clazz.fullName ()).print (';').newline ();
        bAnyImport = true;
      }
    }

    if (bAnyImport)
      newline ();

    declaration (c);
  }

  /**
   * determine if an import statement should be used for given class. This is a
   * matter of style and convention
   *
   * @param aReference
   *        {@link AbstractJClass} referenced class
   * @param clazz
   *        {@link AbstractJClass} currently generated class
   * @return true if an import statement can be used to shorten references to
   *         referenced class
   */
  private boolean _shouldBeImported (@Nonnull final AbstractJClass aReference, @Nonnull final JDefinedClass clazz)
  {
    AbstractJClass aRealReference = aReference;
    if (aRealReference instanceof JAnonymousClass)
    {
      aRealReference = ((JAnonymousClass) aRealReference)._extends ();
    }
    if (aRealReference instanceof JNarrowedClass)
    {
      aRealReference = aRealReference.erasure ();
    }

    final AbstractJClass aOuter = aRealReference.outer ();
    if (aOuter != null)
    {
      // Import inner class only when it's name contain a name of enclosing
      // class.
      // In such case no information is lost when we refer to inner class
      // without mentioning
      // it's enclosing class
      if (aRealReference.name ().contains (aOuter.name ()) && _shouldBeImported (aOuter, clazz))
        return true;

      // Do not import inner classes in all other cases to aid
      // understandability/readability.
      return false;
    }

    return true;
  }

  /**
   * determine if an import statement should be suppressed
   *
   * @param aReference
   *        {@link AbstractJClass} that may or may not have an import
   * @param aGeneratingClass
   *        {@link AbstractJClass} that is the current class being processed
   * @return true if an import statement should be suppressed, false otherwise
   */
  private boolean _isImplicitlyImported (@Nonnull final AbstractJClass aReference, @Nonnull final AbstractJClass clazz)
  {
    AbstractJClass aRealReference = aReference;
    if (aRealReference instanceof JAnonymousClass)
    {
      aRealReference = ((JAnonymousClass) aRealReference)._extends ();
    }
    if (aRealReference instanceof JNarrowedClass)
    {
      aRealReference = aRealReference.erasure ();
    }

    final JPackage aPackage = aRealReference._package ();
    if (aPackage == null)
    {
      // May be null for JTypeVar and JTypeWildcard
      return true;
    }

    if (aPackage.isUnnamed ())
      return true;

    if (aPackage == m_aPckJavaLang)
    {
      // no need to explicitly import java.lang classes
      return true;
    }

    // All pkg local classes do not need an
    // import stmt for ref, except for inner classes
    if (aPackage == clazz._package ())
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
      return aTopLevelClass == clazz;
    }
    return false;
  }

  /**
   * If reference is inner-class adds some outer class to the list of imported
   * classes if it
   *
   * @param clazz
   *        {@link AbstractJClass} that may or may not have an import
   * @param aGeneratingClass
   *        {@link AbstractJClass} that is the current class being processed
   * @return true if an import statement should be suppressed, false otherwise
   */
  private void _importOuterClassIfCausesNoAmbiguities (@Nonnull final AbstractJClass reference,
                                                       @Nonnull final JDefinedClass clazz)
  {
    final AbstractJClass outer = reference.outer ();
    if (outer != null)
    {
      if (_causesNoAmbiguities (outer, clazz) && _shouldBeImported (outer, clazz))
        m_aImportedClasses.add (outer);
      else
        _importOuterClassIfCausesNoAmbiguities (outer, clazz);
    }
  }

  private boolean _causesNoAmbiguities (@Nonnull final AbstractJClass reference, @Nonnull final JDefinedClass clazz)
  {
    final Usages usage = m_aCollectedReferences.get (reference.name ());
    return usage == null || (!usage.isAmbiguousIn (clazz) && usage.containsReferencedType (reference));
  }

  /**
   * Used during the optimization of class imports. List of
   * {@link AbstractJClass}es whose short name is the same.
   *
   * @author Ryan.Shoemaker@Sun.COM
   */
  private final class Usages
  {
    private final List <AbstractJClass> _referencedClasses = new ArrayList <AbstractJClass> ();

    /** true if this name is used as an identifier (like a variable name.) **/
    private boolean _isVariableName;

    /**
     * @return true if the short name is ambiguous in context of enclosingClass
     *         and classes with this name can't be imported.
     */
    public boolean isAmbiguousIn (final JDefinedClass enclosingClass)
    {
      // more than one type with the same name
      if (_referencedClasses.size () > 1)
        return true;

      // an id and (at least one) type with the same name
      if (_isVariableName && !_referencedClasses.isEmpty ())
        return true;

      // no references is always unambiguous
      if (_referencedClasses.isEmpty ())
        return false;

      AbstractJClass singleRef = _referencedClasses.get (0);
      if (singleRef instanceof JAnonymousClass)
      {
        singleRef = singleRef._extends ();
      }

      // special case where a generated type collides with a type in package
      // java.lang
      if (singleRef._package () == JFormatter.this.m_aPckJavaLang)
      {
        // make sure that there's no other class with this name within the
        // same package
        for (final JDefinedClass n : enclosingClass._package ().classes ())
        {
          // even if this is the only "String" class we use,
          // if the class called "String" is in the same package,
          // we still need to import it.
          if (n.name ().equals (singleRef.name ()))
            return true; // collision
        }
      }

      return false;
    }

    public boolean addReferencedType (final AbstractJClass clazz)
    {
      if (_referencedClasses.contains (clazz))
        return false;
      return _referencedClasses.add (clazz);
    }

    public boolean containsReferencedType (final AbstractJClass clazz)
    {
      return _referencedClasses.contains (clazz);
    }

    @Nonnull
    public AbstractJClass getSingleReferencedType ()
    {
      assert _referencedClasses.size () == 1;
      return _referencedClasses.get (0);
    }

    @Nonnull
    public List <AbstractJClass> getReferencedTypes ()
    {
      return _referencedClasses;
    }

    public void setVariableName ()
    {
      // FIXME: Strangely special processing of inner-classes references
      // Why is this?
      // Should this be removed?
      for (final AbstractJClass type : _referencedClasses)
      {
        if (type.outer () != null)
        {
          _isVariableName = false;
          return;
        }
      }
      _isVariableName = true;
    }

    /**
     * @return true if this name is used as an identifier (like a variable
     *         name.).
     */
    public boolean isVariableName ()
    {
      return _isVariableName;
    }

    /**
     * @return true if this name is used as an type name (like class name.)
     */
    @SuppressWarnings ("unused")
    public boolean isTypeName ()
    {
      return !_referencedClasses.isEmpty ();
    }
  }
}
