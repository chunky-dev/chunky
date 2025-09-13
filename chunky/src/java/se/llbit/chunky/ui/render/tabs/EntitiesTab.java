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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import se.llbit.chunky.entity.*;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.dialogs.AddEntityDialog;
import se.llbit.chunky.ui.dialogs.EditMaterialDialog;
import se.llbit.chunky.ui.elements.AngleAdjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se.llbit.chunky.entity.*;
import se.llbit.chunky.world.material.DyedTextureMaterial;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.util.mojangapi.MinecraftProfile;

public class EntitiesTab extends RenderControlsTab implements Initializable {
  public static final Map<String, EntitiesTab.EntityType<?>> entityTypes = new HashMap<>();

  static {
    entityTypes.put("Player", (position, scene) -> {
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
      PlayerEntity player = new PlayerEntity(String.format("%016X%016X", 0, id), position);
      player.randomPoseAndLook();
      return player;
    });
    entityTypes.put("Armor stand", (position, scene) -> new ArmorStand(position, new CompoundTag()));
    entityTypes.put("Lectern", (position, scene) -> new Lectern(position, "north", true));
    entityTypes.put("Book", (position, scene) -> new Book(position, Math.PI - Math.PI / 16, Math.toRadians(30), Math.toRadians(180 - 30)));
    entityTypes.put("Beacon beam", (position, scene) -> new BeaconBeam(position));
    entityTypes.put("Sphere", (position, scene) -> new SphereEntity(position, 0.5));
    entityTypes.put("Sheep", (position, scene) -> new SheepEntity(position, new CompoundTag()));
    entityTypes.put("Cow", (position, scene) -> new CowEntity(position, new CompoundTag()));
    entityTypes.put("Chicken", (position, scene) -> new ChickenEntity(position, new CompoundTag()));
    entityTypes.put("Pig", (position, scene) -> new PigEntity(position, new CompoundTag()));
    entityTypes.put("Mooshroom", (position, scene) -> new MooshroomEntity(position, new CompoundTag()));
    entityTypes.put("Squid", (position, scene) -> new SquidEntity(position, new CompoundTag()));
  }

  public enum EntityPlacement {
    TARGET("Preview target position"),
    CAMERA("Camera position"),
    POSITION("Specific position");

    private final String name;

