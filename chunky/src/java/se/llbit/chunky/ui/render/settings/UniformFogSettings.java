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

public class UniformFogSettings extends VBox implements Initializable {
  private Scene scene;

  @FXML private DoubleAdjuster fogDensity;
  @FXML private DoubleAdjuster skyFogDensity;
  @FXML private LuxColorPicker fogColor;

  private ChangeListener<? super Color> fogColorListener =
    (observable, oldValue, newValue) -> scene.setFogColor(ColorUtil.fromFx(newValue));

  public UniformFogSettings() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("UniformFogSettings.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    fogDensity.setTooltip("Fog thickness. Set to 0 to disable volumetric fog effect.");
    fogDensity.setRange(0.000001, 1);
    fogDensity.setMaximumFractionDigits(6);
    fogDensity.makeLogarithmic();
    fogDensity.clampMin();
    fogDensity.onValueChange(value -> scene.setFogDensity(value));
    fogDensity.setName("Fog density");

    skyFogDensity.setTooltip(
      "How much the fog color is blended over the sky/skymap. No effect when fog is disabled.");
    skyFogDensity.setRange(0, 1);
    skyFogDensity.clampMin();
    skyFogDensity.onValueChange(value -> scene.setSkyFogDensity(value));
    skyFogDensity.setName("Sky fog blending");

    fogColor.setText("Fog color");
    fogColor.colorProperty().addListener(fogColorListener);
  }

  public void setRenderController(RenderController controller) {
    scene = controller.getSceneManager().getScene();
  }

  public void update(Scene scene) {
    fogDensity.set(scene.fog.getUniformDensity());
    skyFogDensity.set(scene.fog.getSkyFogDensity());
    fogColor.colorProperty().removeListener(fogColorListener);
    fogColor.setColor(ColorUtil.toFx(scene.fog.getFogColor()));
    fogColor.colorProperty().addListener(fogColorListener);
  }
}
