/* Copyright (c) 2014-2016 Jesper Öqvist <jesper@llbit.se>
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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Helper dialog for asking the user if the render should be reset.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ConfirmResetPopup extends Popup implements Initializable {

  public interface ChoiceListener {
    void onChoice();
  }

  @FXML private Text text;
  @FXML private Button accept;
  @FXML private Button cancel;
  @FXML private BorderPane pane;

  private boolean fired = false;
  private final ChoiceListener acceptListener;
  private final ChoiceListener rejectListener;

  public ConfirmResetPopup(ChoiceListener acceptListener, ChoiceListener rejectListener) throws IOException {
    this.acceptListener = acceptListener;
    this.rejectListener = rejectListener;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfirmResetPopup.fxml"));
    loader.setController(this);
    Parent root = loader.load();
    getContent().add(root);

    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        onReject();
        e.consume();
      }
    });
    focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        onReject();
      }
    });
  }

  private void onAccept() {
    if (!fired) {
      fired = true;
      acceptListener.onChoice();
    }
    hide();
  }

  private void onReject() {
    if (!fired) {
      fired = true;
      rejectListener.onChoice();
    }
    hide();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    text.setText("Something in the scene settings changed which requires a render reset. "
        + "Apply the changes and rest render?");
    accept.setTooltip(new Tooltip("Apply changes and reset the render."));
    accept.setText("Reset");
    accept.setOnAction(e -> onAccept());
    accept.setTooltip(new Tooltip("Discard changes and save render progress."));
    cancel.setText("Cancel");
    cancel.setOnAction(e -> onReject());
    pane.setStyle(
        "-fx-background-color: -fx-background;"
        + "-fx-border-color: -fx-accent;"
        + "-fx-border-style: solid;"
        + "-fx-border-width: 1px;");
  }

}
