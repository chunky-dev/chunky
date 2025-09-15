package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;

/**
 * Post-processing filter that supports processing one pixel at a time.
 */
@PluginApi
public abstract class PixelPostProcessingFilter extends PostProcessingFilter {
  /**
   * Post process a single pixel
   * @param width The width of the image
   * @param height The height of the image
   * @param input The input linear image as double array
   * @param x The x position of the pixel to process
   * @param y The y position of the pixel to process
   * @param output The output buffer for the processed pixel
   */
  public abstract void processPixel(int width, int height, double[] input, int x, int y,
      double[] output);

  /**
   * Post-process a single pixel after exposure has been applied
   * @param pixel Input/Output - the rgb component of the pixel with already applied exposure.
   *              Will also be clamped afterwards.
   */
  public abstract void processPixel(double[] pixel);
}
