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

abstract class DumpFormat {

  /**
   * Used in the calculation of the total loading/saving task size
   */
  protected int getProgressSize(Scene scene) {
    return 1 + scene.width * scene.height;
  }

  /**
   * The input stream is expected to start with the header of the dump,
   * magic number and version number should have been read before calling load.
   */
  public void load(
    DataInputStream inputStream,
    Scene scene,
    TaskTracker taskTracker
  ) throws IOException, IllegalStateException {
    try (TaskTracker.Task task = taskTracker.task(
      "Loading render dump",
      getProgressSize(scene)
    )) {
      readHeader(inputStream, scene, task);
      loadBody(inputStream, scene, task);
    }
  }

  protected void readHeader(
    DataInputStream inputStream,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException, IllegalStateException {
    int width = inputStream.readInt();
    int height = inputStream.readInt();
    if (width != scene.width || height != scene.height) {
      throw new IllegalStateException("Scene size does not match dump size");
    }
    scene.spp = inputStream.readInt();
    scene.renderTime = inputStream.readLong();
    task.update(1);
  }

  protected void loadBody(
    DataInputStream inputStream,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException {
    double[] buffer = scene.getSampleBuffer();
    readBody(
      inputStream,
      scene,
      (pixelIndex, r, g, b) -> {
        int index = 3 * pixelIndex;
        buffer[index]     = r;
        buffer[index + 1] = g;
        buffer[index + 2] = b;
      },
      task
    );
  }

  /**
   * The input stream is expected to start with the header of the dump,
   * magic number and version number should have been read before calling load.
   */
  public void merge(
    DataInputStream inputStream,
    Scene scene,
    TaskTracker taskTracker
  ) throws IOException, IllegalStateException {
    try (TaskTracker.Task task = taskTracker.task(
      "Merging render dump",
      getProgressSize(scene)
    )) {
      int previousSpp = scene.spp;
      long previousRenderTime = scene.renderTime;
      readHeader(inputStream, scene, task);
      mergeBody(inputStream, previousSpp, scene, task);
      scene.spp += previousSpp;
      scene.renderTime += previousRenderTime;
    }
  }

  protected void mergeBody(
    DataInputStream inputStream,
    int previousSpp,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException {
    int dumpSpp = scene.spp;
    double sa = previousSpp / (double) (previousSpp + dumpSpp);
    double sb = 1 - sa;
    double[] buffer = scene.getSampleBuffer();
    readBody(
      inputStream,
      scene,
      (pixelIndex, r, g, b) -> {
        int index = 3 * pixelIndex;
        buffer[index]     = buffer[index]     * sa + r * sb;
        buffer[index + 1] = buffer[index + 1] * sa + g * sb;
        buffer[index + 2] = buffer[index + 2] * sa + b * sb;
      },
      task
    );
  }

  protected abstract void readBody(
    DataInputStream inputStream,
    Scene scene,
    PixelConsumer consumer,
    TaskTracker.Task task
  ) throws IOException;

  /**
   * The output stream is expected to start with the header of the dump,
   * magic number and version number should have been written before calling save.
   */
  public void save(
    DataOutputStream outputStream,
    Scene scene,
    TaskTracker taskTracker
  ) throws IOException {
    try (TaskTracker.Task task = taskTracker.task(
      "Saving render dump",
      getProgressSize(scene)
    )) {
      writeHeader(outputStream, scene, task);
      writeBody(outputStream, scene, task);
      outputStream.flush();
    }
  }

  protected void writeHeader(
    DataOutputStream outputStream,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException {
    outputStream.writeInt(scene.width);
    outputStream.writeInt(scene.height);
    outputStream.writeInt(scene.spp);
    outputStream.writeLong(scene.renderTime);
    task.update(1);
  }

  protected abstract void writeBody(
    DataOutputStream outputStream,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException;
}
