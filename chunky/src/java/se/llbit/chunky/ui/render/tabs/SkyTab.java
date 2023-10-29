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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
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
  @FXML private ComboBox<String> cloudLayers;
  @FXML private Button addCloudLayer;
  @FXML private Button removeCloudLayer;
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
  @FXML private DoubleAdjuster emittance;
  @FXML private DoubleAdjuster specular;
  @FXML private DoubleAdjuster smoothness;
  @FXML private DoubleAdjuster ior;
  @FXML private DoubleAdjuster metalness;
  @FXML private DoubleAdjuster anisotropy;
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
    (observable, oldValue, newValue) -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerColor(index, ColorUtil.fromFx(newValue));
    };
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

    cloudLayers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      updateControls()
    );

    addCloudLayer.setText("Add cloud layer");
    addCloudLayer.setOnAction(event -> {
      scene.sky().addCloudLayer();
      disableControls();
      if (updateLayersList()) {
        updateControls();
      }
      cloudLayers.getSelectionModel().selectLast();
    });

    removeCloudLayer.setText("Remove cloud layer");
    removeCloudLayer.setOnAction(event -> {
      if (scene.sky().getNumCloudLayers() > 0) {
        scene.sky().removeCloudLayer(cloudLayers.getSelectionModel().getSelectedIndex());
        disableControls();
        if (updateLayersList()) {
          updateControls();
        }
      }
    });

    cloudSizeX.setName("Cloud X scale");
    cloudSizeX.setTooltip("Scale of the X-dimension of the clouds, measured in blocks per pixel of clouds.png texture");
    cloudSizeX.setRange(0.01, 128);
    cloudSizeX.clampMin();
    cloudSizeX.makeLogarithmic();
    cloudSizeX.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerSizeX(index, value);
    });

    cloudSizeY.setName("Cloud Y scale");
    cloudSizeY.setTooltip("Scale of the Y-dimension of the clouds, measured in blocks per pixel of clouds.png texture");
    cloudSizeY.setRange(0.01, 128);
    cloudSizeY.clampMin();
    cloudSizeY.makeLogarithmic();
    cloudSizeY.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerSizeY(index, value);
    });

    cloudSizeZ.setName("Cloud Z scale");
    cloudSizeZ.setTooltip("Scale of the Z-dimension of the clouds, measured in blocks per pixel of clouds.png texture");
    cloudSizeZ.setRange(0.01, 128);
    cloudSizeZ.clampMin();
    cloudSizeZ.makeLogarithmic();
    cloudSizeZ.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerSizeZ(index, value);
    });

    cloudOffsetX.setName("Cloud X offset");
    cloudOffsetX.setTooltip("Changes the X-offset of the clouds.");
    cloudOffsetX.setRange(-256, 256);
    cloudOffsetX.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerXOffset(index, value);
    });

    cloudOffsetY.setName("Cloud Y offset");
    cloudOffsetY.setTooltip("Changes the altitude of the clouds.");
    cloudOffsetY.setRange(-64, 320);
    cloudOffsetY.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerYOffset(index, value);
    });

    cloudOffsetZ.setName("Cloud Z offset");
    cloudOffsetZ.setTooltip("Changes the Z-offset of the clouds.");
    cloudOffsetZ.setRange(-256, 256);
    cloudOffsetZ.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerZOffset(index, value);
    });

    cloudColor.colorProperty().addListener(cloudColorListener);

    enableVolumetricClouds.setTooltip(new Tooltip("Use a volume scatter for the cloud material."));
    enableVolumetricClouds.selectedProperty().addListener((observable, oldValue, newValue) -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerVolumetricClouds(index, newValue);
      disableControls();
      enableControls(newValue);
    });

    cloudDensity.setName("Volumetric cloud density");
    cloudDensity.setRange(0.000001, 1);
    cloudDensity.clampMin();
    cloudDensity.setMaximumFractionDigits(6);
    cloudDensity.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerDensity(index, value);
    });

    emittance.setName("Emittance");
    emittance.setRange(0, 100);
    emittance.clampMin();
    emittance.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerEmittance(index, value.floatValue());
    });

    specular.setName("Specular");
    specular.setRange(0, 1);
    specular.clampBoth();
    specular.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerSpecular(index, value.floatValue());
    });

    smoothness.setName("Smoothness");
    smoothness.setRange(0, 1);
    smoothness.clampBoth();
    smoothness.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerSmoothness(index, value.floatValue());
    });

    ior.setName("IoR");
    ior.setRange(0, 5);
    ior.clampMin();
    ior.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerIor(index, value.floatValue());
    });

    metalness.setName("Metalness");
    metalness.setRange(0, 1);
    metalness.clampBoth();
    metalness.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerMetalness(index, value.floatValue());
    });

    anisotropy.setName("Anisotropy");
    anisotropy.setRange(-1, 1);
    anisotropy.clampBoth();
    anisotropy.onValueChange(value -> {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      scene.sky().setCloudLayerAnisotropy(index, value.floatValue());
    });

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

  private void disableControls() {
    cloudSizeX.setDisable(true);
    cloudSizeY.setDisable(true);
    cloudSizeZ.setDisable(true);
    cloudOffsetX.setDisable(true);
    cloudOffsetY.setDisable(true);
    cloudOffsetZ.setDisable(true);
    cloudColor.setDisable(true);
    enableVolumetricClouds.setDisable(true);
    cloudDensity.setDisable(true);
    emittance.setDisable(true);
    specular.setDisable(true);
    smoothness.setDisable(true);
    ior.setDisable(true);
    metalness.setDisable(true);
    anisotropy.setDisable(true);
  }

  private void enableControls(boolean volumetricClouds) {
    cloudSizeX.setDisable(false);
    cloudSizeY.setDisable(false);
    cloudSizeZ.setDisable(false);
    cloudOffsetX.setDisable(false);
    cloudOffsetY.setDisable(false);
    cloudOffsetZ.setDisable(false);
    cloudColor.setDisable(false);
    enableVolumetricClouds.setDisable(false);
    emittance.setDisable(false);
    if (!volumetricClouds) {
      specular.setDisable(false);
      smoothness.setDisable(false);
      ior.setDisable(false);
      metalness.setDisable(false);
    } else {
      cloudDensity.setDisable(false);
      anisotropy.setDisable(false);
    }
  }

  private boolean updateLayersList() {
    cloudLayers.getSelectionModel().clearSelection();
    cloudLayers.getItems().clear();
    int numLayers = scene.sky().getNumCloudLayers();
    boolean emptyLayers = !(numLayers > 0);
    if (!emptyLayers) {
      for (int i = 0; i < numLayers; i++) {
        cloudLayers.getItems().add(String.format("Layer %d", i + 1));
      }
    }
    return emptyLayers;
  }

  private void updateControls() {
    if (!cloudLayers.getSelectionModel().isEmpty()) {
      int index = cloudLayers.getSelectionModel().getSelectedIndex();
      cloudSizeX.set(scene.sky().getCloudLayerSizeX(index));
      cloudSizeY.set(scene.sky().getCloudLayerSizeY(index));
      cloudSizeZ.set(scene.sky().getCloudLayerSizeZ(index));
      cloudOffsetX.set(scene.sky().getCloudLayerXOffset(index));
      cloudOffsetY.set(scene.sky().getCloudLayerYOffset(index));
      cloudOffsetZ.set(scene.sky().getCloudLayerZOffset(index));
      cloudColor.colorProperty().removeListener(cloudColorListener);
      cloudColor.setColor(ColorUtil.toFx(scene.sky().getCloudLayerColor(index)));
      cloudColor.colorProperty().addListener(cloudColorListener);
      boolean volumetricClouds = scene.sky().getCloudLayerVolumetricClouds(index);
      enableVolumetricClouds.setSelected(volumetricClouds);
      cloudDensity.set(scene.sky().getCloudLayerDensity(index));
      emittance.set(scene.sky().getCloudLayerEmittance(index));
      specular.set(scene.sky().getCloudLayerSpecular(index));
      smoothness.set(scene.sky().getCloudLayerSmoothness(index));
      ior.set(scene.sky().getCloudLayerIor(index));
      metalness.set(scene.sky().getCloudLayerMetalness(index));
      anisotropy.set(scene.sky().getCloudLayerAnisotropy(index));
      enableControls(volumetricClouds);
    }
  }

  @Override public void update(Scene scene) {
    skyMode.getSelectionModel().select(scene.sky().getSkyMode());
    simulatedSky.setOnAction(null);
    simulatedSky.getSelectionModel().select(scene.sky().getSimulatedSky());
    simulatedSky.setOnAction(simSkyListener);
    disableControls();
    if (updateLayersList()) {
      updateControls();
    }
    transparentSkyEnabled.setSelected(scene.transparentSky());
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
