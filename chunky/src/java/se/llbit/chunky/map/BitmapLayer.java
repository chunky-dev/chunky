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
package se.llbit.chunky.map;

import se.llbit.chunky.world.ChunkView;

abstract public class BitmapLayer extends AbstractLayer {

	/**
	 * Render this layer
	 * @param rbuff
	 * @param cx
	 * @param cz
	 */
	@Override
	final public void render(MapBuffer rbuff, int cx, int cz) {
		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (view.chunkScale == 1) {
			rbuff.setRGB(x0, z0, getAvgColor());
		} else if (view.chunkScale == 16) {
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					rbuff.setRGB(x0 + x, z0 + z, colorAt(x, z));
				}
			}
		} else {
			int blockScale = view.chunkScale / 16;
			for (int z = 0; z < 16; ++z) {
				int yp0 = z0 + z * blockScale;
				for (int x = 0; x < 16; ++x) {
					int xp0 = x0 + x * blockScale;
					rbuff.fillRect(xp0, yp0, blockScale, blockScale,
							colorAt(x, z));
				}
			}
		}
	}

	abstract public int colorAt(int x, int z);

}
