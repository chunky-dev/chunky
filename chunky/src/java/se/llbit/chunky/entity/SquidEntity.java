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
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;

public class SquidEntity extends Entity implements Poseable {

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-6 / 16.0, -3 / 16.0, -6 / 16.0), new Vector3(6 / 16.0, 13 / 16.0, 6 / 16.0), box ->
      box.forTextureSize(Texture.squid, 64, 32).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE)
    ).toQuads();

  private static final Quad[] tentacle = new BoxModelBuilder()
    .addBox(new Vector3(-1 / 16.0, -17 / 16.0, -1 / 16.0), new Vector3(1 / 16.0, 1 / 16.0, 1 / 16.0), box ->
      box.forTextureSize(Texture.squid, 64, 32).atUVCoordinates(48, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private final JsonObject pose;

  private double scale = 1;

  private static final String[] partNames = {"all", "front_tentacle", "front_left_tentacle", "left_tentacle", "back_left_tentacle", "back_tentacle", "back_right_tentacle", "right_tentacle", "front_right_tentacle"};

  public SquidEntity(Vector3 position, CompoundTag tag) {
    super(position);

    Tag rotation = tag.get("Rotation");
    double yaw = rotation.get(0).floatValue();
    double pitch = rotation.get(1).floatValue();

    pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(QuickMath.degToRad(pitch), QuickMath.degToRad(180 - yaw), 0)));
    pose.add("front_left_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(-45), 0)));
    pose.add("left_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(-90), 0)));
    pose.add("back_left_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(-135), 0)));
    pose.add("back_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(180), 0)));
    pose.add("back_right_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(135), 0)));
    pose.add("right_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(90), 0)));
    pose.add("front_right_tentacle", JsonUtil.vec3ToJson(new Vector3(0, QuickMath.degToRad(45), 0)));
  }

  public SquidEntity(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.pose = json.get("pose").object();
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();

    TextureMaterial skinMaterial = new TextureMaterial(Texture.squid);

    Vector3 allPose = JsonUtil.vec3FromJsonArray(pose.get("all"));
    Vector3 frontTentacle = JsonUtil.vec3FromJsonArray(pose.get("front_tentacle"));
    Vector3 frontLeftTentacle = JsonUtil.vec3FromJsonArray(pose.get("front_left_tentacle"));
    Vector3 leftTentacle = JsonUtil.vec3FromJsonArray(pose.get("left_tentacle"));
    Vector3 backLeftTentacle = JsonUtil.vec3FromJsonArray(pose.get("back_left_tentacle"));
    Vector3 backTentacle = JsonUtil.vec3FromJsonArray(pose.get("back_tentacle"));
    Vector3 backRightTentacle = JsonUtil.vec3FromJsonArray(pose.get("back_right_tentacle"));
    Vector3 rightTentacle = JsonUtil.vec3FromJsonArray(pose.get("right_tentacle"));
    Vector3 frontRightTentacle = JsonUtil.vec3FromJsonArray(pose.get("front_right_tentacle"));

    Vector3 worldOffset = new Vector3(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Transform worldTransform = Transform.NONE
      .scale(scale)
      .rotateX(allPose.x)
      .rotateY(allPose.y)
      .rotateZ(allPose.z)
      .translate(worldOffset);

    Transform transform = Transform.NONE
      .chain(worldTransform);
    for (Quad quad : body) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontTentacle.x)
      .rotateY(frontTentacle.y)
      .rotateZ(frontTentacle.z)
      .translate(0 / 16.0, -3 / 16.0, -5 / 16.0 + Ray.OFFSET) // Prevent Z-fighting
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontLeftTentacle.x)
      .rotateY(frontLeftTentacle.y)
      .rotateZ(frontLeftTentacle.z)
      .translate(-4 / 16.0, -3 / 16.0, -4 / 16.0)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(leftTentacle.x)
      .rotateY(leftTentacle.y)
      .rotateZ(leftTentacle.z)
      .translate(-5 / 16.0 + Ray.OFFSET, -3 / 16.0, 0 / 16.0)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backLeftTentacle.x)
      .rotateY(backLeftTentacle.y)
      .rotateZ(backLeftTentacle.z)
      .translate(-4 / 16.0, -3 / 16.0, 4 / 16.0)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backTentacle.x)
      .rotateY(backTentacle.y)
      .rotateZ(backTentacle.z)
      .translate(0 / 16.0, -3 / 16.0, 5 / 16.0 - Ray.OFFSET)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backRightTentacle.x)
      .rotateY(backRightTentacle.y)
      .rotateZ(backRightTentacle.z)
      .translate(4 / 16.0, -3 / 16.0, 4 / 16.0)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(rightTentacle.x)
      .rotateY(rightTentacle.y)
      .rotateZ(rightTentacle.z)
      .translate(5 / 16.0 + Ray.OFFSET, -3 / 16.0, 0 / 16.0)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontRightTentacle.x)
      .rotateY(frontRightTentacle.y)
      .rotateZ(frontRightTentacle.z)
      .translate(4 / 16.0, -3 / 16.0, -4 / 16.0)
      .chain(worldTransform);
    for (Quad quad : tentacle) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "squid");
    json.add("position", position.toJson());
    json.add("scale", getScale());
    json.add("pose", pose);

    return json;
  }

  public static SquidEntity fromJson(JsonObject json) {
    return new SquidEntity(json);
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
  public JsonObject getPose() {
    return pose;
  }

  @Override
  public boolean hasHead() { return false; }
}
