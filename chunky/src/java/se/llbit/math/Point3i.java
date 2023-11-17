package se.llbit.math;

import se.llbit.json.JsonObject;

public class Point3i {
  public int x;
  public int y;
  public int z;

  public Point3i(Point3i point) {
    this.x = point.x;
    this.y = point.y;
    this.z = point.z;
  }

  public Point3i(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point3i(int a) {
    this.x = a;
    this.y = a;
    this.z = a;
  }

  /**
   * Creates a new point at (0, 0, 0).
   */
  public Point3i() {
    this.x = 0;
    this.y = 0;
    this.z = 0;
  }

  public void add(Point3i point) {
    this.x += point.x;
    this.y += point.y;
    this.z += point.z;
  }

  public void add(Vector3i vector) {
    this.x += vector.x;
    this.y += vector.y;
    this.z += vector.z;
  }

  public void sub(Point3i point) {
    this.x -= point.x;
    this.y -= point.y;
    this.z -= point.z;
  }

  public void set(Point3i point) {
    this.x = point.x;
    this.y = point.y;
    this.z = point.z;
  }

  public void set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Unmarshals a point from JSON.
   */
  public void fromJson(JsonObject object) {
    x = object.get("x").intValue(0);
    y = object.get("y").intValue(0);
    z = object.get("z").intValue(0);
  }

  /**
   * Serialize to JSON
   *
   * @return JSON object
   */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.add("x", x);
    object.add("y", y);
    object.add("z", z);
    return object;
  }
}
