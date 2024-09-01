package se.llbit.chunky.resources.texture;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.resources.ImageLoader;
import se.llbit.util.annotation.NotNull;

public class BitmapTexture extends AbstractNonlinearTexture {
  @NotNull protected BitmapImage image;
  protected int width;
  protected int height;
  protected int avgColor;
  protected float[] avgColorLinear;

  public BitmapTexture() {
    this(ImageLoader.missingImage);
  }

  public BitmapTexture(String resourceName) {
    this(ImageLoader.readResourceNonNull("textures/" + resourceName + ".png"));
  }

  public BitmapTexture(BitmapImage img) {
    this.setTexture(img);
  }

  public void setTexture(AbstractTexture texture) {
    this.setTexture(texture.getBitmap());
  }

  public void setTexture(BitmapImage newImage) {
    image = newImage;

    // Gamma correct the texture.
    avgColorLinear = new float[] {0, 0, 0, 0};

    int[] data = image.data;
    width = image.width;
    height = image.height;
    float[] pixelBuffer = new float[4];
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int index = width * y + x;
        ColorUtil.getRGBAComponentsGammaCorrected(data[index], pixelBuffer);
        avgColorLinear[0] += pixelBuffer[3] * pixelBuffer[0];
        avgColorLinear[1] += pixelBuffer[3] * pixelBuffer[1];
        avgColorLinear[2] += pixelBuffer[3] * pixelBuffer[2];
        avgColorLinear[3] += pixelBuffer[3];
      }
    }

//    avgColorFlat = new float[4];
//    if (avgColorLinear[3] > 0.001) {
//      avgColorFlat[0] = avgColorLinear[0] / avgColorLinear[3];
//      avgColorFlat[1] = avgColorLinear[1] / avgColorLinear[3];
//      avgColorFlat[2] = avgColorLinear[2] / avgColorLinear[3];
//      avgColorFlat[3] = 1;
//    }

    avgColorLinear[0] /= width * height;
    avgColorLinear[1] /= width * height;
    avgColorLinear[2] /= width * height;
    avgColorLinear[3] /= width * height;

    avgColor = ColorUtil.getArgb(FastMath.pow(avgColorLinear[0], 1 / Scene.DEFAULT_GAMMA),
      FastMath.pow(avgColorLinear[1], 1 / Scene.DEFAULT_GAMMA),
      FastMath.pow(avgColorLinear[2], 1 / Scene.DEFAULT_GAMMA), avgColorLinear[3]);
  }

  @Override
  public int getWidth() {
    return image.width;
  }

  @Override
  public int getHeight() {
    return image.height;
  }

  @Override
  public float[] getAvgColorLinear() {
    return avgColorLinear;
  }

  @Override
  public int getAvgColor() {
    return avgColor;
  }

  @Override
  public int getTextureColorI(int x, int y) {
    return image.data[width*y + x];
  }
}
