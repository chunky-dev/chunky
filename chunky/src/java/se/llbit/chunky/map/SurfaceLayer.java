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
import se.llbit.chunky.idblock.IdBlock;
import se.llbit.chunky.resources.Texture;
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
   * @param dim         current dimension
   * @param blocksArray block id array
   */
  public SurfaceLayer(int dim, byte[] blocksArray, byte[] biomes, byte[] blockData) {

    bitmap = new int[Chunk.X_MAX * Chunk.Z_MAX];
    topo = new int[Chunk.X_MAX * Chunk.Z_MAX];
    for (int x = 0; x < Chunk.X_MAX; ++x) {
      for (int z = 0; z < Chunk.Z_MAX; ++z) {

        // Find the topmost non-empty block.
        int y = Chunk.Y_MAX - 1;
        if (dim != -1) {
          for (; y > 0; --y) {
            int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
            if (block != IdBlock.AIR.id)
              break;
          }
        } else {
          // nether worlds have a ceiling that we want to skip
          for (; y > 1; --y) {
            int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
            if (block != IdBlock.AIR.id)
              break;
          }
          for (; y > 1; --y) {
            int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
            if (block == IdBlock.AIR.id)
              break;
          }
          for (; y > 1; --y) {
            int block = 0xFF & blocksArray[Chunk.chunkIndex(x, y, z)];
            if (block != IdBlock.AIR.id)
              break;
          }
        }

        float[] color = new float[4];

        for (; y >= 0 && color[3] < 1.f; ) {
          IdBlock block = IdBlock.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
          float[] blockColor = new float[4];
          int biomeId = 0xFF & biomes[Chunk.chunkXZIndex(x, z)];

          int data = 0xFF & blockData[Chunk.chunkIndex(x, y, z) / 2];
          data >>= (x % 2) * 4;
          data &= 0xF;

          switch (block.id) {

            case IdBlock.LEAVES_ID:
            case IdBlock.LEAVES2_ID:
              ColorUtil.getRGBComponents(Biomes.getFoliageColor(biomeId), blockColor);
              blockColor[3] = 1.f;// foliage colors don't include alpha

              y -= 1;
              break;

            case IdBlock.GRASS_ID:
            case IdBlock.VINES_ID:
            case IdBlock.TALLGRASS_ID:
              ColorUtil.getRGBComponents(Biomes.getGrassColor(biomeId), blockColor);
              blockColor[3] = 1.f;// grass colors don't include alpha

              y -= 1;
              break;

            case IdBlock.ICE_ID:
              ColorUtil.getRGBAComponents(block.getTexture(data).getAvgColor(), blockColor);
              color = blend(color, blockColor);
              y -= 1;

              for (; y >= 0; --y) {
                if (IdBlock.get(blocksArray[Chunk.chunkIndex(x, y, z)]).opaque) {
                  ColorUtil.getRGBAComponents(block.getTexture(data).getAvgColor(), blockColor);
                  break;
                }
              }
              break;

            case IdBlock.WATER_ID:
            case IdBlock.STATIONARYWATER_ID:
              int depth = 1;
              y -= 1;
              for (; y >= 0; --y) {
                IdBlock block1 = IdBlock.get(blocksArray[Chunk.chunkIndex(x, y, z)]);
                if (!block1.isWater())
                  break;
                depth += 1;
              }

              ColorUtil.getRGBAComponents(Texture.water.getAvgColor(), blockColor);
              blockColor[3] = QuickMath.max(.5f, 1.f - depth / 32.f);
              break;

            default:
              ColorUtil.getRGBAComponents(block.getTexture(data).getAvgColor(), blockColor);

              if (block.opaque && y > 64) {
                float fade = QuickMath.min(0.6f, (y - World.SEA_LEVEL) / 60.f);
                fade = QuickMath.max(0.f, fade);
                blockColor[0] = (1 - fade) * blockColor[0] + fade;
                blockColor[1] = (1 - fade) * blockColor[1] + fade;
                blockColor[2] = (1 - fade) * blockColor[2] + fade;
              }

              y -= 1;
              break;
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
