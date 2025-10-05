package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;

public class CopperGolemEntity extends Entity implements Poseable {
  private static final Quad[] head = new BoxModelBuilder()
    // head
    .addBox(new Vector3(-4 / 16.0, -5 / 16.0, -5 / 16.0), new Vector3(4 / 16.0, 0, 5 / 16.0), box ->
      box
        .grow(0.015 / 16.0)
        .forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, 5 / 16.0, 0))
    )
    // nose
    .addBox(new Vector3(-1 / 16.0, -2 / 16.0, -6 / 16.0), new Vector3(1 / 16.0, 1 / 16.0, -4 / 16.0), box ->
      box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(56, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, 1 / 16.0, 0))
    )
    // antenna
    .addBox(new Vector3(-1 / 16.0, -9 / 16.0, -1 / 16.0), new Vector3(1 / 16.0, -5 / 16.0, 1 / 16.0), box ->
      box
        .grow(-0.015 / 16.0)
        .forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 8).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, 14 / 16.0, 0))
    )
    .addBox(new Vector3(-2 / 16.0, -13 / 16.0, -2 / 16.0), new Vector3(2 / 16.0, -9 / 16.0, 2 / 16.0), box ->
      box
        .grow(-0.015 / 16.0)
        .forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 0).flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, 22 / 16.0, 0))
    )
    .toQuads();

  private static final Quad[] body = new BoxModelBuilder()
    .addBox(new Vector3(-4 / 16.0, -6 / 16.0, -3 / 16.0), new Vector3(4 / 16.0, 0, 3 / 16.0), box ->
      box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 15)
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(0, 6 / 16.0, 0))
    )
    .toQuads();

  private static final Quad[] rightArm = new BoxModelBuilder()
    .addBlockUnitsBox(-3, -1, -2, 3, 10, 4, box ->
      box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(50, 16)
        .flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(-4 / 16.0, 1 / 16.0, 0))
    )
    .toQuads();

  private static final Quad[] leftArm = new BoxModelBuilder()
    .addBlockUnitsBox(0, -1, -2, 3, 10, 4, box ->
      box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(36, 16)
        .flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
        .transform(Transform.NONE.translate(4 / 16.0, 1 / 16.0, 0))
    )
    .toQuads();

  private static final Quad[] rightLeg = new BoxModelBuilder()
    .addBlockUnitsBox(-4, 0, -2, 4, 5, 4, box ->
      box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(16, 27)
        .flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    )
    .toQuads();

  private static final Quad[] leftLeg = new BoxModelBuilder()
    .addBlockUnitsBox(0, 0, -2, 4, 5, 4, box ->
      box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 27)
        .flipX()
        .addTopFace().addBottomFace().addLeftFace().addRightFace().addFrontFace().addBackFace()
    )
    .toQuads();

  private final JsonObject pose;
  private double scale = 1;

  public CopperGolemEntity(Vector3 position) {
    super(position);
    pose = new JsonObject();
  }

  public void applyFacing(String facing) {
    Vector3 pose = getPose("all");
    if (facing.equals("east")) {
      pose.y = Math.toRadians(270);
    } else if (facing.equals("south")) {
      pose.y = Math.toRadians(180);
    } else if (facing.equals("west")) {
      pose.y = Math.toRadians(90);
    } else {
      pose.y = 0;
    }
    this.pose.set("all", JsonUtil.vec3ToJson(pose));
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();
    TextureMaterial material = new TextureMaterial(Texture.copperGolem); // TODO

    Vector3 allPose = JsonUtil.vec3FromJsonArray(pose.get("all"));

    Vector3 worldOffset = new Vector3(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Transform worldTransform = Transform.NONE
      .scale(scale)
      .rotateX(allPose.x)
      .rotateY(allPose.y)
      .rotateZ(allPose.z)
      .translate(worldOffset);

    Transform headTransform = Transform.NONE.translate(0, 11 / 16.0, 0).chain(worldTransform);
    for (Quad quad : head) {
      quad.addTriangles(faces, material, headTransform);
    }

    Transform bodyTransform = Transform.NONE.translate(0, 5 / 16.0, 0).chain(worldTransform);
    for (Quad quad : body) {
      quad.addTriangles(faces, material, bodyTransform);
    }

    Transform rightArmTransform = Transform.NONE.translate(0, 2 / 16.0, 0).chain(worldTransform);
    for (Quad quad : rightArm) {
      quad.addTriangles(faces, material, rightArmTransform);
    }

    Transform leftArmTransform = Transform.NONE.translate(0, 2 / 16.0, 0).chain(worldTransform);
    for (Quad quad : leftArm) {
      quad.addTriangles(faces, material, leftArmTransform);
    }

    Transform rightLegTransform = Transform.NONE.chain(worldTransform);
    for (Quad quad : rightLeg) {
      quad.addTriangles(faces, material, rightLegTransform);
    }

    Transform leftLegTransform = Transform.NONE.chain(worldTransform);
    for (Quad quad : leftLeg) {
      quad.addTriangles(faces, material, leftLegTransform);
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    return null;
  }

  @Override
  public String[] partNames() {
    return new String[]{"all"};
  }

  @Override
  public double getScale() {
    return scale;
  }

  @Override
  public void setScale(double value) {
    this.scale = scale;
  }

  @Override
  public JsonObject getPose() {
    return pose;
  }
}
