/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import se.llbit.log.Log;
import se.llbit.util.OSDetector;

public class ShutdownAlert extends ProgressMonitor implements ActionListener {

  /**
   * Time in seconds to shutdown
   */
  private static final int time = 30;
  /**
   * Message for shutdown alert. Does not change.
   */
  private static final String message = "The render has finished. Your computer will be shut down.";
  /**
   * Note for shutdown alert, updates every second.
   */
  private static final String note = "Shutting down in %d seconds.";

  /**
   * Set this to false to not actually shut down the machine
   */
  private static final boolean DO_SHUTDOWN = true;

  private static final String SHUTDOWN_WINDOWS = "shutdown -s";

  /**
   * Shutdown command is identical across most Unix-like OSes
   */
  private static final String SHUTDOWN_UNIX = "sudo shutdown -h now";

  public ShutdownAlert(Component parentComponent) {
    super(parentComponent, message, note, 0, time);

    timeLeft = time;
    timer.start();
  }

  private final Timer timer = new Timer(1000, this);

  private int timeLeft;

  private void update() {
    if (!isCanceled()) {
      if (timeLeft > 0) {
        setProgress(--timeLeft);
        setNote(String.format(note, timeLeft));
      } else {
        timer.stop();
        shutdown();
      }
    } else {
      timer.stop();
    }
  }

  @Override public void actionPerformed(ActionEvent e) {
    update();
  }

  private static void shutdown() {
    if (DO_SHUTDOWN) {
      try {
        Process proc = Runtime.getRuntime().exec(getShutdownCommand());
        proc.waitFor();
        if (0 != proc.exitValue()) {
          Log.error("Failed to shutdown computer (command returned error code)");
        }
      } catch (IOException | InterruptedException e) {
        Log.error("Failed to shutdown computer", e);
      }
    } else {
      Log.info("Shutdown command: " + getShutdownCommand());
    }
  }

  private static String getShutdownCommand() {
    switch (OSDetector.getOS()) {
      case WIN:
        return SHUTDOWN_WINDOWS;
      case MAC:
      case LINUX:
      case BSD:
        return SHUTDOWN_UNIX;
      default:
        Log.info("Don't know how to shutdown this computer (OS unknown)");
        return "";
    }
  }

  public static boolean canShutdown() {
    return !getShutdownCommand().isEmpty();
  }
}
