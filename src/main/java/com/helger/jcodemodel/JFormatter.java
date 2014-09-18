/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2014 Philip Helger
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

/**
 * This is a utility class for managing indentation and other basic formatting
 * for PrintWriter.
 */
public class JFormatter implements Closeable
{
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
    PRINTING
  }

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
  private final Map <String, Usages> _collectedReferences = new HashMap <String, Usages> ();

  /**
   * set of imported types (including package java types, even though we won't
   * generate imports for them)
   */
  private final Set <AbstractJClass> _importedClasses = new HashSet <AbstractJClass> ();

  /**
   * The current running mode. Set to PRINTING so that a casual client can use a
   * formatter just like before.
   */
  private EMode _mode = EMode.PRINTING;

  /**
   * Current number of indentation strings to print
   */
  private int _indentLevel;

  /**
   * String to be used for each indentation. Defaults to four spaces.
   */
  private final String _indentSpace;

  /**
   * Stream associated with this JFormatter
   */
  private final PrintWriter _pw;

  private char _lastChar = 0;
  private boolean _atBeginningOfLine = true;
  private JPackage _javaLang;

  /**
   * Creates a JFormatter.
   *
   * @param aPW
   *        PrintWriter to JFormatter to use.
   * @param space
   *        Incremental indentation string, similar to tab value.
   */
  public JFormatter (@Nonnull final PrintWriter aPW, @Nonnull final String space)
  {
    _pw = aPW;
    _indentSpace = space;
  }

  /**
   * Creates a formatter with default incremental indentations of four spaces.
   */
  public JFormatter (@Nonnull final PrintWriter aPW)
  {
    this (aPW, "    ");
  }

  /**
   * Creates a formatter with default incremental indentations of four spaces.
   */
  public JFormatter (@Nonnull final Writer w)
  {
    this (w instanceof PrintWriter ? (PrintWriter) w : new PrintWriter (w));
  }

  /**
   * Closes this formatter.
   */
  public void close ()
  {
    _pw.close ();
  }

  /**
   * Returns true if we are in the printing mode, where we actually produce
   * text. The other mode is the "collecting mode'
   */
  public boolean isPrinting ()
  {
    return _mode == EMode.PRINTING;
  }

  /**
   * Decrement the indentation level.
   */
  @Nonnull
  public JFormatter outdent ()
  {
    _indentLevel--;
    return this;
  }

  /**
   * Increment the indentation level.
   */
  @Nonnull
  public JFormatter indent ()
  {
    _indentLevel++;
    return this;
  }

  private boolean _needSpace (final char c1, final char c2)
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
    if (_atBeginningOfLine)
    {
      for (int i = 0; i < _indentLevel; i++)
        _pw.print (_indentSpace);
      _atBeginningOfLine = false;
    }
    else
      if ((_lastChar != 0) && _needSpace (_lastChar, c))
        _pw.print (' ');
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
    if (_mode == EMode.PRINTING)
    {
      if (c == CLOSE_TYPE_ARGS)
      {
        _pw.print ('>');
      }
      else
      {
        _spaceIfNeeded (c);
        _pw.print (c);
      }
      _lastChar = c;
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
    if (_mode == EMode.PRINTING && s.length () > 0)
    {
      _spaceIfNeeded (s.charAt (0));
      _pw.print (s);
      _lastChar = s.charAt (s.length () - 1);
    }
    return this;
  }

  @Nonnull
  public JFormatter type (@Nonnull final AbstractJType type)
  {
    if (type.isReference ())
      return type ((AbstractJClass) type);
    return generable (type);
  }

  /**
   * Print a type name.
   * <p>
   * In the collecting mode we use this information to decide what types to
   * import and what not to.
   */
  @Nonnull
  public JFormatter type (@Nonnull final AbstractJClass type)
  {
    switch (_mode)
    {
      case PRINTING:
        // many of the JTypes in this list are either primitive or belong to
        // package java so we don't need a FQCN
        if (_importedClasses.contains (type) || type._package () == _javaLang)
        {
          // FQCN imported or not necessary, so generate short name
          print (type.name ());
        }
        else
        {
          final AbstractJClass aOuterClass = type.outer ();
          if (aOuterClass != null)
            type (aOuterClass).print ('.').print (type.name ());
          else
          {
            // collision was detected, so generate FQCN
            print (type.fullName ());
          }
        }
        break;
      case COLLECTING:
        final String shortName = type.name ();
        Usages usage = _collectedReferences.get (shortName);
        if (usage == null)
        {
          usage = new Usages ();
          _collectedReferences.put (shortName, usage);
        }
        usage.addReferencedType (type);
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
    switch (_mode)
    {
      case PRINTING:
        print (id);
        break;
      case COLLECTING:
        // see if there is a type name that collides with this id
        Usages usage = _collectedReferences.get (id);
        if (usage == null)
        {
          // not a type, but we need to create a place holder to
          // see if there might be a collision with a type
          usage = new Usages ();
          _collectedReferences.put (id, usage);
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
    if (_mode == EMode.PRINTING)
    {
      _pw.println ();
      _lastChar = 0;
      _atBeginningOfLine = true;
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
    _javaLang = c.owner ()._package ("java.lang");

    // first collect all the types and identifiers
    _mode = EMode.COLLECTING;
    declaration (c);

    // collate type names and identifiers to determine which types can be
    // imported
    for (final Usages usage : _collectedReferences.values ())
    {
      if (!usage.isAmbiguousIn (c) && !usage.isVariableName ())
      {
        final AbstractJClass reference = usage.getSingleReferencedType ();

        if (_shouldBeImported (reference, c))
          _importedClasses.add (reference);
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
    _importedClasses.add (c);

    // then print the declaration
    _mode = EMode.PRINTING;

    assert c.parentContainer ().isPackage () : "this method is only for a pacakge-level class";
    final JPackage pkg = (JPackage) c.parentContainer ();
    if (!pkg.isUnnamed ())
    {
      newline ().declaration (pkg);
      newline ();
    }

    // generate import statements
    final AbstractJClass [] imports = _importedClasses.toArray (new AbstractJClass [_importedClasses.size ()]);
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

    if (aPackage == _javaLang)
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
        _importedClasses.add (outer);
      else
        _importOuterClassIfCausesNoAmbiguities (outer, clazz);
    }
  }

  private boolean _causesNoAmbiguities (@Nonnull final AbstractJClass reference, @Nonnull final JDefinedClass clazz)
  {
    final Usages usage = _collectedReferences.get (reference.name ());
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
      if (singleRef._package () == JFormatter.this._javaLang)
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
