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
package se.llbit.chunky.ui.render.tabs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.util.StringConverter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import se.llbit.util.TaskTracker.Task;

public class PostprocessingTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;
  private RenderControlsFxController controller;

  @FXML private DoubleAdjuster exposure;
  @FXML private ChoiceBox<PostProcessingFilter> postprocessingFilter;

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
    postprocessingFilter.getSelectionModel().select(scene.getPostProcessingFilter());
    exposure.set(scene.getExposure());
  }

  @Override public String getTabTitle() {
    return "Postprocessing";
  }

  @Override public Node getTabContent() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    postprocessingFilter.getItems().add(PostProcessingFilters.NONE);
    postprocessingFilter.getItems().add(new PostprocessingSeparator());
    for (PostProcessingFilter filter : PostProcessingFilters.getFilters()) {
      if (filter != PostProcessingFilters.NONE) {
        postprocessingFilter.getItems().add(filter);
      }
    }
    postprocessingFilter.getSelectionModel().select(Scene.DEFAULT_POSTPROCESSING_FILTER);
    postprocessingFilter.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          scene.setPostprocess(newValue);
          scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
          controller.getCanvas().forceRepaint();
        });
    postprocessingFilter.setConverter(new StringConverter<PostProcessingFilter>() {
      @Override
      public String toString(PostProcessingFilter object) {
        return object == null ? null : object.getName();
      }

      @Override
      public PostProcessingFilter fromString(String string) {
        return PostProcessingFilters.getPostProcessingFilterFromName(string).orElse(Scene.DEFAULT_POSTPROCESSING_FILTER);
      }
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

  /**
   * Fake post processing filter that is also a seperator for the combobox.
   */
  private static class PostprocessingSeparator extends Separator implements PostProcessingFilter {

    @Override
    public void processFrame(int width, int height, double[] input, BitmapImage output,
        double exposure, Task task) {
    }

    @Override
    public String getName() {
      return "";
    }
  }
}
