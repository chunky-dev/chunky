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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.watershading.WaterShadingStrategy;
import se.llbit.chunky.renderer.scene.*;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.World;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WaterTab extends RenderControlsTab implements Initializable {
  @FXML private ChoiceBox<WaterShadingStrategy> waterShader;
  @FXML private CheckBox waterPlaneEnabled;
  @FXML private DoubleAdjuster waterPlaneHeight;
  @FXML private CheckBox waterPlaneOffsetEnabled;
  @FXML private CheckBox waterPlaneClip;
  @FXML private TitledPane waterWorldModeDetailsPane;
  @FXML private TitledPane waterShaderControls;

  public WaterTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("WaterTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void update(Scene scene) {
    waterShader.getSelectionModel().select(scene.getWaterShadingStrategy());
    waterShaderControls.setContent(scene.getCurrentWaterShader().getControls(this));

    waterPlaneEnabled.setSelected(scene.isWaterPlaneEnabled());
    waterPlaneHeight.setRange(scene.yClipMin, scene.yClipMax);
    waterPlaneHeight.set(scene.getWaterPlaneHeight());
    waterPlaneOffsetEnabled.setSelected(scene.isWaterPlaneOffsetEnabled());
    waterPlaneClip.setSelected(scene.getWaterPlaneChunkClip());
  }

  @Override
  public String getTabTitle() {
    return "Water";
  }

  @Override
  public VBox getTabContent() {
    return this;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    waterShader.getItems().addAll(WaterShadingStrategy.values());
    waterShader.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        scene.setWaterShadingStrategy(newValue);
        waterShaderControls.setContent(scene.getCurrentWaterShader().getControls(this));
      });
    StringBuilder waterShaderOptions = new StringBuilder("\n\n");
    for (WaterShadingStrategy strategy : WaterShadingStrategy.values()) {
      waterShaderOptions.append(strategy.getId()).append(": ").append(strategy.getDescription()).append("\n");
    }
    waterShader.setTooltip(new Tooltip("Change how the water surface is rendered." + waterShaderOptions));

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
  }

}
