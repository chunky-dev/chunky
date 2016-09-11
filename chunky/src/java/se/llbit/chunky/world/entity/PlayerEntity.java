/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.entity;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texturepack.EntityTextureLoader;
import se.llbit.chunky.resources.texturepack.TextureFormatError;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;

public class PlayerEntity extends Entity {

  public final String uuid;
  public double yaw;
  public double pitch;
  public double headYaw = 0.0;
  public double leftLegPose;
  public double rightLegPose;
  public double leftArmPose;
  public double rightArmPose;
  public PlayerModel model;
  public String skin = "";

  public PlayerEntity(String uuid, Vector3 position, double yawDegrees, double pitchDegrees) {
    this(uuid, position, QuickMath.degToRad(180 - yawDegrees), -QuickMath.degToRad(pitchDegrees),
        0.4, -0.4, 0.4, -0.4, PlayerModel.STEVE);
  }

  public PlayerEntity(String uuid, Vector3 position, double yaw, double pitch, double leftLegPose,
      double rightLegPose, double leftArmPose, double rightArmPose, PlayerModel model) {
    super(position);
    this.uuid = uuid;
    this.yaw = yaw;
    this.pitch = pitch;
    this.leftLegPose = leftLegPose;
    this.rightLegPose = rightLegPose;
    this.leftArmPose = leftArmPose;
    this.rightArmPose = rightArmPose;
    this.model = model;
  }

  private void poseLimb(Box part, Transform transform, Transform offset) {
    part.transform(transform);
    part.transform(Transform.NONE.rotateY(yaw));
    part.transform(offset);
  }

