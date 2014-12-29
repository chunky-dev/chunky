/* Copyright (c) 2010-2014 Jesper Öqvist <jesper@llbit.se>
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
import java.util.Collection;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.CorruptLayer;
import se.llbit.chunky.map.MapBuffer;
import se.llbit.nbt.CompoundTag;

/**
 * Empty or non-existent chunk.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EmptyRegionChunk extends Chunk {

	/**
	 * Singleton instance
	 */
	public static final EmptyRegionChunk INSTANCE = new EmptyRegionChunk();

	private static final int COLOR = 0xFFEEEEEE;

	@Override
	public boolean isEmpty() {
		return true;
	}

	private EmptyRegionChunk() {
		super(ChunkPosition.get(0, 0), EmptyWorld.instance);
		surface = CorruptLayer.INSTANCE;
		caves = CorruptLayer.INSTANCE;
		layer = CorruptLayer.INSTANCE;
	}

	@Override
	public synchronized void getBlockData(byte[] blocks, byte[] data,
			byte[] biomes, Collection<CompoundTag> tileEntities,
			Collection<CompoundTag> entities) {
		for (int i = 0; i < X_MAX * Y_MAX * Z_MAX; ++i)
			blocks[i] = 0;

		for (int i = 0; i < X_MAX * Z_MAX; ++i)
			biomes[i] = 0;

		for (int i = 0; i < (X_MAX * Y_MAX * Z_MAX) / 2; ++i)
			data[i] = 0;
	}

	@Override
	protected void renderLayer(MapBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	@Override
	protected void renderSurface(MapBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	@Override
	protected void renderCaves(MapBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	@Override
	protected void renderBiomes(MapBuffer rbuff, int cx, int cz) {
		renderEmpty(rbuff, cx, cz);
	}

	private void renderEmpty(MapBuffer rbuff, int cx, int cz) {
		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.px0);
		int z0 = view.chunkScale * (cz - view.pz0);

		if (view.chunkScale == 1) {
			rbuff.setRGB(x0, z0, COLOR);
		} else {
			int blockScale = view.chunkScale / 16;
			rbuff.fillRect(x0, z0, view.chunkScale, view.chunkScale, COLOR);

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

	@Override
	public synchronized void loadChunk(Chunky chunky) {
		// do nothing
	}

	@Override
	public String toString() {
		return "Chunk: [empty region]";
	}
}
