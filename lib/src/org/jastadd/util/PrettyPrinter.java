/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jastadd.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class PrettyPrinter {
  private final String indentation;
  private final java.util.List<String> ind = new ArrayList<String>(32);

  {
    ind.add("");
  }

  private final Stack<Integer> indentStack = new Stack<Integer>();

  {
    indentStack.push(0);
  }

  private int currentIndent = 0;

  private PrintStream out = System.out;
  private boolean newline = false;

  /**
   * @param ind
   */
  public PrettyPrinter(String ind) {
    this.indentation = ind;
  }

  /**
   * @param ind
   * @param target
   */
  public PrettyPrinter(String ind, PrintStream target) {
    this(ind);
    out = target;
  }

  /**
   * @param target
   */
  public void setTarget(PrintStream target) {
    out = target;
  }

  /**
   * @param level The level of indentation
   * @return The indentation string for the given indentation level
   */
  public String getIndentation(int level) {
    while (ind.size() < (level + 1)) {
      ind.add(ind.get(ind.size() - 1) + indentation);
    }
    return ind.get(level);
  }


  /**
   * @param str
   */
  public void print(String str) {
    indentNewline();
    out.print(str);
  }

  /**
   *
   */
  public void println() {
    out.println();
    newline = true;
  }

  /**
   * @param node
   */
  public void print(PrettyPrintable node) {
    pushIndentation();
    node.prettyPrint(this);
    popIndentation();
  }

  /**
   * @param level
   */
  public void indent(int level) {
    indentNewline();
    currentIndent = level;
    out.print(getIndentation(level));
  }

  public void setIndent(int level) {
    currentIndent = level;
  }

  private void pushIndentation() {
    indentStack.push(currentIndent + indentStack.peek());
    currentIndent = 0;
  }

  private void popIndentation() {
    currentIndent = indentStack.pop();
    currentIndent -= indentStack.peek();
  }

  private void indentNewline() {
    if (newline) {
      out.print(getIndentation(indentStack.peek()));
      newline = false;
    }
  }
}
