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


abstract class AbstractDumpFormat implements DumpFormat {

  /**
   * Read samples from the stream.
   *
   * @param inputStream   Stream to read samples from.
   * @param scene         Scene this dump is a part of. Do not modify.
   * @param consumer      Pixel consumer. Does not need to be in order.
   * @param pixelProgress Progress consumer. Inputs to this must be increasing and end at
   *                      {@code scene.width * scene.height}.
   */
  protected abstract void readSamples(DataInputStream inputStream, Scene scene,
                                      PixelConsumer consumer, IntConsumer pixelProgress)
      throws IOException;

  /**
   * Write sample buffer to the stream.
   *
   * @param outputStream  Stream to write to.
   * @param scene         Scene to take the sample buffer from. Do not modify.
   * @param pixelProgress Progress consumer. Inputs must be increasing and end at
   *                      {@code scene.width * scene.height}.
   */
  protected abstract void writeSamples(DataOutputStream outputStream, Scene scene,
                                       IntConsumer pixelProgress)
      throws IOException;

  public abstract int getVersion();

  public abstract String getName();

  public abstract String getDescription();

  public abstract String getId();

  @Override
  public void load(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    double[] samples = scene.getSampleBuffer();

    try (TaskTracker.Task task = taskTracker.task("Loading render dump", scene.canvasConfig.getPixelCount())) {
      readHeader(inputStream, scene);
      readSamples(inputStream, scene, (index, r, g, b) -> {
        int offset = index * 3;
        samples[offset + 0] = r;
        samples[offset + 1] = g;
        samples[offset + 2] = b;
      }, i -> task.updateInterval(i, scene.canvasConfig.getWidth()));
    }
  }

  @Override
  public void save(DataOutputStream outputStream, Scene scene, TaskTracker taskTracker)
      throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Saving render dump", scene.canvasConfig.getPixelCount())) {
      writeHeader(outputStream, scene);
      writeSamples(outputStream, scene, i -> task.updateInterval(i, scene.canvasConfig.getWidth()));
    }
  }

  @Override
  public void merge(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    try (TaskTracker.Task task = taskTracker.task("Merging render dump", scene.canvasConfig.getPixelCount())) {
      int sceneSpp = scene.spp;
      long previousRenderTime = scene.renderTime;

      double[] samples = scene.getSampleBuffer();

      readHeader(inputStream, scene);

      double dumpSpp = scene.spp;
      double sinv = 1.0 / (sceneSpp + dumpSpp);

      readSamples(inputStream, scene, (index, r, g, b) -> {
        int offset = index * 3;
        samples[offset + 0] = (samples[offset + 0] * sceneSpp + r * dumpSpp) * sinv;
        samples[offset + 1] = (samples[offset + 1] * sceneSpp + g * dumpSpp) * sinv;
        samples[offset + 2] = (samples[offset + 2] * sceneSpp + b * dumpSpp) * sinv;
      }, i -> task.updateInterval(i, scene.canvasConfig.getWidth()));

      scene.spp += sceneSpp;
      scene.renderTime += previousRenderTime;
    }
  }

  protected void readHeader(DataInputStream inputStream, Scene scene) throws IOException, IllegalStateException {
    int width = inputStream.readInt();
    int height = inputStream.readInt();

    if (width != scene.canvasConfig.getWidth() || height != scene.canvasConfig.getHeight()) {
      throw new IllegalStateException("Scene size does not match dump size");
    }

    scene.spp = inputStream.readInt();
    scene.renderTime = inputStream.readLong();
  }

  protected void writeHeader(DataOutputStream outputStream, Scene scene) throws IOException {
    outputStream.writeInt(scene.canvasConfig.getWidth());
    outputStream.writeInt(scene.canvasConfig.getHeight());
    outputStream.writeInt(scene.spp);
    outputStream.writeLong(scene.renderTime);
  }
}
