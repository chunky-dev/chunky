package se.llbit.fxutil;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Dialogs {

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

  /**
   * Init design of the dialog to the design of the main window.
   * This sets the icon and color scheme.
   */
  public static void setupDialogDesign(Dialog<?> dialog, Scene mainScene) {
    Window mainWindow = mainScene.getWindow();
    if (mainWindow instanceof Stage) {
      Stage mainWindowStage = (Stage) mainWindow;
      Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();

      dialogStage.getIcons().addAll(mainWindowStage.getIcons());
    }
    dialog.initOwner(mainWindow);
    dialog.initModality(Modality.WINDOW_MODAL);
  }

  /**
   * Sets the default button to the one matching the <code>targetButtonType</code>.
   */
  public static void setDefaultButton(Alert alert, ButtonType targetButtonType) {
    alert.getButtonTypes().forEach(buttonType -> {
      Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
      button.setDefaultButton(buttonType == targetButtonType);
    });
  }

  /**
   * Makes the given dialog always stay on top of its parent window.
   *
   * @param dialog A dialog
   */
  public static void stayOnTop(Alert dialog) {
    Window window = dialog.getDialogPane().getScene().getWindow();
    if (window instanceof Stage) {
      ((Stage) window).setAlwaysOnTop(true);
      ((Stage) window).toFront();
    }
  }
}
