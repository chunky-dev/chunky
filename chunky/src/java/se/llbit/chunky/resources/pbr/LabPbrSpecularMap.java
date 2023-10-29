package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.Ray;

/**
 * Specular map that follows the labPBR format.
 *
 * @see <a href="https://github.com/rre36/lab-pbr/wiki/Specular-Texture-Details#alpha">Specular
 * Texture details</a>
 */
public class LabPbrSpecularMap implements EmissionMap, ReflectanceMap, RoughnessMap, MetalnessMap {

  private final int width;
  private final int height;
  private byte[] emissionMap;
  private byte[] reflectanceMap;
  private float[] roughnessMap;
  private boolean hasMetalness;

  public LabPbrSpecularMap(BitmapImage texture) {
    width = texture.width;
    height = texture.height;

    emissionMap = new byte[texture.width * texture.height];
    boolean hasEmission = false;
    for (int y = 0; y < texture.height; ++y) {
      for (int x = 0; x < texture.width; ++x) {
        // alpha channel
        if ((emissionMap[y * texture.width + x] = (byte) (
          texture.data[y * texture.width + x] >>> 24)) != (byte) 0x00) {
          hasEmission = true;
        }
      }
    }
    if (!hasEmission) {
      emissionMap = null;
    }

    reflectanceMap = new byte[texture.width * texture.height];
    boolean hasReflectance = false;
    hasMetalness = false;
    for (int y = 0; y < texture.height; ++y) {
      for (int x = 0; x < texture.width; ++x) {
        // green channel (0 to 299 are f0, where 229 is 229/255 reflectance (about 90%)
        int value = (texture.data[y * texture.width + x] >>> 8) & 0xFF;
        if (value > 0 && value < 230) {
          hasReflectance = true;
        }
        if (value >= 230) {
          // values from 230 to 255 represent metals (hard-coded metals not yet supported)
          hasMetalness = true;
        }
        reflectanceMap[y * texture.width + x] = (byte) value;
      }
    }
    if (!hasReflectance && !hasMetalness) {
      reflectanceMap = null;
    }

    roughnessMap = new float[texture.width * texture.height];
    boolean hasRoughness = false;
    for (int y = 0; y < texture.height; ++y) {
      for (int x = 0; x < texture.width; ++x) {
        // red channel stores perceptual smoothness
        int value = (texture.data[y * texture.width + x] >>> 16) & 0xFF;
        if (value > 0) {
          hasRoughness = true;
          roughnessMap[y * texture.width + x] = (float) Math.pow((255 - value) / 255.0, 2);
        }
      }
    }
    if (!hasRoughness) {
      roughnessMap = null;
    }
  }

  @Override
  public float getEmittanceAt(double u, double v) {
    if (emissionMap == null) {
      return 0;
    }
    int x = (int) (u * width - Ray.EPSILON);
    int y = (int) ((1 - v) * height - Ray.EPSILON);
    int rawValue = emissionMap[y * width + x] & 0xFF;
    if (rawValue == 255) {
      return 0;
    }
    return rawValue / 254.0f;
  }

  public boolean hasEmission() {
    return emissionMap != null;
  }

  @Override
  public float getReflectanceAt(double u, double v) {
    if (reflectanceMap == null) {
      return 0;
    }
    int x = (int) (u * width - Ray.EPSILON);
    int y = (int) ((1 - v) * height - Ray.EPSILON);
    int rawValue = reflectanceMap[y * width + x] & 0xFF;
    if (rawValue >= 230) {
      // values from 230 to 255 represent metals (hard-coded metals not yet supported)
      return 0;
    }
    return rawValue / 255.0f;
  }

  public boolean hasReflectance() {
    return reflectanceMap != null;
  }

  @Override
  public float getMetalnessAt(double u, double v) {
    if (reflectanceMap == null) {
      return 0;
    }
    int x = (int) (u * width - Ray.EPSILON);
    int y = (int) ((1 - v) * height - Ray.EPSILON);
    int rawValue = reflectanceMap[y * width + x] & 0xFF;
    return rawValue >= 230 ? 1 : 0;
  }

  public boolean hasMetalness() {
    return hasMetalness;
  }

  @Override
  public float getRoughnessAt(double u, double v) {
    if (roughnessMap == null) {
      return 0;
    }
    int x = (int) (u * width - Ray.EPSILON);
    int y = (int) ((1 - v) * height - Ray.EPSILON);
    return roughnessMap[y * width + x];
  }

  public boolean hasRoughness() {
    return roughnessMap != null;
  }
}
