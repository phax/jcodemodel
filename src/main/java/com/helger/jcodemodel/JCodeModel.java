/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.helger.jcodemodel.meta.CodeModelBuildingException;
import com.helger.jcodemodel.meta.ErrorTypeFound;
import com.helger.jcodemodel.meta.JCodeModelJavaxLangModelAdapter;
import com.helger.jcodemodel.util.JCSecureLoader;
import com.helger.jcodemodel.util.JCValueEnforcer;
import com.helger.jcodemodel.writer.FileCodeWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter;

/**
 * Root of the code DOM.
 * <p>
 * Here's your typical CodeModel application.
 *
 * <pre>
 * JCodeModel cm = new JCodeModel();
 *
 * // generate source code by populating the 'cm' tree.
 * cm._class(...);
 * ...
 *
 * // write them out
 * cm.build(new File("."));
 * </pre>
 * <p>
 * Every CodeModel node is always owned by one {@link JCodeModel} object at any
 * given time (which can be often accessed by the <tt>owner()</tt> method.) As
 * such, when you generate Java code, most of the operation works in a top-down
 * fashion. For example, you create a class from {@link JCodeModel}, which gives
 * you a {@link JDefinedClass}. Then you invoke a method on it to generate a new
 * method, which gives you {@link JMethod}, and so on. There are a few
 * exceptions to this, most notably building {@link IJExpression}s, but
 * generally you work with CodeModel in a top-down fashion. Because of this
 * design, most of the CodeModel classes aren't directly instanciable.
 * <h2>Where to go from here?</h2>
 * <p>
 * Most of the time you'd want to populate new type definitions in a
 * {@link JCodeModel}. See {@link #_class(String, EClassType)}.
 */
public final class JCodeModel
{
  protected static boolean checkIfFileSystemIsCaseSensitive ()
  {
    try
    {
      // let the system property override, in case the user really
      // wants to override.
      if (System.getProperty ("com.sun.codemodel.FileSystemCaseSensitive") != null)
        return true;

      // Add special override to differentiate if Sun implementation is also in
      // scope
      if (System.getProperty ("com.helger.jcodemodel.FileSystemCaseSensitive") != null)
        return true;
    }
    catch (final Exception e)
    {
      // Fall through
    }

    // on Unix, it's case sensitive.
    return File.separatorChar == '/';
  }

  /**
   * If the flag is true, we will consider two classes "Foo" and "foo" as a
   * collision.
   */
  private static final boolean s_bIsCaseSensitiveFileSystem = checkIfFileSystemIsCaseSensitive ();

  /**
   * @return <code>true</code> if the file system is case sensitive (*x) or
   *         <code>false</code> if not (e.g. Windows).
   * @since 3.0.0
   */
  public static boolean isFileSystemCaseSensitive ()
  {
    return s_bIsCaseSensitiveFileSystem;
  }

  /**
   * Conversion from primitive type {@link Class} (such as {@link Integer#TYPE})
   * to its boxed type (such as <tt>Integer.class</tt>). It's an unmodifiable
   * map.
   */
  public static final Map <Class <?>, Class <?>> primitiveToBox;

  /**
   * The reverse look up for {@link #primitiveToBox}. It's an unmodifiable map.
   */
  public static final Map <Class <?>, Class <?>> boxToPrimitive;

  static
  {
    final Map <Class <?>, Class <?>> m1 = new HashMap <> ();
    final Map <Class <?>, Class <?>> m2 = new HashMap <> ();

    m1.put (Boolean.class, Boolean.TYPE);
    m1.put (Byte.class, Byte.TYPE);
    m1.put (Character.class, Character.TYPE);
    m1.put (Double.class, Double.TYPE);
    m1.put (Float.class, Float.TYPE);
    m1.put (Integer.class, Integer.TYPE);
    m1.put (Long.class, Long.TYPE);
    m1.put (Short.class, Short.TYPE);
    m1.put (Void.class, Void.TYPE);

    // Swap keys and values
    for (final Map.Entry <Class <?>, Class <?>> e : m1.entrySet ())
      m2.put (e.getValue (), e.getKey ());

    boxToPrimitive = Collections.unmodifiableMap (m1);
    primitiveToBox = Collections.unmodifiableMap (m2);
  }

