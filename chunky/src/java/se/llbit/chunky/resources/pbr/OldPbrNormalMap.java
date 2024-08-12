package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

public class OldPbrNormalMap implements NormalMap {

  private int width;
  private int height;
  private float[] normals;

  public OldPbrNormalMap(BitmapImage normalMap) {
    width = normalMap.width;
    height = normalMap.height;
    normals = new float[width * height * 3];
    float[] color = new float[4];
    for (int x = 0; x < normalMap.width; x++) {
      for (int y = 0; y < normalMap.height; y++) {
        ColorUtil.getRGBAComponents(normalMap.getPixel(x, y), color);
        float nx = color[0] * 2 - 1;
        float ny = color[1] * 2 - 1;
        float nz = color[2] * 2 - 1;
        float invLength = (float) (1 / Math.sqrt(nx * nx + ny * ny + nz * nz));
        normals[(width * y + x) * 3] = nx * invLength;
        normals[(width * y + x) * 3 + 1] = ny * invLength;
        normals[(width * y + x) * 3 + 2] = nz * invLength;
      }
    }
  }

  @Override
  public boolean applyNormal(Ray ray) {
    int x = (int) (ray.u * width - Ray.EPSILON);
    int y = (int) ((1 - ray.v) * height - Ray.EPSILON);
    int i = (y * width + x) * 3;

    float nx = normals[i];
    float ny = normals[i + 1];
    float nz = normals[i + 2];
    if (Math.abs(nx) > 0 || Math.abs(ny) > 0 || Math.abs(nz) > 0) {
      Vector3 normal = ray.getNormal();
      normal.set(normals[i], -normals[i + 1], normals[i + 2]);
      return true;
    }
    return false;
  }
}
