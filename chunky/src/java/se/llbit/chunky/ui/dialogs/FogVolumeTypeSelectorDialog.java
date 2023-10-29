package se.llbit.chunky.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.FogVolumeType;

public class FogVolumeTypeSelectorDialog extends Dialog<ButtonType> {
  protected ChoiceBox<FogVolumeType> choiceBox = new ChoiceBox<>();

  public FogVolumeTypeSelectorDialog() {
    this.setTitle("Select fog volume type");

    DialogPane dialogPane = this.getDialogPane();
    VBox vBox = new VBox();

    choiceBox.getItems().addAll(FogVolumeType.values());

    vBox.getChildren().add(choiceBox);
    vBox.setPadding(new Insets(10));

    dialogPane.setContent(vBox);
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
  }

  public FogVolumeType getType() {
    return choiceBox.getSelectionModel().getSelectedItem();
  }
}
