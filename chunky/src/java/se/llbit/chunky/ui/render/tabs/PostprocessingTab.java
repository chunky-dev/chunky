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

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.dialogs.PostprocessingFilterChooser;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostprocessingTab extends RenderControlsTab implements Initializable {

  @FXML
  private DoubleAdjuster exposure;
  @FXML
  private TableView<PostProcessingFilter> filterTable;
  @FXML
  private TableColumn<PostProcessingFilter, String> filterTypeCol;
  @FXML
  private Button addFilter;
  @FXML
  private Button removeFilter;
  @FXML
  private TitledPane filterControls;

  public PostprocessingTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("PostprocessingTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void update(Scene scene) {
    exposure.set(scene.getExposure());
    rebuildList();
  }

  private void rebuildList() {
    filterTable.getSelectionModel().clearSelection();
    filterTable.getItems().clear();
    scene.postprocessingFilters.forEach(filterTable.getItems()::add);
  }

  @Override
  public String getTabTitle() {
    return "Postprocessing";
  }

  @Override
  public VBox getTabContent() {
    return this;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    exposure.setName("Exposure");
    exposure.setTooltip("Exposure of the image. Applied before postprocessing filters.");
    exposure.setRange(Scene.MIN_EXPOSURE, Scene.MAX_EXPOSURE);
    exposure.clampMin();
    exposure.onValueChange(value -> {
      scene.setExposure(value);
      applyChangedSettings(false);
    });

    filterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        filterControls.setContent(newValue.getControls(this));
      } else {
        filterControls.setContent(null);
      }
    });
    filterTable.refresh();
    filterTypeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
    filterTypeCol.setSortable(false);

    addFilter.setOnAction(e -> {
      PostprocessingFilterChooser dialog = new PostprocessingFilterChooser();
      if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        PostProcessingFilter filter = dialog.getFilter();
        try {
          scene.addPostprocessingFilter(filter.getClass().newInstance());
        } catch (InstantiationException | IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
        applyChangedSettings(true);
        filterTable.getSelectionModel().selectLast();
      }
    });

    removeFilter.setOnAction(e -> {
      int index = filterTable.getSelectionModel().getSelectedIndex();
      scene.removePostprocessingFilter(index);
      applyChangedSettings(true);
    });
  }

  private void applyChangedSettings(boolean update) {
    if (scene.getMode() == RenderMode.PREVIEW) {
      // Don't interrupt the render if we are currently rendering.
      scene.refresh();
    }
    if (update) {
      update(scene);
    }
    scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
    controller.getCanvas().forceRepaint();
  }
}
