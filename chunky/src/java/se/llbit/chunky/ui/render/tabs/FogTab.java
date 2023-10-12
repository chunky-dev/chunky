/* Copyright (c) 2016 - 2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016 - 2021 Chunky contributors
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import se.llbit.chunky.renderer.scene.*;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.FogVolumeTypeSelectorDialog;
import se.llbit.chunky.ui.elements.TextFieldLabelWrapper;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.ui.render.settings.LayeredFogSettings;
import se.llbit.chunky.ui.render.settings.UniformFogSettings;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.AABB;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector3;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FogTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  private static class FogVolumeData {
    final String type;

    FogVolumeData(FogVolume fogVolume) {
      this.type = fogVolume.getType().name();
    }
  }

  @FXML private ComboBox<FogMode> fogMode;
  @FXML private TitledPane fogDetailsPane;
  @FXML private VBox fogDetailsBox;
  @FXML private TableView<FogVolumeData> fogVolumeTable;
  @FXML private TableColumn<FogVolumeData, String> typeCol;
  @FXML private Button addVolume;
  @FXML private Button removeVolume;
  @FXML private VBox volumeSpecificControls;

  private final UniformFogSettings uniformFogSettings = new UniformFogSettings();
  private final LayeredFogSettings layeredFogSettings = new LayeredFogSettings();
  private final FogVolumeTypeSelectorDialog fogVolumeTypeSelectorDialog = new FogVolumeTypeSelectorDialog();

  public FogTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FogTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
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
    typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().type));
    typeCol.setSortable(false);

    addVolume.setOnAction(e -> {
      if (fogVolumeTypeSelectorDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        FogVolumeType type = fogVolumeTypeSelectorDialog.getType();
        scene.fog.addVolume(type);
        rebuildList();
        fogVolumeTable.getSelectionModel().selectLast();
      }
    });

    removeVolume.setOnAction(e -> {
      int index = fogVolumeTable.getSelectionModel().getSelectedIndex();
      scene.fog.removeVolume(index);
      rebuildList();
    });
  }

  @Override public void update(Scene scene) {
    fogMode.getSelectionModel().select(scene.fog.getFogMode());
    uniformFogSettings.update(scene);
    layeredFogSettings.update(scene);
    rebuildList();
  }

  private void rebuildList() {
    fogVolumeTable.getSelectionModel().clearSelection();
    fogVolumeTable.getItems().clear();
    for (FogVolume fogVolume : scene.fog.getFogVolumes()) {
      FogVolumeData fogVolumeData = new FogVolumeData(fogVolume);
      fogVolumeTable.getItems().add(fogVolumeData);
    }
  }

  private void updateControls() {
    volumeSpecificControls.getChildren().clear();
    if (!fogVolumeTable.getSelectionModel().isEmpty()) {
      int index = fogVolumeTable.getSelectionModel().getSelectedIndex();
      FogVolume fogVolume = scene.fog.getFogVolumes().get(index);

      HBox fogColorPickerBox = new HBox();
      fogColorPickerBox.setSpacing(10);
      Label label = new Label("Fog color:");
      LuxColorPicker luxColorPicker = new LuxColorPicker();
      luxColorPicker.setColor(ColorUtil.toFx(fogVolume.getColor()));
      luxColorPicker.colorProperty().addListener(
        (observable, oldValue, newValue) -> {
          fogVolume.setColor(ColorUtil.fromFx(newValue));
          scene.refresh();
        });
      fogColorPickerBox.getChildren().addAll(label, luxColorPicker);

      DoubleAdjuster density = new DoubleAdjuster();
      density.setName("Fog density");
      density.setTooltip("Fog thickness");
      density.setMaximumFractionDigits(6);
      density.setRange(0.000001, 1);
      density.clampMin();
      density.set(fogVolume.getDensity());
      density.onValueChange(value -> {
        fogVolume.setDensity(value);
        scene.refresh();
      });

      DoubleAdjuster anisotropy = new DoubleAdjuster();
      anisotropy.setName("Anisotropy");
      anisotropy.setTooltip("Changes the direction light is more likely to be scattered.\n" +
        "Positive values increase the chance light scatters into its original direction of travel.\n" +
        "Negative values increase the chance light scatters away from its original direction of travel");
      anisotropy.set(fogVolume.getAnisotropy());
      anisotropy.setRange(-1, 1);
      anisotropy.clampBoth();
      anisotropy.onValueChange(value -> {
        fogVolume.setAnisotropy(value.floatValue());
        scene.refresh();
      });

      DoubleAdjuster emittance = new DoubleAdjuster();
      emittance.setName("Emittance");
      emittance.setRange(0, 100);
      emittance.clampMin();
      emittance.set(fogVolume.getEmittance());
      emittance.onValueChange(value -> {
        fogVolume.setEmittance(value.floatValue());
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

      ColumnConstraints labelConstraints = new ColumnConstraints();
      labelConstraints.setHgrow(Priority.NEVER);
      labelConstraints.setPrefWidth(90);
      ColumnConstraints posFieldConstraints = new ColumnConstraints();
      posFieldConstraints.setMinWidth(20);
      posFieldConstraints.setPrefWidth(90);

      switch (fogVolume.getType()) {
        case EXPONENTIAL: {
          ExponentialFogVolume exponentialFogVolume = (ExponentialFogVolume) fogVolume;
          DoubleAdjuster scaleHeight = new DoubleAdjuster();
          scaleHeight.setName("Height scale");
          scaleHeight.setTooltip("Scales the vertical distribution of the fog");
          scaleHeight.setRange(1, 50);
          scaleHeight.set(exponentialFogVolume.getScaleHeight());
          scaleHeight.onValueChange(value -> {
            exponentialFogVolume.setScaleHeight(value);
            scene.refresh();
          });

          DoubleAdjuster yOffset = new DoubleAdjuster();
          yOffset.setName("Y-offset");
          yOffset.setTooltip("Y-offset (altitude) of the distribution");
          yOffset.setRange(-100, 100);
          yOffset.set(exponentialFogVolume.getYOffset());
          yOffset.onValueChange(value -> {
            exponentialFogVolume.setYOffset(value);
            scene.refresh();
          });

          volumeSpecificControls.getChildren().addAll(scaleHeight, yOffset);
          break;
        }
        case LAYER: {
          LayerFogVolume layerFogVolume = (LayerFogVolume) fogVolume;
          DoubleAdjuster layerBreadth = new DoubleAdjuster();
          layerBreadth.setName("Layer thickness");
          layerBreadth.setTooltip("Scales the vertical distribution of the fog");
          layerBreadth.setRange(0.001, 100);
          layerBreadth.set(layerFogVolume.getLayerBreadth());
          layerBreadth.clampMin();
          layerBreadth.onValueChange(value -> {
            layerFogVolume.setLayerBreadth(value);
            scene.refresh();
          });

          DoubleAdjuster yOffset = new DoubleAdjuster();
          yOffset.setName("Layer altitude");
          yOffset.setTooltip("Y-coordinate (altitude) of the fog layer");
          yOffset.setRange(-64, 320);
          yOffset.set(layerFogVolume.getYOffset());
          yOffset.onValueChange(value -> {
            layerFogVolume.setYOffset(value);
            scene.refresh();
          });

          volumeSpecificControls.getChildren().addAll(layerBreadth, yOffset);
          break;
        }
        case SPHERE: {
          SphericalFogVolume sphericalFogVolume = (SphericalFogVolume) fogVolume;

          DoubleTextField posX = new DoubleTextField();
          DoubleTextField posY = new DoubleTextField();
          DoubleTextField posZ = new DoubleTextField();

          posX.setTooltip(new Tooltip("Sphere x-coordinate (east/west)"));
          posY.setTooltip(new Tooltip("Sphere y-coordinate (up/down)"));
          posZ.setTooltip(new Tooltip("Sphere z-coordinate (south/north)"));

          Vector3 center = sphericalFogVolume.getCenter();
          posX.valueProperty().setValue(center.x);
          posY.valueProperty().setValue(center.y);
          posZ.valueProperty().setValue(center.z);

          posX.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              sphericalFogVolume.setCenterX(newValue.doubleValue());
              scene.refresh();
            });
          posY.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              sphericalFogVolume.setCenterY(newValue.doubleValue());
              scene.refresh();
            });
          posZ.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              sphericalFogVolume.setCenterZ(newValue.doubleValue());
              scene.refresh();
            });

          TextFieldLabelWrapper xText = new TextFieldLabelWrapper();
          TextFieldLabelWrapper yText = new TextFieldLabelWrapper();
          TextFieldLabelWrapper zText = new TextFieldLabelWrapper();

          xText.setTextField(posX);
          yText.setTextField(posY);
          zText.setTextField(posZ);

          xText.setLabelText("x:");
          yText.setLabelText("y:");
          zText.setLabelText("z:");

          Button toCamera = new Button();
          toCamera.setText("To camera");
          toCamera.setOnAction(event -> {
            Vector3 cameraPosition = new Vector3(scene.camera().getPosition());
            posX.valueProperty().setValue(cameraPosition.x);
            posY.valueProperty().setValue(cameraPosition.y);
            posZ.valueProperty().setValue(cameraPosition.z);
            scene.refresh();
          });

          Button toTarget = new Button();
          toTarget.setText("To target");
          toTarget.setOnAction(event -> {
            Vector3 targetPosition = scene.getTargetPosition();
            if (targetPosition != null) {
              posX.valueProperty().setValue(targetPosition.x);
              posY.valueProperty().setValue(targetPosition.y);
              posZ.valueProperty().setValue(targetPosition.z);
              scene.refresh();
            }
          });

          GridPane gridPane = new GridPane();
          gridPane.setHgap(6);
          gridPane.getColumnConstraints().addAll(
            labelConstraints,
            posFieldConstraints,
            posFieldConstraints,
            posFieldConstraints
          );
          gridPane.addRow(0, new Label("Center:"), xText, yText, zText);

          HBox hBox = new HBox();
          hBox.setSpacing(10);
          hBox.getChildren().addAll(toCamera, toTarget);

          DoubleAdjuster radius = new DoubleAdjuster();
          radius.setName("Radius");
          radius.setTooltip("Radius of the sphere");
          radius.setRange(0.001, 100);
          radius.set(sphericalFogVolume.getRadius());
          radius.clampMin();
          radius.onValueChange(value -> {
            sphericalFogVolume.setRadius(value);
            scene.refresh();
          });

          volumeSpecificControls.getChildren().addAll(gridPane, hBox, radius);
          break;
        }
        case CUBOID: {
          CuboidFogVolume cuboidFogVolume = (CuboidFogVolume) fogVolume;

          DoubleTextField x1 = new DoubleTextField();
          DoubleTextField y1 = new DoubleTextField();
          DoubleTextField z1 = new DoubleTextField();
          DoubleTextField x2 = new DoubleTextField();
          DoubleTextField y2 = new DoubleTextField();
          DoubleTextField z2 = new DoubleTextField();

          x1.setTooltip(new Tooltip("X-coordinate (east/west) of first corner"));
          y1.setTooltip(new Tooltip("Y-coordinate (up/down) of first corner"));
          z1.setTooltip(new Tooltip("Z-coordinate (south/north) of first corner"));
          x2.setTooltip(new Tooltip("X-coordinate (east/west) of second corner"));
          y2.setTooltip(new Tooltip("Y-coordinate (up/down) of second corner"));
          z2.setTooltip(new Tooltip("Z-coordinate (south/north) of second corner"));

          AABB bounds = cuboidFogVolume.getBounds();
          x1.valueProperty().setValue(bounds.xmin);
          y1.valueProperty().setValue(bounds.ymin);
          z1.valueProperty().setValue(bounds.zmin);
          x2.valueProperty().setValue(bounds.xmax);
          y2.valueProperty().setValue(bounds.ymax);
          z2.valueProperty().setValue(bounds.zmax);

          x1.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              cuboidFogVolume.setBounds(
                Math.min(newValue.doubleValue(), x2.valueProperty().doubleValue()),
                Math.max(newValue.doubleValue(), x2.valueProperty().doubleValue()),
                Math.min(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.max(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.min(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue()),
                Math.max(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue())
              );
              scene.refresh();
            });
          y1.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              cuboidFogVolume.setBounds(
                Math.min(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.max(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.min(newValue.doubleValue(), y2.valueProperty().doubleValue()),
                Math.max(newValue.doubleValue(), y2.valueProperty().doubleValue()),
                Math.min(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue()),
                Math.max(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue())
              );
              scene.refresh();
            });
          z1.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              cuboidFogVolume.setBounds(
                Math.min(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.max(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.min(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.max(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.min(newValue.doubleValue(), z2.valueProperty().doubleValue()),
                Math.max(newValue.doubleValue(), z2.valueProperty().doubleValue())
              );
              scene.refresh();
            });
          x2.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              cuboidFogVolume.setBounds(
                Math.min(x1.valueProperty().doubleValue(), newValue.doubleValue()),
                Math.max(x1.valueProperty().doubleValue(), newValue.doubleValue()),
                Math.min(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.max(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.min(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue()),
                Math.max(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue())
              );
              scene.refresh();
            });
          y2.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              cuboidFogVolume.setBounds(
                Math.min(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.max(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.min(y1.valueProperty().doubleValue(), newValue.doubleValue()),
                Math.max(y1.valueProperty().doubleValue(), newValue.doubleValue()),
                Math.min(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue()),
                Math.max(z1.valueProperty().doubleValue(), z2.valueProperty().doubleValue())
              );
              scene.refresh();
            });
          z2.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
              cuboidFogVolume.setBounds(
                Math.min(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.max(x1.valueProperty().doubleValue(), x2.valueProperty().doubleValue()),
                Math.min(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.max(y1.valueProperty().doubleValue(), y2.valueProperty().doubleValue()),
                Math.min(z1.valueProperty().doubleValue(), newValue.doubleValue()),
                Math.max(z1.valueProperty().doubleValue(), newValue.doubleValue())
              );
              scene.refresh();
            });

          TextFieldLabelWrapper x1Text = new TextFieldLabelWrapper();
          TextFieldLabelWrapper y1Text = new TextFieldLabelWrapper();
          TextFieldLabelWrapper z1Text = new TextFieldLabelWrapper();
          TextFieldLabelWrapper x2Text = new TextFieldLabelWrapper();
          TextFieldLabelWrapper y2Text = new TextFieldLabelWrapper();
          TextFieldLabelWrapper z2Text = new TextFieldLabelWrapper();

          x1Text.setTextField(x1);
          y1Text.setTextField(y1);
          z1Text.setTextField(z1);
          x2Text.setTextField(x2);
          y2Text.setTextField(y2);
          z2Text.setTextField(z2);

          x1Text.setLabelText("x:");
          y1Text.setLabelText("y:");
          z1Text.setLabelText("z:");
          x2Text.setLabelText("x:");
          y2Text.setLabelText("y:");
          z2Text.setLabelText("z:");

          Button pos1ToCamera = new Button();
          pos1ToCamera.setText("To camera");
          pos1ToCamera.setOnAction(event -> {
            Vector3 cameraPosition = new Vector3(scene.camera().getPosition());
            x1.valueProperty().setValue(cameraPosition.x);
            y1.valueProperty().setValue(cameraPosition.y);
            z1.valueProperty().setValue(cameraPosition.z);
            scene.refresh();
          });

          Button pos1ToTarget = new Button();
          pos1ToTarget.setText("To target");
          pos1ToTarget.setOnAction(event -> {
            Vector3 targetPosition = scene.getTargetPosition();
            if (targetPosition != null) {
              x1.valueProperty().setValue(targetPosition.x);
              y1.valueProperty().setValue(targetPosition.y);
              z1.valueProperty().setValue(targetPosition.z);
              scene.refresh();
            }
          });

          Button pos2ToCamera = new Button();
          pos2ToCamera.setText("To camera");
          pos2ToCamera.setOnAction(event -> {
            Vector3 cameraPosition = new Vector3(scene.camera().getPosition());
            x2.valueProperty().setValue(cameraPosition.x);
            y2.valueProperty().setValue(cameraPosition.y);
            z2.valueProperty().setValue(cameraPosition.z);
            scene.refresh();
          });

          Button pos2ToTarget = new Button();
          pos2ToTarget.setText("To target");
          pos2ToTarget.setOnAction(event -> {
            Vector3 targetPosition = scene.getTargetPosition();
            if (targetPosition != null) {
              x2.valueProperty().setValue(targetPosition.x);
              y2.valueProperty().setValue(targetPosition.y);
              z2.valueProperty().setValue(targetPosition.z);
              scene.refresh();
            }
          });

          GridPane gridPane1 = new GridPane();
          gridPane1.setHgap(6);
          gridPane1.getColumnConstraints().addAll(
            labelConstraints,
            posFieldConstraints,
            posFieldConstraints,
            posFieldConstraints
          );
          gridPane1.addRow(0, new Label("Corner 1:"), x1Text, y1Text, z1Text);

          HBox hBox1 = new HBox();
          hBox1.setSpacing(10);
          hBox1.getChildren().addAll(pos1ToCamera, pos1ToTarget);

          GridPane gridPane2 = new GridPane();
          gridPane2.setHgap(6);
          gridPane2.getColumnConstraints().addAll(
            labelConstraints,
            posFieldConstraints,
            posFieldConstraints,
            posFieldConstraints
          );
          gridPane2.addRow(1, new Label("Corner 2:"), x2Text, y2Text, z2Text);

          HBox hBox2 = new HBox();
          hBox2.setSpacing(10);
          hBox2.getChildren().addAll(pos2ToCamera, pos2ToTarget);

          volumeSpecificControls.getChildren().addAll(gridPane1, hBox1, gridPane2, hBox2);
          break;
        }
      }
    }
  }

  @Override public String getTabTitle() {
    return "Fog";
  }

  @Override public Node getTabContent() {
    return this;
  }
}
