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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
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

public class LightingTab extends ScrollPane implements RenderControlsTab, Initializable {
  private RenderControlsFxController controller;
  private Scene scene;

  @FXML private DoubleAdjuster skyExposure;
  @FXML private DoubleAdjuster skyIntensity;
  @FXML private DoubleAdjuster apparentSkyBrightness;
  @FXML private DoubleAdjuster emitterIntensity;
  @FXML private DoubleAdjuster sunIntensity;
  @FXML private CheckBox drawSun;
  @FXML private ComboBox<SunSamplingStrategy> sunSamplingStrategy;
  @FXML private TitledPane importanceSamplingDetailsPane;
  @FXML private DoubleAdjuster importanceSampleChance;
  @FXML private DoubleAdjuster importanceSampleRadius;
  @FXML private DoubleAdjuster sunLuminosity;
  @FXML private DoubleAdjuster apparentSunBrightness;
  @FXML private DoubleAdjuster sunRadius;
  @FXML private AngleAdjuster sunAzimuth;
  @FXML private AngleAdjuster sunAltitude;
  @FXML private CheckBox enableEmitters;
  @FXML private LuxColorPicker sunColor;
  @FXML private LuxColorPicker apparentSunColor;
  @FXML private CheckBox modifySunTexture;
  @FXML private ChoiceBox<EmitterSamplingStrategy> emitterSamplingStrategy;

  private ChangeListener<Color> sunColorListener = (observable, oldValue, newValue) -> scene.sun().setColor(ColorUtil.fromFx(newValue));

  private ChangeListener<Color> apparentSunColorListener = (observable, oldValue, newValue) -> scene.sun().setApparentColor(ColorUtil.fromFx(newValue));

