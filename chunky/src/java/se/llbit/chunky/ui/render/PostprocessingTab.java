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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostprocessingTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;
  private RenderControlsFxController controller;

  @FXML private DoubleAdjuster exposure;
  @FXML private ChoiceBox<Postprocess> postprocessingMode;

  public PostprocessingTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("PostprocessingTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void setController(RenderControlsFxController controller) {
    this.controller = controller;
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  @Override public void update(Scene scene) {
    postprocessingMode.getSelectionModel().select(scene.getPostprocess());
    exposure.set(scene.getExposure());
  }

  @Override public String getTabTitle() {
    return "Postprocessing";
  }

  @Override public Node getTabContent() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    postprocessingMode.getItems().addAll(Postprocess.values());
    postprocessingMode.getSelectionModel().select(Postprocess.DEFAULT);
    postprocessingMode.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          scene.setPostprocess(newValue);
          scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
          controller.getCanvas().forceRepaint();
        });
    exposure.setName("Exposure");
    exposure.setRange(Scene.MIN_EXPOSURE, Scene.MAX_EXPOSURE);
    exposure.makeLogarithmic();
    exposure.clampMin();
    exposure.onValueChange(value -> {
      scene.setExposure(value);
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });
  }
}
