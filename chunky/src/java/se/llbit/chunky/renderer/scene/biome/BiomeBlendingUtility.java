package se.llbit.chunky.renderer.scene.biome;

import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.math.Vector3i;
import se.llbit.math.structures.Position2IntStructure;

import java.util.Set;

public class BiomeBlendingUtility {
  /**
   * Helper class to compute average of subrectangle of a 2D table in O(n²) init time
   * and O(1) use time. Based on summed-area table
   */
  /**
   * Internal helper class that implements a 2D summed-area table (SAT)
   * for fast rectangular averaging of biome color values.
   *
   * <p>Each element in the table represents a {@code TableElement},
   * conceptually containing four {@code Color} values (for grass, foliage, water, and dry foliage),
   * each with RGB components.</p>
   *
   * <p>To reduce memory usage and avoid Java object overhead, all values are stored in a flat float array ({@code satData}),
   * with 12 floats per element (4 × RGB colors). An accompanying {@code satN} holds count values for normalization.</p>
   *
   * <p>One extra dummy element is allocated to simplify edge handling. Any negative index
   * (accessing blocks outside the SAT area) is redirected to this zero-filled padding element.</p>
   *
   * <p>The underlying algorithms are based on the <a href="https://en.wikipedia.org/wiki/Summed-area_table">
   * summed area table</a></p>
   */
  private static class SummedAreaTable {
    private final int ENTRY_SIZE = 12;
    // (16+2*r)² Summed area table of 12 entries vector (3 components for 4 colors)
    float[] satData;
    // Other summed area table to count the number of summed color to get denominator for average
    int[] satN;
    final int blurRadius;
    final int xStride;

    public SummedAreaTable(int blurRadius) {
      satData = new float[(16 + 2 * blurRadius) * (16 + 2 * blurRadius) * ENTRY_SIZE + ENTRY_SIZE];
      satN = new int[(16 + 2 * blurRadius) * (16 + 2 * blurRadius) + 1];
      // (a bit more space is allocated at the end and left with 0 to simplify implementation of getAverageFor
      this.blurRadius = blurRadius;
      xStride = 16 + 2 * blurRadius;
    }

    int indexFor(int x, int z) {
      int offsetedX = x + blurRadius;
      int offsetedZ = z + blurRadius;
      if (offsetedX < 0 || offsetedZ < 0) {
        // if requesting before the table, redirect to the space allocated at the end
        return satN.length - 1;
      }
      return offsetedX * xStride + offsetedZ;
    }

    /**
     * Initializes a cell in the summed-area table with the color values from the given biome.
     *
     * <p>This method stores the linear RGB values for grass, foliage, water, and dry foliage colors
     * at the given table coordinate. The internal arrays are updated accordingly,
     * and the count for that cell is incremented.</p>
     *
     * @param x     The local X coordinate relative to the center chunk
     * @param z     The local Z coordinate relative to the center chunk
     * @param biome The biome whose color values will be stored at this location
     */
    void init(int x, int z, Biome biome) {
      int idx = indexFor(x, z);
      satData[idx * ENTRY_SIZE] = biome.grassColorLinear[0];
      satData[idx * ENTRY_SIZE + 1] = biome.grassColorLinear[1];
      satData[idx * ENTRY_SIZE + 2] = biome.grassColorLinear[2];
      satData[idx * ENTRY_SIZE + 3] = biome.foliageColorLinear[0];
      satData[idx * ENTRY_SIZE + 4] = biome.foliageColorLinear[1];
      satData[idx * ENTRY_SIZE + 5] = biome.foliageColorLinear[2];
      satData[idx * ENTRY_SIZE + 6] = biome.waterColorLinear[0];
      satData[idx * ENTRY_SIZE + 7] = biome.waterColorLinear[1];
      satData[idx * ENTRY_SIZE + 8] = biome.waterColorLinear[2];
      satData[idx * ENTRY_SIZE + 9] = biome.dryFoliageColorLinear[0];
      satData[idx * ENTRY_SIZE + 10] = biome.dryFoliageColorLinear[1];
      satData[idx * ENTRY_SIZE + 11] = biome.dryFoliageColorLinear[2];
      satN[idx] = 1;
    }

