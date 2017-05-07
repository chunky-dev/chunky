/* Copyright (c) 2013-2017 Jesper Öqvist <jesper@llbit.se>
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
package releasetools;

import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.PrettyPrinter;
import se.llbit.util.Util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;

/**
 * This is a tool for generating the version JSON file and the release notes
 * for a Chunky release or snapshot.
 */
public class ReleaseBuilder {
  private static final FileFilter JAR_FILES = file ->
      file.isDirectory() || file.getName().endsWith(".jar");

  static final String LIBRARY_PATH = "chunky/lib";
  private static final String SYS_NL = System.getProperty("line.separator");

  public static void main(String[] args) {
    for (String arg : args) {
      if (arg.equals("-help") || arg.equals("-h")) {
        printHelp();
        return;
      }
    }
    if (args.length != 2) {
      System.err.println("Incorrect number of arguments");
      printHelp();
      System.exit(1);
    }
    String versionName = args[0];
    System.out.format("ReleaseBuilder 1.2, building version %s%n", versionName);

    String releaseNotes = readReleaseNotes(args[1]);
    String changeLog = readChangeLog("ChangeLog.txt");
    if (!changeLog.isEmpty()) {
      if (!releaseNotes.isEmpty()) {
        releaseNotes += SYS_NL + SYS_NL;
      }
      releaseNotes += "Changes:" + SYS_NL + changeLog;
    }

    buildVersionInfo(versionName, releaseNotes);
  }

  private static String readReleaseNotes(String path) {
    try {
      File file = new File(path);
      Scanner in = new Scanner(new FileInputStream(file));
      StringBuilder sb = new StringBuilder();
      while (in.hasNextLine()) {
        sb.append(in.nextLine());
        sb.append(SYS_NL);
      }
      in.close();
      return sb.toString();
    } catch (IOException e) {
      System.err.println("WARNING: Failed to read release notes! " + e.getMessage());
      System.err.println("WARNING: Release notes will be empty!");
    }
    return "";
  }

  private static String readChangeLog(String path) {
    try {
      File file = new File(path);
      Scanner in = new Scanner(new FileInputStream(file));
      in.nextLine();
      StringBuilder sb = new StringBuilder();
      while (in.hasNextLine()) {
        String line = in.nextLine();
        if (line.isEmpty()) {
          break;
        }
        sb.append(line);
        sb.append(SYS_NL);
      }
      in.close();
      return sb.toString();
    } catch (IOException e) {
      System.err.println("WARNING: Failed to read ChangeLog! " + e.getMessage());
      System.err.println("WARNING: ChangeLog will be empty!");
    }
    return "";
  }

  private static void printHelp() {
    System.out.println("Usage: ReleaseBuilder <VERSION> <NOTES>");
    System.out.println("    VERSION    version name");
    System.out.println("    NOTES      release notes file");
  }

  private static void buildVersionInfo(String versionName, String notes) {
    try {
      // Write composed release notes to build dir.
      PrintWriter out = null;
      try {
        File targetFile = new File("build", "release_notes-" + versionName + ".txt");
        System.out.println("Writing file " + targetFile);
        out = new PrintWriter(targetFile);
        out.print(notes);
      } catch (IOException e) {
        System.err.println("Failed to write release notes (" + e.getMessage() + ")");
        System.exit(1);
        return;
      } finally {
        if (out != null) {
          out.close();
        }
      }

      File chunkyCore = new File("build/chunky-core-" + versionName + ".jar");
      File libDir = new File(LIBRARY_PATH);
      if (!libDir.isDirectory()) {
        System.err.println("Not a valid directory: " + LIBRARY_PATH);
      }
      JsonObject version = new JsonObject();
      version.add("name", versionName);
      version.add("timestamp", Util.ISO8601FromDate(new Date()));
      version.add("notes", notes);
      JsonArray libraries = new JsonArray();
      {
        libraries.add(libraryJson(chunkyCore));
      }
      for (File lib : libDir.listFiles(JAR_FILES)) {
        if (lib.isFile() && lib.getName().endsWith(".jar")) {
          libraries.add(libraryJson(lib));
        }
      }
      version.add("libraries", libraries);

      File latest = new File("latest.json");
      System.out.println("Writing file " + latest);
      try (PrettyPrinter pp =
          new PrettyPrinter("  ", new PrintStream(new FileOutputStream(latest)))) {
        version.prettyPrint(pp);
      }
    } catch (Exception e) {
      System.err.println("Failed to generate version info/release notes.");
      e.printStackTrace(System.err);
    }
  }

  private static JsonObject libraryJson(File lib) {
    JsonObject library = new JsonObject();
    library.add("name", lib.getName());
    library.add("md5", Util.md5sum(lib));
    library.add("size", (int) Math.min(Integer.MAX_VALUE, lib.length()));
    return library;
  }

}
