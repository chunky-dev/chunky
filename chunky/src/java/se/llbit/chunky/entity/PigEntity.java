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

public class PigEntity extends Entity implements Poseable, Saddleable {

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-5 / 16.0, -8 / 16.0, -4 / 16.0), new Vector3(5 / 16.0, 8 / 16.0, 4 / 16.0), box ->
      box.forTextureSize(Texture.pig, 64, 32).atUVCoordinates(28, 8).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90)) // Body Boxes are often sideways for some reason?
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private static final Quad[] leg = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, -6 / 16.0, -2 / 16.0), new Vector3(2 / 16.0, 0, 2 / 16.0), box ->
      box.forTextureSize(Texture.pig, 64, 32).atUVCoordinates(0, 16).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] head = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -4 / 16.0, -6 / 16.0), new Vector3(4 / 16.0, 4 / 16.0, 2 / 16.0), box ->
      box.forTextureSize(Texture.pig, 64, 32).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).addBox(new Vector3(-2 / 16.0, -3 / 16.0, -7 / 16.0), new Vector3(2 / 16.0, 0, -6 / 16.0), box ->
      box.forTextureSize(Texture.pig, 64, 32).atUVCoordinates(16, 16).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] saddle = new BoxModelBuilder()
    .addBox(new Vector3(-5 / 16.0, -8 / 16.0, -4 / 16.0), new Vector3(5 / 16.0, 8 / 16.0, 4 / 16.0), box ->
      box.forTextureSize(Texture.pigSaddle, 64, 32).atUVCoordinates(28, 8).flipX().doubleSided()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .inflate(new Vector3(11 / 10.0, 9 / 8.0, 17 / 16.0))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private final JsonObject pose;

  private double scale = 1;
  private double headScale = 1;

  private boolean saddled;

  private static final String[] partNames = {"all", "head", "body", "front_left_leg", "front_right_leg", "back_left_leg", "back_right_leg"};

  public PigEntity(Vector3 position, CompoundTag tag) {
    super(position);

    Tag rotation = tag.get("Rotation");
    double yaw = rotation.get(0).floatValue();
    double pitch = rotation.get(1).floatValue();

    pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(180 - yaw), 0)));
    pose.add("head", JsonUtil.vec3ToJson(new Vector3(QuickMath.degToRad(pitch), 0, 0)));

    saddled = tag.get("Saddle").boolValue(false);
  }

  public PigEntity(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.headScale = json.get("headScale").asDouble(1);
    this.pose = json.get("pose").object();
    this.saddled = json.get("saddled").asBoolean(false);
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();

    TextureMaterial skinMaterial = new TextureMaterial(Texture.pig);
    TextureMaterial saddleMaterial = new TextureMaterial(Texture.pigSaddle);

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
      .translate(0, 10 / 16.0, 0)
      .chain(worldTransform);
    for (Quad quad : body) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontRightLegPose.x)
      .rotateY(frontRightLegPose.y)
      .rotateZ(frontRightLegPose.z)
      .translate(3 / 16.0, 6 / 16.0, -6 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontLeftLegPose.x)
      .rotateY(frontLeftLegPose.y)
      .rotateZ(frontLeftLegPose.z)
      .translate(-3 / 16.0, 6 / 16.0, -6 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backRightLegPose.x)
      .rotateY(backRightLegPose.y)
      .rotateZ(backRightLegPose.z)
      .translate(3 / 16.0, 6 / 16.0, 7 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backLeftLegPose.x)
      .rotateY(backLeftLegPose.y)
      .rotateZ(backLeftLegPose.z)
      .translate(-3 / 16.0, 6 / 16.0, 7 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(headPose.x)
      .rotateY(headPose.y)
      .rotateZ(headPose.z)
      .scale(headScale)
      .translate(0, 12 / 16.0, -8 / 16.0)
      .chain(worldTransform);
    for (Quad quad : head) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    if(saddled) {
      transform = Transform.NONE
        .rotateX(bodyPose.x)
        .rotateY(bodyPose.y)
        .rotateZ(bodyPose.z)
        .translate(0, 10 / 16.0, 0)
        .chain(worldTransform);
      for (Quad quad : saddle) {
        quad.addTriangles(faces, saddleMaterial, transform);
      }
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "pig");
    json.add("position", position.toJson());
    json.add("scale", getScale());
    json.add("headScale", headScale);
    json.add("pose", pose);
    json.add("saddled", saddled);

    return json;
  }

  public static PigEntity fromJson(JsonObject json) {
    return new PigEntity(json);
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
  public void setIsSaddled(boolean saddled) { this.saddled = saddled; }

  @Override
  public boolean isSaddled() { return saddled; }
}
