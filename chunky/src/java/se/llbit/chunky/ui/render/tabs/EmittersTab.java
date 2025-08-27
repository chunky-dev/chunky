/* Copyright (c) 2016-2022 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2022 Chunky Contributors
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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.EmitterMappingType;
import se.llbit.chunky.renderer.scene.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.Adjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.fxutil.Dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmittersTab extends RenderControlsTab implements Initializable {
  @FXML private DoubleAdjuster emitterIntensity;
  @FXML private ChoiceBox<EmitterMappingType> emitterMappingType;
  @FXML private DoubleAdjuster emitterMappingExponent;
  @FXML private ChoiceBox<EmitterSamplingStrategy> emitterSamplingStrategy;
  @FXML private IntegerAdjuster gridSize;
  @FXML private ToggleSwitch preventNormalEmitterWithSampling;

  public EmittersTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("EmittersTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    emitterIntensity.setName("Emitter intensity");
    emitterIntensity.setTooltip("Modifies the intensity of emitter light.");
    emitterIntensity.setRange(Scene.MIN_EMITTER_INTENSITY, Scene.MAX_EMITTER_INTENSITY);
    emitterIntensity.makeLogarithmic();
    emitterIntensity.clampMin();
    emitterIntensity.onValueChange(value -> scene.setEmitterIntensity(value));

    emitterSamplingStrategy.getItems().addAll(EmitterSamplingStrategy.values());
    emitterSamplingStrategy.getSelectionModel().selectedItemProperty()
      .addListener((observable, oldvalue, newvalue) -> {
        scene.setEmitterSamplingStrategy(newvalue);
        if (newvalue != EmitterSamplingStrategy.NONE && scene.getEmitterGrid() == null && scene.haveLoadedChunks()) {
          Alert warning = Dialogs.createAlert(AlertType.CONFIRMATION);
          warning.setContentText("The selected chunks need to be reloaded in order for emitter sampling to work.");
          warning.getButtonTypes().setAll(
            ButtonType.CANCEL,
            new ButtonType("Reload chunks", ButtonData.FINISH));
          warning.setTitle("Chunk reload required");
          ButtonType result = warning.showAndWait().orElse(ButtonType.CANCEL);
          if (result.getButtonData() == ButtonData.FINISH) {
            controller.getRenderController().getSceneManager().reloadChunks();
          }
        }
      });
    emitterSamplingStrategy.setTooltip(new Tooltip("Determine how emitters are sampled at each bounce."));

    gridSize.setRange(4, 64);
    gridSize.setName("Emitter grid size");
    gridSize.setTooltip("Size of the cells of the emitter grid. " +
        "The bigger, the more emitter will be sampled. " +
        "Need the chunks to be reloaded to apply");
    gridSize.onValueChange(value -> {
      scene.setGridSize(value);
      PersistentSettings.setGridSizeDefault(value);
    });
    gridSize.addEventHandler(Adjuster.AFTER_VALUE_CHANGE, e -> {
      if (scene.getEmitterSamplingStrategy() != EmitterSamplingStrategy.NONE && scene.haveLoadedChunks()) {
        Alert warning = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
        warning.setContentText("The selected chunks need to be reloaded to update the emitter grid size.");
        warning.getButtonTypes().setAll(
            ButtonType.CANCEL,
            new ButtonType("Reload chunks", ButtonBar.ButtonData.FINISH));
        warning.setTitle("Chunk reload required");
        ButtonType result = warning.showAndWait().orElse(ButtonType.CANCEL);
        if (result.getButtonData() == ButtonBar.ButtonData.FINISH) {
          controller.getRenderController().getSceneManager().reloadChunks();
        }
      }
    });

    preventNormalEmitterWithSampling.setTooltip(new Tooltip("Prevent usual emitter contribution when emitter sampling is used"));
    preventNormalEmitterWithSampling.selectedProperty().addListener((observable, oldvalue, newvalue) -> {
      scene.setPreventNormalEmitterWithSampling(newvalue);
      PersistentSettings.setPreventNormalEmitterWithSampling(newvalue);
    });

    emitterMappingType.getItems().addAll(EmitterMappingType.values());
    emitterMappingType.getItems().remove(EmitterMappingType.NONE);
    emitterMappingType.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> scene.setEmitterMappingType(newValue));
    emitterMappingType.setTooltip(new Tooltip("Determines how per-pixel light emission is computed."));

    emitterMappingExponent.setName("Emitter mapping exponent");
    emitterMappingExponent.setTooltip("Determines how much light is emitted from darker or lighter pixels.\nHigher values will result in darker pixels emitting less light.");
    emitterMappingExponent.setRange(Scene.MIN_EMITTER_MAPPING_EXPONENT, Scene.MAX_EMITTER_MAPPING_EXPONENT);
    emitterMappingExponent.clampMin();
    emitterMappingExponent.onValueChange(value -> scene.setEmitterMappingExponent(value));
  }

  @Override public void update(Scene scene) {
    emitterIntensity.set(scene.getEmitterIntensity());
    emitterSamplingStrategy.getSelectionModel().select(scene.getEmitterSamplingStrategy());
    gridSize.set(scene.getGridSize());
    preventNormalEmitterWithSampling.setSelected(scene.isPreventNormalEmitterWithSampling());
    emitterMappingExponent.set(scene.getEmitterMappingExponent());
    emitterMappingType.getSelectionModel().select(scene.getEmitterMappingType());
  }

  @Override public String getTabTitle() {
    return "Emitters";
  }

  @Override public VBox getTabContent() {
    return this;
  }
}
