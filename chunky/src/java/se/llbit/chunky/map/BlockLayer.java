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
import se.llbit.math.Color;

/**
 * A layer with block data.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class BlockLayer extends AbstractLayer {
	private final byte[] blocks;
	private final int avgColor;

	/**
	 * Load layer from block data
	 * @param blockData
	 * @param chunkBiomes
	 * @param layer
	 */
	public BlockLayer(byte[] blockData, byte[] chunkBiomes, int layer) {
		byte[] data = new byte[Chunk.X_MAX*Chunk.Z_MAX];
		double[] sum = new double[3];
		double[] rgb = new double[3];
		for (int x = 0; x < Chunk.X_MAX; ++x) {
			for (int z = 0; z < Chunk.Z_MAX; ++z) {
				byte block = blockData[Chunk.chunkIndex(x, layer, z)];
				data[x*Chunk.Z_MAX+z] = block;
				Color.getRGBComponents(avgBlockColor(block), rgb);
				sum[0] += rgb[0];
				sum[1] += rgb[1];
				sum[2] += rgb[2];
			}
		}
		blocks = data;
		sum[0] /= Chunk.X_MAX*Chunk.Z_MAX;
		sum[1] /= Chunk.X_MAX*Chunk.Z_MAX;
		sum[2] /= Chunk.X_MAX*Chunk.Z_MAX;
		avgColor = Color.getRGB(sum);
	}

	/**
	 * Render this layer
	 * @param rbuff
	 * @param cx
	 * @param cz
	 */
	@Override
	public synchronized void render(MapBuffer rbuff, int cx, int cz) {
		ChunkView view = rbuff.getView();
		int blockScale = view.chunkScale / 16;
		int x0 = view.chunkScale * (cx - view.px0);
		int z0 = view.chunkScale * (cz - view.pz0);

		if (view.chunkScale == 1) {
			rbuff.setRGB(x0, z0, getAvgColor());
		} else if (blockScale == 1) {

			for (int z = 0; z < Chunk.Z_MAX; ++z) {
				int yp = z0 + z;

				for (int x = 0; x < Chunk.X_MAX; ++x) {
					int xp = x0 + x;

					byte block = blocks[x*Chunk.Z_MAX + z];
					rbuff.setRGB(xp, yp, avgBlockColor(block));
				}
			}
		} else if (blockScale < 12) {


			for (int z = 0; z < Chunk.Z_MAX; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < Chunk.X_MAX; ++x) {
					int xp0 = x0 + x * blockScale;

					byte block = blocks[x*Chunk.Z_MAX + z];
					rbuff.fillRect(xp0, yp0, blockScale, blockScale, avgBlockColor(block));
				}
			}
		} else {
			for (int z = 0; z < Chunk.Z_MAX; ++z) {
				int yp0 = z0 + z * blockScale;

				for (int x = 0; x < Chunk.X_MAX; ++x) {
					int xp0 = x0 + x * blockScale;

					byte block = blocks[x*Chunk.Z_MAX + z];
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

	private int avgBlockColor(byte block) {
		if (block == Block.AIR.id) {
			return 0xFFFFFFFF;
		} else {
			return Block.get(block).getIcon().getAvgColor();
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
	public synchronized void renderHighlight(MapBuffer rbuff, int cx, int cz,
			Block hlBlock, java.awt.Color highlight) {

		ChunkView view = rbuff.getView();
		int x0 = view.chunkScale * (cx - view.px0);
		int z0 = view.chunkScale * (cz - view.pz0);

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

	@Override
	public int getAvgColor() {
		return avgColor;
	}
}
