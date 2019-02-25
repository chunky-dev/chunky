package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class Log extends MinecraftBlock {
  private static final Quad[] sides = {
      // north
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(1, 0, 0, 1)),

      // south
      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // west
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // east
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // top
      new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
  };

  private static final int[][] textureIndex =
      {{0, 0, 0, 0, 1, 1}, {0, 0, 1, 1, 0, 0}, {1, 1, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}};

  private static final int[][] uv =
      {{0, 0, 0, 0, 0, 0}, {1, 1, 0, 0, 1, 1}, {0, 0, 1, 1, 0, 0}, {0, 0, 0, 0, 0, 0},};

  private final Texture[] texture;
  private int direction;

  public Log(String name, Texture[] texture, String axis) {
    super(String.format("%s (axis=%s)", name, axis), texture[0]);
    this.texture = texture;
    localIntersect = true;
    switch (axis) {
      case "y":
        this.direction = 0;
        break;
      case "x":
        this.direction = 1;
        break;
      case "z":
        this.direction = 2;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < sides.length; ++i) {
      Quad side = sides[i];
      if (side.intersect(ray)) {
        double u = ray.u;
        int uv_x = uv[direction][i];
        ray.u = (1 - uv_x) * ray.u + uv_x * ray.v;
        ray.v = uv_x * u + (1 - uv_x) * ray.v;
        texture[textureIndex[direction][i]].getColor(ray);
        ray.n.set(side.n);
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
