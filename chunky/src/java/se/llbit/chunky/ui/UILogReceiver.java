/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

import javafx.application.Platform;
import se.llbit.chunky.ui.dialogs.ChunkyErrorDialog;
import se.llbit.log.Level;
import se.llbit.log.Receiver;

import java.io.IOException;

/**
 * A log handler for the global Chunky logger.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class UILogReceiver extends Receiver {

  private ChunkyErrorDialog errorDialog = null;
  private ChunkyErrorDialog warningDialog = null;

  @Override public void logEvent(Level level, final String message) {
    Platform.runLater(() -> {
      createOrGetDialogContainer(level).addMessageAndShow(message);
    });
  }

  private ChunkyErrorDialog createOrGetDialogContainer(Level level) {
    switch (level) {
      case INFO:
      case WARNING:
        if (warningDialog == null) {
          try { 
            warningDialog = new ChunkyErrorDialog(Level.WARNING); 
          } catch (IOException e) { 
            throw new Error("Failed to create warning dialog", e); 
          }
        }
        return warningDialog;
      case ERROR:
      default:
        if (errorDialog == null) {
          try { 
            errorDialog = new ChunkyErrorDialog(Level.ERROR); 
          } catch (IOException e) {
            throw new Error("Failed to create error dialog", e); 
          }
        }
        return errorDialog;
    }
  }
}
