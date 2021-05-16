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
   * @param input The input linear image as double array
   * @param x The x position of the pixel to process
   * @param y The y position of the pixel to process
   * @param exposure The exposure value
   * @param output The output buffer for the processed pixel
   */
  void processPixel(int width, int height, double[] input, int x, int y, double exposure, double[] output);
}
