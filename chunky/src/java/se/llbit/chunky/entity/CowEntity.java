package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.model.builder.UVMapHelper;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;

public class CowEntity extends Entity implements Poseable, Variant {

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-6 / 16.0, -9 / 16.0, -5 / 16.0), new Vector3(6 / 16.0, 9 / 16.0, 5 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(18, 4).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
        )
    ).addBox(new Vector3(-2 / 16.0, -9 / 16.0, -6 / 16.0), new Vector3(2 / 16.0, -3 / 16.0, -5 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(52, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private static final Quad[] cold_wool = new BoxModelBuilder()
    .addBox(new Vector3(-6 / 16.0, -9 / 16.0, -5 / 16.0), new Vector3(6 / 16.0, 9 / 16.0, 5 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(20, 32).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .inflate(new Vector3(13 / 12.0, 11 / 10.0, 19 / 18.0))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private static final Quad[] leg = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, -12 / 16.0, -2 / 16.0), new Vector3(2 / 16.0, 0, 2 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(0, 16).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] temperate_head = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -4 / 16.0, -6 / 16.0), new Vector3(4 / 16.0, 4 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-5 / 16.0, 2 / 16.0, -4 / 16.0), new Vector3(-4 / 16.0, 5 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(22, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(4 / 16.0, 2 / 16.0, -4 / 16.0), new Vector3(5 / 16.0, 5 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(22, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-3 / 16.0, -4 / 16.0, -7 / 16.0), new Vector3(3 / 16.0, -1 / 16.0, -6 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(1, 33).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] cold_head = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -4 / 16.0, -6 / 16.0), new Vector3(4 / 16.0, 4 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-3 / 16.0, -4 / 16.0, -7 / 16.0), new Vector3(3 / 16.0, -1 / 16.0, -6 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(9, 33).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0), new Vector3(2 / 16.0, 6 / 16.0, 2 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(0, 32).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
          .translate(-6 / 16.0, 2 / 16.0, -2 / 16.0)
        )
    ).addBox(new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0), new Vector3(2 / 16.0, 6 / 16.0, 2 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(0, 40).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
          .translate(4 / 16.0, 2 / 16.0, -2 / 16.0)
        )
    ).toQuads();

  private static final Quad[] warm_head = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -4 / 16.0, -6 / 16.0), new Vector3(4 / 16.0, 4 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-3 / 16.0, -4 / 16.0, -7 / 16.0), new Vector3(3 / 16.0, -1 / 16.0, -6 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(1, 33).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-8 / 16.0, 1 / 16.0, -5 / 16.0), new Vector3(-4 / 16.0, 3 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(27, 0)
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-8 / 16.0, 3 / 16.0, -5 / 16.0), new Vector3(-6 / 16.0, 5 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(39, 0)
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(4 / 16.0, 1 / 16.0, -5 / 16.0), new Vector3(8 / 16.0, 3 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(27, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(6 / 16.0, 3 / 16.0, -5 / 16.0), new Vector3(8 / 16.0, 5 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.cow, 64, 64).atUVCoordinates(39, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private final JsonObject pose;

  private double scale = 1;
  private double headScale = 1;
  private String variant = "minecraft:temperate";

  private static final String[] partNames = {"all", "head", "body", "front_left_leg", "front_right_leg", "back_left_leg", "back_right_leg"};

  public CowEntity(Vector3 position, CompoundTag tag) {
    super(position);

    Tag rotation = tag.get("Rotation");
    double yaw = rotation.get(0).floatValue();
    double pitch = rotation.get(1).floatValue();

    boolean isBaby = tag.get("Age").intValue(1) < 0;
    if (isBaby) {
      this.scale = 0.5;
      this.headScale = 2;
    }

    pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(180 - yaw), 0)));
    pose.add("head", JsonUtil.vec3ToJson(new Vector3(QuickMath.degToRad(pitch), 0, 0)));

    this.variant = tag.get("variant").stringValue("minecraft:temperate");
  }

  public CowEntity(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.headScale = json.get("headScale").asDouble(1);
    this.pose = json.get("pose").object();
    this.variant = json.get("variant").stringValue("minecraft:temperate");
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();

    TextureMaterial skinMaterial = switch (variant) {
      case "minecraft:cold" -> new TextureMaterial(Texture.coldCow);
      case "minecraft:warm" -> new TextureMaterial(Texture.warmCow);
      // "minecraft:temperate" covered by default
      default -> new TextureMaterial(Texture.cow);
    };

    Vector3 allPose = JsonUtil.vec3FromJsonArray(pose.get("all"));
    Vector3 bodyPose = JsonUtil.vec3FromJsonArray(pose.get("body"));
    Vector3 frontLeftLegPose = JsonUtil.vec3FromJsonArray(pose.get("front_left_leg"));
    Vector3 frontRightLegPose = JsonUtil.vec3FromJsonArray(pose.get("front_right_leg"));
    Vector3 backLeftLegPose = JsonUtil.vec3FromJsonArray(pose.get("back_left_leg"));
    Vector3 backRightLegPose = JsonUtil.vec3FromJsonArray(pose.get("back_right_leg"));
    Vector3 headPose = JsonUtil.vec3FromJsonArray(pose.get("head"));

    Vector3 worldOffset = new Vector3(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Transform worldTransform = Transform.NONE
      .scale(scale)
      .rotateX(allPose.x)
      .rotateY(allPose.y)
      .rotateZ(allPose.z)
      .translate(worldOffset);

    Transform transform = Transform.NONE
      .rotateX(bodyPose.x)
      .rotateY(bodyPose.y)
      .rotateZ(bodyPose.z)
      .translate(0, 17 / 16.0, 1 / 16.0)
      .chain(worldTransform);
    for (Quad quad : body) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    if (variant.equals("minecraft:cold")) {
      transform = Transform.NONE
        .rotateX(bodyPose.x)
        .rotateY(bodyPose.y)
        .rotateZ(bodyPose.z)
        .translate(0, 17 / 16.0, 1 / 16.0)
        .chain(worldTransform);
      for (Quad quad : cold_wool) {
        quad.addTriangles(faces, skinMaterial, transform);
      }
    }

    transform = Transform.NONE
      .rotateX(frontRightLegPose.x)
      .rotateY(frontRightLegPose.y)
      .rotateZ(frontRightLegPose.z)
      .translate(4 / 16.0, 12 / 16.0, -6 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontLeftLegPose.x)
      .rotateY(frontLeftLegPose.y)
      .rotateZ(frontLeftLegPose.z)
      .translate(-4 / 16.0, 12 / 16.0, -6 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backRightLegPose.x)
      .rotateY(backRightLegPose.y)
      .rotateZ(backRightLegPose.z)
      .translate(4 / 16.0, 12 / 16.0, 7 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backLeftLegPose.x)
      .rotateY(backLeftLegPose.y)
      .rotateZ(backLeftLegPose.z)
      .translate(-4 / 16.0, 12 / 16.0, 7 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(headPose.x)
      .rotateY(headPose.y)
      .rotateZ(headPose.z)
      .scale(headScale)
      .translate(0, 20 / 16.0, -8 / 16.0)
      .chain(worldTransform);
    switch (variant) {
      case "minecraft:cold" -> {
        for (Quad quad : cold_head) {
          quad.addTriangles(faces, skinMaterial, transform);
        }
      }
      case "minecraft:warm" -> {
        for (Quad quad : warm_head) {
          quad.addTriangles(faces, skinMaterial, transform);
        }
      }
      // "minecraft:temperate" covered by default
      default -> {
        for (Quad quad : temperate_head) {
          quad.addTriangles(faces, skinMaterial, transform);
        }
      }
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "cow");
    json.add("position", position.toJson());
    json.add("scale", getScale());
    json.add("headScale", headScale);
    json.add("pose", pose);
    json.add("variant", variant);

    return json;
  }

  public static CowEntity fromJson(JsonObject json) {
    return new CowEntity(json);
  }

  @Override
  public String[] partNames() {
    return partNames;
  }

  @Override
  public double getScale() {
    return scale;
  }

  @Override
  public void setScale(double value) {
    scale = value;
  }

  @Override
  public void setHeadScale(double value) {
    headScale = value;
  }

  @Override
  public double getHeadScale() {
    return headScale;
  }

  @Override
  public JsonObject getPose() {
    return pose;
  }

  @Override
  public String[] variants() {
    return new String[]{"minecraft:temperate", "minecraft:cold", "minecraft:warm"};
  }

  @Override
  public String getVariant() {
    return variant;
  }

  @Override
  public void setVariant(String variant) {
    this.variant = variant;
  }
}
