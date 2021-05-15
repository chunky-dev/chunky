package se.llbit.chunky.renderer.postprecessing;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.util.TaskTracker;

@PluginApi
public interface PostProcessingFilter {
  /**
   * Post process the entire frame
   * @param width The width of the image
   * @param height The height of the image
   * @param input The input linear image as double array
   * @param output The output image
   * @param exposure The exposure value
   * @param task Task
   */
  void processFrame(int width, int height, double[] input, BitmapImage output, double exposure, TaskTracker.Task task);

  /**
   * Post process a single pixel
   * @param pixel the rgb component of the pixel. Input/output parameter
   */
  void processPixel(double[] pixel);

  /**
   * Get name of the post processing filter
   * @return The name of the post processing filter
   */
  String getName();

  /**
   * Get description of the post processing filter
   * @return The description of the post processing filter
   */
  default String getDescription() {
    return null;
  }

  /**
   * Get id of the post processing filter
   * @return The id of the post processing filter
   */
  String getId();
}
