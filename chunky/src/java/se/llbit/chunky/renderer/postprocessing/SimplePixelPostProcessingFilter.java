/* Copyright (c) 2012-2022 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
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
   * Post-process a single channel of a single pixel
   * @param pixel Input/Output - the rgb component of the pixel with already applied exposure.
   *              Will also be clamped afterwards.
   */
  public abstract void processPixel(double[] pixel);

  @Override
  public void processFrame(
    int width, int height,
    double[] input, BitmapImage output,
    double exposure, TaskTracker.Task task
  ) {
    task.update(height, 0);
    AtomicInteger done = new AtomicInteger(0);
    Chunky.getCommonThreads()
      .submit(() ->
        // do rows in parallel
        IntStream.range(0, height).parallel()
        .forEach(y -> {
          double[] pixelBuffer = new double[3];

          int rowOffset = y * width;
          // columns will be processed sequential
          // TODO: SIMD support once Vector API is finalized
          for (int x = 0; x < width; x++) {
            int pixelOffset = (rowOffset + x) * 3;
            for(int i = 0; i < 3; ++i) {
              pixelBuffer[i] = input[pixelOffset + i] * exposure;
            }
            processPixel(pixelBuffer);
            for(int i = 0; i < 3; ++i) {
              // TODO: extract clamping into own interface
              pixelBuffer[i] = Math.min(1.0, pixelBuffer[i]);
            }
            output.setPixel(x, y, ColorUtil.getRGB(pixelBuffer));
          }

          task.update(height, done.incrementAndGet());
        })
      ).join();
  }

  @Override
  public void processPixel(
    int width, int height,
    double[] input,
    int x, int y,
    double exposure,
    double[] output
  ) {
    int index = (y * width + x) * 3;
    for(int i = 0; i < 3; ++i) {
      output[i] = input[index + i] * exposure;
    }
    processPixel(output);
  }
}
