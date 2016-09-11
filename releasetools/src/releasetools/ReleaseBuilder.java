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
package releasetools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.jastadd.util.PrettyPrinter;

import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.util.Util;

public class ReleaseBuilder {
  static final String LIBRARY_PATH = "chunky/lib";
  static final String LAUNCHER_BIN = "launcher/build/classes/main";
  static final String LIB_BIN = "lib/build/classes/main";
  private final String versionName;
  private final String notes;
  private static final String SYS_NL = System.getProperty("line.separator");

  private final Set<String> jarDirs = new HashSet<>();

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

    String releaseNotes = readReleaseNotes(args[1]);
    String changeLog = readChangeLog("ChangeLog.txt");
    if (!changeLog.isEmpty()) {
      if (!releaseNotes.isEmpty()) {
        releaseNotes += SYS_NL + SYS_NL;
      }
      releaseNotes += "Changes:" + SYS_NL + changeLog;
    }

    // write composed release notes to build dir
    PrintWriter out = null;
    try {
      out = new PrintWriter(new File("build", "release_notes-" + versionName + ".txt"));
      out.print(releaseNotes);
    } catch (IOException e) {
      System.err.println("Failed to write release notes (" + e.getMessage() + ")");
      System.exit(1);
      return;
    } finally {
      if (out != null) {
        out.close();
      }
    }

    new ReleaseBuilder(versionName, releaseNotes).buildChunkJar();
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
    System.out.println("Usage: ReleaseBuilder <version name> <release notes file>");
  }

  public ReleaseBuilder(String versionName, String releaseNotes) {
    this.versionName = versionName;
    this.notes = releaseNotes;
  }

  private void buildChunkJar() {
    buildChunkyJar("build/chunky-" + versionName + ".jar");
  }

  private void buildChunkyJar(String targetFile) {
    Manifest mf = new Manifest();
    mf.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    mf.getMainAttributes()
        .put(Attributes.Name.MAIN_CLASS, "se.llbit.chunky.launcher.ChunkyLauncher");
    try {
      JarOutputStream out = new JarOutputStream(new FileOutputStream(targetFile), mf);
      addClassDir(out, new File(LAUNCHER_BIN));
      addClassDir(out, new File(LIB_BIN));
      addToJar(out, new File("chunky/lib"));
      File chunkyCore = new File("build/chunky-core-" + versionName + ".jar");
      addToJar(out, chunkyCore, "lib");
      addVersionInfoJson(out, chunkyCore);
      out.close();
    } catch (IOException e) {
      System.err.println("Jar file writing error occurred!");
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

  private void addVersionInfoJson(JarOutputStream jar, File chunkyCore) throws IOException {
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
    for (File lib : libDir.listFiles()) {
      if (lib.getName().endsWith(".jar")) {
        libraries.add(libraryJson(lib));
      }
    }
    version.add("libraries", libraries);
    JarEntry entry = new JarEntry("version.json");
    entry.setTime(System.currentTimeMillis());
    jar.putNextEntry(entry);
    PrintStream out = new PrintStream(jar);
    PrettyPrinter pp = new PrettyPrinter("  ", out);
    version.prettyPrint(pp);
    out.flush();
    jar.closeEntry();

    File latest = new File("latest.json");
    out = new PrintStream(new FileOutputStream(latest));
    version.prettyPrint(new PrettyPrinter("  ", out));
    out.close();
  }

  private void addClassDir(JarOutputStream jar, File dir) throws IOException {
    for (File binPkg : dir.listFiles()) {
      addToJar(jar, binPkg);
    }
  }

  private void addToJar(JarOutputStream jar, File file) throws IOException {
    addToJar(jar, file, "");
  }

  private void addToJar(JarOutputStream jar, File file, String prefix) throws IOException {
    String name = file.getName();
    String jarPath = prefix.isEmpty() ? name : prefix + '/' + name;
    if (file.isDirectory()) {
      createJarDir(jar, jarPath, file);
      for (File nestedFile : file.listFiles()) {
        addToJar(jar, nestedFile, jarPath);
      }
    } else {
      JarEntry entry = new JarEntry(jarPath);
      entry.setTime(file.lastModified());
      jar.putNextEntry(entry);
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

      byte[] buff = new byte[4096];
      while (true) {
        int len = in.read(buff);
        if (len == -1) {
          break;
        }
        jar.write(buff, 0, len);
      }
      jar.closeEntry();
      in.close();
    }
  }

  private void createJarDir(JarOutputStream jar, String jarPath, File file) throws IOException {
    String jarEntry = jarPath + '/';
    if (!jarPath.isEmpty() && !jarDirs.contains(jarEntry)) {
      jarDirs.add(jarEntry);
      JarEntry entry = new JarEntry(jarEntry);
      entry.setTime(file.lastModified());
      jar.putNextEntry(entry);
      jar.closeEntry();
    }
  }
}
