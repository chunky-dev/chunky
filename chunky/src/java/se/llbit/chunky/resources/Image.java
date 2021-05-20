package se.llbit.chunky.resources;

/**
 * Generic image giving read only access
 */
public interface Image {
  /**
   * Get the width of the image
   */
  int getWidth();

  /**
   * Get the height of the image
   */
  int getHeight();

  /**
   * Get the pixel at position (x, y)
   */
  int getPixel(int x, int y);

  /**
   * Get the pixel at the given index. Index is computed as y * width + x
   */
  int getPixel(int index);

  /**
   * Convert the image to a BitmapImage
   */
  BitmapImage asBitmap();
}
