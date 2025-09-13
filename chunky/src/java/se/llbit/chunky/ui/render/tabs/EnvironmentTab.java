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

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.scene.SunSamplingStrategy;
import se.llbit.chunky.renderer.scene.*;
import se.llbit.chunky.renderer.scene.sky.SimulatedSky;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.sky.Sky;
import se.llbit.chunky.renderer.scene.sky.Sun;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.elements.AngleAdjuster;
import se.llbit.chunky.ui.elements.GradientEditor;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.ui.render.settings.SkyboxSettings;
import se.llbit.chunky.ui.render.settings.SkymapSettings;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector4;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EnvironmentTab extends RenderControlsTab implements Initializable {
  @FXML private DoubleAdjuster skyEmittance;
  @FXML private ChoiceBox<Sky.SkyMode> skyMode;
  @FXML private TitledPane detailsPane;
  @FXML private VBox skyModeSettings;
  @FXML private CheckBox transparentSkyEnabled;

  @FXML private DoubleAdjuster sunIntensity;
  @FXML private ToggleSwitch drawSun;
  @FXML private ToggleSwitch useFlatTexture;
  @FXML private ChoiceBox<SunSamplingStrategy> sunSamplingStrategy;
  @FXML private DoubleAdjuster sunRadius;
  @FXML private AngleAdjuster sunAzimuth;
  @FXML private AngleAdjuster sunAltitude;
  @FXML private LuxColorPicker sunColor;

  private final VBox simulatedSettings = new VBox();
  private final ChoiceBox<SimulatedSky> simulatedSky = new ChoiceBox<>();
  private final GradientEditor gradientEditor = new GradientEditor(this);
  private final LuxColorPicker colorPicker = new LuxColorPicker();
  private final VBox colorEditor = new VBox(colorPicker);
  private final SkyboxSettings skyboxSettings = new SkyboxSettings();
  private final SkymapSettings skymapSettings = new SkymapSettings();

  private final ChangeListener<? super Color> skyColorListener =
      (observable, oldValue, newValue) -> scene.sky().setColor(ColorUtil.fromFx(newValue));

  private final ChangeListener<? super Color> sunColorListener =
      (observable, oldValue, newValue) -> scene.sun().setColor(ColorUtil.fromFx(newValue));
  private final EventHandler<ActionEvent> simSkyListener = event -> {
    int selected = simulatedSky.getSelectionModel().getSelectedIndex();
    scene.sky().setSimulatedSkyMode(selected);
    getSimulatedSkySettings();
  };

  private void getSimulatedSkySettings() {
    if (simulatedSettings.getChildren().size() == 2) {
      simulatedSettings.getChildren().remove(1);
    }
    simulatedSettings.getChildren().add(scene.sky().getSimulatedSky().getControls(this));
  }

  public EnvironmentTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("EnvironmentTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override protected void onSetController(RenderControlsFxController controller) {
    skyboxSettings.setRenderController(controller.getRenderController());
    skymapSettings.setRenderController(controller.getRenderController());
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    skyEmittance.setName("Sky intensity");
    skyEmittance.setTooltip("Changes the intensity of the sky light.");
    skyEmittance.setRange(Sky.MIN_INTENSITY, Sky.MAX_INTENSITY);
    skyEmittance.makeLogarithmic();
    skyEmittance.clampMin();
    skyEmittance.onValueChange(value -> scene.sky().setSkyEmittance(value));

    HBox simulatedSkyBox = new HBox(new Label("Sky Mode:"), simulatedSky);
    simulatedSkyBox.setSpacing(10);
    simulatedSkyBox.setAlignment(Pos.CENTER_LEFT);
    simulatedSettings.getChildren().add(0, simulatedSkyBox);
    simulatedSky.getItems().addAll(Sky.skies);
    simulatedSky.setValue(Sky.skies.get(0));
    simulatedSky.setConverter(new StringConverter<SimulatedSky>() {
      @Override
      public String toString(SimulatedSky object) {
        return object == null ? null : object.getName();
      }

      @Override
      public SimulatedSky fromString(String string) {
        for (SimulatedSky sky : simulatedSky.getItems()) {
          if (string.equals(sky.getName())) {
            return sky;
          }
        }
        return null;
      }
    });
    simulatedSky.setOnAction(simSkyListener);
    simulatedSky.setTooltip(new Tooltip(skiesTooltip(Sky.skies)));

    sunIntensity.setName("Sunlight intensity");
    sunIntensity.setTooltip("Changes the intensity of sunlight. Only used when Sun Sampling Strategy is set to FAST or HIGH_QUALITY.");
    sunIntensity.setRange(Sun.MIN_INTENSITY, Sun.MAX_INTENSITY);
    sunIntensity.makeLogarithmic();
    sunIntensity.clampMin();
    sunIntensity.onValueChange(value -> scene.sun().setIntensity(value));

    drawSun.selectedProperty().addListener((observable, oldValue, newValue) -> scene.sun().setDrawTexture(newValue));
    drawSun.setTooltip(new Tooltip("Draws the sun texture on top of the skymap."));

    useFlatTexture.selectedProperty().addListener((observable, oldValue, newValue) -> scene.sun().setUseFlatTexture(newValue));
    useFlatTexture.setTooltip(new Tooltip("Use a solid color as the sun texture."));

    sunSamplingStrategy.getItems().addAll(SunSamplingStrategy.values());
    sunSamplingStrategy.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, newValue) -> scene.setSunSamplingStrategy(newValue));
    sunSamplingStrategy.setTooltip(new Tooltip("Determines how the sun is sampled at each bounce."));

    sunRadius.setName("Sun size");
    sunRadius.setTooltip("Sun radius in degrees.");
    sunRadius.setRange(0.01, 20);
    sunRadius.clampMin();
    sunRadius.onValueChange(value -> scene.sun().setSunRadius(Math.toRadians(value)));

    sunAzimuth.setName("Sun azimuth");
    sunAzimuth.setTooltip("Changes the horizontal direction of the sun from a reference direction of East.");
    sunAzimuth.onValueChange(value -> scene.sun().setAzimuth(-QuickMath.degToRad(value)));

    sunAltitude.setName("Sun altitude");
    sunAltitude.setTooltip("Changes the vertical direction of the sun from a reference altitude of the horizon.");
    sunAltitude.onValueChange(value -> scene.sun().setAltitude(QuickMath.degToRad(value)));

    sunColor.setText("Sunlight color");
    sunColor.colorProperty().addListener(sunColorListener);

    skyMode.setTooltip(new Tooltip("Set the type of sky to be used in the scene."));
    skyMode.getItems().addAll(Sky.SkyMode.values());
    skyMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          scene.sky().setSkyMode(newValue);
          switch (newValue) {
            case SIMULATED:
              skyModeSettings.getChildren().setAll(simulatedSettings);
              break;
            case SOLID_COLOR:
              skyModeSettings.getChildren().setAll(colorEditor);
              break;
            case GRADIENT:
              skyModeSettings.getChildren().setAll(gradientEditor);
              break;
            case SKYBOX:
              skyModeSettings.getChildren().setAll(skyboxSettings);
              break;
            case SKYMAP_EQUIRECTANGULAR:
              skyModeSettings.getChildren().setAll(skymapSettings);
              skymapSettings.setPanoramic(true);
              break;
            case SKYMAP_ANGULAR:
              skyModeSettings.getChildren().setAll(skymapSettings);
              skymapSettings.setPanoramic(false);
              break;
            default:
              skyModeSettings.getChildren().setAll(new Label("" + newValue));
              break;
          }
          detailsPane.setExpanded(true);
        });
    transparentSkyEnabled
        .setTooltip(new Tooltip("Disables sky rendering for background compositing."));
    transparentSkyEnabled.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setTransparentSky(newValue));
    colorPicker.colorProperty().addListener(skyColorListener);
  }

  @Override public void update(Scene scene) {
    skyEmittance.set(scene.sky().getSkyEmittance());
    skyMode.getSelectionModel().select(scene.sky().getSkyMode());
    simulatedSky.setOnAction(null);
    simulatedSky.getSelectionModel().select(scene.sky().getSimulatedSky());
    simulatedSky.setOnAction(simSkyListener);
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
    getSimulatedSkySettings();
    transparentSkyEnabled.setSelected(scene.transparentSky());
    simulatedSky.setValue(scene.sky().getSimulatedSky());
    gradientEditor.setGradient(scene.sky().getGradient());
    colorPicker.colorProperty().removeListener(skyColorListener);
    colorPicker.setColor(ColorUtil.toFx(scene.sky().getColor()));
    colorPicker.colorProperty().addListener(skyColorListener);
    skyboxSettings.update(scene);
    skymapSettings.update(scene);
  }

  @Override public String getTabTitle() {
    return "Environment";
  }

  @Override public VBox getTabContent() {
    return this;
  }

  public void gradientChanged(List<Vector4> gradient) {
    scene.sky().setGradient(gradient);
  }

  private static String skiesTooltip(List<SimulatedSky> skies) {
    StringBuilder tipString = new StringBuilder("Sky Renderers:");
    for (SimulatedSky sky : skies) {
      tipString.append("\n");
      tipString.append(sky.getName());
      tipString.append(": ");
      tipString.append(sky.getDescription());
    }

    return tipString.toString();
  }
}
