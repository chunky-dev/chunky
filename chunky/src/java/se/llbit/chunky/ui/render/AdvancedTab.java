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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.chunky.ui.ShutdownAlert;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedTab extends ScrollPane implements RenderControlsTab, Initializable {
  private RenderControlsFxController renderControls;
  private RenderController controller;
  private Scene scene;

  @FXML private IntegerAdjuster renderThreads;
  @FXML private IntegerAdjuster cpuLoad;
  @FXML private IntegerAdjuster rayDepth;
  @FXML private Button mergeRenderDump;
  @FXML private CheckBox shutdown;
  @FXML private CheckBox fastFog;
  @FXML private ChoiceBox<OutputMode> outputMode;

  public AdvancedTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("AdvancedTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    outputMode.getItems().addAll(OutputMode.values());
    cpuLoad.setName("CPU utilization");
    cpuLoad.setTooltip("CPU utilization percentage per render thread.");
    cpuLoad.setRange(1, 100);
    cpuLoad.clampBoth();
    cpuLoad.onValueChange(value -> {
      PersistentSettings.setCPULoad(value);
      controller.getRenderer().setCPULoad(value);
    });
    rayDepth.setName("Ray depth");
    rayDepth.setTooltip("Sets the minimum recursive ray depth.");
    rayDepth.setRange(1, 25);
    rayDepth.clampMin();
    rayDepth.onValueChange(value -> scene.setRayDepth(value));
    mergeRenderDump
        .setTooltip(new Tooltip("Merge an existing render dump with the current render."));
    mergeRenderDump.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Merge Render Dump");
      fileChooser
          .setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Render dumps", "*.dump"));
      File dump = fileChooser.showOpenDialog(getScene().getWindow());
      if (dump != null) {
        // TODO: remove cast.
        ((AsynchronousSceneManager) controller.getSceneManager()).mergeRenderDump(dump);
      }
    });
    outputMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> scene.setOutputMode(newValue));
    if (!ShutdownAlert.canShutdown()) {
      shutdown.setDisable(true);
    }
    fastFog.setTooltip(new Tooltip("Enable faster fog rendering algorithm."));
    fastFog.selectedProperty()
        .addListener((observable, oldValue, newValue) -> scene.setFastFog(newValue));
    renderThreads.setName("Render threads");
    renderThreads.setTooltip("Number of rendering threads.");
    renderThreads.setRange(1, 20);
    renderThreads.clampMin();
    renderThreads.onValueChange(value -> {
      PersistentSettings.setNumRenderThreads(value);
      renderControls.showPopup("This change takes effect after restarting Chunky.", renderThreads);
    });
  }

  public boolean shutdownAfterCompletedRender() {
    return shutdown.isSelected();
  }

  @Override public void update(Scene scene) {
    outputMode.getSelectionModel().select(scene.getOutputMode());
    fastFog.setSelected(scene.fastFog());
    renderThreads.set(PersistentSettings.getNumThreads());
    cpuLoad.set(PersistentSettings.getCPULoad());
    rayDepth.set(scene.getRayDepth());
  }

  @Override public String getTabTitle() {
    return "Advanced";
  }

  @Override public Node getTabContent() {
    return this;
  }

  @Override public void setController(RenderControlsFxController controls) {
    this.renderControls = controls;
    this.controller = controls.getRenderController();
    scene = controller.getSceneManager().getScene();
    controller.getRenderer().setOnRenderCompleted((time, sps) -> {
      if (shutdownAfterCompletedRender()) {
        // TODO: rewrite the shutdown alert in JavaFX.
        new ShutdownAlert(null);
      }
    });
  }
}
