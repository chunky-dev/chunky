package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class Conduit extends MinecraftBlockTranslucent {
  private static final Quad[] conduit = {
      // cube1
      new Quad(
          new Vector3(5 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 11 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 14 / 16.0, 8 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector4(2 / 16.0, 8 / 16.0, 8 / 16.0, 14 / 16.0)),
      new Quad(
          new Vector3(11 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector4(14 / 16.0, 8 / 16.0, 8 / 16.0, 2 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 11 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 8 / 16.0, 14 / 16.0)),
      new Quad(
          new Vector3(11 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 11 / 16.0, 5 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 8 / 16.0, 14 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 11 / 16.0, 11 / 16.0),
          new Vector4(8 / 16.0, 14 / 16.0, 8 / 16.0, 14 / 16.0)),
  };

  public Conduit() {
    super("conduit", Texture.conduit);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : conduit) {
      if (quad.intersect(ray)) {
        float[] color = texture.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.setNormal(quad.n);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
