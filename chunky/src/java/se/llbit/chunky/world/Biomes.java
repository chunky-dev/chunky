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
  public static final int BIOME_MASK = 0xFF;
  private static final int[] SWAMP_IDS = { 6, 134 };
  private static final int[] BADLANDS_IDS = { 37, 38, 39, 165, 166, 167 };
  private static final int[] DARK_FOREST_IDS = { 29, 157 };

  private static final Biome unknown = new Biome("unknown", 0.5, 0.5, 0x7E7E7E, 0x7E7E7E, 0x7E7E7E);

  // The fallback foliage and grass colors of the biomes were calculated with the default resourcepack.
  // The map colors use the default biome colors by Amidst, see https://github.com/toolbox4minecraft/amidst/wiki/Biome-Color-Table.
  private static final Biome ocean = new Biome("Ocean", 0.5, 0.5, 0x000070, 0x8EB971, 0x71A74D);
  private static final Biome plains = new Biome("Plains", 0.8, 0.4, 0x8DB360, 0x91BD59, 0x77AB2F);
  private static final Biome desert = new Biome("Desert", 2, 0, 0xFA9418, 0xBFB755, 0xAEA42A);
  private static final Biome mountains = new Biome("Mountains", 0.2, 0.3, 0x606060, 0x8AB689, 0x6DA36B);
  private static final Biome forest = new Biome("Forest", 0.7, 0.8, 0x056621, 0x79C05A, 0x59AE30);
  private static final Biome taiga = new Biome("Taiga", 0.25, 0.8, 0x0B6659, 0x86B783, 0x68A464);
  private static final Biome swamp = new Biome("Swamp", 0.8, 0.9, 0x07F9B2, 0x6A7039, 0x6A7039, 0x617B64);
  private static final Biome river = new Biome("River", 0.5, 0.5, 0x0000FF, 0x8EB971, 0x71A74D);
  private static final Biome netherWastes = new Biome("Nether Wastes", 2, 0, 0xBF3B3B, 0xBFB755, 0xAEA42A);
  private static final Biome theEnd = new Biome("The End", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome frozenOcean = new Biome("Frozen Ocean", 0, 0.5, 0x7070D6, 0x80B497, 0x60A17B, 0x3938C9);
  private static final Biome frozenRiver = new Biome("Frozen River", 0, 0.5, 0xA0A0FF, 0x80B497, 0x60A17B, 0x3938C9);
  private static final Biome snowyTundra = new Biome("Snowy Tundra", 0, 0.5, 0xFFFFFF, 0x80B497, 0x60A17B);
  private static final Biome snowyMountains = new Biome("Snowy Mountains", 0, 0.5, 0xA0A0A0, 0x80B497, 0x60A17B);
  private static final Biome mushroomFields = new Biome("Mushroom Fields", 0.9, 1, 0xFF00FF, 0x55C93F, 0x2BBB0F);
  private static final Biome mushroomFieldShore = new Biome("Mushroom Field Shore", 0.9, 1, 0xA000FF, 0x55C93F, 0x2BBB0F);
  private static final Biome beach = new Biome("Beach", 0.8, 0.4, 0xFADE55, 0x91BD59, 0x77AB2F);
  private static final Biome desertHills = new Biome("Desert Hills", 2, 0, 0xD25F12, 0xBFB755, 0xAEA42A);
  private static final Biome woodedHills = new Biome("Wooded Hills", 0.7, 0.8, 0x22551C, 0x79C05A, 0x59AE30);
  private static final Biome taigaHills = new Biome("Taiga Hills", 0.25, 0.8, 0x163933, 0x86B783, 0x68A464);
  private static final Biome mountainEdge = new Biome("Mountain Edge", 0.2, 0.3, 0x72789A, 0x8AB689, 0x6DA36B);
  private static final Biome jungle = new Biome("Jungle", 0.95, 0.9, 0x537B09, 0x59C93C, 0x30BB0B);
  private static final Biome jungleHills = new Biome("Jungle Hills", 0.95, 0.9, 0x2C4205, 0x59C93C, 0x30BB0B);
  private static final Biome jungleEdge = new Biome("Jungle Edge", 0.95, 0.8, 0x628B17, 0x64C73F, 0x3EB80F);
  private static final Biome deepOcean = new Biome("Deep Ocean", 0.5, 0.5, 0x000030, 0x8EB971, 0x71A74D);
  private static final Biome stoneShore = new Biome("Stone Shore", 0.2, 0.3, 0xA2A284, 0x8AB689, 0x6DA36B);
  private static final Biome snowyBeach = new Biome("Snowy Beach", 0.05, 0.3, 0xFAF0C0, 0x83B593, 0x64A278);
  private static final Biome birchForest = new Biome("Birch Forest", 0.6, 0.6, 0x307444, 0x88BB67, 0x6BA941);
  private static final Biome birchForestHills = new Biome("Birch Forest Hills", 0.6, 0.6, 0x1F5F32, 0x88BB67, 0x6BA941);
  private static final Biome darkForest = new Biome("Dark Forest", 0.7, 0.8, 0x40511A, 0x79C05A, 0x59AE30);
  private static final Biome snowyTaiga = new Biome("Snowy Taiga", -0.5, 0.4, 0x31554A, 0x80B497, 0x60A17B);
  private static final Biome snowyTaigaHills = new Biome("Snowy Taiga Hills", -0.5, 0.4, 0x243F36, 0x80B497, 0x60A17B);
  private static final Biome giantTreeTaiga = new Biome("Giant Tree Taiga", 0.3, 0.8, 0x596651, 0x86B87F, 0x68A55F);
  private static final Biome giantTreeTaigaHills = new Biome("Giant Tree Taiga Hills", 0.3, 0.8, 0x454F3E, 0x86B87F, 0x68A55F);
  private static final Biome woodedMountains = new Biome("Wooded Mountains", 0.2, 0.3, 0x507050, 0x8AB689, 0x6DA36B);
  private static final Biome savanna = new Biome("Savanna", 1.2, 0, 0xBDB25F, 0xBFB755, 0xAEA42A);
  private static final Biome savannaPlateau = new Biome("Savanna Plateau", 1, 0, 0xA79D64, 0xBFB755, 0xAEA42A);
  private static final Biome badlands = new Biome("Badlands", 2, 0, 0xD94515, 0xBFB755, 0xAEA42A);
  private static final Biome woodedBadlandsPlateau = new Biome("Wooded Badlands Plateau", 2, 0, 0xB09765, 0xBFB755, 0xAEA42A);
  private static final Biome badlandsPlateau = new Biome("Badlands Plateau", 2, 0, 0xCA8C65, 0xBFB755, 0xAEA42A);
  private static final Biome smallEndIslands = new Biome("Small End Islands", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome endMidlands = new Biome("End Midlands", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome endHighlands = new Biome("End Highlands", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome endBarrens = new Biome("End Barrens", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome warmOcean = new Biome("Warm Ocean", 0.5, 0.5, 0x0000AC, 0x8EB971, 0x71A74D, 0x43D5EE);
  private static final Biome lukewarmOcean = new Biome("Lukewarm Ocean", 0.5, 0.5, 0x000090, 0x8EB971, 0x71A74D, 0x45ADF2);
  private static final Biome coldOcean = new Biome("Cold Ocean", 0.5, 0.5, 0x202070, 0x8EB971, 0x71A74D, 0x3D57D6);
  private static final Biome deepWarmOcean = new Biome("Deep Warm Ocean", 0.5, 0.5, 0x000050, 0x8EB971, 0x71A74D, 0x43D5EE);
  private static final Biome deepLukewarmOcean = new Biome("Deep Lukewarm Ocean", 0.5, 0.5, 0x000040, 0x8EB971, 0x71A74D, 0x45ADF2);
  private static final Biome deepColdOcean = new Biome("Deep Cold Ocean", 0.5, 0.5, 0x202038, 0x8EB971, 0x71A74D, 0x3D57D6);
  private static final Biome deepFrozenOcean = new Biome("Deep Frozen Ocean", 0.5, 0.5, 0x404090, 0x8EB971, 0x71A74D, 0x3938C9);
  private static final Biome theVoid = new Biome("The Void", 0.5, 0.5, 0x000000, 0x8EB971, 0x71A74D);
  private static final Biome sunflowerPlains = new Biome("Sunflower Plains", 0.8, 0.4, 0xB5DB88, 0x91BD59, 0x77AB2F);
  private static final Biome desertLakes = new Biome("Desert Lakes", 2, 0, 0xFFBC40, 0xBFB755, 0xAEA42A);
  private static final Biome gravellyMountains = new Biome("Gravelly Mountains", 0.2, 0.3, 0x888888, 0x8AB689, 0x6DA36B);
  private static final Biome flowerForest = new Biome("Flower Forest", 0.7, 0.8, 0x2D8E49, 0x79C05A, 0x59AE30);
  private static final Biome taigaMountains = new Biome("Taiga Mountains", 0.25, 0.8, 0x338E81, 0x86B783, 0x68A464);
  private static final Biome swampHills = new Biome("Swamp Hills", 0.8, 0.9, 0x2FFFDA, 0x6A7039, 0x6A7039, 0x617B64);
  private static final Biome iceSpikes = new Biome("Ice Spikes", 0, 0.5, 0xB4DCDC, 0x80B497, 0x60A17B);
  private static final Biome modifiedJungle = new Biome("Modified Jungle", 0.95, 0.9, 0x7BA331, 0x59C93C, 0x30BB0B);
  private static final Biome modifiedJungleEdge = new Biome("Modified Jungle Edge", 0.95, 0.8, 0x8AB33F, 0x64C73F, 0x3EB80F);
  private static final Biome tallBirchForest = new Biome("Tall Birch Forest", 0.6, 0.6, 0x589C6C, 0x88BB67, 0x6BA941);
  private static final Biome tallBirchHills = new Biome("Tall Birch Hills", 0.6, 0.6, 0x47875A, 0x88BB67, 0x6BA941);
  private static final Biome darkForestHills = new Biome("Dark Forest Hills", 0.7, 0.8, 0x687942, 0x79C05A, 0x59AE30);
  private static final Biome snowyTaigaMountains = new Biome("Snowy Taiga Mountains", -0.5, 0.4, 0x597D72, 0x80B497, 0x60A17B);
  private static final Biome giantSpruceTaiga = new Biome("Giant Spruce Taiga", 0.25, 0.8, 0x818E79, 0x86B783, 0x68A464);
  private static final Biome giantSpruceTaigaHills = new Biome("Giant Spruce Taiga Hills", 0.25, 0.8, 0x6D7766, 0x86B783, 0x68A464);
  private static final Biome modifiedGravellyMountains = new Biome("Gravelly Mountains+", 0.2, 0.3, 0x789878, 0x8AB689, 0x6DA36B);
  private static final Biome shatteredSavanna = new Biome("Shattered Savanna", 1.1, 0, 0xE5DA87, 0xBFB755, 0xAEA42A);
  private static final Biome shatteredSavannaPlateau = new Biome("Shattered Savanna Plateau", 1, 0, 0xCFC58C, 0xBFB755, 0xAEA42A);
  private static final Biome erodedBadlands = new Biome("Eroded Badlands", 2, 0, 0xFF6D3D, 0xBFB755, 0xAEA42A);
  private static final Biome modifiedWoodedBadlandsPlateau = new Biome("Modified Wooded Badlands Plateau", 2, 0, 0xD8BF8D, 0xBFB755, 0xAEA42A);
  private static final Biome modifiedBadlandsPlateau = new Biome("Modified Badlands Plateau", 2, 0, 0xF2B48D, 0xBFB755, 0xAEA42A);
  private static final Biome bambooJungle = new Biome("Bamboo Jungle", 0.95, 0.9, 0x768E14, 0x59C93C, 0x30BB0B);
  private static final Biome bambooJungleHills = new Biome("Bamboo Jungle Hills", 0.95, 0.9, 0x3B470A, 0x59C93C, 0x30BB0B);
  private static final Biome soulSandValley = new Biome("Soul Sand Valley", 2, 0, 0x5E3830, 0xBFB755, 0xAEA42A);
  private static final Biome crimsonForest = new Biome("Crimson Forest", 2, 0, 0xDD0808, 0xBFB755, 0xAEA42A);
  private static final Biome warpedForest = new Biome("Warped Forest", 2, 0, 0x49907B, 0xBFB755, 0xAEA42A);
  private static final Biome basaltDeltas = new Biome("Basalt Deltas", 2, 0, 0x403636, 0xBFB755, 0xAEA42A);
  private static final Biome dripstoneCaves = new Biome("Dripstone Caves", 0.8, 0.4, 0x7B6254, 0x91BD59, 0x77AB2F); // TODO rain value is speculative

  private static final Biome[] biomes = {
      ocean, plains, desert, mountains, forest, taiga, swamp, river,
      netherWastes, theEnd, frozenOcean, frozenRiver, snowyTundra, snowyMountains, mushroomFields, mushroomFieldShore,
      beach, desertHills, woodedHills, taigaHills, mountainEdge, jungle, jungleHills, jungleEdge,
      deepOcean, stoneShore, snowyBeach, birchForest, birchForestHills, darkForest, snowyTaiga, snowyTaigaHills,
      giantTreeTaiga, giantTreeTaigaHills, woodedMountains, savanna, savannaPlateau, badlands, woodedBadlandsPlateau, badlandsPlateau,
      smallEndIslands, endMidlands, endHighlands, endBarrens, warmOcean, lukewarmOcean, coldOcean, deepWarmOcean,
      deepLukewarmOcean, deepColdOcean, deepFrozenOcean, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, theVoid,
      unknown, sunflowerPlains, desertLakes, gravellyMountains, flowerForest, taigaMountains, swampHills, unknown,
      unknown, unknown, unknown, unknown, iceSpikes, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, modifiedJungle, unknown, modifiedJungleEdge,
      unknown, unknown, unknown, tallBirchForest, tallBirchHills, darkForestHills, snowyTaigaMountains, unknown,
      giantSpruceTaiga, giantSpruceTaigaHills, modifiedGravellyMountains, shatteredSavanna, shatteredSavannaPlateau, erodedBadlands, modifiedWoodedBadlandsPlateau, modifiedBadlandsPlateau,
      bambooJungle, bambooJungleHills, soulSandValley, crimsonForest, warpedForest, basaltDeltas, dripstoneCaves, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
      unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown,
  };

  private static int[] grassColor = new int[biomes.length];
  private static int[] foliageColor = new int[biomes.length];
  private static int[] waterColor = new int[biomes.length];
  private static float[][] grassColorLinear = new float[grassColor.length][3];
  private static float[][] foliageColorLinear = new float[grassColor.length][3];
  private static float[][] waterColorLinear = new float[grassColor.length][3];

  static {
    for (int i = 0; i < biomes.length; ++i) {
      grassColor[i] = biomes[i].grassColor;
      foliageColor[i] = biomes[i].foliageColor;
      waterColor[i] = biomes[i].waterColor;
    }
    gammaCorrectColors(grassColor, grassColorLinear);
    gammaCorrectColors(foliageColor, foliageColorLinear);
    gammaCorrectColors(waterColor, waterColorLinear);
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
    
    // Dark forest biomes' grass color is retrieved normally, then averaged with 0x28340A to produce the final color
    float[] color = new float[3];
    for (int id : DARK_FOREST_IDS) {
      ColorUtil.getRGBComponents(grassColor[id], color);
      color[0] += 40 / 255.f;
      color[1] += 52 / 255.f;
      color[2] += 10 / 255.f;
      grassColor[id] = ColorUtil.getRGB(color[0] / 2, color[1] / 2, color[2] / 2);
    }
    
    // Badland biome's grass color is hardcoded
    for (int id : BADLANDS_IDS) {
      grassColor[id] = 0x90814D;
    }
    
    gammaCorrectColors(grassColor, grassColorLinear);
  }

  /**
   * Loads foliage colors from a grass color texture.
   */
  public static void loadFoliageColors(BitmapImage texture) {
    loadColorsFromTexture(foliageColor, texture);

    // Badland biome's foliage colors are hardcoded
    for (int id : BADLANDS_IDS) {
      foliageColor[id] = 0x9E814D;
    }

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

    // Swamp biome's grass and foliage colors are hardcoded
    // (actually perlin noise with two colors in Java Edition, for we use one color as in Bedrock Edition)
    for (int id : SWAMP_IDS) {
      dest[id] = 0x6A7039;
    }
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
   * @return Water color for the given biome ID
   */
  public static int getWaterColor(int biomeId) {
    return waterColor[BIOME_MASK & biomeId];
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
   * @return Linear water color for the given biome ID
   */
  public static float[] getWaterColorLinear(int biomeId) {
    return waterColorLinear[BIOME_MASK & biomeId];
  }

  /**
   * @param biomeId truncated to [0,127]
   * @return Biome name
   */
  public static String getName(int biomeId) {
    return biomes[BIOME_MASK & biomeId].name;
  }
}
