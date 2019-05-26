package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TurtleEgg extends MinecraftBlockTranslucent {
  private static final Quad[] quads = {
      // cube1
      new Quad(
          new Vector3(5 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector3(10 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 7 / 16.0, 4 / 16.0),
          new Vector4(0, 4 / 16.0, 12 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 4 / 16.0),
          new Vector3(10 / 16.0, 0, 4 / 16.0),
          new Vector3(5 / 16.0, 0, 9 / 16.0),
          new Vector4(0, 4 / 16.0, 12 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 0, 4 / 16.0),
          new Vector3(10 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 4 / 16.0),
          new Vector3(5 / 16.0, 0, 9 / 16.0),
          new Vector3(5 / 16.0, 7 / 16.0, 4 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 4 / 16.0),
          new Vector3(5 / 16.0, 0, 4 / 16.0),
          new Vector3(10 / 16.0, 7 / 16.0, 4 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(5 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
  };

  public TurtleEgg() {
    super("turtle_egg", Texture.turtleEgg);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        texture.getColor(ray);
        ray.t = ray.tNext;
        hit = true;
      }
    }
    if (hit) {
      ray.color.w = 1;
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
