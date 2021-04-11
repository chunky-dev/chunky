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

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.Grass;
import se.llbit.chunky.block.GrassBlock;
import se.llbit.chunky.block.Leaves;
import se.llbit.chunky.block.TallGrass;
import se.llbit.chunky.block.Vine;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Heightmap;
import se.llbit.chunky.world.World;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

import java.io.IOException;
import java.io.OutputStream;

public class SurfaceLayer extends BitmapLayer {

  private final int[] bitmap;
  private final int[] topo;
  private int avgColor = 0xFF;

  /**
   * Generate the surface bitmap.
   *
   * @param dim current dimension
   * @param chunkData data for the chunk
   */
  public SurfaceLayer(int dim, ChunkData chunkData, BlockPalette palette, int yMax) {
    bitmap = new int[Chunk.X_MAX * Chunk.Z_MAX];
    topo = new int[Chunk.X_MAX * Chunk.Z_MAX];
    for (int x = 0; x < Chunk.X_MAX; ++x) {
      for (int z = 0; z < Chunk.Z_MAX; ++z) {

        // Find the topmost non-empty block.
        int y = Math.min(chunkData.maxY() - 1, yMax);
        int minY = chunkData.minY();
        for (; y > minY; --y) {
          if (palette.get(chunkData.getBlockAt(x, y, z)) != Air.INSTANCE) {
            break;
          }
        }
        if (dim == -1) {
          // Nether worlds have a ceiling that we want to skip.
          for (; y > minY+1; --y) {
            if (palette.get(chunkData.getBlockAt(x, y, z)) == Air.INSTANCE) {
              break;
            }
          }
          for (; y > minY+1; --y) {
            if (palette.get(chunkData.getBlockAt(x, y, z)) != Air.INSTANCE) {
              break;
            }
          }
        }

        float[] color = new float[4];

        while (y >= minY && color[3] < 1.f) {
          Block block = palette.get(chunkData.getBlockAt(x, y, z));
          float[] blockColor = new float[4];
          ColorUtil.getRGBAComponents(block.texture.getAvgColor(), blockColor);
          int biomeId = 0xFF & chunkData.getBiomeAt(x, 0, z);

          if (block instanceof Leaves) {
            ColorUtil.getRGBComponents(Biomes.getFoliageColor(biomeId), blockColor);
            blockColor[3] = 1.f;// foliage colors don't include alpha
            y -= 1;
          } else if (block instanceof GrassBlock || block instanceof Grass
              || block instanceof TallGrass
              || block instanceof Vine) {
            ColorUtil.getRGBComponents(Biomes.getGrassColor(biomeId), blockColor);
            blockColor[3] = 1.f;// grass colors don't include alpha

            y -= 1;
          } else if (block.name.equals("minecraft:ice")) {
            color = blend(color, blockColor);
            y -= 1;

            for (; y >= minY; --y) {
              Block block1 = palette.get(chunkData.getBlockAt(x, y, z));
              if (block1.opaque) {
                ColorUtil.getRGBAComponents(block.texture.getAvgColor(), blockColor);
                break;
              }
            }
          } else if (block.isWater()) {
            int depth = 1;
            y -= 1;
            for (; y >= minY; --y) {
              Block block1 = palette.get(chunkData.getBlockAt(x, y, z));
              if (!block1.isWater())
                break;
              depth += 1;
            }

            ColorUtil.getRGBAComponents(Biomes.getWaterColor(biomeId), blockColor);
            blockColor[3] = QuickMath.max(.5f, 1.f - depth / 32.f);
          } else {
            if (block.opaque && y > World.SEA_LEVEL+1) {
              float fade = QuickMath.min(0.6f, (y - World.SEA_LEVEL) / 60.f);
              fade = QuickMath.max(0.f, fade);
              blockColor[0] = (1 - fade) * blockColor[0] + fade;
              blockColor[1] = (1 - fade) * blockColor[1] + fade;
              blockColor[2] = (1 - fade) * blockColor[2] + fade;
            }
            y -= 1;
          }

          color = blend(color, blockColor);

          if (block.opaque) {
            break;
          }
        }

        bitmap[x * 16 + z] = ColorUtil.getArgb(color[0], color[1], color[2], color[3]);
        topo[x * 16 + z] = bitmap[x * 16 + z];
      }
    }
    avgColor = avgBitmapColor();
  }

