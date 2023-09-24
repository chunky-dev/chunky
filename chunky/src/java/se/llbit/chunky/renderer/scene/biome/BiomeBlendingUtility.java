package se.llbit.chunky.renderer.scene.biome;

import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.math.Vector3i;
import se.llbit.math.structures.Position2IntStructure;

import java.util.Set;

public class BiomeBlendingUtility {
  public static void addEqual(float[] result, float[] addend) {
    result[0] += addend[0];
    result[1] += addend[1];
    result[2] += addend[2];
  }

  /**
   * Helper class to compute average of subrectangle of a 2D table in O(n²) init time
   * and O(1) use time. Based on summed-area table // TODO link to wikipedia here
   */
  private static class SummedAreaTable {
    // (16+2*r)² Summed area table of 9 entries vector (3 components for 3 colors)
    float[] samData;
    // Other summed area table to count the number of summed color to get denominator for average
    int[] samN;
    final int blurRadius;
    final int xStride;

    public SummedAreaTable(int blurRadius) {
      samData = new float[(16 + 2*blurRadius) * (16 + 2*blurRadius) * 9 + 9];
      samN = new int[(16 + 2*blurRadius) * (16 + 2*blurRadius) + 1];
      // (a bit more space is allocated at the end and left with 0 to simplify implmeentation of getAverageFor
      this.blurRadius = blurRadius;
      xStride = 16 + 2*blurRadius;
    }

    int indexFor(int x, int z) {
      int offsetedX = x + blurRadius;
      int offsetedZ = z + blurRadius;
      if(offsetedX < 0 || offsetedZ < 0) {
        // if requesting before the table, redirect to the space allocated at the end
        return samN.length - 1;
      }
      return offsetedX * xStride + offsetedZ;
    }

    void init(int x, int z, Biome biome) {
      int idx = indexFor(x, z);
      samData[idx*9] = biome.grassColorLinear[0];
      samData[idx*9+1] = biome.grassColorLinear[1];
      samData[idx*9+2] = biome.grassColorLinear[2];
      samData[idx*9+3] = biome.foliageColorLinear[0];
      samData[idx*9+4] = biome.foliageColorLinear[1];
      samData[idx*9+5] = biome.foliageColorLinear[2];
      samData[idx*9+6] = biome.waterColorLinear[0];
      samData[idx*9+7] = biome.waterColorLinear[1];
      samData[idx*9+8] = biome.waterColorLinear[2];
      samN[idx] = 1;
    }

    void computeSum() {
      // Compute first line
      for(int offsetedX = 1; offsetedX < 16 + 2*blurRadius; ++offsetedX) {
        for(int i = 0; i < 9; ++i) {
          samData[(offsetedX * xStride) * 9 + i] += samData[((offsetedX - 1) * xStride) * 9 + i];
        }
        samN[offsetedX * xStride] += samN[(offsetedX - 1) * xStride];
      }

      // Compute first column
      for(int offsetedZ = 1; offsetedZ < 16 + 2*blurRadius; ++offsetedZ) {
        for(int i = 0; i < 9; ++i) {
          samData[offsetedZ * 9 + i] += samData[(offsetedZ - 1) * 9 + i];
        }
        samN[offsetedZ] += samN[offsetedZ - 1];
      }

      // Compute rest
      for(int offsetedX = 1; offsetedX < 16 + 2*blurRadius; ++offsetedX) {
        for(int offsetedZ = 1; offsetedZ < 16 + 2 * blurRadius; ++offsetedZ) {
          for(int i = 0; i < 9; ++i) {
            // current += top + left - topleft
            samData[(offsetedX * xStride + offsetedZ) * 9 + i] +=
                samData[((offsetedX - 1) * xStride + offsetedZ) * 9 + i]
              + samData[(offsetedX * xStride + (offsetedZ - 1)) * 9 + i]
              - samData[((offsetedX - 1) * xStride + (offsetedZ - 1)) * 9 + i];
          }
          samN[offsetedX * xStride + offsetedZ] +=
              samN[(offsetedX - 1) * xStride + offsetedZ]
            + samN[offsetedX * xStride + (offsetedZ - 1)]
            - samN[(offsetedX - 1) * xStride + (offsetedZ - 1)];
        }
      }
    }

