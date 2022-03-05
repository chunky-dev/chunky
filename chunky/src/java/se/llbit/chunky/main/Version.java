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
package se.llbit.chunky.main;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for version numbering.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Version {
  private static Properties properties;

  static {
    properties = new Properties();
    try {
      properties.load(Version.class.getClassLoader()
          .getResourceAsStream("se/llbit/chunky/main/Version.properties"));
    } catch (IOException e) {
      throw new Error(e);
    } catch (NullPointerException e) {
      // ignore, this is probably a build from within an IDE
    }
  }

  /**
   * @return Version string
   */
  public static String getVersion() {
    return properties.getProperty("version", "?-snapshot");
  }

  /**
   * @return Git commit sha
   */
  public static String getCommit() { return properties.getProperty("gitSha", "n/a"); }
}
