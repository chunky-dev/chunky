package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.pbr.NormalMap;
import se.llbit.log.Log;
import se.llbit.math.Quad;
import se.llbit.math.Ray;

public abstract class QuadModel implements BlockModel {

  @PluginApi
  public abstract Quad[] getQuads();

  @PluginApi
  public abstract Texture[] getTextures();

  @PluginApi
  public TintType[] getTintedQuads() {
    return null;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    Quad[] quads = getQuads();
    Texture[] textures = getTextures();
    TintType[] tintedQuads = getTintedQuads();

    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = textures[i].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          TintType tintType = tintedQuads == null ? TintType.NONE : tintedQuads[i];
          if (tintType != TintType.NONE) {
            float[] biomeColor;
            switch (tintType) {
              case BIOME_FOLIAGE:
                biomeColor = ray.getBiomeFoliageColor(scene);
                break;
              case BIOME_GRASS:
                biomeColor = ray.getBiomeGrassColor(scene);
                break;
              case BIOME_WATER:
                biomeColor = ray.getBiomeWaterColor(scene);
                break;
              default:
                Log.warn("Unsupported TintType: " + tintType);
                biomeColor = new float[]{1, 1, 1};
                break;
            }
            color[0] *= biomeColor[0];
            color[1] *= biomeColor[1];
            color[2] *= biomeColor[2];
          }
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          NormalMap.apply(ray, quad, textures[i]);
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
}
