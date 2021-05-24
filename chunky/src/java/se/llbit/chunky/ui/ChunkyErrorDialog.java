/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import se.llbit.log.Level;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Error reporting dialog for Chunky.
 *
 * <p>Used to display critical errors in a nicer way.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyErrorDialog extends Stage implements Initializable {

  @FXML TabPane tabPane;

  private int messageCount = 0;
  private final Level type;

  /**
   * Initialize the error dialog.
   */
  public ChunkyErrorDialog(Level errorType) throws IOException {
    type = errorType;

    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName(errorType)));
    loader.setController(this);
    Parent root = loader.load();
    setTitle(getLevelName(errorType) + " Summary");
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    setScene(new Scene(root));
    addEventFilter(KeyEvent.ANY, e -> {
      if (e.getCode() == KeyCode.ESCAPE && e.getEventType() == KeyEvent.KEY_RELEASED) {
        e.consume();
        close();
      }
    });
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
  }

  /**
   * Add a log record to be displayed by this error dialog.
   */
  public synchronized void addMessageAndShow(String message) {
    addMessage(message);
    this.show();
    this.setAlwaysOnTop(true); // will probably fail.
    this.toFront();
  }

  /**
   * Add a log record to be displayed by this error dialog.
   */
  private synchronized void addMessage(String message) {
    messageCount += 1;

    BorderPane.setMargin(tabPane, new Insets(10, 0, 0, 0));

    BorderPane pane = new BorderPane();
    pane.setPadding(new Insets(10));

    TextArea text = new TextArea();
    text.setText(message);
    text.setEditable(false);
    text.setPrefHeight(Region.USE_COMPUTED_SIZE);
    pane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    pane.setCenter(text);

    Tab tab = new Tab(getLevelName(type) + " " + messageCount, pane);
    tabPane.getTabs().add(tab);
  }

  @FXML
  protected void dismissMessage(ActionEvent event) {
    int currentTabIndex = tabPane.getSelectionModel().getSelectedIndex();
    tabPane.getTabs().remove(currentTabIndex);
    if (tabPane.getTabs().isEmpty()) {
      hide();
    }
  }

  @FXML
  protected void dismissAllMessages(ActionEvent event) {
    tabPane.getTabs().clear();
    hide();
  }

  private String fxmlName(Level level)
  {
    switch (level) {
      case WARNING:
        return "WarningDialog.fxml";
      case ERROR:
      default: // To prevent crash should this happen (it shouldn't), use Error.
        return "ErrorDialog.fxml";
    }
  }

  private String getLevelName(Level level) {
    switch (level) {
      case WARNING:
        return "Warning";
      case ERROR:
        return "Error";
      case INFO:
        return "Info";
      default:
        return ""; // should never happen
    }
  }
}