  /**
   * Add topographical gradient to this chunk and calculate average color
   */
  @Override public synchronized void renderTopography(ChunkPosition position, Heightmap heightmap) {

    int cx = position.x * Chunk.X_MAX;
    int cz = position.z * Chunk.Z_MAX;

    float[] rgb = new float[3];
    for (int x = 0; x < 16; ++x) {

      for (int z = 0; z < 16; ++z) {

        ColorUtil.getRGBComponents(bitmap[x * 16 + z], rgb);

        float gradient =
            (heightmap.get(cx + x, cz + z) + heightmap.get(cx + x + 1, cz + z) + heightmap
                .get(cx + x, cz + z + 1) - heightmap.get(cx + x - 1, cz + z) - heightmap
                .get(cx + x, cz + z - 1) - heightmap.get(cx + x - 1, cz + z - 1));
        gradient = (float) ((FastMath.atan(gradient / 15) / (Math.PI / 1.7)) + 1);

        rgb[0] *= gradient;
        rgb[1] *= gradient;
        rgb[2] *= gradient;

        // clip the result
        rgb[0] = QuickMath.max(0.f, rgb[0]);
        rgb[0] = QuickMath.min(1.f, rgb[0]);
        rgb[1] = QuickMath.max(0.f, rgb[1]);
        rgb[1] = QuickMath.min(1.f, rgb[1]);
        rgb[2] = QuickMath.max(0.f, rgb[2]);
        rgb[2] = QuickMath.min(1.f, rgb[2]);

        topo[x * 16 + z] = ColorUtil.getRGB(rgb[0], rgb[1], rgb[2]);
      }
    }
  }

  /**
   * Blend the two argb colors a and b. Result is stored in the array a.
   */
  private static float[] blend(float[] src, float[] dst) {
    float[] out = new float[4];
    out[3] = src[3] + dst[3] * (1 - src[3]);
    out[0] = (src[0] * src[3] + dst[0] * dst[3] * (1 - src[3])) / out[3];
    out[1] = (src[1] * src[3] + dst[1] * dst[3] * (1 - src[3])) / out[3];
    out[2] = (src[2] * src[3] + dst[2] * dst[3] * (1 - src[3])) / out[3];
    return out;
  }

  private int avgBitmapColor() {
    float[] avg = new float[3];
    float[] frgb = new float[3];
    for (int i = 0; i < 16 * 16; ++i) {
      ColorUtil.getRGBComponents(bitmap[i], frgb);
      avg[0] += frgb[0];
      avg[1] += frgb[1];
      avg[2] += frgb[2];
    }
    return ColorUtil.getRGB(avg[0] / (16 * 16), avg[1] / (16 * 16), avg[2] / (16 * 16));
  }

  /**
   * Write a PNG scanline.
   *
   * @throws IOException
   */
  @Override public void writePngLine(int scanline, OutputStream out) throws IOException {
    if (bitmap != null) {
      byte[] rgb = new byte[] {0, 0, 0};
      for (int x = 15; x >= 0; --x) {
        int rgbInt = bitmap[x + scanline * 16];
        rgb[0] = (byte) (rgbInt >> 16);
        rgb[1] = (byte) (rgbInt >> 8);
        rgb[2] = (byte) rgbInt;
        out.write(rgb);
      }
    } else {
      super.writePngLine(scanline, out);
    }
  }

  /**
   * @return The average color of this layer
   */
  @Override public int getAvgColor() {
    return avgColor;
  }

  @Override public int colorAt(int x, int z) {
    return topo[x * 16 + z];
  }
}
