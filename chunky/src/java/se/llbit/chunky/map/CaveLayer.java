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

import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;
import se.llbit.math.Color;
import se.llbit.math.QuickMath;

public class CaveLayer extends BitmapLayer {

	private final byte[] caves;
	private final int avgColor;

	/**
	 * Generate the cave map. Only holes which are large enough to have mobs spawn
	 * in them are counted towards the color of a cave. The color is then determined
	 * by the number of unoccupied blocks beneath the topmost (surface) block.
	 *
	 * The more empty space, the deeper the cave color.
	 *
	 * @param blocksArray
	 * @param heightmap
	 * @return The loaded layer
	 */
	public CaveLayer(byte[] blocksArray, int[] heightmap) {
		caves = new byte[Chunk.X_MAX*Chunk.Z_MAX];
		int luft = 0;
		for (int x = 0; x < Chunk.X_MAX; ++x) {
			for (int z = 0; z < Chunk.Z_MAX; ++z) {
				int y = heightmap[z*16+x];
				y = Math.max(0, y-1);

				// find ground level
				for (; y > 1; --y) {
					int block = blocksArray[Chunk.chunkIndex(x, y, z)] & 0xFF;
					if (Block.get(block).isGroundBlock()) {
						break;
					}
				}

				// find caves
				int luftspalt = 0;
				for (; y > 1; --y) {
					Block block = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
					if (block.isCave()) {
						y -= 1;
						Block block1 = Block.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
						if (block1.isCave()) {
							luftspalt++;
							y -= 1;
						}
					}
				}
				luftspalt = (luftspalt < 64) ? luftspalt : 64;
				caves[x*Chunk.Z_MAX+z] = (byte) luftspalt;
				luft += luftspalt;
			}
		}
		avgColor = color((byte) (luft / (float) (Chunk.X_MAX*Chunk.Z_MAX)));
	}

	@Override
	public int colorAt(int x, int z) {
		return color(caves[x*16+z]);
	}

	private int color(byte luftspalt) {
		if (luftspalt == 0) {
			return 0xFFFFFFFF;
		} else {
			double fade = QuickMath.min(1, (luftspalt*3+5)/64.0);
			return Color.getRGB(1.0-fade, 1.0-fade, 1.0);
		}
	}

	@Override
	public int getAvgColor() {
		return avgColor;
	}

}
