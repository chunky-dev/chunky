/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

/**
 * Exports chunks to a Zip file.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ZipExportJob extends Thread {

  private World world;
  private File targetFile;
  private ProgressTracker progress;
  private Collection<ChunkPosition> selected;

  /**
   * Create a new Zip export job.
   */
  public ZipExportJob(World world, Collection<ChunkPosition> selected, File target,
      ProgressTracker progress) {
    super("Zip Export Job");
    this.world = world;
    this.selected = selected;
    this.targetFile = target;
    this.progress = progress;
  }

  @Override public void run() {
    if (progress.tryStartJob()) {
      try {
        progress.setJobName("Zip Export");
        if (selected.isEmpty()) {
          world.exportWorldToZip(targetFile, progress);
        } else {
          world.exportChunksToZip(targetFile, selected, progress);
        }
      } catch (FileNotFoundException e) {
        Log.error("Could not write zip file.", e);
      } catch (IOException e) {
        Log.error("Error while exporting to zip file.", e);
      }
      progress.finishJob();
    }
  }
}
