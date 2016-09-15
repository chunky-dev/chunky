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

import javafx.stage.Stage;
import se.llbit.chunky.launcher.ui.ChunkyLauncherFx;
import se.llbit.chunky.launcher.ui.DebugConsole;
import se.llbit.chunky.launcher.ui.FirstTimeSetupDialog;
import se.llbit.chunky.resources.SettingsDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

  public static final String LAUNCHER_VERSION = "v1.9.1";

  protected String java;

  /**
   * Print a launch error message to the console.
   * Prints the command that was used to try to launch Chunky.
   */
  protected static void launchFailure(String command) {
    System.out.println("Failed to launch Chunky. Command used:");
    System.out.println(command);
  }

  public static void main(String[] args) {
    final LauncherSettings settings = new LauncherSettings();
    settings.load();

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
      for (String arg : args) {
        switch (arg) {
          case "--nolauncher":
            mode = LaunchMode.GUI;
            break;
          case "--launcher":
            forceLauncher = true;
            break;
          case "--version":
            System.out.println("Chunky Launcher " + LAUNCHER_VERSION);
            return;
          case "--verbose":
            settings.verboseLauncher = true;
            break;
          case "--console":
            settings.forceGuiConsole = true;
            break;
          case "--update":
          case "--updateAlpha":
            if (arg.equals("--updateAlpha")) {
              System.out.println("Checking for Chunky alpha/snapshot updates..");
              settings.downloadSnapshots = true;
            } else {
              System.out.println("Checking for Chunky updates..");
            }
            UpdateChecker updateThread = new UpdateChecker(settings, new UpdateListener() {
              @Override public void updateError(String message) {
              }

              @Override public void updateAvailable(VersionInfo latest) {
                System.out.println("Updating/downloading Chunky version " + latest + ":");
                ConsoleUpdater.update(latest);
              }

              @Override public void noUpdateAvailable() {
                System.out.println("No updates found.");
                if (!settings.downloadSnapshots) {
                  System.out
                      .println("Alpha/snapshot updates are disabled, enable with --updateAlpha");
                }
              }
            });
            updateThread.start();
            return;
          case "--setup":
            // Configure launcher settings.
            doSetup(settings);
            settings.save();
            return;
          default:
            if (!headlessOptions.isEmpty()) {
              headlessOptions += " ";
            }
            headlessOptions += arg;
            break;
        }
      }
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
      settings.debugConsole = true;
      settings.headless = true;
      settings.chunkyOptions = headlessOptions;
      ChunkyDeployer.deploy(settings); // Install the embedded version.
      VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
      if (ChunkyDeployer.canLaunch(version, null, false)) {
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
  }

  interface ShowLauncher {
    boolean showLauncher();
  }

  private static void doSetup(LauncherSettings settings) {
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
      ReadableByteChannel inChannel = Channels.newChannel(url.openStream());
      FileOutputStream out = new FileOutputStream(lib.getFile(libDir));
      out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
      out.close();
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
}
