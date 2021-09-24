package se.llbit.fxutil;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

  public static Dialog<ButtonType> createSpecialApprovalConfirmation(
    String title,
    String header,
    String content,
    String confirmLabel
  ) {
    return new SpecialApprovalConfirmationDialog(
      title,
      header,
      content,
      confirmLabel
    );
  }

  /**
   * makes extra sure that user wants to confirm things
   */
  static class SpecialApprovalConfirmationDialog extends Dialog<ButtonType> {
    final CheckBox checkBox;

    SpecialApprovalConfirmationDialog(
      String title,
      String header,
      String content,
      String checkBoxLabel
    ) {
      setResultConverter(param -> param);
      checkBox = new CheckBox(checkBoxLabel);

      final DialogPane dialogPane = getDialogPane();
      dialogPane.getStyleClass().add("alert");
      dialogPane.getStyleClass().add("warning");

      setTitle(title);
      dialogPane.setHeaderText(header);
      dialogPane.setContent(new VBox(16, new Text(content), checkBox));
      dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

      dialogPane.lookupButton(ButtonType.OK)
        .disableProperty().bind(checkBox.selectedProperty().not());
    }
  }
}