  /** The packages that this JCodeWriter contains. */
  private final Map <String, JPackage> m_aPackages = new HashMap <> ();

  /** All JReferencedClasses are pooled here. */
  private final Map <Class <?>, JReferencedClass> m_aRefClasses = new HashMap <> ();

  /** Obtains a reference to the special "null" type. */
  public final JNullType NULL = new JNullType (this);
  // primitive types
  public final JPrimitiveType BOOLEAN = new JPrimitiveType (this, "boolean", Boolean.class, true);
  public final JPrimitiveType BYTE = new JPrimitiveType (this, "byte", Byte.class, true);
  public final JPrimitiveType CHAR = new JPrimitiveType (this, "char", Character.class, true);
  public final JPrimitiveType DOUBLE = new JPrimitiveType (this, "double", Double.class, true);
  public final JPrimitiveType FLOAT = new JPrimitiveType (this, "float", Float.class, true);
  public final JPrimitiveType INT = new JPrimitiveType (this, "int", Integer.class, true);
  public final JPrimitiveType LONG = new JPrimitiveType (this, "long", Long.class, true);
  public final JPrimitiveType SHORT = new JPrimitiveType (this, "short", Short.class, true);
  public final JPrimitiveType VOID = new JPrimitiveType (this, "void", Void.class, false);

  /**
   * Cached for {@link #wildcard()}.
   */
  private AbstractJClass m_aWildcard;

  /** The charset used for building the output - null means system default */
  private Charset m_aBuildingCharset;

  /** The newline string to be used. Defaults to system default */
  private String m_sBuildingNewLine = AbstractCodeWriter.getDefaultNewLine ();

  private final Set <AbstractJClass> m_aDontImportClasses = new HashSet <> ();

  public JCodeModel ()
  {}

  /**
   * Add a package to the list of packages to be generated
   *
   * @param sName
   *        Name of the package. Use "" to indicate the root package.
   * @return Newly generated package
   */
  @Nonnull
  public JPackage _package (@Nonnull final String sName)
  {
    return m_aPackages.computeIfAbsent (sName, k -> new JPackage (k, this));
  }

  @Nonnull
  public JPackage rootPackage ()
  {
    return _package ("");
  }

  /**
   * @return an iterator that walks the packages defined using this code writer.
   */
  @Nonnull
  public Iterator <JPackage> packages ()
  {
    return m_aPackages.values ().iterator ();
  }

