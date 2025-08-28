package se.llbit.chunky.ui.render.tabs;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.CloudLayer;
import se.llbit.chunky.renderer.scene.fog.FogMode;
import se.llbit.chunky.renderer.scene.volumetricfog.FogVolume;
import se.llbit.chunky.renderer.scene.volumetricfog.FogVolumeShape;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.FogVolumeShapeSelectorDialog;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.ui.render.settings.LayeredFogSettings;
import se.llbit.chunky.ui.render.settings.UniformFogSettings;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FogTab extends RenderControlsTab implements Initializable {
  private static class FogVolumeData {
    final String shape;

    FogVolumeData(FogVolume fogVolume) {
      this.shape = fogVolume.getShape().name();
    }
  }

  @FXML private ChoiceBox<FogMode> fogMode;
  @FXML private TitledPane fogDetailsPane;
  @FXML private VBox fogDetailsBox;

  @FXML private TableView<FogVolumeData> fogVolumeTable;
  @FXML private TableColumn<FogVolumeData, String> typeCol;
  @FXML private Button addVolume;
  @FXML private Button removeVolume;
  @FXML private VBox volumeSpecificControls;

  @FXML private TableView<CloudLayer> cloudLayerTable;
  @FXML private TableColumn<CloudLayer, String> scaleXColumn;
  @FXML private TableColumn<CloudLayer, String> scaleYColumn;
  @FXML private TableColumn<CloudLayer, String> scaleZColumn;
  @FXML private TableColumn<CloudLayer, String> offsetXColumn;
  @FXML private TableColumn<CloudLayer, String> offsetYColumn;
  @FXML private TableColumn<CloudLayer, String> offsetZColumn;
  @FXML private Button addCloudLayer;
  @FXML private Button removeCloudLayer;
  @FXML private VBox layerSpecificControls;

  private final UniformFogSettings uniformFogSettings = new UniformFogSettings();
  private final LayeredFogSettings layeredFogSettings = new LayeredFogSettings();
  private final FogVolumeShapeSelectorDialog fogVolumeShapeSelectorDialog = new FogVolumeShapeSelectorDialog();

  public FogTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FogTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override protected void onSetController(RenderControlsFxController controller) {
    uniformFogSettings.setRenderController(controller.getRenderController());
    layeredFogSettings.setRenderController(controller.getRenderController());
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    fogMode.getItems().addAll(FogMode.values());
    fogMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      scene.setFogMode(newValue);
      switch (newValue) {
        case NONE: {
          fogDetailsBox.getChildren().setAll(new Label("Selected mode has no settings."));
          break;
        }
        case UNIFORM: {
          fogDetailsBox.getChildren().setAll(uniformFogSettings);
          break;
        }
        case LAYERED: {
          fogDetailsBox.getChildren().setAll(layeredFogSettings);
          break;
        }
      }
      fogDetailsPane.setExpanded(true);
    });

    fogVolumeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      updateControls();
    });
    fogVolumeTable.refresh();
    typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().shape));
    typeCol.setSortable(false);

    addVolume.setOnAction(e -> {
      if (fogVolumeShapeSelectorDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        FogVolumeShape shape = fogVolumeShapeSelectorDialog.getShape();
        scene.addFogVolume(shape);
        rebuildFogVolumeList();
        fogVolumeTable.getSelectionModel().selectLast();
      }
    });

    removeVolume.setOnAction(e -> {
      int index = fogVolumeTable.getSelectionModel().getSelectedIndex();
      scene.removeFogVolume(index);
      rebuildFogVolumeList();
    });

    cloudLayerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        layerSpecificControls.getChildren().add(scene.getCloudLayers().get(cloudLayerTable.getSelectionModel().getSelectedIndex()).getControls(this));
      } else {
        layerSpecificControls.getChildren().clear();
      }
    });
    cloudLayerTable.refresh();
    scaleXColumn.setCellValueFactory(new PropertyValueFactory<CloudLayer, String>("scaleX"));
    scaleYColumn.setCellValueFactory(new PropertyValueFactory<CloudLayer, String>("scaleY"));
    scaleZColumn.setCellValueFactory(new PropertyValueFactory<CloudLayer, String>("scaleZ"));
    offsetXColumn.setCellValueFactory(new PropertyValueFactory<CloudLayer, String>("offsetX"));
    offsetYColumn.setCellValueFactory(new PropertyValueFactory<CloudLayer, String>("offsetY"));
    offsetZColumn.setCellValueFactory(new PropertyValueFactory<CloudLayer, String>("offsetZ"));

    scaleXColumn.setSortable(false);
    scaleYColumn.setSortable(false);
    scaleZColumn.setSortable(false);
    offsetXColumn.setSortable(false);
    offsetYColumn.setSortable(false);
    offsetZColumn.setSortable(false);

    addCloudLayer.setOnAction(e -> {
      scene.addCloudLayer();
      rebuildCloudLayerList();
      cloudLayerTable.getSelectionModel().selectLast();
    });

    removeCloudLayer.setOnAction(e -> {
      int index = cloudLayerTable.getSelectionModel().getSelectedIndex();
      scene.removeCloudLayer(index);
      rebuildCloudLayerList();
    });
  }

  @Override public void update(Scene scene) {
    fogMode.getSelectionModel().select(scene.fog.getFogMode());
    uniformFogSettings.update(scene);
    layeredFogSettings.update(scene);
    rebuildFogVolumeList();
    rebuildCloudLayerList();
  }

  @Override
  public String getTabTitle() {
    return "Fog & Clouds";
  }

  @Override
  public VBox getTabContent() {
    return this;
  }

  private void rebuildFogVolumeList() {
    fogVolumeTable.getSelectionModel().clearSelection();
    fogVolumeTable.getItems().clear();
    scene.getFogVolumes().forEach(fogVolume -> {
      FogVolumeData fogVolumeData = new FogVolumeData(fogVolume);
      fogVolumeTable.getItems().add(fogVolumeData);
    });
  }

  private void rebuildCloudLayerList() {
    cloudLayerTable.getSelectionModel().clearSelection();
    cloudLayerTable.getItems().clear();
    scene.getCloudLayers().forEach(cloudLayer -> cloudLayerTable.getItems().add(cloudLayer));
  }

  private void updateControls() {
    volumeSpecificControls.getChildren().clear();
    if (!fogVolumeTable.getSelectionModel().isEmpty()) {
      int index = fogVolumeTable.getSelectionModel().getSelectedIndex();
      FogVolume fogVolume = scene.getFogVolumes().get(index);

      HBox fogColorPickerBox = new HBox();
      fogColorPickerBox.setSpacing(10);
      Label label = new Label("Fog color:");
      LuxColorPicker luxColorPicker = new LuxColorPicker();
      luxColorPicker.setColor(ColorUtil.toFx(fogVolume.getMaterial().volumeColor));
      luxColorPicker.colorProperty().addListener(
        (observable, oldValue, newValue) -> {
          fogVolume.getMaterial().volumeColor.set(ColorUtil.fromFx(newValue));
          scene.refresh();
        });
      fogColorPickerBox.getChildren().addAll(label, luxColorPicker);

      DoubleAdjuster density = new DoubleAdjuster();
      density.setName("Fog density");
      density.setTooltip("Fog thickness");
      density.setMaximumFractionDigits(6);
      density.setRange(0.000001, 1);
      density.clampMin();
      density.set(fogVolume.getMaterial().volumeDensity);
      density.onValueChange(value -> {
        fogVolume.getMaterial().volumeDensity = value.floatValue();
        scene.refresh();
      });

      DoubleAdjuster anisotropy = new DoubleAdjuster();
      anisotropy.setName("Anisotropy");
      anisotropy.setTooltip("Changes the direction light is more likely to be scattered.\n" +
        "Positive values increase the chance light scatters into its original direction of travel.\n" +
        "Negative values increase the chance light scatters away from its original direction of travel");
      anisotropy.set(fogVolume.getMaterial().volumeAnisotropy);
      anisotropy.setRange(-1, 1);
      anisotropy.clampBoth();
      anisotropy.onValueChange(value -> {
        fogVolume.getMaterial().volumeAnisotropy = value.floatValue();
        scene.refresh();
      });

      DoubleAdjuster emittance = new DoubleAdjuster();
      emittance.setName("Emittance");
      emittance.setRange(0, 100);
      emittance.clampMin();
      emittance.set(fogVolume.getMaterial().volumeEmittance);
      emittance.onValueChange(value -> {
        fogVolume.getMaterial().volumeEmittance = value.floatValue();
        scene.refresh();
      });

      Separator separator = new Separator();

      volumeSpecificControls.getChildren().addAll(
        fogColorPickerBox,
        density,
        anisotropy,
        emittance,
        separator
      );

      volumeSpecificControls.getChildren().add(fogVolume.getControls(this));
    }
  }
}
