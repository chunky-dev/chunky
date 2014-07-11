/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import javax.swing.SwingUtilities;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Chunky;

/**
 * Monitors filesystem for changes to region files.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RegionChangeMonitor extends Thread {
	private final Chunky chunky;
	private volatile ChunkView view = new ChunkView(0, 0, 0, 0);

	public RegionChangeMonitor(Chunky chunky) {
		super("Region Refresher");
		this.chunky = chunky;
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				sleep(3000);
				final World world = chunky.getWorld();
				if (world.loadAdditionalData(true)) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							chunky.getControls().setPlayerY(world.playerLocY());
						}
					});
					if (PersistentSettings.getFollowPlayer()) {
						chunky.goToPlayer();
					}
					chunky.getMap().repaint();
					chunky.getMinimap().repaint();
				}
				ChunkView theView = view;
				for (int rx = theView.prx0; rx <= theView.prx1; ++rx) {
					for (int rz = theView.prz0; rz <= theView.prz1; ++rz) {
						Region region = world.getRegion(ChunkPosition.get(rx, rz));
						if (region.isEmpty()) {
							ChunkPosition pos = ChunkPosition.get(rx, rz);
							if (world.regionExists(pos)) {
								region = new Region(pos, world);
							}
							world.setRegion(pos, region);
							region.parse();
							world.regionDiscovered(pos);
							chunky.regionUpdated(pos);
						} else if (region.hasChanged()) {
							region.parse();
							ChunkPosition pos = region.getPosition();
							chunky.regionUpdated(pos);
						}
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}

	public void setView(ChunkView view) {
		this.view = view;
	}

}
