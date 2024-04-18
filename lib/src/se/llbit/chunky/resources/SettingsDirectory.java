/* Copyright (c) 2013-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources;

import se.llbit.chunky.PersistentSettings;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

/**
 * Utility class with helper function to locate the Chunky settings directory.
 */
public final class SettingsDirectory {
  private static final String SETTINGS_DIR = ".chunky";

  private SettingsDirectory() {
  }

  /**
   * @return {@code true} if the settings directory could be located
   */
  public static boolean findSettingsDirectory() {
    File directory = getSettingsDirectory();
    return directory != null && directory.isDirectory();
  }

  /**
   * Locates the Chunky settings directory. The following locations are
   * tested in the listed order:
   * <p>
   * <ul>
   * <li>The path specified by the "chunky.home" system property.
   * <li>The current working directory.
   * <li>The directory where the Chunky or Chunky Launcher Jar file is.
   * <li>The directory $HOME/.chunky, where HOME is the current user home directory.
   * </ul>
   * <p>
   * Falls back on home directory.
   *
   * @return The configured settings directory, or {@code null}
   * if the settings directory could not be located.
   */
  public static File getSettingsDirectory() { // TODO: make this return Optional<File>.
    File directory = getChunkyHomeDirectoryOverwrite().orElse(null);
    if (directory != null) {
      // We don't check if this is a valid settings directory because
      // we should always respect the system property in case the user
      // has manually specified it.
      return directory;
    }
    directory = getWorkingDirectory();
    if (isSettingsDirectory(directory)) {
      return directory;
    }
    directory = getProgramDirectory();
    if (isSettingsDirectory(directory)) {
      return directory;
    }
    directory = getHomeDirectory();
    if (isSettingsDirectory(directory)) {
      return directory;
    }
    return null;
  }

  /**
   * Test if the given directory contains a Chunky configuration file.
   */
  private static boolean isSettingsDirectory(File settingsDir) {
    if (settingsDir != null && settingsDir.exists() &&
        settingsDir.isDirectory() && settingsDir.canWrite()) {
      File settingsFile = new File(settingsDir, PersistentSettings.SETTINGS_FILE);
      if (settingsFile.isFile() && settingsFile.canRead()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the path specified by the "chunky.home" system property, if any
   */
  public static Optional<File> getChunkyHomeDirectoryOverwrite() {
    String chunkyHomeProperty = System.getProperty("chunky.home");
    if (chunkyHomeProperty != null && !chunkyHomeProperty.isEmpty()) {
      return Optional.of(new File(chunkyHomeProperty));
    }
    return Optional.empty();
  }

  /**
   * @return the home directory of the current user.
   */
  public static File getHomeDirectory() {
    String workingDir = System.getProperty("user.home");
    if (workingDir != null && !workingDir.isEmpty()) {
      return new File(workingDir, SETTINGS_DIR);
    }
    return null;
  }

  /**
   * @return the directory where Chunky was started from.
   */
  public static File getWorkingDirectory() {
    String workingDir = System.getProperty("user.dir");
    if (workingDir != null && !workingDir.isEmpty()) {
      return new File(workingDir);
    }
    return null;
  }

  /**
   * @return the plugin directory
   */
  public static File getPluginsDirectory() {
    return new File(getSettingsDirectory(), "plugins");
  }

  /**
   * @return the directory containing the Chunky Jar file.
   */
  public static File getProgramDirectory() {
    URL location = SettingsDirectory.class.getProtectionDomain().getCodeSource().getLocation();
    try {
      File dir = new File(location.toURI());
      if (dir.isFile()) {
        dir = dir.getParentFile();
      }
      return dir;
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
