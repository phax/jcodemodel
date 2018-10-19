package com.helger.jcodemodel.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;
import com.helger.jcodemodel.JFormatter;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.SourcePrintWriter;
import com.helger.jcodemodel.fmt.AbstractJResourceFile;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Java Code Model Builder
 *
 * @author Philip Helger
 * @since 3.2.0
 */
public class JCMWriter
{
  /** default is 4 spaces */
  public static final String DEFAULT_INDENT_STRING = "    ";

  /** Cached default new line */
  private static String s_sDefaultNewLine;

  @Nonnull
  public static String getDefaultNewLine ()
  {
    String ret = s_sDefaultNewLine;
    if (ret == null)
    {
      try
      {
        ret = s_sDefaultNewLine = System.getProperty ("line.separator");
      }
      catch (final Exception ex)
      {
        // Fall through
      }

      // Fall back
      if (ret == null || ret.length () == 0)
        ret = s_sDefaultNewLine = "\n";
    }
    return ret;
  }

  private final JCodeModel m_aCM;

  /** The charset used for building the output - null means system default */
  private Charset m_aCharset;

  /** The newline string to be used. Defaults to system default */
  private String m_sNewLine = getDefaultNewLine ();

  /**
   * String to be used for each indentation. Defaults to four spaces.
   */
  private String m_sIndentString = DEFAULT_INDENT_STRING;

  public JCMWriter (@Nonnull final JCodeModel aCM)
  {
    m_aCM = aCM;
  }

  /**
   * @return The default charset used for building. <code>null</code> means
   *         system default.
   */
  @Nullable
  public Charset getCharset ()
  {
    return m_aCharset;
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
  public JCMWriter setCharset (@Nullable final Charset aCharset)
  {
    m_aCharset = aCharset;
    return this;
  }

  /**
   * @return The newline string to be used. Defaults to system default
   */
  @Nonnull
  public String getNewLine ()
  {
    return m_sNewLine;
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
  public JCMWriter setNewLine (@Nonnull final String sNewLine)
  {
    JCValueEnforcer.notEmpty (sNewLine, "NewLine");
    m_sNewLine = sNewLine;
    return this;
  }

  @Nonnull
  public String getIndentString ()
  {
    return m_sIndentString;
  }

  @Nonnull
  public JCMWriter setIndentString (@Nonnull final String sIndentString)
  {
    JCValueEnforcer.notNull (sIndentString, "IndentString");
    m_sIndentString = sIndentString;
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
    AbstractCodeWriter aSrcWriter = new FileCodeWriter (aSrcDir, m_aCharset, m_sNewLine);
    AbstractCodeWriter aResWriter = new FileCodeWriter (aResourceDir, m_aCharset, m_sNewLine);
    if (aStatusPS != null)
    {
      aSrcWriter = new ProgressCodeWriter (aSrcWriter, aStatusPS);
      aResWriter = new ProgressCodeWriter (aResWriter, aStatusPS);
    }
    build (aSrcWriter, aResWriter);
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
   * @param aSourceWriter
   *        Source code writer
   * @param aResourceWriter
   *        Resource writer
   * @throws IOException
   *         on IO error
   */
  public void build (@Nonnull final AbstractCodeWriter aSourceWriter,
                     @Nonnull final AbstractCodeWriter aResourceWriter) throws IOException
  {
    try
    {
      // Copy to avoid concurrent modification exception
      final List <JPackage> aPackages = m_aCM.getAllPackages ();
      for (final JPackage aPackage : aPackages)
        buildPackage (aSourceWriter, aResourceWriter, aPackage);
    }
    finally
    {
      aSourceWriter.close ();
      aResourceWriter.close ();
    }
  }

  @Nonnull
  private JFormatter _createJavaSourceFileWriter (@Nonnull final AbstractCodeWriter aSrcWriter,
                                                  @Nonnull final JPackage aPackage,
                                                  @Nonnull final String sClassName) throws IOException
  {
    final SourcePrintWriter aWriter = aSrcWriter.openSource (aPackage, sClassName + ".java");
    final JFormatter ret = new JFormatter (aWriter, m_sIndentString);
    // Add all classes to not be imported (may be empty)
    ret.addDontImportClasses (m_aCM.getAllDontImportClasses ());
    return ret;
  }

  public void buildPackage (@Nonnull final AbstractCodeWriter aSrcWriter,
                            @Nonnull final AbstractCodeWriter aResWriter,
                            @Nonnull final JPackage aPackage) throws IOException
  {
    // write classes
    for (final JDefinedClass c : aPackage.classes ())
    {
      if (c.isHidden ())
      {
        // don't generate this file
        continue;
      }

      try (final JFormatter f = _createJavaSourceFileWriter (aSrcWriter, aPackage, c.name ()))
      {
        f.writeClassFull (c);
      }
    }

    // write package annotations
    final Collection <JAnnotationUse> aAnnotations = aPackage.annotations ();
    final JDocComment aJavaDoc = aPackage.javadoc ();
    if (!aAnnotations.isEmpty () || !aJavaDoc.isEmpty ())
    {
      try (final JFormatter f = _createJavaSourceFileWriter (aSrcWriter, aPackage, "package-info"))
      {
        if (!aJavaDoc.isEmpty ())
          f.generable (aJavaDoc);

        // TODO: think about importing
        for (final JAnnotationUse a : aAnnotations)
          f.generable (a).newline ();

        f.declaration (aPackage);
      }
    }

    // write resources
    for (final AbstractJResourceFile rsrc : aPackage.getAllResourceFiles ())
    {
      final AbstractCodeWriter cw = rsrc.isResource () ? aResWriter : aSrcWriter;
      try (final OutputStream os = new BufferedOutputStream (cw.openBinary (aPackage, rsrc.name ())))
      {
        rsrc.build (os);
      }
    }
  }
}
