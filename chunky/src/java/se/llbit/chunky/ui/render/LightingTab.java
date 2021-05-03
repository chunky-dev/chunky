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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.ui.AngleAdjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
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

  @FXML private DoubleAdjuster skyIntensity;
  @FXML private DoubleAdjuster emitterIntensity;
  @FXML private DoubleAdjuster sunIntensity;
  @FXML private AngleAdjuster sunAzimuth;
  @FXML private AngleAdjuster sunAltitude;
  @FXML private CheckBox enableEmitters;
  @FXML private CheckBox enableSunlight;
  @FXML private CheckBox drawSun;
  @FXML private LuxColorPicker sunColor;
  @FXML private ChoiceBox<EmitterSamplingStrategy> emitterSamplingStrategy;

  private ChangeListener<Color> sunColorListener = (observable, oldValue, newValue) ->
      scene.sun().setColor(ColorUtil.fromFx(newValue));

  public LightingTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("LightingTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    skyIntensity.setName("Sky light");
    skyIntensity.setTooltip("Sky light intensity modifier");
    skyIntensity.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyIntensity.makeLogarithmic();
    skyIntensity.clampMin();
    skyIntensity.onValueChange(value -> scene.sky().setSkyLight(value));

    emitterIntensity.setName("Emitter intensity");
    emitterIntensity.setTooltip("Light intensity modifier for emitters");
    emitterIntensity.setRange(Scene.MIN_EMITTER_INTENSITY, Scene.MAX_EMITTER_INTENSITY);
    emitterIntensity.makeLogarithmic();
    emitterIntensity.clampMin();
    emitterIntensity.onValueChange(value -> scene.setEmitterIntensity(value));

    sunIntensity.setName("Sun intensity");
    sunIntensity.setTooltip("Sunlight intensity modifier");
    sunIntensity.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    sunIntensity.makeLogarithmic();
    sunIntensity.clampMin();
    sunIntensity.onValueChange(value -> scene.sun().setIntensity(value));

    sunAzimuth.setName("Sun azimuth");
    sunAzimuth.setTooltip("Change the angle to the sun from north.");
    sunAzimuth.onValueChange(value -> scene.sun().setAzimuth(-QuickMath.degToRad(value)));

    sunAltitude.setName("Sun altitude");
    sunAltitude.setTooltip("Change the angle to the sun above the horizon.");
    sunAltitude.onValueChange(value -> scene.sun().setAltitude(QuickMath.degToRad(value)));

    enableEmitters.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setEmittersEnabled(newValue));
    enableSunlight.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setDirectLight(newValue));
    drawSun.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.sun().setDrawTexture(newValue));
    drawSun.setTooltip(new Tooltip("Draws the sun texture on top of the skymap."));

    sunColor.colorProperty().addListener(sunColorListener);

    emitterSamplingStrategy.getItems().addAll(EmitterSamplingStrategy.values());
    emitterSamplingStrategy.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldvalue, newvalue) -> {
          scene.setEmitterSamplingStrategy(newvalue);
          if (newvalue != EmitterSamplingStrategy.NONE && scene.getEmitterGrid() == null) {
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
    skyIntensity.set(scene.sky().getSkyLight());
    emitterIntensity.set(scene.getEmitterIntensity());
    sunIntensity.set(scene.sun().getIntensity());
    sunAzimuth.set(-QuickMath.radToDeg(scene.sun().getAzimuth()));
    sunAltitude.set(QuickMath.radToDeg(scene.sun().getAltitude()));
    enableEmitters.setSelected(scene.getEmittersEnabled());
    enableSunlight.setSelected(scene.getDirectLight());
    drawSun.setSelected(scene.sun().drawTexture());
    sunColor.colorProperty().removeListener(sunColorListener);
    sunColor.setColor(ColorUtil.toFx(scene.sun().getColor()));
    sunColor.colorProperty().addListener(sunColorListener);
    emitterSamplingStrategy.getSelectionModel().select(scene.getEmitterSamplingStrategy());
  }

  @Override public String getTabTitle() {
    return "Lighting";
  }

  @Override public Node getTabContent() {
    return this;
  }
}