    EntityPlacement(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public interface EntityType<T extends Entity> {
    T createInstance(Vector3 position, Scene scene);
  }

  public static class EntityData {

    public final Entity entity;
    public final String name;
    private String kind;

    public EntityData(Entity entity, Scene scene) {
      this.entity = entity;

      if (entity instanceof PlayerEntity) {
        MinecraftProfile profile = scene.getPlayerProfile((PlayerEntity) entity);
        name = profile != null && profile.name != null ? profile.name : "Unknown";
        kind = "Player";
      } else {
        if (entity instanceof ArmorStand) {
          kind = "Armor stand";
        } else {
          kind = entity.getClass().getSimpleName();
        }
        name = "entity";
      }
    }

    @Override
    public String toString() {
      return "" + entity;
    }

    @Override
    public int hashCode() {
      return entity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
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
  @FXML private Button clear;
  @FXML private Button cameraToEntity;
  @FXML private Button entityToCamera;
  @FXML private Button entityToTarget;
  @FXML private Button faceCamera;
  @FXML private Button faceTarget;
  @FXML private GridPane position;
  @FXML private DoubleTextField posX;
  @FXML private DoubleTextField posY;
  @FXML private DoubleTextField posZ;
  @FXML private VBox controls;

  private final AddEntityDialog addEntityDialog = new AddEntityDialog();

  public EntitiesTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("EntitiesTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void update(Scene scene) {
    // TODO: it might be better to always just rebuild the whole table.
    Collection<EntityData> missing = new HashSet<>(entityTable.getItems());
    for (Entity entity : scene.getActors()) {
      EntityData data = new EntityData(entity, scene);
      if (!entityTable.getItems().contains(data)) {
        entityTable.getItems().add(data);
      }
      missing.remove(data);
    }
    for (Entity entity : scene.getEntities()) {
      EntityData data = new EntityData(entity, scene);
      if (!entityTable.getItems().contains(data)) {
        entityTable.getItems().add(data);
      }
      missing.remove(data);
    }
    entityTable.getItems().removeAll(missing);
  }

  @Override
  public String getTabTitle() {
    return "Entities";
  }

  @Override
  public VBox getTabContent() {
    return this;
  }

  private void updateEntity(Entity entity) {
    controls.getChildren().clear();

    updatePositionFields(entity);
    posX.valueProperty().addListener((observable, oldValue, newValue) -> {
      withEntity(e -> {
        Vector3 currentPosition = e.getPosition();
        e.setPosition(new Vector3(newValue.doubleValue(), currentPosition.y, currentPosition.z));
      });
      scene.rebuildBvh();
      scene.rebuildActorBvh();
    });
    posY.valueProperty().addListener((observable, oldValue, newValue) -> {
      withEntity(e -> {
        Vector3 currentPosition = e.getPosition();
        e.setPosition(new Vector3(currentPosition.x, newValue.doubleValue(), currentPosition.z));
      });
      scene.rebuildBvh();
      scene.rebuildActorBvh();
    });
    posZ.valueProperty().addListener((observable, oldValue, newValue) -> {
      withEntity(e -> {
        Vector3 currentPosition = e.getPosition();
        e.setPosition(new Vector3(currentPosition.x, currentPosition.y, newValue.doubleValue()));
      });
      scene.rebuildBvh();
      scene.rebuildActorBvh();
    });

    controls.getChildren().add(position);
    position.setVisible(true);

    controls.getChildren().add(new Separator());

    controls.getChildren().add(entity.getControls(this));

    controls.getChildren().add(new Separator());

    if (entity instanceof Poseable) {
      Poseable poseable = (Poseable) entity;

      DoubleAdjuster scale = new DoubleAdjuster();
      scale.setName("Scale");
      scale.setTooltip("Modifies entity scale.");
      scale.set(poseable.getScale());
      scale.setRange(0.1, 10);
      scale.onValueChange(value -> {
        poseable.setScale(value);
        scene.rebuildActorBvh();
      });
      controls.getChildren().add(scale);

      if (poseable.hasHead()) {
        DoubleAdjuster headScale = new DoubleAdjuster();
        headScale.setName("Head scale");
        headScale.setTooltip("Modifies entity head scale.");
        headScale.set(poseable.getHeadScale());
        headScale.setRange(0.1, 10);
        headScale.onValueChange(value -> {
          poseable.setHeadScale(value);
          scene.rebuildActorBvh();
        });
        controls.getChildren().add(headScale);
      }

      String[] partNames = poseable.partNames();
      ChoiceBox<String> partList = new ChoiceBox<>();
      partList.setTooltip(new Tooltip("Select the part of the entity to adjust."));
      partList.getItems().setAll(poseable.partNames());

      HBox poseBox = new HBox();
      poseBox.setSpacing(10.0);
      poseBox.setAlignment(Pos.CENTER_LEFT);
      poseBox.getChildren().addAll(new Label("Pose part"), partList);
      if (partNames.length > 1) {
        controls.getChildren().add(poseBox);
      }

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
                    pitch.set(Math.toDegrees(partPose.get(0).asDouble(0)));
                    yaw.set(Math.toDegrees(partPose.get(1).asDouble(0)));
                    roll.set(Math.toDegrees(partPose.get(2).asDouble(0)));
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

      if (partNames.length > 0) {
        controls.getChildren().addAll(pitch, yaw, roll);
      }
    }

    controls.getChildren().add(new Separator());

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
          if (!gearField.getText().trim().isEmpty()) {
            gear.add("id", gearField.getText());
          }
          geared.getGear().set(slot, gear);
          scene.rebuildActorBvh();
        });
        gearField.setText(geared.getGear(slot).get("id").stringValue(""));
        slotBox.getChildren().addAll(new Label(slot + ":"), gearField);
        // Hide these fields to avoid user confusion because they do not actually work.
        if (slot.equals("leftHand") || slot.equals("rightHand")) {
          slotBox.setVisible(false);
          slotBox.setManaged(false);
        }
        controls.getChildren().add(slotBox);
      }
    }

