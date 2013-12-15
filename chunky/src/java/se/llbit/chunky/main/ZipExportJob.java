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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JOptionPane;

import se.llbit.chunky.ui.ProgressPanel;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;

/**
 * Exports chunks to a Zip file.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ZipExportJob extends Thread {

	private World world;
	private File targetFile;
	private ProgressPanel progress;
	private Collection<ChunkPosition> selected;

	/**
	 * Create a new Zip export job.
	 * @param world
	 * @param selected
	 * @param target
	 * @param progress
	 */
	public ZipExportJob(World world, Collection<ChunkPosition> selected,
			File target, ProgressPanel progress) {
		super("Zip Export Job");

		this.world = world;
		this.selected = selected;
		this.targetFile = target;
		this.progress = progress;
	}

	public void run() {
		if (progress.tryStartJob()) {
			try {
				progress.setJobName("Zip Export");
				if (selected.isEmpty())
					world.exportWorldToZip(targetFile, progress);
				else
					world.exportChunksToZip(targetFile, selected, world.currentDimension(), progress);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Could not write zip file:\n" + e.getMessage());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error while exporting to zip file:\n" + e.getMessage());
			}
			progress.finishJob();
		}
	}
}
