/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.VersionInfo.Library;
import se.llbit.chunky.launcher.VersionInfo.LibraryStatus;

/**
 * Helper class to download an update with console output.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ConsoleUpdater {
  public static void update(VersionInfo version) {
    File chunkyDir = PersistentSettings.settingsDirectory();
    File libDir = new File(chunkyDir, "lib");
    if (!libDir.isDirectory()) {
      libDir.mkdirs();
    }
    File versionsDir = new File(chunkyDir, "versions");
    if (!versionsDir.isDirectory()) {
      versionsDir.mkdirs();
    }
    Collection<VersionInfo.Library> neededLibraries = new LinkedList<VersionInfo.Library>();
    for (Library lib : version.libraries) {
      LibraryStatus libStatus = lib.testIntegrity(libDir);
      if (libStatus != LibraryStatus.PASSED && libStatus != LibraryStatus.INCOMPLETE_INFO) {
        neededLibraries.add(lib);

        // pretty print library size
        float size = lib.size;
        String unit = "B";
        if (size >= 1024 * 1024) {
          size /= 1024 * 1024;
          unit = "MiB";
        } else if (size >= 1024) {
          size /= 1024;
          unit = "KiB";
        }
        String libSize;
        if (size >= 10) {
          libSize = String.format("%d %s", (int) size, unit);
        } else {
          libSize = String.format("%.1f %s", size, unit);
        }
        System.out.print("Downloading " + lib + " [" + libSize + "]...");

        if (downloadLibrary(libDir, lib, System.out)) {
          System.out.println("done!");
        } else {
          return;
        }
      }
    }
    try {
      File versionFile = new File(versionsDir, version.name + ".json");
      version.writeTo(versionFile);
    } catch (IOException e) {
      System.err.println("Failed to update version info. Please try again later.");
    }
  }

  /**
   * Attempt to download a library.
   *
   * @param libDir
   * @param lib
   * @param err
   * @return {@code true} if the library was downloaded successfully
   */
  private static boolean downloadLibrary(File libDir, Library lib, PrintStream err) {
    if (!lib.url.isEmpty()) {
      DownloadStatus result = UpdateDialog.tryDownload(libDir, lib, lib.url);
      switch (result) {
        case MALFORMED_URL:
          err.println("Malformed URL: " + lib.url);
          break;
        case FILE_NOT_FOUND:
          err.println("File not found: " + lib.url);
          break;
        case DOWNLOAD_FAILED:
          err.println("Download failed: " + lib.url);
          break;
        case SUCCESS:
          return true;
      }
    }
    String defaultUrl = "http://chunkyupdate.llbit.se/lib/" + lib.name;
    if (!lib.url.isEmpty()) {
      err.print("  retrying with URL=" + defaultUrl + "...");
    }
    DownloadStatus result = UpdateDialog.tryDownload(libDir, lib, defaultUrl);
    switch (result) {
      case MALFORMED_URL:
        err.println("Malformed URL: " + defaultUrl);
        return false;
      case FILE_NOT_FOUND:
        err.println("File not found: " + defaultUrl);
        return false;
      case DOWNLOAD_FAILED:
        err.println("Download failed: " + defaultUrl);
        return false;
      case SUCCESS:
        return true;
    }
    return false;
  }

}
