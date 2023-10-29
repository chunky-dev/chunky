/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2015-2022 Chunky contributors
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
package se.llbit.imageformats.tiff;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import se.llbit.chunky.main.Version;
import se.llbit.chunky.renderer.postprocessing.PixelPostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

/**
 * Basic writer for the TIFF image format.
 * <p>Supports (only) 32-bit floating point channel output.
 * See the <a href="https://download.osgeo.org/libtiff/doc/TIFF6.pdf">format specification</a> for details.
 */
public class TiffFileWriter implements AutoCloseable {

  private final FinalizableBFCOutputStream out;
  private final CompressionType compressionType;

  public TiffFileWriter(
    FileChannel fileChannel,
    CompressionType compressionType
  ) throws IOException {
    this.compressionType = compressionType;
    out = new FinalizableBFCOutputStream(fileChannel);
    // "MM\0*"
    // - MM -> magic bytes
    // - \0* -> magic number 42 for big-endian byte order
    out.writeInt(0x4D4D002A);
  }

  public TiffFileWriter(FileOutputStream outputStream) throws IOException {
    this(outputStream.getChannel(), CompressionType.NONE);
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

  /**
   * Export sample buffer as Baseline TIFF RGB image / TIFF Class R image
   * with 32 bits per color component.
   */
  public void export(Scene scene, TaskTracker.Task task) throws IOException {
    FinalizableBFCOutputStream.UnfinalizedData.Int ifdOffset = out.writeUnfinalizedInt();

    writePrimaryIDF(ifdOffset, scene, task);
  }

  private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

  private static final int BYTES_PER_SAMPLE = 4;
  private FinalizableBFCOutputStream.UnfinalizedData.Int writePrimaryIDF(
    FinalizableBFCOutputStream.UnfinalizedData.Int ifdOffset,
    Scene scene,
    TaskTracker.Task task
  ) throws IOException {
    int width = scene.canvasWidth();
    int height = scene.canvasHeight();

    BasicIFD idf = new BasicIFD(width, height, compressionType);

    // Number of components per pixel (R, G, B)
    idf.addTag(IFDTag.TAG_SAMPLES_PER_PIXEL, (short) 3);
    short bitsPerSample = (short) (8 * BYTES_PER_SAMPLE);
    idf.addMultiTag(IFDTag.TAG_BITS_PER_SAMPLE, new short[]{ bitsPerSample, bitsPerSample, bitsPerSample });
    // Interpret each component as IEEE754 float32
    idf.addMultiTag(IFDTag.TAG_SAMPLE_FORMAT, new short[]{ 3, 3, 3 });

    idf.addTag(IFDTag.TAG_SOFTWARE, "Chunky " + Version.getVersion());
    idf.addTag(IFDTag.TAG_DATETIME, DATETIME_FORMAT.format(LocalDateTime.now()));

    return idf.write(out, ifdOffset, (out) -> {
        PixelPostProcessingFilter filter = requirePixelPostProcessingFilter(scene);
        double[] sampleBuffer = scene.getSampleBuffer();
        double[] pixelBuffer = new double[3];
        for (int y = 0; y < height; ++y) {
          task.update(height, y);
          for (int x = 0; x < width; ++x) {
            // TODO: refactor pixel access to remove duplicate post processing code from here
            filter.processPixel(width, height, sampleBuffer, x, y, scene.getExposure(), pixelBuffer);
            out.writeFloat((float) pixelBuffer[0]);
            out.writeFloat((float) pixelBuffer[1]);
            out.writeFloat((float) pixelBuffer[2]);
          }
        }
        task.update(height, height);
      }
    );
  }

  private PixelPostProcessingFilter requirePixelPostProcessingFilter(Scene scene) {
    PostProcessingFilter filter = scene.getPostProcessingFilter();
    if (filter instanceof PixelPostProcessingFilter) {
      // TODO: use https://openjdk.java.net/jeps/394
      return (PixelPostProcessingFilter) filter;
    } else {
      Log.warn("The selected post processing filter (" + filter.getName()
        + ") doesn't support pixel based processing and can't be used to export TIFF files. " +
        "The TIFF will be exported without post-processing instead.");
      return PostProcessingFilters.NONE;
    }
  }
}
