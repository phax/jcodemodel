/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

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
  private final Map <String, ReferenceList> _collectedReferences = new HashMap <String, ReferenceList> ();

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
      if (c2 == '(') // but not "new Foo<Bar>()"
        return false;
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
    if (!s.isEmpty() && _mode == EMode.PRINTING)
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
        ReferenceList tl = _collectedReferences.get (shortName);
        if (tl == null)
        {
          tl = new ReferenceList ();
          _collectedReferences.put (shortName, tl);
        }
        tl.add (type);
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
        ReferenceList tl = _collectedReferences.get (id);
        if (tl != null)
        {
          for (final AbstractJClass type : tl.getClasses ())
            if (type.outer () != null)
            {
              tl.setId (false);
              return this;
            }
          tl.setId (true);
        }
        else
        {
          // not a type, but we need to create a place holder to
          // see if there might be a collision with a type
          tl = new ReferenceList ();
          tl.setId (true);
          _collectedReferences.put (id, tl);
        }
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
    for (final ReferenceList tl : _collectedReferences.values ())
    {
      if (!tl.collisions (c) && !tl.isId ())
      {
        assert tl.getClasses ().size () == 1;

        // add to list of collected types
        _importedClasses.add (tl.getClasses ().get (0));
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
    Arrays.sort (imports);
    for (AbstractJClass clazz : imports)
    {
      // suppress import statements for primitive types, built-in types,
      // types in the root package, and types in
      // the same package as the current type
      if (!_supressImport (clazz, c))
      {
        if (clazz instanceof JNarrowedClass)
        {
          clazz = clazz.erasure ();
        }

        print ("import").print (clazz.fullName ()).print (';').newline ();
      }
    }
    newline ();

    declaration (c);
  }

  /**
   * determine if an import statement should be suppressed
   * 
   * @param clazz
   *        JType that may or may not have an import
   * @param c
   *        JType that is the current class being processed
   * @return true if an import statement should be suppressed, false otherwise
   */
  private boolean _supressImport (@Nonnull final AbstractJClass aImportClass, @Nonnull final AbstractJClass c)
  {
    AbstractJClass clazz = aImportClass;
    if (clazz instanceof JAnonymousClass)
    {
      clazz = clazz._extends ();
    }
    if (clazz instanceof JNarrowedClass)
    {
      clazz = clazz.erasure ();
    }

    final JPackage pkg = clazz._package ();
    if (pkg == null)
    {
      // May be null for JTypeVar
      return true;
    }
    if (pkg.isUnnamed ())
      return true;

    if (pkg == _javaLang)
    {
      // no need to explicitly import java.lang classes
      return true;
    }

    if (pkg == c._package ())
    {
      // inner classes require an import stmt.
      // All other pkg local classes do not need an
      // import stmt for ref.
      if (clazz.outer () == null)
      {
        return true; // no need to explicitly import a class into itself
      }
    }
    return false;
  }

  /**
   * Used during the optimization of class imports. List of
   * {@link AbstractJClass}es whose short name is the same.
   * 
   * @author Ryan.Shoemaker@Sun.COM
   */
  private final class ReferenceList
  {
    private final List <AbstractJClass> _classes = new ArrayList <AbstractJClass> ();

    /** true if this name is used as an identifier (like a variable name.) **/
    private boolean _id;

    /**
     * Returns true if the symbol represented by the short name is "importable".
     */
    public boolean collisions (final JDefinedClass enclosingClass)
    {
      // special case where a generated type collides with a type in package
      // java

      // more than one type with the same name
      if (_classes.size () > 1)
        return true;

      // an id and (at least one) type with the same name
      if (_id && !_classes.isEmpty ())
        return true;

      for (AbstractJClass c : _classes)
      {
        if (c instanceof JAnonymousClass)
        {
          c = c._extends ();
        }
        if (c._package () == JFormatter.this._javaLang)
        {
          // make sure that there's no other class with this name within the
          // same package
          final Iterator <JDefinedClass> itr = enclosingClass._package ().classes ();
          while (itr.hasNext ())
          {
            // even if this is the only "String" class we use,
            // if the class called "String" is in the same package,
            // we still need to import it.
            final JDefinedClass n = itr.next ();
            if (n.name ().equals (c.name ()))
              return true; // collision
          }
        }
        /*
         * if (c.outer () != null) return true; // avoid importing inner class
         * to work around 6431987. // Also see jaxb issue 166
         */
      }

      return false;
    }

    public void add (final AbstractJClass clazz)
    {
      if (!_classes.contains (clazz))
        _classes.add (clazz);
    }

    public List <AbstractJClass> getClasses ()
    {
      return _classes;
    }

    public void setId (final boolean value)
    {
      _id = value;
    }

    /**
     * Return true if this is strictly an id, meaning that there are no
     * collisions with type names.
     */
    public boolean isId ()
    {
      return _id && _classes.isEmpty ();
    }
  }
}
