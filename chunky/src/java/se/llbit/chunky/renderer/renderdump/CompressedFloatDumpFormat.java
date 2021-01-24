/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.util.TaskTracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class CompressedFloatDumpFormat extends DumpFormat {

  public static final DumpFormat INSTANCE = new CompressedFloatDumpFormat();

  private CompressedFloatDumpFormat() {
  }

  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  protected int getBodyProgressSize(Scene scene) {
    return scene.width * scene.height;
  }

  @Override
  public void readBody(
    DataInputStream inputStream,
    Scene scene,
    PixelConsumer consumer,
    TaskTracker.Task task
  ) throws IOException {
    double[] buffer = scene.getSampleBuffer();
    FloatingPointCompressor.decompress(
      inputStream,
      buffer.length,
      consumer,
      pixelIndex -> task.update(1 + pixelIndex)
    );
  }

  @Override
  public void writeBody(
    DataOutputStream outputStream,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException {
    FloatingPointCompressor.compress(
      outputStream,
      scene.getSampleBuffer(),
      pixelIndex -> task.update(1 + pixelIndex)
    );
  }
}
