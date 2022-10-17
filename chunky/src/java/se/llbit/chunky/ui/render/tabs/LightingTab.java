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
  @FXML private DoubleAdjuster apparentSkyIntensity;
  @FXML private DoubleAdjuster emitterIntensity;
  @FXML private DoubleAdjuster sunIntensity;
  @FXML private CheckBox drawSun;
  @FXML private ComboBox<SunSamplingStrategy> sunSamplingStrategy;
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
    skyExposure.setTooltip("Changes the exposure of the sky");
    skyExposure.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyExposure.makeLogarithmic();
    skyExposure.clampMin();
    skyExposure.onValueChange(value -> scene.sky().setSkyExposure(value));

    skyIntensity.setName("Sky brightness modifier");
    skyIntensity.setTooltip("Modifies the intensity of the sky light");
    skyIntensity.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyIntensity.makeLogarithmic();
    skyIntensity.clampMin();
    skyIntensity.onValueChange(value -> scene.sky().setSkyLight(value));

    apparentSkyIntensity.setName("Apparent sky brightness modifier");
    apparentSkyIntensity.setTooltip("Modifies the apparent brightness of the sky");
    apparentSkyIntensity.setRange(Sky.MIN_APPARENT_INTENSITY, Sky.MAX_APPARENT_INTENSITY);
    apparentSkyIntensity.makeLogarithmic();
    apparentSkyIntensity.clampMin();
    apparentSkyIntensity.onValueChange(value -> scene.sky().setApparentSkyLight(value));

    emitterIntensity.setName("Emitter intensity");
    emitterIntensity.setTooltip("Light intensity modifier for emitters");
    emitterIntensity.setRange(Scene.MIN_EMITTER_INTENSITY, Scene.MAX_EMITTER_INTENSITY);
    emitterIntensity.makeLogarithmic();
    emitterIntensity.clampMin();
    emitterIntensity.onValueChange(value -> scene.setEmitterIntensity(value));

    drawSun.selectedProperty().addListener((observable, oldValue, newValue) -> scene.sun().setDrawTexture(newValue));
    drawSun.setTooltip(new Tooltip("Draws the sun texture on top of the skymap."));

    sunSamplingStrategy.getItems().addAll(SunSamplingStrategy.values());
    sunSamplingStrategy.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> scene.setSunSamplingStrategy(newValue));
    sunSamplingStrategy.setTooltip(new Tooltip("Determine how the sun is sampled at each bounce."));

    apparentSunBrightness.setName("Apparent sun brightness");
    apparentSunBrightness.setTooltip("Apparent brightness of the sun texture");
    apparentSunBrightness.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    apparentSunBrightness.makeLogarithmic();
    apparentSunBrightness.clampMin();
    apparentSunBrightness.onValueChange(value -> scene.sun().setApparentBrightness(value));

    sunIntensity.setName("Sun intensity");
    sunIntensity.setTooltip("Sunlight intensity modifier.");
    sunIntensity.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    sunIntensity.makeLogarithmic();
    sunIntensity.clampMin();
    sunIntensity.onValueChange(value -> scene.sun().setIntensity(value));

    sunLuminosity.setName("Sun luminosity");
    sunLuminosity.setTooltip("Absolute brightness of the sun. Only used when Sun Sampling Strategy is set to OFF or HIGH_QUALITY.");
    sunLuminosity.setRange(1, 10000);
    sunLuminosity.makeLogarithmic();
    sunLuminosity.clampMin();
    sunLuminosity.onValueChange(value -> scene.sun().setLuminosity(value));

    sunAzimuth.setName("Sun azimuth");
    sunAzimuth.setTooltip("The horizontal direction of the sun from a reference direction of East.");
    sunAzimuth.onValueChange(value -> scene.sun().setAzimuth(-QuickMath.degToRad(value)));

    sunAltitude.setName("Sun altitude");
    sunAltitude.setTooltip("The vertical direction of the sun from a reference altitude of the horizon.");
    sunAltitude.onValueChange(value -> scene.sun().setAltitude(QuickMath.degToRad(value)));

    enableEmitters.setTooltip(new Tooltip("Allow blocks to emit light based on their material settings."));
    enableEmitters.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setEmittersEnabled(newValue));

    sunColor.colorProperty().addListener(sunColorListener);

    modifySunTexture.setTooltip(new Tooltip("Change whether the the color of the sun texture is modified by the apparent sun color"));
    modifySunTexture.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.sun().setEnableTextureModification(newValue);
      apparentSunColor.setDisable(!newValue);
    });

    apparentSunColor.setDisable(true);
    apparentSunColor.colorProperty().addListener(apparentSunColorListener);

    sunRadius.setName("Sun radius");
    sunRadius.setTooltip("Radius of the sun");
    sunRadius.setRange(0.01, 10);
    sunRadius.clampMin();
    sunRadius.onValueChange(value -> scene.sun().setSunRadius(value));

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
    emitterSamplingStrategy.setTooltip(new Tooltip("Determine how emitters are sampled at each bounce"));
  }

  @Override
  public void setController(RenderControlsFxController controller) {
    this.controller = controller;
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  @Override public void update(Scene scene) {
    skyExposure.set(scene.sky().getSkyExposure());
    skyIntensity.set(scene.sky().getSkyLight());
    apparentSkyIntensity.set(scene.sky().getApparentSkyLight());
    emitterIntensity.set(scene.getEmitterIntensity());
    sunIntensity.set(scene.sun().getIntensity());
    sunLuminosity.set(scene.sun().getLuminosity());
    apparentSunBrightness.set(scene.sun().getApparentBrightness());
    sunRadius.set(scene.sun().getSunRadius());
    modifySunTexture.setSelected(scene.sun().getEnableTextureModification());
    sunAzimuth.set(-QuickMath.radToDeg(scene.sun().getAzimuth()));
    sunAltitude.set(QuickMath.radToDeg(scene.sun().getAltitude()));
    enableEmitters.setSelected(scene.getEmittersEnabled());
    sunSamplingStrategy.getSelectionModel().select(scene.getSunSamplingStrategy());
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
