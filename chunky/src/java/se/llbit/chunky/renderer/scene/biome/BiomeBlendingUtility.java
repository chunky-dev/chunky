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
   * Compute the blended biome colors for a chunk by doing a 2D blur and store the result in the given biome structures
   * Sample the biome at a given y level and writes the result for y levels going from samplingY (inclusive) to maxFillY (exclusive)
   */
  static public void chunk2DBlur(ChunkPosition cp, int blurRadius, int samplingY, int maxFillY, Vector3i origin, Position2IntStructure biomeIdx, BiomePalette biomePalette, Set<ChunkPosition> nonEmptyChunks, BiomeStructure grassTexture, BiomeStructure foliageTexture, BiomeStructure waterTexture) {
    for (int x = 0; x < 16; ++x) {
      for (int z = 0; z < 16; ++z) {
        int nsum = 0;
        float[] grassMix = {0, 0, 0};
        float[] foliageMix = {0, 0, 0};
        float[] waterMix = {0, 0, 0};
        for (int sx = x - blurRadius; sx <= x + blurRadius; ++sx) {
          int wx = cp.x * 16 + sx;
          for (int sz = z - blurRadius; sz <= z + blurRadius; ++sz) {
            int wz = cp.z * 16 + sz;

            ChunkPosition ccp = new ChunkPosition(wx >> 4, wz >> 4);
            if (nonEmptyChunks.contains(ccp)) {
              nsum += 1;
              Biome biome = biomePalette.get(biomeIdx.get(wx, samplingY, wz));
              addEqual(grassMix, biome.grassColorLinear);
              addEqual(foliageMix, biome.foliageColorLinear);
              addEqual(waterMix, biome.waterColorLinear);
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
