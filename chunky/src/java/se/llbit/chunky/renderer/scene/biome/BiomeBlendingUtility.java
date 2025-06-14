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
   * and O(1) use time. Based on summed-area table https://en.wikipedia.org/wiki/Summed-area_table
   */
  private static class SummedAreaTable {
    // (16+2*r)² Summed area table of 9 entries vector (3 components for 3 colors)
    float[] satData;
    // Other summed area table to count the number of summed color to get denominator for average
    int[] satN;
    final int blurRadius;
    final int xStride;

    public SummedAreaTable(int blurRadius) {
      satData = new float[(16 + 2*blurRadius) * (16 + 2*blurRadius) * 9 + 9];
      satN = new int[(16 + 2*blurRadius) * (16 + 2*blurRadius) + 1];
      // (a bit more space is allocated at the end and left with 0 to simplify implmeentation of getAverageFor
      this.blurRadius = blurRadius;
      xStride = 16 + 2*blurRadius;
    }

    int indexFor(int x, int z) {
      int offsetedX = x + blurRadius;
      int offsetedZ = z + blurRadius;
      if(offsetedX < 0 || offsetedZ < 0) {
        // if requesting before the table, redirect to the space allocated at the end
        return satN.length - 1;
      }
      return offsetedX * xStride + offsetedZ;
    }

    void init(int x, int z, Biome biome) {
      int idx = indexFor(x, z);
      satData[idx*9] = biome.grassColorLinear[0];
      satData[idx*9+1] = biome.grassColorLinear[1];
      satData[idx*9+2] = biome.grassColorLinear[2];
      satData[idx*9+3] = biome.foliageColorLinear[0];
      satData[idx*9+4] = biome.foliageColorLinear[1];
      satData[idx*9+5] = biome.foliageColorLinear[2];
      satData[idx*9+6] = biome.waterColorLinear[0];
      satData[idx*9+7] = biome.waterColorLinear[1];
      satData[idx*9+8] = biome.waterColorLinear[2];
      satN[idx] = 1;
    }

    void computeSum() {
      // Compute first line
      for(int offsetedX = 1; offsetedX < 16 + 2*blurRadius; ++offsetedX) {
        for(int i = 0; i < 9; ++i) {
          satData[(offsetedX * xStride) * 9 + i] += satData[((offsetedX - 1) * xStride) * 9 + i];
        }
        satN[offsetedX * xStride] += satN[(offsetedX - 1) * xStride];
      }

      // Compute first column
      for(int offsetedZ = 1; offsetedZ < 16 + 2*blurRadius; ++offsetedZ) {
        for(int i = 0; i < 9; ++i) {
          satData[offsetedZ * 9 + i] += satData[(offsetedZ - 1) * 9 + i];
        }
        satN[offsetedZ] += satN[offsetedZ - 1];
      }

      // Compute rest
      for(int offsetedX = 1; offsetedX < 16 + 2*blurRadius; ++offsetedX) {
        for(int offsetedZ = 1; offsetedZ < 16 + 2 * blurRadius; ++offsetedZ) {
          for(int i = 0; i < 9; ++i) {
            // current += top + left - topleft
            satData[(offsetedX * xStride + offsetedZ) * 9 + i] +=
                satData[((offsetedX - 1) * xStride + offsetedZ) * 9 + i]
              + satData[(offsetedX * xStride + (offsetedZ - 1)) * 9 + i]
              - satData[((offsetedX - 1) * xStride + (offsetedZ - 1)) * 9 + i];
          }
          satN[offsetedX * xStride + offsetedZ] +=
              satN[(offsetedX - 1) * xStride + offsetedZ]
            + satN[offsetedX * xStride + (offsetedZ - 1)]
            - satN[(offsetedX - 1) * xStride + (offsetedZ - 1)];
        }
      }
    }

    public float[] getAverageFor(int x, int z) {
      int topLeft = indexFor(x - blurRadius - 1, z - blurRadius - 1);
      int topRight = indexFor(x - blurRadius - 1, z + blurRadius);
      int bottomLeft = indexFor(x + blurRadius, z - blurRadius - 1);
      int bottomRight = indexFor(x + blurRadius, z + blurRadius);

      int n = satN[topLeft] + satN[bottomRight] - satN[topRight] - satN[bottomLeft];

      float[] result = new float[9];
      for(int i = 0; i < 9; ++i) {
        result[i] = satData[topLeft*9+i] + satData[bottomRight*9+i] - satData[topRight*9+i] - satData[bottomLeft*9+i];
        result[i] /= n;
      }

      return result;
    }
  }

  /**
   * Compute the blended biome colors for a chunk by doing a 2D blur and store the result in the given biome structures
   * Sample the biome at a given y level and writes the result for y levels going from samplingY (inclusive) to maxFillY (exclusive)
   */
  static public void chunk2DBlur(ChunkPosition cp, int blurRadius, int samplingY, int maxFillY, Vector3i origin,
                                 Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks,
                                 BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure dryFoliageTexture, BiomeStructure waterTexture) {

    SummedAreaTable table = new SummedAreaTable(blurRadius);
    for(int x = -blurRadius; x < 16 + blurRadius; ++x) {
      for(int z = -blurRadius; z < 16 + blurRadius; ++z) {
        ChunkPosition ccp = new ChunkPosition(Math.floorDiv(cp.x * 16 + x, 16), Math.floorDiv(cp.z * 16 + z, 16));
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
   * Same as SummedAreaTable but in 3D
   * https://stackoverflow.com/questions/20445084/3d-variant-for-summed-area-table-sat
   */
  private static class SummedVolumeTable {
    // (16+2*r)^3 Summed area table of 9 entries vector (3 components for 3 colors)
    float[] satData;
    // Other summed area table to count the number of summed color to get denominator for average
    int[] satN;
    final int blurRadius;
    final int yMin;
    final int yMax;
    final int yStride;
    final int xStride;

    public SummedVolumeTable(int blurRadius, int yMin, int yMax) {
      satData = new float[(16 + 2*blurRadius) * (16 + 2*blurRadius) * (yMax - yMin + 2*blurRadius) * 9 + 9];
      satN = new int[(16 + 2*blurRadius) * (16 + 2*blurRadius) * (yMax - yMin + 2*blurRadius) + 1];
      // (a bit more space is allocated at the end and left with 0 to simplify implmeentation of getAverageFor
      this.blurRadius = blurRadius;
      this.yMin = yMin;
      this.yMax = yMax;
      yStride = (16 + 2*blurRadius) * (16 + 2*blurRadius);
      xStride = 16 + 2*blurRadius;
    }

    int indexFor(int x, int y, int z) {
      int offsetedY = y - yMin + blurRadius;
      int offsetedX = x + blurRadius;
      int offsetedZ = z + blurRadius;
      if(offsetedY < 0 || offsetedX < 0 || offsetedZ < 0) {
        // if requesting before the table, redirect to the space allocated at the end
        return satN.length - 1;
      }
      return offsetedY * yStride + offsetedX * xStride + offsetedZ;
    }

    void init(int x, int y, int z, Biome biome) {
      int idx = indexFor(x, y, z);
      satData[idx*9] = biome.grassColorLinear[0];
      satData[idx*9+1] = biome.grassColorLinear[1];
      satData[idx*9+2] = biome.grassColorLinear[2];
      satData[idx*9+3] = biome.foliageColorLinear[0];
      satData[idx*9+4] = biome.foliageColorLinear[1];
      satData[idx*9+5] = biome.foliageColorLinear[2];
      satData[idx*9+6] = biome.waterColorLinear[0];
      satData[idx*9+7] = biome.waterColorLinear[1];
      satData[idx*9+8] = biome.waterColorLinear[2];
      satN[idx] = 1;
    }

    void computeSum() {
      // Because of the trick of redirecting out of bounds index to the end
      // (that we only read and never write, so full of zeros)
      // there is no need to special case the start

      for(int y = yMin - blurRadius; y < yMax + blurRadius; ++y) {
        for(int x = -blurRadius; x < 16 + blurRadius; ++x) {
          for(int z = -blurRadius; z < 16 + blurRadius; ++z) {
            int xyz = indexFor(x, y, z);
            int xyz1 = indexFor(x, y, z - 1);
            int xy1z = indexFor(x, y - 1, z);
            int xy1z1 = indexFor(x, y - 1, z - 1);
            int x1yz = indexFor(x - 1, y, z);
            int x1yz1 = indexFor(x - 1, y, z - 1);
            int x1y1z = indexFor(x - 1, y - 1, z);
            int x1y1z1 = indexFor(x - 1, y - 1, z - 1);

            for(int i = 0; i < 9; ++i) {
              satData[xyz*9 + i] +=
                  satData[x1yz*9 + i]
                + satData[xy1z*9 + i]
                + satData[xyz1*9 + i]
                - satData[x1y1z*9 + i]
                - satData[x1yz1*9 + i]
                - satData[xy1z1*9 + i]
                + satData[x1y1z1*9 + i];
            }
            satN[xyz] +=
                satN[x1yz]
              + satN[xy1z]
              + satN[xyz1]
              - satN[x1y1z]
              - satN[x1yz1]
              - satN[xy1z1]
              + satN[x1y1z1];
          }
        }
      }
    }

    public float[] getAverageFor(int x, int y, int z) {
      int x1y1z1 = indexFor(x - blurRadius - 1, y - blurRadius - 1, z - blurRadius - 1);
      int x1y1z2 = indexFor(x - blurRadius - 1, y - blurRadius - 1, z + blurRadius);
      int x1y2z1 = indexFor(x - blurRadius - 1, y + blurRadius, z - blurRadius - 1);
      int x1y2z2 = indexFor(x - blurRadius - 1, y + blurRadius, z + blurRadius);
      int x2y1z1 = indexFor(x + blurRadius, y - blurRadius - 1, z - blurRadius - 1);
      int x2y1z2 = indexFor(x + blurRadius, y - blurRadius - 1, z + blurRadius);
      int x2y2z1 = indexFor(x + blurRadius, y + blurRadius, z - blurRadius - 1);
      int x2y2z2 = indexFor(x + blurRadius, y + blurRadius, z + blurRadius);

      int n =
          satN[x2y2z2]
        - satN[x2y2z1]
        - satN[x2y1z2]
        - satN[x1y2z2]
        + satN[x2y1z1]
        + satN[x1y2z1]
        + satN[x1y1z2]
        - satN[x1y1z1];

      float[] result = new float[9];
      for(int i = 0; i < 9; ++i) {
        result[i] =
            satData[x2y2z2*9 + i]
          - satData[x2y2z1*9 + i]
          - satData[x2y1z2*9 + i]
          - satData[x1y2z2*9 + i]
          + satData[x2y1z1*9 + i]
          + satData[x1y2z1*9 + i]
          + satData[x1y1z2*9 + i]
          - satData[x1y1z1*9 + i];
        result[i] /= n;
      }

      return result;
    }
  }

  /**
   * Compute the blended biome colors for a portion of chunk by doing a 3D blur and store the result in the given biome structures
   */
  static public void chunk3DBlur(ChunkPosition cp, int blurRadius, int minY, int maxY, Vector3i origin,
                                 Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks,
                                 BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure dryFoliageTexture, BiomeStructure waterTexture) {
    SummedVolumeTable table = new SummedVolumeTable(blurRadius, minY, maxY);
    for(int y = minY - blurRadius; y < maxY + blurRadius; ++y) {
      for(int x = -blurRadius; x < 16 + blurRadius; ++x) {
        for(int z = -blurRadius; z < 16 + blurRadius; ++z) {
          ChunkPosition ccp = new ChunkPosition(Math.floorDiv(cp.x * 16 + x, 16), Math.floorDiv(cp.z * 16 + z, 16));
          if (nonEmptyChunks.contains(ccp)) {
            int biomeId = biomeIdx.get(cp.x * 16 + x, y, cp.z * 16 + z);
            if(biomeId != -1) {
              Biome biome = biomePalette.get(biomeId);
              table.init(x, y, z, biome);
            }
            // if biomeId is -1, either y is outside of the loaded interval
            // or there is a bug in loading similar to chunk2DBlur
          }
        }
      }
    }
    table.computeSum();

    for(int y = minY; y < maxY; ++y) {
      for (int x = 0; x < 16; ++x) {
        for (int z = 0; z < 16; ++z) {
          float[] data = table.getAverageFor(x, y, z);
          float[] grassMix = {
            data[0], data[1], data[2]
          };
          float[] foliageMix = {
            data[3], data[4], data[5]
          };
          float[] waterMix = {
            data[6], data[7], data[8]
          };

          grassTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, grassMix);
          foliageTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, foliageMix);
          waterTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, waterMix);
        }
      }
    }
  }
}
