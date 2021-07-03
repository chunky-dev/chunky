package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.util.TaskTracker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Base class for post processing filter that process each pixel independently
 */
public abstract class SimplePixelPostProcessingFilter implements PixelPostProcessingFilter {
  /**
   * Post process a single pixel
   * @param pixel the rgb component of the pixel. Input/output parameter.
   *              The exposure has already been applied
   */
  public abstract void processPixel(double[] pixel);

  @Override
  public void processFrame(int width, int height, double[] input, BitmapImage output, double exposure, TaskTracker.Task task) {
    task.update(height, 0);
    AtomicInteger done = new AtomicInteger(0);
    Chunky.getCommonThreads().submit(() -> {
      IntStream.range(0, height).parallel().forEach(y -> {
        double[] pixelBuffer = new double[3];

        int rowOffset = y * width;
        for (int x = 0; x < width; x++) {
          int pixelOffset = (rowOffset + x) * 3;
          for(int i = 0; i < 3; ++i) {
            pixelBuffer[i] = input[pixelOffset + i] * exposure;
          }
          processPixel(pixelBuffer);
          for(int i = 0; i < 3; ++i) {
            pixelBuffer[i] = Math.min(1.0, pixelBuffer[i]);
          }
          output.setPixel(x, y, ColorUtil.getRGB(pixelBuffer));
        }

        task.update(height, done.incrementAndGet());
      });
    }).join();
  }

  @Override
  public void processPixel(int width, int height, double[] input, int x, int y, double exposure, double[] output) {
    int index = (y * width + x) * 3;
    for(int i = 0; i < 3; ++i)
      output[i] = input[index + i] * exposure;
    processPixel(output);
  }
}
