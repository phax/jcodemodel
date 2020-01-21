package com.helger.jcodemodel.inmemory;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

/**
 * java file manager that also checks and writes inside a given
 * {@link DynamicClassLoader}. This is used during compilation of a
 * {@link com.sun.codemodel.JCodeModel} specification.
 * <p>
 * basically must overwrite the
 * {@link #list(javax.tools.JavaFileManager.Location, String, Set, boolean)}
 * method to check inside the jar
 * </p>
 * <p>
 * most of the code comes from
 *
 * http://atamur.blogspot.fr/2009/10/using-built-in-javacompiler-with-custom.html
 * </p>
 *
 */
public class ClassLoaderFileManager extends ForwardingJavaFileManager<JavaFileManager> {

  public ClassLoaderFileManager(JavaFileManager fileManager, DynamicClassLoader cl) {
    super(fileManager);
    this.cl = cl;
  }

  DynamicClassLoader cl;

  @Override
  public ClassLoader getClassLoader(Location location) {
    return cl;
  }

  @Override
  public boolean hasLocation(Location location) {
    return super.hasLocation(location);
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
      throws IOException {
    if (location == StandardLocation.PLATFORM_CLASS_PATH || packageName.startsWith("java")) {
      // let standard manager handle
      return super.list(location, packageName, kinds, recurse);
    } else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
      // app specific classes are here
      return find(packageName);
    }
    return Collections.emptyList();
  }

  public static final String CLASS_FILE_EXTENSION = ".class";

  public List<JavaFileObject> find(String packageName) throws IOException {
    String javaPackageName = packageName.replaceAll("\\.", "/");
    List<JavaFileObject> result = new ArrayList<>();
    Enumeration<URL> urlEnumeration = cl.getResources(javaPackageName);
    while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the
      // classpath that has the given
      // package
      URL packageFolderURL = urlEnumeration.nextElement();
      result.addAll(listUnder(packageName, packageFolderURL));
    }

    return result;
  }

  private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
    File directory = new File(packageFolderURL.getFile());
    if (directory.isDirectory()) { // browse local .class files - useful for
      // local execution
      return processDir(packageName, directory);
    } else { // browse a jar file
      return processJar(packageFolderURL);
    } // maybe there can be something else for more involved class loaders
  }

  private List<JavaFileObject> processJar(URL packageFolderURL) {
    List<JavaFileObject> result = new ArrayList<>();
    try {
      String jarUri = packageFolderURL.toExternalForm().split("!")[0];

      JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
      String rootEntryName = jarConn.getEntryName();
      int rootEnd = rootEntryName.length() + 1;

      Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
      while (entryEnum.hasMoreElements()) {
        JarEntry jarEntry = entryEnum.nextElement();
        String name = jarEntry.getName();
        if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) {
          URI uri = URI.create(jarUri + "!/" + name);
          String binaryName = name.replaceAll("/", ".");
          binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

          result.add(new CustomJavaFileObject(binaryName, uri));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
    }
    return result;
  }

  private List<JavaFileObject> processDir(String packageName, File directory) {
    List<JavaFileObject> result = new ArrayList<>();

    File[] childFiles = directory.listFiles();
    for (File childFile : childFiles) {
      if (childFile.isFile()) {
        // We only want the .class files.
        if (childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
          String binaryName = packageName + "." + childFile.getName();
          binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

          result.add(new CustomJavaFileObject(binaryName, childFile.toURI()));
        }
      }
    }

    return result;
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
      throws IOException {
    CompiledCodeJavaFile ret = cl.getCode(className);
    if (ret == null) {
      try {
        ret = new CompiledCodeJavaFile(className);
        cl.setCode(ret);
      } catch (Exception e) {
        throw new UnsupportedOperationException("while creating code for " + className, e);
      }
    }
    return ret;
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    if (file instanceof CustomJavaFileObject) {
      return ((CustomJavaFileObject) file).binaryName();
    } else {
      // if it's not CustomJavaFileObject, then it's coming from standard file
      // manager - let it handle the file
      return super.inferBinaryName(location, file);
    }
  }

  /**
   * @author atamur
   * @since 15-Oct-2009
   */
  private static class CustomJavaFileObject implements JavaFileObject {
    private final String binaryName;
    private final URI uri;
    private final String name;

    public CustomJavaFileObject(String binaryName, URI uri) {
      this.uri = uri;
      this.binaryName = binaryName;
      name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath();
    }

    @Override
    public URI toUri() {
      return uri;
    }

    @Override
    public InputStream openInputStream() throws IOException {
      return uri.toURL().openStream(); // easy way to handle any URI!
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified() {
      return 0;
    }

    @Override
    public boolean delete() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind() {
      return Kind.CLASS;
    }

    @Override // copied from SImpleJavaFileManager
    public boolean isNameCompatible(String simpleName, Kind kind) {
      String baseName = simpleName + kind.extension;
      return kind.equals(getKind()) && (baseName.equals(getName()) || getName().endsWith("/" + baseName));
    }

    @Override
    public NestingKind getNestingKind() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
      throw new UnsupportedOperationException();
    }

    public String binaryName() {
      return binaryName;
    }

    @Override
    public String toString() {
      return "CustomJavaFileObject{" + "uri=" + uri + '}';
    }
  }

}
