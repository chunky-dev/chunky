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
package se.llbit.chunky.launcher;

import se.llbit.chunky.launcher.VersionInfo.Library;
import se.llbit.chunky.launcher.VersionInfo.LibraryStatus;
import se.llbit.chunky.launcher.ui.ChunkyLauncherController;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.log.Level;
import se.llbit.log.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is responsible for launching Chunky after the
 * launcher has constructed the command line. The deployer also
 * tracks installed Chunky versions, and deploys the embedded version.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class ChunkyDeployer {

  public interface LoggerBuilder {
    Logger build();
  }

  private ChunkyDeployer() {
  }

  /**
   * Check the integrity of an installed version.
   *
   * @return <code>true</code> if the version is installed locally
   */
  public static boolean checkVersionIntegrity(String version) {
    File chunkyDir = SettingsDirectory.getSettingsDirectory();
    if (chunkyDir == null) {
      return false;
    }

    File versionsDir = new File(chunkyDir, "versions");
    File libDir = new File(chunkyDir, "lib");
    if (!versionsDir.isDirectory() || !libDir.isDirectory()) {
      return false;
    }

    File versionFile = new File(versionsDir, version + ".json");
    if (!versionFile.isFile()) {
      return false;
    }

    // Check version.
    try {
      FileInputStream in = new FileInputStream(versionFile);
      JsonParser parser = new JsonParser(in);
      JsonObject obj = parser.parse().object();
      in.close();
      String versionName = obj.get("name").stringValue("");
      if (!versionName.equals(version)) {
        System.err.println("Stored version name does not match file name");
        return false;
      }
      JsonArray array = obj.get("libraries").array();
      for (JsonValue value : array.getElementList()) {
        VersionInfo.Library lib = new VersionInfo.Library(value.object());
        switch (lib.testIntegrity(libDir)) {
          case INCOMPLETE_INFO:
            System.err.println("Missing library name or checksum");
            return false;
          case MD5_MISMATCH:
            System.err.println("Library MD5 checksum mismatch");
            return false;
          case MISSING:
            System.err.println("Missing library " + lib.name);
            return false;
          default:
            break;
        }
      }
      return true;
    } catch (IOException e) {
      System.err.println("Could not read version info file: " + e.getMessage());
    } catch (SyntaxError e) {
      System.err.println("Corrupted version info file: " + e.getMessage());
    }
    return false;
  }

  /**
   * Unpacks the embedded Chunky jar files.
   * <p>
   * <p>Updates the settings to use the latest version if a new embedded version is installed.
   */
  public static void deploy(LauncherSettings settings) {
    List<VersionInfo> versions = availableVersions();
    VersionInfo embedded = embeddedVersion();
    if (embedded != null && (!versions.contains(embedded) || !checkVersionIntegrity(
        embedded.name))) {
      Log.infof("Deploying embedded version: %s", embedded.name);
      deployEmbeddedVersion(embedded);
      if (!settings.version.equals(VersionInfo.LATEST.name)) {
        settings.version = VersionInfo.LATEST.name;
        settings.save();
      }
    }
  }

  /**
   * @return a list of available Chunky versions sorted by release date.
   */
  public static List<VersionInfo> availableVersions() {
    File chunkyDir = SettingsDirectory.getSettingsDirectory();
    if (chunkyDir == null) {
      return Collections.emptyList();
    }

    File versionsDir = new File(chunkyDir, "versions");
    if (!versionsDir.isDirectory()) {
      return Collections.emptyList();
    }

    File[] versionFiles = versionsDir.listFiles();
    if (versionFiles == null) {
      return Collections.emptyList();
    }
    List<VersionInfo> versions = new ArrayList<>();

    for (File versionFile : versionFiles) {
      if (versionFile.getName().endsWith(".json")) {
        try {
          FileInputStream in = new FileInputStream(versionFile);
          JsonParser parser = new JsonParser(in);
          versions.add(new VersionInfo(parser.parse().object()));
          in.close();
        } catch (IOException e) {
          System.err.println("Could not read version info file: " + e.getMessage());
        } catch (SyntaxError e) {
          System.err.println("Corrupted version info file: " + e.getMessage());
        }
      }
    }

    Collections.sort(versions);
    return versions;
  }

  /**
   * Unpack embedded libraries and deploy the embedded Chunky version.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  private static void deployEmbeddedVersion(VersionInfo version) {
    File chunkyDir = SettingsDirectory.getSettingsDirectory();
    if (chunkyDir == null) {
      return;
    }

    File versionsDir = new File(chunkyDir, "versions");
    if (!versionsDir.isDirectory()) {
      versionsDir.mkdirs();
    }
    File libDir = new File(chunkyDir, "lib");
    if (!libDir.isDirectory()) {
      libDir.mkdirs();
    }
    try {
      File versionJson = new File(versionsDir, version.name + ".json");
      version.writeTo(versionJson);

      ClassLoader parentCL = ChunkyDeployer.class.getClassLoader();

      // Deploy libraries that were not already installed correctly.
      for (Library lib : version.libraries) {
        if (lib.testIntegrity(libDir) != LibraryStatus.PASSED) {
          unpackLibrary(parentCL, "lib/" + lib.name, new File(libDir, lib.name));
        }
      }
    } catch (SecurityException | IllegalArgumentException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Unpack the jar file to the target directory.
   *
   * @param dest     destination file
   * @throws IOException
   */
  private static void unpackLibrary(ClassLoader parentCL, String name, File dest)
      throws IOException {

    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    InputStream in = parentCL.getResourceAsStream(name);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = in.read(buffer)) != -1) {
      out.write(buffer, 0, len);
    }
    out.close();
  }

  /** Gets the version info descriptor for the Chunky version embedded in this Jar. */
  private static VersionInfo embeddedVersion() {
    try {
      ClassLoader parentCL = ChunkyDeployer.class.getClassLoader();
      try (InputStream in = parentCL.getResourceAsStream("version.json")) {
        if (in != null) {
          JsonParser parser = new JsonParser(in);
          return new VersionInfo(parser.parse().object());
        }
      } catch (IOException | SyntaxError ignored) {
        // Ignored.
      }
    } catch (SecurityException ignored) {
      // Ignored.
    }
    return null;
  }

  /**
   * Launch a specific Chunky version.
   *
   * @return zero on success, non-zero if there is any problem
   * launching Chunky (waits 200ms to see if everything launched)
   */
  public static int launchChunky(LauncherSettings settings, VersionInfo version,
      LaunchMode mode, Consumer<String> failureHandler, LoggerBuilder loggerBuilder) {
    List<String> command = buildCommandLine(version, settings);
    if (settings.verboseLauncher || Log.level == Level.INFO) {
      System.out.println(commandString(command));
    }
    int exitValue = launchChunky(mode, command, loggerBuilder);
    if (exitValue != 0) {
      failureHandler.accept(commandString(command));
    }
    return exitValue;
  }

  public static int launchChunky(LaunchMode mode, List<String> command,
      LoggerBuilder loggerBuilder) {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    final Logger logger = loggerBuilder.build();
    try {
      final Process process = processBuilder.start();
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override public void run() {
          // Kill the subprocess.
          process.destroy();
        }
      });
      final Thread outputScanner = new Thread("Output Logger") {
        @Override public void run() {
          try (InputStream is = process.getInputStream()) {
            byte[] buffer = new byte[4096];
            while (true) {
              int size = is.read(buffer, 0, buffer.length);
              if (size == -1) {
                break;
              }
              logger.appendStdout(buffer, size);
            }
          } catch (IOException ignored) {
          }
        }
      };
      outputScanner.start();
      final Thread errorScanner = new Thread("Error Logger") {
        @Override public void run() {
          try (InputStream is = process.getErrorStream()) {
            byte[] buffer = new byte[4096];
            while (true) {
              int size = is.read(buffer, 0, buffer.length);
              if (size == -1) {
                break;
              }
              logger.appendStderr(buffer, size);
            }
          } catch (IOException ignored) {
          }
        }
      };
      errorScanner.start();
      ShutdownThread shutdownThread = new ShutdownThread(process, logger, outputScanner, errorScanner);
      shutdownThread.start();
      try {
        if (mode == LaunchMode.GUI) {
          // Just wait a little while to check for startup errors.
          Thread.sleep(3000);
          return shutdownThread.exitValue;
        } else {
          // Wait until completion so we can return correct exit code.
          return shutdownThread.exitValue();
        }
      } catch (InterruptedException ignored) {
        // Ignored.
      }
      return 0;
    } catch (IOException e) {
      logger.appendErrorLine(e.getMessage());
      // TODO(jesper): Add constant for this return value.
      // Exit code 3 indicates launcher error.
      return 3;
    }
  }

  /**
   * Convert a command in list form to string.
   *
   * @return command in string form
   */
  public static String commandString(List<String> command) {
    StringBuilder sb = new StringBuilder();
    for (String part : command) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(part);
    }
    return sb.toString();
  }

  private static List<String> buildCommandLine(VersionInfo version, LauncherSettings settings) {
    List<String> cmd = new LinkedList<>();

    cmd.add(JreUtil.javaCommand(settings.javaDir));
    cmd.add("-Xmx" + settings.memoryLimit + "m");

    File settingsDirectory = SettingsDirectory.getSettingsDirectory();
    if (settingsDirectory != null) {
      cmd.add("-Dchunky.home=" + settingsDirectory.getAbsolutePath());
    }

    String[] parts = settings.javaOptions.split(" ");
    for (String part : parts) {
      if (!part.isEmpty()) {
        cmd.add(part);
      }
    }

    cmd.add("-classpath");
    cmd.add(classpath(version));

    if (settings.verboseLogging) {
      cmd.add("-DlogLevel=INFO");
    }

    cmd.add("se.llbit.chunky.main.Chunky");

    parts = settings.chunkyOptions.split(" ");
    for (String part : parts) {
      if (!part.isEmpty()) {
        cmd.add(part);
      }
    }

    return cmd;
  }

  private static String classpath(VersionInfo version) {
    File chunkyDir = SettingsDirectory.getSettingsDirectory();
    File libDir = new File(chunkyDir, "lib");
    List<File> jars = version.libraries.stream()
        .map(library -> library.getFile(libDir))
        .collect(Collectors.toList());
    String classpath = "";
    for (File file : jars) {
      if (!classpath.isEmpty()) {
        classpath += File.pathSeparator;
      }
      classpath += file.getAbsolutePath();
    }
    return classpath;
  }

  private static class ShutdownThread extends Thread {
    public volatile int exitValue = 0;
    private final Thread outputScanner;
    private final Thread errorScanner;
    private final Process proc;
    private final Logger logger;
    private boolean finished = false;

    public ShutdownThread(Process proc, Logger logger, Thread output, Thread error) {
      this.proc = proc;
      this.logger = logger;
      this.outputScanner = output;
      this.errorScanner = error;
    }

    public synchronized int exitValue() throws InterruptedException {
      while (!finished) {
        wait();
      }
      return exitValue;
    }

    @Override public void run() {
      try {
        outputScanner.join();
      } catch (InterruptedException ignored) {
      }
      try {
        errorScanner.join();
      } catch (InterruptedException ignored) {
      }
      try {
        proc.waitFor();
        exitValue = proc.exitValue();
        logger.processExited(exitValue);
      } catch (InterruptedException ignored) {
      }
      synchronized (this) {
        finished = true;
        notifyAll();
      }
    }
  }

  public static VersionInfo resolveVersion(String name) {
    List<VersionInfo> versions = availableVersions();
    VersionInfo version = VersionInfo.LATEST;
    for (VersionInfo info : versions) {
      if (info.name.equals(name)) {
        version = info;
        break;
      }
    }
    if (version == VersionInfo.LATEST) {
      if (versions.size() > 0) {
        return versions.get(0);
      } else {
        return VersionInfo.NONE;
      }
    } else {
      return version;
    }
  }

  public static boolean canLaunch(VersionInfo version, ChunkyLauncherController launcher,
      boolean reportErrors) {
    if (version == VersionInfo.NONE) {
      // Version not available!
      System.err.println("No version installed");
      if (reportErrors) {
        launcher.launcherError("No Chunky Available",
            "There is no local Chunky version installed. Please try updating.");
      }
      return false;
    }
    if (!ChunkyDeployer.checkVersionIntegrity(version.name)) {
      // TODO: add a way to fix this (delete corrupt version and then update)!
      System.err.println("Version integrity check failed for version " + version.name);
      if (reportErrors) {
        launcher.launcherError("Chunky Version is Corrupt",
            "Version integrity check failed for version "
            + version.name + ". Please select another version.");
      }
      return false;
    }
    return true;
  }
}
