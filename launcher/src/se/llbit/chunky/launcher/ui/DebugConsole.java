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
package se.llbit.chunky.launcher.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.llbit.chunky.launcher.Logger;

/**
 * Window that displays debug messages.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class DebugConsole extends Stage implements Logger {

  private final TextArea statusText;
  private final boolean closeConsoleOnExit;

  // TODO: add support for colored text in the debug console.
  public DebugConsole(boolean closeConsoleOnExit) {
    setTitle("Debug Console");

    this.closeConsoleOnExit = closeConsoleOnExit;

    statusText = new TextArea();
    statusText.setPrefWidth(600);
    statusText.setPrefHeight(400);
    statusText.setEditable(false);

    Button closeBtn = new Button("Close");
    closeBtn.setDefaultButton(true);
    closeBtn.setCancelButton(true);
    closeBtn.setTooltip(new Tooltip("Close the debug console"));
    closeBtn.setOnAction(event -> hide());

    getIcons().add(new Image(getClass().getResourceAsStream("chunky-cfg.png")));

    VBox buttonBar = new VBox();
    buttonBar.setPadding(new Insets(10));
    buttonBar.setAlignment(Pos.TOP_RIGHT);
    buttonBar.getChildren().add(closeBtn);

    BorderPane content = new BorderPane();
    content.setCenter(statusText);
    content.setBottom(buttonBar);

    setScene(new Scene(content));
  }

  /**
   * Append text to the status text area.
   */
  public void appendStatusText(final String text) {
    Platform.runLater(() -> statusText.appendText(text));
  }

  @Override public void processExited(int exitValue) {
    if (exitValue == 0) {
      appendLine("Chunky exited normally");
      if (closeConsoleOnExit) {
        Platform.runLater(() -> hide());
      }
    } else {
      appendLine("Chunky exited abnormally with exit value " + exitValue);
    }
  }

  @Override public void appendStdout(byte[] buffer, int size) {
    appendStatusText(new String(buffer, 0, size));
  }

  @Override public void appendStderr(byte[] buffer, int size) {
    appendStatusText(new String(buffer, 0, size));
  }

  @Override public void appendLine(String line) {
    appendStatusText(line + "\n");
  }

  @Override public void appendErrorLine(String line) {
    appendStatusText(line + "\n");
  }
}
