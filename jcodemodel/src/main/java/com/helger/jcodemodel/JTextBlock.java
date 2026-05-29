package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringReplace;
import com.helger.cache.regex.RegExHelper;

/**
 * Represents a text block declaration, one line at a time.
 * <h2>Main usage</h2>
 * <p>
 * This class produces java text blocks in the generated source file. It is used by adding lines to
 * it, either one at a time or by passing multiline-string. The added lines are split by newline
 * separator, then double quotes are escaped when needed. The {@link #keepWhitespaces} property
 * specifies whether the string added are the one in the <em>file</em> (default), or in the
 * resulting <em>String</em>.
 * <h2>Indenting</h2>
 * <p>
 * The {@link #indentSize} and {@link #indentChar} (by default size 0 and char space) specify which
 * indentation is to be <em>added</em> at the beginning of each line. Note that if the last line is
 * not empty, then the <em>source</em> output will have requested indent but the <em>produced</em>
 * String will have space indentation even when {@link #indentChar} is set to tab.
 * <h2>Double quote escaping</h2>
 * <p>
 * triple doublequotes <code>"""</code> are escaped by having the third one backslashed
 * <code>""\"</code>. Plus, if the last line ends with an unescaped doublequote, this doublequote is
 * escaped to avoid breaking the parser.
 * <h2>Property keepWhiteSpaces</h2>
 * <p>
 * The produced lines differ depending on {@link #keepWhitespaces}
 * <ul>
 * <li>when false(default), the content of the <b>file</b> will be the one added. Adding
 * <code> a </code> will result in the textblock containing it, thus the resulting line will be
 * <code>a</code> because of whitespaces strupping in textblocks</li>
 * <li>when true, the content of the <b>parsed string</b> will be the one added. Adding
 * <code>  a  </code> will result in the textblock containing instead <code>\040 a \040</code> to
 * ensure the parsed string will be <code>  a  </code>.</li>
 * </ul>
 * In the later, if all lines start with a whitespace, then the first character is set to octal ;
 * plus all line-ending whitespace are also set to octal.
 *
 * @see <a href=
 *      "https://docs.oracle.com/en/java/javase/26/language/text-blocks.html">text-blocks</a>
 * @author Guillaume Le Louët (guillaume.lelouet@gmail.com)
 */
public class JTextBlock implements IJExpression, Iterable <String>
{
  private static final String LIMITER = "\"\"\"";

  private char indentChar = ' ';
  private int indentSize = 0;
  private final List <String> lines = new ArrayList <> ();
  private boolean keepWhitespaces = false;

  public JTextBlock ()
  {}

  public JTextBlock (final @Nullable String value)
  {
    add (value);
  }

  /**
   * convert a user line into several individual text block lines.
   */
  static @NonNull Stream <String> formatLines (final @NonNull String line)
  {
    return line.lines ().map (JTextBlock::formatLine);
  }

  /**
   * format a standard String line to make it a text block line.
   */
  static @Nullable String formatLine (final @Nullable String line)
  {
    // escape triple parenthesis
    return StringReplace.replaceAll (line, "\"\"\"", "\"\"\\\"");
  }

  static @Nullable String escapeLastIfDoubleQuote (final @Nullable String s)
  {
    if (s == null)
      return null;
    if (s.equals ("\""))
      return "\\\"";
    return RegExHelper.stringReplacePattern ("([^\\\\])\"$", s, "$1\\\\\"");
  }

