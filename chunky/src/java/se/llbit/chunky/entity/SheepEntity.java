package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.model.builder.UVMapHelper;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.DyedTextureMaterial;
import se.llbit.chunky.world.material.DyedTextureMaterial.DyeColor;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Constants;
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

public class SheepEntity extends Entity implements Poseable, Dyeable {

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -8 / 16.0, -3 / 16.0), new Vector3(4 / 16.0, 8 / 16.0, 3 / 16.0), box ->
      box.forTextureSize(Texture.sheep, 64, 32).atUVCoordinates(28, 8).flipX()
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private static final Quad[] leg = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, -12 / 16.0, -2 / 16.0), new Vector3(2 / 16.0, 0, 2 / 16.0), box ->
      box.forTextureSize(Texture.sheep, 64, 32).atUVCoordinates(0, 16).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] head = new BoxModelBuilder()
    .addBox(new Vector3(-3 / 16.0, -3 / 16.0, -8 / 16.0), new Vector3(3 / 16.0, 3 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.sheep, 64, 32).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    ).toQuads();

  private static final Quad[] legFur = new BoxModelBuilder()
    .addBox(new Vector3(-2 / 16.0, -6 / 16.0, -2 / 16.0), new Vector3(2 / 16.0, 0, 2 / 16.0), box ->
      box.forTextureSize(Texture.sheepFur, 64, 32).atUVCoordinates(0, 16).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .inflate(new Vector3(5 / 4.0, 7 / 6.0, 5 / 4.0))
          .translate(-0.5, -0.5, -0.5)
          .translate(0, 0.5 / 16.0, 0)
        )
    ).toQuads();

  private static final Quad[] bodyFur = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -8 / 16.0, -3 / 16.0), new Vector3(4 / 16.0, 8 / 16.0, 3 / 16.0), box ->
      box.forTextureSize(Texture.sheepFur, 64, 32).atUVCoordinates(28, 8).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .rotateX(Math.toRadians(-90))
          .inflate(new Vector3(11.5 / 8.0, 9.5 / 6.0, 19.5 / 16.0))
          .translate(-0.5, -0.5, -0.5)
        )
    ).toQuads();

  private static final Quad[] headFur = new BoxModelBuilder()
    .addBox(new Vector3(-3 / 16.0, -3 / 16.0, -6 / 16.0), new Vector3(3 / 16.0, 3 / 16.0, 0 / 16.0), box ->
      box.forTextureSize(Texture.sheepFur, 64, 32).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE
          .translate(0.5, 0.5, 0.5)
          .inflate(new Vector3(7.2 / 6.0, 7.2 / 6.0, 7.2 / 6.0))
          .translate(-0.5, -0.5, -0.5)
          .translate(0, 0, 0.6 / 16.0)
        )
    ).toQuads();

  private final JsonObject pose;

  private double scale = 1;
  private double headScale = 1;

  public boolean sheared;

  private final DyedTextureMaterial materialFur;

  private static final String[] partNames = {"all", "head", "body", "front_left_leg", "front_right_leg", "back_left_leg", "back_right_leg"};

  public SheepEntity(Vector3 position, CompoundTag tag) {
    super(position);

    materialFur = new DyedTextureMaterial(DyeColor.get(tag.get("Color").byteValue()), Texture.sheepFur);
    sheared = tag.get("Sheared").boolValue(false);

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
  }

  public SheepEntity(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.headScale = json.get("headScale").asDouble(1);
    this.pose = json.get("pose").object();
    this.materialFur = new DyedTextureMaterial(DyeColor.WHITE, Texture.sheepFur);
    materialFur.loadMaterialProperties(json.get("furMaterial").object());
    sheared = json.get("sheared").asBoolean(false);
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();

    TextureMaterial skinMaterial = new TextureMaterial(Texture.sheep);
    DyedTextureMaterial undercoatMaterial = new DyedTextureMaterial(materialFur.getColorInt(), Texture.sheepUndercoat);
    undercoatMaterial.emittance = materialFur.emittance;
    undercoatMaterial.specular = materialFur.specular;
    undercoatMaterial.ior = materialFur.ior;
    undercoatMaterial.roughness = materialFur.roughness;
    undercoatMaterial.metalness = materialFur.metalness;

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
      .translate(0, 15 / 16.0, 0)
      .chain(worldTransform);
    for (Quad quad : body) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontRightLegPose.x)
      .rotateY(frontRightLegPose.y)
      .rotateZ(frontRightLegPose.z)
      .translate(3 / 16.0, 12 / 16.0, -5 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(frontLeftLegPose.x)
      .rotateY(frontLeftLegPose.y)
      .rotateZ(frontLeftLegPose.z)
      .translate(-3 / 16.0, 12 / 16.0, -5 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backRightLegPose.x)
      .rotateY(backRightLegPose.y)
      .rotateZ(backRightLegPose.z)
      .translate(3 / 16.0, 12 / 16.0, 7 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(backLeftLegPose.x)
      .rotateY(backLeftLegPose.y)
      .rotateZ(backLeftLegPose.z)
      .translate(-3 / 16.0, 12 / 16.0, 7 / 16.0)
      .chain(worldTransform);
    for (Quad quad : leg) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    transform = Transform.NONE
      .rotateX(headPose.x)
      .rotateY(headPose.y)
      .rotateZ(headPose.z)
      .scale(headScale)
      .translate(0, 19 / 16.0, -6 / 16.0)
      .chain(worldTransform);
    for (Quad quad : head) {
      quad.addTriangles(faces, skinMaterial, transform);
    }

    double inflateOffset = 1.0 + Constants.OFFSET;

    if (sheared) {
      // The sheared overlay needs some specific translations and scaling to prevent z-fighting because of their rotation points.
      transform = Transform.NONE
        .scale(inflateOffset)
        .rotateX(bodyPose.x)
        .rotateY(bodyPose.y)
        .rotateZ(bodyPose.z)
        .translate(0, 15 / 16.0, 0)
        .chain(worldTransform);
      for (Quad quad : body) {
        quad.addTriangles(faces, undercoatMaterial, transform);
      }

      transform = Transform.NONE
        .translate(0, Constants.OFFSET, 0)
        .scale(inflateOffset)
        .rotateX(frontRightLegPose.x)
        .rotateY(frontRightLegPose.y)
        .rotateZ(frontRightLegPose.z)
        .translate(3 / 16.0, 12 / 16.0, -5 / 16.0)
        .chain(worldTransform);
      for (Quad quad : leg) {
        quad.addTriangles(faces, undercoatMaterial, transform);
      }

      transform = Transform.NONE
        .translate(0, Constants.OFFSET, 0)
        .scale(inflateOffset)
        .rotateX(frontLeftLegPose.x)
        .rotateY(frontLeftLegPose.y)
        .rotateZ(frontLeftLegPose.z)
        .translate(-3 / 16.0, 12 / 16.0, -5 / 16.0)
        .chain(worldTransform);
      for (Quad quad : leg) {
        quad.addTriangles(faces, undercoatMaterial, transform);
      }

      transform = Transform.NONE
        .translate(0, Constants.OFFSET, 0)
        .scale(inflateOffset)
        .rotateX(backRightLegPose.x)
        .rotateY(backRightLegPose.y)
        .rotateZ(backRightLegPose.z)
        .translate(3 / 16.0, 12 / 16.0, 7 / 16.0)
        .chain(worldTransform);
      for (Quad quad : leg) {
        quad.addTriangles(faces, undercoatMaterial, transform);
      }

      transform = Transform.NONE
        .translate(0, Constants.OFFSET, 0)
        .scale(inflateOffset)
        .rotateX(backLeftLegPose.x)
        .rotateY(backLeftLegPose.y)
        .rotateZ(backLeftLegPose.z)
        .translate(-3 / 16.0, 12 / 16.0, 7 / 16.0)
        .chain(worldTransform);
      for (Quad quad : leg) {
        quad.addTriangles(faces, undercoatMaterial, transform);
      }

      transform = Transform.NONE
        .translate(0, 0, Constants.OFFSET)
        .scale(inflateOffset)
        .rotateX(headPose.x)
        .rotateY(headPose.y)
        .rotateZ(headPose.z)
        .scale(headScale)
        .translate(0, 19 / 16.0, -6 / 16.0)
        .chain(worldTransform);
      for (Quad quad : head) {
        quad.addTriangles(faces, undercoatMaterial, transform);
      }
    } else {
      transform = Transform.NONE
        .rotateX(frontRightLegPose.x)
        .rotateY(frontRightLegPose.y)
        .rotateZ(frontRightLegPose.z)
        .translate(3 / 16.0, 12 / 16.0, -5 / 16.0)
        .chain(worldTransform);
      for (Quad quad : legFur) {
        quad.addTriangles(faces, materialFur, transform);
      }

      transform = Transform.NONE
        .rotateX(frontLeftLegPose.x)
        .rotateY(frontLeftLegPose.y)
        .rotateZ(frontLeftLegPose.z)
        .translate(-3 / 16.0, 12 / 16.0, -5 / 16.0)
        .chain(worldTransform);
      for (Quad quad : legFur) {
        quad.addTriangles(faces, materialFur, transform);
      }

      transform = Transform.NONE
        .rotateX(backRightLegPose.x)
        .rotateY(backRightLegPose.y)
        .rotateZ(backRightLegPose.z)
        .translate(3 / 16.0, 12 / 16.0, 7 / 16.0)
        .chain(worldTransform);
      for (Quad quad : legFur) {
        quad.addTriangles(faces, materialFur, transform);
      }

      transform = Transform.NONE
        .rotateX(backLeftLegPose.x)
        .rotateY(backLeftLegPose.y)
        .rotateZ(backLeftLegPose.z)
        .translate(-3 / 16.0, 12 / 16.0, 7 / 16.0)
        .chain(worldTransform);
      for (Quad quad : legFur) {
        quad.addTriangles(faces, materialFur, transform);
      }

      transform = Transform.NONE
        .rotateX(bodyPose.x)
        .rotateY(bodyPose.y)
        .rotateZ(bodyPose.z)
        .translate(0, 15 / 16.0, 0)
        .chain(worldTransform);
      for (Quad quad : bodyFur) {
        quad.addTriangles(faces, materialFur, transform);
      }

      transform = Transform.NONE
        .rotateX(headPose.x)
        .rotateY(headPose.y)
        .rotateZ(headPose.z)
        .scale(headScale)
        .translate(0, 19 / 16.0, -6 / 16.0)
        .chain(worldTransform);
      for (Quad quad : headFur) {
        quad.addTriangles(faces, materialFur, transform);
      }
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "sheep");
    json.add("position", position.toJson());
    json.add("scale", getScale());
    json.add("headScale", headScale);
    json.add("pose", pose);

    json.add("furMaterial", materialFur.saveMaterialProperties());
    json.add("sheared", sheared);
    return json;
  }

  public static SheepEntity fromJson(JsonObject json) {
    return new SheepEntity(json);
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
  public DyedTextureMaterial getMaterial() {
    return materialFur;
  }
}
