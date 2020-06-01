/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.TextureCache;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.resources.texturepack.ColoredTexture;
import se.llbit.chunky.resources.texturepack.EntityTextureLoader;
import se.llbit.chunky.resources.texturepack.LayeredTextureLoader;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.chunky.resources.texturepack.TextureFormatError;
import se.llbit.chunky.resources.texturepack.TextureLoader;
import se.llbit.chunky.world.PlayerEntityData;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.chunky.world.model.CubeModel;
import se.llbit.chunky.world.model.JsonModel;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class PlayerEntity extends Entity implements Poseable, Geared {
  private static final String[] partNames =
      { "all", "head", "chest", "leftArm", "rightArm", "leftLeg", "rightLeg" };
  private static final String[] gearSlots =
      { "leftHand", "rightHand", "head", "chest", "legs", "feet" };

  public final String uuid;
  public JsonObject gear = new JsonObject();
  public JsonObject pose = new JsonObject();
  public double scale = 1.0;
  public double headScale = 1.0;
  public PlayerModel model;
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
    super(position);
    this.uuid = uuid;
    this.model = PlayerModel.STEVE;
    this.gear = gear;
    double rotation = QuickMath.degToRad(180 - rotationDegrees);
    JsonObject pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, rotation, 0)));
    pose.add("head", JsonUtil.vec3ToJson(new Vector3(-QuickMath.degToRad(pitchDegrees), 0, 0)));
    pose.add("leftArm", JsonUtil.vec3ToJson(new Vector3(0.4, 0, 0)));
    pose.add("rightArm", JsonUtil.vec3ToJson(new Vector3(-0.4, 0, 0)));
    pose.add("leftLeg", JsonUtil.vec3ToJson(new Vector3(0.4, 0, 0)));
    pose.add("rightLeg", JsonUtil.vec3ToJson(new Vector3(-0.4, 0, 0)));
    this.pose = pose;
  }

  public PlayerEntity(JsonObject settings) {
    super(JsonUtil.vec3FromJsonObject(settings.get("position")));
    this.model = PlayerModel.get(settings.get("model").stringValue("STEVE"));
    this.uuid = settings.get("uuid").stringValue("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
    this.skin = settings.get("skin").stringValue("");
    this.scale = settings.get("scale").doubleValue(1.0);
    this.headScale = settings.get("headScale").doubleValue(1.0);
    this.pose = settings.get("pose").object();
    this.gear = settings.get("gear").object();
  }

  static JsonObject parseItem(CompoundTag tag) {
    JsonObject item = new JsonObject();
    String id = tag.get("id").stringValue("");
    item.add("id", id);
    Tag color = tag.get("tag").get("display").get("color");
    if (!color.isError()) {
      item.add("color", color.intValue(0));
    }
    if (id.equals("minecraft:skull")) {
      // Skull type is stored in the damage field.
      int damage = tag.get("Damage").shortValue();
      item.add("type", damage);
    }
    return item;
  }

  static JsonObject buildGear(PlayerEntityData data) {
    JsonObject gear = new JsonObject();
    if (!data.chestplate.asCompound().isEmpty()) {
      gear.add("chest", parseItem(data.chestplate.asCompound()));
    }
    if (!data.feet.asCompound().isEmpty()) {
      gear.add("feet", parseItem(data.feet.asCompound()));
    }
    if (!data.head.asCompound().isEmpty()) {
      gear.add("head", parseItem(data.head.asCompound()));
    }
    if (!data.legs.asCompound().isEmpty()) {
      gear.add("legs", parseItem(data.legs.asCompound()));
    }
    return gear;
  }

  private void poseLimb(Box part, double rotation, Transform transform, Transform offset) {
    part.transform(transform);
    part.transform(Transform.NONE.rotateY(rotation));
    part.transform(offset);
  }

  private void poseHead(Box part, double rotation, Transform transform,
      Transform offset) {
    part.transform(transform);
    part.transform(Transform.NONE.rotateY(rotation));
    part.transform(offset);
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    EntityTexture texture = Texture.steve;
    double armWidth = model == PlayerModel.ALEX ? 1.5 : 2;
    if (skin.isEmpty()) {
      switch (model) {
        case ALEX:
          texture = Texture.alex;
          break;
        case STEVE:
          texture = Texture.steve;
          break;
      }
    } else {
      texture = new EntityTexture();
      EntityTextureLoader loader = new EntityTextureLoader(skin, texture);
      try {
        loader.load(new File(skin));
      } catch (IOException | TextureFormatError e) {
        Log.warn("Failed to load skin", e);
        texture = Texture.steve;
      }
    }
    Vector3 allPose = JsonUtil.vec3FromJsonArray(pose.get("all"));
    Vector3 headPose = JsonUtil.vec3FromJsonArray(pose.get("head"));
    Vector3 chestPose = JsonUtil.vec3FromJsonArray(pose.get("chest"));
    Vector3 leftLegPose = JsonUtil.vec3FromJsonArray(pose.get("leftLeg"));
    Vector3 rightLegPose = JsonUtil.vec3FromJsonArray(pose.get("rightLeg"));
    Vector3 leftArmPose = JsonUtil.vec3FromJsonArray(pose.get("leftArm"));
    Vector3 rightArmPose = JsonUtil.vec3FromJsonArray(pose.get("rightArm"));
    Vector3 worldOffset = new Vector3(
        position.x + offset.x,
        position.y + offset.y,
        position.z + offset.z);
    Transform worldTransform = Transform.NONE
        .scale(scale)
        .rotateX(allPose.x)
        .rotateY(allPose.y)
        .rotateZ(allPose.z)
        .translate(worldOffset);
    Collection<Primitive> primitives = new LinkedList<>();
    Box head = new Box(-4 / 16., 4 / 16., -4 / 16., 4 / 16., -4 / 16., 4 / 16.);
    head.transform(Transform.NONE
        .translate(0, 4 / 16., 0)
        .rotateX(headPose.x)
        .rotateY(headPose.y)
        .rotateZ(headPose.z)
        .translate(0, -4 / 16., 0)
        .scale(headScale)
        .translate(0, 28 / 16., 0)
        .chain(worldTransform));
    head.addFrontFaces(primitives, texture, texture.headFront);
    head.addBackFaces(primitives, texture, texture.headBack);
    head.addLeftFaces(primitives, texture, texture.headLeft);
    head.addRightFaces(primitives, texture, texture.headRight);
    head.addTopFaces(primitives, texture, texture.headTop);
    head.addBottomFaces(primitives, texture, texture.headBottom);
    Box hat = new Box(-4.2 / 16., 4.2 / 16., -4.2 / 16., 4.2 / 16., -4.2 / 16., 4.2 / 16.);
    hat.transform(Transform.NONE
        .rotateX(headPose.x)
        .rotateY(headPose.y)
        .rotateZ(headPose.z)
        .translate(0, 28.2 / 16., 0)
        .chain(worldTransform));
    hat.addFrontFaces(primitives, texture, texture.hatFront);
    hat.addBackFaces(primitives, texture, texture.hatBack);
    hat.addLeftFaces(primitives, texture, texture.hatLeft);
    hat.addRightFaces(primitives, texture, texture.hatRight);
    hat.addTopFaces(primitives, texture, texture.hatTop);
    hat.addBottomFaces(primitives, texture, texture.hatBottom);
    Box chest = new Box(-4 / 16., 4 / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    chest.transform(Transform.NONE
        .translate(0, -5 / 16., 0)
        .rotateX(chestPose.x)
        .rotateY(chestPose.y)
        .rotateZ(chestPose.z)
        .translate(0, (5 + 18) / 16., 0)
        .chain(worldTransform));
    chest.addFrontFaces(primitives, texture, texture.chestFront);
    chest.addBackFaces(primitives, texture, texture.chestBack);
    chest.addLeftFaces(primitives, texture, texture.chestLeft);
    chest.addRightFaces(primitives, texture, texture.chestRight);
    chest.addTopFaces(primitives, texture, texture.chestTop);
    chest.addBottomFaces(primitives, texture, texture.chestBottom);
    Box leftLeg = new Box(-2 / 16., 2 / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    leftLeg.transform(Transform.NONE.translate(0, -6 / 16., 0)
        .rotateX(leftLegPose.x)
        .rotateY(leftLegPose.y)
        .rotateZ(leftLegPose.z)
        .translate(-2 / 16., 12 / 16., 0)
        .chain(worldTransform));
    leftLeg.addFrontFaces(primitives, texture, texture.leftLegFront);
    leftLeg.addBackFaces(primitives, texture, texture.leftLegBack);
    leftLeg.addLeftFaces(primitives, texture, texture.leftLegLeft);
    leftLeg.addRightFaces(primitives, texture, texture.leftLegRight);
    leftLeg.addTopFaces(primitives, texture, texture.leftLegTop);
    leftLeg.addBottomFaces(primitives, texture, texture.leftLegBottom);
    Box rightLeg = new Box(-2 / 16., 2 / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    rightLeg.transform(Transform.NONE.translate(0, -6 / 16., 0)
        .rotateX(rightLegPose.x)
        .rotateY(rightLegPose.y)
        .rotateZ(rightLegPose.z)
        .translate(2 / 16., 12 / 16., 0)
        .chain(worldTransform));
    rightLeg.addFrontFaces(primitives, texture, texture.rightLegFront);
    rightLeg.addBackFaces(primitives, texture, texture.rightLegBack);
    rightLeg.addLeftFaces(primitives, texture, texture.rightLegLeft);
    rightLeg.addRightFaces(primitives, texture, texture.rightLegRight);
    rightLeg.addTopFaces(primitives, texture, texture.rightLegTop);
    rightLeg.addBottomFaces(primitives, texture, texture.rightLegBottom);
    Box leftArm = new Box(-armWidth / 16., armWidth / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    leftArm.transform(Transform.NONE.translate(0, -5 / 16., 0)
        .rotateX(leftArmPose.x)
        .rotateY(leftArmPose.y)
        .rotateZ(leftArmPose.z)
        .translate(-(4 + armWidth) / 16., 23 / 16., 0)
        .chain(worldTransform));
    leftArm.addFrontFaces(primitives, texture, texture.leftArmFront);
    leftArm.addBackFaces(primitives, texture, texture.leftArmBack);
    leftArm.addLeftFaces(primitives, texture, texture.leftArmLeft);
    leftArm.addRightFaces(primitives, texture, texture.leftArmRight);
    leftArm.addTopFaces(primitives, texture, texture.leftArmTop);
    leftArm.addBottomFaces(primitives, texture, texture.leftArmBottom);
    Box rightArm = new Box(-armWidth / 16., armWidth / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    rightArm.transform(Transform.NONE.translate(0, -5 / 16., 0)
        .rotateX(rightArmPose.x)
        .rotateY(rightArmPose.y)
        .rotateZ(rightArmPose.z)
        .translate((4 + armWidth) / 16., 23 / 16., 0)
        .chain(worldTransform));
    rightArm.addFrontFaces(primitives, texture, texture.rightArmFront);
    rightArm.addBackFaces(primitives, texture, texture.rightArmBack);
    rightArm.addLeftFaces(primitives, texture, texture.rightArmLeft);
    rightArm.addRightFaces(primitives, texture, texture.rightArmRight);
    rightArm.addTopFaces(primitives, texture, texture.rightArmTop);
    rightArm.addBottomFaces(primitives, texture, texture.rightArmBottom);

    addArmor(primitives, gear, pose, armWidth, worldTransform, headScale);
    return primitives;
  }

  public static void addArmor(Collection<Primitive> faces,
      JsonObject gear,
      JsonObject pose,
      double armWidth,
      Transform worldTransform,
      double headScale) {
    Vector3 headPose = JsonUtil.vec3FromJsonArray(pose.get("head"));
    Vector3 chestPose = JsonUtil.vec3FromJsonArray(pose.get("chest"));
    Vector3 leftArmPose = JsonUtil.vec3FromJsonArray(pose.get("leftArm"));
    Vector3 rightArmPose = JsonUtil.vec3FromJsonArray(pose.get("rightArm"));
    Vector3 leftLegPose = JsonUtil.vec3FromJsonArray(pose.get("leftLeg"));
    Vector3 rightLegPose = JsonUtil.vec3FromJsonArray(pose.get("rightLeg"));

    JsonObject headItem = gear.get("head").object();
    if (!headItem.isEmpty()) {
      Transform transform = Transform.NONE
          .translate(-0.5, -0.5, -0.5)
          .rotateX(headPose.x)
          .rotateY(headPose.y)
          .rotateZ(headPose.z)
          .scale(headScale)
          .translate(0, 28 / 16.0, 0)
          .chain(worldTransform);
      addModel(faces, getHelmModel(headItem), transform);
    }

    // Add chest armor.
    JsonObject chestItem = gear.get("chest").object();
    if (!chestItem.isEmpty()) {
      Transform transform = Transform.NONE
          .translate(0, -5 / 16.0, 0)
          .rotateX(chestPose.x)
          .rotateY(chestPose.y)
          .rotateZ(chestPose.z)
          .translate(0, (5 + 18) / 16.0, 0)
          .chain(worldTransform);
      addModel(faces, getChestModel(chestItem), transform);

      transform = Transform.NONE
          .translate(-0.5, -14 / 16., -0.5)
          .rotateX(leftArmPose.x)
          .rotateY(leftArmPose.y)
          .rotateZ(leftArmPose.z)
          .translate(-(4 + armWidth) / 16., 23 / 16., 0)
          .chain(worldTransform);
      addModel(faces, getLeftPauldron(chestItem), transform);

      transform = Transform.NONE
          .translate(-0.5, -14 / 16., -0.5)
          .rotateX(rightArmPose.x)
          .rotateY(rightArmPose.y)
          .rotateZ(rightArmPose.z)
          .translate((4 + armWidth) / 16., 23 / 16., 0)
          .chain(worldTransform);
      addModel(faces, getRightPauldron(chestItem), transform);
    }

    // Add leggings.
    JsonObject legs = gear.get("legs").object();
    if (!legs.isEmpty()) {
      Transform transform = Transform.NONE
          .translate(0, -5 / 16.0, 0)
          .rotateX(chestPose.x)
          .rotateY(chestPose.y)
          .rotateZ(chestPose.z)
          .translate(0, (5 + 18) / 16.0, 0)
          .chain(worldTransform);
      addModel(faces, getLeggingsModel(legs), transform);

      transform = Transform.NONE
          .translate(0, -6 / 16.0, 0)
          .rotateX(leftLegPose.x)
          .rotateY(leftLegPose.y)
          .rotateZ(leftLegPose.z)
          .translate(-2 / 16., 12 / 16., 0)
          .chain(worldTransform);
      addModel(faces, getLeftLeg(legs), transform);

      transform = Transform.NONE
          .translate(0, -6 / 16.0, 0)
          .rotateX(rightLegPose.x)
          .rotateY(rightLegPose.y)
          .rotateZ(rightLegPose.z)
          .translate(2 / 16., 12 / 16., 0)
          .chain(worldTransform);
      addModel(faces, getRightLeg(legs), transform);
    }

    JsonObject feet = gear.get("feet").object();
    if (!feet.isEmpty()) {
      Transform transform = Transform.NONE
          .translate(0, -6 / 16.0, 0)
          .rotateX(leftLegPose.x)
          .rotateY(leftLegPose.y)
          .rotateZ(leftLegPose.z)
          .translate(-2 / 16., 12 / 16., 0)
          .chain(worldTransform);
      addModel(faces, getLeftBoot(feet), transform);

      transform = Transform.NONE
          .translate(0, -6 / 16.0, 0)
          .rotateX(rightLegPose.x)
          .rotateY(rightLegPose.y)
          .rotateZ(rightLegPose.z)
          .translate(2 / 16., 12 / 16., 0)
          .chain(worldTransform);
      addModel(faces, getRightBoot(feet), transform);
    }
  }

  private static final String chestJson =
      "{\"elements\":[{\"from\":[-4.4,-6,-2.4],\"to\":[4.4,6,2.4],\"faces\":{\"east\":{\"uv\":[7,10,8,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[4,10,5,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[8,10,10,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[5,10,7,16],\"texture\":\"#texture\"}}}]}";

  private static final String leggingsJson =
      "{\"elements\":[{\"from\":[-4.2,-6,-2.2],\"to\":[4.2,6,2.2],\"faces\":{\"east\":{\"uv\":[7,10,8,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[4,10,5,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[8,10,10,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[5,10,7,16],\"texture\":\"#texture\"}}}]}";

  private static final String helmJson =
      "{\"elements\":[{\"from\":[3.6,3.6,3.6],\"to\":[12.4,12.4,12.4],\"faces\":{\"up\":{\"uv\":[2,0,4,4],\"texture\":\"#texture\"},\"east\":{\"uv\":[0,4,2,8],\"texture\":\"#texture\"},\"west\":{\"uv\":[4,4,6,8],\"texture\":\"#texture\"},\"north\":{\"uv\":[2,4,4,8],\"texture\":\"#texture\"},\"south\":{\"uv\":[6,4,8,8],\"texture\":\"#texture\"}}}]}";

  private static final String headJson =
      "{\"elements\":[{\"from\":[4,4,4],\"to\":[12,12,12],\"faces\":{\"up\":{\"uv\":[2,0,4,2],\"texture\":\"#texture\"},\"down\":{\"uv\":[4,0,6,2],\"texture\":\"#texture\"},\"east\":{\"uv\":[6,2,4,4],\"texture\":\"#texture\"},\"west\":{\"uv\":[2,2,0,4],\"texture\":\"#texture\"},\"north\":{\"uv\":[2,2,4,4],\"texture\":\"#texture\"},\"south\":{\"uv\":[6,2,8,4],\"texture\":\"#texture\"}}}]}";

  // The difference between skullJson/headJson is that skullJson is textured with a half as tall texture.
  private static final String skullJson =
      "{\"elements\":[{\"from\":[4,4,4],\"to\":[12,12,12],\"faces\":{\"up\":{\"uv\":[2,0,4,4],\"texture\":\"#texture\"},\"down\":{\"uv\":[4,0,6,4],\"texture\":\"#texture\"},\"east\":{\"uv\":[6,4,4,8],\"texture\":\"#texture\"},\"west\":{\"uv\":[2,4,0,8],\"texture\":\"#texture\"},\"north\":{\"uv\":[2,4,4,8],\"texture\":\"#texture\"},\"south\":{\"uv\":[6,4,8,8],\"texture\":\"#texture\"}}}]}";

  private static final String leftPauldron =
      "{\"elements\":[{\"from\":[5,0,5],\"to\":[11,16,11],\"faces\":{\"up\":{\"uv\":[11,8,12,10],\"texture\":\"#texture\"},\"east\":{\"uv\":[12,10,13,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[10,10,11,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[13,10,14,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[11,10,12,16],\"texture\":\"#texture\"}}}]}";

  private static final String rightPauldron =
      "{\"elements\":[{\"from\":[5,0,5],\"to\":[11,16,11],\"faces\":{\"up\":{\"uv\":[12,8,11,10],\"texture\":\"#texture\"},\"east\":{\"uv\":[11,10,10,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[13,10,12,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[14,10,13,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[12,10,11,16],\"texture\":\"#texture\"}}}]}";

  private static final String leftLeg =
      "{\"elements\":[{\"from\":[-2.2,-6.2,-2.2],\"to\":[2.2,6.2,2.2],\"faces\":{\"up\":{\"uv\":[1,8,2,10],\"texture\":\"#texture\"},\"east\":{\"uv\":[3,10,2,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[1,10,0,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[2,10,1,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[4,10,3,16],\"texture\":\"#texture\"}}}]}";

  private static final String rightLeg =
      "{\"elements\":[{\"from\":[-2.2,-6.2,-2.2],\"to\":[2.2,6.2,2.2],\"faces\":{\"up\":{\"uv\":[1,8,2,10],\"texture\":\"#texture\"},\"east\":{\"uv\":[0,10,1,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[2,10,3,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[1,10,2,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[3,10,4,16],\"texture\":\"#texture\"}}}]}";

  private static final String leftBoot =
      "{\"elements\":[{\"from\":[-2.5,-6.5,-2.5],\"to\":[2.5,6.5,2.5],\"faces\":{\"down\":{\"uv\":[2,8,3,10],\"texture\":\"#texture\"},\"east\":{\"uv\":[3,10,2,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[1,10,0,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[2,10,1,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[4,10,3,16],\"texture\":\"#texture\"}}}]}";

  private static final String rightBoot =
      "{\"elements\":[{\"from\":[-2.5,-6.5,-2.5],\"to\":[2.5,6.5,2.5],\"faces\":{\"down\":{\"uv\":[3,8,2,10],\"texture\":\"#texture\"},\"east\":{\"uv\":[0,10,1,16],\"texture\":\"#texture\"},\"west\":{\"uv\":[2,10,3,16],\"texture\":\"#texture\"},\"north\":{\"uv\":[1,10,2,16],\"texture\":\"#texture\"},\"south\":{\"uv\":[3,10,4,16],\"texture\":\"#texture\"}}}]}";

  private static void addModel(Collection<Primitive> primitives, CubeModel model,
      Transform baseTransform) {
    for (int i = 0; i < model.quads.length; ++i) {
      Quad quad = model.quads[i];
      Texture texture = model.textures[i];
      quad.addTriangles(primitives, new TextureMaterial(texture), baseTransform);
    }
  }

  static CubeModel getChestModel(JsonObject item) {
    JsonObject json = parseJson(chestJson);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  private static CubeModel getLeggingsModel(JsonObject item) {
    JsonObject json = parseJson(leggingsJson);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  static CubeModel getLeftPauldron(JsonObject item) {
    JsonObject json = parseJson(leftPauldron);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  static CubeModel getRightPauldron(JsonObject item) {
    JsonObject json = parseJson(rightPauldron);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  private static CubeModel getLeftLeg(JsonObject item) {
    JsonObject json = parseJson(leftLeg);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  private static CubeModel getRightLeg(JsonObject item) {
    JsonObject json = parseJson(rightLeg);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  private static CubeModel getLeftBoot(JsonObject item) {
    JsonObject json = parseJson(leftBoot);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  private static CubeModel getRightBoot(JsonObject item) {
    JsonObject json = parseJson(rightBoot);
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  static CubeModel getHelmModel(JsonObject item) {
    String id = item.get("id").asString("");
    JsonObject json = parseJson(helmJson);
    if (id.equals("minecraft:skull")) {
      // Reference: https://minecraft.gamepedia.com/Mob_head#Data_values
      int type = item.get("type").asInt(3);
      switch (type) {
        case 0:
          // Skeleton skull.
          json = parseJson(skullJson);
          break;
        case 1:
          // Wither skeleton skull.
          json = parseJson(skullJson);
          break;
        case 2:
          // Zombie head.
          json = parseJson(headJson);
          break;
        case 3:
          // Steve head.
          json = parseJson(headJson);
          break;
        case 4:
          // Creeper head.
          json = parseJson(skullJson);
          break;
        case 5:
          // Dragon head.
          json = parseJson(skullJson);
          break;
      }
    }
    Map<String, Texture> textureMap = Collections.singletonMap("#texture", getTexture(item));
    return new CubeModel(JsonModel.fromJson(json), 16, textureMap);
  }

  public static JsonObject parseJson(String helmetJson) {
    try (ByteArrayInputStream in = new ByteArrayInputStream(helmetJson.getBytes());
        JsonParser parser = new JsonParser(in)) {
      return parser.parse().object();
    } catch (IOException | JsonParser.SyntaxError e) {
      Log.warn("Failed to parse JSON", e);
      return new JsonObject();
    }
  }

  private Transform withScale(Transform transform) {
    if (scale == 1.0) {
      return transform;
    } else {
      return transform.scale(scale);
    }
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "player");
    json.add("uuid", uuid);
    json.add("position", position.toJson());
    json.add("model", model.name());
    json.add("skin", skin);
    json.add("pose", pose);
    json.add("gear", gear);
    json.add("scale", scale);
    json.add("headScale", headScale);
    return json;
  }

  static Texture getTexture(JsonObject item) {
    String id = item.get("id").asString("");
    if (TextureCache.containsKey(item)) {
      return TextureCache.get(item);
    }
    Texture texture = new Texture();
    String textureId = id; // Texture ID is used for log messages.
    if (id.startsWith("minecraft:")) {
      TextureLoader loader = null;
      switch (id.substring("minecraft:".length())) {
        case "skull": {
          // Reference: https://minecraft.gamepedia.com/Mob_head#Data_values
          int type = item.get("type").asInt(3);
          switch (type) {
            case 0:
              // Skeleton skull.
              textureId = "entity/skeleton/skeleton";
              break;
            case 1:
              // Wither skeleton skull.
              textureId = "entity/skeleton/wither_skeleton";
              break;
            case 2:
              // Zombie head.
              textureId = "entity/zombie/zombie";
              break;
            case 3:
              // Steve head.
              textureId = "entity/steve";
              break;
            case 4:
              // Creeper head.
              textureId = "entity/creeper/creeper";
              break;
            case 5:
              // Dragon head.
              // TODO: fixme
              textureId = "entity/skeleton/wither_skeleton";
              break;
          }
          loader = simpleTexture(textureId, texture);
          break;
        }
        case "leather_boots":
        case "leather_helmet":
        case "leather_chestplate":
          loader = leatherTexture("models/armor/leather_layer_1",
              item.get("color").intValue(0x96613A), texture);
          break;
        case "leather_leggings":
          loader = leatherTexture("models/armor/leather_layer_2",
              item.get("color").intValue(0x96613A), texture);
          break;
        case "golden_boots":
        case "golden_helmet":
        case "golden_chestplate":
          loader = simpleTexture("models/armor/gold_layer_1", texture);
          break;
        case "golden_leggings":
          loader = simpleTexture("models/armor/gold_layer_2", texture);
          break;
        case "iron_boots":
        case "iron_helmet":
        case "iron_chestplate":
          loader = simpleTexture("models/armor/iron_layer_1", texture);
          break;
        case "iron_leggings":
          loader = simpleTexture("models/armor/iron_layer_2", texture);
          break;
        case "chainmail_boots":
        case "chainmail_helmet":
        case "chainmail_chestplate":
          loader = simpleTexture("models/armor/chainmail_layer_1", texture);
          break;
        case "chainmail_leggings":
          loader = simpleTexture("models/armor/chainmail_layer_2", texture);
          break;
        case "diamond_boots":
        case "diamond_helmet":
        case "diamond_chestplate":
          loader = simpleTexture("models/armor/diamond_layer_1", texture);
          break;
        case "diamond_leggings":
          loader = simpleTexture("models/armor/diamond_layer_2", texture);
          break;
        case "netherite_boots":
        case "netherite_helmet":
        case "netherite_chestplate":
          loader = simpleTexture("models/armor/netherite_layer_1", texture);
          break;
        case "netherite_leggings":
          loader = simpleTexture("models/armor/netherite_layer_2", texture);
          break;
        default:
          Log.warnf("Unknown item ID: %s%n", id);
      }
      if (loader != null) {
        // TODO: defer loading.
        Map<String, TextureLoader> toLoad = Collections.singletonMap(textureId, loader);
        Log.infof("Loading textures: %s", toLoad.keySet().toString());
        Collection<Map.Entry<String, TextureLoader>> missing =
            TexturePackLoader.loadTextures(toLoad.entrySet());
        for (Map.Entry<String, TextureLoader> tex : missing) {
          Log.warnf("Failed to load texture: %s", tex.getValue());
        }
      }
    }
    TextureCache.put(item, texture);
    return texture;
  }

  private static TextureLoader simpleTexture(String id, Texture texture) {
    return new SimpleTexture("assets/minecraft/textures/" + id, texture);
  }

  private static TextureLoader leatherTexture(String id, int color, Texture texture) {
    String textureName = "assets/minecraft/textures/" + id;
    return new LayeredTextureLoader(textureName + "_overlay", texture,
        new ColoredTexture(textureName, color, texture));
  }

  public static PlayerEntity fromJson(JsonObject json) {
    return new PlayerEntity(json);
  }

  @Override public String toString() {
    return "player: " + uuid;
  }

  @Override public int hashCode() {
    return uuid.hashCode();
  }

  @Override public boolean equals(Object obj) {
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

  @Override public String[] partNames() {
    return partNames;
  }

  @Override public double getScale() {
    return scale;
  }

  @Override public void setScale(double value) {
    scale = value;
  }

  @Override public double getHeadScale() {
    return headScale;
  }

  @Override public void setHeadScale(double value) {
    headScale = value;
  }

  @Override public JsonObject getPose() {
    return pose;
  }

  @Override public String[] gearSlots() {
    return gearSlots;
  }

  @Override public JsonObject getGear() {
    return gear;
  }
}
