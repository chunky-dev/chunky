package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;

/**
 * Emission map that follows the old PBR format. In BSL this option is called "Old PBR + Emissive"
 * and the emission map is stored in the blue channel.
 */
public class OldPbrEmissionMap implements EmissionMap {

  private int width;
  private int height;
  private byte[] data;

  @Override
  public boolean load(BitmapImage texture) {
    width = texture.width;
    height = texture.height;
    data = new byte[texture.width * texture.height];

    boolean hasEmission = false;
    for (int y = 0; y < texture.height; ++y) {
      for (int x = 0; x < texture.width; ++x) {
        // blue
        if ((data[y * texture.width + x] = (byte) (
            texture.data[y * texture.width + x] & 0xFF)) != (byte) 0x00) {
          hasEmission = true;
        }
      }
    }
    return hasEmission;
  }

  @Override
  public double getEmittanceAt(double u, double v) {
    int x = (int) (u * width);
    int y = (int) ((1 - v) * height);
    int rawValue = data[y * width + x] & 0xFF;
    return rawValue / 255.0;
  }
}
