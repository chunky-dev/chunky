package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * A block model that is made out of textured axis-aligned bounding boxes (AABBs).
 */
@PluginApi
public abstract class AABBModel implements BlockModel {

  /**
   * Different UV mapping methods.
   */
  public enum UVMapping {
    /**
     * No change in mapping.
     */
    NONE,
    /**
     * Rotate by 90 degrees clockwise.
     */
    ROTATE_90,
    /**
     * Rotate by 180 degrees.
     */
    ROTATE_180,
    /**
     * Rotate 270 degrees clockwise (90 degrees counter clockwise).
     */
    ROTATE_270,
    /**
     * Mirror horizontally (u = 1 - u).
     */
    FLIP_U,
    /**
     * Mirror vertically (v = 1 - v).
     */
    FLIP_V
  }

  /**
   * Get the boxes for this model.
   *
   * @return An array of boxes.
   */
  @PluginApi
  public abstract AABB[] getBoxes();

  /**
   * Get textures for the boxes.
   *
   * @return An array of textures for the boxes, each in north, east, south, west, top, bottom order.
   */
  @PluginApi
  public abstract Texture[][] getTextures();

  /**
   * Get tints for the boxes. If an entry is <code>null</code> or this method returns <code>null</code>, it is equivalent to {@link Tint#NONE}.
   *
   * @return An array of tints for the boxes, each in north, east, south, west, top, bottom order.
   */
  @PluginApi
  public Tint[][] getTints() {
    return null;
  }

  /**
   * Get UV mappings for the boxes. If an entry is <code>null</code> or this method returns <code>null</code>, it is equivalent to {@link UVMapping#NONE}.
   *
   * @return An array of UV mappings for the boxes, each in north, east, south, west, top, bottom order.
   */
  @PluginApi
  public UVMapping[][] getUVMapping() {
    return null;
  }

  @Override
  public int faceCount() {
    return getBoxes().length * 6;
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    getBoxes()[(face / 6) % getBoxes().length].sampleFace(face % 6, loc, rand);
  }

  @Override
  public double faceSurfaceArea(int face) {
    return getBoxes()[(face / 6) % getBoxes().length].faceSurfaceArea(face);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    AABB[] boxes = getBoxes();
    Texture[][] textures = getTextures();
    UVMapping[][] mapping = getUVMapping();
    Tint[][] tintedFaces = getTints();

    boolean hit = false;
    Tint tint = Tint.NONE;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].intersect(ray)) {
        Texture[] texturesBox = textures[i];
        Tint[] tintedFacesBox = tintedFaces != null ? tintedFaces[i] : null;
        UVMapping[] mappingBox = mapping != null ? mapping[i] : null;
        Vector3 n = ray.getNormal();
        if (n.y > 0) { // top
          ray.v = 1 - ray.v;
          if (intersectFace(ray, texturesBox[4],
            mappingBox != null ? mappingBox[4] : null
          )) {
            tint = tintedFacesBox != null ? tintedFacesBox[4] : Tint.NONE;
            hit = true;
          }
        } else if (n.y < 0) { // bottom
          if (intersectFace(ray, texturesBox[5],
            mappingBox != null ? mappingBox[5] : null)) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[5] : Tint.NONE;
          }
        } else if (n.z < 0) { // north
          if (intersectFace(ray, texturesBox[0],
            mappingBox != null ? mappingBox[0] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[0] : Tint.NONE;
          }
        } else if (n.z > 0) { // south
          if (intersectFace(ray, texturesBox[2],
            mappingBox != null ? mappingBox[2] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[2] : Tint.NONE;
          }
        } else if (n.x < 0) { // west
          if (intersectFace(ray, texturesBox[3],
            mappingBox != null ? mappingBox[3] : null)) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[3] : Tint.NONE;
          }
        } else if (n.x > 0) { // east
          if (intersectFace(ray, texturesBox[1],
            mappingBox != null ? mappingBox[1] : null)) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[1] : Tint.NONE;
          }
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

      tint.tint(ray.color, ray, scene);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  private boolean intersectFace(Ray ray, Texture texture, UVMapping mapping) {
    // This is the method that handles intersecting faces of all AABB-based models.
    // Do normal mapping, parallax occlusion mapping, specular maps and all the good stuff here!

    if (texture == null) {
      return false;
    }

    double tmp;
    if (mapping != null) {
      switch (mapping) {
        case ROTATE_90:
          tmp = ray.u;
          ray.u = 1 - ray.v;
          ray.v = tmp;
          break;
        case ROTATE_180:
          ray.u = 1 - ray.u;
          ray.v = 1 - ray.v;
          break;
        case ROTATE_270:
          tmp = ray.v;
          ray.v = 1 - ray.u;
          ray.u = tmp;
          break;
        case FLIP_U:
          ray.u = 1 - ray.u;
          break;
        case FLIP_V:
          ray.v = 1 - ray.v;
          break;
      }
    }

    float[] color = texture.getColor(ray.u, ray.v);
    if (color[3] > Ray.EPSILON) {
      ray.color.set(color);
      return true;
    }
    return false;
  }

  @Override
  public boolean isBiomeDependant() {
    Tint[][] tints = getTints();
    if (tints == null)
      return false;

    return Arrays.stream(tints)
      .filter(Objects::nonNull)
      .flatMap(Arrays::stream)
      .anyMatch(Tint::isBiomeTint);
  }
}
