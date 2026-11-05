package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

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
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    AABB[] boxes = getBoxes();
    Texture[][] textures = getTextures();
    UVMapping[][] mapping = getUVMapping();
    Tint[][] tintedFaces = getTints();

    boolean hit = false;
    Tint tint = Tint.NONE;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].closestIntersection(ray, intersectionRecord)) {
        Texture[] texturesBox = textures[i];
        Tint[] tintedFacesBox = tintedFaces != null ? tintedFaces[i] : null;
        UVMapping[] mappingBox = mapping != null ? mapping[i] : null;
        Vector3 n = intersectionRecord.shadeN;
        if (n.y > 0) { // top
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.y;
          if (intersectFace(intersectionRecord, texturesBox[4],
            mappingBox != null ? mappingBox[4] : null
          )) {
            tint = tintedFacesBox != null ? tintedFacesBox[4] : Tint.NONE;
            hit = true;
          }
        } else if (n.y < 0) { // bottom
          if (intersectFace(intersectionRecord, texturesBox[5],
            mappingBox != null ? mappingBox[5] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[5] : Tint.NONE;
          }
        } else if (n.z < 0) { // north
          if (intersectFace(intersectionRecord, texturesBox[0],
            mappingBox != null ? mappingBox[0] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[0] : Tint.NONE;
          }
        } else if (n.z > 0) { // south
          if (intersectFace(intersectionRecord, texturesBox[2],
            mappingBox != null ? mappingBox[2] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[2] : Tint.NONE;
          }
        } else if (n.x < 0) { // west
          if (intersectFace(intersectionRecord, texturesBox[3],
            mappingBox != null ? mappingBox[3] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[3] : Tint.NONE;
          }
        } else if (n.x > 0) { // east
          if (intersectFace(intersectionRecord, texturesBox[1],
            mappingBox != null ? mappingBox[1] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[1] : Tint.NONE;
          }
        }
      }
    }
    if (hit) {
      tint.tint(intersectionRecord.color, ray, scene);
    }
    return hit;
  }

  protected boolean intersectFace(IntersectionRecord intersectionRecord, Texture texture, UVMapping mapping) {
    // This is the method that handles intersecting faces of all AABB-based models.
    // Do normal mapping, parallax occlusion mapping, specular maps and all the good stuff here!

    if (texture == null) {
      intersectionRecord.color.set(1, 1, 1, 0);
      return true;
    }

    double tmp;
    if (mapping != null) {
      switch (mapping) {
        case ROTATE_90:
          tmp = intersectionRecord.uv.x;
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.y;
          intersectionRecord.uv.y = tmp;
          break;
        case ROTATE_180:
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.x;
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.y;
          break;
        case ROTATE_270:
          tmp = intersectionRecord.uv.y;
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.x;
          intersectionRecord.uv.x = tmp;
          break;
        case FLIP_U:
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.x;
          break;
        case FLIP_V:
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.y;
          break;
      }
    }

    float[] color = texture.getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
    if (color[3] > Constants.EPSILON) {
      intersectionRecord.color.set(color);
    } else {
      intersectionRecord.color.set(1, 1, 1, 0);
    }
    return true;
  }

  @Override
  public boolean isInside(Ray ray) {
    return isInside(ray.o);
  }

  public boolean isInside(Vector3 p) {
    double ix = p.x - QuickMath.floor(p.x);
    double iy = p.y - QuickMath.floor(p.y);
    double iz = p.z - QuickMath.floor(p.z);
    AABB[] boxes = getBoxes();
    for (AABB box: boxes) {
      if (box.inside(ix, iy, iz)) {
        return true;
      }
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
