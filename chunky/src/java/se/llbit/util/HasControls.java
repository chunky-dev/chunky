package se.llbit.util;

import javafx.scene.layout.VBox;
import se.llbit.chunky.ui.render.RenderControlsTab;

/**
 * This interface specifies an object that has its own set of controls.
 */
public interface HasControls {
  /**
   * Get controls for this object.
   */
  default VBox getControls(RenderControlsTab parent) {
    VBox vBox = new VBox();
    vBox.setVisible(false);
    vBox.setManaged(false);
    return vBox;
  }
}
