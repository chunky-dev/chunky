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

import com.vdurmont.semver4j.Semver;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.ui.ChunkyLauncherFx;
import se.llbit.chunky.launcher.ui.DebugConsole;
import se.llbit.chunky.launcher.ui.FirstTimeSetupDialog;
import se.llbit.chunky.resources.SettingsDirectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Map;
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

  public static final Semver LAUNCHER_VERSION = new Semver("1.13.0");
  public static final int LAUNCHER_SETTINGS_REVISION = 1;

  /**
   * Print a launch error message to the console.
   * Prints the command that was used to try to launch Chunky.
   */
  protected static void launchFailure(String command) {
    System.out.println("Failed to launch Chunky. Command used:");
    System.out.println(command);
  }

  public static void main(String[] args) throws FileNotFoundException {
    boolean retryIfMissingJavafx = true;

    try {
      final LauncherSettings settings = new LauncherSettings();
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

      if(args.length > 0) {
        mode = LaunchMode.HEADLESS;
        for(int i = 0; i < args.length; i++) {
          String arg = args[i];
          switch(arg) {
            case "--nolauncher":
              mode = LaunchMode.GUI;
              break;
            case "--launcher":
              forceLauncher = true;
              break;
            case "--version":
              System.out.println("Chunky Launcher v" + LAUNCHER_VERSION);
              return;
            case "--verbose":
              settings.verboseLauncher = true;
              break;
            case "--console":
              settings.forceGuiConsole = true;
              break;
            case "--update":
            case "--updateAlpha":
              ReleaseChannel channel;
              if(arg.equals("--updateAlpha")) {
                channel = LauncherSettings.SNAPSHOT_RELEASE_CHANNEL;
              } else {
                channel = settings.selectedChannel;
                if (i < args.length - 1 && settings.releaseChannels.containsKey(args[i + 1])) {
                  channel = settings.releaseChannels.getOrDefault(args[i + 1], channel);
                }
              }
              System.out.println("Checking for updates on the \"" + channel.name + "\" channel...");
              UpdateChecker updateThread = new UpdateChecker(settings, channel, new UpdateListener() {
                @Override
                public void updateError(String message) {
                }

                @Override
                public void updateAvailable(VersionInfo latest) {
                  try {
                    headlessCreateSettingsDirectory();
                  } catch(FileNotFoundException e) {
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
              return;
            case "--setup":
              // Configure launcher settings.
              doSetup(settings);
              settings.save();
              return;
            case "--noRetryJavafx":
              retryIfMissingJavafx = false;
              // if this is the only option, with "--javaOptions" "<param>" we want the launcher
              if(args.length == 3)
                forceLauncher = true;
              break;
            case "--javaOptions":
              if(i == args.length-1) {
                System.err.println("--javaOptions must be followed by the options to can chunky with");
                System.exit(1);
              }
              if(settings.javaOptions.isEmpty())
                settings.javaOptions = args[i+1];
              else if(!settings.javaOptions.contains(args[i+1]))
                settings.javaOptions = args[i + 1] + " " + settings.javaOptions;
              ++i;
              break;
            default:
              if(!headlessOptions.isEmpty()) {
                headlessOptions += " ";
              }
              headlessOptions += arg;
              break;
          }
        }
        if(forceLauncher) {
          mode = LaunchMode.GUI;
        }
      }

      ChunkyDeployer.LoggerBuilder loggerBuilder = () -> {
        if(settings.forceGuiConsole || (!settings.headless && settings.debugConsole)) {
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
          } catch(InterruptedException ignored) {
            // Ignored.
          }
          return console.get();
        } else {
          return new ConsoleLogger();
        }
      };

      if(mode == LaunchMode.HEADLESS) {
        // Chunky is being run from the console, i.e. headless mode.
        headlessCreateSettingsDirectory();
        settings.debugConsole = true;
        settings.headless = true;
        settings.chunkyOptions = headlessOptions;
        ChunkyDeployer.deploy(settings); // Install the embedded version.
        VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
        if(ChunkyDeployer.canLaunch(version, null, false)) {
          int exitCode = ChunkyDeployer.launchChunky(settings, version, LaunchMode.HEADLESS,
                  ChunkyLauncher::launchFailure, loggerBuilder);
          if(exitCode != 0) {
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

          if(!finalForceLauncher && !settings.showLauncher) {
            // Skip the launcher only if we can launch this version.
            VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
            if(ChunkyDeployer.canLaunch(version, null, false)) {
              if(ChunkyDeployer.launchChunky(settings, version, LaunchMode.GUI,
                      ChunkyLauncher::launchFailure,
                      loggerBuilder) == 0) {
                return false;
              }
            }
          }
          return true;
        });
      }
    } catch(NoClassDefFoundError e) {
      String cause = e.getMessage();
      if(cause != null && cause.contains("javafx")) {
        // Javafx error
        if(retryIfMissingJavafx)
          JavaFxLocator.retryWithJavafx(args);
        showJavafxError();
      }
      e.printStackTrace(System.err);
    }
  }

  /** Ensure that the settings directory exists. */
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

  interface ShowLauncher {
    boolean showLauncher();
  }

  private static void doSetup(LauncherSettings settings) throws FileNotFoundException {
    headlessCreateSettingsDirectory();
    System.out.print("Memory limit (MiB): ");
    Scanner in = new Scanner(System.in);
    settings.memoryLimit = in.nextInt();
    in.nextLine();
    System.out.print("Java options: ");
    settings.javaOptions = in.nextLine();
  }

  /**
   * Shows the first-time setup dialog if needed and then calls the afterFirstTimeSetup
   * callback. Ensures that the JavaFX application (ChunkyLauncherFx) is initialized
   * before calling afterFirstTimeSetup.
   */
  private static void firstTimeSetup(LauncherSettings settings, ShowLauncher afterFirstTimeSetup) {
    if (SettingsDirectory.findSettingsDirectory()) {
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

  private static void showJavafxError() {
    String[] errorMessages = new String[]{
            "Error: Java cannot find JavaFX.",
            "If you are using a JVM for Java 11 or later, " +
                    "JavaFX is no longer shipped alongside and must be installed separately.",
            "If you already have JavaFX installed, you need to run Chunky with the command:",
            "java --module-path <path/to/JavaFX/lib> --add-modules javafx.controls,javafx.fxml -jar <path/to/ChunkyLauncher.jar>"
    };
    String faqLink = "https://chunky.lemaik.de/java11";
    String faqMessage = "Check out this page for more information on how to use Chunky with JavaFX";
    if(!GraphicsEnvironment.isHeadless()) {
      JTextField faqLabel;
      if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        faqLabel = new JTextField(faqMessage);
        Font font = faqLabel.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        faqLabel.setFont(font.deriveFont(attributes));
        faqLabel.setForeground(Color.BLUE.darker());
        faqLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        faqLabel.setEditable(false);
        faqLabel.setBackground(null);
        faqLabel.setBorder(null);
        faqLabel.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            try {
              Desktop.getDesktop().browse(new URI(faqLink));
            } catch(IOException | URISyntaxException ioException) {
              ioException.printStackTrace();
            }
          }
        });
      } else {
        faqLabel = new JTextField(String.format("%s: %s", faqMessage, faqLink));
        faqLabel.setEditable(false);
        faqLabel.setBackground(null);
        faqLabel.setBorder(null);
        faqLabel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
      }
      Object[] dialogContent = {
              Arrays.stream(errorMessages).map(msg -> {
                JTextField field = new JTextField(msg);
                field.setEditable(false);
                field.setBackground(null);
                field.setBorder(null);
                field.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                return field;
              }).toArray(),
              faqLabel
      };
      JOptionPane.showMessageDialog(null, dialogContent, "Cannot find JavaFX", JOptionPane.ERROR_MESSAGE);
    }
    for(String message : errorMessages) {
      System.err.println(message);
    }
    System.err.printf("%s: %s\n", faqMessage, faqLink);
  }
}
