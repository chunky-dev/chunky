package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;

public class CopperGolemStatueEntity extends Entity {
  private static class CopperGolemStarModel {
    private static final Quad[] body = new BoxModelBuilder()
      .addBlockUnitsBox(-4, 5, -3, 8, 6, 6, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 15).flipX()
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] head = new BoxModelBuilder()
      .addBlockUnitsBox(-4, 11, -5, 8, 5, 10, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-1, 10, -6, 2, 3, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(56, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-1, 16, -1, 2, 4, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 8).flipX()
          .addAllFaces()
          .grow(-0.01 / 16.)
      )
      .addBlockUnitsBox(-2, 20, -2, 4, 4, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 0).flipX()
          .addAllFaces()
          .grow(-0.01 / 16.)
      )
      .toQuads();
    private static final Quad[] rightArm = new BoxModelBuilder()
      .addBlockUnitsBox(1.5, 5, -2, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(36, 16).flipX()
          .transform(Transform.NONE
            .translate(-3 / 16., -10 / 16., 0 / 16.)
            .rotateZ(Math.toRadians(110))
            .translate(3 / 16., 10 / 16., 0 / 16.)
          )
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] leftArm = new BoxModelBuilder()
      .addBlockUnitsBox(-4.5, 5, -2, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(50, 16).flipX()
          .transform(Transform.NONE
            .translate(3 / 16., -10 / 16., 0 / 16.)
            .rotateZ(Math.toRadians(-110))
            .translate(-3 / 16., 10 / 16., 0 / 16.)
          )
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] rightLeg = new BoxModelBuilder()
      .addBlockUnitsBox(0.6500000000000004, 0.5, -1.99, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 27).flipX()
          .transform(Transform.NONE
            .translate(-2.65 / 16., -3 / 16., -0.01 / 16.)
            .rotateZ(Math.toRadians(15))
            .translate(2.65 / 16., 3 / 16., 0.01 / 16.)
          )
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] leftLeg = new BoxModelBuilder()
      .addBlockUnitsBox(-4.65, 0.5, -2, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(16, 27).flipX()
          .transform(Transform.NONE
            .translate(2.65 / 16., -3 / 16., 0 / 16.)
            .rotateZ(Math.toRadians(-15))
            .translate(-2.65 / 16., 3 / 16., 0 / 16.)
          )
          .addAllFaces()
      )
      .toQuads();

    public static void getPrimitives(Collection<Primitive> faces, Material material, Transform transform) {
      for (Quad quad : head) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : body) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightLeg) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftLeg) {
        quad.addTriangles(faces, material, transform);
      }
    }
  }

  private static class CopperGolemRunningModel {
    private static final Quad[] body = new BoxModelBuilder()
      .addBlockUnitsBox(-4, 4.8, -3, 8, 6, 6, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 15).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(0 / 16., -4.8 / 16., -0.5 / 16.)
            .rotateX(Math.toRadians(-6.92217))
            .rotateY(Math.toRadians(-4.59656))
            .rotateZ(Math.toRadians(-3.86389))
            .translate(0 / 16., 4.8 / 16., 0.5 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] head = new BoxModelBuilder()
      .addBlockUnitsBox(-3.7, 10.6, -7, 8, 5, 10, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-0.7, 9.6, -8, 2, 3, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(56, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-0.7, 15.6, -3, 2, 4, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 8).flipX()
          .addAllFaces()
          .grow(-0.01 / 16.)
      )
      .addBlockUnitsBox(-1.7000000000000002, 19.6, -4, 4, 4, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 0).flipX()
          .addAllFaces()
          .grow(-0.01 / 16.)
      )
      .toQuads();
    private static final Quad[] rightArm = new BoxModelBuilder()
      .addBlockUnitsBox(4.4, 2, -3, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(36, 16).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(-4.4 / 16., -11 / 16., 1 / 16.)
            .rotateX(Math.toRadians(-57.5))
            .translate(4.4 / 16., 11 / 16., -1 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] leftArm = new BoxModelBuilder()
      .addBlockUnitsBox(-6.6, 2, -2, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(50, 16).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(3.6 / 16., -11 / 16., 0 / 16.)
            .rotateX(Math.toRadians(50))
            .translate(-3.6 / 16., 11 / 16., 0 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] rightLeg = new BoxModelBuilder()
      .addBlockUnitsBox(-0.10000000000000009, 0, -1.99, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 27).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(-2 / 16., -5 / 16., 0.9 / 16.)
            .rotateX(Math.toRadians(50))
            .translate(2 / 16., 5 / 16., -0.9 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] leftLeg = new BoxModelBuilder()
      .addBlockUnitsBox(-3.9, 0, -2, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(16, 27).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(2 / 16., -5 / 16., 0 / 16.)
            .rotateX(Math.toRadians(-45))
            .translate(-2 / 16., 5 / 16., 0 / 16.)
          )
      )
      .toQuads();

    public static void getPrimitives(Collection<Primitive> faces, Material material, Transform transform) {
      for (Quad quad : head) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : body) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightLeg) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftLeg) {
        quad.addTriangles(faces, material, transform);
      }
    }
  }

  private static class CopperGolemStandingModel {
    private static final Quad[] body = new BoxModelBuilder()
      .addBlockUnitsBox(-4, 5, -3, 8, 6, 6, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 15).flipX()
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] head = new BoxModelBuilder()
      .addBlockUnitsBox(-4, 11, -5, 8, 5, 10, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-1, 10, -6, 2, 3, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(56, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-1, 16, -1, 2, 4, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 8).flipX()
          .addAllFaces()
          .grow(-0.01 / 16.)
      )
      .addBlockUnitsBox(-2, 20, -2, 4, 4, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 0).flipX()
          .addAllFaces()
          .grow(-0.01 / 16.)
      )
      .toQuads();
    private static final Quad[] rightArm = new BoxModelBuilder()
      .addBlockUnitsBox(4, 2, -2, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(36, 16).flipX()
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] leftArm = new BoxModelBuilder()
      .addBlockUnitsBox(-7, 2, -2, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(50, 16).flipX()
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] rightLeg = new BoxModelBuilder()
      .addBlockUnitsBox(-0.10000000000000009, 0, -1.99, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 27).flipX()
          .addAllFaces()
      )
      .toQuads();
    private static final Quad[] leftLeg = new BoxModelBuilder()
      .addBlockUnitsBox(-3.9, 0, -2, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(16, 27).flipX()
          .addAllFaces()
      )
      .toQuads();

    public static void getPrimitives(Collection<Primitive> faces, Material material, Transform transform) {
      for (Quad quad : head) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : body) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightLeg) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftLeg) {
        quad.addTriangles(faces, material, transform);
      }
    }
  }

  static class CopperGolemSittingModel {
    private static final Quad[] body = new BoxModelBuilder()
      .addBlockUnitsBox(-3, 1.1, 0.175, 6, 7.2, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64)
          .addBackFace(face -> face.set(6.5, 13.5, 21.1, 27).flipX())
          .addLeftFace(face -> face.set(1, 6.5, 21.1, 27).flipX())
          .addFrontFace(face -> face.set(20.1, 27.9, 21.1, 27.1).flipX())
          .addRightFace(face -> face.set(13.5, 19, 21.1, 27).flipX())
          .addTopFace(face -> face.set(7, 14, 15.2, 21).flipY())
          .addBottomFace(face -> face.set(13.8, 20.5, 15.2, 21))
      )
      .addBlockUnitsBox(-4.025, 0, -1.025, 8, 6, 6, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64)
          .addBackFace(face -> face.set(6.5, 13.5, 21.1, 27).flipX())
          .addLeftFace(face -> face.set(1, 6.5, 21.1, 27).flipX())
          .addFrontFace(face -> face.set(20.1, 27.9, 21.1, 27.1).flipX())
          .addRightFace(face -> face.set(13.5, 19, 21.1, 27).flipX())
          .addTopFace(face -> face.set(7, 14, 15.2, 21).flipY())
          .addBottomFace(face -> face.set(13.8, 20.5, 15.2, 21))
      )
      .addBlockUnitsBox(-4, 2.95, -3.825, 8, 7, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64)
          .addBackFace(face -> face.set(6.5, 13.5, 21.1, 27).flipX())
          .addLeftFace(face -> face.set(1, 6.5, 21.1, 27).flipX())
          .addFrontFace(face -> face.set(20.1, 27.9, 21.1, 27.1).flipX())
          .addRightFace(face -> face.set(13.5, 19, 21.1, 27).flipX())
          .addTopFace(face -> face.set(7, 14, 15.2, 21).flipY())
          .addBottomFace(face -> face.set(13.8, 20.5, 15.2, 21))
      )
      .toQuads();
    private static final Quad[] head = new BoxModelBuilder()
      .addBlockUnitsBox(-4.1, 7.3, -5, 8.2, 5, 10, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 0).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-2, 6.3, -6.8, 2, 3, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(56, 0).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(1 / 16., -7.8 / 16., 4.8 / 16.)
            .rotateY(Math.toRadians(-90))
            .translate(-1 / 16., 7.8 / 16., -4.8 / 16.)
          )
      )
      .addBlockUnitsBox(-1, 8.3, -3.2, 2, 3, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(54, -2).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(0 / 16., -9.8 / 16., 1.2 / 16.)
            .rotateY(Math.toRadians(-90))
            .translate(0 / 16., 9.8 / 16., -1.2 / 16.)
          )
      )
      .addBlockUnitsBox(-1, 12, -1, 2, 4, 2, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(37, 8).flipX()
          .addAllFaces()
      )
      .addBlockUnitsBox(-2, 16, -2, 4, 4, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64)
          .addBackFace(face -> face.set(41, 45, 4, 8).flipX())
          .addLeftFace(face -> face.set(37, 41, 4, 8).flipX())
          .addFrontFace(face -> face.set(49, 53, 4, 8).flipX())
          .addRightFace(face -> face.set(45, 49, 4, 8).flipX())
          .addTopFace(face -> face.set(41, 45, 0, 4).flipY())
          .addBottomFace(face -> face.set(45, 49, 0, 4))
          .grow(-0.01 / 16.)
      )
      .toQuads();
    private static final Quad[] rightArm = new BoxModelBuilder()
      .addBlockUnitsBox(4, 4.63211, -0.80571, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(36, 16).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(-4 / 16., -14 / 16., 3 / 16.)
            .rotateX(Math.toRadians(60))
            .translate(4 / 16., 14 / 16., -3 / 16.)
            .translate(-4 / 16., -14 / 16., -3.5 / 16.)
            .rotateX(Math.toRadians(-25))
            .translate(4 / 16., 14 / 16., 3.5 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] leftArm = new BoxModelBuilder()
      .addBlockUnitsBox(-7, 3.3041, -1.01759, 3, 10, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(50, 16).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(4 / 16., -13 / 16., 1 / 16.)
            .rotateX(Math.toRadians(60))
            .translate(-4 / 16., 13 / 16., -1 / 16.)
            .translate(4 / 16., -13 / 16., -5.5 / 16.)
            .rotateX(Math.toRadians(-25))
            .translate(-4 / 16., 13 / 16., 5.5 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] rightLeg = new BoxModelBuilder()
      .addBlockUnitsBox(0, -2.225, 0.2, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(0, 27).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(-2 / 16., -4 / 16., -0.2 / 16.)
            .rotateX(Math.toRadians(90))
            .translate(2 / 16., 4 / 16., 0.2 / 16.)
          )
      )
      .toQuads();
    private static final Quad[] leftLeg = new BoxModelBuilder()
      .addBlockUnitsBox(-4, -2.225, 0.2, 4, 5, 4, box ->
        box.forTextureSize(Texture.copperBlock, 64, 64).atUVCoordinates(16, 27).flipX()
          .addAllFaces()
          .transform(Transform.NONE
            .translate(2 / 16., -4 / 16., -0.2 / 16.)
            .rotateX(Math.toRadians(90))
            .translate(-2 / 16., 4 / 16., 0.2 / 16.)
          )
      )
      .toQuads();

    public static void getPrimitives(Collection<Primitive> faces, Material material, Transform transform) {
      for (Quad quad : head) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : body) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftArm) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : rightLeg) {
        quad.addTriangles(faces, material, transform);
      }
      for (Quad quad : leftLeg) {
        quad.addTriangles(faces, material, transform);
      }
    }
  }

  private final String pose;
  private final String facing;
  private final CopperGolemEntity.Oxidation oxidation;

  public CopperGolemStatueEntity(Vector3 position, String pose, String facing, CopperGolemEntity.Oxidation oxidation) {
    super(position);
    this.pose = pose;
    this.facing = facing;
    this.oxidation = oxidation;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> faces = new ArrayList<>();

    double rotateY = switch (facing) {
      case "east" -> Math.toRadians(270);
      case "south" -> Math.toRadians(180);
      case "west" -> Math.toRadians(90);
      default -> 0;
    };

    Material material = CopperGolemEntity.getMaterial(oxidation);

    Vector3 worldOffset = new Vector3(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Transform worldTransform = Transform.NONE
      .rotateY(rotateY)
      .translate(worldOffset);

    if (pose.equals("star")) {
      CopperGolemStarModel.getPrimitives(faces, material, worldTransform);
    } else if (pose.equals("running")) {
      CopperGolemRunningModel.getPrimitives(faces, material, worldTransform);
    } else if (pose.equals("sitting")) {
      CopperGolemSittingModel.getPrimitives(faces, material, worldTransform);
    } else if (pose.equals("standing")) {
      CopperGolemStandingModel.getPrimitives(faces, material, worldTransform);
    } else {
      Log.warn("Unknown copper golem statue pose: " + pose);
      CopperGolemStandingModel.getPrimitives(faces, material, worldTransform);
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "copperGolemStatue");
    json.add("position", position.toJson());
    json.add("pose", pose);
    json.add("oxidation", oxidation.name().toLowerCase());
    json.add("facing", facing);
    return json;
  }

  public static CopperGolemStatueEntity fromJson(JsonObject json) {
    return new CopperGolemStatueEntity(
      JsonUtil.vec3FromJsonObject(json.get("position")),
      json.get("pose").asString("standing"),
      json.get("facing").asString("north"),
      CopperGolemEntity.Oxidation.valueOf(json.get("oxidation").asString("NONE").toUpperCase())
    );
  }
}
