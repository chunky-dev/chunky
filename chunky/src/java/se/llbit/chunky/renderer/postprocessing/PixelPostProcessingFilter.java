package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;

/**
 * Post processing filter that supports processing one pixel at a time.
 */
@PluginApi
public interface PixelPostProcessingFilter extends PostProcessingFilter {
  /**
   * Post process a single pixel
   * @param width The width of the image
   * @param height The height of the image
   * @param input The input linear image as ReadOnlySampleBufferWrapper
   * @param x The x position of the pixel to process
   * @param y The y position of the pixel to process
   * @param exposure The exposure value
   * @param output The output buffer for the processed pixel (r, g, b)
   */
  void processPixel(
    int width, int height,
    ReadOnlySampleBufferWrapper input,
    int x, int y,
    double exposure,
    double[] output
  );
}
