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
 * Attached melon or pumpkin stem.
 */
public class AttachedStem extends MinecraftBlockTranslucent {
  private static final Quad[] growth = new Quad[2];
  private static final Quad[] ripe = {
      new DoubleSidedQuad(new Vector3(0, 0, .5), new Vector3(1, 0, .5), new Vector3(0, 1, .5),
          new Vector4(0, 1, 0, 1)),
      new DoubleSidedQuad(new Vector3(0, 0, .5), new Vector3(1, 0, .5),
          new Vector3(0, 1, .5), new Vector4(1, 0, 0, 1)),
      new DoubleSidedQuad(new Vector3(.5, 0, 0), new Vector3(.5, 0, 1),
          new Vector3(.5, 1, 0), new Vector4(0, 1, 0, 1)),
      new DoubleSidedQuad(new Vector3(.5, 0, 0), new Vector3(.5, 0, 1),
          new Vector3(.5, 1, 0), new Vector4(1, 0, 0, 1)),
  };

  static {
    int height = 3;
    growth[0] = new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 1),
        new Vector3(0, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1));
    growth[1] = new DoubleSidedQuad(new Vector3(1, 0, 0), new Vector3(0, 0, 1),
        new Vector3(1, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1));
  }

  private final int facing;
  private final String description;

  public AttachedStem(String name, String facing) {
    super(name, Texture.stemBent);
    description = "facing=" + facing;
    localIntersect = true;
    switch (facing) {
      default:
      case "north":
        this.facing = 2; //0;
        break;
      case "south":
        this.facing = 3; //1;
        break;
      case "east":
        this.facing = 1; //2;
        break;
      case "west":
        this.facing = 0; //3;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : growth) {
      if (quad.intersect(ray)) {
        float[] color = Texture.stemStraight.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.color.x *= Stem.stemColor[7][0];
          ray.color.y *= Stem.stemColor[7][1];
          ray.color.z *= Stem.stemColor[7][2];
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          ray.n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
          hit = true;
        }
      }
    }
    Quad quad = ripe[facing];
    if (quad.intersect(ray)) {
      float[] color = Texture.stemBent.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.color.x *= Stem.stemColor[7][0];
        ray.color.y *= Stem.stemColor[7][1];
        ray.color.z *= Stem.stemColor[7][2];
        ray.t = ray.tNext;
        ray.n.set(quad.n);
        ray.n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
        hit = true;
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  @Override public String description() {
    return description;
  }
}
