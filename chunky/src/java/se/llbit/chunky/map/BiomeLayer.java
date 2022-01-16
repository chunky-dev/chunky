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

import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.Chunk;
import se.llbit.math.ColorUtil;

public class BiomeLayer extends BitmapLayer {

  private final Biome[] biomes;
  private final int avgColor;

  /**
   * Load biome IDs into layer.
   */
  public BiomeLayer(ChunkData chunkData, BiomePalette biomePalette) {
    biomes = new Biome[Chunk.X_MAX * Chunk.Z_MAX];
    double[] sum = new double[3];
    double[] rgb = new double[3];
    for(int x = 0; x < Chunk.X_MAX; x++) {
      for(int z = 0; z < Chunk.Z_MAX; z++) {
        Biome biome = biomePalette.get(chunkData.getBiomeAt(x, 0, z) & 0xff);
        biomes[Chunk.chunkXZIndex(x, z)] = biome;
        ColorUtil.getRGBComponents(biome.mapColor, rgb);
        sum[0] += rgb[0];
        sum[1] += rgb[1];
        sum[2] += rgb[2];
      }
    }
    sum[0] /= biomes.length;
    sum[1] /= biomes.length;
    sum[2] /= biomes.length;
    avgColor = ColorUtil.getRGB(sum);
  }

  @Override public int colorAt(int x, int z) {
    return biomes[Chunk.chunkXZIndex(x, z)].mapColor;
  }

  public String biomeAt(int x, int z) {
    return biomes[Chunk.chunkXZIndex(x, z)].name;
  }

  @Override public int getAvgColor() {
    return avgColor;
  }
}
