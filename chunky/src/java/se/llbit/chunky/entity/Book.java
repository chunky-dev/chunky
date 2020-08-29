package se.llbit.chunky.entity;

import java.util.Collection;
import java.util.LinkedList;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

public class Book extends Entity implements Poseable {

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
          new Vector4(1 / 64.0, 6 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(3 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(6 / 64.0, 11 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(3 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(3 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(0 / 64.0, 1 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(6 / 64.0, 7 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(3 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(3 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(7 / 64.0, 12 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(3 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(1 / 64.0, 6 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
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
          new Vector4(13 / 64.0, 18 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(18 / 64.0, 23 / 64.0, 1 - 10 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(12 / 64.0, 13 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(18 / 64.0, 19 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(13 / 16.0, 12 / 16.0, 6.99 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 6.99 / 16.0),
          new Vector4(13 / 64.0, 18 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
      ),
      new Quad(
          new Vector3(13 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(8 / 16.0, 12 / 16.0, 7.99 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 7.99 / 16.0),
          new Vector4(19 / 64.0, 24 / 64.0, 1 - 19 / 32.00, 1 - 11 / 32.0)
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
  private final JsonObject pose;
  private double scale = 1;

  public Book(Vector3 position, double openAngle, double pageAngleA, double pageAngleB) {
    super(position);
    this.openAngle = openAngle;
    this.pageAngleA = Math.max(openAngle, pageAngleA);
    this.pageAngleB = Math.min(Math.PI - openAngle, pageAngleB);
    this.pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, 0, 0)));
  }

  public Book(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.openAngle = json.get("openAngle").doubleValue(0);
    this.pageAngleA = json.get("pageAngleA").doubleValue(0);
    this.pageAngleB = json.get("pageAngleB").doubleValue(0);
    this.scale = json.get("scale").asDouble(1);
    this.pose = json.get("pose").object();
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Vector3 allPose = JsonUtil.vec3FromJsonArray(this.pose.get("all"));
    return primitives(Transform.NONE
        .translate(-0.5, -0.5, -0.5)
        .scale(scale)
        .rotateX(allPose.x)
        .rotateY(allPose.y)
        .rotateZ(allPose.z)
        .translate(0.5, 0.5, 0.5)
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z));
  }

  public Collection<Primitive> primitives(Transform transform) {
    Collection<Primitive> faces = new LinkedList<>();

    double pageAngle = (Math.PI - openAngle) / 2;
    for (Quad quad : Model
        .translate(Model.rotateY(leftCover, -pageAngle),
            -1 / 16.0, 0, 0)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    for (Quad quad : Model
        .translate(Model.rotateY(rightCover, pageAngle),
            1 / 16.0, 0,
            0)) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book), transform);
    }

    double pagesDistance = (1 - Math.sin(Math.PI / 2 - pageAngle)) / 16.0;

    for (int i = 0; i < leftPages.length; i++) {
      if (i == 5 && openAngle < Ray.EPSILON) {
        continue; // the cover would overlay the pages if the book is closed
      }
      if (i == 4 && (pageAngleA >= (Math.PI + openAngle) / 2
          || pageAngleB >= (Math.PI + openAngle) / 2)) {
        continue; // the a single angle is clamped to the right pages, which would overlay this face
      }
      leftPages[i].addTriangles(faces, new TextureMaterial(Texture.book),
          Transform.NONE.translate(-0.5, -0.5, -0.5 + 1.01 / 16.0).rotateY(-pageAngle)
              .translate(0.5, 0.5, 0.5 - 1.01 / 16.0 + pagesDistance).chain(transform));
    }

    for (int i = 0; i < rightPages.length; i++) {
      if (i == 5 && openAngle < Ray.EPSILON) {
        continue; // the cover would overlay the pages if the book is closed
      }
      if (i == 4 && (pageAngleA <= (Math.PI - openAngle) / 2
          || pageAngleB <= (Math.PI - openAngle) / 2)) {
        continue; // the a single angle is clamped to the right pages, which would overlay this face
      }
      rightPages[i].addTriangles(faces, new TextureMaterial(Texture.book),
          Transform.NONE.translate(-0.5, -0.5, -0.5 + 1.01 / 16.0).rotateY(pageAngle)
              .translate(0.5, 0.5, 0.5 - 1.01 / 16.0 + pagesDistance).chain(transform));
    }

    double clampedPageAngleA = Math
        .min((Math.PI + openAngle) / 2, Math.max((Math.PI - openAngle) / 2, pageAngleA));
    for (Quad quad : pageA) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book),
          Transform.NONE.translate(-0.5, -0.5, -0.5 + 1.01 / 16.0).rotateY(clampedPageAngleA)
              .translate(0.5, 0.5, 0.5 - 1.01 / 16.0 + pagesDistance).chain(transform));
    }

    double clampedPageAngleB = Math
        .min((Math.PI + openAngle) / 2, Math.max((Math.PI - openAngle) / 2, pageAngleB));
    for (Quad quad : pageB) {
      quad.addTriangles(faces, new TextureMaterial(Texture.book),
          Transform.NONE.translate(-0.5, -0.5, -0.5 + 1.01 / 16.0).rotateY(clampedPageAngleB)
              .translate(0.5, 0.5, 0.5 - 1.01 / 16.0 + pagesDistance).chain(transform));
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
    json.add("scale", getScale());
    json.add("pose", pose);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new Book(json);
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
    this.scale = value;
  }

  public void setPitch(double pitch) {
    pose.get("all").array().set(0, Json.of(pitch));
  }

  public void setYaw(double yaw) {
    pose.get("all").array().set(1, Json.of(yaw));
  }

  @Override
  public JsonObject getPose() {
    return pose;
  }

  @Override
  public boolean hasHead() {
    return false;
  }

  public double getOpenAngle() {
    return openAngle;
  }

  public void setOpenAngle(double openAngle) {
    this.openAngle = openAngle;
  }

  public double getPageAngleA() {
    return pageAngleA;
  }

  public void setPageAngleA(double pageAngleA) {
    this.pageAngleA = pageAngleA;
  }

  public double getPageAngleB() {
    return pageAngleB;
  }

  public void setPageAngleB(double pageAngleB) {
    this.pageAngleB = pageAngleB;
  }
}
