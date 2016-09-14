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
package se.llbit.chunky.launcher.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Error reporting dialog for Chunky launch errors.
 * Used to display critical errors in a nicer way.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LaunchErrorDialog extends Stage {

  public LaunchErrorDialog(String command) {
    initModality(Modality.APPLICATION_MODAL);
    setTitle("Launch Error");

    Button dismissButton = new Button("Dismiss");

    dismissButton.setDefaultButton(true);
    dismissButton.setCancelButton(true);
    dismissButton.setOnAction(event -> hide());

    TextField commandField = new TextField(command);
    commandField.setPrefWidth(400);
    commandField.setMaxWidth(400);
    commandField.setEditable(false);
    commandField.setOnMouseClicked(event -> commandField.selectAll());

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(10));
    HBox buttons = new HBox();
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.getChildren().setAll(dismissButton);

    vBox.getChildren().setAll(new Label("Chunky failed to start! "
        + "See the Debug Console for error messages."),
        new Label("The following command was used to start Chunky:"),
        commandField, buttons);

    setScene(new Scene(vBox));
  }
}
