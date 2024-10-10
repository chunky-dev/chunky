package se.llbit.chunky.resources.texture;

import se.llbit.math.ColorUtil;
import se.llbit.math.Vector4;

public abstract class AbstractNonlinearTexture extends AbstractTexture {
  /**
   * Get the width of the texture.
   */
  @Override
  public abstract int getWidth();

  /**
   * Get the height of the texture.
   */
  @Override
  public abstract int getHeight();

  /**
   * @return The average linear color of this texture
   */
  @Override
  public abstract float[] getAvgColorLinear();

  /**
   * Get the average non-linear color of this texture.
   */
  @Override
  public abstract int getAvgColor();

  /**
   * @return Get the non-linear color at the given texture coordinate.
   */
  @Override
  public abstract int getTextureColorI(int x, int y);

  @Override
  public float[] getTextureColor(int x, int y) {
    float[] result = new float[4];
    ColorUtil.getRGBAComponentsGammaCorrected(this.getTextureColorI(x, y), result);
    return result;
  }

  @Override
  public void getTextureColor(int x, int y, Vector4 color) {
    ColorUtil.getRGBAComponentsGammaCorrected(this.getTextureColorI(x, y), color);
  }
}