  /**
   * @return this, to chain
   */
  public @Nonnegative int indentSize ()
  {
    return indentSize;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock indentSize (final @Nonnegative int val)
  {
    ValueEnforcer.isGE0 (val, "IndentSize");
    indentSize = val;
    return this;
  }

  /**
   * @return this, to chain
   */
  public char indentChar ()
  {
    return indentChar;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock indentChar (final char val)
  {
    if (val != ' ' && val != '\t')
      throw new IllegalArgumentException ("escape char must be space or tab");

    indentChar = val;
    return this;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock indentSpace ()
  {
    return indentChar (' ');
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock indentTab ()
  {
    return indentChar ('\t');
  }

  public boolean keepWhitespaces ()
  {
    return keepWhitespaces;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock keepWhitespaces (final boolean verbatim)
  {
    keepWhitespaces = verbatim;
    return this;
  }

  /**
   * transforms a line to make it fit to the text block syntax.
   *
   * @param line
   *        if null nothing happens
   * @return this, to chain
   */
  public @NonNull JTextBlock add (final @Nullable String line)
  {
    if (line != null)
      formatLines (line).forEach (lines::add);

    return this;
  }

  /**
   * shortcut to add an empty line
   *
   * @return this, to chain
   */
  public @NonNull JTextBlock newline ()
  {
    lines.add ("");
    return this;
  }

  /**
   * shortcut to add empty lines
   *
   * @param nb
   *        when &lt;1 nothing happens
   * @return this, to chain
   */
  public @NonNull JTextBlock newlines (final int nb)
  {
    for (int i = 0; i < nb; i++)
      newline ();

    return this;
  }

  /**
   * unmodifiable iterator over the internal lines
   */
  public @NonNull Iterator <String> iterator ()
  {
    return Collections.unmodifiableList (lines).iterator ();
  }

  public @NonNull Stream <String> lines ()
  {
    return lines.stream ();
  }

  public void generate (@NonNull final IJFormatter f)
  {
    f.print (LIMITER).newline ();
    final String indent = indentSize <= 0 || lines.isEmpty () ? ""
                                                              : Character.toString (indentChar).repeat (indentSize);
    boolean firstLine = true;
    boolean lastEmpty = true;
    // don't modify the internal list : work on a copy if modification required.
    List <String> modifiedLines = lines;
    // the last line must not end with unescaped doublequote
    // if that's the case, we copy the full list to not modify the existing one.
    if (!modifiedLines.isEmpty ())
    {
      final String lastLine = modifiedLines.get (modifiedLines.size () - 1);
      final String escapedLastLine = escapeLastIfDoubleQuote (lastLine);
      if (!escapedLastLine.equals (lastLine))
      {
        modifiedLines = new ArrayList <> (lines);
        modifiedLines.set (modifiedLines.size () - 1, escapedLastLine);
      }
    }

    boolean escapeFirstChar = requiresEscapeFirstChar (keepWhitespaces, modifiedLines);
    // if (keepWhitespaces) {
    // System.err.println("escapefirst " + escapeFirstChar + " from " + modifiedLines);
    // }

    for (String line : modifiedLines)
    {
      if (!firstLine)
        f.newline ();

      if (escapeFirstChar && !line.isEmpty ())
      {
        // replace starting space/tab by octal
        line = RegExHelper.stringReplacePattern ("^ ", line, "\\\\s");
        line = RegExHelper.stringReplacePattern ("^\t", line, "\\\\t");
        escapeFirstChar = false;
      }
      if (keepWhitespaces)
      {
        // replace ending space/tab by octal
        line = RegExHelper.stringReplacePattern (" $", line, "\\\\s");
        line = RegExHelper.stringReplacePattern ("\t$", line, "\\\\t");
      }
      f.print (indent).print (line);
      firstLine = false;
      lastEmpty = line.isEmpty ();
    }
    f.print (LIMITER);
    if (!lastEmpty && indentSize > 0)
    {
      // if the last line is not empty, then the delimiter is not enough to enforce
      // the indent. So we add a call after that.
      f.print (".indent(").print (Integer.toString (indentSize)).print (")");
    }
  }

  /**
   * We need to escape initial whitespace to preserve indentation iff
   * <ol>
   * <li>we are keepWhitespaces</li>
   * <li>there are lines</li>
   * <li>all non-blank line start with space/tab</li>
   * <li>final line starts with space/tab even if blank</li>
   * </ol>
   */
  static boolean requiresEscapeFirstChar (final boolean keepWhitespaces, final @NonNull List <String> lines)
  {
    if (!keepWhitespaces)
      return false;

    if (lines.isEmpty ())
      return false;

    // no non-blank line that does not start with a space or tab
    if (lines.stream ()
             .filter (l -> !(l.isBlank () || l.startsWith (" ") || l.startsWith ("\t")))
             .findAny ()
             .isPresent ())
      return false;

    // if last line does not start with space/tab we don't need to escape, blank or
    // not.
    return RegExHelper.stringMatchesPattern ("^[ \\t]+.*", lines.get (lines.size () - 1));
  }

}
