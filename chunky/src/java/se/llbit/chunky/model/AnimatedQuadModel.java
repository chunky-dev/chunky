package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.util.MinecraftPRNG;
import se.llbit.util.VectorUtil;

public abstract class AnimatedQuadModel extends QuadModel {
  public final static class AnimationMode {
    public final int framerate;
    public final boolean positional;

    public AnimationMode(int framerate, boolean positional) {
      this.framerate = framerate;
      this.positional = positional;
    }
  }

  protected final AnimationMode animationMode;

  public AnimatedQuadModel(int framerate, boolean positional) {
    this.animationMode = new AnimationMode(framerate, positional);
  }

  @PluginApi
  public AnimationMode getAnimationMode() {
    return animationMode;
  }

  @PluginApi
  @Override
  public abstract AnimatedTexture[] getTextures();

  @Override
  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = false;
    IntersectionRecord intersectionTest = new IntersectionRecord();

    Quad[] quads = getQuads();
    AnimatedTexture[] textures = getTextures();
    Tint[] tintedQuads = getTints();

    // The animation frame to use
    int j = (int) (scene.getAnimationTime() * animationMode.framerate);
    if (animationMode.positional) {
      Vector3 position = new Vector3(ray.o);
      position.scaleAdd(Constants.OFFSET, ray.d);

      j += (int) MinecraftPRNG.rand((long) position.x, (long) position.y, (long) position.z);
    }

    float[] color = null;
    Tint tint = Tint.NONE;
    if (refractive) {
      for (int i = 0; i < quads.length; ++i) {
        Quad quad = quads[i];
        if (quad.intersect(ray, intersectionTest)) {
          if (ray.d.dot(quad.n) < 0) {
            float[] c = textures[i].getColor(intersectionTest.uv.x, intersectionTest.uv.y);
            if (c[3] > Constants.EPSILON) {
              tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
              color = c;
            } else {
              tint = Tint.NONE;
              color = new float[] {1, 1, 1, 0};
            }
          } else {
            tint = Tint.NONE;
            color = new float[] {1, 1, 1, 0};
          }
          hit = true;
          intersectionRecord.setNormal(quad.n);
          intersectionRecord.distance = intersectionTest.distance;
        }
      }
    } else {
      for (int i = 0; i < quads.length; ++i) {
        Quad quad = quads[i];
        double distance = intersectionTest.distance;
        if (quad.intersect(ray, intersectionTest)) {
          float[] c = textures[i].getColor(intersectionTest.uv.x, intersectionTest.uv.y);
          if (c[3] > Constants.EPSILON) {
            tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
            color = c;
            if (quad.doubleSided) {
              intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, quad.n));
            } else {
              intersectionRecord.setNormal(quad.n);
            }
            intersectionRecord.distance = intersectionTest.distance;
            hit = true;
          } else {
            intersectionTest.distance = distance;
          }
        }
      }
    }

    if (hit) {
      double px = ray.o.x - Math.floor(ray.o.x + ray.d.x * Constants.OFFSET) + ray.d.x * intersectionTest.distance;
      double py = ray.o.y - Math.floor(ray.o.y + ray.d.y * Constants.OFFSET) + ray.d.y * intersectionTest.distance;
      double pz = ray.o.z - Math.floor(ray.o.z + ray.d.z * Constants.OFFSET) + ray.d.z * intersectionTest.distance;
      if (px < E0 || px > E1 || py < E0 || py > E1 || pz < E0 || pz > E1) {
        // TODO this check is only really needed for wall torches
        return false;
      }

      intersectionRecord.color.set(color);
      tint.tint(intersectionRecord.color, ray, scene);
      /*ray.o.scaleAdd(ray.t, ray.d);
      int x;
       */
    }
    return hit;
  }
}
