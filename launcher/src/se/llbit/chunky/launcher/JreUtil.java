/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.launcher;

import java.io.File;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class JreUtil {
  /**
   * @param jre
   * @return {@code true} if the selected directory contains a
   * Java Runtime Environment
   */
  public static boolean isJreDir(File jre) {
    File executable = javaExecutable(jre);
    return executable != null && executable.canExecute();
  }

  /**
   * @param jre A valid JRE directory
   * @return {@code null} if the java executable was not find, or a
   * file object pointing to the executable
   */
  public static File javaExecutable(File jre) {
    if (!jre.isDirectory()) {
      return null;
    }
    File bin = new File(jre, "bin");
    if (bin != null && bin.isDirectory()) {
      String os = System.getProperty("os.name").toLowerCase();
      String exeName = os.contains("win") ? "java.exe" : "java";
      return new File(bin, exeName);
    }
    return null;
  }

  public static String javaCommand(String javaDir) {
    File executable = javaExecutable(new File(javaDir));
    if (executable == null) {
      // attempt to fall back on host Java runtime
      executable = javaExecutable(new File(System.getProperty("java.home")));
    }
    if (executable != null) {
      return executable.getAbsolutePath();
    } else {
      System.err.println("Can not run Chunky! Unable to locate Java runtime!");
      return "";
    }
  }

}
