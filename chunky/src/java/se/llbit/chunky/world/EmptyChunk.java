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

import java.awt.Color;
import java.awt.Graphics;

import se.llbit.chunky.map.RenderBuffer;

/**
 * Empty or non-existent chunk.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EmptyChunk extends Chunk {

	/**
	 * Singleton instance
	 */
	public static final EmptyChunk instance = new EmptyChunk();

	@Override
	public boolean isEmpty() {
		return true;
	}

	private EmptyChunk() {
		super(ChunkPosition.get(0, 0), EmptyWorld.instance);
		surface = Layer.corruptLayer;
		caves = Layer.corruptLayer;
		layer = Layer.corruptLayer;
	}

	@Override
	public synchronized void getBlockData(byte[] blocks, byte[] data, byte[] biomes) {
		for (int i = 0; i < X_MAX * Y_MAX * Z_MAX; ++i)
			blocks[i] = 0;

		for (int i = 0; i < X_MAX * Z_MAX; ++i)
			biomes[i] = 0;

		for (int i = 0; i < (X_MAX * Y_MAX * Z_MAX) / 2; ++i)
			data[i] = 0;
	}

	@Override
	protected void renderLayer(RenderBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	@Override
	protected void renderSurface(RenderBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	@Override
	protected void renderCaves(RenderBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	private void renderEmpty(RenderBuffer rbuff, int cx, int cz) {
		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (view.chunkScale == 1) {
			rbuff.setRGB(x0, z0, 0xFFFFFFFF);
		} else {
			int blockScale = view.chunkScale / 16;
			rbuff.fillRect(x0, z0, view.chunkScale, view.chunkScale, 0xFFFFFFFF);

			Graphics g = rbuff.getGraphics();
			g.setColor(Color.black);
			g.drawLine(x0, z0+8*blockScale, x0+8*blockScale, z0+view.chunkScale);
			g.drawLine(x0, z0, x0+view.chunkScale, z0+view.chunkScale);
			g.drawLine(x0+8*blockScale, z0, x0+view.chunkScale, z0+8*blockScale);
		}
	}

	@Override
	public synchronized void reset() {
		// do nothing
	}
}
