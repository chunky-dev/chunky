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
  @FXML private CheckBox cloudsEnabled;
  @FXML private TitledPane cloudDetailsPane;
  @FXML private DoubleAdjuster cloudSizeX;
  @FXML private DoubleAdjuster cloudSizeY;
  @FXML private DoubleAdjuster cloudSizeZ;
  @FXML private DoubleAdjuster cloudOffsetX;
  @FXML private DoubleAdjuster cloudOffsetY;
  @FXML private DoubleAdjuster cloudOffsetZ;
  @FXML private LuxColorPicker cloudColor;
  @FXML private CheckBox enableVolumetricClouds;
  @FXML private DoubleAdjuster cloudDensity;
  private final VBox simulatedSettings = new VBox();
  private DoubleAdjuster horizonOffset = new DoubleAdjuster();
  private ChoiceBox<SimulatedSky> simulatedSky = new ChoiceBox<>();
  private final GradientEditor gradientEditor = new GradientEditor(this);
  private final LuxColorPicker colorPicker = new LuxColorPicker();
  private final VBox colorEditor = new VBox(colorPicker);
  private final SkyboxSettings skyboxSettings = new SkyboxSettings();
  private final SkymapSettings skymapSettings = new SkymapSettings();

  private ChangeListener<? super javafx.scene.paint.Color> skyColorListener =
      (observable, oldValue, newValue) -> scene.sky().setColor(ColorUtil.fromFx(newValue));

  private ChangeListener<? super javafx.scene.paint.Color> cloudColorListener =
    (observable, oldValue, newValue) -> scene.sky().setCloudColor(ColorUtil.fromFx(newValue));
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

    cloudsEnabled.setTooltip(new Tooltip("Toggle visibility of Minecraft-style clouds."));
    cloudsEnabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.sky().setCloudsEnabled(newValue);
      cloudDetailsPane.setVisible(newValue);
      cloudDetailsPane.setExpanded(newValue);
      cloudDetailsPane.setManaged(newValue);
    });

    cloudDetailsPane.setVisible(false);
    cloudDetailsPane.setExpanded(false);
    cloudDetailsPane.setManaged(false);

    cloudSizeX.setName("Cloud X scale");
    cloudSizeX.setTooltip("Scale of the X-dimension of the clouds, measured in blocks per pixel of clouds.png texture");
    cloudSizeX.setRange(0.01, 128);
    cloudSizeX.clampMin();
    cloudSizeX.makeLogarithmic();
    cloudSizeX.onValueChange(value -> scene.sky().setCloudSizeX(value));

    cloudSizeY.setName("Cloud Y scale");
    cloudSizeY.setTooltip("Scale of the Y-dimension of the clouds, measured in blocks per pixel of clouds.png texture");
    cloudSizeY.setRange(0.01, 128);
    cloudSizeY.clampMin();
    cloudSizeY.makeLogarithmic();
    cloudSizeY.onValueChange(value -> scene.sky().setCloudSizeY(value));

    cloudSizeZ.setName("Cloud Z scale");
    cloudSizeZ.setTooltip("Scale of the Z-dimension of the clouds, measured in blocks per pixel of clouds.png texture");
    cloudSizeZ.setRange(0.01, 128);
    cloudSizeZ.clampMin();
    cloudSizeZ.makeLogarithmic();
    cloudSizeZ.onValueChange(value -> scene.sky().setCloudSizeZ(value));

    cloudOffsetX.setName("Cloud X offset");
    cloudOffsetX.setTooltip("Changes the X-offset of the clouds.");
    cloudOffsetX.setRange(-256, 256);
    cloudOffsetX.onValueChange(value -> scene.sky().setCloudXOffset(value));

    cloudOffsetY.setName("Cloud Y offset");
    cloudOffsetY.setTooltip("Changes the altitude of the clouds.");
    cloudOffsetY.setRange(-64, 320);
    cloudOffsetY.onValueChange(value -> scene.sky().setCloudYOffset(value));

    cloudOffsetZ.setName("Cloud Z offset");
    cloudOffsetZ.setTooltip("Changes the Z-offset of the clouds.");
    cloudOffsetZ.setRange(-256, 256);
    cloudOffsetZ.onValueChange(value -> scene.sky().setCloudZOffset(value));

    cloudColor.colorProperty().addListener(cloudColorListener);

    enableVolumetricClouds.setTooltip(new Tooltip("Use a volume scatter for the cloud material."));
    enableVolumetricClouds.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.sky().setVolumetricClouds(newValue);
      cloudDensity.setDisable(!newValue);
    });

    cloudDensity.setName("Volumetric cloud density");
    cloudDensity.setRange(0.000001, 1);
    cloudDensity.clampMin();
    cloudDensity.setMaximumFractionDigits(6);
    cloudDensity.onValueChange(value -> scene.sky().setCloudDensity(value));
    cloudDensity.setDisable(true);

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
          detailsPane.setExpanded(true);
        });
    transparentSkyEnabled
        .setTooltip(new Tooltip("Disables sky rendering for background compositing."));
    transparentSkyEnabled.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.setTransparentSky(newValue));
    colorPicker.colorProperty().addListener(skyColorListener);
  }

  @Override public void update(Scene scene) {
    skyMode.getSelectionModel().select(scene.sky().getSkyMode());
    simulatedSky.setOnAction(null);
    simulatedSky.getSelectionModel().select(scene.sky().getSimulatedSky());
    simulatedSky.setOnAction(simSkyListener);
    cloudsEnabled.setSelected(scene.sky().cloudsEnabled());
    transparentSkyEnabled.setSelected(scene.transparentSky());
    cloudSizeX.set(scene.sky().getCloudSizeX());
    cloudSizeY.set(scene.sky().getCloudSizeY());
    cloudSizeZ.set(scene.sky().getCloudSizeZ());
    cloudOffsetX.set(scene.sky().cloudXOffset());
    cloudOffsetY.set(scene.sky().cloudYOffset());
    cloudOffsetZ.set(scene.sky().cloudZOffset());
    cloudColor.colorProperty().removeListener(cloudColorListener);
    cloudColor.setColor(ColorUtil.toFx(scene.sky().getCloudColor()));
    cloudColor.colorProperty().addListener(cloudColorListener);
    enableVolumetricClouds.setSelected(scene.sky().getVolumetricClouds());
    cloudDensity.set(scene.sky().getCloudDensity());
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
    return "Sky";
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
