/*
 * Copyright (c) 2024 Chunky contributors
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

package se.llbit.chunky.cli;

import se.llbit.chunky.HelpCopyright;

public class CliUtil {
  /**
   * Width of the CLI help
   */
  public static final int CLI_WIDTH = 102;

  /**
   * Make the header for help messages
   * @param name        Name of the program
   * @param version     Version of the program
   * @param description Description after the copyright statement
   */
  public static String makeHelpHeader(String name, String version, String description) {
    return String.format("%s %s, ", name, version) +
      HelpCopyright.COPYRIGHT_LINE + "\n\n" +
      HelpCopyright.COPYRIGHT_DISCLAIMER + "\n\n" +
      description;
  }
}
