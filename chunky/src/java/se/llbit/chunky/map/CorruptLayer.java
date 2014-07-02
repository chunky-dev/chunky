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

import se.llbit.chunky.resources.MiscImages;
import se.llbit.chunky.world.ChunkView;
import se.llbit.util.ImageTools;

/**
 * Represents a chunk with corrupt chunk data.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CorruptLayer extends AbstractLayer {

	/**
	 * Singleton instance.
	 */
	public static final CorruptLayer INSTANCE = new CorruptLayer();

	private CorruptLayer() {
	}

	@Override
	public synchronized void render(MapBuffer rbuff, int cx, int cz) {
		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.px0);
		int z0 = view.chunkScale * (cz - view.pz0);

		if (view.chunkScale == 1) {
			rbuff.setRGB(x0, z0, averageColor);
		} else {
			rbuff.getGraphics().drawImage(MiscImages.corruptLayer,
					x0, z0, view.chunkScale, view.chunkScale, null);
		}
	}

	private final int averageColor = ImageTools.calcAvgColor(MiscImages.corruptLayer);

	@Override
	public int getAvgColor() {
		return averageColor;
	}

}
