/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.entity;

import se.llbit.chunky.block.minecraft.Head;
import se.llbit.chunky.entity.SkullEntity.Kind;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.resources.*;
import se.llbit.chunky.resources.PlayerTexture.ExtendedUVMap;
import se.llbit.chunky.resources.texturepack.*;
import se.llbit.chunky.world.PlayerEntityData;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.chunky.world.model.CubeModel;
import se.llbit.chunky.world.model.JsonModel;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.JsonUtil;
import se.llbit.util.mojangapi.MinecraftProfile;
import se.llbit.util.mojangapi.MinecraftSkin;
import se.llbit.util.mojangapi.MojangApi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerEntity extends HumanoidEntityBase {

  public final String uuid;
  /**
   * Skin file path used for this player.
   */
  public String skin = "";

  public PlayerEntity(String uuid, Vector3 position) {
    this(uuid, position, 0, 0, new JsonObject());
  }

  public PlayerEntity(PlayerEntityData data) {
    this(data.uuid, new Vector3(data.x, data.y, data.z), data.rotation, data.pitch,
      buildGear(data));
  }

  protected PlayerEntity(String uuid, Vector3 position, double rotationDegrees, double pitchDegrees,
                         JsonObject gear) {
    super(position, rotationDegrees, pitchDegrees, gear);
    this.uuid = uuid;
  }


  public PlayerEntity(JsonObject settings) {
    super(settings);
    this.uuid = settings.get("uuid").stringValue("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
    this.skin = settings.get("skin").stringValue("");
  }

  @Override
  public Optional<PlayerTexture> getSkinTexture() {
    PlayerTexture skinTexture = new PlayerTexture();
    PlayerTextureLoader loader = new PlayerTextureLoader(skin, skinTexture, model);
    try {
      loader.loadFromFile(new File(skin));
    } catch (IOException | TextureFormatError e) {
      Log.info("Failed to load cached skin. Trying API.", e);
      try {
        MinecraftProfile profile = MojangApi.fetchProfile(uuid);
        Optional<String> skinUrl = profile.getSkin().map(MinecraftSkin::getSkinUrl);
        if (skinUrl.isPresent()) {
          File skinFile = MojangApi.downloadSkin(skinUrl.get());
          skin = skinFile.getAbsolutePath();
          loader.loadFromFile(skinFile);
        } else {
          return Optional.empty();
        }
      } catch (IOException | TextureFormatError e2) {
        Log.warn("Failed to download skin", e2);
        return Optional.empty();
      }
    }
    return Optional.of(skinTexture);
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = super.toJson().asObject();
    json.add("kind", "player");
    json.add("uuid", uuid);
    json.add("skin", skin);
    return json;
  }

  public static PlayerEntity fromJson(JsonObject json) {
    return new PlayerEntity(json);
  }

  @Override
  public String toString() {
    return "player: " + uuid;
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PlayerEntity) {
      return ((PlayerEntity) obj).uuid.equals(uuid);
    }
    return false;
  }

  public void setTexture(String path) {
    skin = path;
  }

  public void randomPoseAndLook() {
    Random random = new Random(System.currentTimeMillis());
    randomPose(random);
    randomLook(random);
  }

  public void randomPose() {
    Random random = new Random(System.currentTimeMillis());
    randomPose(random);
  }

  private void randomPose(Random random) {
    double leftLegPose = (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    double rightLegPose = -leftLegPose;
    double leftArmPose = (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    double rightArmPose = -leftArmPose;
    pose.add("leftArm", JsonUtil.vec3ToJson(new Vector3(leftArmPose, 0, 0)));
    pose.add("rightArm", JsonUtil.vec3ToJson(new Vector3(rightArmPose, 0, 0)));
    pose.add("leftLeg", JsonUtil.vec3ToJson(new Vector3(leftLegPose, 0, 0)));
    pose.add("rightLeg", JsonUtil.vec3ToJson(new Vector3(rightLegPose, 0, 0)));
  }

  private void randomLook(Random random) {
    pose.set("rotation", Json.of((random.nextFloat() - 0.5) * QuickMath.TAU));
    double headYaw = 0.4 * (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    double pitch = (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    pose.add("head", JsonUtil.vec3ToJson(new Vector3(pitch, headYaw, 0)));
  }
}
