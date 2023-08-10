/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.LegacyWaterShader;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SimplexWaterShader;
import se.llbit.chunky.renderer.scene.WaterShader;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.World;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector3;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WaterTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  @FXML private CheckBox stillWater;
  @FXML private DoubleAdjuster waterVisibility;
  @FXML private DoubleAdjuster waterOpacity;
  @FXML private CheckBox useCustomWaterColor;
  @FXML private LuxColorPicker waterColor;
  @FXML private Button saveDefaults;
  @FXML private CheckBox waterPlaneEnabled;
  @FXML private DoubleAdjuster waterPlaneHeight;
  @FXML private CheckBox waterPlaneOffsetEnabled;
  @FXML private CheckBox waterPlaneClip;
  @FXML private TitledPane waterWorldModeDetailsPane;
  @FXML private CheckBox useProceduralWater;
  @FXML private IntegerAdjuster proceduralWaterIterations;
  @FXML private DoubleAdjuster proceduralWaterFrequency;
  @FXML private DoubleAdjuster proceduralWaterAmplitude;
  @FXML private DoubleAdjuster proceduralWaterAnimationSpeed;
  @FXML private TitledPane proceduralWaterDetailsPane;

  private RenderControlsFxController renderControls;
  private RenderController controller;
  private final ChangeListener<Color> waterColorListener =
    (observable, oldValue, newValue) -> {
      scene.setWaterColor(ColorUtil.fromFx(newValue));
      useCustomWaterColor.setSelected(true);
    };

  public WaterTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("WaterTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void setController(RenderControlsFxController controller) {
    renderControls = controller;
    this.controller = controller.getRenderController();
    scene = this.controller.getSceneManager().getScene();
  }

  @Override
  public void update(Scene scene) {
    useCustomWaterColor.setSelected(scene.getUseCustomWaterColor());
    stillWater.setSelected(scene.stillWaterEnabled());
    waterVisibility.set(scene.getWaterVisibility());
    waterOpacity.set(scene.getWaterOpacity());

    // Update water color without modifying the useCustomColor value.
    waterColor.colorProperty().removeListener(waterColorListener);
    waterColor.setColor(ColorUtil.toFx(scene.getWaterColor()));
    waterColor.colorProperty().addListener(waterColorListener);

    waterPlaneEnabled.setSelected(scene.isWaterPlaneEnabled());
    waterPlaneHeight.setRange(scene.yClipMin, scene.yClipMax);
    waterPlaneHeight.set(scene.getWaterPlaneHeight());
    waterPlaneOffsetEnabled.setSelected(scene.isWaterPlaneOffsetEnabled());
    waterPlaneClip.setSelected(scene.getWaterPlaneChunkClip());

    if(scene.getWaterShading() instanceof SimplexWaterShader) {
      useProceduralWater.setSelected(true);
      SimplexWaterShader simplexWaterShader = (SimplexWaterShader) scene.getWaterShading();
      proceduralWaterIterations.set(simplexWaterShader.iterations);
      proceduralWaterFrequency.set(simplexWaterShader.baseFrequency);
      proceduralWaterAmplitude.set(simplexWaterShader.baseAmplitude);
      proceduralWaterAnimationSpeed.set(simplexWaterShader.animationSpeed);
    } else {
      useProceduralWater.setSelected(false);
      proceduralWaterIterations.set(4);
      proceduralWaterFrequency.set(0.4);
      proceduralWaterAmplitude.set(0.025);
      proceduralWaterAnimationSpeed.set(1);
    }
  }

  @Override
  public String getTabTitle() {
    return "Water";
  }

  @Override
  public Node getTabContent() {
    return this;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    waterVisibility.setName("Water visibility");
    waterVisibility.setTooltip("Distance of visibility past the water surface.");
    waterVisibility.setRange(0, 50);
    waterVisibility.clampMin();
    waterVisibility.onValueChange(value -> scene.setWaterVisibility(value));

    waterOpacity.setName("Water opacity");
    waterOpacity.setTooltip("Opacity of the water surface.");
    waterOpacity.setRange(0, 1);
    waterOpacity.clampBoth();
    waterOpacity.onValueChange(value -> scene.setWaterOpacity(value));

    stillWater.setTooltip(new Tooltip("Disable the waves on the water surface."));
    stillWater.selectedProperty().addListener((observable, oldValue, newValue) ->
      scene.setStillWater(newValue)
    );

    useCustomWaterColor.setTooltip(new Tooltip("Disable biome tinting for water, and use a custom color instead."));
    useCustomWaterColor.selectedProperty().addListener((observable, oldValue, newValue) ->
      scene.setUseCustomWaterColor(newValue)
    );

    waterColor.colorProperty().addListener(waterColorListener);

    saveDefaults.setTooltip(new Tooltip("Save the current water settings as new defaults."));
    saveDefaults.setOnAction(e -> {
      PersistentSettings.setStillWater(scene.stillWaterEnabled());
      PersistentSettings.setWaterOpacity(scene.getWaterOpacity());
      PersistentSettings.setWaterVisibility(scene.getWaterVisibility());
      boolean useCustomWaterColor = scene.getUseCustomWaterColor();
      PersistentSettings.setUseCustomWaterColor(useCustomWaterColor);
      if (useCustomWaterColor) {
        Vector3 color = scene.getWaterColor();
        PersistentSettings.setWaterColor(color.x, color.y, color.z);
      }
    });

    waterWorldModeDetailsPane.setVisible(waterPlaneEnabled.isSelected());
    waterWorldModeDetailsPane.setExpanded(waterPlaneEnabled.isSelected());
    waterWorldModeDetailsPane.setManaged(waterPlaneEnabled.isSelected());

    waterPlaneEnabled.setTooltip(
      new Tooltip("If enabled, an infinite ocean fills the scene. This ignores air from loaded chunks."));
    waterPlaneEnabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.setWaterPlaneEnabled(newValue);
      waterWorldModeDetailsPane.setVisible(newValue);
      waterWorldModeDetailsPane.setExpanded(newValue);
      waterWorldModeDetailsPane.setManaged(newValue);
    });

    waterPlaneHeight.setName("Water height");
    waterPlaneHeight.setTooltip("The default ocean height is " + World.SEA_LEVEL + ".");
    waterPlaneHeight.onValueChange(value -> scene.setWaterPlaneHeight(value));

    waterPlaneOffsetEnabled.setTooltip(new Tooltip("Lower the water plane from block level to water level."));
    waterPlaneOffsetEnabled.selectedProperty().addListener((observable, oldValue, newValue) ->
      scene.setWaterPlaneOffsetEnabled(newValue)
    );

    waterPlaneClip.selectedProperty().addListener((observable, oldValue, newValue) ->
      scene.setWaterPlaneChunkClip(newValue)
    );

    proceduralWaterDetailsPane.setVisible(useProceduralWater.isSelected());
    proceduralWaterDetailsPane.setExpanded(useProceduralWater.isSelected());
    proceduralWaterDetailsPane.setManaged(useProceduralWater.isSelected());

    useProceduralWater.setTooltip(new Tooltip("Generate customized water waves using noise to prevent tiling at large distances."));
    useProceduralWater.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue && scene.getWaterShading() instanceof LegacyWaterShader) {
        SimplexWaterShader shader = new SimplexWaterShader();
        scene.setWaterShading(shader);
        shader.iterations = proceduralWaterIterations.get();
        shader.baseFrequency = proceduralWaterFrequency.get();
        shader.baseAmplitude = proceduralWaterAmplitude.get();
        shader.animationSpeed = proceduralWaterAnimationSpeed.get();
        scene.refresh();
      } else if(!newValue && scene.getWaterShading() instanceof SimplexWaterShader) {
        scene.setWaterShading(new LegacyWaterShader());
        scene.refresh();
      }
      proceduralWaterDetailsPane.setVisible(newValue);
      proceduralWaterDetailsPane.setExpanded(newValue);
      proceduralWaterDetailsPane.setManaged(newValue);
    });

    proceduralWaterIterations.setName("Iterations");
    proceduralWaterIterations.setTooltip("The number of iterations (layers) of noise used");
    proceduralWaterIterations.setRange(1, 10);
    proceduralWaterIterations.onValueChange(iter -> {
      WaterShader shader = scene.getWaterShading();
      if(shader instanceof SimplexWaterShader) {
        ((SimplexWaterShader) shader).iterations = iter;
        scene.refresh();
      }
    });

    proceduralWaterFrequency.setName("Frequency");
    proceduralWaterFrequency.setTooltip("The frequency of the noise");
    proceduralWaterFrequency.setRange(0, 1);
    proceduralWaterFrequency.onValueChange(freq -> {
      WaterShader shader = scene.getWaterShading();
      if(shader instanceof SimplexWaterShader) {
        ((SimplexWaterShader) shader).baseFrequency = freq;
      }
      scene.refresh();
    });

    proceduralWaterAmplitude.setName("Amplitude");
    proceduralWaterAmplitude.setTooltip("The amplitude of the noise");
    proceduralWaterAmplitude.setRange(0, 1);
    proceduralWaterAmplitude.onValueChange(amp -> {
      WaterShader shader = scene.getWaterShading();
      if(shader instanceof SimplexWaterShader) {
        ((SimplexWaterShader) shader).baseAmplitude = amp;
      }
      scene.refresh();
    });

    proceduralWaterAnimationSpeed.setName("Animation speed");
    proceduralWaterAnimationSpeed.setTooltip("Animation speed of the water. "
            + " Only relevant when rendering animation by varying animation time.");
    proceduralWaterAnimationSpeed.setRange(0, 10);
    proceduralWaterAnimationSpeed.onValueChange(speed -> {
      WaterShader shader = scene.getWaterShading();
      if(shader instanceof SimplexWaterShader) {
        ((SimplexWaterShader) shader).animationSpeed = speed;
      }
      scene.refresh();
    });
  }

}
