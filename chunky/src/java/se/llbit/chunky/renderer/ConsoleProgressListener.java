/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

import se.llbit.util.ProgressListener;

import java.text.DecimalFormat;
import java.time.Duration;

/**
 * Prints progress to standard out.
 */
public class ConsoleProgressListener implements ProgressListener {
  private int lastLineLength = 0;
  private final DecimalFormat decimalFormat;
  private static final int MAX_WHITESPACE = 160;
  private final String[] whitespace = new String[MAX_WHITESPACE];

  public ConsoleProgressListener() {
    decimalFormat = new DecimalFormat();
    decimalFormat.setGroupingSize(3);
    decimalFormat.setGroupingUsed(true);
  }

  @Override public void setProgress(String task, int done, int start, int target, Duration elapsedTime) {
    String line = String.format("%s: %.1f%% (%s of %s)", task, 100 * done / (float) target,
        decimalFormat.format(done), decimalFormat.format(target));
    output(line, done == target);
  }

  @Override public void setProgress(String task, int done, int start, int target, Duration elapsedTime, Duration remainingTime) {
    output(String.format("%s: %.1f%% (%s of %s) [ETA=%s]",
        task, 100 * done / (float) target,
        decimalFormat.format(done), decimalFormat.format(target),
        String.format("%d:%02d:%02d", remainingTime.toHours(), remainingTime.toMinutesPart(), remainingTime.toSecondsPart())),
        done == target);
  }

  /**
   * @param line the text to output
   * @param done {@code true} if this is the last output line for the current task
   */
  private void output(String line, boolean done) {
    if (lastLineLength > 0) {
      System.out.print("\r");
    }
    System.out.print(line);

    // Ensure that the rest of the line is cleared by
    // overwriting the end of the previous line with whitespace.
    int trailingWhitespace = lastLineLength - line.length();
    if (trailingWhitespace > 0) {
      System.out.print(whitespace(trailingWhitespace));
    }
    lastLineLength = line.length();

    if (done) {
      System.out.println();
      System.out.flush();
      lastLineLength = 0;
    }
  }

  /** Get whitespace string of the given length. */
  private String whitespace(int length) {
    if (length < 0) {
      throw new IllegalArgumentException();
    }
    if (length > MAX_WHITESPACE) {
      return whitespace(MAX_WHITESPACE) + whitespace(length - MAX_WHITESPACE);
    } else if (length == 0) {
      return "";
    } else if (length == 1) {
      return " ";
    } else if (whitespace[length] != null) {
      return whitespace[length];
    } else {
      int lengthA = length / 2;
      int lengthB = length - lengthA;
      String result = whitespace(lengthA) + whitespace(lengthB);
      whitespace[length] = result;
      return result;
    }
  }
}
