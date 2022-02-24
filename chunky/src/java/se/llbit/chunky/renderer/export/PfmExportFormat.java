/* Copyright (c) 2021-2022 Chunky contributors
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

import java.io.IOException;
import java.io.OutputStream;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.imageformats.pfm.PfmFileWriter;
import se.llbit.util.TaskTracker;

/**
 * Portable float map (PFM) with 32-bit color channels.
 */
public class PfmExportFormat implements PictureExportFormat {

  @Override
  public String getName() {
    return "PFM";
  }

  @Override
  public String getDescription() {
    return "PFM, Portable FloatMap (32-bit)";
  }

  @Override
  public String getExtension() {
    return ".pfm";
  }

  @Override
  public boolean isTransparencySupported() {
    return false;
  }

  @Override
  public void write(OutputStream out, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Writing PFM rows", scene.canvasHeight());
        PfmFileWriter writer = new PfmFileWriter(out)) {
      writer.write(scene, task);
    }
  }
}
