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
import se.llbit.util.TaskTracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;

/**
 * A dump format reads a render dump from a DataInputStream into the scene/ writes a render dump from the scene into a
 * DataOutputStream.
 * <p>
 * The input stream is expected to <i>not</i> contain the magic number and version number - they should have been read
 * before calling load (logic for that in RenderDump class). The output stream is expected to contain the magic number
 * and version number - they should have been written before calling save (logic for that in RenderDump class).
 * <p>
 * The "header" of a dump typically contains width, height, spp and renderTime and is the same for all currently
 * implemented formats. The strategy for reading/writing the samples (from the buffer in the scene) has to be
 * implemented.
 */
abstract class DumpFormat {

  public void load(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    try (TaskTracker.Task task = taskTracker.task("Loading render dump", scene.canvasWidth() * scene.canvasHeight())) {
      readHeader(inputStream, scene);
      readSamples(inputStream, scene, pixelProgress -> updateTask(task, scene, pixelProgress));
    }
  }

  protected void readHeader(DataInputStream inputStream, Scene scene) throws IOException, IllegalStateException {
    int width = inputStream.readInt();
    int height = inputStream.readInt();

    if (width != scene.canvasWidth() || height != scene.canvasHeight()) {
      throw new IllegalStateException("Scene size does not match dump size");
    }

    scene.spp = inputStream.readInt();
    scene.renderTime = inputStream.readLong();
  }

  protected void readSamples(DataInputStream inputStream, Scene scene, IntConsumer pixelProgress) throws IOException {
    double[] buffer = scene.getSampleBuffer();
    PixelConsumer px = (pixelIndex, r, g, b) -> {
      int index = 3 * pixelIndex;
      buffer[index + 0] = r;
      buffer[index + 1] = g;
      buffer[index + 2] = b;
    };

    readSamples(inputStream, scene, px, pixelProgress);
  }

  public void merge(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    try (TaskTracker.Task task = taskTracker.task("Merging render dump", scene.canvasWidth() * scene.canvasHeight())) {
      int previousSpp = scene.spp;
      long previousRenderTime = scene.renderTime;

      readHeader(inputStream, scene);
      mergeSamples(inputStream, previousSpp, scene, task::update);
      scene.spp += previousSpp;
      scene.renderTime += previousRenderTime;
    }
  }

  protected void mergeSamples(DataInputStream inputStream, int previousSpp, Scene scene, IntConsumer pixelProgress)
      throws IOException {
    int dumpSpp = scene.spp;
    double sa = previousSpp / (double) (previousSpp + dumpSpp);
    double sb = 1 - sa;
    double[] buffer = scene.getSampleBuffer();
    PixelConsumer px = (pixelIndex, r, g, b) -> {
      int index = 3 * pixelIndex;
      buffer[index] = buffer[index] * sa + r * sb;
      buffer[index + 1] = buffer[index + 1] * sa + g * sb;
      buffer[index + 2] = buffer[index + 2] * sa + b * sb;
    };
    readSamples(inputStream, scene, px, pixelProgress);
  }

  protected abstract void readSamples(DataInputStream inputStream,
                                      Scene scene,
                                      PixelConsumer consumer,
                                      IntConsumer pixelProgress)
      throws IOException;

  public void save(DataOutputStream outputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Saving render dump", scene.canvasWidth() * scene.canvasHeight())) {
      writeHeader(outputStream, scene);
      writeSamples(outputStream, scene, pixelProgress -> updateTask(task, scene, pixelProgress));
      outputStream.flush();
    }
  }

  protected void writeHeader(DataOutputStream outputStream, Scene scene) throws IOException {
    outputStream.writeInt(scene.width);
    outputStream.writeInt(scene.height);
    outputStream.writeInt(scene.spp);
    outputStream.writeLong(scene.renderTime);
  }

  protected abstract void writeSamples(DataOutputStream outputStream, Scene scene, IntConsumer pixelProgress)
      throws IOException;

  private void updateTask(TaskTracker.Task task, Scene scene, int pixelProgress) {
    int x = scene.width * scene.height / 100;
    // reduce number of update calls (performance reasons)
    // this results in steps of 1% progress each
    if (pixelProgress % x == 0) {
      task.update(pixelProgress);
    }
  }
}
