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
package se.llbit.chunky.world;
import org.apache.commons.math3.util.FastMath;

import java.util.Collection;

import se.llbit.chunky.ui.ProgressPanel;

/**
 * A job for deleting selected chunks.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class DeleteChunksJob extends Thread {

	private World world;
	private Collection<ChunkPosition> selected;
	private ProgressPanel progress;

	/**
	 * @param world
	 * @param selected
	 * @param progress
	 */
	public DeleteChunksJob(World world, Collection<ChunkPosition> selected,
			ProgressPanel progress) {

		this.world = world;
		this.selected = selected;
		this.progress = progress;
	}

	public void run() {
		if (progress.tryStartJob()) {
			progress.setJobName("Deleting chunks");
			progress.setJobSize(selected.size());
			int ndeleted = 0;
			for (ChunkPosition pos : selected) {
				if (progress.isInterrupted())
					break;
				Region region = world.getRegion(pos.getRegionPosition());
				region.deleteChunk(pos);
				progress.setProgress(++ndeleted);
			}
			progress.finishJob();
		}
	}

}
