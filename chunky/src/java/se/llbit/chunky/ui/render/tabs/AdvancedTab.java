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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.export.PictureExportFormat;
import se.llbit.chunky.renderer.export.PictureExportFormats;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.biome.BiomeStructure;
import se.llbit.chunky.ui.builder.UiBuilder;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.ShutdownAlert;
import se.llbit.chunky.ui.render.AbstractRenderControlsTab;
import se.llbit.fxutil.Dialogs;
import se.llbit.math.Octree;
import se.llbit.math.bvh.BVH;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdvancedTab extends AbstractRenderControlsTab {
  private final LauncherSettings launcherSettings;
  private volatile boolean shutdownAfterCompletedRender;

  public AdvancedTab() {
    super("Advanced");
    launcherSettings = new LauncherSettings();
    launcherSettings.load();
    shutdownAfterCompletedRender = false;
  }

  @Override
  public void build(UiBuilder builder) {
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

        List<File> dumps = fileChooser.showOpenMultipleDialog(ui.getScene().getWindow());
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

    builder.<PictureExportFormat>choiceBoxInput()
      .setName("Output mode:")
      .setTooltip(null)  // TODO
      .addItems(PictureExportFormats.getFormats())
      .set(PictureExportFormats.PNG)
      .setStringConverter(PictureExportFormat::getName)
      .setTooltipConverter(PictureExportFormat::getDescription)
      .addCallback(scene::setOutputMode);

    builder.checkbox()
      .setName("Hide unknown blocks")
      .setTooltip("Hide unknown blocks instead of rendering them as question marks.")
      .set(scene.getHideUnknownBlocks())
      .addCallback(scene::setHideUnknownBlocks);

    builder.separator();

    builder.<Map.Entry<String, Octree.ImplementationFactory>>choiceBoxInput()
      .setName("Octree implementation:")
      .setTooltip(null)  // TODO
      .addItems(Octree.getEntries())
      .select(impl -> Objects.equals(impl.getKey(), PersistentSettings.getOctreeImplementation()))
      .setStringConverter(Map.Entry::getKey)
      .setTooltipConverter(impl -> impl.getValue().getDescription())
      .addCallback(impl -> {
        scene.setOctreeImplementation(impl.getKey());
        PersistentSettings.setOctreeImplementation(impl.getKey());
      });

    builder.<BVH.Factory.BVHBuilder>choiceBoxInput()
      .setName("BVH build method:")
      .setTooltip("Set the BVH build method. Requires reloading chunks to take effect.")
      .addItems(BVH.Factory.getImplementations())
      .select(impl -> Objects.equals(impl.getName(), scene.getBvhImplementation()))
      .setStringConverter(BVH.Factory.BVHBuilder::getName)
      .setTooltipConverter(BVH.Factory.BVHBuilder::getDescription)
      .addCallback(impl -> {
        PersistentSettings.setBvhMethod(impl.getName());
        scene.setBvhImplementation(impl.getName());
      });

    builder.registerableChoiceBoxInput()
      .setName("BiomeStructure implementation:")
      .setTooltip("Set the BiomeStructure implementation. Requires reloading chunks to take effect.")
      .addItems(BiomeStructure.REGISTRY.values())
      .select(impl -> Objects.equals(impl.getId(), scene.getBiomeStructureImplementation()))
      .addCallback(impl -> scene.setBiomeStructureImplementation(impl.getId()));

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

    builder.registerableChoiceBoxInput()
      .setName("Renderer:")
      .setTooltip("The renderer to use for rendering.")
      .addItems(controller.getRenderManager().getRenderers())
      .select(impl -> Objects.equals(impl.getId(), scene.getRenderer()))
      .addCallback(impl -> scene.setRenderer(impl.getId()));

    builder.registerableChoiceBoxInput()
      .setName("Preview Renderer:")
      .setTooltip("The renderer to use for the preview.")
      .addItems(controller.getRenderManager().getPreviewRenderers())
      .select(impl -> Objects.equals(impl.getId(), scene.getPreviewRenderer()))
      .addCallback(impl -> scene.setPreviewRenderer(impl.getId()));

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
  }

  @Override
  public void setController(RenderControlsFxController fxController) {
    super.setController(fxController);

    controller.getRenderManager().setOnRenderCompleted((time, sps) -> {
      if(shutdownAfterCompletedRender) {
        // TODO: rewrite the shutdown alert in JavaFX.
        new ShutdownAlert(null);
      }
    });
  }
}
