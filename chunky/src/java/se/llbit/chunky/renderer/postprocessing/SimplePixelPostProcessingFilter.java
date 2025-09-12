/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2022 Chunky contributors
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

import java.util.stream.IntStream;

/**
 * Base class for postprocessing filter that process each pixel independently
 */
public abstract class SimplePixelPostProcessingFilter extends PixelPostProcessingFilter {
  @Override
  public void processFrame(int width, int height, double[] sampleBuffer) {
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
                    System.arraycopy(sampleBuffer, pixelOffset, pixelBuffer, 0, 3);
                    processPixel(pixelBuffer);
                    System.arraycopy(pixelBuffer, 0, sampleBuffer, pixelOffset, 3);
                  }
                })
        ).join();
  }

  @Override
  public final void processPixel(
      int width, int height,
      double[] input,
      int x, int y,
      double[] output
  ) {
    int index = (y * width + x) * 3;
    System.arraycopy(input, index, output, 0, 3);
    processPixel(output);
  }
}
