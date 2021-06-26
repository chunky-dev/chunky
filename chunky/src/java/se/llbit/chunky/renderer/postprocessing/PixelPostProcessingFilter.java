package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.SampleBuffer;

/**
 * Post processing filter that supports processing one pixel at a time.
 */
@PluginApi
public interface PixelPostProcessingFilter extends PostProcessingFilter {
  /**
   * Post process a single pixel
   * @param input The input linear image as double array
   * @param x The x position of the pixel to process
   * @param y The y position of the pixel to process
   * @param exposure The exposure value
   * @param output The output buffer for the processed pixel
   */
  void processPixel(SampleBuffer input, int x, int y, double exposure, double[] output);
}
