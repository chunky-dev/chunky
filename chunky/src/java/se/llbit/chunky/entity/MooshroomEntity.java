package se.llbit.chunky.entity;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.model.builder.UVMapHelper;
import se.llbit.chunky.model.minecraft.SpriteModel;
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

public class MooshroomEntity extends Entity implements Poseable, Variant {

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-6 / 16.0, -9 / 16.0, -5 / 16.0), new Vector3(6 / 16.0, 9 / 16.0, 5 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(18, 4).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
        )
    ).addBox(new Vector3(-2 / 16.0, -9 / 16.0, -6 / 16.0), new Vector3(2 / 16.0, -3 / 16.0, -5 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(52, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private static final Quad[] leg = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, -12 / 16.0, -2 / 16.0), new Vector3(2 / 16.0, 0, 2 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(0, 16).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] head = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -4 / 16.0, -6 / 16.0), new Vector3(4 / 16.0, 4 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-5 / 16.0, 2 / 16.0, -4 / 16.0), new Vector3(-4 / 16.0, 5 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(22, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(4 / 16.0, 2 / 16.0, -4 / 16.0), new Vector3(5 / 16.0, 5 / 16.0, -3 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(22, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-3 / 16.0, -4 / 16.0, -7 / 16.0), new Vector3(3 / 16.0, -1 / 16.0, -6 / 16.0), box ->
      box.forTextureSize(Texture.brownMooshroom, 64, 64).atUVCoordinates(1, 33).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] mushroom = new SpriteModel(Texture.redMooshroom).getQuads();

  private final JsonObject pose;

  private double scale = 1;
  private double headScale = 1;
  public boolean showMushrooms = true;
  private String type = "red";

  private static final String[] partNames = {"all", "head", "body", "front_left_leg", "front_right_leg", "back_left_leg", "back_right_leg"};
  private static final String[] variantNames = {"red", "brown"};

  public MooshroomEntity(Vector3 position, CompoundTag tag) {
    super(position);

    Tag rotation = tag.get("Rotation");
    double yaw = rotation.get(0).floatValue();
    double pitch = rotation.get(1).floatValue();

    this.type = tag.get("Type").stringValue("red");

    boolean isBaby = tag.get("Age").intValue(1) < 0;
    if (isBaby) {
      this.scale = 0.5;
      this.headScale = 2;
      this.showMushrooms = false;
    }

    pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(180 - yaw), 0)));
    pose.add("head", JsonUtil.vec3ToJson(new Vector3(QuickMath.degToRad(pitch), 0, 0)));
  }

  public MooshroomEntity(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.headScale = json.get("headScale").asDouble(1);
    this.pose = json.get("pose").object();
    this.type = json.get("type").stringValue("red");
    this.showMushrooms = json.get("showShrooms").asBoolean(true);
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();

    TextureMaterial skinMaterial;
    TextureMaterial shroomMaterial;

    switch(type) {
      case "brown":
        skinMaterial = new TextureMaterial(Texture.brownMooshroom);
        shroomMaterial = new TextureMaterial(Texture.brownMushroom);
        break;
      default:
      case "red":
        skinMaterial = new TextureMaterial(Texture.redMooshroom);
        shroomMaterial = new TextureMaterial(Texture.redMushroom);
    }

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
    for (Quad quad : head) {
      quad.addTriangles(faces, skinMaterial, transform);
    }
    if(showMushrooms){
      // Head Mushroom
      transform = Transform.NONE
        .translate(-8 / 16.0, 0 / 16.0, -8 / 16.0)
        .rotateY(FastMath.toRadians(-5))
        .translate(0, 4 / 16.0, -3 / 16.0)
        .rotateX(headPose.x)
        .rotateY(headPose.y)
        .rotateZ(headPose.z)
        .scale(headScale)
        .translate(0 / 16.0, 20 / 16.0, -8 / 16.0)
        .chain(worldTransform);
      for (Quad quad : mushroom) {
        quad.addTriangles(faces, shroomMaterial, transform);
      }
      // Back-body Mushroom
      transform = Transform.NONE
        .translate(-8 / 16.0, 0 / 16.0, -8 / 16.0)
        .rotateY(FastMath.toRadians(-40))
        .translate(-3 / 16.0, 5 / 16.0, 7 / 16.0)
        .rotateX(bodyPose.x)
        .rotateY(bodyPose.y)
        .rotateZ(bodyPose.z)
        .translate(0, 17 / 16.0, 1 / 16.0)
        .chain(worldTransform);
      for (Quad quad : mushroom) {
        quad.addTriangles(faces, shroomMaterial, transform);
      }
      // Middle-back Mushroom
      transform = Transform.NONE
        .translate(-8 / 16.0, 0 / 16.0, -8 / 16.0)
        .translate(2 / 16.0, 5 / 16.0, -1 / 16.0)
        .rotateX(bodyPose.x)
        .rotateY(bodyPose.y)
        .rotateZ(bodyPose.z)
        .translate(0, 17 / 16.0, 1 / 16.0)
        .chain(worldTransform);
      for (Quad quad : mushroom) {
        quad.addTriangles(faces, shroomMaterial, transform);
      }
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "mooshroom");
    json.add("position", position.toJson());
    json.add("scale", getScale());
    json.add("headScale", headScale);
    json.add("pose", pose);
    json.add("showShrooms", showMushrooms);
    json.add("type", type);

    return json;
  }

  public static MooshroomEntity fromJson(JsonObject json) {
    return new MooshroomEntity(json);
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
  public void setHeadScale(double value) { headScale = value; }

  @Override
  public double getHeadScale() { return headScale; }

  @Override
  public JsonObject getPose() {
    return pose;
  }

  @Override
  public String[] variants() {
    return variantNames;
  }

  @Override
  public String getVariant() {
    return type;
  }

  @Override
  public void setVariant(String variant) {
    type = variant;
  }
}
