/* Copyright (c) 2016 - 2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016 - 2021 Chunky contributors
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
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.export.PictureExportFormats;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.export.PictureExportFormat;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.chunky.ui.ShutdownAlert;
import se.llbit.math.Octree;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
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
  @FXML private IntegerAdjuster cacheResolution;
  @FXML private DoubleAdjuster animationTime;
  @FXML private ChoiceBox<PictureExportFormat> outputMode;
  @FXML private ChoiceBox<String> octreeImplementation;
  @FXML private IntegerAdjuster gridSize;
  @FXML private CheckBox preventNormalEmitterWithSampling;
  @FXML private ChoiceBox<String> rendererSelect;
  @FXML private ChoiceBox<String> previewSelect;

  public AdvancedTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("AdvancedTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    outputMode.getItems().addAll(PictureExportFormats.getFormats());
    cpuLoad.setName("CPU utilization");
    cpuLoad.setTooltip("CPU utilization percentage per render thread.");
    cpuLoad.setRange(1, 100);
    cpuLoad.clampBoth();
    cpuLoad.onValueChange(value -> {
      PersistentSettings.setCPULoad(value);
      controller.getRenderManager().setCPULoad(value);
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
              .getExtensionFilters().add(new FileChooser.ExtensionFilter("Render dumps", "*.dump"));
      File dump = fileChooser.showOpenDialog(getScene().getWindow());
      if(dump != null) {
        // TODO: remove cast.
        ((AsynchronousSceneManager) controller.getSceneManager()).mergeRenderDump(dump);
      }
    });
    outputMode.setConverter(new StringConverter<PictureExportFormat>() {
      @Override
      public String toString(PictureExportFormat object) {
        return object.getName();
      }

      @Override
      public PictureExportFormat fromString(String string) {
        return PictureExportFormats.getFormat(string).get();
      }
    });
    outputMode.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> scene.setOutputMode(newValue));
    if(!ShutdownAlert.canShutdown()) {
      shutdown.setDisable(true);
    }
    fastFog.setTooltip(new Tooltip("Enable faster fog rendering algorithm."));
    fastFog.selectedProperty()
            .addListener((observable, oldValue, newValue) -> scene.setFastFog(newValue));
    cacheResolution.setName("Sky cache resolution");
    cacheResolution.setTooltip("Resolution of the sky cache. Lower values will use less memory and improve performance but can cause sky artifacts.");
    cacheResolution.setRange(1, 4096);
    cacheResolution.clampMin();
    cacheResolution.set(128);
    cacheResolution.onValueChange(value -> {
      scene.sky().setSkyCacheResolution(value);
    });
    animationTime.setName("Current animation time");
    animationTime.setTooltip("Current animation time in seconds, used for animated textures.");
    animationTime.setRange(0, 60);
    animationTime.clampMin();
    animationTime.set(0);
    animationTime.onValueChange(value -> {
      scene.setAnimationTime(value);
    });
    renderThreads.setName("Render threads");
    renderThreads.setTooltip("Number of rendering threads.");
    renderThreads.setRange(1, 20);
    renderThreads.clampMin();
    renderThreads.onValueChange(value -> {
      PersistentSettings.setNumRenderThreads(value);
      renderControls.showPopup("This change takes effect after restarting Chunky.", renderThreads);
    });

    ArrayList<String> implNames = new ArrayList<>();
    StringBuilder tooltipTextBuilder = new StringBuilder();
    for(Map.Entry<String, Octree.ImplementationFactory> entry : Octree.getEntries()) {
      implNames.add(entry.getKey());
      tooltipTextBuilder.append(entry.getKey());
      tooltipTextBuilder.append(": ");
      tooltipTextBuilder.append(entry.getValue().getDescription());
      tooltipTextBuilder.append('\n');
    }
    tooltipTextBuilder.append("Requires reloading chunks to take effect.");
    octreeImplementation.getItems().addAll(implNames.toArray(new String[implNames.size()]));
    octreeImplementation.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldvalue, newvalue) -> {
              scene.setOctreeImplementation(newvalue);
              PersistentSettings.setOctreeImplementation(newvalue);
            });
    octreeImplementation.setTooltip(new Tooltip(
            tooltipTextBuilder.toString()
    ));

    gridSize.setRange(4, 64);
    gridSize.setName("Emitter grid size");
    gridSize.setTooltip("Size of the cells of the emitter grid. " +
            "The bigger, the more emitter will be sampled. " +
            "Need the chunks to be reloaded to apply");
    gridSize.onValueChange(value -> {
      scene.setGridSize(value);
      PersistentSettings.setGridSizeDefault(value);
    });

    preventNormalEmitterWithSampling.setTooltip(new Tooltip("Prevent usual emitter contribution when emitter sampling is used"));
    preventNormalEmitterWithSampling.selectedProperty().addListener((observable, oldvalue, newvalue) -> {
      scene.setPreventNormalEmitterWithSampling(newvalue);
      PersistentSettings.setPreventNormalEmitterWithSampling(newvalue);
    });

    rendererSelect.setTooltip(new Tooltip("The renderer to use on a final render."));
    rendererSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        controller.getRenderManager().setRenderer(newValue));

    previewSelect.setTooltip(new Tooltip("The renderer to use when previewing."));
    previewSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        controller.getRenderManager().setPreviewRenderer(newValue));
  }

  public boolean shutdownAfterCompletedRender() {
    return shutdown.isSelected();
  }

  @Override
  public void update(Scene scene) {
    outputMode.getSelectionModel().select(scene.getOutputMode());
    fastFog.setSelected(scene.fastFog());
    renderThreads.set(PersistentSettings.getNumThreads());
    cpuLoad.set(PersistentSettings.getCPULoad());
    rayDepth.set(scene.getRayDepth());
    octreeImplementation.getSelectionModel().select(scene.getOctreeImplementation());
    gridSize.set(scene.getGridSize());
    preventNormalEmitterWithSampling.setSelected(scene.isPreventNormalEmitterWithSampling());
    animationTime.set(scene.getAnimationTime());
  }

  @Override
  public String getTabTitle() {
    return "Advanced";
  }

  @Override
  public Node getTabContent() {
    return this;
  }

  @Override
  public void setController(RenderControlsFxController controls) {
    this.renderControls = controls;
    this.controller = controls.getRenderController();
    scene = controller.getSceneManager().getScene();
    controller.getRenderManager().setOnRenderCompleted((time, sps) -> {
      if(shutdownAfterCompletedRender()) {
        // TODO: rewrite the shutdown alert in JavaFX.
        new ShutdownAlert(null);
      }
    });

    if (rendererSelect.getItems().isEmpty()) {
      RenderManager renderManager = controller.getRenderManager();
      rendererSelect.getItems().addAll(renderManager.getRenderers());
      rendererSelect.getSelectionModel().select(renderManager.getRendererName());
      previewSelect.getItems().addAll(renderManager.getPreviewRenderers());
      previewSelect.getSelectionModel().select(renderManager.getPreviewRendererName());
    }
  }
}
