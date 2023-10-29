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
import java.io.OutputStream;
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
  public boolean isTransparencySupported() {
    return false;
  }

  @Override
  public void write(OutputStream out, Scene scene, TaskTracker taskTracker) throws IOException {
    assert(out instanceof FileOutputStream);
    try (TaskTracker.Task task = taskTracker.task("Writing TIFF");
        TiffFileWriter writer = new TiffFileWriter(
          ((FileOutputStream) out).getChannel(),
          CompressionType.DEFLATE
        )) {
      writer.export(scene, task);
    }
  }
}
