package se.llbit.math;

import se.llbit.json.JsonObject;

public class Point3 extends Tuple3 {

  public Point3(Point3 point) {
    this.x = point.x;
    this.y = point.y;
    this.z = point.z;
  }

  public Point3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point3(double a) {
    this.x = a;
    this.y = a;
    this.z = a;
  }

  /**
   * Creates a new point at (0, 0, 0).
   */
  public Point3() {
    this.x = 0d;
    this.y = 0d;
    this.z = 0d;
  }

  public void add(Tuple3 vector) {
    this.x += vector.x;
    this.y += vector.y;
    this.z += vector.z;
  }

  public void scaleAdd(double scalar, Vector3 vector) {
    this.x += vector.x * scalar;
    this.y += vector.y * scalar;
    this.z += vector.z * scalar;
  }

  public void add(double x, double y, double z) {
    this.x += x;
    this.y += y;
    this.z += z;
  }

  public void sub(Point3 point) {
    this.x -= point.x;
    this.y -= point.y;
    this.z -= point.z;
  }

  public Vector3 vSub(Point3 point) {
    return new Vector3(x - point.x, y - point.y, z - point.z);
  }

  public Vector3 asVector() {
    return new Vector3(x, y, z);
  }

  public void set(Point3 point) {
    this.x = point.x;
    this.y = point.y;
    this.z = point.z;
  }

  public void set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Unmarshals a point from JSON.
   */
  public void fromJson(JsonObject object) {
    x = object.get("x").doubleValue(0);
    y = object.get("y").doubleValue(0);
    z = object.get("z").doubleValue(0);
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
