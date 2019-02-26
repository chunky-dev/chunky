package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Triangle;

import static se.llbit.chunky.block.Water.CORNER_0;
import static se.llbit.chunky.block.Water.CORNER_1;
import static se.llbit.chunky.block.Water.CORNER_2;
import static se.llbit.chunky.block.Water.CORNER_3;
import static se.llbit.chunky.block.Water.FULL_BLOCK;

public class Lava extends MinecraftBlock {
  public final int level;

  public Lava(int level) {
    super("lava", Texture.lava);
    this.level = level;
    solid = false;
    localIntersect = true;
    emittance = 1.0f;
  }

  private static AABB fullBlock = new AABB(0, 1, 0, 1, 0, 1);

  @Override public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;

    int data = ray.getCurrentData();
    int isFull = (data >> FULL_BLOCK) & 1;

    if (isFull != 0) {
      if (fullBlock.intersect(ray)) {
        texture.getColor(ray);
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
      return false;
    }

    int c0 = (0xF & (data >> CORNER_0)) % 8;
    int c1 = (0xF & (data >> CORNER_1)) % 8;
    int c2 = (0xF & (data >> CORNER_2)) % 8;
    int c3 = (0xF & (data >> CORNER_3)) % 8;
    Triangle triangle = Water.t012[c0][c1][c2];
    boolean hit = false;
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
}
