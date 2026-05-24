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
    return
        line
            // escape triple parenthesis
            .replace("\"\"\"", "\\\"\"\"")
            // trailing spaces/tabs are stripped : replace last with octal space
            .replaceAll("[ \t]$", "\\\\040");
  }

  private int indentSize = 0;

  public JTextBlock indentSize(int val) {
    indentSize = val;
    return this;
  }

  public int indentSize() {
    return indentSize;
  }

  private char indentChar = ' ';

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

  private List<String> lines = new ArrayList<>();

  ///
  /// transforms a line to make it fit to the text block syntax.
  ///
  /// @param line if null nothing happens
  public JTextBlock add(String line) {
    if (line != null) {
      formatLines(line).forEach(lines::add);
    }
    return this;
  }

  /// shortcut to add a new line
  public JTextBlock newline() {
    lines.add("");
    return this;
  }

  /// shortcut to add new lines
  /// if i<1 no new line is added
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
            : ("" + indentChar).repeat(indentSize);
    boolean first = true;
    boolean lastEmpty = true;
    for (String line : lines) {
      if (!first) {
        f.newline();
      }
      f.print(indent).print(line);
      first = false;
      lastEmpty = line.isEmpty();
    }
    f.print(LIMITER);
    if (!lastEmpty) {
      // if the last line is not empty, then the delimiter is not enough to enforce
      // the indent. So we add a call after that.
      f
          .print(".indent(")
          .print("" + indentSize)
          .print(")");
    }
  }

}