    if (entity instanceof Variant) {
      Variant variant = (Variant) entity;

      HBox variantHBox = new HBox();
      variantHBox.setSpacing(10.0);

      ComboBox<String> variantBox = new ComboBox<>();
      variantBox.getItems().addAll(variant.variants());
      variantBox.setValue(variant.getVariant());
      variantBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
        variant.setVariant(newValue);
        scene.rebuildActorBvh();
      }));

      variantHBox.getChildren().addAll(new Label("Variant:"), variantBox);
      controls.getChildren().addAll(variantHBox);
    }

    if (entity instanceof Dyeable) {
      Dyeable dyedEntity = (Dyeable) entity;

      DyedTextureMaterial material = dyedEntity.getMaterial();

      LuxColorPicker sheepColorPicker = new LuxColorPicker();
      sheepColorPicker.setColor(ColorUtil.toFx(material.getColorInt()));
      sheepColorPicker.colorProperty().addListener(
        (observableColor, oldColorValue, newColorValue) -> {
          dyedEntity.getMaterial().updateColor(ColorUtil.getRGB(ColorUtil.fromFx(newColorValue)));
        }
      );

      Button editMaterialButton = new Button("Edit material");
      editMaterialButton.setOnAction(e -> new EditMaterialDialog(material, scene).showAndWait());

      controls.getChildren().addAll(sheepColorPicker, editMaterialButton);
    }

    if (entity instanceof Saddleable) {
      Saddleable saddleable = (Saddleable) entity;
      CheckBox showOuterLayer = new CheckBox("Is Saddled?");
      showOuterLayer.setSelected(saddleable.isSaddled());
      showOuterLayer.selectedProperty().addListener(((observable, oldValue, newValue) -> {
        saddleable.setIsSaddled(newValue);
        scene.rebuildActorBvh();
      }));

      controls.getChildren().addAll(showOuterLayer);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    add.setTooltip(new Tooltip("Add an entity at the target position."));
    add.setOnAction(e -> {
      if (addEntityDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        EntityType<?> entityType = addEntityDialog.getType();
        EntityPlacement entityPlacement = addEntityDialog.getPlacement();

        Vector3 position;
        switch (entityPlacement) {
          case POSITION:
            position = addEntityDialog.getPosition();
            break;

          case TARGET:
            position = scene.getTargetPosition();
            if (position == null) {
              position = new Vector3(scene.camera().getPosition());
            }
            break;

          case CAMERA:
          default:
            position = new Vector3(scene.camera().getPosition());
        }

        Entity entity = entityType.createInstance(position, scene);
        if (entity instanceof PlayerEntity) {
          PlayerEntity player = (PlayerEntity) entity;
          withEntity(selected -> {
            if (selected instanceof PlayerEntity) {
              player.skin = ((PlayerEntity) selected).skin;
              player.model = ((PlayerEntity) selected).model;
            }
          });
          scene.addPlayer(player);

        } else {
          scene.addActor(entity);
        }

        EntityData data = new EntityData(entity, scene);
        entityTable.getItems().add(data);
        entityTable.getSelectionModel().select(data);
      }
    });
    delete.setTooltip(new Tooltip("Delete the selected entity."));
    delete.setOnAction(e -> withEntity(entity -> {
      scene.removeEntity(entity);
      update(scene);
    }));
    clear.setTooltip(new Tooltip("Remove all entities from the scene."));
    clear.setOnAction(e -> {
      scene.clearEntities();
      update(scene);
    });
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
    cameraToEntity.setTooltip(new Tooltip("Move the camera to the location of the selected entity."));
    cameraToEntity.setOnAction(e -> withEntity(player -> scene.camera().moveToPlayer(player)));
    entityToCamera.setTooltip(new Tooltip("Move the selected entity to the location of the camera."));
    entityToCamera.setOnAction(e -> withEntity(entity -> {
      entity.setPosition(scene.camera().getPosition());
      updatePositionFields(entity);
      scene.rebuildActorBvh();
    }));
    entityToTarget.setTooltip(new Tooltip("Move the selected entity to the current target."));
    entityToTarget.setOnAction(e -> withEntity(player -> {
      Vector3 target = scene.getTargetPosition();
      if (target != null) {
        player.position.set(target);
        updatePositionFields(player);
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
      JsonObject poseObject = poseable.getPose();
      if (poseObject == null) {
        return;
      }

      JsonArray pose = poseObject.get(part).array();
      if (pose.size() < 3) {
        // Set default pose to [0, 0, 0].
        pose = new JsonArray(3);
        pose.add(0);
        pose.add(0);
        pose.add(0);
        poseObject.set(part, pose);
      }
      consumer.accept(pose);
    }
  }

  private void updatePositionFields(Entity entity) {
    posX.valueProperty().set(entity.position.x);
    posY.valueProperty().set(entity.position.y);
    posZ.valueProperty().set(entity.position.z);
  }
}
