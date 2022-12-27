/* Copyright (c) 2016 - 2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016 - 2022 Chunky contributors
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
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SimulatedSky;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.elements.GradientEditor;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.ui.render.settings.SkyboxSettings;
import se.llbit.chunky.ui.render.settings.SkymapSettings;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector4;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SkyTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  @FXML private ChoiceBox<Sky.SkyMode> skyMode;
  @FXML private TitledPane detailsPane;
  @FXML private VBox skyModeSettings;
  @FXML private CheckBox transparentSkyEnabled;
  @FXML private DoubleAdjuster skyExposure;
  @FXML private DoubleAdjuster skyIntensity;
  @FXML private DoubleAdjuster apparentSkyBrightness;
  @FXML private CheckBox cloudsEnabled;
  @FXML private TitledPane cloudDetailsPane;
  @FXML private DoubleAdjuster cloudSize;
  @FXML private DoubleAdjuster cloudX;
  @FXML private DoubleAdjuster cloudY;
  @FXML private DoubleAdjuster cloudZ;
  @FXML private DoubleAdjuster fogDensity;
  @FXML private DoubleAdjuster skyFogDensity;
  @FXML private LuxColorPicker fogColor;
  private final VBox simulatedSettings = new VBox();
  private DoubleAdjuster horizonOffset = new DoubleAdjuster();
  private ChoiceBox<SimulatedSky> simulatedSky = new ChoiceBox<>();
  private final GradientEditor gradientEditor = new GradientEditor(this);
  private final LuxColorPicker colorPicker = new LuxColorPicker();
  private final VBox colorEditor = new VBox(colorPicker);
  private final SkyboxSettings skyboxSettings = new SkyboxSettings();
  private final SkymapSettings skymapSettings = new SkymapSettings();
  private ChangeListener<? super javafx.scene.paint.Color> fogColorListener =
      (observable, oldValue, newValue) -> scene.setFogColor(ColorUtil.fromFx(newValue));
  private ChangeListener<? super javafx.scene.paint.Color> skyColorListener =
      (observable, oldValue, newValue) -> scene.sky().setColor(ColorUtil.fromFx(newValue));
  private EventHandler<ActionEvent> simSkyListener = event -> {
    int selected = simulatedSky.getSelectionModel().getSelectedIndex();
    scene.sky().setSimulatedSkyMode(selected);
  };

  public SkyTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SkyTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
    skyboxSettings.setRenderController(controller.getRenderController());
    skymapSettings.setRenderController(controller.getRenderController());
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    simulatedSettings.getChildren().add(horizonOffset);
    horizonOffset.setName("Horizon offset");
    horizonOffset.setTooltip("Moves the simulated horizon.");
    horizonOffset.setRange(0, 1);
    horizonOffset.clampBoth();
    horizonOffset.onValueChange(value -> scene.sky().setHorizonOffset(value));

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

    cloudsEnabled.setSelected(false);
    cloudsEnabled.setTooltip(new Tooltip("Toggles visibility of Minecraft-style clouds."));
    cloudsEnabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.sky().setCloudsEnabled(newValue);
      cloudDetailsPane.setVisible(newValue);
      cloudDetailsPane.setExpanded(newValue);
      cloudDetailsPane.setManaged(newValue);
    });

    cloudDetailsPane.setVisible(cloudsEnabled.isSelected());
    cloudDetailsPane.setExpanded(cloudsEnabled.isSelected());
    cloudDetailsPane.setManaged(cloudsEnabled.isSelected());

    cloudSize.setTooltip("Changes the cloud size, measured in blocks per pixel of clouds.png texture");
    cloudSize.setRange(0.1, 128);
    cloudSize.clampMin();
    cloudSize.makeLogarithmic();
    cloudSize.onValueChange(value -> scene.sky().setCloudSize(value));

    cloudX.setTooltip("Changes the X-offset of the clouds.");
    cloudX.setRange(-256, 256);
    cloudX.onValueChange(value -> scene.sky().setCloudXOffset(value));
    cloudY.setTooltip("Changes the height of the clouds.");
    cloudY.setRange(-64, 320);
    cloudY.onValueChange(value -> scene.sky().setCloudYOffset(value));
    cloudZ.setTooltip("Changes the Z-offset of the clouds.");
    cloudZ.setRange(-256, 256);
    cloudZ.onValueChange(value -> scene.sky().setCloudZOffset(value));

    fogDensity.setTooltip("Changes the fog thickness. Set to 0 to disable volumetric fog effect.");
    fogDensity.setRange(0, 1);
    fogDensity.setMaximumFractionDigits(6);
    fogDensity.makeLogarithmic();
    fogDensity.clampMin();
    fogDensity.onValueChange(value -> {
      scene.setFogDensity(value);
    });
    fogDensity.valueProperty().addListener((observable, oldValue, newValue) -> {
      skyFogDensity.setDisable(newValue.doubleValue() == 0);
      fogColor.setDisable(newValue.doubleValue() == 0);
    });

    skyFogDensity.setTooltip("Changes how much the fog color is blended over the sky. Has no effect when fog is disabled.");
    skyFogDensity.setRange(0, 1);
    skyFogDensity.clampMin();
    skyFogDensity.onValueChange(value -> scene.setSkyFogDensity(value));
    skyFogDensity.setDisable(true);

    skyMode.setTooltip(new Tooltip("Sets the type of sky to be used in the scene."));
    skyMode.getItems().addAll(Sky.SkyMode.values());
    skyMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          scene.sky().setSkyMode(newValue);
          skyExposure.setDisable(newValue.equals(Sky.SkyMode.BLACK));
          skyIntensity.setDisable(newValue.equals(Sky.SkyMode.BLACK));
          apparentSkyBrightness.setDisable(newValue.equals(Sky.SkyMode.BLACK));
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
            case BLACK:
              skyModeSettings.getChildren().setAll(new Label("Selected mode has no settings."));
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
        });
    skyMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> detailsPane.setExpanded(true));
    transparentSkyEnabled
        .setTooltip(new Tooltip("Disables sky rendering for background compositing."));
    transparentSkyEnabled.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setTransparentSky(newValue));
    fogColor.colorProperty().addListener(fogColorListener);
    fogColor.setDisable(true);

    colorPicker.colorProperty().addListener(skyColorListener);
  }

  @Override public void update(Scene scene) {
    skyMode.getSelectionModel().select(scene.sky().getSkyMode());
    simulatedSky.setOnAction(null);
    simulatedSky.getSelectionModel().select(scene.sky().getSimulatedSky());
    simulatedSky.setOnAction(simSkyListener);
    skyExposure.set(scene.sky().getSkyExposure());
    skyIntensity.set(scene.sky().getSkyLight());
    apparentSkyBrightness.set(scene.sky().getApparentSkyLight());
    cloudsEnabled.setSelected(scene.sky().cloudsEnabled());
    transparentSkyEnabled.setSelected(scene.transparentSky());
    cloudSize.set(scene.sky().cloudSize());
    cloudX.set(scene.sky().cloudXOffset());
    cloudY.set(scene.sky().cloudYOffset());
    cloudZ.set(scene.sky().cloudZOffset());
    fogDensity.set(scene.getFogDensity());
    skyFogDensity.set(scene.getSkyFogDensity());
    fogColor.colorProperty().removeListener(fogColorListener);
    fogColor.setColor(ColorUtil.toFx(scene.getFogColor()));
    fogColor.colorProperty().addListener(fogColorListener);
    horizonOffset.set(scene.sky().getHorizonOffset());
    simulatedSky.setValue(scene.sky().getSimulatedSky());
    gradientEditor.setGradient(scene.sky().getGradient());
    colorPicker.colorProperty().removeListener(skyColorListener);
    colorPicker.setColor(ColorUtil.toFx(scene.sky().getColor()));
    colorPicker.colorProperty().addListener(skyColorListener);
    skyboxSettings.update(scene);
    skymapSettings.update(scene);
  }

  @Override public String getTabTitle() {
    return "Sky & Fog";
  }

  @Override public Node getTabContent() {
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
