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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.GradientEditor;
import se.llbit.chunky.ui.SimpleColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector4;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SkyTab extends VBox implements RenderControlTab, Initializable {
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
  @FXML private SimpleColorPicker fogColor;
  private final VBox simulatedSettings = new VBox();
  private DoubleAdjuster horizonOffset = new DoubleAdjuster();
  private final GradientEditor gradientEditor = new GradientEditor(this);
  private final SkyboxSettings skyboxSettings = new SkyboxSettings();
  private final SkymapSettings skymapSettings = new SkymapSettings();
  private ChangeListener<? super javafx.scene.paint.Color> fogColorListener =
      (observable, oldValue, newValue) -> scene.setFogColor(ColorUtil.fromFx(newValue));

  public SkyTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SkyTab.fxml"));
    // Needed for Java 1.8u40 where FXMLLoader has a null class loader for some reason.
    loader.setClassLoader(getClass().getClassLoader());
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  public void setRenderController(RenderController controller) {
    scene = controller.getSceneManager().getScene();
    skyboxSettings.setRenderController(controller);
    skymapSettings.setRenderController(controller);
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

    cloudX.setName("Cloud X");
    cloudX.setTooltip("Cloud X offset.");
    cloudX.onValueChange(value -> scene.sky().setCloudXOffset(value));
    cloudY.setName("Cloud Y");
    cloudY.setTooltip("Cloud Y offset.");
    cloudY.onValueChange(value -> scene.sky().setCloudYOffset(value));
    cloudZ.setName("Cloud Z");
    cloudZ.setTooltip("Cloud Z offset.");
    cloudZ.onValueChange(value -> scene.sky().setCloudZOffset(value));

    fogDensity.setName("Fog density");
    fogDensity.setTooltip("Alters the volumetric fog effect.");
    fogDensity.setRange(0, 2);
    fogDensity.clampMin();
    fogDensity.onValueChange(value -> scene.setFogDensity(value));

    skyMode.getItems().addAll(Sky.SkyMode.values());
    skyMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          scene.sky().setSkyMode(newValue);
          switch (newValue) {
            case SIMULATED:
              skyModeSettings.getChildren().setAll(simulatedSettings);
              break;
            case GRADIENT:
              skyModeSettings.getChildren().setAll(gradientEditor);
              break;
            case BLACK:
              skyModeSettings.getChildren()
                  .setAll(new Label(String.format("Selected mode has no settings.")));
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
    fogColor.colorProperty().removeListener(fogColorListener);
    fogColor.setColor(ColorUtil.toFx(scene.getFogColor()));
    fogColor.colorProperty().addListener(fogColorListener);
    horizonOffset.set(scene.sky().getHorizonOffset());
    gradientEditor.setGradient(scene.sky().getGradient());
    skyboxSettings.update(scene);
  }

  public void gradientChanged(List<Vector4> gradient) {
    scene.sky().setGradient(gradient);
  }
}
