package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

import java.util.Random;

/**
 * A block model that is made out of textured AABBs.
 */
@PluginApi
public abstract class AABBModel implements BlockModel {

  /**
   * Different UV mapping methods.
   * - None: No change in mapping
   * - ROTATE_90: Rotate 90 degrees clockwise
   * - ROTATE_180: Rotate 180 degrees
   * - ROTATE_270: Rotate 270 degrees clockwise (90 degrees counterclockwise)
   * - FLIP_U: Flip along the X axis (u = 1 - u)
   * - FLIP_V: Flip along the Y axis (v = 1 - v)
   * <p>
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
  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    AABB[] boxes = getBoxes();
    Texture[][] textures = getTextures();
    UVMapping[][] mapping = getUVMapping();
    Tint[][] tintedFaces = getTints();

    boolean hit = false;
    Tint tint = Tint.NONE;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].closestIntersection(ray, intersectionRecord)) {
        Tint[] tintedFacesBox = tintedFaces != null ? tintedFaces[i] : null;
        Vector3 n = intersectionRecord.shadeN;
        if (n.y > 0) { // top
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.x;
          if (intersectFace(intersectionRecord, scene, textures[i][4],
            mapping != null ? mapping[i][4] : null
          )) {
            tint = tintedFacesBox != null ? tintedFacesBox[4] : Tint.NONE;
            hit = true;
          }
        } else if (n.y < 0) { // bottom
          if (intersectFace(intersectionRecord, scene, textures[i][5],
            mapping != null ? mapping[i][5] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[5] : Tint.NONE;
          }
        } else if (n.z < 0) { // north
          if (intersectFace(intersectionRecord, scene, textures[i][0],
            mapping != null ? mapping[i][0] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[0] : Tint.NONE;
          }
        } else if (n.z > 0) { // south
          if (intersectFace(intersectionRecord, scene, textures[i][2],
            mapping != null ? mapping[i][2] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[2] : Tint.NONE;
          }
        } else if (n.x < 0) { // west
          if (intersectFace(intersectionRecord, scene, textures[i][3],
            mapping != null ? mapping[i][3] : null
          )) {
            hit = true;
            tint = tintedFacesBox != null ? tintedFacesBox[3] : Tint.NONE;
          }
        } else if (n.x > 0) { // east
          if (intersectFace(intersectionRecord, scene, textures[i][1],
            mapping != null ? mapping[i][1] : null
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

  public boolean intersectFace(IntersectionRecord intersectionRecord, Scene scene, Texture texture, UVMapping mapping) {
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
  public boolean isInside(Ray2 ray) {
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
}
