package se.llbit.chunky.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaFxLocator {


  private static final List<String> javafxPathCandidates;

  static {
    javafxPathCandidates = new ArrayList<>();
    javafxPathCandidates.add("C:\\Program Files\\openjfx\\lib");
    javafxPathCandidates.add("/usr/share/openjfx/lib");
    javafxPathCandidates.add("/usr/lib/jvm/java-11-openjdk/lib");
    javafxPathCandidates.add("/usr/lib/jvm/java-12-openjdk/lib");
    javafxPathCandidates.add("/usr/lib/jvm/java-13-openjdk/lib");
    javafxPathCandidates.add("/usr/lib/jvm/java-14-openjdk/lib");
    javafxPathCandidates.add("/usr/lib/jvm/java-15-openjdk/lib");

    javafxPathCandidates.add(System.getProperty("java.home", "") + File.separator + "lib"); // java home
    javafxPathCandidates.add(System.getProperty("user.dir", "") + File.separator + "lib"); // working directory
    try {
      javafxPathCandidates.add(new File(ChunkyLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI())
              .toPath().getParent().toAbsolutePath().toString() + File.separator + "lib"); // directory of the .jar
    } catch(URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private static boolean validJavafxDirectory(Path dir) {
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
    cmd.add(javafxDir.toAbsolutePath().toString());
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
    if (System.getProperty("os.name").startsWith("Windows")) {
      // Escape the path twice to make the second launcher pass the options to Chunky retaining the double speechmarks (fixes paths with spaces)
      javaOptions.append("\\\"" + javafxDir.toAbsolutePath().toString() + "\\\"");
    } else {
      javaOptions.append(javafxDir.toAbsolutePath().toString());
    }
    javaOptions.append(" --add-modules ");
    javaOptions.append("javafx.controls,javafx.fxml");
    cmd.add(javaOptions.toString());

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
    for(String candiate : javafxPathCandidates) {
      Path directory = new File(candiate).toPath();
      if(validJavafxDirectory(directory)) {
        runWithJavafx(directory, args);
      }
    }
  }

}
