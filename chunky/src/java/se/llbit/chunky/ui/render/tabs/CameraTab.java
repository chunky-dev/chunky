/* Copyright (c) 2016-2020 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2020-2022 Chunky contributors
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

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.renderer.scene.camera.ApertureShape;
import se.llbit.chunky.renderer.scene.camera.CameraUtils;
import se.llbit.chunky.renderer.scene.camera.CameraViewListener;
import se.llbit.chunky.renderer.scene.camera.MutableCamera;
import se.llbit.chunky.renderer.scene.camera.Camera;
import se.llbit.chunky.renderer.scene.camera.CameraPreset;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.camera.projection.ProjectionMode;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CameraTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  @FXML private MenuButton loadPreset;
  @FXML private ComboBox<String> cameras;
  @FXML private Button duplicate;
  @FXML private Button removeCamera;
  @FXML private CheckBox lockCamera;
  @FXML private TitledPane positionOrientation;
  @FXML private DoubleTextField posX;
  @FXML private DoubleTextField posY;
  @FXML private DoubleTextField posZ;
  @FXML private DoubleTextField yawField;
  @FXML private DoubleTextField pitchField;
  @FXML private DoubleTextField rollField;
  @FXML private Button centerCamera;
  @FXML private ChoiceBox<ProjectionMode> projectionMode;
  @FXML private DoubleAdjuster fov;
  @FXML private DoubleAdjuster dof;
  @FXML private DoubleAdjuster subjectDistance;
  @FXML private DoubleTextField shiftX;
  @FXML private DoubleTextField shiftY;
  @FXML private Button autofocus;
  @FXML private ChoiceBox<ApertureShape> apertureShape;

  private MapView mapView;
  private CameraViewListener cameraViewListener;

  private boolean preventApertureCallback = false;

  public CameraTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("CameraTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void update(Scene scene) {
    updateCameraList();
    updateCameraPosition();
    updateCameraDirection();
    updateProjectionMode();
    updateFov();
    updateDof();
    updateSubjectDistance();
    updateShift();
    updateCameraLocked();
    preventApertureCallback = true;
    apertureShape.setValue(scene.camera().getApertureShape());
    preventApertureCallback = false;
  }

  @Override public String getTabTitle() {
    return "Camera";
  }

  @Override public Node getTabContent() {
    return this;
  }

  private void updateProjectionMode() {
    projectionMode.getSelectionModel().select(scene.camera().getProjectionMode());
  }

  private void updateSubjectDistance() {
    subjectDistance.set(scene.camera().getSubjectDistance());
  }

  private void updateDof() {
    dof.set(scene.camera().getDof());
  }

  private void updateFov() {
    fov.set(scene.camera().getFov());
  }

  private void updateShift() {
    shiftX.valueProperty().setValue(scene.camera().getShiftX());
    shiftY.valueProperty().setValue(scene.camera().getShiftY());
  }

  private void updateCameraLocked() {
    lockCamera.selectedProperty().setValue(scene.camera().getCameraLocked());
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    loadPreset.setTooltip(new Tooltip("Load a camera preset. Overwrites current camera settings."));
    for (CameraPreset preset : CameraPreset.values()) {
      MenuItem menuItem = new MenuItem(preset.toString());
      menuItem.setGraphic(new ImageView(preset.getIcon()));
      menuItem.setOnAction(e -> {
        MutableCamera camera = scene.camera();
        preset.apply(camera);
        projectionMode.getSelectionModel().select(camera.getProjectionMode());
        updateFov();
        updateCameraDirection();
      });
      loadPreset.getItems().add(menuItem);
    }

    ChangeListener<? super String> cameraSelectionListener = (observable, oldValue, newValue) -> {
      if (newValue != null && oldValue != null) {
        if (cameras.getItems().contains(newValue)) {
          // Save current camera and load existing camera preset.
          if (!cameras.getItems().contains(oldValue)) {
            cameras.getItems().add(oldValue);
          }
          scene.saveCameraPreset(oldValue);
          scene.loadCameraPreset(newValue);
          updateProjectionMode();
          updateFov();
          updateDof();
          updateSubjectDistance();
          updateCameraPosition();
          updateCameraDirection();
          updateShift();
          updateCameraLocked();
        } else {
          // Rename preset.
          scene.deleteCameraPreset(oldValue);
          scene.saveCameraPreset(newValue);
          scene.camera().name = newValue;
          updateCameraList();
        }
      }
    };
    cameras.getSelectionModel().selectedItemProperty().addListener(cameraSelectionListener);

    duplicate.setTooltip(new Tooltip("Create a copy of the current camera."));
    duplicate.setOnAction(e -> generateNextCameraName());

    removeCamera.setTooltip(new Tooltip("Delete the current camera."));
    removeCamera.setOnAction(e -> {
      String selected = cameras.getSelectionModel().getSelectedItem();
      if (selected != null && cameras.getItems().size() > 1) {
        cameras.getSelectionModel().selectedItemProperty().removeListener(cameraSelectionListener);
        cameras.getItems().remove(selected);
        scene.deleteCameraPreset(selected);
        String next = cameras.getValue();
        if (next == null) {
          next = cameras.getItems().get(0);
          cameras.setValue(next);
        }
        scene.loadCameraPreset(next);
        updateProjectionMode();
        updateFov();
        updateDof();
        updateSubjectDistance();
        updateCameraPosition();
        updateCameraDirection();
        updateShift();
        updateCameraLocked();
        cameras.getSelectionModel().selectedItemProperty().addListener(cameraSelectionListener);
      }
    });

    lockCamera.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.camera().setCameraLocked(newValue);
      loadPreset.setDisable(newValue);
      posX.setDisable(newValue);
      posY.setDisable(newValue);
      posZ.setDisable(newValue);
      centerCamera.setDisable(newValue);
      pitchField.setDisable(newValue);
      rollField.setDisable(newValue);
      yawField.setDisable(newValue);
      shiftX.setDisable(newValue);
      shiftY.setDisable(newValue);
      projectionMode.setDisable(newValue);
      fov.setDisable(newValue);
      dof.setDisable(newValue);
      subjectDistance.setDisable(newValue);
      autofocus.setDisable(newValue);
      apertureShape.setDisable(newValue);
    });

    positionOrientation.expandedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        updateCameraPosition();
        updateCameraDirection();
      }
    });

    EventHandler<KeyEvent> positionHandler = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        scene.camera()
            .setPosition(new Vector3(
                posX.valueProperty().get(),
                posY.valueProperty().get(),
                posZ.valueProperty().get()));
      }
    };
    posX.addEventFilter(KeyEvent.KEY_PRESSED, positionHandler);
    posY.addEventFilter(KeyEvent.KEY_PRESSED, positionHandler);
    posZ.addEventFilter(KeyEvent.KEY_PRESSED, positionHandler);

    EventHandler<KeyEvent> directionHandler = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        scene.camera()
            .setView(
                QuickMath.degToRad(yawField.valueProperty().get()),
                QuickMath.degToRad(pitchField.valueProperty().get()),
                QuickMath.degToRad(rollField.valueProperty().get()));
      }
    };
    yawField.addEventFilter(KeyEvent.KEY_PRESSED, directionHandler);
    pitchField.addEventFilter(KeyEvent.KEY_PRESSED, directionHandler);
    rollField.addEventFilter(KeyEvent.KEY_PRESSED, directionHandler);

    centerCamera.setOnAction(e -> {
      scene.moveCameraToCenter();
      updateCameraPosition();
    });

    projectionMode.getItems().addAll(ProjectionMode.REGISTRY.getProjectionPresets());
    projectionMode.getSelectionModel().select(ProjectionMode.PINHOLE);
    projectionMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          scene.camera().setProjectionPreset(newValue);
          apertureShape.setManaged(newValue == ProjectionMode.PINHOLE);
          scene.camera().setApertureShape(ApertureShape.CIRCLE);
          updateFov();
        });

    fov.setName("Field of view (zoom)");
    fov.setRange(0.1, 180);
    fov.clampMin();
    fov.onValueChange(value -> scene.camera().setFoV(value));

    dof.setName("Depth of field");
    dof.setRange(CameraUtils.MIN_DOF, CameraUtils.MAX_DOF);
    dof.clampMin();
    dof.makeLogarithmic();
    dof.setMaxInfinity(true);
    dof.onValueChange(value -> scene.camera().setDof(value));

    subjectDistance.setName("Subject distance");
    subjectDistance.setRange(CameraUtils.MIN_SUBJECT_DISTANCE, CameraUtils.MAX_SUBJECT_DISTANCE);
    subjectDistance.clampMax();
    subjectDistance.makeLogarithmic();
    subjectDistance.setTooltip("Distance to focal plane.");
    subjectDistance.onValueChange(value -> scene.camera().setSubjectDistance(value));

    autofocus.setTooltip(new Tooltip(
        "Focuses on the object that is the set target."));
    autofocus.setOnAction(e -> {
      scene.autoFocus();
      updateDof();
      updateSubjectDistance();
    });

    EventHandler<KeyEvent> shiftHandler = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        scene.camera().setShift(shiftX.valueProperty().get(), shiftY.valueProperty().get());
      }
    };
    shiftX.addEventFilter(KeyEvent.KEY_PRESSED, shiftHandler);
    shiftY.addEventFilter(KeyEvent.KEY_PRESSED, shiftHandler);

    apertureShape.setTooltip(new Tooltip("Change the shape of the aperture of the virtual camera."));
    apertureShape.getItems().addAll(ApertureShape.values());
    apertureShape.getSelectionModel().select(ApertureShape.CIRCLE);
    apertureShape.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
              if(preventApertureCallback)
                return;

              if(newValue == ApertureShape.CUSTOM) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose aperture mask");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Aperture mask", "*.png", "*.jpg"));
                File imageFile = fileChooser.showOpenDialog(getScene().getWindow());
                if (imageFile != null) {
                  scene.camera().setCustomApertureShape(imageFile.getAbsolutePath());
                } else {
                  // On cancel, go back to previous value
                  preventApertureCallback = true;
                  apertureShape.setValue(oldValue);
                  preventApertureCallback = false;
                }
              } else {
                scene.camera().setApertureShape(newValue);
              }
            });
  }

  private void generateNextCameraName() {
    int index = cameras.getItems().size() + 1;
    while (true) {
      boolean unique = true;
      String newName = String.format("camera %d", index);
      for (String name : cameras.getItems()) {
        if (name.equals(newName)) {
          unique = false;
          break;
        }
      }
      if (unique) {
        cameras.getItems().add(newName);
        cameras.setValue(newName);
        break;
      } else {
        index += 1;
      }
    }
  }

  private void updateCameraList() {
    cameras.getItems().clear();
    JsonObject presets = scene.getCameraPresets();
    for (JsonMember member : presets) {
      String name = member.getName().trim();
      if (!name.isEmpty()) {
        cameras.getItems().add(name);
      }
    }
    MutableCamera camera = scene.camera();
    if (!cameras.getItems().contains(camera.name)) {
      cameras.getItems().add(camera.name);
    }
    if (cameras.getValue() == null || cameras.getValue().isEmpty()) {
      cameras.setValue(camera.name);
    }
  }

  private void updateCameraPosition() {
    Camera camera = scene.camera();
    Vector3 pos = camera.getPosition();
    if (positionOrientation.isExpanded()) {
      posX.valueProperty().set(pos.x);
      posY.valueProperty().set(pos.y);
      posZ.valueProperty().set(pos.z);
    }
    if (PersistentSettings.getFollowCamera()) {
      mapView.panTo(pos);
    }
    cameraViewListener.cameraViewUpdated();
  }

  private void updateCameraDirection() {
    if (positionOrientation.isExpanded()) {
      Camera camera = scene.camera();
      yawField.valueProperty().set(QuickMath.radToDeg(camera.getYaw()));
      pitchField.valueProperty().set(QuickMath.radToDeg(camera.getPitch()));
      rollField.valueProperty().set(QuickMath.radToDeg(camera.getRoll()));
    }
    cameraViewListener.cameraViewUpdated();
  }

  @Override public void onChunksLoaded() {
    update(scene);
  }

  @Override public void setController(RenderControlsFxController controller) {
    this.mapView = controller.getChunkyController().getMapView();
    this.cameraViewListener = controller.getChunkyController();

    scene = controller.getRenderController().getSceneManager().getScene();
    scene.camera().setDirectionListener(this::updateCameraDirection);
    scene.camera().setPositionListener(this::updateCameraPosition);
    scene.camera().setProjectionListener(() -> {
      updateFov();
      cameraViewListener.cameraViewUpdated();
    });
  }
}
