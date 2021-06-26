package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Triangle;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import static se.llbit.chunky.block.Water.CORNER_0;
import static se.llbit.chunky.block.Water.CORNER_1;
import static se.llbit.chunky.block.Water.CORNER_2;
import static se.llbit.chunky.block.Water.CORNER_3;
import static se.llbit.chunky.block.Water.FULL_BLOCK;

public class Lava extends MinecraftBlockTranslucent {
  private static final AABB fullBlock = new AABB(0, 1, 0, 1, 0, 1);

  private static final Quad bottom =
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1));

  public final int level;
  public final int data;

  public Lava(int level, int data) {
    super("lava", Texture.lava);
    this.level = level % 8;
    this.data = data;
    solid = false;
    localIntersect = true;
    emittance = 1.0f;
  }

  public Lava(int level) {
    this(level, 1 << FULL_BLOCK);
  }

  public boolean isFullBlock() {
    return (this.data & (1 << FULL_BLOCK)) != 0;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;

    if (isFullBlock()) {
      if (fullBlock.intersect(ray)) {
        texture.getColor(ray);
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
      return false;
    }

    boolean hit = false;
    if (bottom.intersect(ray)) {
      ray.n.set(bottom.n);
      ray.n.scale(-QuickMath.signum(ray.d.dot(bottom.n)));
      ray.t = ray.tNext;
      hit = true;
    }

    int c0 = (0xF & (data >> CORNER_0)) % 8;
    int c1 = (0xF & (data >> CORNER_1)) % 8;
    int c2 = (0xF & (data >> CORNER_2)) % 8;
    int c3 = (0xF & (data >> CORNER_3)) % 8;
    Triangle triangle = Water.t012[c0][c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = Water.t230[c2][c3][c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = Water.westt[c0][c3];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double y = ray.t * ray.d.y + ray.o.y;
      double z = ray.t * ray.d.z + ray.o.z;
      y -= QuickMath.floor(y);
      z -= QuickMath.floor(z);
      ray.u = z;
      ray.v = y;
      hit = true;
    }
    triangle = Water.westb[c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double y = ray.t * ray.d.y + ray.o.y;
      double z = ray.t * ray.d.z + ray.o.z;
      y -= QuickMath.floor(y);
      z -= QuickMath.floor(z);
      ray.u = z;
      ray.v = y;
      hit = true;
    }
    triangle = Water.eastt[c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double y = ray.t * ray.d.y + ray.o.y;
      double z = ray.t * ray.d.z + ray.o.z;
      y -= QuickMath.floor(y);
      z -= QuickMath.floor(z);
      ray.u = z;
      ray.v = y;
      hit = true;
    }
    triangle = Water.eastb[c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double y = ray.t * ray.d.y + ray.o.y;
      double z = ray.t * ray.d.z + ray.o.z;
      y -= QuickMath.floor(y);
      z -= QuickMath.floor(z);
      ray.u = z;
      ray.v = y;
      hit = true;
    }
    triangle = Water.southt[c0][c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double x = ray.t * ray.d.x + ray.o.x;
      double y = ray.t * ray.d.y + ray.o.y;
      x -= QuickMath.floor(x);
      y -= QuickMath.floor(y);
      ray.u = x;
      ray.v = y;
      hit = true;
    }
    triangle = Water.southb[c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double x = ray.t * ray.d.x + ray.o.x;
      double y = ray.t * ray.d.y + ray.o.y;
      x -= QuickMath.floor(x);
      y -= QuickMath.floor(y);
      ray.u = x;
      ray.v = y;
      hit = true;
    }
    triangle = Water.northt[c2][c3];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double x = ray.t * ray.d.x + ray.o.x;
      double y = ray.t * ray.d.y + ray.o.y;
      x -= QuickMath.floor(x);
      y -= QuickMath.floor(y);
      ray.u = 1 - x;
      ray.v = y;
      hit = true;
    }
    triangle = Water.northb[c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      double x = ray.t * ray.d.x + ray.o.x;
      double y = ray.t * ray.d.y + ray.o.y;
      x -= QuickMath.floor(x);
      y -= QuickMath.floor(y);
      ray.u = 1 - x;
      ray.v = y;
      hit = true;
    }
    if (hit) {
      texture.getColor(ray);
      ray.color.w = 1;
      ray.distance += ray.tNext;
      ray.o.scaleAdd(ray.tNext, ray.d);
      return true;
    }
    return false;
  }

  @Override public String description() {
    return "level=" + level;
  }
}
