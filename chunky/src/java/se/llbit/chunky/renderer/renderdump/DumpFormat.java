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
import java.util.SortedMap;
import java.util.TreeMap;

abstract class DumpFormat {
  private static final SortedMap<Integer, DumpFormat> DUMP_FORMATS = new TreeMap<>();
  public static final DumpFormat CLASSIC_DUMP_FORMAT;
  public static final DumpFormat DEFAULT_DUMP_FORMAT;

  static {
    CLASSIC_DUMP_FORMAT = ClassicDumpFormat.INSTANCE;
    registerDumpFormat(CLASSIC_DUMP_FORMAT);
    registerDumpFormat(CompressedFloatDumpFormat.INSTANCE);
    DEFAULT_DUMP_FORMAT = getNewestDumpFormat();
  }

  public static void registerDumpFormat(DumpFormat format) {
    DUMP_FORMATS.put(format.getVersion(), format);
  }

  public static DumpFormat getDumpFormatForVersion(int dumpVersion) throws IOException {
    DumpFormat format = DUMP_FORMATS.get(dumpVersion);
    if (format == null) {
      throw new IOException("Unknown dump format version \"" + dumpVersion + "\"");
    }
    return format;
  }

  public static DumpFormat getNewestDumpFormat() {
    return DUMP_FORMATS.get(DUMP_FORMATS.lastKey());
  }

  public abstract int getVersion();

  /**
   * Used in the calculation of the total loading/saving task size
   */
  protected int getHeaderProgressSize(Scene scene) {
    return 1;
  }

  /**
   * Used in the calculation of the total loading/saving task size
   */
  protected int getBodyProgressSize(Scene scene) {
    return 1;
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
      getHeaderProgressSize(scene) + getBodyProgressSize(scene)
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
      getHeaderProgressSize(scene) + getBodyProgressSize(scene)
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
      getHeaderProgressSize(scene) + getBodyProgressSize(scene)
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
