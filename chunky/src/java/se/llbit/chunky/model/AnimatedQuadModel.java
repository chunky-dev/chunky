package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
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
  public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    Quad[] quads = getQuads();
    AnimatedTexture[] textures = getTextures();
    Tint[] tintedQuads = getTints();

    // The animation frame to use
    int j = (int) (scene.getAnimationTime() * animationMode.framerate);
    if (animationMode.positional) {
      Vector3 position = new Vector3(ray.o);
      position.scaleAdd(Ray.OFFSET, ray.d);

      j += (int) MinecraftPRNG.rand((long) position.x, (long) position.y, (long) position.z);
    }

    float[] color = null;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] c = textures[i].getColor(ray.u, ray.v, j);
        if (c[3] > Ray.EPSILON) {
          Tint tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
          tint.tint(c, ray, scene);
          color = c;
          ray.t = ray.tNext;
          if (quad.doubleSided)
            ray.orientNormal(quad.n);
          else
            ray.setNormal(quad.n);
          hit = true;
        }
      }
    }

    if (hit) {
      double px = ray.o.x - Math.floor(ray.o.x + ray.d.x * Ray.OFFSET) + ray.d.x * ray.tNext;
      double py = ray.o.y - Math.floor(ray.o.y + ray.d.y * Ray.OFFSET) + ray.d.y * ray.tNext;
      double pz = ray.o.z - Math.floor(ray.o.z + ray.d.z * Ray.OFFSET) + ray.d.z * ray.tNext;
      if (px < E0 || px > E1 || py < E0 || py > E1 || pz < E0 || pz > E1) {
        // TODO this check is only really needed for wall torches
        return false;
      }

      ray.color.set(color);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
