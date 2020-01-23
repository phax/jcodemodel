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

/**
 * class loader that allows dynamic classes and resources.
 *
 * <p>
 * add class models using {@link #setCode(CompiledCodeJavaFile)} , add resources
 * using {@link #addResources(Map)} ; then you can use it as a normal
 * classloader, eg {@link ClassLoader.#loadClass(String)} or
 * {@link ClassLoader.#getResource(String)}
 * </p>
 *
 */
public class DynamicClassLoader extends ClassLoader
{

  private Map <String, CompiledCodeJavaFile> customCompiledCode = new HashMap <> ();

  private Map <String, ByteArrayOutputStream> customResources = new HashMap <> ();

  /**
   * create a class loader with its parent.
   *
   * @param parent
   *        the classloader to fall back when a resource or class definition
   *        can't be found.
   */
  public DynamicClassLoader (ClassLoader parent)
  {
    super (parent);
  }

  /** set the bytecode for a given class */
  public void setCode (CompiledCodeJavaFile cc)
  {
    customCompiledCode.put (cc.getName (), cc);
  }

  /**
   * get the bytecode for a given class name.
   *
   * @param fullClassName
   *        the full name of the class, including its package, eg
   *        java.lang.String
   * @return the existing compiledCode for that class, or null.
   */
  public CompiledCodeJavaFile getCode (String fullClassName)
  {
    return customCompiledCode.get (fullClassName);
  }

  /**
   * add a map of path-> resource
   *
   * @param resources
   */
  public void addResources (Map <String, ByteArrayOutputStream> resources)
  {
    customResources.putAll (resources);
  }

  @Override
  protected Class <?> findClass (String name) throws ClassNotFoundException
  {
    CompiledCodeJavaFile cc = customCompiledCode.get (name);
    if (cc == null)
      return super.findClass (name);
    byte[] byteCode = cc.getByteCode ();
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
    protected URLConnection openConnection (URL u) throws IOException
    {
      return new URLConnection (u)
      {

        ByteArrayOutputStream baos = customResources.get (u.getFile ());

        @Override
        public void connect () throws IOException
        {
          if (baos == null)
            throw new FileNotFoundException (u.getFile ());
        }

        @Override
        public InputStream getInputStream () throws IOException
        {
          if (baos == null)
            throw new FileNotFoundException (u.getFile ());
          return new ByteArrayInputStream (baos.toByteArray ());
        }
      };
    }

  };

  @Override
  protected URL findResource (String name)
  {
    ByteArrayOutputStream baos = customResources.get (name);
    if (baos != null)
      try
    {
        return new URL ("memory", null, 0, name, handler);
    }
    catch (MalformedURLException e)
    {
      throw new UnsupportedOperationException ("catch this", e);
    }
    else
      return super.findResource (name);
  }

}
