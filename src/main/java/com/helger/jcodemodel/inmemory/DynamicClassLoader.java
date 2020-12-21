package com.helger.jcodemodel.inmemory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
  private final Map <String, ByteArrayOutputStream> m_aCustomResources = new HashMap <> ();

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
  public void addResources (final Map <String, ByteArrayOutputStream> resources)
  {
    m_aCustomResources.putAll (resources);
  }

  @Override
  protected Class <?> findClass (final String name) throws ClassNotFoundException
  {
    final CompiledCodeJavaFile cc = m_aCustomCompiledCode.get (name);
    if (cc == null)
      return super.findClass (name);
    final byte [] byteCode = cc.getByteCode ();
    return defineClass (name, byteCode, 0, byteCode.length);
  }

  /**
   * internal url handler that generates url to load inside its own resources,
   * if exists. It overloads the openConnection to provide an input stream if a
   * corresponding bytearray is found for the resource.
   */
  private final URLStreamHandler handler = new URLStreamHandler ()
  {
    @Override
    protected URLConnection openConnection (final URL u) throws IOException
    {
      return new URLConnection (u)
      {
        final ByteArrayOutputStream aBAOS = m_aCustomResources.get (u.getFile ());

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
          return new ByteArrayInputStream (aBAOS.toByteArray ());
        }
      };
    }
  };

  @Override
  protected URL findResource (final String name)
  {
    final ByteArrayOutputStream baos = m_aCustomResources.get (name);
    if (baos != null)
      try
      {
        return new URL ("memory", null, 0, name, handler);
      }
      catch (final MalformedURLException e)
      {
        throw new UnsupportedOperationException ("catch this", e);
      }

    return super.findResource (name);
  }

}
