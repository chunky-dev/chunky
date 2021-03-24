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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.converter.NumberStringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
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
  @FXML private CheckBox waterPlane;
  @FXML private TextField waterHeight;
  @FXML private CheckBox useCustomWaterColor;
  @FXML private LuxColorPicker waterColor;
  @FXML private Button saveDefaults;

  private IntegerProperty waterHeightProp = new SimpleIntegerProperty();
  private RenderControlsFxController renderControls;
  private RenderController controller;
  private final ChangeListener<javafx.scene.paint.Color> waterColorListener =
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

  @Override public void setController(RenderControlsFxController controller) {
    renderControls = controller;
    this.controller = controller.getRenderController();
    scene = this.controller.getSceneManager().getScene();
  }

  @Override public void update(Scene scene) {
    int waterHeight = scene.getWaterHeight();
    if (waterHeight != 0) {
      waterHeightProp.set(scene.getWaterHeight());
    }
    waterPlane.setSelected(scene.getWaterHeight() != 0);
    useCustomWaterColor.setSelected(scene.getUseCustomWaterColor());
    stillWater.setSelected(scene.stillWaterEnabled());
    waterVisibility.set(scene.getWaterVisibility());
    waterOpacity.set(scene.getWaterOpacity());

    // Update water color without modifying the useCustomColor value.
    waterColor.colorProperty().removeListener(waterColorListener);
    waterColor.setColor(ColorUtil.toFx(scene.getWaterColor()));
    waterColor.colorProperty().addListener(waterColorListener);
  }

  @Override public String getTabTitle() {
    return "Water";
  }

  @Override public Node getTabContent() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    waterVisibility.setName("Water visibility");
    waterVisibility.setTooltip("Visibility depth under water.");
    waterVisibility.setRange(0, 50);
    waterVisibility.clampMin();
    waterVisibility.onValueChange(value -> scene.setWaterVisibility(value));

    waterOpacity.setName("Water opacity");
    waterOpacity.setTooltip("Sets how opaque the water surface appears.");
    waterOpacity.setRange(0, 1);
    waterOpacity.clampBoth();
    waterOpacity.onValueChange(value -> scene.setWaterOpacity(value));

    stillWater.selectedProperty()
        .addListener((observable, oldValue, newValue) -> scene.setStillWater(newValue));

    waterHeight.textProperty().bindBidirectional(waterHeightProp, new NumberStringConverter());
    waterHeightProp.set(World.SEA_LEVEL);
    waterHeightProp.addListener((observable, oldValue, newValue) -> {
      if (waterPlane.isSelected()) {
        scene.setWaterHeight(newValue.intValue());
      } else {
        scene.setWaterHeight(0);
      }
    });

    waterPlane.setTooltip(
        new Tooltip("In Water World mode, an infinite ocean surrounds the loaded chunks."));
    waterPlane.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        scene.setWaterHeight(waterHeightProp.get());
      } else {
        scene.setWaterHeight(0);
      }
    });

    useCustomWaterColor.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.setUseCustomWaterColor(newValue);
    });

    waterColor.colorProperty().addListener(waterColorListener);

    saveDefaults.setTooltip(new Tooltip("Store the current water settings as new defaults."));
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
  }

}
