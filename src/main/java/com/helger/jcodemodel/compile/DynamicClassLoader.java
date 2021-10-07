/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2021 Philip Helger + contributors
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

/**
 * class loader that allows dynamic classes and resources.
 * <p>
 * add class models using {@link #setCode(CompiledCodeJavaFile)} , add resources
 * using {@link #addResources(Map)}; then you can use it as a normal
 * classloader, eg {@link ClassLoader#loadClass(String)} or
 * {@link ClassLoader#getResource(String)}
 * </p>
 */
public class DynamicClassLoader extends ClassLoader
{
  private final Map <String, CompiledCodeJavaFile> m_aCustomCompiledCode = new HashMap <> ();
  private final Map <String, NonBlockingByteArrayOutputStream> m_aCustomResources = new HashMap <> ();

  /**
   * internal url handler that generates url to load inside its own resources,
   * if exists. It overloads the openConnection to provide an input stream if a
   * corresponding bytearray is found for the resource.
   */
  private final URLStreamHandler m_aURLStreamHandler = new URLStreamHandler ()
  {
    @Override
    protected URLConnection openConnection (final URL u) throws IOException
    {
      return new URLConnection (u)
      {
        final NonBlockingByteArrayOutputStream aBAOS = m_aCustomResources.get (u.getFile ());

        @Override
        public void connect () throws IOException
        {
          if (aBAOS == null)
            throw new FileNotFoundException (u.getFile ());
        }

        @Override
        public InputStream getInputStream () throws IOException
        {
          if (aBAOS == null)
            throw new FileNotFoundException (u.getFile ());
          return aBAOS.getAsInputStream ();
        }
      };
    }
  };

  /**
   * create a class loader with its parent.
   *
   * @param parent
   *        the classloader to fall back when a resource or class definition
   *        can't be found.
   */
  public DynamicClassLoader (final ClassLoader parent)
  {
    super (parent);
  }

  /**
   * set the bytecode for a given class
   *
   * @param cc
   *        the compiled java code file
   */
  public void setCode (@Nonnull final CompiledCodeJavaFile cc)
  {
    m_aCustomCompiledCode.put (cc.getName (), cc);
  }

  /**
   * get the bytecode for a given class name.
   *
   * @param fullClassName
   *        the full name of the class, including its package, eg
   *        java.lang.String
   * @return the existing compiledCode for that class, or null.
   */
  public CompiledCodeJavaFile getCode (final String fullClassName)
  {
    return m_aCustomCompiledCode.get (fullClassName);
  }

  /**
   * add a map of path-> resource
   *
   * @param resources
   *        all resources to add
   */
  public void addResources (final Map <String, NonBlockingByteArrayOutputStream> resources)
  {
    m_aCustomResources.putAll (resources);
  }

  @Override
  protected Class <?> findClass (final String sName) throws ClassNotFoundException
  {
    final CompiledCodeJavaFile cc = m_aCustomCompiledCode.get (sName);
    if (cc != null)
    {
      final byte [] aByteCode = cc.getByteCode ();
      return defineClass (sName, aByteCode, 0, aByteCode.length);
    }

    return super.findClass (sName);
  }

  @Override
  protected URL findResource (final String sName)
  {
    final NonBlockingByteArrayOutputStream aBAOS = m_aCustomResources.get (sName);
    if (aBAOS != null)
      try
      {
        return new URL ("memory", null, 0, sName, m_aURLStreamHandler);
      }
      catch (final MalformedURLException e)
      {
        throw new UnsupportedOperationException ("catch this", e);
      }

    return super.findResource (sName);
  }
}
