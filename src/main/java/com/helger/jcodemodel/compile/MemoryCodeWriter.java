/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2022 Philip Helger + contributors
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
package com.helger.jcodemodel.compile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;

/**
 * An {@link AbstractCodeWriter} that stores the files created in the program
 * memory.
 * <p>
 * Can be loaded directly from a JCodeModel using the static
 * {@link #from(JCodeModel)} All the .java files and the resources are then
 * stored internally with their full path.
 * </p>
 * <p>
 * Give access to the internal resources using {@link #getBinaries()} can also
 * be compiled into memory using {@link #compile()}
 * </p>
 */
public class MemoryCodeWriter extends AbstractCodeWriter
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MemoryCodeWriter.class);
  private static final JavaCompiler JAVAC = ToolProvider.getSystemJavaCompiler ();
  public static final String JAVA_FILE_EXTENSION = JavaFileObject.Kind.SOURCE.extension;

  private DiagnosticListener <? super JavaFileObject> m_aDL;
  private final Map <String, NonBlockingByteArrayOutputStream> m_aBinaries = new HashMap <> ();

  public MemoryCodeWriter ()
  {
    super (Charset.defaultCharset (), System.lineSeparator ());
  }

  @Override
  public void close () throws IOException
  {
    // nothing.
  }

  /**
   * @return The current diagnostic listener. May be <code>null</code>.
   */
  @Nullable
  public final DiagnosticListener <? super JavaFileObject> getDiagnosticListener ()
  {
    return m_aDL;
  }

  /**
   * Set an optional listener that it is invoked in case of compilation errors.
   * By default errors are only logged.
   *
   * @param aDL
   *        The listener to call. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final MemoryCodeWriter setDiagnosticListener (@Nullable final DiagnosticListener <? super JavaFileObject> aDL)
  {
    m_aDL = aDL;
    return this;
  }

  /**
   * @return an unmodifiable map of the internal binaries. It's a map from
   *         filename to the payload. Don't modify the payload, as it is not
   *         copied!
   */
  @Nonnull
  public Map <String, NonBlockingByteArrayOutputStream> getBinaries ()
  {
    return Collections.unmodifiableMap (m_aBinaries);
  }

  @Override
  public OutputStream openBinary (final String sDirName, final String sFilename) throws IOException
  {
    final String sFullname = sDirName + "/" + sFilename;

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("MemoryCodeWriter.openBinary (" + sFullname + ")");

    NonBlockingByteArrayOutputStream aBAOS = m_aBinaries.get (sFullname);
    if (aBAOS == null)
    {
      aBAOS = new NonBlockingByteArrayOutputStream ();
      m_aBinaries.put (sFullname, aBAOS);
    }
    else
    {
      LOGGER.warn ("The filename '" + sFullname + "' is contained more than once. Expect compilation errors.");
    }

    return aBAOS;
  }

  /**
   * Compiling the contained java sources.
   *
   * @param aDynamicClassLoader
   *        The dynamic class loader to use. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if if worked, <code>false</code> if not.
   */
  @Nonnull
  public ESuccess compile (@Nonnull final DynamicClassLoader aDynamicClassLoader)
  {
    final ICommonsList <JavaFileObject> aCompilationUnits = new CommonsArrayList <> ();

    final Map <String, NonBlockingByteArrayOutputStream> aNonJava = new HashMap <> ();

    for (final Entry <String, NonBlockingByteArrayOutputStream> e : getBinaries ().entrySet ())
      if (e.getKey ().endsWith (JAVA_FILE_EXTENSION))
        try
        {
          // Use the configured encoding
          aCompilationUnits.add (new SourceJavaFile (e.getKey (), e.getValue ().getAsString (encoding ())));

          final String className = StringHelper.trimEnd (StringHelper.replaceAll (e.getKey (), '/', '.'), JAVA_FILE_EXTENSION);
          final CompiledCodeJavaFile cc = new CompiledCodeJavaFile (className);
          aDynamicClassLoader.setCode (cc);
        }
        catch (final Exception e1)
        {
          throw new UnsupportedOperationException ("catch this exception", e1);
        }
      else
        aNonJava.put (e.getKey (), e.getValue ());

    if (!aCompilationUnits.isEmpty ())
      try
      {
        LOGGER.info ("Compiling: " + aCompilationUnits.getAllMapped (FileObject::getName));

        final DiagnosticListener <? super JavaFileObject> aLoggingDL = x -> LOGGER.error (x.toString ());
        final DiagnosticListener <? super JavaFileObject> aRealDL = m_aDL != null ? x -> {
          aLoggingDL.report (x);
          m_aDL.report (x);
        } : aLoggingDL;

        final ForwardingJavaFileManager <JavaFileManager> aFileManager = new ClassLoaderFileManager (JAVAC.getStandardFileManager (aRealDL,
                                                                                                                                   null,
                                                                                                                                   StandardCharsets.UTF_8),
                                                                                                     aDynamicClassLoader);
        final JavaCompiler.CompilationTask task = JAVAC.getTask (null, aFileManager, aRealDL, null, null, aCompilationUnits);
        if (!task.call ().booleanValue ())
        {
          LOGGER.error ("Error compiling: " + aCompilationUnits.getAllMapped (FileObject::getName));
          return ESuccess.FAILURE;
        }
      }
      catch (final Exception e1)
      {
        throw new UnsupportedOperationException ("catch this exception", e1);
      }
    aDynamicClassLoader.addResources (aNonJava);
    return ESuccess.SUCCESS;
  }

  /**
   * Creates a dynamic class loaders that delegates unknown resources and
   * classes to the classloader of the this class.
   *
   * @return An instance of {@link DynamicClassLoader} using this class' class
   *         loader.
   */
  @Nonnull
  public static DynamicClassLoader dynCL ()
  {
    return new DynamicClassLoader (JavaCompiler.class.getClassLoader ());
  }

  /**
   * Shortcut for {@link #compile(DynamicClassLoader)} with a correct class
   * loader.
   *
   * @return <code>null</code> if compiling didn't work. The
   *         non-<code>null</code> class loader otherwise.
   * @see #compile(DynamicClassLoader) for an alternative version
   */
  @Nullable
  public DynamicClassLoader compile ()
  {
    final DynamicClassLoader aDCL = dynCL ();
    return compile (aDCL).isSuccess () ? aDCL : null;
  }

  @Nonnull
  public static MemoryCodeWriter from (@Nonnull final JCodeModel jcm)
  {
    final MemoryCodeWriter codeWriter = new MemoryCodeWriter ();
    try
    {
      new JCMWriter (jcm).build (codeWriter);
    }
    catch (final IOException e)
    {
      throw new UnsupportedOperationException ("catch this exception", e);
    }
    return codeWriter;
  }
}
