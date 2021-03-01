package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

public class LabPbrNormalMap implements NormalMap {

  private int width;
  private int height;
  private Vector3[] normals;

  public LabPbrNormalMap(BitmapImage normalMap) {
    width = normalMap.width;
    height = normalMap.height;
    normals = new Vector3[width * height];
    float[] color = new float[4];
    for (int x = 0; x < normalMap.width; x++) {
      for (int y = 0; y < normalMap.height; y++) {
        ColorUtil.getRGBAComponents(normalMap.getPixel(x, y), color);
        Vector2 xy = new Vector2(color[0] * 2 - 1, color[1] * 2 - 1);
        // xy.normalize();
        normals[width * y + x] =
            new Vector3(color[0] * 2 - 1, color[1] * 2 - 1, Math.sqrt(1.0 - xy.dot(xy)));
        normals[width * y + x].normalize();
      }
    }
  }

  @Override
  public Vector3 getNormalAt(double u, double v) {
    return this.normals[
        (int) ((1 - v) * height - Ray.EPSILON) * width + (int) (u * width - Ray.EPSILON)];
  }
}
