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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is the legacy dump format for <= Chunky 2.2.
 * <p>
 * The format is a GZIP stream containing some canvas information followed by the render dump
 * written in column major order.
 * <p>
 * Note: Despite implementing {@code DumpFormat}, it does not use the Chunky render dump container format.
 */
public class ClassicDumpFormat extends AbstractDumpFormat {
  public static final ClassicDumpFormat INSTANCE = new ClassicDumpFormat();

  private ClassicDumpFormat() {}

  @Override
  public int getVersion() {
    return 0;
  }

  @Override
  public String getName() {
    return "Classic";
  }

  @Override
  public String getDescription() {
    return "Legacy dump format from Chunky <= 2.2";
  }

  @Override
  public String getId() {
    return "ClassicDumpFormat";
  }

  @Override
  protected void readSamples(DataInputStream inputStream, Scene scene,
                             PixelConsumer consumer, IntConsumer pixelProgress)
      throws IOException {
    int pixelIndex;
    int done = 0;
    double r, g, b;

    // Warning: This format writes in column major order
    for (int x = 0; x < scene.width; x++) {
      for (int y = 0; y < scene.height; y++) {
        pixelIndex = (y * scene.width + x);
        r = inputStream.readDouble();
        g = inputStream.readDouble();
        b = inputStream.readDouble();
        consumer.consume(pixelIndex, r, g, b);
        pixelProgress.accept(done++);
      }
    }
  }

  @Override
  protected void writeSamples(DataOutputStream outputStream, Scene scene,
                              IntConsumer pixelProgress)
      throws IOException {
    double[] samples = scene.getSampleBuffer();
    int offset;
    int done = 0;

    // Warning: This format writes in column major order
    for (int x = 0; x < scene.width; ++x) {
      for (int y = 0; y < scene.height; ++y) {
        offset = (y * scene.width + x) * 3;
        outputStream.writeDouble(samples[offset + 0]);
        outputStream.writeDouble(samples[offset + 1]);
        outputStream.writeDouble(samples[offset + 2]);
        pixelProgress.accept(done++);
      }
    }
  }

  @Override
  public void load(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    DataInputStream stream = new DataInputStream(new GZIPInputStream(inputStream));
    super.load(stream, scene, taskTracker);
  }

  @Override
  public void save(DataOutputStream outputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    GZIPOutputStream gzipStream = new GZIPOutputStream(outputStream);
    DataOutputStream stream = new DataOutputStream(gzipStream);
    super.save(stream, scene, taskTracker);
    stream.flush();
    gzipStream.finish();
    gzipStream.flush();
  }

  @Override
  public void merge(DataInputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    DataInputStream stream = new DataInputStream(new GZIPInputStream(inputStream));
    super.merge(stream, scene, taskTracker);
  }
}
