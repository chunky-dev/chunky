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

import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Chunk;

public class BiomeLayer extends BitmapLayer {

	byte[] biomes;

	/**
	 * Load biome IDs into layer
	 * @param chunkBiomes
	 */
	public BiomeLayer(byte[] chunkBiomes) {
		biomes = new byte[16*16];
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				//biomes[x*16+z] = Biomes.getColor(0x7F & chunkBiomes[Chunk.chunkXZIndex(x, z)]);
				biomes[x*16+z] = chunkBiomes[Chunk.chunkXZIndex(x, z)];
			}
		}
	}

	@Override
	public int colorAt(int x, int z) {
		return Biomes.getColor(biomes[x*16+z]);
	}

}
