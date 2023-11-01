/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021-2022 Chunky contributors
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
package se.llbit.chunky.renderer.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import se.llbit.chunky.renderer.scene.AlphaBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.imageformats.tiff.CompressionType;
import se.llbit.imageformats.tiff.TiffFileWriter;
import se.llbit.util.TaskTracker;

/**
 * TIFF with 32-bit floating point RGB channels.
 */
public class Tiff32ExportFormat implements PictureExportFormat {

  @Override
  public String getName() {
    return "TIFF_32";
  }

  @Override
  public String getDescription() {
    return "TIFF, 32-bit floating point";
  }

  @Override
  public String getExtension() {
    return ".tiff";
  }

  @Override
  public AlphaBuffer.Type getTransparencyType() {
    return AlphaBuffer.Type.FP32;
  }

  @Override
  public boolean wantsPostprocessing() {
    return false;
  }

  @Override
  public void write(OutputStream out, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Writing TIFF")) {
      if (out instanceof FileOutputStream) {
        write(((FileOutputStream) out).getChannel(), scene, task);
      } else {
        // fallback for the case, that the output stream was not created on a file
        Path tempFile = Files.createTempFile(scene.name + "-", getExtension());
        try (FileChannel fileChannel = FileChannel.open(tempFile, StandardOpenOption.DELETE_ON_CLOSE,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.READ, StandardOpenOption.WRITE
        )) {
          write(fileChannel, scene, task);
          // rewind channel
          fileChannel.position(0);
          try (InputStream inputStream = Channels.newInputStream(fileChannel)) {
            // copy temp file to output
            inputStream.transferTo(out);
          }
        }
      }
    }
  }

  /**
   * Note: does not (!) close the file channel after writing
   */
  private void write(FileChannel fileChannel, Scene scene, TaskTracker.Task task) throws IOException {
    TiffFileWriter writer = new TiffFileWriter(
      fileChannel,
      CompressionType.DEFLATE
    );
    writer.export(scene, task);
    writer.doFinalization();
  }
}
