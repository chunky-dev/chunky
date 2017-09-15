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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.chunky.ui.SimpleColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LightingTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  private final Tab parentTab;
  @FXML private DoubleAdjuster skyIntensity;
  @FXML private DoubleAdjuster emitterIntensity;
  @FXML private DoubleAdjuster sunIntensity;
  @FXML private DoubleAdjuster sunAzimuth;
  @FXML private DoubleAdjuster sunAltitude;
  @FXML private CheckBox enableEmitters;
  @FXML private CheckBox enableSunlight;
  @FXML private SimpleColorPicker sunColor;

  private ChangeListener<Color> sunColorListener = (observable, oldValue, newValue) ->
      scene.sun().setColor(ColorUtil.fromFx(newValue));

  public LightingTab() throws IOException {
    parentTab = new Tab("Lighting", this);
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
    sunAzimuth.setTooltip("The angle to the sun from north");
    sunAzimuth.setRange(0, 360);
    sunAzimuth
        .onValueChange(value -> scene.sun().setAzimuth(QuickMath.degToRad(value)));

    sunAltitude.setName("Sun altitude");
    sunAltitude.setTooltip("The angle to the sun above the horizon");
    sunAltitude.setRange(0, 90);
    sunAltitude
        .onValueChange(value -> scene.sun().setAltitude(QuickMath.degToRad(value)));

    enableEmitters.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setEmittersEnabled(newValue));
    enableSunlight.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setDirectLight(newValue));

    sunColor.colorProperty().addListener(sunColorListener);
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  @Override public void update(Scene scene) {
    skyIntensity.set(scene.sky().getSkyLight());
    emitterIntensity.set(scene.getEmitterIntensity());
    sunIntensity.set(scene.sun().getIntensity());
    sunAzimuth.set(QuickMath.radToDeg(scene.sun().getAzimuth()));
    sunAltitude.set(QuickMath.radToDeg(scene.sun().getAltitude()));
    enableEmitters.setSelected(scene.getEmittersEnabled());
    enableSunlight.setSelected(scene.getDirectLight());
    sunColor.colorProperty().removeListener(sunColorListener);
    sunColor.setColor(ColorUtil.toFx(scene.sun().getColor()));
    sunColor.colorProperty().addListener(sunColorListener);
  }

  @Override public Tab getTab() {
    return parentTab;
  }
}
