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
package se.llbit.chunky.world.biome;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

import java.util.Map;
import java.util.function.BiConsumer;

import static se.llbit.math.ColorUtil.getRGBAComponentsGammaCorrected;

/**
 * Biome constants and utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biomes {
  public static final int BIOME_MASK = 0xFF;

  public static final Biome unknown = new Biome("", "unknown", 0.5, 0.5, 0x7E7E7E, 0x7E7E7E, 0x7E7E7E);

  // The fallback foliage and grass colors of the biomes were calculated with the default resourcepack.
  // The map colors use the default biome colors by Amidst, see https://github.com/toolbox4minecraft/amidst/wiki/Biome-Color-Table.
  private static final Biome ocean = new Biome("minecraft:ocean", "Ocean", 0.5, 0.5, 0x000070, 0x8EB971, 0x71A74D);
  private static final Biome plains = new Biome("minecraft:plains", "Plains", 0.8, 0.4, 0x8DB360, 0x91BD59, 0x77AB2F);
  private static final Biome desert = new Biome("minecraft:desert", "Desert", 2, 0, 0xFA9418, 0xBFB755, 0xAEA42A);
  private static final Biome mountains = new Biome("minecraft:mountains", "Mountains", 0.2, 0.3, 0x606060, 0x8AB689, 0x6DA36B);
  private static final Biome forest = new Biome("minecraft:forest", "Forest", 0.7, 0.8, 0x056621, 0x79C05A, 0x59AE30);
  private static final Biome taiga = new Biome("minecraft:taiga", "Taiga", 0.25, 0.8, 0x0B6659, 0x86B783, 0x68A464);
  private static final Biome swamp = new Biome("minecraft:swamp", "Swamp", 0.8, 0.9, 0x07F9B2, 0x6A7039, 0x6A7039, 0x617B64);
  private static final Biome river = new Biome("minecraft:river", "River", 0.5, 0.5, 0x0000FF, 0x8EB971, 0x71A74D);
  private static final Biome netherWastes = new Biome("minecraft:nether_wastes", "Nether Wastes", 2, 0, 0xBF3B3B, 0xBFB755, 0xAEA42A);
  private static final Biome theEnd = new Biome("minecraft:the_end", "The End", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome frozenOcean = new Biome("minecraft:frozen_ocean", "Frozen Ocean", 0, 0.5, 0x7070D6, 0x80B497, 0x60A17B, 0x3938C9);
  private static final Biome frozenRiver = new Biome("minecraft:frozen_river", "Frozen River", 0, 0.5, 0xA0A0FF, 0x80B497, 0x60A17B, 0x3938C9);
  private static final Biome snowyTundra = new Biome("minecraft:snowy_tundra", "Snowy Tundra", 0, 0.5, 0xFFFFFF, 0x80B497, 0x60A17B);
  private static final Biome snowyMountains = new Biome("minecraft:snowy_mountains", "Snowy Mountains", 0, 0.5, 0xA0A0A0, 0x80B497, 0x60A17B);
  private static final Biome mushroomFields = new Biome("minecraft:mushroom_fields", "Mushroom Fields", 0.9, 1, 0xFF00FF, 0x55C93F, 0x2BBB0F);
  private static final Biome mushroomFieldShore = new Biome("minecraft:mushroom_field_shore", "Mushroom Field Shore", 0.9, 1, 0xA000FF, 0x55C93F, 0x2BBB0F);
  private static final Biome beach = new Biome("minecraft:beach", "Beach", 0.8, 0.4, 0xFADE55, 0x91BD59, 0x77AB2F);
  private static final Biome desertHills = new Biome("minecraft:desert_hills", "Desert Hills", 2, 0, 0xD25F12, 0xBFB755, 0xAEA42A);
  private static final Biome woodedHills = new Biome("minecraft:wooded_hills", "Wooded Hills", 0.7, 0.8, 0x22551C, 0x79C05A, 0x59AE30);
  private static final Biome taigaHills = new Biome("minecraft:taiga_hills", "Taiga Hills", 0.25, 0.8, 0x163933, 0x86B783, 0x68A464);
  private static final Biome mountainEdge = new Biome("minecraft:mountain_edge", "Mountain Edge", 0.2, 0.3, 0x72789A, 0x8AB689, 0x6DA36B);
  private static final Biome jungle = new Biome("minecraft:jungle", "Jungle", 0.95, 0.9, 0x537B09, 0x59C93C, 0x30BB0B);
  private static final Biome jungleHills = new Biome("minecraft:jungle_hills", "Jungle Hills", 0.95, 0.9, 0x2C4205, 0x59C93C, 0x30BB0B);
  private static final Biome jungleEdge = new Biome("minecraft:jungle_edge", "Jungle Edge", 0.95, 0.8, 0x628B17, 0x64C73F, 0x3EB80F);
  private static final Biome deepOcean = new Biome("minecraft:deep_ocean", "Deep Ocean", 0.5, 0.5, 0x000030, 0x8EB971, 0x71A74D);
  private static final Biome stoneShore = new Biome("minecraft:stone_shore", "Stone Shore", 0.2, 0.3, 0xA2A284, 0x8AB689, 0x6DA36B);
  private static final Biome snowyBeach = new Biome("minecraft:snowy_beach", "Snowy Beach", 0.05, 0.3, 0xFAF0C0, 0x83B593, 0x64A278);
  private static final Biome birchForest = new Biome("minecraft:birch_forest", "Birch Forest", 0.6, 0.6, 0x307444, 0x88BB67, 0x6BA941);
  private static final Biome birchForestHills = new Biome("minecraft:birch_forest_hills", "Birch Forest Hills", 0.6, 0.6, 0x1F5F32, 0x88BB67, 0x6BA941);
  private static final Biome darkForest = new Biome("minecraft:dark_forest", "Dark Forest", 0.7, 0.8, 0x40511A, 0x79C05A, 0x59AE30);
  private static final Biome snowyTaiga = new Biome("minecraft:snowy_taiga", "Snowy Taiga", -0.5, 0.4, 0x31554A, 0x80B497, 0x60A17B);
  private static final Biome snowyTaigaHills = new Biome("minecraft:snowy_taiga_hills", "Snowy Taiga Hills", -0.5, 0.4, 0x243F36, 0x80B497, 0x60A17B);
  private static final Biome giantTreeTaiga = new Biome("minecraft:giant_tree_taiga", "Giant Tree Taiga", 0.3, 0.8, 0x596651, 0x86B87F, 0x68A55F);
  private static final Biome giantTreeTaigaHills = new Biome("minecraft:giant_tree_taiga_hills", "Giant Tree Taiga Hills", 0.3, 0.8, 0x454F3E, 0x86B87F, 0x68A55F);
  private static final Biome woodedMountains = new Biome("minecraft:wooded_mountains", "Wooded Mountains", 0.2, 0.3, 0x507050, 0x8AB689, 0x6DA36B);
  private static final Biome savanna = new Biome("minecraft:savanna", "Savanna", 1.2, 0, 0xBDB25F, 0xBFB755, 0xAEA42A);
  private static final Biome savannaPlateau = new Biome("minecraft:savanna_plateau", "Savanna Plateau", 1, 0, 0xA79D64, 0xBFB755, 0xAEA42A);
  private static final Biome badlands = new Biome("minecraft:badlands", "Badlands", 2, 0, 0xD94515, 0xBFB755, 0xAEA42A);
  private static final Biome woodedBadlandsPlateau = new Biome("minecraft:wooded_badlands_plateau", "Wooded Badlands Plateau", 2, 0, 0xB09765, 0xBFB755, 0xAEA42A);
  private static final Biome badlandsPlateau = new Biome("minecraft:badlands_plateau", "Badlands Plateau", 2, 0, 0xCA8C65, 0xBFB755, 0xAEA42A);
  private static final Biome smallEndIslands = new Biome("minecraft:small_end_islands", "Small End Islands", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome endMidlands = new Biome("minecraft:end_midlands", "End Midlands", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome endHighlands = new Biome("minecraft:end_highlands", "End Highlands", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome endBarrens = new Biome("minecraft:end_barrens", "End Barrens", 0.5, 0.5, 0x8080FF, 0x8EB971, 0x71A74D);
  private static final Biome warmOcean = new Biome("minecraft:warm_ocean", "Warm Ocean", 0.5, 0.5, 0x0000AC, 0x8EB971, 0x71A74D, 0x43D5EE);
  private static final Biome lukewarmOcean = new Biome("minecraft:lukewarm_ocean", "Lukewarm Ocean", 0.5, 0.5, 0x000090, 0x8EB971, 0x71A74D, 0x45ADF2);
  private static final Biome coldOcean = new Biome("minecraft:cold_ocean", "Cold Ocean", 0.5, 0.5, 0x202070, 0x8EB971, 0x71A74D, 0x3D57D6);
  private static final Biome deepWarmOcean = new Biome("minecraft:deep_warm_ocean", "Deep Warm Ocean", 0.5, 0.5, 0x000050, 0x8EB971, 0x71A74D, 0x43D5EE);
  private static final Biome deepLukewarmOcean = new Biome("minecraft:deep_lukewarm_ocean", "Deep Lukewarm Ocean", 0.5, 0.5, 0x000040, 0x8EB971, 0x71A74D, 0x45ADF2);
  private static final Biome deepColdOcean = new Biome("minecraft:deep_cold_ocean", "Deep Cold Ocean", 0.5, 0.5, 0x202038, 0x8EB971, 0x71A74D, 0x3D57D6);
  private static final Biome deepFrozenOcean = new Biome("minecraft:deep_frozen_ocean", "Deep Frozen Ocean", 0.5, 0.5, 0x404090, 0x8EB971, 0x71A74D, 0x3938C9);
  private static final Biome theVoid = new Biome("minecraft:the_void", "The Void", 0.5, 0.5, 0x000000, 0x8EB971, 0x71A74D);
  private static final Biome sunflowerPlains = new Biome("minecraft:sunflower_plains", "Sunflower Plains", 0.8, 0.4, 0xB5DB88, 0x91BD59, 0x77AB2F);
  private static final Biome desertLakes = new Biome("minecraft:desert_lakes", "Desert Lakes", 2, 0, 0xFFBC40, 0xBFB755, 0xAEA42A);
  private static final Biome gravellyMountains = new Biome("minecraft:gravelly_mountains", "Gravelly Mountains", 0.2, 0.3, 0x888888, 0x8AB689, 0x6DA36B);
  private static final Biome flowerForest = new Biome("minecraft:flower_forest", "Flower Forest", 0.7, 0.8, 0x2D8E49, 0x79C05A, 0x59AE30);
  private static final Biome taigaMountains = new Biome("minecraft:taiga_mountains", "Taiga Mountains", 0.25, 0.8, 0x338E81, 0x86B783, 0x68A464);
  private static final Biome swampHills = new Biome("minecraft:swamp_hills", "Swamp Hills", 0.8, 0.9, 0x2FFFDA, 0x6A7039, 0x6A7039, 0x617B64);
  private static final Biome iceSpikes = new Biome("minecraft:ice_spikes", "Ice Spikes", 0, 0.5, 0xB4DCDC, 0x80B497, 0x60A17B);
  private static final Biome modifiedJungle = new Biome("minecraft:modified_jungle", "Modified Jungle", 0.95, 0.9, 0x7BA331, 0x59C93C, 0x30BB0B);
  private static final Biome modifiedJungleEdge = new Biome("minecraft:modified_jungle_edge", "Modified Jungle Edge", 0.95, 0.8, 0x8AB33F, 0x64C73F, 0x3EB80F);
  private static final Biome tallBirchForest = new Biome("minecraft:tall_birch_forest", "Tall Birch Forest", 0.6, 0.6, 0x589C6C, 0x88BB67, 0x6BA941);
  private static final Biome tallBirchHills = new Biome("minecraft:tall_birch_hills", "Tall Birch Hills", 0.6, 0.6, 0x47875A, 0x88BB67, 0x6BA941);
  private static final Biome darkForestHills = new Biome("minecraft:dark_forest_hills", "Dark Forest Hills", 0.7, 0.8, 0x687942, 0x79C05A, 0x59AE30);
  private static final Biome snowyTaigaMountains = new Biome("minecraft:snowy_taiga_mountains", "Snowy Taiga Mountains", -0.5, 0.4, 0x597D72, 0x80B497, 0x60A17B);
  private static final Biome giantSpruceTaiga = new Biome("minecraft:giant_spruce_taiga", "Giant Spruce Taiga", 0.25, 0.8, 0x818E79, 0x86B783, 0x68A464);
  private static final Biome giantSpruceTaigaHills = new Biome("minecraft:giant_spruce_taiga_hills", "Giant Spruce Taiga Hills", 0.25, 0.8, 0x6D7766, 0x86B783, 0x68A464);
  private static final Biome modifiedGravellyMountains = new Biome("minecraft:modified_gravelly_mountains+", "Gravelly Mountains+", 0.2, 0.3, 0x789878, 0x8AB689, 0x6DA36B);
  private static final Biome shatteredSavanna = new Biome("minecraft:shattered_savanna", "Shattered Savanna", 1.1, 0, 0xE5DA87, 0xBFB755, 0xAEA42A);
  private static final Biome shatteredSavannaPlateau = new Biome("minecraft:shattered_savanna_plateau", "Shattered Savanna Plateau", 1, 0, 0xCFC58C, 0xBFB755, 0xAEA42A);
  private static final Biome erodedBadlands = new Biome("minecraft:eroded_badlands", "Eroded Badlands", 2, 0, 0xFF6D3D, 0xBFB755, 0xAEA42A);
  private static final Biome modifiedWoodedBadlandsPlateau = new Biome("minecraft:modified_wooded_badlands_plateau", "Modified Wooded Badlands Plateau", 2, 0, 0xD8BF8D, 0xBFB755, 0xAEA42A);
  private static final Biome modifiedBadlandsPlateau = new Biome("minecraft:modified_badlands_plateau", "Modified Badlands Plateau", 2, 0, 0xF2B48D, 0xBFB755, 0xAEA42A);
  private static final Biome bambooJungle = new Biome("minecraft:bamboo_jungle", "Bamboo Jungle", 0.95, 0.9, 0x768E14, 0x59C93C, 0x30BB0B);
  private static final Biome bambooJungleHills = new Biome("minecraft:bamboo_jungle_hills", "Bamboo Jungle Hills", 0.95, 0.9, 0x3B470A, 0x59C93C, 0x30BB0B);
  private static final Biome soulSandValley = new Biome("minecraft:soul_sand_valley", "Soul Sand Valley", 2, 0, 0x5E3830, 0xBFB755, 0xAEA42A);
  private static final Biome crimsonForest = new Biome("minecraft:crimson_forest", "Crimson Forest", 2, 0, 0xDD0808, 0xBFB755, 0xAEA42A);
  private static final Biome warpedForest = new Biome("minecraft:warped_forest", "Warped Forest", 2, 0, 0x49907B, 0xBFB755, 0xAEA42A);
  private static final Biome basaltDeltas = new Biome("minecraft:basalt_deltas", "Basalt Deltas", 2, 0, 0x403636, 0xBFB755, 0xAEA42A);
  private static final Biome dripstoneCaves = new Biome("minecraft:dripstone_caves", "Dripstone Caves", 0.8, 0.4, 0x7B6254, 0x91BD59, 0x77AB2F);

  private static final Biome[] SWAMP_BIOMES = { swamp, swampHills };
  private static final Biome[] BADLANDS_BIOMES = { badlands, woodedBadlandsPlateau, badlandsPlateau, erodedBadlands, modifiedWoodedBadlandsPlateau, modifiedBadlandsPlateau };
  private static final Biome[] DARK_FOREST_BIOMES = { darkForest, darkForestHills };

  public static final Map<String, Biome> biomesByResourceLocation = new Object2ReferenceOpenHashMap<>();
  public static final Object2IntMap<String> biomeIDsByResourceLocation = new Object2IntOpenHashMap<>();

  public static final Biome[] biomes = {
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

  static {
    int i = 0;
    for (Biome biome : biomes) {
      biomesByResourceLocation.put(biome.resourceLocation, biome);
      biomeIDsByResourceLocation.put(biome.resourceLocation, i);
      ++i;
    }
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
    loadColorsFromTexture((biome, color) -> biome.grassColor = color, texture);
    
    // Dark forest biomes' grass color is retrieved normally, then averaged with 0x28340A to produce the final color
    float[] color = new float[3];
    for (Biome biome : DARK_FOREST_BIOMES) {
      ColorUtil.getRGBComponents(biome.grassColor, color);
      color[0] += 40 / 255.f;
      color[1] += 52 / 255.f;
      color[2] += 10 / 255.f;
      biome.grassColor = ColorUtil.getRGB(color[0] / 2, color[1] / 2, color[2] / 2);
    }
    
    // Badland biome's grass color is hardcoded
    for (Biome biome : BADLANDS_BIOMES) {
      biome.grassColor = 0x90814D;
    }

    for (Biome biome : biomes) {
      biome.grassColorLinear = getRGBAComponentsGammaCorrected(biome.grassColor);
    }
  }

  /**
   * Loads foliage colors from a grass color texture.
   */
  public static void loadFoliageColors(BitmapImage texture) {
    loadColorsFromTexture((biome, color) -> biome.foliageColor = color, texture);

    // Badland biome's foliage colors are hardcoded
    for (Biome biome : BADLANDS_BIOMES) {
      biome.foliageColor = 0x9E814D;
    }

    for (Biome biome : biomes) {
      biome.foliageColorLinear = getRGBAComponentsGammaCorrected(biome.foliageColor);
    }
  }

  private static void loadColorsFromTexture(BiConsumer<Biome, Integer> colorConsumer, BitmapImage texture) {
    for (Biome biome : biomes) {
      double temp = QuickMath.clamp(biome.temperature, 0, 1);
      double rain = QuickMath.clamp(biome.rain, 0, 1);
      rain *= temp;
      int color = texture.getPixel((int) ((1 - temp) * 255), (int) ((1 - rain) * 255));
      colorConsumer.accept(biome, color);
    }

    // Swamp biome's grass and foliage colors are hardcoded
    // (actually perlin noise with two colors in Java Edition, for we use one color as in Bedrock Edition)
    for (Biome biome : SWAMP_BIOMES) {
      colorConsumer.accept(biome, 0x6A7039);
    }
  }
}
