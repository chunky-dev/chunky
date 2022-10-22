/* Copyright (c) 2022 Chunky Contributors
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
import javafx.scene.paint.Color;
import se.llbit.chunky.renderer.SunSamplingStrategy;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.ui.elements.AngleAdjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SunTab extends ScrollPane implements RenderControlsTab, Initializable {
  private RenderControlsFxController controller;
  private Scene scene;

  @FXML private ComboBox<SunSamplingStrategy> sunSamplingStrategy;
  @FXML private DoubleAdjuster sunIntensity;
  @FXML private DoubleAdjuster sunLuminosity;
  @FXML private DoubleAdjuster sunRadius;
  @FXML private LuxColorPicker sunlightColor;
  @FXML private CheckBox drawSun;
  @FXML private DoubleAdjuster apparentSunBrightness;
  @FXML private DoubleAdjuster apparentSunRadius;
  @FXML private CheckBox modifySunTexture;
  @FXML private LuxColorPicker apparentSunColor;
  @FXML private AngleAdjuster sunAzimuth;
  @FXML private AngleAdjuster sunAltitude;

  private ChangeListener<Color> sunlightColorListener = (observable, oldValue, newValue) -> scene.sun().setColor(ColorUtil.fromFx(newValue));

  private ChangeListener<Color> apparentSunColorListener = (observable, oldValue, newValue) -> scene.sun().setApparentColor(ColorUtil.fromFx(newValue));

  public SunTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SunTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    sunSamplingStrategy.getItems().addAll(SunSamplingStrategy.values());
    sunSamplingStrategy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      scene.setSunSamplingStrategy(newValue);
      sunIntensity.setDisable(!newValue.doSunSampling());
      sunLuminosity.setDisable(!newValue.isDiffuseSun());
      sunlightColor.setDisable(!newValue.doSunSampling() && !newValue.isDiffuseSun());
      sunRadius.setDisable(!newValue.isDiffuseSun());
    });
    sunSamplingStrategy.setTooltip(new Tooltip("Determines how the sun is sampled at each bounce."));

    sunIntensity.setName("Sunlight intensity");
    sunIntensity.setTooltip("Changes the intensity of sunlight. Only used when Sun Sampling Strategy is set to FAST or HIGH_QUALITY.");
    sunIntensity.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    sunIntensity.makeLogarithmic();
    sunIntensity.clampMin();
    sunIntensity.onValueChange(value -> scene.sun().setIntensity(value));
    sunIntensity.setDisable(false);

    sunLuminosity.setName("Sun luminosity");
    sunLuminosity.setTooltip("Changes the absolute brightness of the sun. Only used when Sun Sampling Strategy is set to OFF or HIGH_QUALITY.");    sunLuminosity.setRange(1, 10000);
    sunLuminosity.makeLogarithmic();
    sunLuminosity.clampMin();
    sunLuminosity.onValueChange(value -> scene.sun().setLuminosity(value));
    sunLuminosity.setDisable(true);

    sunRadius.setName("Sun size");
    sunRadius.setTooltip("Changes the absolute size of the sun. Only used when Sun Sampling Strategy is set to OFF or HIGH_QUALITY.");
    sunRadius.setRange(0.01, 10);
    sunRadius.clampMin();
    sunRadius.onValueChange(value -> scene.sun().setSunRadius(value));
    sunRadius.setDisable(true);

    sunlightColor.colorProperty().addListener(sunlightColorListener);
    sunlightColor.setDisable(false);

    drawSun.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.sun().setDrawTexture(newValue);
      apparentSunBrightness.setDisable(!newValue);
      apparentSunRadius.setDisable(!newValue);
      modifySunTexture.setDisable(!newValue);
      if (modifySunTexture.isSelected()) {
        apparentSunColor.setDisable(!newValue);
      }
    });
    drawSun.setTooltip(new Tooltip("Draws the sun texture on top of the skymap."));

    apparentSunBrightness.setName("Apparent sun brightness");
    apparentSunBrightness.setTooltip("Changes the apparent brightness of the sun texture.");
    apparentSunBrightness.setRange(Sun.MIN_APPARENT_BRIGHTNESS, Sun.MAX_APPARENT_BRIGHTNESS);
    apparentSunBrightness.makeLogarithmic();
    apparentSunBrightness.clampMin();
    apparentSunBrightness.onValueChange(value -> scene.sun().setApparentBrightness(value));
    apparentSunBrightness.setDisable(false);

    apparentSunRadius.setName("Apparent sun size");
    apparentSunRadius.setTooltip("Changes the apparent size of the sun texture.");
    apparentSunRadius.setRange(0.01, 10);
    apparentSunRadius.clampMin();
    apparentSunRadius.onValueChange(value -> scene.sun().setApparentSunRadius(value));
    apparentSunRadius.setDisable(false);

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

  @Override public void setController(RenderControlsFxController controller) {
    this.controller = controller;
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  @Override public void update(Scene scene) {
    sunSamplingStrategy.getSelectionModel().select(scene.getSunSamplingStrategy());
    sunIntensity.set(scene.sun().getIntensity());
    sunLuminosity.set(scene.sun().getLuminosity());
    sunRadius.set(scene.sun().getSunRadius());
    sunlightColor.colorProperty().removeListener(sunlightColorListener);
    sunlightColor.setColor(ColorUtil.toFx(scene.sun().getColor()));
    sunlightColor.colorProperty().addListener(sunlightColorListener);
    drawSun.setSelected(scene.sun().drawTexture());
    apparentSunBrightness.set(scene.sun().getApparentBrightness());
    apparentSunRadius.set(scene.sun().getApparentSunRadius());
    modifySunTexture.setSelected(scene.sun().getEnableTextureModification());
    apparentSunColor.colorProperty().removeListener(apparentSunColorListener);
    apparentSunColor.setColor(ColorUtil.toFx(scene.sun().getApparentColor()));
    apparentSunColor.colorProperty().addListener(apparentSunColorListener);
    sunAzimuth.set(-QuickMath.radToDeg(scene.sun().getAzimuth()));
    sunAltitude.set(QuickMath.radToDeg(scene.sun().getAltitude()));
  }

  @Override public String getTabTitle() {
    return "Sun";
  }

  @Override public Node getTabContent() {
    return this;
  }
}
