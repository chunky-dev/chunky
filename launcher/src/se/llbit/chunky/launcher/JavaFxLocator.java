/* Copyright (c) 2021 Chunky contributors
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
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class JavaFxLocator {

  /**
   * Lazy set of absolute paths to JavaFX library folders.
   * Filled using {@link #scanForJavaFXLibs()}.
   */
  private static final LinkedHashSet<Path> javafxPathCandidates = new LinkedHashSet<>();

  private static void scanForJavaFXLibs() {
    // working directory (local lib overwrites installed system libs)
    addJavaFXPathIfValid(getJavaFXPathBySystemProperty("user.dir"));
    try {
      // directory of the .jar
      File executableFile = new File(
              ChunkyLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      addJavaFXPathIfValid(executableFile.toPath().getParent());
    } catch(URISyntaxException ignored) {
    }

    // home directory installation
    Path userHomePath = getJavaFXPathBySystemProperty("user.home");
    if(userHomePath != null) {
      addJavaFXPathIfValid(userHomePath.resolve(".chunky"), true);
      addJavaFXPathIfValid(userHomePath.resolve(".chunky").resolve("javafx"), true);
    }

    // java home
    Path javaHomePath = getJavaFXPathBySystemProperty("java.home");
    addJavaFXPathIfValid(javaHomePath);

    if(System.getProperty("os.name").startsWith("Windows")) {
      // windows paths
      if(javaHomePath != null &&
              (javaHomePath.endsWith("jre") || javaHomePath.endsWith("jdk"))) {
        // if jre is in a subfolder of the jdk, try jdks lib path (example: OpenJDK from odjkbuild)
        addJavaFXPathIfValid(javaHomePath.getParent(), true);
      }
      addJavaFXPathIfValid("C:\\Program Files\\openjfx");
    } else {
      // linux paths
      addJavaFXPathIfValid("/usr/share/openjfx");
      for(int javaVersion = 11; javaVersion <= 17; javaVersion++) {
        addJavaFXPathIfValid("/usr/lib/jvm/java-" + javaVersion + "-openjdk");
      }
    }
  }

  private static Path getJavaFXPathBySystemProperty(String propertyName) {
    try {
      String propertyValue = System.getProperty(propertyName);
      if(propertyValue != null) {
        return Paths.get(propertyValue);
      }
    } catch(SecurityException ignored) {
    }
    return null;
  }


  private static void addJavaFXPathIfValid(String path) {
    addJavaFXPathIfValid(Paths.get(path), false);
  }

  private static void addJavaFXPathIfValid(Path path) {
    addJavaFXPathIfValid(path, false);
  }

  /**
   * Validates the path to contain the required JavaFX library files.
   * Retests the path with lib appended, if the initial test does not find the files.
   *
   * @param path path to library files, gets converted to absolute while following links
   * @return true if path contains required files
   */
  private static boolean addJavaFXPathIfValid(Path path, boolean searchJavaFXSDK) {
    try {
      // get absolute path, follow links
      path = path.toRealPath();

      if(isValidJavaFXDirectory(path)) {
        javafxPathCandidates.add(path);
        return true;
      }
      if(!path.endsWith("lib")) {
        if(addJavaFXPathIfValid(path.resolve("lib"), searchJavaFXSDK)) {
          return true;
        }
      }
      if(searchJavaFXSDK) {
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, subPath -> subPath.toFile().isDirectory())) {
          for(Path subPath : directoryStream) {
            if(subPath.getFileName().toString().contains("javafx")) {
              addJavaFXPathIfValid(subPath);
            }
          }
        }
      }
    } catch(IOException ignored) {
    }
    return false;
  }

  private static boolean isValidJavaFXDirectory(Path dir) {
    return dir.toFile().exists()
            && dir.resolve("javafx.base.jar").toFile().exists()
            && dir.resolve("javafx.controls.jar").toFile().exists()
            && dir.resolve("javafx.graphics.jar").toFile().exists()
            && dir.resolve("javafx.fxml.jar").toFile().exists();
  }

  private static void runWithJavafx(Path javafxDir, String[] args) {
    // https://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
    ArrayList<String> cmd = new ArrayList<>();
    cmd.add(JreUtil.javaCommand(""));
    cmd.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
    cmd.add("--module-path");
    cmd.add(javafxDir.toString());
    cmd.add("--add-modules");
    cmd.add("javafx.controls,javafx.fxml");

    cmd.add("-cp");
    cmd.add(ManagementFactory.getRuntimeMXBean().getClassPath());
    cmd.add(ChunkyLauncher.class.getName());
    cmd.addAll(Arrays.asList(args));
    cmd.add("--noRetryJavafx"); // Make sure this doesn't end up as a fork bomb*
    // add the options again so the launcher can use them for chunky
    cmd.add("--javaOptions");
    StringBuilder javaOptions = new StringBuilder();
    javaOptions.append("--module-path ");
    if(System.getProperty("os.name").startsWith("Windows")) {
      // Escape the path twice to make the second launcher pass the options to Chunky retaining the double quotation marks (fixes paths with spaces)
      javaOptions.append("\\\"").append(javafxDir.toAbsolutePath()).append("\\\"");
    } else {
      javaOptions.append(javafxDir);
    }
    javaOptions.append(" --add-modules ");
    javaOptions.append("javafx.controls,javafx.fxml");
    cmd.add(javaOptions.toString());

    System.out.println("Trying to start the chunky process with the following arguments:");
    System.out.println(String.join(" ", cmd));

    ProcessBuilder builder = new ProcessBuilder(cmd);
    builder.inheritIO();
    try {
      Process process = builder.start();
      System.exit(process.waitFor());
    } catch(IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void retryWithJavafx(String[] args) {
    if(javafxPathCandidates.isEmpty()) {
      scanForJavaFXLibs();
      System.out.println("JavaFX scan found the following candidates:");
      javafxPathCandidates.forEach(System.out::println);
    }
    for(Path pathCandiate : javafxPathCandidates) {
      runWithJavafx(pathCandiate, args);
    }
  }

}
