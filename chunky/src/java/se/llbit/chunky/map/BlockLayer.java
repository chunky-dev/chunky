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
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;
import se.llbit.math.ColorUtil;

import java.awt.image.DataBufferInt;

/**
 * A layer with block data.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class BlockLayer extends AbstractLayer {
  private final byte[] blocks;
  private final byte[] biomes;
  private final int avgColor;

  /**
   * Load layer from block data.
   */
  public BlockLayer(byte[] blockData, byte[] chunkBiomes, int layer) {
    blocks = new byte[Chunk.X_MAX * Chunk.Z_MAX];
    biomes = new byte[Chunk.X_MAX * Chunk.Z_MAX];
    double[] sum = new double[3];
    double[] rgb = new double[3];
    for (int x = 0; x < Chunk.X_MAX; ++x) {
      for (int z = 0; z < Chunk.Z_MAX; ++z) {
        byte block = blockData[Chunk.chunkIndex(x, layer, z)];
        byte biome = chunkBiomes[Chunk.chunkXZIndex(x, z)];
        blocks[x * Chunk.Z_MAX + z] = block;
        biomes[x * Chunk.Z_MAX + z] = block;
        ColorUtil.getRGBComponents(avgBlockColor(block, biome), rgb);
        sum[0] += rgb[0];
        sum[1] += rgb[1];
        sum[2] += rgb[2];
      }
    }
    sum[0] /= Chunk.X_MAX * Chunk.Z_MAX;
    sum[1] /= Chunk.X_MAX * Chunk.Z_MAX;
    sum[2] /= Chunk.X_MAX * Chunk.Z_MAX;
    avgColor = ColorUtil.getRGB(sum);
  }

  /**
   * Render this layer
   */
  @Override public synchronized void render(MapTile tile) {
    if (tile.scale == 1) {
      tile.setPixel(0, 0, getAvgColor());
    } else if (tile.scale == 16) {
      // TODO convert to using pixel array directly.
      for (int z = 0; z < Chunk.Z_MAX; ++z) {
        for (int x = 0; x < Chunk.X_MAX; ++x) {
          byte block = blocks[x * Chunk.Z_MAX + z];
          byte biome = biomes[x * Chunk.Z_MAX + z];
          tile.setPixel(x, z, avgBlockColor(block, biome));
        }
      }
    } else if (tile.scale == 16 * 16) {
      for (int z = 0; z < Chunk.Z_MAX; ++z) {
        int yp0 = z * 16;

        for (int x = 0; x < Chunk.X_MAX; ++x) {
          int xp0 = x * 16;

          byte block = blocks[x * Chunk.Z_MAX + z];
          if (block == Block.AIR.id) {
            for (int i = 0; i < 16; ++i) {
              for (int j = 0; j < 16; ++j) {
                tile.setPixel(xp0 + j, yp0 + i, 0xFFFFFFFF);
              }
            }
            continue;
          }

          switch ((int) block) {
            case Block.GRASS_ID:
            case Block.TALLGRASS_ID:
            case Block.LEAVES_ID:
            case Block.LEAVES2_ID:
            case Block.VINES_ID: {
              Texture tex = Block.get(block).getIcon();
              for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                  float[] rgb = tex.getColor(j, i);
                  if (rgb[3] != 0) {
                    tile.setPixel(xp0 + j, yp0 + i,
                        getBiomeColor(rgb, block, biomes[x * Chunk.Z_MAX + z]));
                  } else {
                    tile.setPixel(xp0 + j, yp0 + i, 0xFFFFFFFF);
                  }
                }
              }
              break;
            }
            default: {
              int[] tex = Block.get(block).getIcon().getData();
              for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                  int rgb = tex[i * 16 + j];
                  if ((rgb & 0xFF000000) != 0) {
                    tile.setPixel(xp0 + j, yp0 + i, rgb);
                  } else {
                    tile.setPixel(xp0 + j, yp0 + i, 0xFFFFFFFF);
                  }
                }
              }
            }
          }
        }
      }
    } else {
      throw new Error("Unsupported chunk scale.");
    }
  }

  private int avgBlockColor(byte block, byte biome) {
    if (block == Block.AIR.id) {
      return 0xFFFFFFFF;
    } else {
      switch ((int) block) {
        case Block.GRASS_ID:
        case Block.TALLGRASS_ID:
        case Block.LEAVES_ID:
        case Block.LEAVES2_ID:
        case Block.VINES_ID: {
          float[] rgb = Block.get(block).getIcon().getAvgColorLinear();
          return getBiomeColor(rgb, block, biome);
        }
        default:
          return Block.get(block).getIcon().getAvgColor();
      }
    }
  }

  private int getBiomeColor(float[] rgb, byte block, byte biome) {
    float[] biomeColor;
    float alpha = rgb[3];
    switch ((int) block) {
      case Block.GRASS_ID:
      case Block.TALLGRASS_ID:
        biomeColor = Biomes.getGrassColorLinear(biome);
        return ColorUtil.getRGB(
            (1 - alpha) + alpha * FastMath.pow(rgb[0] * biomeColor[0], Scene.DEFAULT_GAMMA_INV),
            (1 - alpha) + alpha * FastMath.pow(rgb[1] * biomeColor[1], Scene.DEFAULT_GAMMA_INV),
            (1 - alpha) + alpha * FastMath.pow(rgb[2] * biomeColor[2], Scene.DEFAULT_GAMMA_INV));
      case Block.LEAVES_ID:
      case Block.LEAVES2_ID:
      case Block.VINES_ID:
        biomeColor = Biomes.getFoliageColorLinear(biome);
        return ColorUtil.getRGB(
            (1 - alpha) + alpha * FastMath.pow(rgb[0] * biomeColor[0], Scene.DEFAULT_GAMMA_INV),
            (1 - alpha) + alpha * FastMath.pow(rgb[1] * biomeColor[1], Scene.DEFAULT_GAMMA_INV),
            (1 - alpha) + alpha * FastMath.pow(rgb[2] * biomeColor[2], Scene.DEFAULT_GAMMA_INV));
      default:
        return ColorUtil.getRGB((1 - alpha) + alpha * FastMath.pow(rgb[0], Scene.DEFAULT_GAMMA_INV),
            (1 - alpha) + alpha * FastMath.pow(rgb[1], Scene.DEFAULT_GAMMA_INV),
            (1 - alpha) + alpha * FastMath.pow(rgb[2], Scene.DEFAULT_GAMMA_INV));
    }
  }

  /**
   * Render block highlight.
   */
  @Override public synchronized void renderHighlight(MapTile tile, Block hlBlock, int hlColor) {

    if (blocks == null) {
      return;
    }

    //g.setColor(new java.awt.Color(1,1,1,0.35f));
    //g.fillRect(x0, z0, view.chunkScale, view.chunkScale);
    //g.setColor(highlight);

    if (tile.scale == 16) {
      for (int z = 0; z < 16; ++z) {
        for (int x = 0; x < 16; ++x) {
          if (hlBlock.id == (0xFF & blocks[x * 16 + z])) {
            tile.setPixel(x, z, hlColor);
          }
        }
      }
    } else if (tile.scale > 16) {
      int blockScale = tile.scale / 16;
      for (int x = 0; x < 16; ++x) {
        for (int z = 0; z < 16; ++z) {
          if (hlBlock.id == (0xFF & blocks[x * 16 + z])) {
            int xp0 = x * blockScale;
            int xp1 = xp0 + blockScale;
            int zp0 = z * blockScale;
            int zp1 = zp0 + blockScale;
            for (int j = zp0; j < zp1; ++j) {
              for (int i = xp0; i < xp1; ++i) {
                if (hlBlock.id == (0xFF & blocks[j * 16 + i])) {
                  tile.setPixel(i, j, hlColor);
                }
              }
            }
          }
        }
      }
    }
  }

  @Override public int getAvgColor() {
    return avgColor;
  }
}
