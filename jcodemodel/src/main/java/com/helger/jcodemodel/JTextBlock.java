package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

/// Represents a text block declaration, one line at a time.
///
/// [https://docs.oracle.com/en/java/javase/26/language/text-blocks.html]
///
@SuppressWarnings("serial")
public class JTextBlock implements IJExpression, Iterable<String> {

  private char indentChar = ' ';

  private int indentSize = 0;

  private List<String> lines = new ArrayList<>();

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
        .replace("\"\"\"", "\"\"\\\"")
        // trailing spaces/tabs are stripped : replace last space octal space (ascii d32
        // = o040 )
        .replaceAll(" $", "\\\\040")
        // and last tab with octal tab (ascii d9 = o011 )
        .replaceAll("\t$", "\\\\011");
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

  public JTextBlock indentSize(int val) {
    indentSize = val;
    return this;
  }

  public int indentSize() {
    return indentSize;
  }

  public JTextBlock indentChar(char val) {
    if (val != ' ' && val != '\t') {
      throw new UnsupportedOperationException("escape char must be space or tab");
    }
    indentChar = val;
    return this;
  }

  public JTextBlock indentSpace() {
    return indentChar(' ');
  }

  public JTextBlock indentTab() {
    return indentChar('\t');
  }

  public char indentChar() {
    return indentChar;
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
    boolean first = true;
    boolean lastEmpty = true;
    // the last line must not end with unescaped doublequote
    if (!lines.isEmpty()) {
      String lastLine = lines.get(lines.size() - 1);
      lines.set(lines.size() - 1, escapeLastIfDoubleQuote(lastLine));
    }
    for (String line : lines) {
      if (!first) {
        f.newline();
      }
      f.print(indent).print(line);
      first = false;
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
