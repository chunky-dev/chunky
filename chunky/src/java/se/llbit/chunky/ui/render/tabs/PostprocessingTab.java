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
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;
import se.llbit.chunky.renderer.postprocessing.NoneFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.builder.UiBuilder;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.AbstractRenderControlsTab;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.fxutil.ListSeparator;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import se.llbit.util.TaskTracker.Task;

public class PostprocessingTab extends AbstractRenderControlsTab {
  public PostprocessingTab() {
    super("Postprocessing");
  }

  @Override
  public void build(UiBuilder builder) {
    builder.doubleAdjuster()
      .setName("Exposure")
      .setTooltip("Linear exposure of the image.")
      .setRange(Scene.MIN_EXPOSURE, Scene.MAX_EXPOSURE)
      .set(scene.getExposure())
      .setLogarithmic(true)
      .setClamp(true, false)
      .addCallback(value -> {
        scene.setExposure(value);
        scene.postProcessFrame(TaskTracker.NONE);
        fxController.getCanvas().forceRepaint();
      });

    builder.<PostProcessingFilter>choiceBoxInput()
      .setName("Postprocessing filter:")
      .setTooltip("Set the postprocessing filter to be used on the image.")
      .addItems(PostProcessingFilters.NONE)
      .addItems(new PostprocessingSeparator())
      .addItems(PostProcessingFilters.getFilters().stream().filter(f -> f != PostProcessingFilters.NONE).collect(Collectors.toList()))
      .set(scene.getPostProcessingFilter())
      .setStringConverter(PostProcessingFilter::getName)
      .setTooltipConverter(PostProcessingFilter::getDescription)
      .addCallback(value -> {
        scene.setPostprocess(value);
        scene.postProcessFrame(TaskTracker.NONE);
        fxController.getCanvas().forceRepaint();
      });

    builder.text().setText("Postprocessing affects performance when Render Preview tab is visible. Switching to the Map tab mitigates this.");
  }

  private static class PostprocessingSeparator extends NoneFilter implements ListSeparator {
  }
}
