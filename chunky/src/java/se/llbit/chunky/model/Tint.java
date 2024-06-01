package se.llbit.chunky.model;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.log.Log;
import se.llbit.math.ColorUtil;
import se.llbit.math.Constants;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector4;

public class Tint {
  public enum TintType {
    NONE,
    CONSTANT,
    BIOME_FOLIAGE,
    BIOME_GRASS,
    BIOME_WATER
  }

  /**
   * Commonly used tint types for convenience. Use these instead of creating a new Tint object.
   */
  public static final Tint NONE = new Tint(TintType.NONE);
  public static final Tint BIOME_FOLIAGE = new Tint(TintType.BIOME_FOLIAGE);
  public static final Tint BIOME_GRASS = new Tint(TintType.BIOME_GRASS);
  public static final Tint BIOME_WATER = new Tint(TintType.BIOME_WATER);

  public final TintType type;
  public final float[] tint;

  /**
   * Create a tint with a specified type. Do not use. Use the static Tints.
   */
  private Tint(TintType type) {
    this.type = type;
    tint = null;
  }

  /**
   * Create a constant tint.
   *
   * @param tint Tint color as an ARGB integer
   */
  public Tint(int tint) {
    this.type = TintType.CONSTANT;
    this.tint = new float[4];
    ColorUtil.getRGBAComponents(tint, this.tint);
    ColorUtil.toLinear(this.tint);
  }

  /**
   * Create a constant tint.
   *
   * @param tint Tint color as an array of floats (length >= 3)
   */
  public Tint(float[] tint) {
    this.type = TintType.CONSTANT;
    this.tint = new float[3];
    System.arraycopy(tint, 0, this.tint, 0, 3);
  }

  private float[] getTintColor(Ray2 ray, Scene scene) {
    switch (type) {
      case NONE:
        return null;
      case CONSTANT:
        return this.tint;
      case BIOME_FOLIAGE:
        return scene.getFoliageColor(
          (int) (ray.o.x + ray.d.x * Constants.OFFSET),
          (int) (ray.o.y + ray.d.y * Constants.OFFSET),
          (int) (ray.o.z + ray.d.z * Constants.OFFSET)
        );
      case BIOME_GRASS:
        return scene.getGrassColor(
          (int) (ray.o.x + ray.d.x * Constants.OFFSET),
          (int) (ray.o.y + ray.d.y * Constants.OFFSET),
          (int) (ray.o.z + ray.d.z * Constants.OFFSET)
        );
      case BIOME_WATER:
        return scene.getWaterColor(
          (int) (ray.o.x + ray.d.x * Constants.OFFSET),
          (int) (ray.o.y + ray.d.y * Constants.OFFSET),
          (int) (ray.o.z + ray.d.z * Constants.OFFSET)
        );
      default:
        Log.warn("Unsupported tint type " + type);
        return null;
    }
  }

  /**
   * Tint a color array with the tint option of this Tint object.
   */
  public void tint(float[] color, Ray2 ray, Scene scene) {
    float[] tintColor = this.getTintColor(ray, scene);
    if (tintColor != null) {
      color[0] *= tintColor[0];
      color[1] *= tintColor[1];
      color[2] *= tintColor[2];
    }
  }

  /**
   * Tint a color vector with the tint option of this Tint object.
   */
  public void tint(Vector4 color, Ray2 ray, Scene scene) {
    float[] tintColor = this.getTintColor(ray, scene);
    if (tintColor != null) {
      color.x *= tintColor[0];
      color.y *= tintColor[1];
      color.z *= tintColor[2];
    }
  }
}
