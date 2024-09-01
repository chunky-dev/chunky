package se.llbit.chunky.resources.texture;

import se.llbit.math.ColorUtil;
import se.llbit.math.Vector4;

public class SolidColorTexture extends AbstractTexture {
  private final float[] value;
  private final int valueI;

  public SolidColorTexture(float r, float g, float b, float a) {
    this.value = new float[] {r, g, b, a};
    this.valueI = ColorUtil.ArgbFromLinear(this.value);
  }

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
    return value;
  }

  @Override
  public float[] getAvgColorLinear() {
    return value;
  }

  @Override
  public int getAvgColor() {
    return valueI;
  }

  @Override
  public int getTextureColorI(int x, int y) {
    return valueI;
  }

  @Override
  public void getColorInterpolated(double u, double v, Vector4 c) {
    c.set(value);
  }
}