  /**
   * Creates a new generated class.
   *
   * @param nMods
   *        Modifiers to use
   * @param sFullyQualifiedClassName
   *        FQCN
   * @param eClassType
   *        Class type to use (enum/class/interface/annotation)
   * @return New {@link JDefinedClass}
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _class (final int nMods,
                               @Nonnull final String sFullyQualifiedClassName,
                               @Nonnull final EClassType eClassType) throws JClassAlreadyExistsException
  {
    final int nIdx = sFullyQualifiedClassName.lastIndexOf ('.');
    if (nIdx < 0)
      return rootPackage ()._class (nMods, sFullyQualifiedClassName, eClassType);
    return _package (sFullyQualifiedClassName.substring (0, nIdx))._class (nMods,
                                                                           sFullyQualifiedClassName.substring (nIdx +
                                                                                                               1),
                                                                           eClassType);
  }

  /**
   * Creates a new generated class.
   *
   * @param sFullyQualifiedClassName
   *        FQCN
   * @return New {@link JDefinedClass}
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _class (@Nonnull final String sFullyQualifiedClassName) throws JClassAlreadyExistsException
  {
    return _class (sFullyQualifiedClassName, EClassType.CLASS);
  }

  /**
   * Creates a new generated class.
   *
   * @param nMods
   *        Modifiers to use
   * @param sFullyQualifiedClassName
   *        FQCN
   * @return New {@link JDefinedClass}
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _class (final int nMods,
                               @Nonnull final String sFullyQualifiedClassName) throws JClassAlreadyExistsException
  {
    return _class (nMods, sFullyQualifiedClassName, EClassType.CLASS);
  }

  /**
   * Creates a new generated class.
   *
   * @param sFullyQualifiedClassName
   *        FQCN
   * @param eClassType
   *        Class type to use (enum/class/interface/annotation)
   * @return New {@link JDefinedClass}
   * @exception JClassAlreadyExistsException
   *            When the specified class/interface was already created.
   */
  @Nonnull
  public JDefinedClass _class (@Nonnull final String sFullyQualifiedClassName,
                               @Nonnull final EClassType eClassType) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, sFullyQualifiedClassName, eClassType);
  }

  /**
   * Creates a dummy, unknown {@link JDirectClass} that represents a given name.
   * <br>
   * This method is useful when the code generation needs to include the
   * user-specified class that may or may not exist, and only thing known about
   * it is a class name.
   *
   * @param sName
   *        The fully qualified name of the class. When using type parameters
   *        please use {@link #parseType(String)} instead!
   * @return New {@link JDirectClass}
   */
  @Nonnull
  public JDirectClass directClass (@Nonnull final String sName)
  {
    return directClass (EClassType.CLASS, sName);
  }

  /**
   * Creates a dummy, unknown {@link JDirectClass} that represents a given name.
   * <br>
   * This method is useful when the code generation needs to include the
   * user-specified class that may or may not exist, and only thing known about
   * it is a class name.
   *
   * @param eClassType
   *        Class type to use.
   * @param sName
   *        The fully qualified name of the class. When using type parameters
   *        please use {@link #parseType(String)} instead!
   * @return New {@link JDirectClass}
   */
  @Nonnull
  public JDirectClass directClass (@Nonnull final EClassType eClassType, @Nonnull final String sName)
  {
    return new JDirectClass (this, null, eClassType, sName);
  }

  /**
   * Creates a dummy, error {@link AbstractJClass} that can only be referenced
   * from hidden classes.
   * <p>
   * This method is useful when the code generation needs to include some error
   * class that should never leak into actually written code.
   * <p>
   * Error-types represents holes or place-holders that can't be filled.
   * References to error-classes can be used in hidden class-models. Such
   * classes should never be actually written but can be somehow used during
   * code generation. Use {@code JCodeModel#buildsErrorTypeRefs} method to test
   * if your generated Java-sources contains references to error-types.
   * <p>
   * You should probably always check generated code with
   * {@code JCodeModel#buildsErrorTypeRefs} method if you use any error-types.
   * <p>
   * Most of error-types methods throws {@code JErrorClassUsedException}
   * unchecked exceptions. Be careful and use {@link AbstractJType#isError()
   * AbstractJType#isError} method to check for error-types before actually
   * using it's methods.
   *
   * @param sMessage
   *        some free form text message to identify source of error
   * @return New {@link JErrorClass}
   * @see JCodeModel#buildsErrorTypeRefs()
   * @see JErrorClass
   */
  @Nonnull
  public JErrorClass errorClass (@Nonnull final String sMessage)
  {
    return errorClass (sMessage, null);
  }

  /**
   * Creates a dummy, error {@link AbstractJClass} that can only be referenced
   * from hidden classes.
   * <p>
   * This method is useful when the code generation needs to include some error
   * class that should never leak into actually written code.
   * <p>
   * Error-types represents holes or place-holders that can't be filled.
   * References to error-classes can be used in hidden class-models. Such
   * classes should never be actually written but can be somehow used during
   * code generation. Use {@code JCodeModel#buildsErrorTypeRefs} method to test
   * if your generated Java-sources contains references to error-types.
   * <p>
   * You should probably always check generated code with
   * {@code JCodeModel#buildsErrorTypeRefs} method if you use any error-types.
   * <p>
   * Most of error-types methods throws {@code JErrorClassUsedException}
   * unchecked exceptions. Be careful and use {@link AbstractJType#isError()
   * AbstractJType#isError} method to check for error-types before actually
   * using it's methods.
   *
   * @param sName
   *        name of missing class if it is known
   * @param sMessage
   *        some free form text message to identify source of error
   * @return New {@link JErrorClass}
   * @see JCodeModel#buildsErrorTypeRefs()
   * @see JErrorClass
   */
  @Nonnull
  public JErrorClass errorClass (@Nonnull final String sMessage, @Nullable final String sName)
  {
    return new JErrorClass (this, sMessage, sName);
  }

  /**
   * Check if any error-types leaked into output Java-sources.
   *
   * @return <code>true</code> if so
   * @see JCodeModel#errorClass(String)
   */
  public boolean buildsErrorTypeRefs ()
  {
    final JPackage [] pkgs = m_aPackages.values ().toArray (new JPackage [m_aPackages.size ()]);
    // avoid concurrent modification exception
    for (final JPackage pkg : pkgs)
    {
      if (pkg.buildsErrorTypeRefs ())
        return true;
    }
    return false;
  }

  /**
   * Gets a reference to the already created generated class.
   *
   * @param sFullyQualifiedClassName
   *        FQCN
   * @return <code>null</code> If the class is not yet created.
   * @see JPackage#_getClass(String)
   */
  @Nullable
  public JDefinedClass _getClass (@Nonnull final String sFullyQualifiedClassName)
  {
    final int nIndex = sFullyQualifiedClassName.lastIndexOf ('.');
    if (nIndex < 0)
      return rootPackage ()._getClass (sFullyQualifiedClassName);
    return _package (sFullyQualifiedClassName.substring (0,
                                                         nIndex))._getClass (sFullyQualifiedClassName.substring (nIndex + 1));
  }

  /**
   * Creates a new anonymous class.
   *
   * @param aBaseClass
   *        Base class
   * @return New {@link JAnonymousClass}
   */
  @Nonnull
  public JAnonymousClass anonymousClass (@Nonnull final AbstractJClass aBaseClass)
  {
    return new JAnonymousClass (aBaseClass);
  }

  /**
   * Creates a new anonymous class.
   *
   * @param aBaseClass
   *        Base class
   * @return New {@link JAnonymousClass}
   */
  @Nonnull
  public JAnonymousClass anonymousClass (@Nonnull final Class <?> aBaseClass)
  {
    return anonymousClass (ref (aBaseClass));
  }

  /**
   * @return The default charset used for building. <code>null</code> means
   *         system default.
   */
  @Nullable
  public Charset getBuildingCharset ()
  {
    return m_aBuildingCharset;
  }

  /**
   * Set the charset to be used for emitting files.
   *
   * @param aCharset
   *        The charset to be used. May be <code>null</code> to indicate the use
   *        of the system default.
   * @return this for chaining
   */
  @Nonnull
  public JCodeModel setBuildingCharset (@Nullable final Charset aCharset)
  {
    m_aBuildingCharset = aCharset;
    return this;
  }

  /**
   * @return The newline string to be used. Defaults to system default
   */
  public String getBuildingNewLine ()
  {
    return m_sBuildingNewLine;
  }

  /**
   * Set the new line string to be used for emitting source files.
   *
   * @param sNewLine
   *        The new line string to be used. May neither be <code>null</code> nor
   *        empty.
   * @return this for chaining
   */
  @Nonnull
  public JCodeModel setBuildingNewLine (@Nonnull final String sNewLine)
  {
    JCValueEnforcer.notEmpty (sNewLine, "NewLine");
    m_sBuildingNewLine = sNewLine;
    return this;
  }

  /**
   * Generates Java source code. A convenience method for
   * <code>build(destDir,destDir,status)</code>.
   *
   * @param aDestDir
   *        source files and resources are generated into this directory.
   * @param aStatusPS
   *        if non-<code>null</code>, progress indication will be sent to this
   *        stream.
   * @throws IOException
   *         on IO error
   */
  public void build (@Nonnull final File aDestDir, @Nullable final PrintStream aStatusPS) throws IOException
  {
    build (aDestDir, aDestDir, aStatusPS);
  }

  /**
   * Generates Java source code. A convenience method that calls
   * {@link #build(AbstractCodeWriter,AbstractCodeWriter)}.
   *
   * @param aSrcDir
   *        Java source files are generated into this directory.
   * @param aResourceDir
   *        Other resource files are generated into this directory.
   * @param aStatusPS
   *        Progress stream. May be <code>null</code>.
   * @throws IOException
   *         on IO error if non-null, progress indication will be sent to this
   *         stream.
   */
  public void build (@Nonnull final File aSrcDir,
                     @Nonnull final File aResourceDir,
                     @Nullable final PrintStream aStatusPS) throws IOException
  {
    AbstractCodeWriter res = new FileCodeWriter (aResourceDir, m_aBuildingCharset, m_sBuildingNewLine);
    AbstractCodeWriter src = new FileCodeWriter (aSrcDir, m_aBuildingCharset, m_sBuildingNewLine);
    if (aStatusPS != null)
    {
      src = new ProgressCodeWriter (src, aStatusPS);
      res = new ProgressCodeWriter (res, aStatusPS);
    }
    build (src, res);
  }

  /**
   * A convenience method for <code>build(destDir,System.out)</code>.
   *
   * @param aDestDir
   *        source files and resources are generated into this directory.
   * @throws IOException
   *         on IO error
   */
  public void build (@Nonnull final File aDestDir) throws IOException
  {
    build (aDestDir, System.out);
  }

  /**
   * A convenience method for <code>build(srcDir,resourceDir,System.out)</code>.
   *
   * @param aSrcDir
   *        Java source files are generated into this directory.
   * @param aResourceDir
   *        Other resource files are generated into this directory.
   * @throws IOException
   *         on IO error
   */
  public void build (@Nonnull final File aSrcDir, @Nonnull final File aResourceDir) throws IOException
  {
    build (aSrcDir, aResourceDir, System.out);
  }

  /**
   * A convenience method for <code>build(out,out)</code>.
   *
   * @param aWriter
   *        Source code and resource writer
   * @throws IOException
   *         on IO error
   */
  public void build (@Nonnull final AbstractCodeWriter aWriter) throws IOException
  {
    build (aWriter, aWriter);
  }

  /**
   * Generates Java source code.
   *
   * @param aSource
   *        Source code writer
   * @param aResource
   *        Resource writer
   * @throws IOException
   *         on IO error
   */
  public void build (@Nonnull final AbstractCodeWriter aSource,
                     @Nonnull final AbstractCodeWriter aResource) throws IOException
  {
    try
    {
      // Copy to avoid concurrent modification exception
      final List <JPackage> pkgs = new ArrayList <> (m_aPackages.values ());
      for (final JPackage pkg : pkgs)
        pkg.build (aSource, aResource);
    }
    finally
    {
      aSource.close ();
      aResource.close ();
    }
  }

  /**
   * @return the number of files to be generated if {@link #build} is invoked
   *         now.
   */
  @Nonnegative
  public int countArtifacts ()
  {
    int r = 0;
    final JPackage [] pkgs = m_aPackages.values ().toArray (new JPackage [m_aPackages.size ()]);
    // avoid concurrent modification exception
    for (final JPackage pkg : pkgs)
      r += pkg.countArtifacts ();
    return r;
  }

  /**
   * Obtains a reference to an existing class from its Class object.
   * <p>
   * The parameter may not be primitive.
   *
   * @param aClazz
   *        Existing class to reference
   * @return Singleton reference to this class. Might be a
   *         {@link JReferencedClass} or a {@link JArrayClass}
   * @see #_ref(Class) for the version that handles more cases.
   */
  @Nonnull
  public AbstractJClass ref (@Nonnull final Class <?> aClazz)
  {
    JReferencedClass aRefClass = m_aRefClasses.get (aClazz);
    if (aRefClass == null)
    {
      if (aClazz.isPrimitive ())
      {
        // Cannot return BYTE etc. because the return type does not match
        throw new IllegalArgumentException (aClazz + " is a primitive");
      }

      if (aClazz.isArray ())
      {
        final Class <?> aComponentType = aClazz.getComponentType ();
        // Component type may be a primitive!
        return new JArrayClass (this, _ref (aComponentType));
      }

      aRefClass = new JReferencedClass (this, aClazz);
      m_aRefClasses.put (aClazz, aRefClass);
    }
    return aRefClass;
  }

  /**
   * Obtains a reference to a processable class from its TypeElement
   * description.
   * <p>
   * Annotation processors can get access of {@link TypeElement} objects during
   * annotation processing. These TypeElement objects can be used with
   * jcodemodel as a references to classes.
   * <p>
   * This method result-class definition can never include references to
   * "error"-types. Error-types araise during annotation processing when
   * something is not fully defined.
   * <p>
   * You can post-pond annotation processing for later stages of annotation
   * processor hoping that all error-types will become defined on some
   * annotation processing stage at last. You can catch {@link ErrorTypeFound}
   * exception to achieve this.
   *
   * @param aElement
   *        Processable class to reference
   * @param aElementUtils
   *        Utility functions to handle Element-objects
   * @return Singleton reference to this class.
   * @throws ErrorTypeFound
   *         if some classes are not fully defined during annotation processing.
   * @throws CodeModelBuildingException
   *         In case of an internal error (?)
   * @see JCodeModelJavaxLangModelAdapter
   * @see #refWithErrorTypes(TypeElement,Elements)
   */
  @Nonnull
  public JDefinedClass ref (@Nonnull final TypeElement aElement,
                            @Nonnull final Elements aElementUtils) throws ErrorTypeFound, CodeModelBuildingException
  {
    final JCodeModelJavaxLangModelAdapter adapter = new JCodeModelJavaxLangModelAdapter (this, aElementUtils);
    return adapter.getClass (aElement);
  }

  /**
   * Obtains a reference to a processable class from its TypeElement
   * description.
   * <p>
   * Annotation processors can get access of TypeElement objects during
   * annotation processing. These TypeElement objects can be used with
   * jcodemodel as a references to classes.
   * <p>
   * This method result-class definition can include references to
   * "error"-types. Error-types araise during annotation processing when
   * something is not fully defined.
   * <p>
   * Sometimes direct treatment of error-types is required. You can use
   * {@link AbstractJType#isError()} and
   * {@link JCodeModel#buildsErrorTypeRefs()} methods to handle error-types and
   * to prevent error-types to leak into generated code.
   *
   * @param aElement
   *        Processable class to reference
   * @param aElementUtils
   *        Utility functions to handle Element-objects
   * @return Singleton reference to this class.
   * @throws CodeModelBuildingException
   *         In case of an internal error (?)
   * @see JCodeModelJavaxLangModelAdapter
   * @see #ref(TypeElement, Elements)
   * @see JErrorClass
   * @see #buildsErrorTypeRefs()
   */
  @Nonnull
  public JDefinedClass refWithErrorTypes (@Nonnull final TypeElement aElement,
                                          @Nonnull final Elements aElementUtils) throws CodeModelBuildingException
  {
    final JCodeModelJavaxLangModelAdapter adapter = new JCodeModelJavaxLangModelAdapter (this, aElementUtils);
    return adapter.getClassWithErrorTypes (aElement);
  }

  /**
   * Like {@link #ref(Class)} but also handling primitive types!
   *
   * @param aClass
   *        Class to be referenced
   * @return primitive or class
   */
  @Nonnull
  public AbstractJType _ref (@Nonnull final Class <?> aClass)
  {
    if (aClass.isPrimitive ())
      return AbstractJType.parse (this, aClass.getName ());
    return ref (aClass);
  }

  /**
   * Obtains a reference to an existing class from its fully-qualified class
   * name. <br>
   * First, this method attempts to load the class of the given name. If that
   * fails, we assume that the class is derived straight from {@link Object},
   * and return a {@link AbstractJClass}.
   *
   * @param sFullyQualifiedClassName
   *        FQCN
   * @return Singleton reference to this class. Might be a
   *         {@link JReferencedClass} or a {@link JArrayClass} or a
   *         {@link JDirectClass}
   */
  @Nonnull
  public AbstractJClass ref (@Nonnull final String sFullyQualifiedClassName)
  {
    try
    {
      // try the context class loader first
      return ref (JCSecureLoader.getContextClassLoader ().loadClass (sFullyQualifiedClassName));
    }
    catch (final ClassNotFoundException e)
    {
      // fall through
    }

    // then the default mechanism.
    try
    {
      return ref (Class.forName (sFullyQualifiedClassName));
    }
    catch (final ClassNotFoundException e)
    {
      // fall through
    }

    // assume it's not visible to us.
    return new JDirectClass (this, null, EClassType.CLASS, sFullyQualifiedClassName);
  }

  /**
   * @return Singleton {@link AbstractJClass} representation for "?", which is
   *         equivalent to "? extends Object".
   */
  @Nonnull
  public AbstractJClass wildcard ()
  {
    if (m_aWildcard == null)
    {
      // "Hack" if base class is "Object" it is omitted
      m_aWildcard = ref (Object.class).wildcardExtends ();
    }
    return m_aWildcard;
  }

  /**
   * Obtains a type object from a type name.
   * <p>
   * This method handles primitive types, arrays, and existing {@link Class}es.
   *
   * @param sName
   *        Type name to parse
   * @return The internal representation of the specified name. Might be a
   *         {@link JArrayClass}, a {@link JPrimitiveType}, a
   *         {@link JReferencedClass}, a {@link JNarrowedClass}
   */
  @Nonnull
  public AbstractJType parseType (@Nonnull final String sName)
  {
    // array
    if (sName.endsWith ("[]"))
    {
      // Simply remove trailing "[]"
      return parseType (sName.substring (0, sName.length () - 2)).array ();
    }

    // try primitive type
    try
    {
      return AbstractJType.parse (this, sName);
    }
    catch (final IllegalArgumentException e)
    {
      // Not a primitive type
    }

    // existing class
    return new TypeNameParser (sName).parseTypeName ();
  }

  @NotThreadSafe
  private final class TypeNameParser
  {
    private final String m_sTypeName;
    private int m_nIdx;

    public TypeNameParser (@Nonnull final String sTypeName)
    {
      m_sTypeName = sTypeName;
    }

    /**
     * Parses a type name token T (which can be potentially of the form
     * T&lt;T1,T2,...&gt;, or "? extends/super T".)
     *
     * @return The parsed type name
     */
    @Nonnull
    AbstractJClass parseTypeName ()
    {
      final int nStart = m_nIdx;

      if (m_sTypeName.charAt (m_nIdx) == '?')
      {
        // wildcard
        m_nIdx++;
        _skipWs ();

        final String head = m_sTypeName.substring (m_nIdx);
        if (head.startsWith ("extends"))
        {
          // 7 == "extends".length
          m_nIdx += 7;
          _skipWs ();
          return parseTypeName ().wildcardExtends ();
        }

        if (head.startsWith ("super"))
        {
          // 5 == "super".length
          m_nIdx += 5;
          _skipWs ();
          return parseTypeName ().wildcardSuper ();
        }

        // not supported
        throw new IllegalArgumentException ("only extends/super can follow ?, but found " +
                                            m_sTypeName.substring (m_nIdx));
      }

      while (m_nIdx < m_sTypeName.length ())
      {
        final char ch = m_sTypeName.charAt (m_nIdx);
        if (Character.isJavaIdentifierStart (ch) || Character.isJavaIdentifierPart (ch) || ch == '.')
          m_nIdx++;
        else
          break;
      }

      final AbstractJClass aClazz = ref (m_sTypeName.substring (nStart, m_nIdx));
      return _parseSuffix (aClazz);
    }

    /**
     * Parses additional left-associative suffixes, like type arguments and
     * array specifiers.
     */
    @Nonnull
    private AbstractJClass _parseSuffix (@Nonnull final AbstractJClass aClazz)
    {
      if (m_nIdx == m_sTypeName.length ())
      {
        // hit EOL
        return aClazz;
      }

      final char ch = m_sTypeName.charAt (m_nIdx);

      if (ch == '<')
        return _parseSuffix (_parseArguments (aClazz));

      if (ch == '[')
      {
        if (m_sTypeName.charAt (m_nIdx + 1) == ']')
        {
          m_nIdx += 2;
          return _parseSuffix (aClazz.array ());
        }
        throw new IllegalArgumentException ("Expected ']' but found " + m_sTypeName.substring (m_nIdx + 1));
      }

      return aClazz;
    }

    /**
     * Skips whitespaces
     */
    private void _skipWs ()
    {
      while (Character.isWhitespace (m_sTypeName.charAt (m_nIdx)) && m_nIdx < m_sTypeName.length ())
        m_nIdx++;
    }

    /**
     * Parses '&lt;T1,T2,...,Tn&gt;'
     *
     * @return the index of the character next to '>'
     */
    @Nonnull
    private AbstractJClass _parseArguments (@Nonnull final AbstractJClass aRawType)
    {
      JCValueEnforcer.isTrue (m_sTypeName.charAt (m_nIdx) == '<', "Expected '<' at current index");
      m_nIdx++;

      final List <AbstractJClass> args = new ArrayList <> ();

      while (true)
      {
        args.add (parseTypeName ());
        if (m_nIdx == m_sTypeName.length ())
          throw new IllegalArgumentException ("Missing '>' in " + m_sTypeName);
        final char ch = m_sTypeName.charAt (m_nIdx);
        if (ch == '>')
          return aRawType.narrow (args);

        if (ch != ',')
          throw new IllegalArgumentException (m_sTypeName);
        m_nIdx++;
      }
    }
  }

  /**
   * Add a class that should <strong>NOT</strong> be imported.
   *
   * @param aClass
   *        The class to use. May not be <code>null</code>.
   * @return <code>true</code> if it was added, <code>false</code> if it was
   *         already contained.
   * @since 3.0.0
   */
  public boolean addDontImportClass (@Nonnull final Class <?> aClass)
  {
    return addDontImportClass (ref (aClass));
  }

  /**
   * Add a class that should <strong>NOT</strong> be imported.
   *
   * @param aClass
   *        The class to use. May not be <code>null</code>.
   * @return <code>true</code> if it was added, <code>false</code> if it was
   *         already contained.
   * @since 3.0.0
   */
  public boolean addDontImportClass (@Nonnull final AbstractJClass aClass)
  {
    JCValueEnforcer.notNull (aClass, "Class");
    return m_aDontImportClasses.add (aClass);
  }

  /**
   * @return A copy all classes that should not be imported. Never
   *         <code>null</code> but maybe empty.
   * @since 3.0.0
   */
  @Nonnull
  public Set <AbstractJClass> getAllDontImportClasses ()
  {
    return new HashSet <> (m_aDontImportClasses);
  }
}
