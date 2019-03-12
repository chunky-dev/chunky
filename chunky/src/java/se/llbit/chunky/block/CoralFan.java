package se.llbit.chunky.block;

import se.llbit.chunky.entity.CoralFanEntity;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CoralFan extends MinecraftBlockTranslucent {

  private static final Quad[] quads = {
      // cube1
      new Quad(
          new Vector3(8 / 16.0, 0, 0),
          new Vector3(8 / 16.0, 0, 16 / 16.0),
          new Vector3(24 / 16.0, 0, 0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
      new Quad(
          new Vector3(24 / 16.0, 0, 0),
          new Vector3(24 / 16.0, 0, 16 / 16.0),
          new Vector3(8 / 16.0, 0, 0),
          new Vector4(0, 16 / 16.0, 16 / 16.0, 0)),
      // cube2
      new Quad(
          new Vector3(8 / 16.0, 0, 16 / 16.0),
          new Vector3(8 / 16.0, 0, 0),
          new Vector3(-8 / 16.0, 0, 16 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
      new Quad(
          new Vector3(-8 / 16.0, 0, 16 / 16.0),
          new Vector3(-8 / 16.0, 0, 0),
          new Vector3(8 / 16.0, 0, 16 / 16.0),
          new Vector4(0, 16 / 16.0, 16 / 16.0, 0)),
      // cube3
      new Quad(
          new Vector3(0, 0, 24 / 16.0),
          new Vector3(16 / 16.0, 0, 24 / 16.0),
          new Vector3(0, 0, 8 / 16.0),
          new Vector4(16 / 16.0, 0, 16 / 16.0, 0)),
      new Quad(
          new Vector3(0, 0, 8 / 16.0),
          new Vector3(16 / 16.0, 0, 8 / 16.0),
          new Vector3(0, 0, 24 / 16.0),
          new Vector4(16 / 16.0, 0, 0, 16 / 16.0)),
      // cube4
      new Quad(
          new Vector3(0, 0, 8 / 16.0),
          new Vector3(16 / 16.0, 0, 8 / 16.0),
          new Vector3(0, 0, -8 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
      new Quad(
          new Vector3(0, 0, -8 / 16.0),
          new Vector3(16 / 16.0, 0, -8 / 16.0),
          new Vector3(0, 0, 8 / 16.0),
          new Vector4(0, 16 / 16.0, 16 / 16.0, 0)),
  };

  public CoralFan(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    invisible = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = texture.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    //return hit;
    return false;
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity toEntity(Vector3 position) {
    return new CoralFanEntity(position);
  }
}
