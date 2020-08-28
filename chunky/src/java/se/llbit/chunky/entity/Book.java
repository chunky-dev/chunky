package se.llbit.chunky.entity;

import java.util.Collection;
import java.util.LinkedList;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

public class Book extends Entity {

  private static final Quad[] leftCover = new Quad[]{
      // left cover
      new Quad(
          new Vector3(2 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(2 / 16.0, 3 / 16.0, 8 / 16.0),
          new Vector4(0, 6 / 64.0, 1, 1 - 10 / 32.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
          new Vector4(6 / 64.0, 12 / 64.0, 1, 1 - 10 / 32.0)
      ),
  };

  private static final Quad[] leftPages = new Quad[]{
      // left pages
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector4(1 / 64.0, 6 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(3 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(6 / 64.0, 11 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(3 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(3 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(0 / 64.0, 1 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(6 / 64.0, 7 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(3 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(7 / 64.0, 12 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(3 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(1 / 64.0, 6 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      )
  };

  private static final Quad[] middleCover = new Quad[]{
      new Quad(
          new Vector3(7 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 3 / 16.0, 8 / 16.0),
          new Vector4(14 / 64.0, 16 / 64.0, 1, 1 - 10 / 32.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 3 / 16.0, 8 / 16.0),
          new Vector4(12 / 64.0, 14 / 64.0, 1, 1 - 10 / 32.0)
      ),
  };

  private static final Quad[] rightCover = new Quad[]{
      // right cover
      new Quad(
          new Vector3(8 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
          new Vector4(16 / 64.0, 22 / 64.0, 1, 1 - 10 / 32.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 13 / 16.0, 8 / 16.0),
          new Vector3(14 / 16.0, 3 / 16.0, 8 / 16.0),
          new Vector4(22 / 64.0, 28 / 64.0, 1, 1 - 10 / 32.0)
      ),
  };

  private static final Quad[] rightPages = new Quad[]{
      // right pages
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector4(13 / 64.0, 18 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(18 / 64.0, 23 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(12 / 64.0, 13 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(18 / 64.0, 19 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(13 / 64.0, 18 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(19 / 64.0, 24 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0) //ok
      ),
  };

  private static final Quad[] pageA = new Quad[]{
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(24 / 64.0, 29 / 64.0, 1 - 18 / 32.0, 1 - 10 / 32.0)
      ),
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(34 / 64.0, 29 / 64.0, 1 - 18 / 32.0, 1 - 10 / 32.0)
      ),
  };

  private static final Quad[] pageB = new Quad[]{
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(29 / 64.0, 24 / 64.0, 1 - 18 / 32.0, 1 - 10 / 32.0)
      ),
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(29 / 64.0, 34 / 64.0, 1 - 18 / 32.0, 1 - 10 / 32.0)
      ),
  };

  private double openAngle;
  private double pageAngleA;
  private double pageAngleB;
  private double pitch;
  private double yaw;

  public Book(Vector3 position, double openAngle, double pageAngleA, double pageAngleB) {
    super(position);
    this.openAngle = openAngle;
    this.pageAngleA = Math.max(openAngle, pageAngleA);
    this.pageAngleB = Math.min(Math.PI - openAngle, pageAngleB);
  }

  public Book(JsonObject json) {
    this(JsonUtil.vec3FromJsonObject(json.get("position")),
        json.get("openAngle").doubleValue(0),
        json.get("pageAngleA").doubleValue(0),
        json.get("pageAngleB").doubleValue(0));
    setPitch(json.get("pitch").asDouble(0));
    setYaw(json.get("yaw").asDouble(0));
  }

  public double getPitch() {
    return pitch;
  }

  public void setPitch(double pitch) {
    this.pitch = pitch;
  }

  public double getYaw() {
    return yaw;
  }

  public void setYaw(double yaw) {
    this.yaw = yaw;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    return primitives(Transform.NONE
        .translate(-0.5, -0.5, -0.5)
        .rotateX(getPitch())
        .translate(0, 0, 0)
        .rotateY(getYaw())
        .translate(0.5, 0.5, 0.5)
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z));
  }

  public Collection<Primitive> primitives(Transform transform) {
    Collection<Primitive> faces = new LinkedList<>();

    for (Quad quad : Model
        .translate(Model.rotateY(leftCover, -openAngle),
            -1 / 16.0, 0, 0)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    for (Quad quad : Model
        .translate(Model.rotateY(rightCover, openAngle),
            1 / 16.0, 0,
            0)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    double pagesDistance = 0.01 + ((1 - openAngle * 2 / Math.PI)) / 16.0; // TODO

    for (Quad quad : Model
        .translate(Model.rotateY(Model.translate(leftPages, 0, 0, pagesDistance), -openAngle),
            0, 0, -pagesDistance)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    for (Quad quad : Model
        .translate(Model.rotateY(Model.translate(rightPages, 0, 0, pagesDistance), openAngle),
            0, 0, -pagesDistance)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    for (Quad quad : Model
        .translate(Model.rotateY(Model.translate(pageA, -0.5, 0, -pagesDistance), pageAngleA),
            0.5, 0, pagesDistance)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    for (Quad quad : Model
        .translate(Model.rotateY(Model.translate(pageB, 0.5, 0, -pagesDistance), pageAngleB),
            -0.5, 0, pagesDistance)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    for (Quad quad : middleCover) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "book");
    json.add("position", position.toJson());
    json.add("openAngle", openAngle);
    json.add("pageAngleA", pageAngleA);
    json.add("pageAngleB", pageAngleB);
    json.add("pitch", getPitch());
    json.add("yaw", getYaw());
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new Book(json);
  }
}
