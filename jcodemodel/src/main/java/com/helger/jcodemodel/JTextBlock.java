package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

/// Represents a text block declaration, one line at a time.
///
///
///
/// ## doublequote escaping
///
/// triple doublequotes `"""` are escaped by having the third one backslashed `""\"`.
/// Plus, if the last line ends with an unescaped doublequote, this doublequote is escaped to avoid breaking the parser.
///
/// ## keepWhiteSpaces
///
/// The output of the lines differ depending on [#keepWhitespaces]
///  - when false(default), the content of the file will be the one added.
///  - when true, the content of the resulting string will be the one added
/// In the later, if all lines start with a whitespace, then all starting whitespace are set to otal ; plus all ending whitespace are also set to octal.
///
/// [https://docs.oracle.com/en/java/javase/26/language/text-blocks.html]
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
    // we need to escape starting whitespaces iff we are verbatim and all lines
    // start with space or tab
    boolean requireEscapeStart =
        keepWhitespaces
            && modifiedLines.stream()
                .filter(l -> !l.startsWith(" ") && !l.startsWith("\t"))
                .findAny().isEmpty();
    for (String line : modifiedLines) {
      if (!firstLine) {
        f.newline();
      }
      if (requireEscapeStart) {
        // replace starting space/tab by octal
        line =
            line
                .replaceAll("^ ", "\\\\040")
                .replaceAll("^\t", "\\\\011");
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

}
