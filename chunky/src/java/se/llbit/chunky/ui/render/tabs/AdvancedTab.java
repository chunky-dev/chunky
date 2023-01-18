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
package se.llbit.chunky.ui.render.tabs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.export.PictureExportFormat;
import se.llbit.chunky.renderer.export.PictureExportFormats;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.biome.BiomeStructure;
import se.llbit.chunky.ui.Adjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.builder.javafx.FxBuildableUi;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.ShutdownAlert;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.fxutil.Dialogs;
import se.llbit.log.Log;
import se.llbit.math.Octree;
import se.llbit.math.bvh.BVH;
import se.llbit.util.Registerable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AdvancedTab extends ScrollPane implements RenderControlsTab, Initializable {
  private RenderControlsFxController renderControls;
  private RenderController controller;
  private Scene scene;
  private LauncherSettings launcherSettings;
  private volatile boolean shutdownAfterCompletedRender;

  @FXML private FxBuildableUi chunkyUi;

  @FXML private HBox outputModeBox;
  @FXML private ChoiceBox<PictureExportFormat> outputMode;
  @FXML private HBox octreeImplementationBox;
  @FXML private ChoiceBox<String> octreeImplementation;
  @FXML private HBox bvhMethodBox;
  @FXML private ChoiceBox<String> bvhMethod;
  @FXML private HBox biomeStructureImplementationBox;
  @FXML private ChoiceBox<String> biomeStructureImplementation;

  private Button mergeRenderDump;

  public AdvancedTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("AdvancedTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    launcherSettings = new LauncherSettings();
    launcherSettings.load();

    outputMode.getItems().addAll(PictureExportFormats.getFormats());
    outputMode.getSelectionModel().select(PictureExportFormats.PNG);
    outputMode.setConverter(new StringConverter<PictureExportFormat>() {
      @Override
      public String toString(PictureExportFormat object) {
        return object == null ? null : object.getName();
      }

      @Override
      public PictureExportFormat fromString(String string) {
        return PictureExportFormats.getFormat(string).orElse(PictureExportFormats.PNG);
      }
    });
    outputMode.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> scene.setOutputMode(newValue));

    ArrayList<String> octreeNames = new ArrayList<>();
    StringBuilder tooltipTextBuilder = new StringBuilder();
    for(Map.Entry<String, Octree.ImplementationFactory> entry : Octree.getEntries()) {
      octreeNames.add(entry.getKey());
      tooltipTextBuilder.append(entry.getKey());
      tooltipTextBuilder.append(": ");
      tooltipTextBuilder.append(entry.getValue().getDescription());
      tooltipTextBuilder.append('\n');
    }
    tooltipTextBuilder.append("Requires reloading chunks to take effect.");
    octreeImplementation.getItems().addAll(octreeNames);
    octreeImplementation.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldvalue, newvalue) -> {
              scene.setOctreeImplementation(newvalue);
              PersistentSettings.setOctreeImplementation(newvalue);
            });
    octreeImplementation.setTooltip(new Tooltip(tooltipTextBuilder.toString()));

    ArrayList<String> bvhNames = new ArrayList<>();
    StringBuilder bvhMethodBuilder = new StringBuilder();
    for (BVH.Factory.BVHBuilder builder : BVH.Factory.getImplementations()) {
      bvhNames.add(builder.getName());
      bvhMethodBuilder.append(builder.getName());
      bvhMethodBuilder.append(": ");
      bvhMethodBuilder.append(builder.getDescription());
      bvhMethodBuilder.append('\n');
    }
    bvhMethodBuilder.append("Requires reloading chunks to take effect.");
    bvhMethod.getItems().addAll(bvhNames);
    bvhMethod.getSelectionModel().select(PersistentSettings.getBvhMethod());
    bvhMethod.getSelectionModel().selectedItemProperty()
            .addListener(((observable, oldValue, newValue) -> {
              PersistentSettings.setBvhMethod(newValue);
              scene.setBvhImplementation(newValue);
            }));
    bvhMethod.setTooltip(new Tooltip(bvhMethodBuilder.toString()));

    ArrayList<String> biomeStructureIds = new ArrayList<>();
    StringBuilder biomeStructureTooltipBuilder = new StringBuilder();
    for (Registerable entry : BiomeStructure.REGISTRY.values()) {
      biomeStructureIds.add(entry.getId());
      biomeStructureTooltipBuilder.append(entry.getName());
      biomeStructureTooltipBuilder.append(": ");
      biomeStructureTooltipBuilder.append(entry.getDescription());
      biomeStructureTooltipBuilder.append('\n');
    }
    biomeStructureTooltipBuilder.append("Requires reloading chunks to take effect.");
    biomeStructureImplementation.getItems().addAll(biomeStructureIds);
    biomeStructureImplementation.getSelectionModel().selectedItemProperty()
      .addListener((observable, oldvalue, newvalue) -> {
        scene.setBiomeStructureImplementation(newvalue);
        PersistentSettings.setBiomeStructureImplementation(newvalue);
      });
    biomeStructureImplementation.setTooltip(new Tooltip(biomeStructureTooltipBuilder.toString()));
  }

  @Override
  public void update(Scene scene) {
    chunkyUi.build(builder -> {
      builder.integerAdjuster()
        .setName("Render threads")
        .setTooltip("Number of rendering threads.")
        .setRange(1, 20)
        .setClamp(true, false)
        .set(PersistentSettings.getNumThreads())
        .addCallback(PersistentSettings::setNumRenderThreads);

      builder.integerAdjuster()
        .setName("CPU utilization")
        .setTooltip("CPU utilization percentage per render thread")
        .setRange(1, 100)
        .setClamp(true, true)
        .set(PersistentSettings.getCPULoad())
        .addCallback(v -> controller.getRenderManager().setCPULoad(v))
        .addCallback(PersistentSettings::setCPULoad);

      builder.separator();

      builder.integerAdjuster()
        .setName("Ray depth")
        .setTooltip("Sets the minimum recursive ray depth")
        .setRange(1, 25)
        .setClamp(true, false)
        .set(scene.getRayDepth())
        .addCallback(scene::setRayDepth);

      builder.separator();

      builder.button()
        .setText("Merge render dumps")
        .setTooltip("Merge an existing render dump with the current render.")
        .addCallback(b -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Merge Render Dumps");
          fileChooser
            .getExtensionFilters().add(new FileChooser.ExtensionFilter("Render dumps", "*.dump"));

          List<File> dumps = fileChooser.showOpenMultipleDialog(getScene().getWindow());
          if (dumps != null) {
            // TODO: remove cast.
            AsynchronousSceneManager sceneManager = ((AsynchronousSceneManager) controller.getSceneManager());
            for (File dump : dumps) {
              sceneManager.mergeRenderDump(dump);
            }
          }
        });

      builder.separator();

      builder.checkbox()
        .setName("Shutdown computer when render completes")
        .set(false)
        .setDisable(!ShutdownAlert.canShutdown())
        .addCallback(v -> shutdownAfterCompletedRender = v);

      builder.checkbox()
        .setName("Fast fog")
        .setTooltip("Enable faster fog rendering algorithm.")
        .set(scene.fog.fastFog())
        .addCallback(scene::setFastFog);

      builder.integerAdjuster()
        .setName("Sky cache resolution")
        .setTooltip("Resolution of the sky cache. Lower values will use less memory and improve performance but can cause sky artifacts.")
        .setRange(1, 4096)
        .setClamp(true, false)
        .set(128)
        .addCallback(value -> scene.sky().setSkyCacheResolution(value));

      builder.doubleAdjuster()
        .setName("Current animation time")
        .setTooltip("Current animation time in seconds, used for animated textures.")
        .setRange(0, 60)
        .setClamp(true, false)
        .set(scene.getAnimationTime())
        .addCallback(scene::setAnimationTime);

      builder.addNodeOrElse(outputModeBox, b -> Log.error("Failed to build `AdvancedTab.outputMode`"));

      builder.checkbox()
        .setName("Hide unknown blocks")
        .setTooltip("Hide unknown blocks instead of rendering them as question marks.")
        .set(scene.getHideUnknownBlocks())
        .addCallback(scene::setHideUnknownBlocks);

      builder.separator();

      builder.addNodeOrElse(octreeImplementationBox, b -> Log.error("Failed to build `AdvancedTab.octreeImplementation"));
      builder.addNodeOrElse(bvhMethodBox, b -> Log.error("Failed to build `AdvancedTab.bvhMethod`"));
      builder.addNodeOrElse(biomeStructureImplementationBox, b -> Log.error("Failed to build `AdvancedTab.biomeStructureImplementation"));

      builder.integerAdjuster()
        .setName("Emitter grid size")
        .setTooltip("Size of the cells of the emitter grid. The bigger, the more emitter will be sampled. Need the chunks to be reloaded to apply")
        .set(scene.getGridSize())
        .addCallback(scene::setGridSize)
        .addCallback(PersistentSettings::setGridSizeDefault)
        .addAfterChangeHandler(value -> {
          if (scene.getEmitterSamplingStrategy() != EmitterSamplingStrategy.NONE && scene.haveLoadedChunks()) {
            Alert warning = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
            warning.setContentText("The selected chunks need to be reloaded to update the emitter grid size.");
            warning.getButtonTypes().setAll(
              ButtonType.CANCEL,
              new ButtonType("Reload chunks", ButtonBar.ButtonData.FINISH));
            warning.setTitle("Chunk reload required");
            ButtonType result = warning.showAndWait().orElse(ButtonType.CANCEL);
            if (result.getButtonData() == ButtonBar.ButtonData.FINISH) {
              controller.getSceneManager().reloadChunks();
            }
          }
        });

      builder.checkbox()
        .setName("Prevent normal emitter when using emitter sampling")
        .setTooltip("Prevent usual emitter contribution when emitter sampling is used")
        .set(scene.isPreventNormalEmitterWithSampling())
        .addCallback(scene::setPreventNormalEmitterWithSampling)
        .addCallback(PersistentSettings::setPreventNormalEmitterWithSampling);

      builder.separator();

      builder.choiceBoxInput()
        .setName("Renderer:")
        .setTooltip("The renderer to use for rendering.")
        .addItems(controller.getRenderManager().getRenderers())
        .set(scene.getRenderer())
        .addCallback(r -> scene.setRenderer(r.getId()));

      builder.choiceBoxInput()
        .setName("Preview Renderer:")
        .setTooltip("The renderer to use for the preview.")
        .addItems(controller.getRenderManager().getPreviewRenderers())
        .set(scene.getPreviewRenderer())
        .addCallback(r -> scene.setPreviewRenderer(r.getId()));

      builder.separator();

      builder.checkbox()
        .setName("Show launcher when starting Chunky")
        .setTooltip("Opens the Chunky launcher when starting Chunky next time.")
        .set(launcherSettings.showLauncher)
        .addCallback(value -> {
          launcherSettings.load();
          launcherSettings.showLauncher = value;
          launcherSettings.save();
        });
    });

    outputMode.getSelectionModel().select(scene.getOutputMode());
    octreeImplementation.getSelectionModel().select(scene.getOctreeImplementation());
    bvhMethod.getSelectionModel().select(scene.getBvhImplementation());
    biomeStructureImplementation.getSelectionModel().select(scene.getBiomeStructureImplementation());
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
      if(shutdownAfterCompletedRender) {
        // TODO: rewrite the shutdown alert in JavaFX.
        new ShutdownAlert(null);
      }
    });
  }
}
