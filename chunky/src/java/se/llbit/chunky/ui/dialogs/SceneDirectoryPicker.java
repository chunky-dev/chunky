/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.world.Icon;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SceneDirectoryPicker extends Stage implements Initializable {

  @FXML TextField sceneDirectory;
  @FXML CheckBox createDirectory;
  @FXML Button browseBtn;
  @FXML Button okBtn;
  @FXML Button cancelBtn;
  @FXML Label warningLabel;

  private File selectedDirectory;
  private boolean accepted = false;

  /**
   * Constructor
   */
  public SceneDirectoryPicker(ChunkyOptions options) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SceneDirectoryChooser.fxml"));
    loader.setController(this);
    Parent root = loader.load();
    setTitle("Scene Directory Picker");
    initModality(Modality.APPLICATION_MODAL);
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    setScene(new Scene(root));
    addEventFilter(KeyEvent.ANY, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        close();
      }
    });

    createDirectory.selectedProperty()
        .addListener(observable -> updateSelectedDirectory(selectedDirectory));

    updatePathField(options.sceneDir);
    updateSelectedDirectory(options.sceneDir);

    browseBtn.setOnAction(event -> {
      DirectoryChooser fileChooser = new DirectoryChooser();
      if (selectedDirectory != null && selectedDirectory.isDirectory()) {
        fileChooser.setInitialDirectory(selectedDirectory);
      }
      fileChooser.setTitle("Select Scene Directory");
      File result = fileChooser.showDialog(getScene().getWindow());
      if (result != null) {
        updateSelectedDirectory(result);
        updatePathField(result);
      }
    });

    cancelBtn.setOnAction(event -> hide());

    okBtn.setOnAction(event -> {
      if (createDirectory.isSelected()) {
        tryCreateSceneDir(selectedDirectory);
      }
      accepted = true;
      hide();
    });

    sceneDirectory.textProperty().addListener(
        (observable, oldValue, newValue) -> updateSelectedDirectory(new File(newValue)));

    warningLabel.setGraphic(new ImageView(Icon.failed.fxImage()));
  }

  protected void updateSelectedDirectory(File path) {
    selectedDirectory = path;
    boolean directoryExists = isValidDirectory(path);
    boolean invalid = !(createDirectory.isSelected() || directoryExists);
    createDirectory.setVisible(!directoryExists);
    warningLabel.setVisible(invalid);
    okBtn.setDisable(invalid);
  }

  private static boolean isValidDirectory(File path) {
    return path.isDirectory() && path.canWrite();
  }

  protected void updatePathField(File path) {
    sceneDirectory.setText(path.getAbsolutePath());
  }

  /**
   * @return The selected scene directory
   */
  public File getSelectedDirectory() {
    return selectedDirectory;
  }

  /**
   * @return <code>true</code> if the OK button was clicked
   */
  public boolean isAccepted() {
    return accepted;
  }

  /**
   * Opens a dialog asking the user to specify a new scene directory.
   *
   * @return The file representing the selected directory
   */
  public static File changeSceneDirectory(ChunkyOptions options) {
    try {
      while (true) {
        SceneDirectoryPicker sceneDirPicker = new SceneDirectoryPicker(options);
        sceneDirPicker.showAndWait();
        if (!sceneDirPicker.isAccepted()) {
          return null;
        }
        File sceneDir = sceneDirPicker.getSelectedDirectory();
        if (isValidDirectory(sceneDir)) {
          PersistentSettings.setSceneDirectory(sceneDir);
          // TODO: It may be a good idea to not write directly to the shared scene directory.
          // It does not matter much right now, but it might be useful in the future to have
          // an API for getting/setting the scene directory so that custom render contexts can
          // use a fixed scene directory.
          options.sceneDir = sceneDir;
          return sceneDir;
        }
      }
    } catch (IOException e) {
      Log.error("Failed to open scene directory chooser.", e);
      return null;
    }
  }

  private static boolean tryCreateSceneDir(File sceneDir) {
    if (!sceneDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      sceneDir.mkdirs();
    }
    if (!isValidDirectory(sceneDir)) {
      Log.warnf("Could not open or create the scene directory %s", sceneDir.getAbsolutePath());
      return false;
    } else {
      return true;
    }
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
  }
}
