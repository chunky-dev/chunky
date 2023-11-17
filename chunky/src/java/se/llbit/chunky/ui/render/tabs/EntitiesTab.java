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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import se.llbit.chunky.entity.ArmorStand;
import se.llbit.chunky.entity.BeaconBeam;
import se.llbit.chunky.entity.Book;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.Geared;
import se.llbit.chunky.entity.Lectern;
import se.llbit.chunky.entity.PlayerEntity;
import se.llbit.chunky.entity.Poseable;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.dialogs.DialogUtils;
import se.llbit.chunky.ui.dialogs.ValidatingTextInputDialog;
import se.llbit.chunky.ui.elements.AngleAdjuster;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.IntegerTextField;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.material.BeaconBeamMaterial;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;
import se.llbit.math.ColorUtil;
import se.llbit.math.Point3;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.util.mojangapi.MinecraftProfile;
import se.llbit.util.mojangapi.MinecraftSkin;
import se.llbit.util.mojangapi.MojangApi;

public class EntitiesTab extends ScrollPane implements RenderControlsTab, Initializable {
  private static final Map<String, EntitiesTab.EntityType<?>> entityTypes = new HashMap<>();

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
  }

  private Scene scene;

  public interface EntityType<T extends Entity> {
    T createInstance(Point3 position, Scene scene);
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
  @FXML private ComboBox<String> entityType;

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
    entityTable.getItems().removeAll(missing);
  }

  @Override
  public String getTabTitle() {
    return "Entities";
  }

  @Override
  public Node getTabContent() {
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
              .getExtensionFilters()
              .add(new FileChooser.ExtensionFilter("Minecraft skin", "*.png"));
          File skinFile = fileChooser.showOpenDialog(getScene().getWindow());
          if (skinFile != null) {
            player.setTexture(skinFile.getAbsolutePath());
            skinField.setText(skinFile.getAbsolutePath());
            scene.rebuildActorBvh();
          }
        });
        Button downloadSkin = new Button("Download skin...");
        downloadSkin.setOnAction(e -> {
          TextInputDialog playerIdentifierInput = new ValidatingTextInputDialog(input -> input != null && !input.isEmpty());
          playerIdentifierInput.setTitle("Input player identifier");
          playerIdentifierInput.setHeaderText("Please enter the UUID or name of the player.");
          playerIdentifierInput.setContentText("UUID / player name:");
          DialogUtils.setupDialogDesign(playerIdentifierInput, getScene());
          playerIdentifierInput.showAndWait().map(playerIdentifier -> {
            try {
              // TODO: refactor this (deduplicate code, check UUID format, trim input, better error handling)
              MinecraftProfile profile = MojangApi.fetchProfile(playerIdentifier); //Search by uuid
              Optional<MinecraftSkin> skin = profile.getSkin();
              if (skin.isPresent()) { // If it found a skin, pass it back to caller
                downloadAndApplySkinForPlayer(
                  skin.get(),
                  player,
                  playerModel,
                  skinField
                );
                return true;
              } else { // Otherwise, search by Username
                String uuid = MojangApi.usernameToUUID(playerIdentifier);
                profile = MojangApi.fetchProfile(uuid);
                skin = profile.getSkin();
                if (skin.isPresent()) {
                  downloadAndApplySkinForPlayer(
                    skin.get(),
                    player,
                    playerModel,
                    skinField
                  );
                  return true;
                } else { //If still not found, warn user.
                  Log.warn("Could not find player with that identifier");
                }
              }
            } catch (IOException ex) {
              Log.warn("Could not download skin", ex);
            }
            return false;
          });
        });
        skinBox.getChildren().addAll(new Label("Skin:"), skinField, selectSkin, downloadSkin);

        CheckBox showOuterLayer = new CheckBox("Show second layer");
        showOuterLayer.setSelected(player.showOuterLayer);
        showOuterLayer.selectedProperty().addListener(((observable, oldValue, newValue) -> {
          player.showOuterLayer = newValue;
          scene.rebuildActorBvh();
        }));
        HBox layerBox = new HBox();
        layerBox.setSpacing(10.0);
        layerBox.setAlignment(Pos.CENTER_LEFT);
        layerBox.getChildren().addAll(showOuterLayer);

        controls.getChildren().addAll(modelBox, skinBox, layerBox);
      }

      if (entity instanceof Book || entity instanceof Lectern) {
        Book book;
        if (entity instanceof Lectern) {
          book = ((Lectern) entity).getBook();
        } else {
          book = (Book) entity;
        }

        if (book != null) {
          DoubleAdjuster openingAngle = new DoubleAdjuster();
          openingAngle.setName("Opening angle");
          openingAngle.setTooltip("Modifies the book's opening angle.");
          openingAngle.set(Math.toDegrees(book.getOpenAngle()));
          openingAngle.setRange(0, 180);
          openingAngle.onValueChange(value -> {
            book.setOpenAngle(Math.toRadians(value));
            scene.rebuildActorBvh();
          });
          controls.getChildren().add(openingAngle);

          DoubleAdjuster page1Angle = new DoubleAdjuster();
          page1Angle.setName("Page 1 angle");
          page1Angle.setTooltip("Modifies the book's first visible page's angle.");
          page1Angle.set(Math.toDegrees(book.getPageAngleA()));
          page1Angle.setRange(0, 180);
          page1Angle.onValueChange(value -> {
            book.setPageAngleA(Math.toRadians(value));
            scene.rebuildActorBvh();
          });
          controls.getChildren().add(page1Angle);

          DoubleAdjuster page2Angle = new DoubleAdjuster();
          page2Angle.setName("Page 2 angle");
          page2Angle.setTooltip("Modifies the book's second visible page's angle.");
          page2Angle.set(Math.toDegrees(book.getPageAngleB()));
          page2Angle.setRange(0, 180);
          page2Angle.onValueChange(value -> {
            book.setPageAngleB(Math.toRadians(value));
            scene.rebuildActorBvh();
          });
          controls.getChildren().add(page2Angle);
        }
      }
      else if(entity instanceof BeaconBeam) {
        BeaconBeam beam = (BeaconBeam) entity;
        IntegerAdjuster height = new IntegerAdjuster();
        height.setName("Height");
        height.setTooltip("Modifies the height of the beam. Useful if your scene is taller than the world height.");
        height.set(beam.getHeight());
        height.setRange(1, 512);
        height.onValueChange(value -> {
          beam.setHeight(value);
          scene.rebuildActorBvh();
        });
        controls.getChildren().add(height);

        HBox beamColor = new HBox();
        VBox listControls = new VBox();
        VBox propertyControls = new VBox();

        listControls.setMaxWidth(200);
        beamColor.setPadding(new Insets(10));
        beamColor.setSpacing(15);
        propertyControls.setSpacing(10);

        DoubleAdjuster emittance = new DoubleAdjuster();
        emittance.setName("Emittance");
        emittance.setRange(0, 100);

        DoubleAdjuster specular = new DoubleAdjuster();
        specular.setName("Specular");
        specular.setRange(0, 1);

        DoubleAdjuster ior = new DoubleAdjuster();
        ior.setName("IoR");
        ior.setRange(0, 5);

        DoubleAdjuster perceptualSmoothness = new DoubleAdjuster();
        perceptualSmoothness.setName("Smoothness");
        perceptualSmoothness.setRange(0, 1);

        DoubleAdjuster metalness = new DoubleAdjuster();
        metalness.setName("Metalness");
        metalness.setRange(0, 1);

        LuxColorPicker beamColorPicker = new LuxColorPicker();

        ObservableList<Integer> colorHeights = FXCollections.observableArrayList();
        colorHeights.addAll(beam.getMaterials().keySet());
        ListView<Integer> colorHeightList = new ListView<>(colorHeights);
        colorHeightList.setMaxHeight(150.0);
        colorHeightList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, heightIndex) -> {

              BeaconBeamMaterial beamMat = beam.getMaterials().get(heightIndex);
              emittance.set(beamMat.emittance);
              specular.set(beamMat.specular);
              ior.set(beamMat.ior);
              perceptualSmoothness.set(beamMat.getPerceptualSmoothness());
              metalness.set(beamMat.metalness);
              beamColorPicker.setColor(ColorUtil.toFx(beamMat.getColorInt()));

              emittance.onValueChange(value -> {
                beamMat.emittance = value.floatValue();
                scene.rebuildActorBvh();
              });
              specular.onValueChange(value -> {
                beamMat.specular = value.floatValue();
                scene.rebuildActorBvh();
              });
              ior.onValueChange(value -> {
                beamMat.ior = value.floatValue();
                scene.rebuildActorBvh();
              });
              perceptualSmoothness.onValueChange(value -> {
                beamMat.setPerceptualSmoothness(value);
                scene.rebuildActorBvh();
              });
              metalness.onValueChange(value -> {
                beamMat.metalness = value.floatValue();
                scene.rebuildActorBvh();
              });
            }
        );
        beamColorPicker.colorProperty().addListener(
            (observableColor, oldColorValue, newColorValue) -> {
              Integer index = colorHeightList.getSelectionModel().getSelectedItem();
              if (index != null) {
                beam.getMaterials().get(index).updateColor(ColorUtil.getRGB(ColorUtil.fromFx(newColorValue)));
                scene.rebuildActorBvh();
              }
            }
        );

        HBox listButtons = new HBox();
        listButtons.setPadding(new Insets(10));
        listButtons.setSpacing(15);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
          Integer index = colorHeightList.getSelectionModel().getSelectedItem();
          if (index != null && index != 0) { //Prevent removal of the bottom layer
            beam.getMaterials().remove(index);
            colorHeightList.getItems().removeAll(index);
            scene.rebuildActorBvh();
          }
        });
        IntegerTextField layerInput = new IntegerTextField();
        layerInput.setMaxWidth(50);
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
          if (!beam.getMaterials().containsKey(layerInput.valueProperty().get())) { //Don't allow duplicate indices
            beam.getMaterials().put(layerInput.valueProperty().get(), new BeaconBeamMaterial(BeaconBeamMaterial.DEFAULT_COLOR));
            colorHeightList.getItems().add(layerInput.valueProperty().get());
            scene.rebuildActorBvh();
          }
        });

        listButtons.getChildren().addAll(deleteButton, layerInput, addButton);
        propertyControls.getChildren().addAll(emittance, specular, perceptualSmoothness, ior, metalness, beamColorPicker);
        listControls.getChildren().addAll(new Label("Start Height:"), colorHeightList, listButtons);
        beamColor.getChildren().addAll(listControls, propertyControls);
        controls.getChildren().add(beamColor);
      }

      updatePositionFields(entity);
      posX.valueProperty().addListener((observable, oldValue, newValue) -> {
        withEntity(e -> {
          Point3 currentPosition = e.getPosition();
          e.setPosition(new Point3(newValue.doubleValue(), currentPosition.y, currentPosition.z));
        });
        scene.rebuildActorBvh();
      });
      posY.valueProperty().addListener((observable, oldValue, newValue) -> {
        withEntity(e -> {
          Point3 currentPosition = e.getPosition();
          e.setPosition(new Point3(currentPosition.x, newValue.doubleValue(), currentPosition.z));
        });
        scene.rebuildActorBvh();
      });
      posZ.valueProperty().addListener((observable, oldValue, newValue) -> {
        withEntity(e -> {
          Point3 currentPosition = e.getPosition();
          e.setPosition(new Point3(currentPosition.x, currentPosition.y, newValue.doubleValue()));
        });
        scene.rebuildActorBvh();
      });

      controls.getChildren().add(position);
      position.setVisible(true);

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
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    entityType.getItems().addAll(entityTypes.keySet());
    entityType.setValue("Player");
    add.setTooltip(new Tooltip("Add an entity at the target position."));
    add.setOnAction(e -> {
      Point3 position = scene.getTargetPosition();
      if (position == null) {
        position = new Point3(scene.camera().getPosition());
      }

      Entity entity = entityTypes.get(entityType.getValue()).createInstance(position, scene);
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
    });
    delete.setTooltip(new Tooltip("Delete the selected entity."));
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
      Point3 target = scene.getTargetPosition();
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
      Point3 target = scene.getTargetPosition();
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

  @Override
  public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  private void downloadAndApplySkinForPlayer(
    MinecraftSkin skin,
    PlayerEntity player,
    ChoiceBox<PlayerModel> playerModelSelector,
    TextField skinField
  ) throws IOException {
    if (skin != null) {
      String filePath = MojangApi.downloadSkin(skin.getSkinUrl()).getAbsolutePath();
      player.setTexture(filePath);
      playerModelSelector.getSelectionModel().select(skin.getPlayerModel());
      skinField.setText(filePath);
      Log.info("Successfully set skin");
      scene.rebuildActorBvh();
    }
  }
}
