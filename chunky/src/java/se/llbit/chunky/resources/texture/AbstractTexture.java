package se.llbit.chunky.resources.texture;

import javafx.scene.image.Image;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.fxutil.FxImageUtil;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;


public abstract class AbstractTexture {
  /** Should the average color be used? */
  public static boolean useAverageColor = PersistentSettings.getSingleColorTextures();

  /** True if {@code useAverageColor} should be respected */
  protected boolean followAverageColor = true;

  /**
   * Get the width of the texture.
   */
  public abstract int getWidth();

  /**
   * Get the height of the texture.
   */
  public abstract int getHeight();

  /**
   * Get the linear color at the given texture coordinate. This should never return the average color.
   * @return The RGBA color
   */
  public abstract float[] getTextureColor(int x, int y);

  /**
   * Get the average linear color of this texture.
   * @return The RGBA color
   */
  public abstract float[] getAvgColorLinear();

  /**
   * Get the average non-linear color of this texture. It is recommended
   * to override this with a more efficient implementation.
   * @return The average non-linear color
   */
  public int getAvgColor() {
    return ColorUtil.ArgbFromLinear(this.getAvgColorLinear());
  }

  /**
   * Get the linear color at the given texture coordinate. This should never return the average color.
   * It is recommended to override this with a more efficient implementation.
   * @param color The vector to store the resulting color in
   */
  public void getTextureColor(int x, int y, Vector4 color) {
    color.set(this.getTextureColor(x, y));
  }

  /**
   * Get the non-linear color at the given texture coordinate. It is recommended
   * to override this with a more efficient implementation.
   *
   * @return The ARGB color
   */
  public int getTextureColorI(int x, int y) {
    return ColorUtil.ArgbFromLinear(this.getTextureColor(x, y));
  }



  /**
   * Get the linear color at the given texture coordinate
   */
  public void getColor(int x, int y, Vector4 color) {
    if (AbstractTexture.useAverageColor && this.followAverageColor) {
      color.set(this.getAvgColorLinear());
    } else {
      getTextureColor(x, y, color);
    }
  }

  /**
   * Get the linear color at the given texture coordinate
   */
  public float[] getColor(int x, int y) {
    if (AbstractTexture.useAverageColor && this.followAverageColor) {
      return this.getAvgColorLinear();
    } else {
      return getTextureColor(x, y);
    }
  }

  /**
   * Get the linear color at the given UV coordinate
   */
  public void getColor(double u, double v, Vector4 color) {
    int x = (int) (u * this.getWidth() - Ray.EPSILON);
    int y = (int) ((1 - v) * this.getHeight() - Ray.EPSILON);
    this.getColor(x, y, color);
  }

  /**
   * Get the linear color value at the given UV coordinate
   * @return The RGBA color
   */
  public float[] getColor(double u, double v) {
    int x = (int) (u * this.getWidth() - Ray.EPSILON);
    int y = (int) ((1 - v) * this.getHeight() - Ray.EPSILON);
    return this.getColor(x, y);
  }

  /**
   * Get the linear color at the ray's UV coordinate and store the value in the ray.
   */
  public void getColor(Ray ray) {
    getColor(ray.u, ray.v, ray.color);
  }

  /**
   * Get the non-linear color at the given texture coordinate, wrapping the texture.
   * This will never return the average color.
   *
   * @return The ARGB color
   */
  public int getColorWrapped(int x, int y) {
    x = (x + this.getWidth()) % this.getWidth();
    y = (y + this.getHeight()) % this.getHeight();
    return getTextureColorI(x, y);
  }

  /**
   * Get bilinear interpolated color value.
   */
  public void getColorInterpolated(double u, double v, Vector4 c) {
    double x = u * (this.getWidth() - 1);
    double y = (1 - v) * (this.getHeight() - 1);
    double weight;
    float[] rgb;
    int fx = (int) QuickMath.floor(x);
    int cx = (int) QuickMath.ceil(x);
    int fy = (int) QuickMath.floor(y);
    int cy = (int) QuickMath.ceil(y);

    rgb = this.getColor(fx, fy);
    weight = (1 - (y - fy)) * (1 - (x - fx));
    c.x = weight * rgb[0];
    c.y = weight * rgb[1];
    c.z = weight * rgb[2];
    rgb = this.getColor(cx, fy);
    weight = (1 - (y - fy)) * (1 - (cx - x));
    c.x += weight * rgb[0];
    c.y += weight * rgb[1];
    c.z += weight * rgb[2];
    rgb = this.getColor(fx, cy);
    weight = (1 - (cy - y)) * (1 - (x - fx));
    c.x += weight * rgb[0];
    c.y += weight * rgb[1];
    c.z += weight * rgb[2];
    rgb = this.getColor(cx, cy);
    weight = (1 - (cy - y)) * (1 - (cx - x));
    c.x += weight * rgb[0];
    c.y += weight * rgb[1];
    c.z += weight * rgb[2];
  }

  /**
   * Export the current texture as a bitmap image.
   */
  @PluginApi
  public BitmapImage getBitmap() {
    BitmapImage img = new BitmapImage(this.getWidth(), this.getHeight());
    for (int x = 0; x < img.width; x++) {
      for (int y = 0; y < img.height; y++) {
        img.setPixel(x, y, this.getTextureColorI(x, y));
      }
    }
    return img;
  }

  /**
   * Transform this texture into a JavaFX image. Warning: This will cache the
   * image on first use. The cached image can be purged by calling {@code clearFxImage}
   */
  public Image fxImage() {
    Image img = fxImage;
    if (img == null) {
      img = FxImageUtil.toFxImage(this.getBitmap());
      fxImage = img;
    }
    return img;
  }

  /**
   * Clear the cached JavaFX image.
   */
  public void clearFxImage() {
    fxImage = null;
  }

  protected Image fxImage = null;
}