    /**
     * Computes the full 2D summed-area table based on the initialized values.
     *
     * <p>This must be called after all data has been initialized via {@link #init}.
     * It populates each cell with the sum of all values in the rectangle
     * from (0,0) to the current cell, enabling O(1) averaging queries using inclusion-exclusion.</p>
     *
     * <p>Edge cases are simplified by the use of a zero-filled dummy entry for negative indices.</p>
     */
    void computeSum() {
      // Compute first line
      for (int offsetedX = 1; offsetedX < 16 + 2 * blurRadius; ++offsetedX) {
        for (int i = 0; i < ENTRY_SIZE; ++i) {
          satData[(offsetedX * xStride) * ENTRY_SIZE + i] += satData[((offsetedX - 1) * xStride) * ENTRY_SIZE + i];
        }
        satN[offsetedX * xStride] += satN[(offsetedX - 1) * xStride];
      }

      // Compute first column
      for (int offsetedZ = 1; offsetedZ < 16 + 2 * blurRadius; ++offsetedZ) {
        for (int i = 0; i < ENTRY_SIZE; ++i) {
          satData[offsetedZ * ENTRY_SIZE + i] += satData[(offsetedZ - 1) * ENTRY_SIZE + i];
        }
        satN[offsetedZ] += satN[offsetedZ - 1];
      }

      // Compute rest
      for (int offsetedX = 1; offsetedX < 16 + 2 * blurRadius; ++offsetedX) {
        for (int offsetedZ = 1; offsetedZ < 16 + 2 * blurRadius; ++offsetedZ) {
          for (int i = 0; i < ENTRY_SIZE; ++i) {
            // current += top + left - topleft
            satData[(offsetedX * xStride + offsetedZ) * ENTRY_SIZE + i] +=
              satData[((offsetedX - 1) * xStride + offsetedZ) * ENTRY_SIZE + i]
                + satData[(offsetedX * xStride + (offsetedZ - 1)) * ENTRY_SIZE + i]
                - satData[((offsetedX - 1) * xStride + (offsetedZ - 1)) * ENTRY_SIZE + i];
          }
          satN[offsetedX * xStride + offsetedZ] +=
            satN[(offsetedX - 1) * xStride + offsetedZ]
              + satN[offsetedX * xStride + (offsetedZ - 1)]
              - satN[(offsetedX - 1) * xStride + (offsetedZ - 1)];
        }
      }
    }

    /**
     * Computes the average biome colors in a square window centered around (x, z).
     *
     * <p>The window has a side length of (2 × blurRadius + 1). The method uses
     * summed-area table lookups and inclusion-exclusion to compute the sum
     * of all color values in the area, divided by the number of contributing cells.</p>
     *
     * <p>Returns an array of 12 floats:
     * <ul>
     *   <li>[0–2] Grass RGB</li>
     *   <li>[3–5] Foliage RGB</li>
     *   <li>[6–8] Water RGB</li>
     *   <li>[9–11] Dry foliage RGB</li>
     * </ul></p>
     *
     * @param x The local X coordinate (chunk-relative)
     * @param z The local Z coordinate (chunk-relative)
     * @return A float array containing the averaged RGB values for grass, foliage, water, and dry foliage
     */
    public float[] getAverageFor(int x, int z) {
      int topLeft = indexFor(x - blurRadius - 1, z - blurRadius - 1);
      int topRight = indexFor(x - blurRadius - 1, z + blurRadius);
      int bottomLeft = indexFor(x + blurRadius, z - blurRadius - 1);
      int bottomRight = indexFor(x + blurRadius, z + blurRadius);

      int n = satN[topLeft] + satN[bottomRight] - satN[topRight] - satN[bottomLeft];

      float[] result = new float[ENTRY_SIZE];
      for (int i = 0; i < ENTRY_SIZE; ++i) {
        result[i] = satData[topLeft * ENTRY_SIZE + i] + satData[bottomRight * ENTRY_SIZE + i] - satData[topRight * ENTRY_SIZE + i] - satData[bottomLeft * ENTRY_SIZE + i];
        result[i] /= n;
      }

      return result;
    }
  }

