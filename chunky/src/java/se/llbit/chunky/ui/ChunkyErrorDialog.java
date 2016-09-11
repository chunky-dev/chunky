/* Copyright (c) 2012,2014 Jesper Öqvist <jesper@llbit.se>
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Error reporting dialog for Chunky.
 * <p>
 * Used to display critical errors in a nicer way.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyErrorDialog extends Stage implements Initializable {

  @FXML TabPane tabPane;

  private int errorCount = 0;

  /**
   * Initialize the error dialog.
   */
  public ChunkyErrorDialog() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("ErrorDialog.fxml"));
    // Needed for Java 1.8u40 where FXMLLoader has a null class loader for some reason.
    loader.setClassLoader(getClass().getClassLoader());
    loader.setController(this);
    Parent root = loader.load();
    setTitle("Error Summary");
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    setScene(new Scene(root));
    addEventFilter(KeyEvent.ANY, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
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
  public synchronized void addErrorMessage(String message) {
    errorCount += 1;

    VBox vBox = new VBox();
    vBox.setPadding(new Insets(10));
    vBox.setSpacing(10);
    vBox.setAlignment(Pos.TOP_RIGHT);
    TextArea text = new TextArea();
    text.setText(message);
    text.setEditable(false);
    Button dismissBtn = new Button("Dismiss");

    text.setPrefHeight(Region.USE_COMPUTED_SIZE);
    vBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
    vBox.getChildren().setAll(text, dismissBtn);

    Tab tab = new Tab("Error " + errorCount, vBox);
    tabPane.getTabs().add(tab);

    dismissBtn.setOnAction(event -> {
      tabPane.getTabs().remove(tab);
      if (tabPane.getTabs().isEmpty()) {
        hide();
      }
    });
  }

}
