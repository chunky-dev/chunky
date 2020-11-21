package se.llbit.fxutil;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class AlertFactory {

  /**
   * Create an alert dialog of the given type that will resize to the text content properly on
   * Linux.
   */
  public static Alert createAlert(AlertType type) {
    Alert alert = new Alert(type);
    // We have to do some adjustments to make the alert dialog resize to
    // the text content on Linux. Source: http://stackoverflow.com/a/33905734
    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
        .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
    return alert;
  }
}