  /**
   * Computes a 2D biome blur over a single chunk by averaging neighboring biome colors
   * and writes the result into biome texture structures for multiple vertical levels.
   *
   * <p>The method uses a 2D summed-area table (SAT) to efficiently compute the average
   * grass, foliage, and water color components across a square window centered at each pixel
   * in the chunk. This results in visually smooth transitions between neighboring biomes,
   * improving the appearance of biome-based rendering.</p>
   *
   * <p>The biome is sampled at a fixed Y level ({@code samplingY}), but the computed
   * averaged color is written to a vertical range from {@code samplingY} (inclusive) up to {@code maxFillY} (exclusive).
   * The blur includes neighboring chunks within the given {@code blurRadius}, but only if those chunks
   * are known to be non-empty.</p>
   *
   * @param cp                The chunk position for which the blur should be computed
   * @param blurRadius        Radius of the blur window. A value of 1 uses a 3x3 window
   * @param samplingY         The Y-level at which to sample biome data
   * @param maxFillY          The exclusive upper Y-level to which blurred colors should be written
   * @param origin            The coordinate offset used to normalize output structure coordinates
   * @param biomeIdx          A data structure that provides biome IDs at specific coordinates
   * @param biomePalette      A lookup table mapping biome IDs to biome definitions (colors etc.)
   * @param nonEmptyChunks    Set of chunk positions that are loaded and contain data
   * @param grassTexture      Output texture for blurred grass colors
   * @param foliageTexture    Output texture for blurred foliage colors
   * @param dryFoliageTexture Output texture for blurred dry foliage colors (eg. leaf litter)
   * @param waterTexture      Output texture for blurred water colors
   */
  static public void chunk2DBlur(ChunkPosition cp, int blurRadius, int samplingY, int maxFillY, Vector3i origin,
                                 Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks,
                                 BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure dryFoliageTexture, BiomeStructure waterTexture) {

    SummedAreaTable table = new SummedAreaTable(blurRadius);
    for (int x = -blurRadius; x < 16 + blurRadius; ++x) {
      for (int z = -blurRadius; z < 16 + blurRadius; ++z) {
        ChunkPosition ccp = new ChunkPosition(Math.floorDiv(cp.x * 16 + x, 16), Math.floorDiv(cp.z * 16 + z, 16));
        if (nonEmptyChunks.contains(ccp)) {
          int biomeId = biomeIdx.get(cp.x * 16 + x, samplingY, cp.z * 16 + z);
          if (biomeId != -1) {
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
        float[] dryFoliageMix = {
          data[9], data[10], data[11]
        };

        for (int y = samplingY; y < maxFillY; ++y) {
          // TODO Introduce additional API to BiomeStructure to make them aware of the vertical repetition so they can optimize if wanted
          grassTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, grassMix);
          foliageTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, foliageMix);
          waterTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, waterMix);
          dryFoliageTexture.set(cp.x * 16 + x - origin.x, y - origin.y, cp.z * 16 + z - origin.z, dryFoliageMix);
        }
      }
    }
  }

  /**
   * Internal helper class that implements a 3D summed-volume table (SVT)
   * to enable fast volume averaging of biome color values in 3D.
   *
   * <p>Like its 2D counterpart, each element conceptually stores four colors:
   * grass, foliage, water, and dry foliage, each as RGB triplets. All values are packed into
   * a flat float array with 12 floats per voxel to reduce memory overhead.</p>
   *
   * <p>Indexing is offset and padded to allow negative values and simplify boundary conditions,
   * using a dummy entry that is always zeroed out.</p>
   *
   * <p>The underlying algorithms are based on the 3D variant of a summed-area table.
   * For details, see: <a href="https://stackoverflow.com/questions/20445084/3d-variant-for-summed-area-table-sat">
   * https://stackoverflow.com/questions/20445084/3d-variant-for-summed-area-table-sat</a></p>
   */
  private static class SummedVolumeTable {
    private static final int ENTRY_SIZE = 12;
    // (16+2*r)^3 Summed area table of 12 entries vector (3 components for 4 colors)
    float[] satData;
    // Other summed area table to count the number of summed color to get denominator for average
    int[] satN;
    final int blurRadius;
    final int yMin;
    final int yMax;
    final int yStride;
    final int xStride;

    public SummedVolumeTable(int blurRadius, int yMin, int yMax) {
      satData = new float[(16 + 2 * blurRadius) * (16 + 2 * blurRadius) * (yMax - yMin + 2 * blurRadius) * ENTRY_SIZE + ENTRY_SIZE];
      satN = new int[(16 + 2 * blurRadius) * (16 + 2 * blurRadius) * (yMax - yMin + 2 * blurRadius) + 1];
      // (a bit more space is allocated at the end and left with 0 to simplify implmeentation of getAverageFor
      this.blurRadius = blurRadius;
      this.yMin = yMin;
      this.yMax = yMax;
      yStride = (16 + 2 * blurRadius) * (16 + 2 * blurRadius);
      xStride = 16 + 2 * blurRadius;
    }

    int indexFor(int x, int y, int z) {
      int offsetedY = y - yMin + blurRadius;
      int offsetedX = x + blurRadius;
      int offsetedZ = z + blurRadius;
      if (offsetedY < 0 || offsetedX < 0 || offsetedZ < 0) {
        // if requesting before the table, redirect to the space allocated at the end
        return satN.length - 1;
      }
      return offsetedY * yStride + offsetedX * xStride + offsetedZ;
    }

    /**
     * Initializes a voxel in the summed-volume table with the color values from the given biome.
     *
     * @param x     The local X coordinate relative to the chunk
     * @param y     The world Y coordinate
     * @param z     The local Z coordinate relative to the chunk
     * @param biome The biome whose color values are to be stored
     */
    void init(int x, int y, int z, Biome biome) {
      int idx = indexFor(x, y, z);
      satData[idx * ENTRY_SIZE] = biome.grassColorLinear[0];
      satData[idx * ENTRY_SIZE + 1] = biome.grassColorLinear[1];
      satData[idx * ENTRY_SIZE + 2] = biome.grassColorLinear[2];
      satData[idx * ENTRY_SIZE + 3] = biome.foliageColorLinear[0];
      satData[idx * ENTRY_SIZE + 4] = biome.foliageColorLinear[1];
      satData[idx * ENTRY_SIZE + 5] = biome.foliageColorLinear[2];
      satData[idx * ENTRY_SIZE + 6] = biome.waterColorLinear[0];
      satData[idx * ENTRY_SIZE + 7] = biome.waterColorLinear[1];
      satData[idx * ENTRY_SIZE + 8] = biome.waterColorLinear[2];
      satData[idx * ENTRY_SIZE + 9] = biome.dryFoliageColorLinear[0];
      satData[idx * ENTRY_SIZE + 10] = biome.dryFoliageColorLinear[1];
      satData[idx * ENTRY_SIZE + 11] = biome.dryFoliageColorLinear[2];
      satN[idx] = 1;
    }

    /**
     * Builds the 3D summed-volume table from initialized values.
     *
     * <p>After this is called, each voxel contains the cumulative sum of all color values
     * from the volume (0,0,0) to that point, enabling constant-time blur queries via
     * 3D inclusion-exclusion logic.</p>
     *
     * <p>Negative indices are handled by redirecting to a dummy entry.</p>
     */
    void computeSum() {
      // Because of the trick of redirecting out of bounds index to the end
      // (that we only read and never write, so full of zeros)
      // there is no need to special case the start

      for (int y = yMin - blurRadius; y < yMax + blurRadius; ++y) {
        for (int x = -blurRadius; x < 16 + blurRadius; ++x) {
          for (int z = -blurRadius; z < 16 + blurRadius; ++z) {
            int xyz = indexFor(x, y, z);
            int xyz1 = indexFor(x, y, z - 1);
            int xy1z = indexFor(x, y - 1, z);
            int xy1z1 = indexFor(x, y - 1, z - 1);
            int x1yz = indexFor(x - 1, y, z);
            int x1yz1 = indexFor(x - 1, y, z - 1);
            int x1y1z = indexFor(x - 1, y - 1, z);
            int x1y1z1 = indexFor(x - 1, y - 1, z - 1);

            for (int i = 0; i < ENTRY_SIZE; ++i) {
              satData[xyz * ENTRY_SIZE + i] +=
                satData[x1yz * ENTRY_SIZE + i]
                  + satData[xy1z * ENTRY_SIZE + i]
                  + satData[xyz1 * ENTRY_SIZE + i]
                  - satData[x1y1z * ENTRY_SIZE + i]
                  - satData[x1yz1 * ENTRY_SIZE + i]
                  - satData[xy1z1 * ENTRY_SIZE + i]
                  + satData[x1y1z1 * ENTRY_SIZE + i];
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

    /**
     * Computes the average biome colors within a cubic region centered at (x, y, z).
     *
     * <p>The cubic region includes all voxels within the {@code blurRadius} distance in each direction.</p>
     *
     * <p>Returns an array of 12 floats:
     * <ul>
     *   <li>[0–2] Grass RGB</li>
     *   <li>[3–5] Foliage RGB</li>
     *   <li>[6–8] Water RGB</li>
     *   <li>[9–11] Dry foliage RGB</li>
     * </ul></p>
     *
     * <p>Uses 3D inclusion-exclusion on the summed-volume table to compute the result in O(1) time.</p>
     *
     * @param x The local X coordinate (0–15) relative to the chunk
     * @param y The Y coordinate (within minY–maxY)
     * @param z The local Z coordinate (0–15) relative to the chunk
     * @return A float array containing the averaged RGB values for grass, foliage, water, and dry foliage
     */
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

      float[] result = new float[ENTRY_SIZE];
      for (int i = 0; i < ENTRY_SIZE; ++i) {
        result[i] =
          satData[x2y2z2 * ENTRY_SIZE + i]
            - satData[x2y2z1 * ENTRY_SIZE + i]
            - satData[x2y1z2 * ENTRY_SIZE + i]
            - satData[x1y2z2 * ENTRY_SIZE + i]
            + satData[x2y1z1 * ENTRY_SIZE + i]
            + satData[x1y2z1 * ENTRY_SIZE + i]
            + satData[x1y1z2 * ENTRY_SIZE + i]
            - satData[x1y1z1 * ENTRY_SIZE + i];
        result[i] /= n;
      }

      return result;
    }
  }

  /**
   * Computes a 3D biome blur over a chunk volume using a summed-volume table (SVT)
   * and writes the result into biome texture structures for each voxel.
   *
   * <p>This method generalizes the 2D blur to three dimensions, applying a cubic blur window
   * of radius {@code blurRadius} over grass, foliage, and water color components. It enables
   * vertical smoothing of biome transitions in addition to horizontal blending, leading to
   * more natural rendering in features such as caves or vertical landscapes.</p>
   *
   * <p>The blur is applied only to loaded and non-empty chunks. If biome data is missing
   * (e.g., outside loaded height), the block is skipped.</p>
   *
   * @param cp                The chunk position (X, Z) to process
   * @param blurRadius        Radius of the 3D cubic blur window
   * @param minY              Minimum Y-level (inclusive) to process
   * @param maxY              Maximum Y-level (exclusive) to process
   * @param origin            Offset used to translate world coordinates to texture coordinates
   * @param biomeIdx          Lookup structure for biome IDs per voxel
   * @param biomePalette      Biome ID to biome definition mapping
   * @param nonEmptyChunks    Set of chunks known to contain valid data
   * @param grassTexture      Output texture for blurred grass colors
   * @param foliageTexture    Output texture for blurred foliage colors
   * @param dryFoliageTexture Output texture for blurred dry foliage colors (e.g. leaf litter)
   * @param waterTexture      Output texture for blurred water colors
   */
  static public void chunk3DBlur(ChunkPosition cp, int blurRadius, int minY, int maxY, Vector3i origin,
                                 Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks,
                                 BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure dryFoliageTexture, BiomeStructure waterTexture) {
    SummedVolumeTable table = new SummedVolumeTable(blurRadius, minY, maxY);
    for (int y = minY - blurRadius; y < maxY + blurRadius; ++y) {
      for (int x = -blurRadius; x < 16 + blurRadius; ++x) {
        for (int z = -blurRadius; z < 16 + blurRadius; ++z) {
          ChunkPosition ccp = new ChunkPosition(Math.floorDiv(cp.x * 16 + x, 16), Math.floorDiv(cp.z * 16 + z, 16));
          if (nonEmptyChunks.contains(ccp)) {
            int biomeId = biomeIdx.get(cp.x * 16 + x, y, cp.z * 16 + z);
            if (biomeId != -1) {
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

    for (int y = minY; y < maxY; ++y) {
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
