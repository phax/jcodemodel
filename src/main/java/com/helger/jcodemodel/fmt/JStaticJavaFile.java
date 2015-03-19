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
package com.helger.jcodemodel.fmt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJResourceFile;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JTypeVar;
import com.helger.jcodemodel.util.SecureLoader;

/**
 * Statically generated Java soruce file.
 * <p>
 * This {@link AbstractJResourceFile} implementation will generate a Java source
 * file by copying the source code from a resource.
 * <p>
 * While copying a resource, we look for a package declaration and replace it
 * with the target package name. This allows the static Java source code to have
 * an arbitrary package declaration.
 * <p>
 * You can also use the getJClass method to obtain a {@link AbstractJClass}
 * object that represents the static file. This allows the client code to refer
 * to the class from other CodeModel generated code.
 * <p>
 * Note that because we don't parse the static Java source code, the returned
 * {@link AbstractJClass} object doesn't respond to methods like "isInterface"
 * or "_extends",
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JStaticJavaFile extends AbstractJResourceFile
{
  private final JPackage m_aPkg;
  private final String m_sClassName;
  private final URL _source;
  private final JStaticClass _clazz;
  private final ILineFilter _filter;

  public JStaticJavaFile (@Nonnull final JPackage pkg,
                          @Nonnull final String className,
                          @Nonnull final String resourceName)
  {
    this (pkg, className, SecureLoader.getClassClassLoader (JStaticJavaFile.class).getResource (resourceName), null);
  }

  public JStaticJavaFile (@Nonnull final JPackage pkg,
                          @Nonnull final String className,
                          @Nonnull final URL source,
                          @Nullable final ILineFilter filter)
  {
    super (className + ".java");
    if (source == null)
      throw new NullPointerException ();
    this.m_aPkg = pkg;
    this._clazz = new JStaticClass ();
    this.m_sClassName = className;
    this._source = source;
    this._filter = filter;
  }

  /**
   * Returns a class object that represents a statically generated code.
   */
  @Nonnull
  public final AbstractJClass getJClass ()
  {
    return _clazz;
  }

  @Override
  protected boolean isResource ()
  {
    return false;
  }

  @Override
  protected void build (@Nonnull final OutputStream os) throws IOException
  {
    final InputStream is = _source.openStream ();

    final BufferedReader r = new BufferedReader (new InputStreamReader (is));
    final PrintWriter w = new PrintWriter (new BufferedWriter (new OutputStreamWriter (os)));
    final ILineFilter filter = _createLineFilter ();
    int lineNumber = 1;

    try
    {
      String line;
      while ((line = r.readLine ()) != null)
      {
        line = filter.process (line);
        if (line != null)
          w.println (line);
        lineNumber++;
      }
    }
    catch (final ParseException e)
    {
      throw new IOException ("unable to process " + _source + " line:" + lineNumber + "\n" + e.getMessage ());
    }

    w.close ();
    r.close ();
  }

  /**
   * Creates a {@link ILineFilter}.
   * <p>
   * A derived class can override this method to process the contents of the
   * source file.
   */
  @Nonnull
  private ILineFilter _createLineFilter ()
  {
    // this filter replaces the package declaration.
    final ILineFilter f = new ILineFilter ()
    {
      @Nullable
      public String process (@Nonnull final String line)
      {
        if (line.startsWith ("package "))
        {
          // replace package decl
          if (m_aPkg.isUnnamed ())
            return null;
          return "package " + m_aPkg.name () + ";";
        }
        return line;
      }
    };
    if (_filter != null)
      return new ChainFilter (_filter, f);
    return f;
  }

  /**
   * Filter that alters the Java source code.
   * <p>
   * By implementing this interface, derived classes can modify the Java source
   * file before it's written out.
   */
  public interface ILineFilter
  {
    /**
     * @param sLine
     *        a non-null valid String that corresponds to one line. No '\n'
     *        included.
     * @return null to strip the line off. Otherwise the returned String will be
     *         written out. Do not add '\n' at the end of this string.
     * @exception ParseException
     *            when for some reason there's an error in the line.
     */
    @Nullable
    String process (@Nonnull String sLine) throws ParseException;
  }

  /**
   * A {@link ILineFilter} that combines two {@link ILineFilter}s.
   */
  public static final class ChainFilter implements ILineFilter
  {
    private final ILineFilter m_aFirst, m_aSecond;

    public ChainFilter (@Nonnull final ILineFilter aFirst, @Nonnull final ILineFilter aSecond)
    {
      m_aFirst = aFirst;
      m_aSecond = aSecond;
    }

    @Nullable
    public String process (@Nonnull final String sLine) throws ParseException
    {
      final String sProcessedLine = m_aFirst.process (sLine);
      if (sProcessedLine == null)
        return null;
      return m_aSecond.process (sProcessedLine);
    }
  }

  private class JStaticClass extends AbstractJClass
  {
    private final JTypeVar [] typeParams;

    JStaticClass ()
    {
      super (m_aPkg.owner ());
      // TODO: allow those to be specified
      typeParams = new JTypeVar [0];
    }

    @Override
    public String name ()
    {
      return m_sClassName;
    }

    @Override
    @Nonnull
    public String fullName ()
    {
      if (m_aPkg.isUnnamed ())
        return m_sClassName;
      return m_aPkg.name () + '.' + m_sClassName;
    }

    @Override
    @Nonnull
    public JPackage _package ()
    {
      return m_aPkg;
    }

    @Override
    public AbstractJClass _extends ()
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public Iterator <AbstractJClass> _implements ()
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public boolean isInterface ()
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public boolean isAbstract ()
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public JTypeVar [] typeParams ()
    {
      return typeParams;
    }

    @Override
    protected AbstractJClass substituteParams (final JTypeVar [] variables,
                                               final List <? extends AbstractJClass> bindings)
    {
      return this;
    }
  }
}
