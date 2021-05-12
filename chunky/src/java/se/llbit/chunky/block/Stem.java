package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Melon or pumpkin stem.
 */
public class Stem extends MinecraftBlockTranslucent {
  private static final Quad[][] growth = new Quad[8][2];
  public static final double[][] stemColor = {
      {0, 0xE2 / 255., 0x10 / 255.},
      {0, 0xE2 / 255., 0x10 / 255.},
      {0, 0xE2 / 255., 0x10 / 255.},
      {0, 0xCC / 255., 0x06 / 255.},
      {0x5F / 255., 0xC8 / 255., 0x03 / 255.},
      {0x65 / 255., 0xC2 / 255., 0x06 / 255.},
      {0xA0 / 255., 0xB8 / 255., 0},
      {0xBF / 255., 0xB6 / 255., 0},
  };

  static {
    for (int height = 0; height < 8; ++height) {
      growth[height][0] = new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 1),
          new Vector3(0, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1));
      growth[height][1] = new DoubleSidedQuad(new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector3(1, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1));
    }
  }

  private final int age;

  public Stem(String name, int age) {
    super(name, Texture.stemStraight);
    localIntersect = true;
    this.age = age & 7;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : growth[age]) {
      if (quad.intersect(ray)) {
        float[] color = Texture.stemStraight.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.color.x *= stemColor[age][0];
          ray.color.y *= stemColor[age][1];
          ray.color.z *= stemColor[age][2];
          ray.t = ray.tNext;
          Vector3 n = new Vector3(quad.n);
          n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
          ray.setN(n);
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

  @Override public String description() {
    return "age=" + age;
  }
}
