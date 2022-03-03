package se.llbit.chunky.ui.dialogs;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogUtils {

  /**
   * Init design of the dialog to the design of the main window.
   * This sets the icon and color scheme.
   */
  public static void setupDialogDesign(Dialog<?> dialog, Scene mainScene) {
    Window mainWindow = mainScene.getWindow();
    if(mainWindow instanceof Stage) {
      Stage mainWindowStage = (Stage) mainWindow;
      Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();

      dialogStage.getIcons().addAll(mainWindowStage.getIcons());
    }
    dialog.initOwner(mainWindow);
  }
}
