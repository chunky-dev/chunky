package se.llbit.chunky.resources.texture;

import se.llbit.math.Vector4;

public class EmptyTexture extends AbstractTexture {
  public static final EmptyTexture INSTANCE = new EmptyTexture();

  private final float[] zeros = new float[4];

  private EmptyTexture() {}

  @Override
  public int getWidth() {
    return 1;
  }

  @Override
  public int getHeight() {
    return 1;
  }

  @Override
  public float[] getTextureColor(int x, int y) {
    return zeros;
  }

  @Override
  public float[] getAvgColorLinear() {
    return zeros;
  }

  @Override
  public int getAvgColor() {
    return 0;
  }

  @Override
  public int getTextureColorI(int x, int y) {
    return 0;
  }

  @Override
  public void getColorInterpolated(double u, double v, Vector4 c) {
    c.set(0, 0, 0, 0);
  }
}
