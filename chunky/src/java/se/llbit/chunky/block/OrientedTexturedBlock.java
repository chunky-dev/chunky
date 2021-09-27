package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * A textured block that can have one of six orientations, e.g. barrels.
 */
public class OrientedTexturedBlock extends TexturedBlock {

  private static final Quad[] sides = {
      // north
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // south
      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // west
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // east
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // top
      new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
  };

  private static final int[][] uvRotationMap = {
      {0, 0, 0, 0, 2, 0}, // up
      {2, 2, 2, 2, 2, 0}, // down
      {2, 0, 3, 1, 2, 2}, // north
      {0, 2, 1, 3, 0, 0}, // south
      {1, 3, 2, 0, 1, 3}, // west
      {3, 1, 0, 2, 3, 1}, // east
  };

  /**
   * Maps textures (0=north, 1=south, 2=west, 3=east, 4=top, 5=bottom) by the orientation. If
   * orientation=up (0), does nothing
   */
  private static final int[][] textureOrientationMap = {
      {0, 1, 2, 3, 4, 5}, // up
      {0, 1, 3, 2, 5, 4}, // down
      {4, 5, 3, 2, 0, 1}, // north
      {5, 4, 2, 3, 0, 1}, // south
      {3, 2, 4, 5, 0, 1}, // west
      {2, 3, 5, 4, 0, 1}, // east
  };

  protected final int facing;

  public OrientedTexturedBlock(String name, String facing, Texture side, Texture top,
      Texture bottom) {
    this(name, facing, side, side, side, side, top, bottom);
  }


  public OrientedTexturedBlock(String name, String facing, Texture north, Texture south,
      Texture east, Texture west, Texture top, Texture bottom) {
    super(name, north, south, east, west, top, bottom);
    switch (facing) {
      case "up":
        this.facing = 0;
        break;
      case "down":
        this.facing = 1;
        break;
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
        break;
      default:
        throw new IllegalArgumentException(("Invalid facing: " + facing));
    }
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    int[][] textureOrientationMap = getTextureOrientationMap();
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < sides.length; ++i) {
      Quad side = sides[i];
      if (side.intersect(ray)) {
        rotateUV(ray, i);
        texture[textureOrientationMap[facing][i]].getColor(ray);
        ray.setNormal(side.n);
        ray.t = ray.tNext;
        hit = true;
      }
    }
    if (hit) {
      ray.color.w = 1;
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  private void rotateUV(Ray ray, int sideIndex) {
    int[][] uv = this.getUvRotationMap();
    double u = ray.u;
    int angle = uv[facing][sideIndex];
    double c, s;
    if (angle == 0) {
      return;
    } else if (angle == 1) {
      c = 0;
      s = 1;
    } else if (angle == 2) {
      c = -1;
      s = 0;
    } else if (angle == 3) {
      c = 0;
      s = -1;
    } else {
      throw new IllegalArgumentException("Invalid angle");
    }
    ray.u = c * (ray.u - 0.5) - ((ray.v) - 0.5) * s + 0.5;
    ray.v = ((u - 0.5) * s + ((ray.v) - 0.5) * c + 0.5);
  }

  protected int[][] getUvRotationMap() {
    return uvRotationMap;
  }

  protected int[][] getTextureOrientationMap() {
    return textureOrientationMap;
  }
}