    public float[] getAverageFor(int x, int z) {
      int topLeft = indexFor(x - blurRadius - 1, z - blurRadius - 1);
      int topRight = indexFor(x - blurRadius - 1, z + blurRadius);
      int bottomLeft = indexFor(x + blurRadius, z - blurRadius - 1);
      int bottomRight = indexFor(x + blurRadius, z + blurRadius);

      int n = samN[topLeft] + samN[bottomRight] - samN[topRight] - samN[bottomLeft];

      float[] result = new float[9];
      for(int i = 0; i < 9; ++i) {
        result[i] = samData[topLeft*9+i] + samData[bottomRight*9+i] - samData[topRight*9+i] - samData[bottomLeft*9+i];
        result[i] /= n;
      }

      return result;
    }
  }

  /**
   * Compute the blended biome colors for a chunk by doing a 2D blur and store the result in the given biome structures
   * Sample the biome at a given y level and writes the result for y levels going from samplingY (inclusive) to maxFillY (exclusive)
   */
  static public void chunk2DBlur(ChunkPosition cp, int blurRadius, int samplingY, int maxFillY, Vector3i origin, Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks, BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure waterTexture) {

    SummedAreaTable table = new SummedAreaTable(blurRadius);
    for(int x = -blurRadius; x < 16 + blurRadius; ++x) {
      for(int z = -blurRadius; z < 16 + blurRadius; ++z) {
        ChunkPosition ccp = new ChunkPosition((cp.x * 16 + x) / 16, (cp.z * 16 + z) / 16);
        if (nonEmptyChunks.contains(ccp)) {
          int biomeId = biomeIdx.get(cp.x * 16 + x, samplingY, cp.z * 16 + z);
          if(biomeId != -1) {
            Biome biome = biomePalette.get(biomeId);
            table.init(x, z, biome);
          } else {
            // Not having the biome data loaded at this point is
            // a bug earlier in the loading process.
            // In case it happens, ignore the block to get a somewhat sensible result
            assert false;
          }
        }
      }
    }
    table.computeSum();

    for (int x = 0; x < 16; ++x) {
      for (int z = 0; z < 16; ++z) {
        float[] data = table.getAverageFor(x, z);
        float[] grassMix = {
          data[0], data[1], data[2]
        };
        float[] foliageMix = {
          data[3], data[4], data[5]
        };
        float[] waterMix = {
          data[6], data[7], data[8]
        };

        for(int y = samplingY; y < maxFillY; ++y) {
          // TODO Introduce additional API to BiomeStructure to make them aware of the vertical repetition so they can optimize if wanted
          grassTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, grassMix);
          foliageTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, foliageMix);
          waterTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, waterMix);
        }
      }
    }
  }

  /**
   * Compute the blended biome colors for a portion of chunk by doing a 3D blur and store the result in the given biome structures
   */
  static public void chunk3DBlur(ChunkPosition cp, int blurRadius, int minY, int maxY, Vector3i origin, Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks, BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure waterTexture) {
    for (int x = 0; x < 16; ++x) {
      for (int z = 0; z < 16; ++z) {
        for(int y = minY; y < maxY; ++y) {
          int nsum = 0;
          float[] grassMix = {0, 0, 0};
          float[] foliageMix = {0, 0, 0};
          float[] waterMix = {0, 0, 0};
          for(int sx = x - blurRadius; sx <= x + blurRadius; ++sx) {
            int wx = cp.x * 16 + sx;
            for(int sz = z - blurRadius; sz <= z + blurRadius; ++sz) {
              int wz = cp.z * 16 + sz;

              ChunkPosition ccp = new ChunkPosition(wx >> 4, wz >> 4);
              if(nonEmptyChunks.contains(ccp)) {
                // TODO if y is out of bounds, biomeIdx.get will return 0 and we will blur with the wrong biome
                for(int sy = y - blurRadius; sy <= y + blurRadius; ++sy) {
                  nsum += 1;
                  Biome biome = biomePalette.get(biomeIdx.get(wx, sy, wz));
                  addEqual(grassMix, biome.grassColorLinear);
                  addEqual(foliageMix, biome.foliageColorLinear);
                  addEqual(waterMix, biome.waterColorLinear);
                }
              }
            }
          }
          grassMix[0] /= nsum;
          grassMix[1] /= nsum;
          grassMix[2] /= nsum;

          foliageMix[0] /= nsum;
          foliageMix[1] /= nsum;
          foliageMix[2] /= nsum;

          waterMix[0] /= nsum;
          waterMix[1] /= nsum;
          waterMix[2] /= nsum;

          grassTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, grassMix);
          foliageTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, foliageMix);
          waterTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, waterMix);
        }
      }
    }
  }
}
