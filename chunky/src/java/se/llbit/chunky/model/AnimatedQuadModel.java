package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.util.MinecraftPRNG;

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
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = false;

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
        if (quad.closestIntersection(ray, intersectionRecord)) {
          if (ray.d.dot(quad.n) < 0) {
            float[] c = textures[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y, j);
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
        }
      }
    } else {
      for (int i = 0; i < quads.length; ++i) {
        Quad quad = quads[i];
        double distance = intersectionRecord.distance;
        if (quad.closestIntersection(ray, intersectionRecord)) {
          float[] c = textures[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y, j);
          if (c[3] > Constants.EPSILON) {
            tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
            color = c;
            if (quad.doubleSided) {
              intersectionRecord.setNormal(Vector3.orientNormal(ray.d, quad.n));
            } else {
              intersectionRecord.setNormal(quad.n);
            }
            hit = true;
          } else {
            intersectionRecord.distance = distance;
          }
        }
      }
    }

    if (hit) {
      intersectionRecord.color.set(color);
      tint.tint(intersectionRecord.color, ray, scene);
    }
    return hit;
  }
}