  private void poseHead(Box part, Transform transform, Transform offset) {
    part.transform(Transform.NONE.rotateX(pitch));
    part.transform(transform);
    part.transform(Transform.NONE.rotateY(yaw));
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
    Collection<Primitive> faces = new LinkedList<>();
    Transform offsetTransform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Box head = new Box(-4 / 16., 4 / 16., -4 / 16., 4 / 16., -4 / 16., 4 / 16.);
    poseHead(head, Transform.NONE.rotateY(headYaw).translate(0, 28 / 16., 0), offsetTransform);
    head.addFrontFaces(faces, texture, texture.headFront);
    head.addBackFaces(faces, texture, texture.headBack);
    head.addLeftFaces(faces, texture, texture.headLeft);
    head.addRightFaces(faces, texture, texture.headRight);
    head.addTopFaces(faces, texture, texture.headTop);
    head.addBottomFaces(faces, texture, texture.headBottom);
    Box hat = new Box(-4.2 / 16., 4.2 / 16., -4.2 / 16., 4.2 / 16., -4.2 / 16., 4.2 / 16.);
    poseHead(hat, Transform.NONE.rotateY(headYaw).translate(0, 28.2 / 16., 0), offsetTransform);
    hat.addFrontFaces(faces, texture, texture.hatFront);
    hat.addBackFaces(faces, texture, texture.hatBack);
    hat.addLeftFaces(faces, texture, texture.hatLeft);
    hat.addRightFaces(faces, texture, texture.hatRight);
    hat.addTopFaces(faces, texture, texture.hatTop);
    hat.addBottomFaces(faces, texture, texture.hatBottom);
    Box chest = new Box(-4 / 16., 4 / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    poseLimb(chest, Transform.NONE.translate(0, 18 / 16., 0), offsetTransform);
    chest.addFrontFaces(faces, texture, texture.chestFront);
    chest.addBackFaces(faces, texture, texture.chestBack);
    chest.addLeftFaces(faces, texture, texture.chestLeft);
    chest.addRightFaces(faces, texture, texture.chestRight);
    chest.addTopFaces(faces, texture, texture.chestTop);
    chest.addBottomFaces(faces, texture, texture.chestBottom);
    Box leftLeg = new Box(-2 / 16., 2 / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    poseLimb(leftLeg, Transform.NONE.translate(0, -6 / 16., 0).rotateX(leftLegPose)
        .translate(-2 / 16., 12 / 16., 0), offsetTransform);
    leftLeg.addFrontFaces(faces, texture, texture.leftLegFront);
    leftLeg.addBackFaces(faces, texture, texture.leftLegBack);
    leftLeg.addLeftFaces(faces, texture, texture.leftLegLeft);
    leftLeg.addRightFaces(faces, texture, texture.leftLegRight);
    leftLeg.addTopFaces(faces, texture, texture.leftLegTop);
    leftLeg.addBottomFaces(faces, texture, texture.leftLegBottom);
    Box rightLeg = new Box(-2 / 16., 2 / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    poseLimb(rightLeg, Transform.NONE.translate(0, -6 / 16., 0).rotateX(rightLegPose)
        .translate(2 / 16., 12 / 16., 0), offsetTransform);
    rightLeg.addFrontFaces(faces, texture, texture.rightLegFront);
    rightLeg.addBackFaces(faces, texture, texture.rightLegBack);
    rightLeg.addLeftFaces(faces, texture, texture.rightLegLeft);
    rightLeg.addRightFaces(faces, texture, texture.rightLegRight);
    rightLeg.addTopFaces(faces, texture, texture.rightLegTop);
    rightLeg.addBottomFaces(faces, texture, texture.rightLegBottom);
    Box leftArm = new Box(-armWidth / 16., armWidth / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    poseLimb(leftArm, Transform.NONE.translate(0, -5 / 16., 0).rotateX(leftArmPose)
        .translate(-(4 + armWidth) / 16., 23 / 16., 0), offsetTransform);
    leftArm.addFrontFaces(faces, texture, texture.leftArmFront);
    leftArm.addBackFaces(faces, texture, texture.leftArmBack);
    leftArm.addLeftFaces(faces, texture, texture.leftArmLeft);
    leftArm.addRightFaces(faces, texture, texture.leftArmRight);
    leftArm.addTopFaces(faces, texture, texture.leftArmTop);
    leftArm.addBottomFaces(faces, texture, texture.leftArmBottom);
    Box rightArm = new Box(-armWidth / 16., armWidth / 16., -6 / 16., 6 / 16., -2 / 16., 2 / 16.);
    poseLimb(rightArm, Transform.NONE.translate(0, -5 / 16., 0).rotateX(rightArmPose)
        .translate((4 + armWidth) / 16., 23 / 16., 0), offsetTransform);
    rightArm.addFrontFaces(faces, texture, texture.rightArmFront);
    rightArm.addBackFaces(faces, texture, texture.rightArmBack);
    rightArm.addLeftFaces(faces, texture, texture.rightArmLeft);
    rightArm.addRightFaces(faces, texture, texture.rightArmRight);
    rightArm.addTopFaces(faces, texture, texture.rightArmTop);
    rightArm.addBottomFaces(faces, texture, texture.rightArmBottom);
    return faces;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "player");
    json.add("uuid", uuid);
    json.add("position", position.toJson());
    json.add("model", model.name());
    json.add("skin", skin);
    json.add("pitch", pitch);
    json.add("yaw", yaw);
    json.add("leftLegPose", leftLegPose);
    json.add("rightLegPose", rightLegPose);
    json.add("leftArmPose", leftArmPose);
    json.add("rightArmPose", rightArmPose);
    json.add("headYaw", headYaw);
    return json;
  }

  public static PlayerEntity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    PlayerModel model = PlayerModel.get(json.get("model").stringValue("STEVE"));
    double pitch = json.get("pitch").doubleValue(0.0);
    double yaw = json.get("yaw").doubleValue(0.0);
    String uuid = json.get("uuid").stringValue("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
    String skin = json.get("skin").stringValue("");
    double leftLegPose = json.get("leftLegPose").doubleValue(0.0);
    double rightLegPose = json.get("rightLegPose").doubleValue(0.0);
    double leftArmPose = json.get("leftArmPose").doubleValue(0.0);
    double rightArmPose = json.get("rightArmPose").doubleValue(0.0);
    PlayerEntity entity =
        new PlayerEntity(uuid, position, yaw, pitch, leftLegPose, rightLegPose, leftArmPose,
            rightArmPose, model);
    entity.headYaw = json.get("headYaw").doubleValue(0.0);
    entity.skin = skin;
    return entity;
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
    leftLegPose = (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    rightLegPose = -leftLegPose;
    leftArmPose = (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    rightArmPose = -leftArmPose;
  }

  private void randomLook(Random random) {
    yaw = (random.nextFloat() - 0.5) * QuickMath.TAU;
    headYaw = 0.4 * (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
    pitch = (random.nextFloat() - 0.5) * QuickMath.HALF_PI;
  }

  public void lookAt(Vector3 camera) {
    Vector3 dir = new Vector3(camera);
    Vector3 face = new Vector3(position);
    face.add(0, 28 / 16., 0);
    dir.sub(face);
    dir.normalize();
    yaw = FastMath.atan2(dir.x, dir.z) + Math.PI - headYaw;
    pitch = Math.asin(dir.y);
  }
}
