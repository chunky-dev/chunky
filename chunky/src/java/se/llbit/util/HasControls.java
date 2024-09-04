package se.llbit.util;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;

public interface HasControls {
  /**
   * Get controls for this object.
   */
  default VBox getControls(Node parent, Scene scene) {
    VBox vBox = new VBox();
    vBox.setVisible(false);
    vBox.setManaged(false);
    return vBox;
  }
}
