package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A block model that is made out of textured AABBs.
 */
@PluginApi
public abstract class AABBModel implements BlockModel {

  /**
   * Different UV mapping methods.
   *  - None: No change in mapping
   *  - ROTATE_90: Rotate 90 degrees clockwise
   *  - ROTATE_180: Rotate 180 degrees
   *  - ROTATE_270: Rotate 270 degrees clockwise (90 degrees counterclockwise)
   *  - FLIP_U: Flip along the X axis (u = 1 - u)
   *  - FLIP_V: Flip along the Y axis (v = 1 - v)
   *
   * Note: a value of {@code null} is equivalent to {@code NONE}
   */
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
  public Tint[][] getTints() {
    return null;
  }

  @PluginApi
  public UVMapping[][] getUVMapping() {
    return null;
  }

  @Override
  public int numFaces() {
    return getBoxes().length * 6;
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    int index = (face / 6) % getBoxes().length;
    getBoxes()[index].sampleFace(face % 6, loc, rand);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    AABB[] boxes = getBoxes();
    Texture[][] textures = getTextures();
    UVMapping[][] mapping = getUVMapping();
    Tint[][] tintedFaces = getTints();

    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].intersect(ray)) {
        Tint[] tintedFacesBox = tintedFaces != null ? tintedFaces[i] : null;
        Vector3 n = ray.getNormal();
        if (n.y > 0) { // top
          ray.v = 1 - ray.v;
          if (intersectFace(ray, scene, textures[i][4],
              mapping != null ? mapping[i][4] : null,
              tintedFacesBox != null ? tintedFacesBox[4] : Tint.NONE)) {
            hit = true;
          }
        } else if (n.y < 0) { // bottom
          hit = intersectFace(ray, scene, textures[i][5],
              mapping != null ? mapping[i][4] : null,
              tintedFacesBox != null ? tintedFacesBox[5] : Tint.NONE) || hit;
        } else if (n.z < 0) { // north
          hit = intersectFace(ray, scene, textures[i][0],
              mapping != null ? mapping[i][4] : null,
              tintedFacesBox != null ? tintedFacesBox[0] : Tint.NONE) || hit;
        } else if (n.z > 0) { // south
          hit = intersectFace(ray, scene, textures[i][2],
              mapping != null ? mapping[i][4] : null,
              tintedFacesBox != null ? tintedFacesBox[2] : Tint.NONE) || hit;
        } else if (n.x < 0) { // west
          hit = intersectFace(ray, scene, textures[i][3],
              mapping != null ? mapping[i][4] : null,
              tintedFacesBox != null ? tintedFacesBox[3] : Tint.NONE) || hit;
        } else if (n.x > 0) { // east
          hit = intersectFace(ray, scene, textures[i][1],
              mapping != null ? mapping[i][4] : null,
              tintedFacesBox != null ? tintedFacesBox[1] : Tint.NONE) || hit;
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

  private boolean intersectFace(Ray ray, Scene scene, Texture texture, UVMapping mapping,
      Tint tintType) {
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
      tintType.tint(color, ray, scene);
      ray.color.set(color);
      return true;
    }
    return false;
  }
}
