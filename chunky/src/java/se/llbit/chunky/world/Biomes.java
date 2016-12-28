/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

/**
 * Biome constants and utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biomes {
  /**
   * Mask to get valid biome IDs (removes variant bit).
   * <p>
   * Currently we only handle 128 different biomes (40 currently implemented)
   * because biome IDs with the high bit set have the same properties, as far
   * as we are concerned, as the corresponding biome ID without the high bit.
   */
  public static final int BIOME_MASK = 0x7F;

  private static final int SWAMP_ID = 6;

  private static final Biome unknown = new Biome("unknown", 0.5, 0.5, 0x7E7E7E, 0x7E7E7E);
  private static final Biome ocean = new Biome("ocean", 0.5, 0.5, 0x000070, 0x75B646);
  private static final Biome plains = new Biome("plains", 0.8, 0.4, 0x8DB360, 0x8DB84A);
  private static final Biome desert = new Biome("desert", 1.0, 0.0, 0xFA9418, 0x9BA863);
  private static final Biome extremeHills =
      new Biome("extreme hills", 0.2, 0.3, 0x606060, 0x75B646);
  private static final Biome forest = new Biome("forest", 0.7, 0.8, 0x056621, 0x4A8F3A);
  private static final Biome taiga = new Biome("taiga", 0.05, 0.8, 0x00DD2D, 0x478852);
  private static final Biome swampland = new Biome("swampland", 0.8, 0.9, 0x07F9B2, 0x3e5226);
  private static final Biome river = new Biome("river", 0.5, 0.5, 0x0000FF, 0x75B646);
  private static final Biome hell = new Biome("hell", 1.0, 0.0, 0xFF0000, 0x75B646);
  private static final Biome sky = new Biome("sky", 0.5, 0.5, 0x8080FF, 0x75B646);
  private static final Biome frozenOcean = new Biome("frozen ocean", 0.0, 0.5, 0x9090A0, 0x7A9C91);
  private static final Biome frozenRiver = new Biome("frozen river", 0.0, 0.5, 0xA0A0FF, 0x7A9C91);
  private static final Biome icePlains = new Biome("ice plains", 0.0, 0.5, 0xFFFFFF, 0x7A9C91);
  private static final Biome iceMountains =
      new Biome("ice mountains", 0.0, 0.5, 0xA0A0A0, 0x7A9C91);
  private static final Biome mushroomIsland =
      new Biome("mushroom island", 0.9, 1.0, 0xFF00FF, 0x939D88);
  private static final Biome mushroomIslandShore =
      new Biome("mushroom island shore", 0.9, 1.0, 0xA000FF, 0x939D88);
  private static final Biome beach = new Biome("beach", 0.8, 0.4, 0xFADE55, 0x75B646);
  private static final Biome desertHills = new Biome("desert hills", 1.0, 0.0, 0xD25F12, 0x9BA863);
  private static final Biome forestHills = new Biome("forest hills", 0.7, 0.8, 0x22551C, 0x4A8F3A);
  private static final Biome taigaHills = new Biome("taiga hills", 0.05, 0.8, 0x163933, 0x478852);
  private static final Biome extremeHillsEdge =
      new Biome("extreme hills edge", 0.2, 0.3, 0x72789A, 0x75B646);
  private static final Biome jungle = new Biome("jungle", 1.0, 0.9, 0x537B09, 0x3A8B25);
  private static final Biome jungleHills = new Biome("jungle hills", 1.0, 0.9, 0x2C4205, 0x3A8B25);
  private static final Biome jungleEdge = new Biome("jungle edge", 0.95, 0.8, 0x628B17, 0x3EB80F);
  private static final Biome deepOcean = new Biome("deep ocean", 0.5, 0.5, 0x000030, 0x71A74D);
  private static final Biome stoneBeach = new Biome("stone beach", 0.2, 0.3, 0xA2A284, 0x6DA36B);
  private static final Biome coldBeach = new Biome("cold beach", 0.05, 0.3, 0xFAF0C0, 0x64A278);
  private static final Biome birchForest = new Biome("birch forest", 0.7, 0.8, 0x307444, 0x59AE30);
  private static final Biome birchForestHills =
      new Biome("birch forest hills", 0.7, 0.8, 0x1F5F32, 0x59AE30);
  private static final Biome roofedForest =
      new Biome("roofed forest", 0.7, 0.8, 0x40511A, 0x59AE30);
  private static final Biome coldTaiga = new Biome("cold taiga", -0.5, 0.4, 0x31554A, 0x60A17B);
  private static final Biome coldTaigaHills =
      new Biome("cold taiga hills", -0.5, 0.4, 0x243F36, 0x60A17B);
  private static final Biome megaTaiga = new Biome("mega taiga", 0.3, 0.8, 0x596651, 0x68A55F);
  private static final Biome megaTaigaHills =
      new Biome("mega taiga hills", 0.3, 0.8, 0x454F3E, 0x68A55F);
  private static final Biome extremeHillsPlus =
      new Biome("extreme hills+", 0.2, 0.3, 0x507050, 0x6DA36B);
  private static final Biome savanna = new Biome("savanna", 1.2, 0.0, 0xBDB25F, 0xAEA42A);
  private static final Biome savannaPlateau =
      new Biome("savanna plateau", 1.0, 0.0, 0xA79D64, 0xAEA42A);
  private static final Biome mesa = new Biome("mesa", 2.0, 0.0, 0xD94515, 0xAEA42A);
  private static final Biome mesaPlateauF =
      new Biome("mesa plateau f", 2.0, 0.0, 0xB09765, 0xAEA42A);
  private static final Biome mesaPlateau = new Biome("mesa plateau", 2.0, 0.0, 0xCA8C65, 0xAEA42A);

  private static final Biome biomes[] =
      {ocean, plains, desert, extremeHills, forest, taiga, swampland, river, hell, sky, frozenOcean,
          frozenRiver, icePlains, iceMountains, mushroomIsland, mushroomIslandShore, beach,
          desertHills, forestHills, taigaHills, extremeHillsEdge, jungle, jungleHills, jungleEdge,
          deepOcean, stoneBeach, coldBeach, birchForest, birchForestHills, roofedForest, coldTaiga,
          coldTaigaHills, megaTaiga, megaTaigaHills, extremeHillsPlus, savanna, savannaPlateau,
          mesa, mesaPlateauF, mesaPlateau, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
          unknown, unknown};

  private static int[] grassColor = new int[128];
  private static int[] foliageColor = new int[128];
  private static float[][] grassColorLinear = new float[grassColor.length][3];
  private static float[][] foliageColorLinear = new float[grassColor.length][3];

  static {
    for (int i = 0; i < biomes.length; ++i) {
      grassColor[i] = biomes[i].grassColor;
      foliageColor[i] = grassColor[i];
    }
    gammaCorrectColors(grassColor, grassColorLinear);
    gammaCorrectColors(foliageColor, foliageColorLinear);
  }

  /**
   * @return Biome color for given biome ID
   */
  public static int getColor(int biomeId) {
    return biomes[BIOME_MASK & biomeId].mapColor;
  }

  /**
   * Loads grass colors from a grass color texture.
   */
  public static void loadGrassColors(BitmapImage texture) {
    loadColorsFromTexture(grassColor, texture);
    gammaCorrectColors(grassColor, grassColorLinear);
  }

  /**
   * Loads foliage colors from a grass color texture.
   */
  public static void loadFoliageColors(BitmapImage texture) {
    loadColorsFromTexture(foliageColor, texture);
    gammaCorrectColors(foliageColor, foliageColorLinear);
  }

  private static void loadColorsFromTexture(int[] dest, BitmapImage texture) {
    for (int i = 0; i < biomes.length; ++i) {
      double temp = QuickMath.clamp(biomes[i].temp, 0, 1);
      double rain = QuickMath.clamp(biomes[i].rain, 0, 1);
      rain *= temp;
      int color = texture.getPixel((int) ((1 - temp) * 255), (int) ((1 - rain) * 255));
      dest[i] = color;
    }
    // Swamp get special treatment.
    dest[SWAMP_ID] = ((dest[SWAMP_ID] & 0xFEFEFE) + 0x4E0E4E) / 2;
  }

  private static void gammaCorrectColors(int[] src, float[][] dest) {
    float[] frgb = new float[3];
    for (int i = 0; i < src.length; ++i) {
      ColorUtil.getRGBComponents(src[i], frgb);
      dest[i][0] = (float) FastMath.pow(frgb[0], Scene.DEFAULT_GAMMA);
      dest[i][1] = (float) FastMath.pow(frgb[1], Scene.DEFAULT_GAMMA);
      dest[i][2] = (float) FastMath.pow(frgb[2], Scene.DEFAULT_GAMMA);
    }
  }

  /**
   * @param biomeId truncated to [0,127]
   * @return Grass color for the given biome ID
   */
  public static int getGrassColor(int biomeId) {
    return grassColor[BIOME_MASK & biomeId];
  }

  /**
   * @param biomeId truncated to [0,127]
   * @return Foliage color for the given biome ID
   */
  public static int getFoliageColor(int biomeId) {
    return foliageColor[BIOME_MASK & biomeId];
  }

  /**
   * @param biomeId truncated to [0,127]
   * @return Linear biome color for the given biome ID
   */
  public static float[] getGrassColorLinear(int biomeId) {
    return grassColorLinear[BIOME_MASK & biomeId];
  }

  /**
   * @param biomeId truncated to [0,127]
   * @return Linear foliage color for the given biome ID
   */
  public static float[] getFoliageColorLinear(int biomeId) {
    return foliageColorLinear[BIOME_MASK & biomeId];
  }

  /**
   * @param biomeId truncated to [0,127]
   * @return Biome name
   */
  public static String getName(int biomeId) {
    return biomes[BIOME_MASK & biomeId].name;
  }
}
