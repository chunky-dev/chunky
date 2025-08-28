/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import se.llbit.log.Level;
import se.llbit.log.Receiver;

/**
 * A headless log receiver that tracks the number of errors and warnings generated.
 */
public class HeadlessErrorTrackingLogger extends Receiver {
  private int numErrors = 0;
  private int numWarnings = 0;

  @Override public void logEvent(Level level, String message) {
    if (level == Level.ERROR) {
      System.err.println();  // Clear the current progress line.
      System.err.println(message);
      numErrors += 1;
    } else {
      if (level == Level.WARNING) {
        numWarnings += 1;
      }
      System.out.println();  // Clear the current progress line.
      System.out.println(message);
    }
  }

  public int getNumErrors() {
    return numErrors;
  }

  public int getNumWarnings() {
    return numWarnings;
  }
}
