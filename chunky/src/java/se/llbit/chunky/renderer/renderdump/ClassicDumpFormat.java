/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.renderdump;

import se.llbit.chunky.renderer.scene.Scene;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;

class ClassicDumpFormat extends DumpFormat {

  public static final DumpFormat INSTANCE = new ClassicDumpFormat();

  private ClassicDumpFormat() {
  }

  @Override
  public void readSamples(DataInputStream inputStream, Scene scene, PixelConsumer consumer, IntConsumer pixelProgress)
      throws IOException {
    int pixelIndex, progress = 0;
    double r, g, b;
    // Warning: This format writes in columns instead of rows
    for (int x = 0; x < scene.width; ++x) {
      for (int y = 0; y < scene.height; ++y) {
        pixelIndex = y * scene.width + x;
        r = inputStream.readDouble();
        g = inputStream.readDouble();
        b = inputStream.readDouble();
        consumer.consume(pixelIndex, r, g, b);
        pixelProgress.accept(progress++);
      }
    }
  }

  @Override
  public void writeSamples(DataOutputStream outputStream, Scene scene, IntConsumer pixelProgress) throws IOException {
    double[] samples = scene.getSampleBuffer();
    int pixelIndex, index;
    // Warning: This format writes in columns instead of rows
    for (int x = 0; x < scene.width; ++x) {
      for (int y = 0; y < scene.height; ++y) {
        pixelIndex = (y * scene.width + x);
        index = pixelIndex * 3;
        outputStream.writeDouble(samples[index]);
        outputStream.writeDouble(samples[index + 1]);
        outputStream.writeDouble(samples[index + 2]);
        pixelProgress.accept(pixelIndex);
      }
    }
  }
}
