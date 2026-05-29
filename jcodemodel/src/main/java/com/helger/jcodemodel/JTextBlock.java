package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

/// Represents a text block declaration, one line at a time.
///
/// ## Main usage
///
/// This class produces java text blocks in the generated source file.
/// It is used by adding lines to it, either one at a time or by passing multiline-string.
/// The added lines are split by newline separator, then double quotes are escaped when needed.
/// The [#keepWhiteSpaces] property specifies whether the string added are the one in the *file* (default), or in the resulting *String*.
///
/// ## Indenting
///
/// The [#indentSize] and [#indentChar] (by default size 0 and char space) specify which indentation is to be *added* at the beginning of each line.
/// Note that if the last line is not empty, then the *source* output will have requested indent but the *produced* String will have space indentation even when [indentChar] is set to tab.
///
/// ## Double quote escaping
///
/// triple doublequotes `"""` are escaped by having the third one backslashed `""\"`.
/// Plus, if the last line ends with an unescaped doublequote, this doublequote is escaped to avoid breaking the parser.
///
/// ## Property keepWhiteSpaces
///
/// The produced lines differ depending on [#keepWhitespaces]
///  - when false(default), the content of the **file** will be the one added.
///    Adding ` a ` will result in the textblock containing it, thus the resulting line will be `a` because of whitespaces strupping in textblocks
///  - when true, the content of the **parsed string** will be the one added.
///    Adding `  a  ` will result in the textblock containing instead `\040 a \040` to ensure the parsed string will be `  a  `.
/// In the later, if all lines start with a whitespace, then the first character is set to octal ; plus all line-ending whitespace are also set to octal.
///
///
/// [https://docs.oracle.com/en/java/javase/26/language/text-blocks.html]
/// @author Guillaume Le Louët (guillaume.lelouet@gmail.com)
///
@SuppressWarnings("serial")
public class JTextBlock implements IJExpression, Iterable<String> {

  private char indentChar = ' ';

  private int indentSize = 0;

  private List<String> lines = new ArrayList<>();

  private boolean keepWhitespaces = false;

  public JTextBlock() {
  }

  public JTextBlock(String value) {
    add(value);
  }

  /// convert a user line into several individual text block lines.
  static Stream<String> formatLines(String line) {
    return line.lines()
        .map(JTextBlock::formatLine);

  }

  /// format a standard String line to make it a text block line.
  static String formatLine(String line) {
    return line
        // escape triple parenthesis
        .replace("\"\"\"", "\"\"\\\"");
  }

  static String escapeLastIfDoubleQuote(String s) {
    if (s == null) {
      return null;
    }
    if (s.equals("\"")) {
      return "\\\"";
    }
    return s
        .replaceAll("([^\\\\])\"$", "$1\\\\\"");
  }

  /// @return this, to chain
  public JTextBlock indentSize(int val) {
    indentSize = val;
    return this;
  }

  /// @return this, to chain
  public int indentSize() {
    return indentSize;
  }

  /// @return this, to chain
  public JTextBlock indentChar(char val) {
    if (val != ' ' && val != '\t') {
      throw new UnsupportedOperationException("escape char must be space or tab");
    }
    indentChar = val;
    return this;
  }

  /// @return this, to chain
  public JTextBlock indentSpace() {
    return indentChar(' ');
  }

  /// @return this, to chain
  public JTextBlock indentTab() {
    return indentChar('\t');
  }

  /// @return this, to chain
  public char indentChar() {
    return indentChar;
  }

  /// @return this, to chain
  public JTextBlock keepWhitespaces(boolean verbatim) {
    keepWhitespaces = verbatim;
    return this;
  }

  public boolean keepWhitespaces() {
    return keepWhitespaces;
  }

  ///
  /// transforms a line to make it fit to the text block syntax.
  ///
  /// @param line if null nothing happens
  /// @return this, to chain
  public JTextBlock add(String line) {
    if (line != null) {
      formatLines(line).forEach(lines::add);
    }
    return this;
  }

  /// shortcut to add an empty line
  /// @return this, to chain
  public JTextBlock newline() {
    lines.add("");
    return this;
  }

  /// shortcut to add empty lines
  /// @param nb when <1 nothing happens
  /// @return this, to chain
  public JTextBlock newlines(int nb) {
    for (int i = 0; i < nb; i++) {
      newline();
    }
    return this;
  }

  /// unmodifiable iterator over the internal lines
  @Override
  public Iterator<String> iterator() {
    return Collections.unmodifiableList(lines).iterator();
  }

  public Stream<String> lines() {
    return lines.stream();
  }

  private static final String LIMITER = "\"\"\"";

  @Override
  public void generate(@NonNull IJFormatter f) {
    f.print(LIMITER).newline();
    String indent =
        indentSize <= 0 || lines.isEmpty()
            ? ""
            : Character.toString(indentChar).repeat(indentSize);
    boolean firstLine = true;
    boolean lastEmpty = true;
    // don't modify the internal list : work on a copy if modification required.
    List<String> modifiedLines = lines;
    // the last line must not end with unescaped doublequote
    // if that's the case, we copy the full list to not modify the existing one.
    if (!modifiedLines.isEmpty()) {
      String lastLine = modifiedLines.get(modifiedLines.size() - 1);
      String escapedLastLine = escapeLastIfDoubleQuote(lastLine);
      if (!escapedLastLine.equals(lastLine)) {
        modifiedLines = new ArrayList<>(lines);
        modifiedLines.set(modifiedLines.size() - 1, escapedLastLine);
      }
    }

    boolean escapeFirstChar = requiresEscapeFirstChar(keepWhitespaces, modifiedLines);
//		if (keepWhitespaces) {
//			System.err.println("escapefirst " + escapeFirstChar + " from " + modifiedLines);
//		}

    for (String line : modifiedLines) {
      if (!firstLine) {
        f.newline();
      }
      if (escapeFirstChar && !line.isEmpty()) {
        // replace starting space/tab by octal
        line =
            line
                .replaceAll("^ ", "\\\\040")
                .replaceAll("^\t", "\\\\011");
        escapeFirstChar = false;
      }
      if (keepWhitespaces) {
        // replace ending space/tab by octal
        line =
            line
                .replaceAll(" $", "\\\\040")
                .replaceAll("\t$", "\\\\011");
      }
      f.print(indent).print(line);
      firstLine = false;
      lastEmpty = line.isEmpty();
    }
    f.print(LIMITER);
    if (!lastEmpty && indentSize > 0) {
      // if the last line is not empty, then the delimiter is not enough to enforce
      // the indent. So we add a call after that.
      f
          .print(".indent(")
          .print("" + indentSize)
          .print(")");
    }
  }

  /// We need to escape initial whitespace to preserve indentation iff
  /// 1. we are keepWhitespaces
  /// 2. there are lines
  /// 3. all non-blank line start with space/tab
  /// 4. final line starts with space/tab even if blank
  static boolean requiresEscapeFirstChar(boolean keepWhitespaces, List<String> lines) {
    return keepWhitespaces
        && !lines.isEmpty()
        // no non-blank line that does not start with a space or tab
        && lines.stream()
            .filter(l -> !(l.isBlank() || l.startsWith(" ") || l.startsWith("\t")))
            .findAny().isEmpty()
        // if last line does not start with space/tab we don't need to escape, blank or
        // not.
        && lines.get(lines.size() - 1).matches("^[ \\t].*");
  }

}
