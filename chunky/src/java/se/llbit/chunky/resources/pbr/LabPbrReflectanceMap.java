package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.Ray;

public class LabPbrReflectanceMap implements ReflectanceMap {

  private int width;
  private int height;
  private byte[] data;

  @Override
  public boolean load(BitmapImage texture) {
    width = texture.width;
    height = texture.height;
    data = new byte[texture.width * texture.height];

    boolean hasReflectance = false;
    for (int y = 0; y < texture.height; ++y) {
      for (int x = 0; x < texture.width; ++x) {
        // green channel (0 to 299 are f0, where 229 is 229/255 reflectance (about 90%)
        int value = (texture.data[y * texture.width + x] >>> 8) & 0xFF;
        if (value > 0 && value < 230) {
          hasReflectance = true;
          data[y * texture.width + x] = (byte) value;
        } else {
          // values from 230 to 255 represent metals (not yet supported)
        }
      }
    }
    return hasReflectance;
  }

  @Override
  public double getReflectanceAt(double u, double v) {
    int x = (int) (u * width - Ray.EPSILON);
    int y = (int) ((1 - v) * height - Ray.EPSILON);
    int rawValue = data[y * width + x] & 0xFF;
    return rawValue / 255.0;
  }
}