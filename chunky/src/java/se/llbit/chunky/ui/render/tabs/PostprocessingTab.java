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

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.postprocessing.HableToneMappingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.postprocessing.UE4ToneMappingFilter;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;
import se.llbit.util.TaskTracker.Task;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostprocessingTab extends VBox implements RenderControlsTab, Initializable {
  private Scene scene;
  private RenderControlsFxController controller;

  @FXML private DoubleAdjuster exposure;
  @FXML private ChoiceBox<PostProcessingFilter> postprocessingFilter;

  @FXML private VBox hableCurveSettings;
  @FXML private DoubleTextField hableShoulderStrength;
  @FXML private DoubleTextField hableLinearStrength;
  @FXML private DoubleTextField hableLinearAngle;
  @FXML private DoubleTextField hableToeStrength;
  @FXML private DoubleTextField hableToeNumerator;
  @FXML private DoubleTextField hableToeDenominator;
  @FXML private DoubleTextField hableLinearWhitePointValue;
  @FXML private Button gdcPreset;
  @FXML private Button fwPreset;

  @FXML private VBox ue4CurveSettings;
  @FXML private DoubleTextField ue4Saturation;
  @FXML private DoubleTextField ue4Slope;
  @FXML private DoubleTextField ue4Toe;
  @FXML private DoubleTextField ue4Shoulder;
  @FXML private DoubleTextField ue4BlackClip;
  @FXML private DoubleTextField ue4WhiteClip;
  @FXML private Button acesPreset;
  @FXML private Button ue4LegacyPreset;

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
    hableCurveSettings.setVisible(scene.getPostProcessingFilter() instanceof HableToneMappingFilter);
    ue4CurveSettings.setVisible(scene.getPostProcessingFilter() instanceof UE4ToneMappingFilter);
    exposure.set(scene.getExposure());

    if (scene.getPostProcessingFilter() instanceof HableToneMappingFilter) {
      HableToneMappingFilter filter = (HableToneMappingFilter) scene.getPostProcessingFilter();
      hableShoulderStrength.valueProperty().set(filter.getShoulderStrength());
      hableLinearStrength.valueProperty().set(filter.getLinearStrength());
      hableLinearAngle.valueProperty().set(filter.getLinearAngle());
      hableToeStrength.valueProperty().set(filter.getToeStrength());
      hableToeNumerator.valueProperty().set(filter.getToeNumerator());
      hableToeDenominator.valueProperty().set(filter.getToeDenominator());
      hableLinearWhitePointValue.valueProperty().set(filter.getLinearWhitePointValue());
    } else if (scene.getPostProcessingFilter() instanceof UE4ToneMappingFilter) {
      UE4ToneMappingFilter filter = (UE4ToneMappingFilter) scene.getPostProcessingFilter();
      ue4Saturation.valueProperty().set(filter.getSaturation());
      ue4Slope.valueProperty().set(filter.getSlope());
      ue4Toe.valueProperty().set(filter.getToe());
      ue4Shoulder.valueProperty().set(filter.getShoulder());
      ue4BlackClip.valueProperty().set(filter.getBlackClip());
      ue4WhiteClip.valueProperty().set(filter.getWhiteClip());
    }
  }

  @Override public String getTabTitle() {
    return "Postprocessing";
  }

  @Override public VBox getTabContent() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    postprocessingFilter.setTooltip(new Tooltip("Set the postprocessing filter to be used on the image."));
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
          applyChangedSettings(false);
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
    exposure.setTooltip("Linear exposure of the image.");
    exposure.setRange(Scene.MIN_EXPOSURE, Scene.MAX_EXPOSURE);
    exposure.makeLogarithmic();
    exposure.clampMin();
    exposure.onValueChange(value -> {
      scene.setExposure(value);
      applyChangedSettings(false);
    });
    hableCurveSettings.managedProperty().bind(hableCurveSettings.visibleProperty());
    gdcPreset.setOnAction((e) -> {
      if (scene.postProcessingFilter instanceof HableToneMappingFilter) {
        ((HableToneMappingFilter) scene.postProcessingFilter).applyPreset(HableToneMappingFilter.Preset.GDC);
        applyChangedSettings(true);
      }
    });
    fwPreset.setOnAction((e) -> {
      if (scene.postProcessingFilter instanceof HableToneMappingFilter) {
        ((HableToneMappingFilter) scene.postProcessingFilter).applyPreset(HableToneMappingFilter.Preset.FILMIC_WORLDS);
        applyChangedSettings(true);
      }
    });
    ue4CurveSettings.managedProperty().bind(ue4CurveSettings.visibleProperty());
    acesPreset.setOnAction((e) -> {
      if (scene.postProcessingFilter instanceof UE4ToneMappingFilter) {
        ((UE4ToneMappingFilter) scene.postProcessingFilter).applyPreset(UE4ToneMappingFilter.Preset.ACES);
        applyChangedSettings(true);
      }
    });
    ue4LegacyPreset.setOnAction((e) -> {
      if (scene.postProcessingFilter instanceof UE4ToneMappingFilter) {
        ((UE4ToneMappingFilter) scene.postProcessingFilter).applyPreset(UE4ToneMappingFilter.Preset.LEGACY_UE4);
        applyChangedSettings(true);
      }
    });

    EventHandler<KeyEvent> postprocessingSettingsHandler = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        if (scene.postProcessingFilter instanceof HableToneMappingFilter) {
          HableToneMappingFilter filter = (HableToneMappingFilter) scene.postProcessingFilter;
          filter.setShoulderStrength(hableShoulderStrength.valueProperty().floatValue());
          filter.setLinearStrength(hableLinearStrength.valueProperty().floatValue());
          filter.setLinearAngle(hableLinearAngle.valueProperty().floatValue());
          filter.setToeStrength(hableToeStrength.valueProperty().floatValue());
          filter.setToeNumerator(hableToeNumerator.valueProperty().floatValue());
          filter.setToeDenominator(hableToeDenominator.valueProperty().floatValue());
          filter.setLinearWhitePointValue(hableLinearWhitePointValue.valueProperty().floatValue());
        } else if (scene.postProcessingFilter instanceof UE4ToneMappingFilter) {
          UE4ToneMappingFilter filter = (UE4ToneMappingFilter) scene.postProcessingFilter;
          filter.setSaturation(ue4Saturation.valueProperty().floatValue());
          filter.setSlope(ue4Slope.valueProperty().floatValue());
          filter.setToe(ue4Toe.valueProperty().floatValue());
          filter.setShoulder(ue4Shoulder.valueProperty().floatValue());
          filter.setBlackClip(ue4BlackClip.valueProperty().floatValue());
          filter.setWhiteClip(ue4WhiteClip.valueProperty().floatValue());
        }
        applyChangedSettings(true);
      }
    };
    hableShoulderStrength.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    hableLinearStrength.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    hableLinearAngle.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    hableToeStrength.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    hableToeNumerator.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    hableToeDenominator.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    hableLinearWhitePointValue.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    ue4Saturation.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    ue4Slope.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    ue4Toe.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    ue4Shoulder.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    ue4BlackClip.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
    ue4WhiteClip.addEventFilter(KeyEvent.KEY_PRESSED, postprocessingSettingsHandler);
  }

  private void applyChangedSettings(boolean refreshScene) {
    if (refreshScene && scene.getMode() == RenderMode.PREVIEW) {
      // Don't interrupt the render if we are currently rendering.
      scene.refresh();
    }
    update(scene);
    scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
    controller.getCanvas().forceRepaint();
  }

  /**
   * Fake post processing filter that is also a separator for the combobox.
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
