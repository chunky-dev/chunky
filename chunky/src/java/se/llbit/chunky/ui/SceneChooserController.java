/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.main.SceneHelper;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.fxutil.Dialogs;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SceneChooserController implements Initializable {
  @FXML private TableView<SceneListItem> sceneTbl;
  @FXML private TableColumn<SceneListItem, String> nameCol;
  @FXML private TableColumn<SceneListItem, Number> chunkCountCol;
  @FXML private TableColumn<SceneListItem, String> sizeCol;
  @FXML private TableColumn<SceneListItem, Number> sppCol;
  @FXML private TableColumn<SceneListItem, Number> renderTimeCol;
  @FXML private TableColumn<SceneListItem, Date> lastModifiedCol;

  @FXML private Button loadSceneBtn;
  @FXML private Button cancelBtn;
  @FXML private Button exportBtn;
  @FXML private Button deleteBtn;

  private Stage stage;

  private ChunkyFxController controller;

  private static final HashMap<FileTimeCache, SceneListItem> sceneListCache = new HashMap<>();

  @Override public void initialize(URL location, ResourceBundle resources) {
    exportBtn.setTooltip(new Tooltip("Exports the selected scene as a Zip archive."));
    exportBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        SceneListItem scene = sceneTbl.getSelectionModel().getSelectedItem();
        if (scene.sceneName.isEmpty()) {
          Log.error("Can not export scene with unknown filename.");
          return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Scene");
        fileChooser
            .getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip files", "*.zip"));
        fileChooser.setInitialFileName(String.format("%s.zip", scene.sceneName));
        File targetFile = fileChooser.showSaveDialog(stage);
        if (targetFile != null) {
          Scene.exportToZip(scene.sceneName, targetFile);
        }
      }
    });
    deleteBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        SceneListItem scene = sceneTbl.getSelectionModel().getSelectedItem();
        if (scene.sceneName.isEmpty()) {
          Log.error("Can not delete scene with unknown filename.");
          return;
        }
        Alert alert = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Scene");
        alert.setContentText(String.format("Are you sure you want to delete the scene %s? "
            + "All files for the scene, except snapshot images, will be deleted.", scene.sceneName));
        if (alert.showAndWait().get() == ButtonType.OK) {
          Scene.delete(scene.sceneName, scene.sceneDirectory);
          sceneTbl.getItems().remove(sceneTbl.getSelectionModel().getSelectedItem());
        }
      }
    });
    nameCol.setCellValueFactory(data -> {
      SceneListItem scene = data.getValue();
      if (scene.isBackup) {
        return new ReadOnlyStringWrapper(scene.sceneName + " [backup]");
      } else {
        return new ReadOnlyStringWrapper(scene.sceneName);
      }
    });
    chunkCountCol.setCellValueFactory(data -> {
      SceneListItem scene = data.getValue();
      return scene.chunkSize;
    });
    sizeCol.setCellValueFactory(data -> {
      SceneListItem scene = data.getValue();
      return scene.dimensions;
    });
    sppCol.setCellValueFactory(data -> {
      SceneListItem scene = data.getValue();
      return scene.sppCount;
    });

    renderTimeCol.setCellValueFactory(data -> {
      SceneListItem scene = data.getValue();
      return scene.renderTime;
    });
    renderTimeCol.setCellFactory(col -> new TableCell<SceneListItem, Number>() {
      public void updateItem(Number item, boolean empty) {
        if (item == this.getItem()) return;
        super.updateItem(item, empty);
        super.setGraphic(null);
        super.setText(item == null ? null : SceneListItem.renderTimeString(item.longValue()));
      }
    });

    DateFormat localeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    lastModifiedCol.setCellValueFactory(data -> {
      SceneListItem scene = data.getValue();
      Date lastModified = new Date(scene.sceneDirectory.lastModified());
      return new ReadOnlyObjectWrapper<>(lastModified);
    });
    lastModifiedCol.setCellFactory(col -> new TableCell<SceneListItem, Date>() {
      public void updateItem(Date item, boolean empty) {
        if (item == this.getItem()) return;
        super.updateItem(item, empty);
        super.setGraphic(null);
        super.setText(item == null ? null : localeFormat.format(item));
      }
    });
  }

  public void setStage(Stage stage) {
    this.stage = stage;
    sceneTbl.setRowFactory(tbl -> {
      TableRow<SceneListItem> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()) {
          SceneListItem scene = row.getItem();
          if (scene.sceneName.isEmpty()) {
            Log.error("Can't load scene with unknown filename.");
          } else {
            controller.loadScene(scene.sceneName);
            e.consume();
            stage.close();
          }
        }
      });
      return row;
    });
    loadSceneBtn.setOnAction(e -> {
      if (!sceneTbl.getSelectionModel().isEmpty()) {
        SceneListItem scene = sceneTbl.getSelectionModel().getSelectedItem();
        if (scene.sceneName.isEmpty()) {
          Log.error("Can't load scene with unknown filename.");
        } else {
          controller.loadScene(scene.sceneName);
          stage.close();
        }
      }
    });
    cancelBtn.setOnAction(e -> {
      sceneTbl.getItems().clear();
      stage.hide();
    });
  }

  private void populateSceneTable(File sceneDir) {
    List<SceneListItem> scenes = new ArrayList<>();
    List<File> fileList = SceneHelper.getAvailableSceneFiles(sceneDir);
    fileList.sort(Comparator.comparing(File::length));
    Executor loadExecutor = Executors.newSingleThreadExecutor();
    for (File sceneFile : fileList) {
      FileTimeCache file = new FileTimeCache(sceneFile);
      scenes.add(sceneListCache.computeIfAbsent(file, f -> new SceneListItem(f.file, loadExecutor)));
    }

    scenes.sort(Comparator
        .comparing((SceneListItem scene) -> -scene.lastModified.getTime())
        .thenComparing((SceneListItem scene) -> scene.sceneName.toLowerCase())
    );
    sceneTbl.setItems(FXCollections.observableArrayList(scenes));
    if (!scenes.isEmpty()) {
      sceneTbl.getSelectionModel().select(0);
    }
  }

  public void setController(ChunkyFxController controller) {
    this.controller = controller;

    populateSceneTable(controller.getChunky().options.sceneDir);
  }

  private static class SceneListItem {
    /** The name of the scene */
    public final String sceneName;
    /** The last modified time of the scene */
    public final Date lastModified;
    /** What folder the scene is in */
    public final File sceneDirectory;
    /** Whether this scene description file is a backup file and the original .json is missing. */
    public final boolean isBackup;

    /** The number of chunks in the scene */
    public final ReadOnlyObjectWrapper<Number> chunkSize;
    /** The dimensions of the scene canvas */
    public final ReadOnlyObjectWrapper<String> dimensions;
    /** The spp count of the render */
    public final ReadOnlyObjectWrapper<Number> sppCount;
    /** The elapsed render time */
    public final ReadOnlyObjectWrapper<Number> renderTime;

    private SceneListItem(File sceneFile, Executor backgroundLoadExecutor) {
      this.sceneDirectory = sceneFile.getParentFile();
      this.lastModified = new Date(sceneFile.lastModified());
      String sceneName = sceneFile.getName();
      this.isBackup = sceneName.endsWith(".backup");
      int lengthWithoutExtension = sceneName.length()
          - (Scene.EXTENSION.length() + (isBackup ? ".backup".length() : 0));
      this.sceneName = sceneName.substring(0, lengthWithoutExtension);

      this.chunkSize = new ReadOnlyObjectWrapper<>();
      this.dimensions = new ReadOnlyObjectWrapper<>();
      this.sppCount = new ReadOnlyObjectWrapper<>();
      this.renderTime = new ReadOnlyObjectWrapper<>();

      backgroundLoadExecutor.execute(() -> parseScene(sceneFile));
    }

    public static String renderTimeString(long milliseconds) {
      if (milliseconds < 1000) return " — ";

      long seconds = FastMath.round(milliseconds / 1000d);
      long minutes = seconds / 60;
      long hours = minutes / 60;
      StringBuilder sb = new StringBuilder();
      if (hours > 0)
        sb.append(hours).append("hr ");
      if (minutes > 0) {
        sb.append(String.format("%02dm %02ds", minutes % 60, seconds % 60));
      } else {
        sb.append(String.format("%ds", milliseconds / 1000));
      }
      return sb.toString();
    }

    private void parseScene(File sceneFile) {
      String dimensions = null;
      Integer chunkSize = null;
      Integer sppCount = null;
      Long renderTime = null;

      try (JsonParser parser = new JsonParser(new FileInputStream(new File(sceneFile.getParentFile(), sceneFile.getName())))) {
        JsonObject scene = parser.parse().object();

        int width = scene.get("width").intValue(400);
        int height = scene.get("height").intValue(400);
        dimensions = String.format("%sx%s", width, height);

        chunkSize = scene.get("chunkList").array().size();
        sppCount = scene.get("spp").intValue(0);

        // Not currently used (Planned for Chunky 2.5.0; See PR #786)
        //    JsonValue crop = scene.get("crop");
        //    if (crop.isArray()) {
        //      JsonArray cropArray = crop.asArray();
        //      if (cropArray.size() >= 4) {
        //        int w = cropArray.get(2).asInt(width);
        //        int h = cropArray.get(3).asInt(height);
        //        if (w != width || h != height || cropArray.get(0).asInt(0) != 0 || cropArray.get(1).asInt(0) != 0) {
        //          cropping = " [" + w + "x" + h + "]";
        //        } else cropping = null;
        //      } else cropping = null;
        //    } else cropping = null;

        renderTime = scene.get("renderTime").longValue(0);
      } catch (IOException | JsonParser.SyntaxError e) {
        Log.warnf("Warning: could not load scene description: %s", sceneFile.getName());
      }

      this.dimensions.setValue(dimensions);
      this.chunkSize.setValue(chunkSize);
      this.sppCount.setValue(sppCount);
      this.renderTime.setValue(renderTime);

//      Log.infof("Finished parsing: %s", this);
    }

    @Override
    public String toString() {
      String dimensions = this.dimensions.get() != null ? this.dimensions.get() : "-";
      String chunkSize = this.chunkSize.get() != null ? this.chunkSize.get().toString() : "-";
      String sppCount = this.sppCount.get() != null ? this.sppCount.get().toString() : "-";
      String renderTime = this.renderTime.get() != null ? renderTimeString(this.renderTime.get().longValue()) : "-";

      return String.format("Name:%s, Chunks:%s, Size:%s, Spp:%s, Time:%s, Location:%s",
          sceneName, chunkSize, dimensions, sppCount, renderTime, sceneDirectory.getName());
    }
  }

  private static class FileTimeCache {
    public final File file;
    public final long lastModified;

    public FileTimeCache(File file) {
      this.file = file;
      this.lastModified = file.lastModified();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FileTimeCache that = (FileTimeCache) o;
      return lastModified == that.lastModified && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
      return Objects.hash(file, lastModified);
    }
  }
}
