package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.PersistentSettings;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.util.Configurable;

public class CanvasConfig implements Configurable {
  /**
   * Minimum canvas width.
   */
  public static final int MIN_CANVAS_WIDTH = 20;

  /**
   * Minimum canvas height.
   */
  public static final int MIN_CANVAS_HEIGHT = 20;

  private int width;
  private int height;

  private int cropWidth = 0;
  private int cropHeight = 0;

  private int cropX = 0;
  private int cropY = 0;

  public CanvasConfig() {
    this(
      PersistentSettings.get3DCanvasWidth(),
      PersistentSettings.get3DCanvasHeight()
    );
  }
  public CanvasConfig(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public void reset() {
    cropWidth = 0;
    cropHeight = 0;
    cropX = 0;
    cropY = 0;
  }

  public void copyState(CanvasConfig other) {
    width = other.width;
    height = other.height;
    cropWidth = other.cropWidth;
    cropHeight = other.cropHeight;
    cropX = other.cropX;
    cropY = other.cropY;
  }

  /**
   * @return Canvas width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return Canvas height
   */
  public int getHeight() {
    return height;
  }

  public int getSavedCropWidth() {
    return cropWidth;
  }

  /**
   * @return cropWidth if the canvas is cropped, otherwise width
   */
  public int getCropWidth() {
    if (isCanvasCropped()) {
      return cropWidth;
    }
    return width;
  }

  public int getSavedCropHeight() {
    return cropHeight;
  }

  /**
   * @return cropHeight if the canvas is cropped, otherwise height
   */
  public int getCropHeight() {
    if (isCanvasCropped()) {
      return cropHeight;
    }
    return height;
  }

  public int getSavedCropX() {
    return cropX;
  }

  /**
   * @return cropX if the canvas is cropped, otherwise 0
   */
  public int getCropX() {
    if (isCanvasCropped()) {
      return cropX;
    }
    return 0;
  }

  /**
   * @return cropY if the canvas is cropped, otherwise 0
   */
  public int getSavedCropY() {
    return cropY;
  }

  public int getCropY() {
    if (isCanvasCropped()) {
      return cropY;
    }
    return 0;
  }

  public boolean isCanvasCropped() {
    return cropWidth != 0 && cropHeight != 0;
  }

  /**
   * @return true if buffers have to be reinitialized
   */
  public synchronized boolean setSize(int width, int height) {
    int newWidth = Math.max(MIN_CANVAS_WIDTH, width);
    int newHeight = Math.max(MIN_CANVAS_HEIGHT, height);
    if (newWidth != this.width || newHeight != this.height) {
      this.width = newWidth;
      this.height = newHeight;
      return true;
    }
    return false;
  }

  /**
   * @return true if something changed
   */
  public synchronized boolean setCropSize(
    int newCropWidth, int newCropHeight,
    int newCropX, int newCropY
  ) {
    if (newCropWidth == 0 || newCropHeight == 0) {
      // Crop disabled
      newCropWidth = 0;
      newCropHeight = 0;
      newCropX = 0;
      newCropY = 0;
    } else {
      // Crop enabled
      newCropWidth = Math.max(width, newCropWidth);
      newCropHeight = Math.max(height, newCropHeight);
      newCropX = QuickMath.clamp(newCropX, 0, newCropWidth - width);
      newCropY = QuickMath.clamp(newCropY, 0, newCropHeight - height);
    }
    if (newCropWidth != this.cropWidth || newCropHeight != this.cropHeight ||
      newCropX != this.cropX || newCropY != this.cropY) {
      this.cropWidth = newCropWidth;
      this.cropHeight = newCropHeight;
      this.cropX = newCropX;
      this.cropY = newCropY;
      return true;
    }
    return false;
  }

  public int getPixelCount() {
    return width * height;
  }

  @Override
  public void storeConfiguration(JsonObject json) {
    json.add("width", width);
    json.add("height", height);

    // TODO: rename
    json.add("fullWidth", cropWidth);
    json.add("fullHeight", cropHeight);

    json.add("cropX", cropX);
    json.add("cropY", cropY);
  }

  @Override
  public void loadConfiguration(JsonObject json) {
    width = json.get("width").intValue(width);
    height = json.get("height").intValue(height);

    // TODO: rename
    cropWidth = json.get("fullWidth").intValue(cropWidth);
    cropHeight = json.get("fullHeight").intValue(cropHeight);

    cropX = json.get("cropX").intValue(cropX);
    cropY = json.get("cropY").intValue(cropY);
  }
}
