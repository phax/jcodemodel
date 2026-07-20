/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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

  private char m_cIndentChar = ' ';
  private int m_nIndentSize = 0;
  private final List <String> m_aLines = new ArrayList <> ();
  private boolean m_bKeepWhitespaces = false;

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
    return m_nIndentSize;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock indentSize (final @Nonnegative int val)
  {
    ValueEnforcer.isGE0 (val, "IndentSize");
    m_nIndentSize = val;
    return this;
  }

  /**
   * @return this, to chain
   */
  public char indentChar ()
  {
    return m_cIndentChar;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock indentChar (final char val)
  {
    if (val != ' ' && val != '\t')
      throw new IllegalArgumentException ("escape char must be space or tab");

    m_cIndentChar = val;
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
    return m_bKeepWhitespaces;
  }

  /**
   * @return this, to chain
   */
  public @NonNull JTextBlock keepWhitespaces (final boolean verbatim)
  {
    m_bKeepWhitespaces = verbatim;
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
    if (line != null) {
      formatLines (line).forEach (m_aLines::add);
    }

    return this;
  }

  /**
   * shortcut to add an empty line
   *
   * @return this, to chain
   */
  public @NonNull JTextBlock newline ()
  {
    m_aLines.add ("");
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
  @Override
  public @NonNull Iterator <String> iterator ()
  {
    return Collections.unmodifiableList (m_aLines).iterator ();
  }

  public @NonNull Stream <String> lines ()
  {
    return m_aLines.stream ();
  }

  @Override
  public void generate (@NonNull final IJFormatter f)
  {
    StringBuilder sb = new StringBuilder();
    String newLine = f.getNewLine();
    sb.append(LIMITER).append(newLine);
    final String indent = m_nIndentSize <= 0 || m_aLines.isEmpty () ? ""
                                                                    : Character.toString (m_cIndentChar)
                                                                               .repeat (m_nIndentSize);
    boolean firstLine = true;
    boolean lastEmpty = true;
    // don't modify the internal list : work on a copy if modification required.
    List <String> modifiedLines = m_aLines;
    // the last line must not end with unescaped doublequote
    // if that's the case, we copy the full list to not modify the existing one.
    if (!modifiedLines.isEmpty ())
    {
      final String lastLine = modifiedLines.get (modifiedLines.size () - 1);
      final String escapedLastLine = escapeLastIfDoubleQuote (lastLine);
      if (!escapedLastLine.equals (lastLine))
      {
        modifiedLines = new ArrayList <> (m_aLines);
        modifiedLines.set (modifiedLines.size () - 1, escapedLastLine);
      }
    }

    boolean escapeFirstChar = requiresEscapeFirstChar (m_bKeepWhitespaces, modifiedLines);

    for (String line : modifiedLines)
    {
      if (!firstLine) {
        sb.append(newLine);
      }

      if (escapeFirstChar && !line.isEmpty ())
      {
        // replace starting space/tab by octal
        line = RegExHelper.stringReplacePattern ("^ ", line, "\\\\s");
        line = RegExHelper.stringReplacePattern ("^\t", line, "\\\\t");
        escapeFirstChar = false;
      }
      if (m_bKeepWhitespaces)
      {
        // replace ending space/tab by octal
        line = RegExHelper.stringReplacePattern (" $", line, "\\\\s");
        line = RegExHelper.stringReplacePattern ("\t$", line, "\\\\t");
      }
      sb.append(indent).append(line);
      firstLine = false;
      lastEmpty = line.isEmpty ();
    }
    sb.append(LIMITER);
    if (!lastEmpty && m_nIndentSize > 0)
    {
      // if the last line is not empty, then the delimiter is not enough to enforce
      // the indent. So we add a call after that.
      sb.append(".indent(").append (Integer.toString (m_nIndentSize)).append (")");
    }
    f.print(sb.toString());
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
    if (!keepWhitespaces) {
      return false;
    }

    if (lines.isEmpty ()) {
      return false;
    }

    // no non-blank line that does not start with a space or tab
    if (lines.stream ()
             .filter (l -> !(l.isBlank () || l.startsWith (" ") || l.startsWith ("\t")))
             .findAny ()
             .isPresent ()) {
      return false;
    }

    // if last line does not start with space/tab we don't need to escape, blank or
    // not.
    return RegExHelper.stringMatchesPattern ("^[ \\t]+.*", lines.get (lines.size () - 1));
  }

}
