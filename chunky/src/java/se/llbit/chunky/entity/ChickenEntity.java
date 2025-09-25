package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.model.builder.UVMapHelper;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ChickenEntity extends Entity implements Poseable, Variant {

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-3 / 16.0, -4 / 16.0, -3 / 16.0), new Vector3(3 / 16.0, 4 / 16.0, 3 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(0, 9).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  //TODO: Skip adding blank faces on Chicken legs? Same for Pig's saddle.
  private static final Quad[] leg = new BoxModelBuilder()
    .addBox(new Vector3(0 / 16.0, -5 / 16.0, 0 / 16.0), new Vector3(3 / 16.0, 0, 3 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(26, 0).flipX().doubleSided()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, Ray.OFFSET, 0)) // Prevent Z-Fighting with the block the Chicken is standing on
    ).toQuads();

  private static final Quad[] wing = new BoxModelBuilder()
    .addBox(new Vector3(0 / 16.0, -4 / 16.0, -3 / 16.0), new Vector3(1 / 16.0, 0 / 16.0, 3 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(24, 13).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] head = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, 0 / 16.0, -3 / 16.0), new Vector3(2 / 16.0, 6 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-2 / 16.0, 2 / 16.0, -5 / 16.0), new Vector3(2 / 16.0, 4 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(14, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-1 / 16.0, 0 / 16.0, -4 / 16.0), new Vector3(1 / 16.0, 2 / 16.0, -2 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(14, 4).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] cold_head = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, 0 / 16.0, -3 / 16.0), new Vector3(2 / 16.0, 6 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-2 / 16.0, 2 / 16.0, -5 / 16.0), new Vector3(2 / 16.0, 4 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(14, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-1 / 16.0, 0 / 16.0, -4 / 16.0), new Vector3(1 / 16.0, 2 / 16.0, -2 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(14, 4).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-3 / 16.0, 4 / 16.0, -3 / 16.0), new Vector3(3 / 16.0, 7 / 16.0, 1 / 16.0), box ->
      box.forTextureSize(Texture.chicken, 64, 32).atUVCoordinates(44, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, 0, -Ray.OFFSET)).doubleSided()
    ).toQuads();

  private static final Quad[] cold_tail = {
    new Quad(new Vector3(0, 3 / 16.0, 3 / 16.0), new Vector3(0, -2 / 16.0, 3 / 16.0), new Vector3(0, 3 / 16.0, 0), new Vector4(38 / 64.0, 43 / 64.0, 15 / 32.0, 18 / 32.0)),
    new Quad(new Vector3(0, 3 / 16.0, 0 / 16.0), new Vector3(0, -2 / 16.0, 0), new Vector3(0, 3 / 16.0, 3 / 16.0), new Vector4(48 / 64.0, 43 / 64.0, 18 / 32.0, 15 / 32.0))
  };

  private final JsonObject pose;

  private double scale = 1;
  private double headScale = 1;

  private String variant = "minecraft:temperate";

  private static final String[] partNames = {"all", "head", "body", "left_leg", "right_leg", "left_wing", "right_wing", "tail"};

  public ChickenEntity(Vector3 position, CompoundTag tag) {
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

    variant = tag.get("variant").stringValue("minecraft:temperate");
  }

  public ChickenEntity(JsonObject json) {
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
      case "minecraft:cold" -> new TextureMaterial(Texture.coldChicken);
      case "minecraft:warm" -> new TextureMaterial(Texture.warmChicken);
      default -> new TextureMaterial(Texture.chicken);
    };

    Vector3 allPose = JsonUtil.vec3FromJsonArray(pose.get("all"));
    Vector3 bodyPose = JsonUtil.vec3FromJsonArray(pose.get("body"));
    Vector3 leftLegPose = JsonUtil.vec3FromJsonArray(pose.get("left_leg"));
    Vector3 rightLegPose = JsonUtil.vec3FromJsonArray(pose.get("right_leg"));
    Vector3 leftWingPose = JsonUtil.vec3FromJsonArray(pose.get("left_wing"));
    Vector3 rightWingPose = JsonUtil.vec3FromJsonArray(pose.get("right_wing"));
    Vector3 headPose = JsonUtil.vec3FromJsonArray(pose.get("head"));
    Vector3 tailPose = JsonUtil.vec3FromJsonArray(pose.get("tail"));

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
      .translate(0, 8 / 16.0, 0 / 16.0)
      .chain(worldTransform);
    for (Quad quad : body) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    if (variant.equals("minecraft:cold")) {
      transform = Transform.NONE
        .rotateX(tailPose.x)
        .rotateY(tailPose.y)
        .rotateZ(tailPose.z)
        .translate(0, 9 / 16.0, 3 / 16.0)
        .chain(worldTransform);
      for (Quad quad : cold_tail) {
        quad.addTriangles(faces, skinMaterial, transform);
      }
    }

    transform = Transform.NONE
      .translate(-1.5 / 16.0, 0 / 16.0, -3 / 16.0) // Move rotation point to middle of back of the leg
      .rotateX(rightLegPose.x)
      .rotateY(rightLegPose.y)
      .rotateZ(rightLegPose.z)
      .translate(1.5 / 16.0, 5 / 16.0, 3 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .translate(-1.5 / 16.0, 0 / 16.0, -3 / 16.0)
      .rotateX(leftLegPose.x)
      .rotateY(leftLegPose.y)
      .rotateZ(leftLegPose.z)
      .translate(-1.5 / 16.0, 5 / 16.0, 3 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(rightWingPose.x)
      .rotateY(rightWingPose.y)
      .rotateZ(rightWingPose.z)
      .translate(3 / 16.0, 11 / 16.0, 0 / 16.0)
      .chain(worldTransform);
    for (Quad quad : wing) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .translate(-1 / 16.0, 0, 0) // Correct the rotation point because we're copying the wing Quads for the right wing
      .rotateX(leftWingPose.x)
      .rotateY(leftWingPose.y)
      .rotateZ(leftWingPose.z)
      .translate(-3 / 16.0, 11 / 16.0, 0 / 16.0)
      .chain(worldTransform);
    for (Quad quad : wing) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(headPose.x)
      .rotateY(headPose.y)
      .rotateZ(headPose.z)
      .scale(headScale)
      .translate(0, 9 / 16.0, -3 / 16.0)
      .chain(worldTransform);

    if (variant.equals("minecraft:cold")) {
      for (Quad quad : cold_head) {
        quad.addTriangles(faces, skinMaterial, transform);
      }
    } else {
      for (Quad quad : head) {
        quad.addTriangles(faces, skinMaterial, transform);
      }
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "chicken");
    json.add("position", position.toJson());
    json.add("scale", getScale());
    json.add("headScale", headScale);
    json.add("pose", pose);
    json.add("variant", variant);

    return json;
  }

  public static Collection<Entity> fromJson(JsonObject json) {
    return Collections.singleton(new ChickenEntity(json));
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
