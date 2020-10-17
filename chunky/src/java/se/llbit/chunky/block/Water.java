package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Triangle;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.List;

public class Water extends MinecraftBlockTranslucent {

  public static final Water INSTANCE = new Water(0);
  
  // Used only as starting material when camera is submerged.
  public static final Water OCEAN_WATER = new Water(0, 1 << Water.FULL_BLOCK);

  public final int level;
  public final int data;

  public Water(int level, int data) {
    super("water", Texture.water);
    this.level = level % 8;
    this.data = data;
    solid = false;
    localIntersect = true;
    specular = 0.12f;
    ior = 1.333f;
    refractive = true;
  }

  public Water(int level) {
    this(level, 0);
  }

  public boolean isFullBlock() {
    return (this.data & (1 << FULL_BLOCK)) != 0;
  }

  @Override public boolean isWater() {
    return true;
  }

  @Override public boolean isSameMaterial(Material other) {
    return other.isWater();
  }

  private static final Quad[] fullBlock = {
      // Bottom.
      new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
      // Top.
      new DoubleSidedQuad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),
      // West.
      new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
      // East.
      new DoubleSidedQuad(new Vector3(1, 0, 0), new Vector3(1, 1, 0), new Vector3(1, 0, 1),
          new Vector4(0, 1, 0, 1)),
      // North.
      new DoubleSidedQuad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 0, 0),
          new Vector4(0, 1, 0, 0)),
      // South.
      new DoubleSidedQuad(new Vector3(0, 1, 1), new Vector3(1, 1, 1), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
  };

  static final DoubleSidedQuad bot =
      new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1));
  static final Triangle[][][] t012 = new Triangle[8][8][8];
  static final Triangle[][][] t230 = new Triangle[8][8][8];
  static final Triangle[][] westt = new Triangle[8][8];
  static final Triangle[] westb = new Triangle[8];
  static final Triangle[][] northt = new Triangle[8][8];
  static final Triangle[] northb = new Triangle[8];
  static final Triangle[][] eastt = new Triangle[8][8];
  static final Triangle[] eastb = new Triangle[8];
  static final Triangle[][] southt = new Triangle[8][8];
  static final Triangle[] southb = new Triangle[8];

  /** Water height levels. */
  static final double height[] = {
      14 / 16., 12.25 / 16., 10.5 / 16, 8.75 / 16, 7. / 16, 5.25 / 16, 3.5 / 16, 1.75 / 16
  };

  /**
   * Block data offset for water above flag.
   */
  public static final int FULL_BLOCK = 16;
  public static final int CORNER_0 = 0;
  public static final int CORNER_1 = 4;
  public static final int CORNER_2 = 8;
  public static final int CORNER_3 = 12;

  static {
    // Precompute water triangles.
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c1 = height[j];
        for (int k = 0; k < 8; ++k) {
          double c2 = height[k];
          t012[i][j][k] =
              new Triangle(new Vector3(1, c1, 1), new Vector3(1, c2, 0), new Vector3(0, c0, 1));
        }
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c2 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c3 = height[j];
        for (int k = 0; k < 8; ++k) {
          double c0 = height[k];
          t230[i][j][k] =
              new Triangle(new Vector3(0, c3, 0), new Vector3(0, c0, 1), new Vector3(1, c2, 0));
        }
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c3 = height[j];
        westt[i][j] =
            new Triangle(new Vector3(0, c3, 0), new Vector3(0, 0, 0), new Vector3(0, c0, 1));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      westb[i] = new Triangle(new Vector3(0, 0, 1), new Vector3(0, c0, 1), new Vector3(0, 0, 0));
    }
    for (int i = 0; i < 8; ++i) {
      double c1 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c2 = height[j];
        eastt[i][j] =
            new Triangle(new Vector3(1, c2, 0), new Vector3(1, c1, 1), new Vector3(1, 0, 0));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c1 = height[i];
      eastb[i] = new Triangle(new Vector3(1, c1, 1), new Vector3(1, 0, 1), new Vector3(1, 0, 0));
    }
    for (int i = 0; i < 8; ++i) {
      double c2 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c3 = height[j];
        northt[i][j] =
            new Triangle(new Vector3(0, c3, 0), new Vector3(1, c2, 0), new Vector3(0, 0, 0));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c2 = height[i];
      northb[i] =
          new Triangle(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, c2, 0));
    }
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c1 = height[j];
        southt[i][j] =
            new Triangle(new Vector3(0, c0, 1), new Vector3(0, 0, 1), new Vector3(1, c1, 1));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c1 = height[i];
      southb[i] =
          new Triangle(new Vector3(1, 0, 1), new Vector3(1, c1, 1), new Vector3(0, 0, 1));
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;

    int data = ray.getCurrentData();
    int isFull = (data >> FULL_BLOCK) & 1;

    if (isFull != 0) {
      boolean hit = false;
      for (Quad quad : fullBlock) {
        if (quad.intersect(ray)) {
          texture.getAvgColorLinear(ray.color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
          hit = true;
        }
      }
      if (hit) {
        ray.distance += ray.t;
        ray.o.scaleAdd(ray.t, ray.d);
      }
      return hit;
    }

    boolean hit = false;
    if (bot.intersect(ray)) {
      ray.n.set(bot.n);
      ray.n.scale(-QuickMath.signum(ray.d.dot(bot.n)));
      ray.t = ray.tNext;
      hit = true;
    }

    int c0 = (0xF & (data >> CORNER_0)) % 8;
    int c1 = (0xF & (data >> CORNER_1)) % 8;
    int c2 = (0xF & (data >> CORNER_2)) % 8;
    int c3 = (0xF & (data >> CORNER_3)) % 8;
    Triangle triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = westt[c0][c3];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = westb[c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = eastt[c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = eastb[c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = southt[c0][c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = southb[c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = northt[c2][c3];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = northb[c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    if (hit) {
      texture.getAvgColorLinear(ray.color);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  @Override public String description() {
    return String.format("level=%d", level);
  }
}