  public LightingTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("LightingTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    skyExposure.setName("Sky exposure");
    skyExposure.setTooltip("Changes the exposure of the sky.");
    skyExposure.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyExposure.makeLogarithmic();
    skyExposure.clampMin();
    skyExposure.onValueChange(value -> scene.sky().setSkyExposure(value));

    skyIntensity.setName("Sky light intensity modifier");
    skyIntensity.setTooltip("Modifies the intensity of the light emitted by the sky.");
    skyIntensity.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyIntensity.makeLogarithmic();
    skyIntensity.clampMin();
    skyIntensity.onValueChange(value -> scene.sky().setSkyLight(value));

    apparentSkyBrightness.setName("Apparent sky brightness modifier");
    apparentSkyBrightness.setTooltip("Modifies the apparent brightness of the sky.");
    apparentSkyBrightness.setRange(Sky.MIN_APPARENT_INTENSITY, Sky.MAX_APPARENT_INTENSITY);
    apparentSkyBrightness.makeLogarithmic();
    apparentSkyBrightness.clampMin();
    apparentSkyBrightness.onValueChange(value -> scene.sky().setApparentSkyLight(value));

    enableEmitters.setTooltip(new Tooltip("Allow blocks to emit light based on their material settings."));
    enableEmitters.selectedProperty().addListener(
      (observable, oldValue, newValue) -> scene.setEmittersEnabled(newValue));

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

    sunSamplingStrategy.getItems().addAll(SunSamplingStrategy.values());
    // Hide HIGH_QUALITY in the GUI but leave it available through JSON editing/loading existing scenes
    sunSamplingStrategy.getItems().remove(SunSamplingStrategy.HIGH_QUALITY);
    sunSamplingStrategy.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
              scene.setSunSamplingStrategy(newValue);

              boolean visible = scene != null && scene.getSunSamplingStrategy().isImportanceSampling();
              importanceSamplingDetailsPane.setVisible(visible);
              importanceSamplingDetailsPane.setExpanded(visible);
              importanceSamplingDetailsPane.setManaged(visible);
            });
    sunSamplingStrategy.setTooltip(new Tooltip("Determines how the sun is sampled at each bounce."));

    boolean visible = scene != null && scene.getSunSamplingStrategy().isImportanceSampling();
    importanceSamplingDetailsPane.setVisible(visible);
    importanceSamplingDetailsPane.setExpanded(visible);
    importanceSamplingDetailsPane.setManaged(visible);

    importanceSampleChance.setName("Importance sample chance");
    importanceSampleChance.setTooltip("Probability of sampling the sun on each importance bounce");
    importanceSampleChance.setRange(Sun.MIN_IMPORTANCE_SAMPLE_CHANCE, Sun.MAX_IMPORTANCE_SAMPLE_CHANCE);
    importanceSampleChance.clampBoth();
    importanceSampleChance.onValueChange(value -> scene.sun().setImportanceSampleChance(value));

    importanceSampleRadius.setName("Importance sample radius");
    importanceSampleRadius.setTooltip("Radius of possible sun sampling bounces (relative to the sun's radius)");
    importanceSampleRadius.setRange(Sun.MIN_IMPORTANCE_SAMPLE_RADIUS, Sun.MAX_IMPORTANCE_SAMPLE_RADIUS);
    importanceSampleRadius.clampMin();
    importanceSampleRadius.onValueChange(value -> scene.sun().setImportanceSampleRadius(value));

    sunIntensity.setName("Sunlight intensity");
    sunIntensity.setTooltip("Changes the intensity of sunlight. Only used when Sun Sampling Strategy is set to FAST or HIGH_QUALITY.");
    sunIntensity.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    sunIntensity.makeLogarithmic();
    sunIntensity.clampMin();
    sunIntensity.onValueChange(value -> scene.sun().setIntensity(value));

    sunLuminosity.setName("Sun luminosity");
    sunLuminosity.setTooltip("Changes the absolute brightness of the sun. Only used when Sun Sampling Strategy is set to OFF or HIGH_QUALITY.");    sunLuminosity.setRange(1, 10000);
    sunLuminosity.makeLogarithmic();
    sunLuminosity.clampMin();
    sunLuminosity.onValueChange(value -> scene.sun().setLuminosity(value));

    apparentSunBrightness.setName("Apparent sun brightness");
    apparentSunBrightness.setTooltip("Changes the apparent brightness of the sun texture.");
    apparentSunBrightness.setRange(Sun.MIN_APPARENT_BRIGHTNESS, Sun.MAX_APPARENT_BRIGHTNESS);
    apparentSunBrightness.makeLogarithmic();
    apparentSunBrightness.clampMin();
    apparentSunBrightness.onValueChange(value -> scene.sun().setApparentBrightness(value));

    sunRadius.setName("Sun size");
    sunRadius.setTooltip("Sun radius in degrees.");
    sunRadius.setRange(0.01, 20);
    sunRadius.clampMin();
    sunRadius.onValueChange(value -> scene.sun().setSunRadius(Math.toRadians(value)));

    sunColor.colorProperty().addListener(sunColorListener);

    modifySunTexture.setTooltip(new Tooltip("Changes whether the the color of the sun texture is modified by the apparent sun color."));
    modifySunTexture.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.sun().setEnableTextureModification(newValue);
      apparentSunColor.setDisable(!newValue);
    });

    apparentSunColor.setDisable(true);
    apparentSunColor.colorProperty().addListener(apparentSunColorListener);

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
    skyExposure.set(scene.sky().getSkyExposure());
    skyIntensity.set(scene.sky().getSkyLight());
    apparentSkyBrightness.set(scene.sky().getApparentSkyLight());
    emitterIntensity.set(scene.getEmitterIntensity());
    sunIntensity.set(scene.sun().getIntensity());
    sunLuminosity.set(scene.sun().getLuminosity());
    apparentSunBrightness.set(scene.sun().getApparentBrightness());
    sunRadius.set(Math.toDegrees(scene.sun().getSunRadius()));
    modifySunTexture.setSelected(scene.sun().getEnableTextureModification());
    sunAzimuth.set(-QuickMath.radToDeg(scene.sun().getAzimuth()));
    sunAltitude.set(QuickMath.radToDeg(scene.sun().getAltitude()));
    enableEmitters.setSelected(scene.getEmittersEnabled());
    sunSamplingStrategy.getSelectionModel().select(scene.getSunSamplingStrategy());
    importanceSampleChance.set(scene.sun().getImportanceSampleChance());
    importanceSampleRadius.set(scene.sun().getImportanceSampleRadius());
    drawSun.setSelected(scene.sun().drawTexture());
    sunColor.colorProperty().removeListener(sunColorListener);
    sunColor.setColor(ColorUtil.toFx(scene.sun().getColor()));
    sunColor.colorProperty().addListener(sunColorListener);
    apparentSunColor.colorProperty().removeListener(apparentSunColorListener);
    apparentSunColor.setColor(ColorUtil.toFx(scene.sun().getApparentColor()));
    apparentSunColor.colorProperty().addListener(apparentSunColorListener);
    emitterSamplingStrategy.getSelectionModel().select(scene.getEmitterSamplingStrategy());
  }

  @Override public String getTabTitle() {
    return "Lighting";
  }

  @Override public Node getTabContent() {
    return this;
  }
}
