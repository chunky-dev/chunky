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

import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.LongConsumer;

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
    try (TaskTracker.Task task = taskTracker.task("Loading render dump", scene.renderWidth() * scene.renderHeight())) {
      readHeader(inputStream, scene);
      readSamples(inputStream, scene, pixelProgress -> updateTask(task, scene, pixelProgress));
      scene.getSampleBuffer().setGlobalSpp(scene.spp);
    }
  }

  protected void readHeader(DataInputStream inputStream, Scene scene) throws IOException, IllegalStateException {
    int width = inputStream.readInt();
    int height = inputStream.readInt();

    if (width != scene.renderWidth() || height != scene.renderHeight()) {
      throw new IllegalStateException("Scene size does not match dump size");
    }

    scene.spp = inputStream.readInt();
    scene.getSampleBuffer().setGlobalSpp(scene.spp);
    scene.renderTime = inputStream.readLong();
  }

  protected void readSamples(DataInputStream inputStream, Scene scene, LongConsumer pixelProgress) throws IOException {
    SampleBuffer buffer = scene.getSampleBuffer();
    PixelConsumer px = (pixelIndex, r, g, b) -> {
      long index = 3 * pixelIndex;
      buffer.set(index + 0, r);
      buffer.set(index + 1, g);
      buffer.set(index + 2, b);
    };

    readSamples(inputStream, scene, px, pixelProgress);
  }

  //  protected void readSpp(DataInputStream inputStream, Scene scene, LongConsumer pixelProgress) throws IOException {
  //    SampleBuffer buffer = scene.getSampleBuffer();
  //    readSpp(inputStream, scene, buffer::setSpp, pixelProgress);
  //  }
  //
  //  protected void readSpp(DataInputStream inputStream,
  //                         Scene scene,
  //                         BiConsumer<Long, Integer> sppConsumer,
  //                         LongConsumer pixelProgress)
  //      throws IOException {
  //    throw new IllegalStateException("This dump format has not implemented an SPP processor.");
  //  }
  //
  //
  //
  public void merge(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    try (TaskTracker.Task task = taskTracker.task("Merging render dump", scene.renderWidth() * scene.renderHeight())) {
      if (scene.getSampleBuffer().width != scene.width || scene.getSampleBuffer().height != scene.height) {
        throw new Error("Failed to merge render dump - wrong canvas size.");
      }

      int previousSpp = scene.spp;
      long previousRenderTime = scene.renderTime;

      readHeader(inputStream, scene);
      mergeSamples(inputStream, scene, progress -> task.update((int) progress)); // TODO fix task progress for long indexes
      scene.spp += previousSpp;
      scene.renderTime += previousRenderTime;
    }
  }

  protected void mergeSamples(DataInputStream inputStream, Scene scene, LongConsumer pixelProgress)
      throws IOException {
    int dumpSpp = scene.spp;
    SampleBuffer buffer = scene.getSampleBuffer();
    PixelConsumer px = (pixelIndex, r, g, b) -> buffer.mergeSamples(pixelIndex, dumpSpp, r, g, b);
    readSamples(inputStream, scene, px, pixelProgress);
  }

  protected abstract void readSamples(DataInputStream inputStream,
                                      Scene scene,
                                      PixelConsumer consumer,
                                      LongConsumer pixelProgress)
      throws IOException;

  public void save(DataOutputStream outputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Saving render dump", scene.renderWidth() * scene.renderHeight())) {
      writeHeader(outputStream, scene);
      writeSamples(outputStream, scene, pixelProgress -> updateTask(task, scene, (int) pixelProgress));
      outputStream.flush();
    }
  }

  protected void writeHeader(DataOutputStream outputStream, Scene scene) throws IOException {
    outputStream.writeInt(scene.renderWidth());
    outputStream.writeInt(scene.renderHeight());
    outputStream.writeInt(scene.spp);
    outputStream.writeLong(scene.renderTime);
  }

  protected abstract void writeSamples(DataOutputStream outputStream, Scene scene, LongConsumer pixelProgress)
      throws IOException;

  protected void updateTask(TaskTracker.Task task, Scene scene, long pixelProgress) {

    if (((long) scene.renderWidth()) * scene.renderHeight() <= Integer.MAX_VALUE) {
      int x = scene.renderWidth() * scene.renderHeight() / 100;
      // reduce number of update calls (performance reasons)
      // this results in steps of 1% progress each
      if (pixelProgress % x == 0) {
        task.update((int) pixelProgress);
      }
    } else {
      // If larger than int max, give .1% progress updates as 1/1000 instead of out of full value (would overflow)
      long x = ((long) scene.renderWidth()) * scene.renderHeight() / 1000;
      if (pixelProgress % x == 0)
        task.update((int) (pixelProgress / x));
    }
  }
}
