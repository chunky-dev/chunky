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
import se.llbit.chunky.main.SceneHelper;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
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
  @FXML private TableView<JsonObject> sceneTbl;

  @FXML private TableColumn<JsonObject, String> nameCol;

  @FXML private TableColumn<JsonObject, Number> chunkCountCol;

  @FXML private TableColumn<JsonObject, String> sizeCol;

  @FXML private TableColumn<JsonObject, Number> sppCol;

  @FXML private TableColumn<JsonObject, String> renderTimeCol;

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
        JsonObject scene = sceneTbl.getSelectionModel().getSelectedItem();
        String sceneName = scene.get("fileName").stringValue("");
        if (sceneName.isEmpty()) {
          Log.error("Can not export scene with unknown filename.");
          return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Scene");
        fileChooser
            .getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip files", "*.zip"));
        fileChooser.setInitialFileName(String.format("%s.zip", sceneName));
        File targetFile = fileChooser.showSaveDialog(stage);
        if (targetFile != null) {
          Scene.exportToZip(sceneName, targetFile);
        }
      }
    });
    deleteBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        JsonObject scene = sceneTbl.getSelectionModel().getSelectedItem();
        String sceneName = scene.get("fileName").stringValue("");
        if (sceneName.isEmpty()) {
          Log.error("Can not delete scene with unknown filename.");
          return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Scene");
        alert.setContentText(String.format("Are you sure you want to delete the scene %s? "
            + "All files for the scene, except snapshot images, will be deleted.", sceneName));
        if (alert.showAndWait().get() == ButtonType.OK) {
          Scene.delete(sceneName, controller.getChunky().options.sceneDir);
          sceneTbl.getItems().remove(sceneTbl.getSelectionModel().getSelectedItem());
        }
      }
    });
    nameCol.setCellValueFactory(data -> {
      JsonObject scene = data.getValue();
      String sceneName = scene.get("fileName").stringValue("");
      return new ReadOnlyStringWrapper(sceneName);
    });
    chunkCountCol.setCellValueFactory(data -> {
      JsonObject scene = data.getValue();
      return new ReadOnlyIntegerWrapper(scene.get("chunkList").array().size());
    });
    sizeCol.setCellValueFactory(data -> {
      JsonObject scene = data.getValue();
      return new ReadOnlyStringWrapper(String.format("%sx%s", scene.get("width").intValue(400),
          scene.get("height").intValue(400)));
    });
    sppCol.setCellValueFactory(data -> {
      JsonObject scene = data.getValue();
      return new ReadOnlyIntegerWrapper(scene.get("spp").intValue(0));
    });
    renderTimeCol.setCellValueFactory(data -> {
      JsonObject scene = data.getValue();
      long renderTime = scene.get("renderTime").longValue(0);
      int seconds = (int) ((renderTime / 1000) % 60);
      int minutes = (int) ((renderTime / 60000) % 60);
      int hours = (int) (renderTime / 3600000);
      return new ReadOnlyStringWrapper(String.format("%d:%d:%d", hours, minutes, seconds));
    });
  }

  public void setStage(Stage stage) {
    this.stage = stage;
    sceneTbl.setRowFactory(tbl -> {
      TableRow<JsonObject> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()) {
          JsonObject scene = row.getItem();
          String sceneName = scene.get("fileName").stringValue("");
          if (sceneName.isEmpty()) {
            Log.error("Can't load scene with unknown filename.");
          } else {
            controller.loadScene(sceneName);
            e.consume();
            stage.close();
          }
        }
      });
      return row;
    });
    loadSceneBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        JsonObject scene = sceneTbl.getSelectionModel().getSelectedItem();
        String sceneName = scene.get("fileName").stringValue("");
        if (sceneName.isEmpty()) {
          Log.error("Can't load scene with unknown filename.");
        } else {
          controller.loadScene(sceneName);
          stage.close();
        }
      }
    });
    cancelBtn.setOnAction(e -> stage.hide());
  }

  private void populateSceneTable(File sceneDir) {
    List<JsonObject> scenes = new ArrayList<>();
    List<File> fileList = SceneHelper.getAvailableSceneFiles(sceneDir);
    Collections.sort(fileList);
    for (File sceneFile : fileList) {
      String fileName = sceneFile.getName();
      try {
        JsonParser parser = new JsonParser(new FileInputStream(new File(sceneDir, fileName)));
        JsonObject scene = parser.parse().object();
        // The scene name and filename may not always match. This can happen
        // if the user has copied and renamed some scene file.
        // Therefore we make sure to distinguish scenes by filename.
        scene.add("fileName",
            fileName.substring(0, fileName.length() - Scene.EXTENSION.length()));
        scenes.add(scene);
      } catch (IOException | JsonParser.SyntaxError e) {
        Log.warnf("Warning: could not load scene description: %s", fileName);
      }
    }
    sceneTbl.setItems(FXCollections.observableArrayList(scenes));
    if (!scenes.isEmpty()) {
      sceneTbl.getSelectionModel().select(0);
    }
  }

  public void setController(ChunkyFxController controller) {
    this.controller = controller;

    populateSceneTable(controller.getChunky().options.sceneDir);
  }
}
