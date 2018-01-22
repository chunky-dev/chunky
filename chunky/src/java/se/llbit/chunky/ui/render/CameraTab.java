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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.NumberStringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.renderer.CameraViewListener;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.CameraPreset;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CameraTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  @FXML private MenuButton loadPreset;
  @FXML private ComboBox<String> cameras;
  @FXML private Button duplicate;
  @FXML private Button removeCamera;
  @FXML private TitledPane positionOrientation;
  @FXML private TextField posX;
  @FXML private TextField posY;
  @FXML private TextField posZ;
  @FXML private TextField yawField;
  @FXML private TextField pitchField;
  @FXML private TextField rollField;
  @FXML private Button cameraToPlayer;
  @FXML private Button centerCamera;
  @FXML private ChoiceBox<ProjectionMode> projectionMode;
  @FXML private DoubleAdjuster fov;
  @FXML private DoubleAdjuster dof;
  @FXML private DoubleAdjuster subjectDistance;
  @FXML private Button autofocus;

  private DoubleProperty xpos = new SimpleDoubleProperty();
  private DoubleProperty ypos = new SimpleDoubleProperty();
  private DoubleProperty zpos = new SimpleDoubleProperty();
  private DoubleProperty yaw = new SimpleDoubleProperty();
  private DoubleProperty pitch = new SimpleDoubleProperty();
  private DoubleProperty roll = new SimpleDoubleProperty();
  private MapView mapView;
  private CameraViewListener cameraViewListener;

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

  @Override public void initialize(URL location, ResourceBundle resources) {
    loadPreset.setTooltip(new Tooltip("Load a camera preset. Overwrites current camera settings."));
    for (CameraPreset preset : CameraPreset.values()) {
      MenuItem menuItem = new MenuItem(preset.toString());
      menuItem.setGraphic(new ImageView(preset.getIcon()));
      menuItem.setOnAction(e -> {
        Camera camera = scene.camera();
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
        } else {
          // Create new camera preset.
          cameras.getItems().add(newValue);
          scene.saveCameraPreset(oldValue);
          scene.camera().name = newValue;
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
        cameras.getSelectionModel().selectedItemProperty().addListener(cameraSelectionListener);
      }
    });

    positionOrientation.expandedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        updateCameraPosition();
        updateCameraDirection();
      }
    });

    posX.textProperty().bindBidirectional(xpos, new NumberStringConverter());
    posY.textProperty().bindBidirectional(ypos, new NumberStringConverter());
    posZ.textProperty().bindBidirectional(zpos, new NumberStringConverter());

    EventHandler<KeyEvent> positionHandler = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        scene.camera()
            .setPosition(new Vector3(xpos.getValue(), ypos.get(), zpos.get()));
      }
    };
    posX.addEventFilter(KeyEvent.KEY_PRESSED, positionHandler);
    posY.addEventFilter(KeyEvent.KEY_PRESSED, positionHandler);
    posZ.addEventFilter(KeyEvent.KEY_PRESSED, positionHandler);

    yawField.textProperty().bindBidirectional(yaw, new NumberStringConverter());
    pitchField.textProperty().bindBidirectional(pitch, new NumberStringConverter());
    rollField.textProperty().bindBidirectional(roll, new NumberStringConverter());

    EventHandler<KeyEvent> directionHandler = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        scene.camera()
            .setView(QuickMath.degToRad(yaw.get()), QuickMath.degToRad(pitch.get()),
                QuickMath.degToRad(roll.get()));
      }
    };
    yawField.setTooltip(new Tooltip("Camera yaw."));
    yawField.addEventFilter(KeyEvent.KEY_PRESSED, directionHandler);
    pitchField.setTooltip(new Tooltip("Camera pitch."));
    pitchField.addEventFilter(KeyEvent.KEY_PRESSED, directionHandler);
    rollField.setTooltip(new Tooltip("Camera roll."));
    rollField.addEventFilter(KeyEvent.KEY_PRESSED, directionHandler);

    cameraToPlayer.setTooltip(new Tooltip("Move camera to the player position."));
    cameraToPlayer.setOnAction(e -> {
      scene.moveCameraToPlayer();
      updateCameraPosition();
    });

    centerCamera.setTooltip(new Tooltip("Center camera above loaded chunks."));
    centerCamera.setOnAction(e -> {
      scene.moveCameraToCenter();
      updateCameraPosition();
    });

    projectionMode.getItems().addAll(ProjectionMode.values());
    projectionMode.getSelectionModel().select(ProjectionMode.PINHOLE);
    projectionMode.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          scene.camera().setProjectionMode(newValue);
          updateFov();
        });

    fov.setName("Field of view (zoom)");
    fov.setRange(0.1, 180);
    fov.clampMin();
    fov.onValueChange(value -> scene.camera().setFoV(value));

    dof.setName("Depth of field");
    dof.setRange(Camera.MIN_DOF, Camera.MAX_DOF);
    dof.clampMin();
    dof.makeLogarithmic();
    dof.setMaxInfinity(true);
    dof.onValueChange(value -> scene.camera().setDof(value));

    subjectDistance.setName("Subject distance");
    subjectDistance.setRange(Camera.MIN_SUBJECT_DISTANCE, Camera.MAX_SUBJECT_DISTANCE);
    subjectDistance.clampMax();
    subjectDistance.makeLogarithmic();
    subjectDistance.setTooltip("Distance to focal plane.");
    subjectDistance.onValueChange(value -> scene.camera().setSubjectDistance(value));

    autofocus.setTooltip(new Tooltip(
        "Focuses on the object in the center of the view indicated by the crosshairs."));
    autofocus.setOnAction(e -> {
      scene.autoFocus();
      updateDof();
      updateSubjectDistance();
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
    Camera camera = scene.camera();
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
      xpos.set(pos.x);
      ypos.set(pos.y);
      zpos.set(pos.z);
    }
    if (PersistentSettings.getFollowCamera()) {
      mapView.panTo(pos);
    }
    cameraViewListener.cameraViewUpdated();
  }

  private void updateCameraDirection() {
    if (positionOrientation.isExpanded()) {
      Camera camera = scene.camera();
      yaw.set(QuickMath.radToDeg(camera.getYaw()));
      pitch.set(QuickMath.radToDeg(camera.getPitch()));
      roll.set(QuickMath.radToDeg(camera.getRoll()));
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
