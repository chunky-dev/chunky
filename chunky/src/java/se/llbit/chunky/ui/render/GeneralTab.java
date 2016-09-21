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
package se.llbit.chunky.ui.render;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.SceneDirectoryPicker;
import se.llbit.chunky.ui.ChunkyFxController;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.RenderCanvasFx;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.chunky.ui.SceneChooser;
import se.llbit.chunky.world.Icon;
import se.llbit.log.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralTab extends VBox implements RenderControlTab, Initializable {
  private Scene scene;

  @FXML private Button loadSceneBtn;

  @FXML private Button openSceneDirBtn;

  @FXML private Button loadSelectedChunks;

  @FXML private Button reloadChunks;

  @FXML private ComboBox<String> canvasSize;

  @FXML private Label canvasSizeLbl;

  @FXML private Button applySize;

  @FXML private Button makeDefaultSize;

  @FXML private Button scale05;

  @FXML private Button scale15;

  @FXML private Button scale20;

  @FXML private CheckBox loadPlayers;

  @FXML private CheckBox biomeColors;

  @FXML private CheckBox saveDumps;

  @FXML private CheckBox saveSnapshots;

  @FXML private ComboBox<Number> dumpFrequency;

  @FXML private IntegerAdjuster yCutoff;

  private ChangeListener<String> canvasSizeListener =
      (observable, oldValue, newValue) -> updateCanvasSize();

  private RenderController controller;
  private WorldMapLoader mapLoader;
  private RenderControlsFxController fxController;
  private final Tooltip reloadHint;
  private ChunkyFxController chunkyFxController;

  public GeneralTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneralTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();

    reloadHint = new Tooltip("This takes effect the next time chunks are reloaded.");
    reloadHint.setConsumeAutoHidingEvents(false);
    reloadHint.setAutoHide(true);
  }

  @Override public void update(Scene scene) {
    yCutoff.set(PersistentSettings.getYCutoff());
    canvasSize.valueProperty().removeListener(canvasSizeListener);
    canvasSize.setValue(String.format("%dx%d", scene.width, scene.height));
    canvasSize.valueProperty().addListener(canvasSizeListener);
    if (scene.shouldSaveDumps()) {
      dumpFrequency.setValue(scene.getDumpFrequency());
      dumpFrequency.setDisable(false);
      saveDumps.setSelected(true);
    } else {
      dumpFrequency.setDisable(true);
      saveDumps.setSelected(false);
    }
    loadPlayers.setSelected(PersistentSettings.getLoadPlayers());
    biomeColors.setSelected(scene.biomeColorsEnabled());
    saveSnapshots.setSelected(scene.shouldSaveSnapshots());
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    loadPlayers.setTooltip(new Tooltip("Enable/disable player entity loading. "
        + "Reload the chunks after changing this option."));
    loadPlayers.selectedProperty().addListener((observable, oldValue, newValue) -> {
      PersistentSettings.setLoadPlayers(newValue);
      attachTooltip(loadPlayers, reloadHint);
    });
    biomeColors.setTooltip(new Tooltip("Colors grass and tree leaves according to biome."));
    biomeColors.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.setBiomeColorsEnabled(newValue);
    });
    dumpFrequency.setConverter(new NumberStringConverter());
    dumpFrequency.getItems().addAll(50, 100, 500, 1000, 2500, 5000);
    dumpFrequency.setValue(Scene.DEFAULT_DUMP_FREQUENCY);
    dumpFrequency.setEditable(true);
    dumpFrequency.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (saveDumps.isSelected()) {
        scene.setDumpFrequency(newValue.intValue());
      } else {
        scene.setDumpFrequency(0);
      }
    });
    saveDumps.selectedProperty().addListener((observable, oldValue, enable) -> {
      dumpFrequency.setDisable(!enable);
      if (enable) {
        Number frequency = dumpFrequency.getValue();
        if (frequency != null) {
          scene.setDumpFrequency(frequency.intValue());
        }
      } else {
        scene.setDumpFrequency(0);
      }
    });
    saveSnapshots.selectedProperty().addListener((observable1, oldValue1, newValue1) -> {
      scene.setSaveSnapshots(newValue1);
    });
    canvasSizeLbl.setGraphic(new ImageView(Icon.scale.fxImage()));
    canvasSize.setEditable(true);
    canvasSize.getItems().addAll("400x400", "1024x768", "960x540", "1920x1080");
    canvasSize.valueProperty().addListener(canvasSizeListener);
    yCutoff.setName("Y cutoff");
    yCutoff.setTooltip(
        "Blocks below the Y cutoff are not loaded. Requires reloading chunks to take effect.");
    yCutoff.onValueChange(value -> {
      PersistentSettings.setYCutoff(value);
      attachTooltip(yCutoff, reloadHint);
    });
    loadSceneBtn.setTooltip(new Tooltip("This replaces the current scene!"));
    loadSceneBtn.setGraphic(new ImageView(Icon.load.fxImage()));
    loadSceneBtn.setOnAction(e -> {
      try {
        SceneChooser chooser = new SceneChooser(chunkyFxController);
        chooser.show();
      } catch (IOException e1) {
        Log.error("Failed to create scene chooser window.", e1);
      }
    });
    openSceneDirBtn.setTooltip(
        new Tooltip("Open the directory where Chunky stores scene descriptions and renders."));
    openSceneDirBtn.setOnAction(e -> {
      try {
        if (Desktop.isDesktopSupported()) {
          File sceneDir = SceneDirectoryPicker.getCurrentSceneDirectory();
          if (sceneDir != null) {
            Desktop.getDesktop().open(sceneDir);
          }
        } else {
          Log.warn("Can not open system file browser.");
        }
      } catch (IOException e1) {
        Log.warn("Failed to open scene directory.", e1);
      }
    });
    loadSelectedChunks
        .setTooltip(new Tooltip("Load the chunks that are currently selected in the map view"));
    loadSelectedChunks.setOnAction(e -> controller.getSceneManager()
        .loadChunks(mapLoader.getWorld(), mapLoader.getChunkSelection().getSelection()));
    reloadChunks.setTooltip(new Tooltip("Reload all chunks in the scene."));
    reloadChunks.setGraphic(new ImageView(Icon.reload.fxImage()));
    reloadChunks.setOnAction(e -> controller.getSceneManager().reloadChunks());
    applySize.setTooltip(new Tooltip("Set the canvas size to the value in the field."));
    applySize.setOnAction(e -> updateCanvasSize());
    makeDefaultSize.setTooltip(new Tooltip("Make the current canvas size the default."));
    makeDefaultSize.setOnAction(e -> PersistentSettings
        .set3DCanvasSize(scene.canvasWidth(), scene.canvasHeight()));
    scale15.setTooltip(new Tooltip("Halve canvas width and height."));
    scale05.setOnAction(e -> {
      int width = scene.canvasWidth() / 2;
      int height = scene.canvasHeight() / 2;
      setCanvasSize(width, height);
    });
    scale15.setTooltip(new Tooltip("Multiply canvas width and height by 1.5."));
    scale15.setOnAction(e -> {
      int width = (int) (scene.canvasWidth() * 1.5);
      int height = (int) (scene.canvasHeight() * 1.5);
      setCanvasSize(width, height);
    });
    scale20.setTooltip(new Tooltip("Multiply canvas width and height by 2.0."));
    scale20.setOnAction(e -> {
      int width = scene.canvasWidth() * 2;
      int height = scene.canvasHeight() * 2;
      setCanvasSize(width, height);
    });
  }

  private void attachTooltip(Region node, Tooltip tooltip) {
    if (node.getScene() != null && node.getScene().getWindow() != null) {
      Point2D offset = node.localToScene(0, 0);
      tooltip
          .show(node, offset.getX() + node.getScene().getX() + node.getScene().getWindow().getX(),
              offset.getY() + node.getScene().getY() + node.getScene().getWindow().getY() + node
                  .getHeight());
    }
  }

  private void updateCanvasSize() {
    String size = canvasSize.getValue();
    try {
      Pattern regex = Pattern.compile("([0-9]+)[xX.*]([0-9]+)");
      Matcher matcher = regex.matcher(size);
      if (matcher.matches()) {
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        RenderCanvasFx canvas = fxController.getCanvas();
        if (canvas != null && canvas.isShowing()) {
          canvas.setCanvasSize(width, height);
        }
        scene.setCanvasSize(width, height);
      } else {
        Log.info("Failed to set canvas size: format must be <width>x<height>!");
      }
    } catch (NumberFormatException e1) {
      Log.info("Failed to set canvas size: invalid dimensions!");
    }
  }

  private void setCanvasSize(int width, int height) {
    // Updating the combo box value triggers canvas resizing.
    canvasSize.setValue(String.format("%dx%d", width, height));
  }

  public void setRenderController(RenderController controller) {
    this.controller = controller;
    this.scene = controller.getSceneManager().getScene();
  }

  public void setMapLoader(WorldMapLoader mapLoader) {
    this.mapLoader = mapLoader;
  }

  public void setFxController(RenderControlsFxController fxController) {
    this.fxController = fxController;
  }

  public void setChunkyFxController(ChunkyFxController chunkyFxController) {
    this.chunkyFxController = chunkyFxController;
  }
}
