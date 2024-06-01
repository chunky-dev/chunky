/* Copyright (c) 2023 Chunky Contributors
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
package se.llbit.chunky.ui.render.settings;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LayeredFogSettings extends VBox implements Initializable {
  private Scene scene;

  @FXML private ComboBox<String> layers;
  @FXML private Button addLayer;
  @FXML private Button removeLayer;
  @FXML private DoubleAdjuster layerDensity;
  @FXML private DoubleAdjuster layerY;
  @FXML private DoubleAdjuster layerBreadth;
  @FXML private LuxColorPicker fogColor;

  private ChangeListener<? super Color> fogColorListener =
    (observable, oldValue, newValue) -> scene.setFogColor(ColorUtil.fromFx(newValue));

  public LayeredFogSettings() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("LayeredFogSettings.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  public void enableControls() {
    layerY.setDisable(false);
    layerBreadth.setDisable(false);
    layerDensity.setDisable(false);
  }

  public void disableControls() {
    layerY.setDisable(true);
    layerBreadth.setDisable(true);
    layerDensity.setDisable(true);
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    layers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      updateControls()
    );

    addLayer.setOnAction(e -> {
      scene.fog.addLayer();
      update(scene);
      layers.getSelectionModel().selectLast();
    });

    removeLayer.setOnAction(e -> {
      if (scene.fog.getFogLayers().size() > 0) {
        scene.fog.removeLayer(layers.getSelectionModel().getSelectedIndex());
        update(scene);
      }
    });

    if (!(layers.getItems().size() > 0)) {
      disableControls();
    }

    layerY.setTooltip("Y-coordinate of the selected fog layer");
    layerY.setRange(-64, 320);
    layerY.onValueChange(value -> scene.fog.setY(layers.getSelectionModel().getSelectedIndex(), value));
    layerY.setName("Layer altitude");

    layerBreadth.setTooltip("Vertical spread of the selected fog layer");
    layerBreadth.setRange(0.1, 100);
    layerBreadth.makeLogarithmic();
    layerBreadth.clampMin();
    layerBreadth.onValueChange(value -> scene.fog.setBreadth(layers.getSelectionModel().getSelectedIndex(), value));
    layerBreadth.setName("Layer thickness");

    layerDensity.setTooltip("Density of the selected fog layer");
    layerDensity.setRange(0, 1);
    layerDensity.setMaximumFractionDigits(6);
    layerDensity.makeLogarithmic();
    layerDensity.clampMin();
    layerDensity.onValueChange(value -> scene.fog.setDensity(layers.getSelectionModel().getSelectedIndex(), value));
    layerDensity.setName("Fog density");

    fogColor.colorProperty().addListener(fogColorListener);
  }

  public void setRenderController(RenderController controller) {
    scene = controller.getSceneManager().getScene();
  }

  private boolean updateLayersList() {
    layers.getSelectionModel().clearSelection();
    layers.getItems().clear();
    int numLayers = scene.fog.getFogLayers().size();
    boolean emptyLayers = !(numLayers > 0);
    if (!emptyLayers) {
      for (int i = 0; i < numLayers; i++) {
        layers.getItems().add(String.format("Layer %d", i + 1));
      }
    }
    return emptyLayers;
  }

  private void updateControls() {
    if (!layers.getSelectionModel().isEmpty()) {
      int index = layers.getSelectionModel().getSelectedIndex();
      layerY.set(scene.fog.getFogLayers().get(index).y);
      layerBreadth.set(scene.fog.getFogLayers().get(index).breadth);
      layerDensity.set(scene.fog.getFogLayers().get(index).density);
      enableControls();
    }
  }

  public void update(Scene scene) {
    disableControls();
    if (updateLayersList()) {
      updateControls();
    }
    fogColor.colorProperty().removeListener(fogColorListener);
    fogColor.setColor(ColorUtil.toFx(scene.fog.getFogColor()));
    fogColor.colorProperty().addListener(fogColorListener);
  }
}
