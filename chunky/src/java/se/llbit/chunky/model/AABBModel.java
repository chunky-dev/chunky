package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

/**
 * A block model that is made out of textured AABBs.
 */
public abstract class AABBModel implements BlockModel {

  public enum UVMapping {
    NONE,
    ROTATE_90,
    ROTATE_180,
    ROTATE_270,
    FLIP_U,
    FLIP_V
  }

  @PluginApi
  public abstract AABB[] getBoxes();

  @PluginApi
  public abstract Texture[][] getTextures();

  @PluginApi
  public TintType[][] getTintedFaces() {
    return null;
  }

  @PluginApi
  public UVMapping[][] getUVMapping() {
    return null;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    AABB[] boxes = getBoxes();
    Texture[][] textures = getTextures();
    TintType[][] tintedFaces = getTintedFaces();

    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].intersect(ray)) {
        TintType[] tintedFacesBox = tintedFaces != null ? tintedFaces[i] : null;
        if (ray.n.y > 0) { // top
          ray.v = 1 - ray.v;
          if (intersectFace(ray, scene, textures[i][4],
              tintedFacesBox != null ? tintedFacesBox[4] : TintType.NONE)) {
            hit = true;
          }
        } else if (ray.n.y < 0) { // bottom
          hit = intersectFace(ray, scene, textures[i][5],
              tintedFacesBox != null ? tintedFacesBox[5] : TintType.NONE) || hit;
        } else if (ray.n.z < 0) { // north
          hit = intersectFace(ray, scene, textures[i][0],
              tintedFacesBox != null ? tintedFacesBox[0] : TintType.NONE) || hit;
        } else if (ray.n.z > 0) { // south
          hit = intersectFace(ray, scene, textures[i][2],
              tintedFacesBox != null ? tintedFacesBox[2] : TintType.NONE) || hit;
        } else if (ray.n.x < 0) { // west
          hit = intersectFace(ray, scene, textures[i][3],
              tintedFacesBox != null ? tintedFacesBox[3] : TintType.NONE) || hit;
        } else if (ray.n.x > 0) { // east
          hit = intersectFace(ray, scene, textures[i][1],
              tintedFacesBox != null ? tintedFacesBox[1] : TintType.NONE) || hit;
        }
        if (hit) {
          ray.t = ray.tNext;
        }
      }
    }
    if (hit) {
      if (ray.getCurrentMaterial().opaque) {
        ray.color.w = 1;
      }
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  private boolean intersectFace(Ray ray, Scene scene, Texture texture, TintType tintType) {
    // This is the method that handles intersecting faces of all AABB-based models.
    // Do normal mapping, parallax occlusion mapping, specular maps and all the good stuff here!

    if (texture == null) {
      return false;
    }
    // TODO uv mapping (rotation, flipping)
    float[] color = texture.getColor(ray.u, ray.v);
    if (color[3] > Ray.EPSILON) {
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
      return true;
    }
    return false;
  }
}
