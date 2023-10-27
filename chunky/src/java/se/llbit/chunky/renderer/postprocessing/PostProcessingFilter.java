package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.util.Registerable;
import se.llbit.util.TaskTracker;

/**
 * A post-processing filter.
 * <p>
 * These filters are used to convert the HDR sample buffer into an SDR image that can be displayed.
 * Exposure is also applied by the filter.
 * <p>
 * Filters that support processing a single pixel at a time should implement {@link
 * PixelPostProcessingFilter} instead.
 */
@PluginApi
public interface PostProcessingFilter extends Registerable {
  /**
   * Post process the entire frame
   * @param width The width of the image
   * @param height The height of the image
   * @param input The input linear image as double array, exposure has not been applied
   * @param output The output image
   * @param exposure The exposure value
   * @param task Task
   */
  void processFrame(int width, int height, double[] input, BitmapImage output, double exposure, TaskTracker.Task task);

  /**
   * Get name of the post processing filter
   * @return The name of the post processing filter
   */
  @Override
  String getName();

  /**
   * Get description of the post processing filter
   * @return The description of the post processing filter
   */
  @Override
  default String getDescription() {
    return null;
  }

  /**
   * Get id of the post processing filter
   * @return The id of the post processing filter
   */
  @Override
  String getId();
}
