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

import java.awt.Graphics;
import java.awt.image.DataBufferInt;

import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkView;

/**
 * A layer with block data.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class BlockLayer extends AbstractLayer {
	private final byte[] blocks;

	/**
	 * Load layer from block data
	 * @param blockData
	 * @param layer
	 */
	public BlockLayer(byte[] blockData, int layer) {
		byte[] data = new byte[16*16];
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				data[x*16+z] = blockData[Chunk.chunkIndex(x, layer, z)];
			}
		}
		blocks = data;
	}

	/**
	 * Render this layer
	 * @param rbuff
	 * @param cx
	 * @param cz
	 */
	@Override
	public synchronized void render(RenderBuffer rbuff, int cx, int cz) {
		ChunkView view = rbuff.getView();
		int blockScale = view.chunkScale / 16;
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (view.chunkScale == 1) {

		} else if (blockScale == 1) {

			for (int z = 0; z < 16; ++z) {
				int yp = z0 + z;

				for (int x = 0; x < 16; ++x) {
					int xp = x0 + x;

					byte block = blocks[x * 16 + z];
					if (block == Block.AIR.id) {
						rbuff.setRGB(xp, yp, 0xFFFFFFFF);
					} else {
						rbuff.setRGB(xp, yp, Block.get(block).getIcon().getAvgColor());
					}
				}
			}
		} else if (blockScale < 12) {


			for (int z = 0; z < 16; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < 16; ++x) {
					int xp0 = x0 + x * blockScale;

					byte block = blocks[x * 16 + z];
					if (block == Block.AIR.id) {
						rbuff.fillRect(xp0, yp0, blockScale, blockScale, 0xFFFFFFFF);
					} else {
						rbuff.fillRect(xp0, yp0, blockScale, blockScale,
								Block.get(block).getIcon().getAvgColor());
					}
				}
			}
		} else {
			for (int z = 0; z < 16; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < 16; ++x) {
					int xp0 = x0 + x * blockScale;

					byte block = blocks[x * 16 + z];
					if (block == Block.AIR.id) {
						rbuff.fillRect(xp0, yp0, blockScale, blockScale, 0xFFFFFFFF);
						continue;
					}

					int[] tex = ((DataBufferInt) Block.get(block).getIcon()
							.getScaledImage(blockScale).getRaster().getDataBuffer()).getData();
					for (int i = 0; i < blockScale; ++i) {
						for (int j = 0; j < blockScale; ++j) {
							rbuff.setRGB(xp0 + j, yp0 + i, tex[j + blockScale * i]);
						}
					}
				}
			}
		}
	}


	/**
	 * Render block highlight
	 * @param rbuff
	 * @param cx
	 * @param cz
	 * @param hlBlock
	 * @param highlight
	 */
	@Override
	public synchronized void renderHighlight(RenderBuffer rbuff, int cx, int cz,
			Block hlBlock, java.awt.Color highlight) {

		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.ix0);
		int z0 = view.chunkScale * (cz - view.iz0);

		if (blocks == null)
			return;

		Graphics g = rbuff.getGraphics();
		g.setColor(new java.awt.Color(1,1,1,0.35f));
		g.fillRect(x0, z0, view.chunkScale, view.chunkScale);
		g.setColor(highlight);

		if (view.chunkScale == 16) {

			for (int x = 0; x < 16; ++x) {
				int xp = x0 + x;

				for (int z = 0; z < 16; ++z) {
					int yp = z0 + z;

					if (hlBlock.id == (0xFF & blocks[x * 16 + z])) {
						rbuff.setRGB(xp, yp, highlight.getRGB());
					}
				}
			}
		} else {
			int blockScale = view.chunkScale / 16;

			for (int x = 0; x < 16; ++x) {
				int xp0 = x0 + x * blockScale;
				int xp1 = xp0 + blockScale;

				for (int z = 0; z < 16; ++z) {
					int yp0 = z0 + z * blockScale;
					int yp1 = yp0 + blockScale;

					if (hlBlock.id == (0xFF & blocks[x * 16 + z])) {
						g.fillRect(xp0, yp0, xp1 - xp0, yp1 - yp0);
					}
				}
			}
		}
	}

}
