/* Copyright (c) 2013-2016 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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

import javafx.stage.Stage;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.commons.cli.*;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.ui.ChunkyLauncherFx;
import se.llbit.chunky.launcher.ui.DebugConsole;
import se.llbit.chunky.launcher.ui.FirstTimeSetupDialog;
import se.llbit.chunky.resources.SettingsDirectory;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Chunky launcher sets up the command to launch Chunky.
 * The launcher locates the Jar dependencies and builds the Java
 * command line for launching Chunky with the Jars on the classpath.
 *
 * <p>Various settings can be controlled
 * via the launcher, and it can show a debug console while running
 * Chunky.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class ChunkyLauncher {

  public static final ArtifactVersion LAUNCHER_VERSION = new DefaultArtifactVersion("1.14.0");

  public static final int LAUNCHER_SETTINGS_REVISION = 1;

  public static Options cliOptions() {
    Options options = new Options();

    options.addOption(Option.builder()
      .option("h")
      .longOpt("help")
      .desc("Show this help message")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("launcher")
      .desc("Forces the launcher GUI to be shown")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("version")
      .desc("Show the launcher version and exit")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("verbose")
      .desc("Enables verbose logging")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("console")
      .desc("Forces debug console to be opened")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("update")
      .argName("release channel")
      .optionalArg(true)
      .desc("Update Chunky to the latest release")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("setup")
      .desc("Runs the interactive command-line launcher setup")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("javaOptions")
      .hasArg(true)
      .argName("options")
      .desc("Add a Java option when launching Chunky")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("noRetryJavafx")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("checkJvm")
      .desc("Check if JVM version is 64-bit")
      .build()
    );

    options.addOption(Option.builder()
      .longOpt("dangerouslyDisableLibraryValidation")
      .desc("Disable library validation. This can be dangerous!")
      .build()
    );

    return options;
  }

  public static CommandLine parseCli(String[] args) throws ParseException {
    Options options = cliOptions();
    return new DefaultParser()
      .parse(options, args);
  }

  public static void main(String[] args) throws FileNotFoundException {
    boolean retryIfMissingJavafx = true;

    final LauncherSettings settings = new LauncherSettings();
    try {
      settings.load();

      // Currently, there's nothing that changed from previous launcher settings revisions.
      // In the future this can be queried to make any changes / show messages needed for a launcher version switch.
      settings.settingsRevision = LAUNCHER_SETTINGS_REVISION;

      /*
       * If there are command line arguments then we assume that Chunky should run
       * in headless mode, unless the --nolauncher command is present in which case
       * we strip that and start regularly, but without launcher. The --launcher
       * option overrides everything else and forces the launcher to appear.
       */

      boolean forceLauncher = false;
      LaunchMode mode = LaunchMode.GUI;
      String headlessOptions = "";

      if (args.length > 0) {
        mode = LaunchMode.HEADLESS;

        CommandLine cmd = parseCli(args);

        if (cmd.hasOption("help")) {
          new HelpFormatter()
            .printHelp("java -jar ChunkyLauncher.jar", cliOptions());
          return;
        }

        if (cmd.hasOption("launcher")) {
          forceLauncher = true;
        }

        if (cmd.hasOption("version")) {
          System.out.println("Chunky Launcher v" + LAUNCHER_VERSION);
          return;
        }

        if (cmd.hasOption("verbose")) {
          settings.verboseLauncher = true;
        }

        if (cmd.hasOption("console")) {
          settings.forceGuiConsole = true;
        }

        if (cmd.hasOption("update")) {
          ReleaseChannel channel = settings.selectedChannel;

          String selected = cmd.getOptionValue("update");
          if (selected != null) {
            channel = settings.releaseChannels.getOrDefault(selected, channel);
          }

          headlessUpdateChunky(settings, channel);
          return;
        }

        if (cmd.hasOption("setup")) {
          doSetup(settings);
          settings.save();
          return;
        }

        if (cmd.hasOption("noRetryJavafx")) {
          retryIfMissingJavafx = false;
          // if this is the only option, with "--javaOptions" "<param>" we want the launcher
          if (args.length == 3)
            forceLauncher = true;
        }

        if (cmd.hasOption("javaOptions")) {
          String options = cmd.getOptionValue("javaOptions");
          if (settings.javaOptions.isEmpty()) {
            settings.javaOptions = options;
          } else if (!settings.javaOptions.contains(options)) {
            settings.javaOptions = options + " " + settings.javaOptions;
          }
        }

        if (cmd.hasOption("checkJvm")) {
          boolean is64Bit = JreUtil.is64BitJvm();
          if (!is64Bit) {
            System.err.println("This does not appear to be a 64-bit JVM.");
          }
          System.exit(is64Bit ? 0 : -1);
        }

        if (cmd.hasOption("dangerouslyDisableLibraryValidation")) {
          System.out.println("Library validation is disabled.");
          LauncherSettings.disableLibraryValidation = true;
        }

        headlessOptions = String.join(" ", cmd.getArgList());

        if (forceLauncher) {
          mode = LaunchMode.GUI;
        }
      }

      ChunkyDeployer.LoggerBuilder loggerBuilder = () -> {
        if (settings.forceGuiConsole || (!settings.headless && settings.debugConsole)) {
          AtomicReference<DebugConsole> console = new AtomicReference<>(null);
          CountDownLatch latch = new CountDownLatch(1);
          ChunkyLauncherFx.withLauncher(settings, stage -> {
            DebugConsole debugConsole = new DebugConsole(settings.closeConsoleOnExit);
            debugConsole.show();
            console.set(debugConsole);
            latch.countDown();
          });
          try {
            latch.await();
          } catch (InterruptedException ignored) {
            // Ignored.
          }
          return console.get();
        } else {
          return new ConsoleLogger();
        }
      };

      if (mode == LaunchMode.HEADLESS) {
        // Chunky is being run from the console, i.e. headless mode.
        headlessCreateSettingsDirectory();
        settings.debugConsole = true;
        settings.headless = true;
        settings.chunkyOptions = headlessOptions;
        ChunkyDeployer.deploy(settings); // Install the embedded version.
        VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
        if (ChunkyDeployer.canLaunch(version, null, false)) {
          if (!settings.skipJvmCheck) {
            if (!ChunkyDeployer.is64BitJvm(settings)) {
              System.err.println("It seems like you're not using 64-bit Java. For best " +
                "performance and in order to allocate more than 3 GB of RAM to Chunky, you need a 64-bit JVM.");
            } else {
              settings.skipJvmCheck = true;
            }
          }
          int exitCode = ChunkyDeployer.launchChunky(settings, version, LaunchMode.HEADLESS,
            ChunkyLauncher::launchFailure, loggerBuilder);
          if (exitCode != 0) {
            System.exit(exitCode);
          }
        } else {
          System.err.println("Could not launch selected Chunky version. Try updating with --update");
          System.exit(1);
        }
      } else {
        final boolean finalForceLauncher = forceLauncher;
        // A callback is used to decide if the launcher should be displayed after
        // the first time setup dialog has been shown (if needed).
        firstTimeSetup(settings, () -> {
          ChunkyDeployer.deploy(settings); // Install the embedded version.

          if (!finalForceLauncher && !settings.showLauncher) {
            // Skip the launcher only if we can launch this version.
            VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
            if (ChunkyDeployer.canLaunch(version, null, false)) {
              if (ChunkyDeployer.launchChunky(settings, version, LaunchMode.GUI,
                ChunkyLauncher::launchFailure,
                loggerBuilder) == 0) {
                return false;
              }
            }
          }
          return true;
        });
      }
    } catch (NoClassDefFoundError e) {
      String cause = e.getMessage();
      if (cause != null && cause.contains("javafx")) {
        // Javafx error
        if (retryIfMissingJavafx)
          JavaFxLocator.retryWithJavafx(args);
        JavaFxInstaller.launch(settings, args);
      }
      e.printStackTrace(System.err);
    } catch (ParseException e) {
        System.out.println(e.getMessage());
    }
  }

  /**
   * Ensure that the settings directory exists.
   */
  private static void headlessCreateSettingsDirectory() throws FileNotFoundException {
    // We will try to set up a Chunky settings directory if one is not already configured.
    File directory = SettingsDirectory.getSettingsDirectory();
    if (directory == null || !directory.isDirectory()) {
      System.out.println("Chunky settings directory not found - trying to create one.");
      if (directory != null && !directory.mkdir()) {
        System.out.format("Failed to create directory: %s%n", directory.getAbsolutePath());
      }
      if (directory == null || !directory.isDirectory()) {
        directory = SettingsDirectory.getHomeDirectory();
        if (directory != null && !directory.mkdir()) {
          System.out.format("Failed to create directory: %s%n", directory.getAbsolutePath());
        }
      }
      if (directory != null && directory.isDirectory()) {
        System.out.format("Created settings directory: %s%n", directory.getAbsolutePath());
      }
    }
    if (directory != null) {
      // Initialize empty settings files.
      File settingsFile = new File(directory, PersistentSettings.SETTINGS_FILE);
      if (!settingsFile.exists()) {
        try (PrintStream out = new PrintStream(new FileOutputStream(settingsFile))) {
          // Create an empty settings file (default settings will be used).
          out.println("{}");
        }
      }
      settingsFile = new File(directory, LauncherSettings.LAUNCHER_SETTINGS_FILE);
      if (!settingsFile.exists()) {
        try (PrintStream out = new PrintStream(new FileOutputStream(settingsFile))) {
          // Create an empty settings file (default settings will be used).
          out.println("{}");
        }
      }
    }
  }

  private static void headlessUpdateChunky(LauncherSettings settings, ReleaseChannel channel) {
    System.out.println("Checking for updates on the \"" + channel.name + "\" channel...");
    UpdateChecker updateThread = new UpdateChecker(settings, channel, new UpdateListener() {
      @Override
      public void updateError(String message) {
      }

      @Override
      public void updateAvailable(VersionInfo latest) {
        try {
          headlessCreateSettingsDirectory();
        } catch (FileNotFoundException e) {
          throw new Error(e);
        }
        System.out.println("Downloading Chunky " + latest + ":");
        ConsoleUpdater.update(latest, settings);
      }

      @Override
      public void noUpdateAvailable() {
        System.out.println("No updates found.");
      }
    });
    updateThread.start();
    try {
      updateThread.join();
    } catch (InterruptedException ignored) {
    }
  }

  interface ShowLauncher {
    boolean showLauncher();
  }

  private static void doSetup(LauncherSettings settings) throws FileNotFoundException {
    headlessCreateSettingsDirectory();
    Scanner in = new Scanner(System.in);

    System.out.printf("Memory limit (MiB) [%d]: ", settings.memoryLimit);
    try {
      String memoryLimit = in.nextLine().trim();
      settings.memoryLimit = Integer.parseInt(memoryLimit);
    } catch (NumberFormatException ignored) {
    }

    System.out.printf("Java options [%s]: ", settings.javaOptions);
    {
      String javaOptions = in.nextLine().trim();
      if (!javaOptions.isEmpty()) {
        settings.javaOptions = javaOptions;
      }
    }

    System.out.print("Reload release channels [Y/n]: ");
    {
      String updateReleaseChannels = in.nextLine().trim();
      if (!updateReleaseChannels.contains("n")) {
        LauncherInfoChecker checker = new LauncherInfoChecker(
          settings,
          error -> {
            System.err.println("Failed to fetch launcher info!");
            System.err.println(error);
          },
          info -> {
            if (info != null) {
              if (info.version.compareTo(ChunkyLauncher.LAUNCHER_VERSION) > 0) {
                System.out.printf("Launcher update found! Version %s released on %s: %s\n",
                  info.version, info.date, settings.getResourceUrl(info.path));
                if (info.notes.isEmpty()) {
                  System.out.println("No release notes available.");
                } else {
                  System.out.println(info.notes);
                }
                System.out.println();
              }

              settings.setReleaseChannels(info.channels);
            }
          }
        );
        checker.start();
        try {
          checker.join();
        } catch (InterruptedException e) {
          System.err.println("Interrupted!");
        }
      }
    }

    System.out.println("Available channels:");
    System.out.println(String.join(", ", settings.releaseChannels.keySet()));
    System.out.printf("Release channel [%s]: ", settings.selectedChannel.id);
    {
      String releaseChannel = in.nextLine().trim();
      settings.selectedChannel = settings.releaseChannels.getOrDefault(releaseChannel, settings.selectedChannel);
    }
  }

  /**
   * Shows the first-time setup dialog if needed and then calls the afterFirstTimeSetup
   * callback. Ensures that the JavaFX application (ChunkyLauncherFx) is initialized
   * before calling afterFirstTimeSetup.
   */
  private static void firstTimeSetup(LauncherSettings settings, ShowLauncher afterFirstTimeSetup) throws FileNotFoundException {
    if (SettingsDirectory.findSettingsDirectory()) {
      if (afterFirstTimeSetup.showLauncher()) {
        ChunkyLauncherFx.withLauncher(settings, Stage::show);
      }
    } else if (SettingsDirectory.getChunkyHomeDirectoryOverwrite().isPresent()) {
      headlessCreateSettingsDirectory();
      if (afterFirstTimeSetup.showLauncher()) {
        ChunkyLauncherFx.withLauncher(settings, Stage::show);
      }
    } else {
      ChunkyLauncherFx.withLauncher(settings, stage -> {
        FirstTimeSetupDialog picker = new FirstTimeSetupDialog(() -> {
          if (afterFirstTimeSetup.showLauncher()) {
            stage.show();
          }
        });
        picker.show();
      });
    }
  }

  public static DownloadStatus tryDownload(File libDir, VersionInfo.Library lib, String theUrl) {
    try {
      URL url = new URL(theUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      int responseCode = conn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
        responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
        responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
        return tryDownload(libDir, lib, conn.getHeaderField("Location"));
      }
      try (
        ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
        FileOutputStream out = new FileOutputStream(lib.getFile(libDir))
      ) {
        out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
      }
      VersionInfo.LibraryStatus status = lib.testIntegrity(libDir);
      if (status == VersionInfo.LibraryStatus.PASSED) {
        return DownloadStatus.SUCCESS;
      } else {
        return DownloadStatus.DOWNLOAD_FAILED;
      }
    } catch (MalformedURLException e) {
      return DownloadStatus.MALFORMED_URL;
    } catch (FileNotFoundException e) {
      return DownloadStatus.FILE_NOT_FOUND;
    } catch (IOException e) {
      return DownloadStatus.DOWNLOAD_FAILED;
    }
  }

  public static String prettyPrintSize(int size) {
    // Pretty print library size.
    float fSize = size;
    String unit = "B";
    if (size >= 1024 * 1024) {
      fSize /= 1024 * 1024;
      unit = "MiB";
    } else if (size >= 1024) {
      fSize /= 1024;
      unit = "KiB";
    }
    if (fSize >= 10) {
      return String.format("%d %s", (int) fSize, unit);
    } else {
      return String.format("%.1f %s", fSize, unit);
    }
  }

  /**
   * Print a launch error message to the console.
   * Prints the command that was used to try to launch Chunky.
   */
  protected static void launchFailure(String command) {
    System.out.println("Failed to launch Chunky. Command used:");
    System.out.println(command);
  }
}
