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

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import se.llbit.chunky.entity.ArmorStand;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.Geared;
import se.llbit.chunky.entity.PlayerEntity;
import se.llbit.chunky.entity.Poseable;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.AngleAdjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.math.Vector3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

public class EntitiesTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  static class EntityData {
    public final Entity entity;
    public final String name;
    private String kind;

    public EntityData(Entity entity, Scene scene) {
      this.entity = entity;

      if (entity instanceof PlayerEntity) {
        JsonObject profile = scene.getPlayerProfile((PlayerEntity) entity);
        name = getName(profile);
        kind = "Player";
      } else {
        if (entity instanceof ArmorStand) {
          kind = "Armor stand";
        } else {
          kind = "Other";
        }
        name = "entity";
      }
    }

    private static String getName(JsonObject profile) {
      return profile.get("name").stringValue("Unknown");
    }

    @Override public String toString() {
      return "" + entity;
    }

    @Override public int hashCode() {
      return entity.hashCode();
    }

    @Override public boolean equals(Object obj) {
      // Identity comparison is used to ensure that the table in the
      // entities tab is properly updated after rebuilding the scene.
      if (obj instanceof EntityData) {
        return ((EntityData) obj).entity == entity;
      }
      return false;
    }

    public String getKind() {
      return kind;
    }
  }

  @FXML private TableView<EntityData> entityTable;
  @FXML private TableColumn<EntityData, String> nameCol;
  @FXML private TableColumn<EntityData, String> kindCol;
  @FXML private Button delete;
  @FXML private Button add;
  @FXML private Button cameraToPlayer;
  @FXML private Button playerToCamera;
  @FXML private Button playerToTarget;
  @FXML private Button faceCamera;
  @FXML private Button faceTarget;
  @FXML private VBox controls;

  public EntitiesTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("EntitiesTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void update(Scene scene) {
    // TODO: it might be better to always just rebuild the whole table.
    Collection<EntityData> missing = new HashSet<>(entityTable.getItems());
    for (Entity entity : scene.getActors()) {
      EntityData data = new EntityData(entity, scene);
      if (!entityTable.getItems().contains(data)) {
        entityTable.getItems().add(data);
      }
      missing.remove(data);
    }
    entityTable.getItems().removeAll(missing);
  }

  @Override public String getTabTitle() {
    return "Entities";
  }

  @Override public Node getTabContent() {
    return this;
  }

  private void updateEntity(Entity entity) {
    controls.getChildren().clear();
    if (entity instanceof Poseable) {
      Poseable poseable = (Poseable) entity;
      if (entity instanceof PlayerEntity) {
        PlayerEntity player = (PlayerEntity) entity;
        ChoiceBox<PlayerModel> playerModel = new ChoiceBox<>();
        playerModel.getSelectionModel().select(((PlayerEntity) entity).model);
        playerModel.getItems().addAll(PlayerModel.values());
        playerModel.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
              player.model = newValue;
              scene.rebuildActorBvh();
            });
        HBox modelBox = new HBox();
        modelBox.setSpacing(10.0);
        modelBox.setAlignment(Pos.CENTER_LEFT);
        modelBox.getChildren().addAll(new Label("Player model:"), playerModel);

        HBox skinBox = new HBox();
        skinBox.setSpacing(10.0);
        skinBox.setAlignment(Pos.CENTER_LEFT);
        TextField skinField = new TextField();
        skinField.setText(((PlayerEntity) entity).skin);
        Button selectSkin = new Button("Select skin...");
        selectSkin.setOnAction(e -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Load Skin");
          fileChooser
              .getExtensionFilters().add(new FileChooser.ExtensionFilter("Minecraft skin", "*.png"));
          File skinFile = fileChooser.showOpenDialog(getScene().getWindow());
          if (skinFile != null) {
            player.setTexture(skinFile.getAbsolutePath());
            skinField.setText(skinFile.getAbsolutePath());
            scene.rebuildActorBvh();
          }
        });
        skinBox.getChildren().addAll(new Label("Skin:"), skinField, selectSkin);

        controls.getChildren().addAll(modelBox, skinBox);
      }

      DoubleAdjuster scale = new DoubleAdjuster();

      scale.setName("Scale");
      scale.setTooltip("Modifies entity scale.");
      scale.set(poseable.getScale());
      scale.setRange(0.1, 10);
      scale.onValueChange(value -> {
        poseable.setScale(value);
        scene.rebuildActorBvh();
      });

      DoubleAdjuster headScale = new DoubleAdjuster();
      headScale.setName("Head scale");
      headScale.setTooltip("Modifies entity head scale.");
      headScale.set(poseable.getHeadScale());
      headScale.setRange(0.1, 10);
      headScale.onValueChange(value -> {
        poseable.setHeadScale(value);
        scene.rebuildActorBvh();
      });

      ChoiceBox<String> partList = new ChoiceBox<>();
      partList.setTooltip(new Tooltip("Select the part of the entity to adjust."));
      partList.getItems().setAll(poseable.partNames());

      HBox poseBox = new HBox();
      poseBox.setSpacing(10.0);
      poseBox.setAlignment(Pos.CENTER_LEFT);
      poseBox.getChildren().addAll(new Label("Pose part"), partList);

      AngleAdjuster yaw = new AngleAdjuster();
      yaw.setTooltip("Modifies yaw of currently selected entity part.");
      yaw.setName("yaw");

      AngleAdjuster pitch = new AngleAdjuster();
      pitch.setTooltip("Modifies pitch of currently selected entity part.");
      pitch.setName("pitch");

      AngleAdjuster roll = new AngleAdjuster();
      roll.setTooltip("Modifies roll of currently selected entity part.");
      roll.setName("roll");

      partList.getSelectionModel().selectedItemProperty().addListener(
          (observable, oldValue, part) ->
              withPose(entity, part, partPose -> {
                pitch.set(partPose.get(0).asDouble(0));
                yaw.set(partPose.get(1).asDouble(0));
                roll.set(partPose.get(2).asDouble(0));
            }
          ));

      partList.getSelectionModel().selectFirst(); // Updates the pose parameters.

      pitch.onValueChange(value -> {
        withPose(entity, partList.getValue(), partPose -> {
          partPose.set(0, Json.of(Math.toRadians(value)));
        });
        scene.rebuildActorBvh();
      });
      yaw.onValueChange(value -> {
        withPose(entity, partList.getValue(), partPose -> {
          partPose.set(1, Json.of(Math.toRadians(value)));
        });
        scene.rebuildActorBvh();
      });
      roll.onValueChange(value -> {
        withPose(entity, partList.getValue(), partPose -> {
          partPose.set(2, Json.of(Math.toRadians(value)));
        });
        scene.rebuildActorBvh();
      });

      controls.getChildren().addAll(scale, headScale, poseBox, pitch, yaw, roll);
    }

    if (entity instanceof Geared) {
      Geared geared = (Geared) entity;
      controls.getChildren().addAll(new Label("Gear:"));
      for (String slot : geared.gearSlots()) {
        HBox slotBox = new HBox();
        slotBox.setSpacing(10.0);
        slotBox.setAlignment(Pos.BASELINE_LEFT);
        TextField gearField = new TextField();
        gearField.setOnAction(event -> {
          JsonObject gear = new JsonObject();
          gear.add("id", gearField.getText());
          geared.getGear().set(slot, gear);
          scene.rebuildActorBvh();
        });
        gearField.setText(geared.getGear(slot).get("id").stringValue(""));
        slotBox.getChildren().addAll(new Label(slot + ":"), gearField);
        controls.getChildren().add(slotBox);
      }
    }
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    add.setTooltip(new Tooltip("Add a player at the target position."));
    add.setOnAction(e -> {
      Collection<Entity> entities = scene.getActors();
      Set<String> ids = new HashSet<>();
      for (Entity entity : entities) {
        if (entity instanceof PlayerEntity) {
          ids.add(((PlayerEntity) entity).uuid);
        }
      }
      // Pick a new UUID for the new entity.
      long id = System.currentTimeMillis();
      while (ids.contains(String.format("%016X%016X", 0, id))) {
        id += 1;
      }
      Vector3 position = scene.getTargetPosition();
      if (position == null) {
        position = new Vector3(scene.camera().getPosition());
      }
      PlayerEntity player = new PlayerEntity(String.format("%016X%016X", 0, id), position);
      withEntity(selected -> {
        if (selected instanceof PlayerEntity) {
          player.skin = ((PlayerEntity) selected).skin;
          player.model = ((PlayerEntity) selected).model;
        }
      });
      player.randomPoseAndLook();
      scene.addPlayer(player);
      EntityData data = new EntityData(player, scene);
      entityTable.getItems().add(data);
      entityTable.getSelectionModel().select(data);
    });
    delete.setTooltip(new Tooltip("Delete the selected player."));
    delete.setOnAction(e -> withEntity(entity -> {
      scene.removeEntity(entity);
      update(scene);
    }));
    // TODO: remove or update the pose editing dialog.
    /*entityTable.setRowFactory(tbl -> {
      TableRow<PlayerData> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()) {
          e.consume();
          try {
            Poser poser = new Poser(row.getItem());
            poser.show();
          } catch (IOException e1) {
            Log.warn("Could not open player poser window.", e1);
          }
        }
      });
      return row;
    });*/
    cameraToPlayer.setTooltip(new Tooltip("Move the camera to the selected player position."));
    cameraToPlayer.setOnAction(e -> withEntity(player -> scene.camera().moveToPlayer(player)));
    playerToCamera.setTooltip(new Tooltip("Move the selected player to the camera position."));
    playerToCamera.setOnAction(e -> withEntity(entity -> {
      entity.setPosition(scene.camera().getPosition());
      scene.rebuildActorBvh();
    }));
    playerToTarget.setTooltip(new Tooltip("Move the selected player to the current target."));
    playerToTarget.setOnAction(e -> withEntity(player -> {
      Vector3 target = scene.getTargetPosition();
      if (target != null) {
        player.position.set(target);
        scene.rebuildActorBvh();
      }
    }));
    faceCamera.setTooltip(new Tooltip("Makes the selected player look at the camera."));
    faceCamera.setOnAction(e -> withEntity(entity -> {
      if (entity instanceof Poseable) {
        Poseable player = (Poseable) entity;
        player.lookAt(scene.camera().getPosition());
        scene.rebuildActorBvh();
      }
    }));
    faceTarget.setTooltip(new Tooltip("Makes the selected player look at the current view target."));
    faceTarget.setOnAction(e -> withEntity(entity -> {
      Vector3 target = scene.getTargetPosition();
      if (target != null && entity instanceof Poseable) {
        Poseable player = (Poseable) entity;
        player.lookAt(target);
        scene.rebuildActorBvh();
      }
    }));
    entityTable.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, entityData) -> {
          if (entityData != null) {
            updateEntity(entityData.entity);
          }
        });
    nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().name));
    kindCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getKind()));
  }

  private void withEntity(Consumer<Entity> consumer) {
    EntityData player = entityTable.getSelectionModel().getSelectedItem();
    if (player != null) {
      consumer.accept(player.entity);
    }
  }

  private void withPose(Entity entity, String part, Consumer<JsonArray> consumer) {
    if (entity instanceof Poseable && part != null && !part.isEmpty()) {
      Poseable poseable = (Poseable) entity;
      JsonArray pose = poseable.getPose().get(part).array();
      if (pose.size() < 3) {
        // Set default pose to [0, 0, 0].
        pose = new JsonArray(3);
        pose.add(0);
        pose.add(0);
        pose.add(0);
        poseable.getPose().set(part, pose);
      }
      consumer.accept(pose);
    }
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }
}
