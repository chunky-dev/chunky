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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.GradientEditor;
import se.llbit.chunky.ui.RenderControlsFxController;
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
  @FXML private DoubleAdjuster cloudSize;
  @FXML private DoubleAdjuster cloudX;
  @FXML private DoubleAdjuster cloudY;
  @FXML private DoubleAdjuster cloudZ;
  @FXML private DoubleAdjuster fogDensity;
  @FXML private DoubleAdjuster skyFogDensity;
  @FXML private LuxColorPicker fogColor;
  private final VBox simulatedSettings = new VBox();
  private DoubleAdjuster horizonOffset = new DoubleAdjuster();
  private final GradientEditor gradientEditor = new GradientEditor(this);
  private final LuxColorPicker colorPicker = new LuxColorPicker();
  private final VBox colorEditor = new VBox(colorPicker);
  private final SkyboxSettings skyboxSettings = new SkyboxSettings();
  private final SkymapSettings skymapSettings = new SkymapSettings();
  private ChangeListener<? super javafx.scene.paint.Color> fogColorListener =
      (observable, oldValue, newValue) -> scene.setFogColor(ColorUtil.fromFx(newValue));

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

    cloudSize.setName("Cloud size");
    cloudSize.setRange(0.1, 128);
    cloudSize.clampMin();
    cloudSize.makeLogarithmic();
    cloudSize.onValueChange(value -> scene.sky().setCloudSize(value));

    cloudX.setTooltip("Cloud X offset.");
    cloudX.onValueChange(value -> scene.sky().setCloudXOffset(value));
    cloudY.setTooltip("Cloud Y offset.");
    cloudY.onValueChange(value -> scene.sky().setCloudYOffset(value));
    cloudZ.setTooltip("Cloud Z offset.");
    cloudZ.onValueChange(value -> scene.sky().setCloudZOffset(value));

    fogDensity.setTooltip("Fog thickness. Set to 0 to disable volumetric fog effect.");
    fogDensity.setRange(0, 2);
    fogDensity.clampMin();
    fogDensity.makeLogarithmic();
    fogDensity.onValueChange(value -> scene.setFogDensity(value));

    skyFogDensity.setTooltip(
        "How much the fog color is blended over the sky/skymap. No effect when fog is disabled.");
    skyFogDensity.setRange(0, 1);
    skyFogDensity.clampMin();
    skyFogDensity.onValueChange(value -> scene.setSkyFogDensity(value));

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
            case SKYMAP_PANORAMIC:
              skyModeSettings.getChildren().setAll(skymapSettings);
              skymapSettings.setPanoramic(true);
              break;
            case SKYMAP_SPHERICAL:
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
    cloudsEnabled.selectedProperty().addListener((observable, oldValue, newValue) -> {

      scene.sky().setCloudsEnabled(newValue);
    });
    fogColor.colorProperty().addListener(fogColorListener);

    colorPicker.colorProperty().addListener(
        (observable, oldValue, newValue) -> scene.sky().setColor(ColorUtil.fromFx(newValue)));
  }

  @Override public void update(Scene scene) {
    skyMode.getSelectionModel().select(scene.sky().getSkyMode());
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
    gradientEditor.setGradient(scene.sky().getGradient());
    colorPicker.setColor(ColorUtil.toFx(scene.sky().getColor()));
    skyboxSettings.update(scene);
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
}
