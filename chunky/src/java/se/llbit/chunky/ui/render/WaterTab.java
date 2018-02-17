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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
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

  private final Tab parentTab;
  @FXML private CheckBox stillWater;
  @FXML private DoubleAdjuster waterVisibility;
  @FXML private DoubleAdjuster waterOpacity;
  @FXML private CheckBox waterWorld;
  @FXML private TextField waterHeight;
  @FXML private Button applyWaterHeight;
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
    parentTab = new Tab("Water", this);
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
    waterWorld.setSelected(scene.getWaterHeight() != 0);
    useCustomWaterColor.setSelected(scene.getUseCustomWaterColor());
    stillWater.setSelected(scene.stillWaterEnabled());
    waterVisibility.set(scene.getWaterVisibility());
    waterOpacity.set(scene.getWaterOpacity());

    // Update water color without modifying the useCustomColor value.
    waterColor.colorProperty().removeListener(waterColorListener);
    waterColor.setColor(ColorUtil.toFx(scene.getWaterColor()));
    waterColor.colorProperty().addListener(waterColorListener);
  }

  @Override public Tab getTab() {
    return parentTab;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    waterVisibility.setName("Water visibility");
    waterVisibility.setTooltip("Visibility depth under water.");
    waterVisibility.setRange(0, 20);
    waterVisibility.clampMin();
    waterVisibility.onValueChange(value -> scene.setWaterVisibility(value));

    waterOpacity.setName("Water opacity");
    waterOpacity.setTooltip("Sets how opaque the water surface appears.");
    waterOpacity.setRange(0, 1);
    waterOpacity.clampBoth();
    waterOpacity.onValueChange(value -> scene.setWaterOpacity(value));

    stillWater.selectedProperty()
        .addListener((observable, oldValue, newValue) -> scene.setStillWater(newValue));

    int initialWaterHeight = PersistentSettings.getWaterHeight();
    if (initialWaterHeight == 0) {
      initialWaterHeight = World.SEA_LEVEL;
    }
    waterHeight.textProperty().bindBidirectional(waterHeightProp, new NumberStringConverter());
    waterHeightProp.set(initialWaterHeight);

    applyWaterHeight.setOnAction(e -> {
      boolean needsReload;
      if (waterWorld.isSelected()) {
        needsReload = scene.setWaterHeight(waterHeightProp.get());
      } else {
        needsReload = scene.setWaterHeight(0);
      }
      if (needsReload) {
        controller.getSceneManager().reloadChunks();
      }
    });

    waterWorld.setTooltip(
        new Tooltip("In Water World mode, an infinite ocean surrounds the loaded chunks."));
    waterWorld.selectedProperty().addListener((observable, oldValue, newValue) -> {
      int waterHeight;
      if (newValue) {
        waterHeight = waterHeightProp.get();
      } else {
        waterHeight = 0;
      }
      if (waterHeight != scene.getWaterHeight()) {
        renderControls.showPopup(
            "Click the Apply button to apply this setting and reload all chunks.", waterWorld);
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
      PersistentSettings.setWaterHeight(scene.getWaterHeight());
      boolean useCustomWaterColor = scene.getUseCustomWaterColor();
      PersistentSettings.setUseCustomWaterColor(useCustomWaterColor);
      if (useCustomWaterColor) {
        Vector3 color = scene.getWaterColor();
        PersistentSettings.setWaterColor(color.x, color.y, color.z);
      }
    });
  }

}
