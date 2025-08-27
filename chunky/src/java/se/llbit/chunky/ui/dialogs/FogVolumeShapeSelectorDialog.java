package se.llbit.chunky.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.volumetricfog.FogVolumeShape;

public class FogVolumeShapeSelectorDialog extends Dialog<ButtonType> {
  protected ChoiceBox<FogVolumeShape> choiceBox = new ChoiceBox<>();

  public FogVolumeShapeSelectorDialog() {
    this.setTitle("Select fog volume type");

    DialogPane dialogPane = this.getDialogPane();
    VBox vBox = new VBox();

    choiceBox.getItems().addAll(FogVolumeShape.values());

    vBox.getChildren().add(choiceBox);
    vBox.setPadding(new Insets(10));

    dialogPane.setContent(vBox);
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
  }

  public FogVolumeShape getShape() {
    return choiceBox.getSelectionModel().getSelectedItem();
  }
}