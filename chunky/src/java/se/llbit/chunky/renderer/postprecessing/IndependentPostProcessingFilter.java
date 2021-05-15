package se.llbit.chunky.renderer.postprecessing;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.util.TaskTracker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Base class for post processing filter that process each pixel independently
 */
public abstract class IndependentPostProcessingFilter implements PostProcessingFilter {


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
          output.setPixel(x, y, ColorUtil.getRGB(pixelBuffer));
        }

        task.update(width, done.incrementAndGet());
      });
    }).join();
  }
}
