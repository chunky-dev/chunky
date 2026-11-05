package se.llbit.chunky.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;

public class EditMaterialDialog extends Dialog<ButtonType> {
  public EditMaterialDialog(Material material, Scene scene) {
    DialogPane dialogPane = this.getDialogPane();
    VBox vBox = Material.getControls(material, scene);
    vBox.setPadding(new Insets(10));

    dialogPane.setContent(vBox);
    dialogPane.getButtonTypes().add(ButtonType.CLOSE);
  }
}
