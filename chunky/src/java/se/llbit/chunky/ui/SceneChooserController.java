/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.SceneDescription;
import se.llbit.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class SceneChooserController implements Initializable {
  @FXML private TableView<SceneDescription> sceneTbl;

  @FXML private TableColumn<SceneDescription, String> nameCol;

  @FXML private TableColumn<SceneDescription, Number> chunkCountCol;

  @FXML private TableColumn<SceneDescription, String> sizeCol;

  @FXML private TableColumn<SceneDescription, Number> sppCol;

  @FXML private TableColumn<SceneDescription, String> renderTimeCol;

  @FXML private Button loadSceneBtn;

  @FXML private Button cancelBtn;

  @FXML private Button exportBtn;

  @FXML private Button deleteBtn;

  private Stage stage;

  private ChunkyFxController controller;

  @Override public void initialize(URL location, ResourceBundle resources) {
    exportBtn.setTooltip(new Tooltip("Exports the selected scene as a Zip archive."));
    exportBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        SceneDescription scene = sceneTbl.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Scene");
        fileChooser
            .setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Zip files", "*.zip"));
        fileChooser.setInitialFileName(String.format("%s.zip", scene.name));
        File target = fileChooser.showSaveDialog(stage);
        if (target != null) {
          scene.exportToZip(target);
        }
      }
    });
    deleteBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        SceneDescription scene = sceneTbl.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Scene");
        alert.setContentText(String.format("Are you sure you want to delete the scene %s? "
            + "All files for the scene, except snapshot images, will be deleted.", scene.name));
        if (alert.showAndWait().get() == ButtonType.OK) {
          scene.delete();
          sceneTbl.getItems().remove(sceneTbl.getSelectionModel().getSelectedItem());
        }
      }
    });
    nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().name));
    chunkCountCol
        .setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().numberOfChunks()));
    sizeCol.setCellValueFactory(data -> {
      SceneDescription scene = data.getValue();
      return new ReadOnlyStringWrapper(String.format("%dx%d", scene.width, scene.height));
    });
    sppCol.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().spp));
    renderTimeCol.setCellValueFactory(data -> {
      SceneDescription scene = data.getValue();
      int seconds = (int) ((scene.renderTime / 1000) % 60);
      int minutes = (int) ((scene.renderTime / 60000) % 60);
      int hours = (int) (scene.renderTime / 3600000);
      return new ReadOnlyStringWrapper(String.format("%d:%d:%d", hours, minutes, seconds));
    });

    populate();
  }

  public void setStage(Stage stage) {
    this.stage = stage;
    sceneTbl.setRowFactory(tbl -> {
      TableRow<SceneDescription> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()) {
          SceneDescription scene = row.getItem();
          controller.loadScene(scene);
          e.consume();
          stage.close();
        }
      });
      return row;
    });
    loadSceneBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        SceneDescription scene = sceneTbl.getSelectionModel().getSelectedItem();
        controller.loadScene(scene);
        stage.close();
      }
    });
    cancelBtn.setOnAction(e -> stage.close());
  }

  private void populate() {
    List<SceneDescription> scenes = new ArrayList<>();
    File sceneDir = PersistentSettings.getSceneDirectory();
    List<File> fileList = getAvailableSceneFiles(sceneDir);
    Collections.sort(fileList);
    for (File sceneFile : fileList) {
      String fileName = sceneFile.getName();
      try {
        SceneDescription desc = new SceneDescription();
        desc.loadDescription(new FileInputStream(new File(sceneDir, fileName)));
        scenes.add(desc);
      } catch (IOException e) {
        Log.warningfmt("Warning: could not load scene description: %s", fileName);
      }
    }
    sceneTbl.setItems(FXCollections.observableArrayList(scenes));
    if (!scenes.isEmpty()) {
      sceneTbl.getSelectionModel().select(0);
    }
  }

  /**
   * @return a list of available scene description files in the given scene
   * directory
   */
  public static final List<File> getAvailableSceneFiles(File sceneDir) {
    File[] sceneFiles = sceneDir.listFiles((dir, name) -> {
      return name.endsWith(SceneDescription.SCENE_DESCRIPTION_EXTENSION);
    });
    List<File> fileList = new ArrayList<>(sceneFiles.length);
    for (File file : sceneFiles) {
      fileList.add(file);
    }
    return fileList;
  }

  public void setController(ChunkyFxController controller) {
    this.controller = controller;
  }
}
