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

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.SunSamplingStrategy;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.ui.elements.AngleAdjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.fx.LuxColorPicker;
import se.llbit.fxutil.Dialogs;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LightingTab extends VBox implements RenderControlsTab, Initializable {
  private RenderControlsFxController controller;
  private Scene scene;

  @FXML private DoubleAdjuster skyEmittance;
  @FXML private DoubleAdjuster emitterIntensity;
  @FXML private DoubleAdjuster sunIntensity;
  @FXML private CheckBox drawSun;
  @FXML private CheckBox useFlatTexture;
  @FXML private ChoiceBox<SunSamplingStrategy> sunSamplingStrategy;
  @FXML private DoubleAdjuster sunRadius;
  @FXML private AngleAdjuster sunAzimuth;
  @FXML private AngleAdjuster sunAltitude;
  @FXML private LuxColorPicker sunColor;
  @FXML private ChoiceBox<EmitterSamplingStrategy> emitterSamplingStrategy;

  private final ChangeListener<Color> sunColorListener = (observable, oldValue, newValue) -> scene.sun().setColor(ColorUtil.fromFx(newValue));

  public LightingTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("LightingTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    skyEmittance.setName("Sky exposure");
    skyEmittance.setTooltip("Changes the exposure of the sky.");
    skyEmittance.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyEmittance.makeLogarithmic();
    skyEmittance.clampMin();
    skyEmittance.onValueChange(value -> scene.sky().setSkyEmittance(value));

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

    drawSun.selectedProperty().addListener((observable, oldValue, newValue) -> scene.sun().setDrawTexture(newValue));
    drawSun.setTooltip(new Tooltip("Draws the sun texture on top of the skymap."));

    useFlatTexture.selectedProperty().addListener((observable, oldValue, newValue) -> scene.sun().setUseFlatTexture(newValue));
    useFlatTexture.setTooltip(new Tooltip("Use a solid color as the sun texture."));

    sunSamplingStrategy.getItems().addAll(SunSamplingStrategy.values());
    sunSamplingStrategy.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> scene.setSunSamplingStrategy(newValue));
    sunSamplingStrategy.setTooltip(new Tooltip("Determines how the sun is sampled at each bounce."));

    sunIntensity.setName("Sunlight intensity");
    sunIntensity.setTooltip("Changes the intensity of sunlight. Only used when Sun Sampling Strategy is set to FAST or HIGH_QUALITY.");
    sunIntensity.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    sunIntensity.makeLogarithmic();
    sunIntensity.clampMin();
    sunIntensity.onValueChange(value -> scene.sun().setIntensity(value));

    sunRadius.setName("Sun size");
    sunRadius.setTooltip("Sun radius in degrees.");
    sunRadius.setRange(0.01, 20);
    sunRadius.clampMin();
    sunRadius.onValueChange(value -> scene.sun().setSunRadius(Math.toRadians(value)));

    sunColor.colorProperty().addListener(sunColorListener);

    sunAzimuth.setName("Sun azimuth");
    sunAzimuth.setTooltip("Changes the horizontal direction of the sun from a reference direction of East.");
    sunAzimuth.onValueChange(value -> scene.sun().setAzimuth(-QuickMath.degToRad(value)));

    sunAltitude.setName("Sun altitude");
    sunAltitude.setTooltip("Changes the vertical direction of the sun from a reference altitude of the horizon.");
    sunAltitude.onValueChange(value -> scene.sun().setAltitude(QuickMath.degToRad(value)));
  }

  @Override
  public void setController(RenderControlsFxController controller) {
    this.controller = controller;
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  @Override public void update(Scene scene) {
    skyEmittance.set(scene.sky().getSkyEmittance());
    emitterIntensity.set(scene.getEmitterIntensity());
    sunIntensity.set(scene.sun().getIntensity());
    sunRadius.set(Math.toDegrees(scene.sun().getSunRadius()));
    sunAzimuth.set(-QuickMath.radToDeg(scene.sun().getAzimuth()));
    sunAltitude.set(QuickMath.radToDeg(scene.sun().getAltitude()));
    sunSamplingStrategy.getSelectionModel().select(scene.getSunSamplingStrategy());
    drawSun.setSelected(scene.sun().getDrawTexture());
    useFlatTexture.setSelected(scene.sun().getUseFlatTexture());
    sunColor.colorProperty().removeListener(sunColorListener);
    sunColor.setColor(ColorUtil.toFx(scene.sun().getColor()));
    sunColor.colorProperty().addListener(sunColorListener);
    emitterSamplingStrategy.getSelectionModel().select(scene.getEmitterSamplingStrategy());
  }

  @Override public String getTabTitle() {
    return "Lighting";
  }

  @Override public VBox getTabContent() {
    return this;
  }
}
