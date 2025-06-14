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
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static se.llbit.math.ColorUtil.getRGBAComponentsGammaCorrected;

/**
 * Biome constants and utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biomes {
  public static final Biome unknown = Biome.create("", "unknown", 0.5, 0.5).mapColor(0x7E7E7E).grassColor(0x8EB971).foliageColor(0x71A74D).build();

  public static final Map<String, Biome> biomesByResourceLocation = new Object2ReferenceOpenHashMap<>();
  public static final Object2IntMap<String> biomeIDsByResourceLocation = new Object2IntOpenHashMap<>();

  public static final List<Biome> biomes = new ArrayList<>();
  private static final List<Biome> minecraftBiomes = new ArrayList<>();

  // The fallback foliage and grass colors of the biomes were calculated with the default resourcepack.
  // The map colors use the default biome colors by Amidst, see https://github.com/toolbox4minecraft/amidst/wiki/Biome-Color-Table.
  // The generator script source is https://github.com/leMaik/chunky-biomegen
  private static final Biome ocean = register(Biome.create("minecraft:ocean", "Ocean", 0.5, 0.5).mapColor(0x000070).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome plains = register(Biome.create("minecraft:plains", "Plains", 0.8, 0.4).mapColor(0x8DB360).defaultColors(0x91BD59, 0x77AB2F));
  private static final Biome desert = register(Biome.create("minecraft:desert", "Desert", 2, 0).mapColor(0xFA9418).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome mountains = register(Biome.create("minecraft:mountains", "Mountains", 0.2, 0.3).mapColor(0x606060).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome forest = register(Biome.create("minecraft:forest", "Forest", 0.7, 0.8).mapColor(0x056621).defaultColors(0x79C05A, 0x59AE30));
  private static final Biome taiga = register(Biome.create("minecraft:taiga", "Taiga", 0.25, 0.8).mapColor(0x0B6659).defaultColors(0x86B783, 0x68A464));
  private static final Biome swamp = register(Biome.create("minecraft:swamp", "Swamp", 0.8, 0.9).defaultColors(0x6A7039, 0x6A7039).waterColor(0x617B64).mapColor(0x07F9B2).swamp());
  private static final Biome river = register(Biome.create("minecraft:river", "River", 0.5, 0.5).mapColor(0x0000FF).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome netherWastes = register(Biome.create("minecraft:nether_wastes", "Nether Wastes", 2, 0).mapColor(0xBF3B3B).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome theEnd = register(Biome.create("minecraft:the_end", "The End", 0.5, 0.5).mapColor(0x8080FF).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome frozenOcean = register(Biome.create("minecraft:frozen_ocean", "Frozen Ocean", 0, 0.5).mapColor(0x7070D6).defaultColors(0x80B497, 0x60A17B).waterColor(0x3938C9));
  private static final Biome frozenRiver = register(Biome.create("minecraft:frozen_river", "Frozen River", 0, 0.5).mapColor(0xA0A0FF).defaultColors(0x80B497, 0x60A17B).waterColor(0x3938C9));
  private static final Biome snowyTundra = register(Biome.create("minecraft:snowy_tundra", "Snowy Tundra", 0, 0.5).mapColor(0xFFFFFF).defaultColors(0x80B497, 0x60A17B));
  private static final Biome snowyMountains = register(Biome.create("minecraft:snowy_mountains", "Snowy Mountains", 0, 0.5).mapColor(0xA0A0A0).defaultColors(0x80B497, 0x60A17B));
  private static final Biome mushroomFields = register(Biome.create("minecraft:mushroom_fields", "Mushroom Fields", 0.9, 1).mapColor(0xFF00FF).defaultColors(0x55C93F, 0x2BBB0F));
  private static final Biome mushroomFieldShore = register(Biome.create("minecraft:mushroom_field_shore", "Mushroom Field Shore", 0.9, 1).mapColor(0xA000FF).defaultColors(0x55C93F, 0x2BBB0F));
  private static final Biome beach = register(Biome.create("minecraft:beach", "Beach", 0.8, 0.4).mapColor(0xFADE55).defaultColors(0x91BD59, 0x77AB2F));
  private static final Biome desertHills = register(Biome.create("minecraft:desert_hills", "Desert Hills", 2, 0).mapColor(0xD25F12).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome woodedHills = register(Biome.create("minecraft:wooded_hills", "Wooded Hills", 0.7, 0.8).mapColor(0x22551C).defaultColors(0x79C05A, 0x59AE30));
  private static final Biome taigaHills = register(Biome.create("minecraft:taiga_hills", "Taiga Hills", 0.25, 0.8).mapColor(0x163933).defaultColors(0x86B783, 0x68A464));
  private static final Biome mountainEdge = register(Biome.create("minecraft:mountain_edge", "Mountain Edge", 0.2, 0.3).mapColor(0x72789A).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome jungle = register(Biome.create("minecraft:jungle", "Jungle", 0.95, 0.9).mapColor(0x537B09).defaultColors(0x59C93C, 0x30BB0B));
  private static final Biome jungleHills = register(Biome.create("minecraft:jungle_hills", "Jungle Hills", 0.95, 0.9).mapColor(0x2C4205).defaultColors(0x59C93C, 0x30BB0B));
  private static final Biome jungleEdge = register(Biome.create("minecraft:jungle_edge", "Jungle Edge", 0.95, 0.8).mapColor(0x628B17).defaultColors(0x64C73F, 0x3EB80F));
  private static final Biome deepOcean = register(Biome.create("minecraft:deep_ocean", "Deep Ocean", 0.5, 0.5).mapColor(0x000030).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome stoneShore = register(Biome.create("minecraft:stone_shore", "Stone Shore", 0.2, 0.3).mapColor(0xA2A284).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome snowyBeach = register(Biome.create("minecraft:snowy_beach", "Snowy Beach", 0.05, 0.3).mapColor(0xFAF0C0).defaultColors(0x83B593, 0x64A278));
  private static final Biome birchForest = register(Biome.create("minecraft:birch_forest", "Birch Forest", 0.6, 0.6).mapColor(0x307444).defaultColors(0x88BB67, 0x6BA941));
  private static final Biome birchForestHills = register(Biome.create("minecraft:birch_forest_hills", "Birch Forest Hills", 0.6, 0.6).mapColor(0x1F5F32).defaultColors(0x88BB67, 0x6BA941));
  private static final Biome darkForest = register(Biome.create("minecraft:dark_forest", "Dark Forest", 0.7, 0.8).defaultColors(0x26C05A, 0x59AE30).mapColor(0x40511A).darkForest());
  private static final Biome snowyTaiga = register(Biome.create("minecraft:snowy_taiga", "Snowy Taiga", -0.5, 0.4).mapColor(0x31554A).defaultColors(0x80B497, 0x60A17B));
  private static final Biome snowyTaigaHills = register(Biome.create("minecraft:snowy_taiga_hills", "Snowy Taiga Hills", -0.5, 0.4).mapColor(0x243F36).defaultColors(0x80B497, 0x60A17B));
  private static final Biome giantTreeTaiga = register(Biome.create("minecraft:giant_tree_taiga", "Giant Tree Taiga", 0.3, 0.8).mapColor(0x596651).defaultColors(0x86B87F, 0x68A55F));
  private static final Biome giantTreeTaigaHills = register(Biome.create("minecraft:giant_tree_taiga_hills", "Giant Tree Taiga Hills", 0.3, 0.8).mapColor(0x454F3E).defaultColors(0x86B87F, 0x68A55F));
  private static final Biome woodedMountains = register(Biome.create("minecraft:wooded_mountains", "Wooded Mountains", 0.2, 0.3).mapColor(0x507050).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome savanna = register(Biome.create("minecraft:savanna", "Savanna", 1.2, 0).mapColor(0xBDB25F).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome savannaPlateau = register(Biome.create("minecraft:savanna_plateau", "Savanna Plateau", 1, 0).mapColor(0xA79D64).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome badlands = register(Biome.create("minecraft:badlands", "Badlands", 2, 0).mapColor(0xD94515).badlands());
  private static final Biome woodedBadlandsPlateau = register(Biome.create("minecraft:wooded_badlands_plateau", "Wooded Badlands Plateau", 2, 0).mapColor(0xB09765).badlands());
  private static final Biome badlandsPlateau = register(Biome.create("minecraft:badlands_plateau", "Badlands Plateau", 2, 0).mapColor(0xCA8C65).badlands());
  private static final Biome smallEndIslands = register(Biome.create("minecraft:small_end_islands", "Small End Islands", 0.5, 0.5).mapColor(0x8080FF).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome endMidlands = register(Biome.create("minecraft:end_midlands", "End Midlands", 0.5, 0.5).mapColor(0x8080FF).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome endHighlands = register(Biome.create("minecraft:end_highlands", "End Highlands", 0.5, 0.5).mapColor(0x8080FF).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome endBarrens = register(Biome.create("minecraft:end_barrens", "End Barrens", 0.5, 0.5).mapColor(0x8080FF).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome warmOcean = register(Biome.create("minecraft:warm_ocean", "Warm Ocean", 0.5, 0.5).mapColor(0x0000AC).defaultColors(0x8EB971, 0x71A74D).waterColor(0x43D5EE));
  private static final Biome lukewarmOcean = register(Biome.create("minecraft:lukewarm_ocean", "Lukewarm Ocean", 0.5, 0.5).mapColor(0x000090).defaultColors(0x8EB971, 0x71A74D).waterColor(0x45ADF2));
  private static final Biome coldOcean = register(Biome.create("minecraft:cold_ocean", "Cold Ocean", 0.5, 0.5).mapColor(0x202070).defaultColors(0x8EB971, 0x71A74D).waterColor(0x3D57D6));
  private static final Biome deepWarmOcean = register(Biome.create("minecraft:deep_warm_ocean", "Deep Warm Ocean", 0.5, 0.5).mapColor(0x000050).defaultColors(0x8EB971, 0x71A74D).waterColor(0x43D5EE));
  private static final Biome deepLukewarmOcean = register(Biome.create("minecraft:deep_lukewarm_ocean", "Deep Lukewarm Ocean", 0.5, 0.5).mapColor(0x000040).defaultColors(0x8EB971, 0x71A74D).waterColor(0x45ADF2));
  private static final Biome deepColdOcean = register(Biome.create("minecraft:deep_cold_ocean", "Deep Cold Ocean", 0.5, 0.5).mapColor(0x202038).defaultColors(0x8EB971, 0x71A74D).waterColor(0x3D57D6));
  private static final Biome deepFrozenOcean = register(Biome.create("minecraft:deep_frozen_ocean", "Deep Frozen Ocean", 0.5, 0.5).mapColor(0x404090).defaultColors(0x8EB971, 0x71A74D).waterColor(0x3938C9));
  private static final Biome theVoid = register(Biome.create("minecraft:the_void", "The Void", 0.5, 0.5).mapColor(0x000000).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome sunflowerPlains = register(Biome.create("minecraft:sunflower_plains", "Sunflower Plains", 0.8, 0.4).mapColor(0xB5DB88).defaultColors(0x91BD59, 0x77AB2F));
  private static final Biome desertLakes = register(Biome.create("minecraft:desert_lakes", "Desert Lakes", 2, 0).mapColor(0xFFBC40).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome gravellyMountains = register(Biome.create("minecraft:gravelly_mountains", "Gravelly Mountains", 0.2, 0.3).mapColor(0x888888).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome flowerForest = register(Biome.create("minecraft:flower_forest", "Flower Forest", 0.7, 0.8).mapColor(0x2D8E49).defaultColors(0x79C05A, 0x59AE30));
  private static final Biome taigaMountains = register(Biome.create("minecraft:taiga_mountains", "Taiga Mountains", 0.25, 0.8).mapColor(0x338E81).defaultColors(0x86B783, 0x68A464));
  private static final Biome swampHills = register(Biome.create("minecraft:swamp_hills", "Swamp Hills", 0.8, 0.9).defaultColors(0x6A7039, 0x6A7039).waterColor(0x617B64).mapColor(0x2FFFDA).swamp());
  private static final Biome iceSpikes = register(Biome.create("minecraft:ice_spikes", "Ice Spikes", 0, 0.5).mapColor(0xB4DCDC).defaultColors(0x80B497, 0x60A17B));
  private static final Biome modifiedJungle = register(Biome.create("minecraft:modified_jungle", "Modified Jungle", 0.95, 0.9).mapColor(0x7BA331).defaultColors(0x59C93C, 0x30BB0B));
  private static final Biome modifiedJungleEdge = register(Biome.create("minecraft:modified_jungle_edge", "Modified Jungle Edge", 0.95, 0.8).mapColor(0x8AB33F).defaultColors(0x64C73F, 0x3EB80F));
  private static final Biome tallBirchForest = register(Biome.create("minecraft:tall_birch_forest", "Tall Birch Forest", 0.6, 0.6).mapColor(0x589C6C).defaultColors(0x88BB67, 0x6BA941));
  private static final Biome tallBirchHills = register(Biome.create("minecraft:tall_birch_hills", "Tall Birch Hills", 0.6, 0.6).mapColor(0x47875A).defaultColors(0x88BB67, 0x6BA941));
  private static final Biome darkForestHills = register(Biome.create("minecraft:dark_forest_hills", "Dark Forest Hills", 0.7, 0.8).defaultColors(0x1BC05A, 0x59AE30).mapColor(0x687942).darkForest());
  private static final Biome snowyTaigaMountains = register(Biome.create("minecraft:snowy_taiga_mountains", "Snowy Taiga Mountains", -0.5, 0.4).mapColor(0x597D72).defaultColors(0x80B497, 0x60A17B));
  private static final Biome giantSpruceTaiga = register(Biome.create("minecraft:giant_spruce_taiga", "Giant Spruce Taiga", 0.25, 0.8).mapColor(0x818E79).defaultColors(0x86B783, 0x68A464));
  private static final Biome giantSpruceTaigaHills = register(Biome.create("minecraft:giant_spruce_taiga_hills", "Giant Spruce Taiga Hills", 0.25, 0.8).mapColor(0x6D7766).defaultColors(0x86B783, 0x68A464));
  private static final Biome modifiedGravellyMountains = register(Biome.create("minecraft:modified_gravelly_mountains+", "Gravelly Mountains+", 0.2, 0.3).mapColor(0x789878).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome shatteredSavanna = register(Biome.create("minecraft:shattered_savanna", "Shattered Savanna", 1.1, 0).mapColor(0xE5DA87).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome shatteredSavannaPlateau = register(Biome.create("minecraft:shattered_savanna_plateau", "Shattered Savanna Plateau", 1, 0).mapColor(0xCFC58C).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome erodedBadlands = register(Biome.create("minecraft:eroded_badlands", "Eroded Badlands", 2, 0).mapColor(0xFF6D3D).badlands());
  private static final Biome modifiedWoodedBadlandsPlateau = register(Biome.create("minecraft:modified_wooded_badlands_plateau", "Modified Wooded Badlands Plateau", 2, 0).mapColor(0xD8BF8D).badlands());
  private static final Biome modifiedBadlandsPlateau = register(Biome.create("minecraft:modified_badlands_plateau", "Modified Badlands Plateau", 2, 0).mapColor(0xF2B48D).badlands());
  private static final Biome bambooJungle = register(Biome.create("minecraft:bamboo_jungle", "Bamboo Jungle", 0.95, 0.9).mapColor(0x768E14).defaultColors(0x59C93C, 0x30BB0B));
  private static final Biome bambooJungleHills = register(Biome.create("minecraft:bamboo_jungle_hills", "Bamboo Jungle Hills", 0.95, 0.9).mapColor(0x3B470A).defaultColors(0x59C93C, 0x30BB0B));
  private static final Biome soulSandValley = register(Biome.create("minecraft:soul_sand_valley", "Soul Sand Valley", 2, 0).mapColor(0x5E3830).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome crimsonForest = register(Biome.create("minecraft:crimson_forest", "Crimson Forest", 2, 0).mapColor(0xDD0808).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome warpedForest = register(Biome.create("minecraft:warped_forest", "Warped Forest", 2, 0).mapColor(0x49907B).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome basaltDeltas = register(Biome.create("minecraft:basalt_deltas", "Basalt Deltas", 2, 0).mapColor(0x403636).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome dripstoneCaves = register(Biome.create("minecraft:dripstone_caves", "Dripstone Caves", 0.8, 0.4).mapColor(0x7B6254).defaultColors(0x91BD59, 0x77AB2F));
  private static final Biome snowyPlains = register(Biome.create("minecraft:snowy_plains", "Snowy Plains", 0, 0.5).mapColor(0x7E7E7E).defaultColors(0x80B497, 0x60A17B));
  private static final Biome oldGrowthBirchForest = register(Biome.create("minecraft:old_growth_birch_forest", "Old-Growth Birch Forest", 0.6, 0.6).mapColor(0x7E7E7E).defaultColors(0x88BB67, 0x6BA941));
  private static final Biome oldGrowthPineTaiga = register(Biome.create("minecraft:old_growth_pine_taiga", "Old-Growth Pine Taiga", 0.3, 0.8).mapColor(0x7E7E7E).defaultColors(0x86B87F, 0x68A55F));
  private static final Biome oldGrowthSpruceTaiga = register(Biome.create("minecraft:old_growth_spruce_taiga", "Old-Growth Spruce Taiga", 0.25, 0.8).mapColor(0x7E7E7E).defaultColors(0x86B783, 0x68A464));
  private static final Biome windsweptHills = register(Biome.create("minecraft:windswept_hills", "Windswept Hills", 0.2, 0.3).mapColor(0x7E7E7E).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome windsweptGravellyHills = register(Biome.create("minecraft:windswept_gravelly_hills", "Windswept Gravelly Hills", 0.2, 0.3).mapColor(0x7E7E7E).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome windsweptForest = register(Biome.create("minecraft:windswept_forest", "Windswept Forest", 0.2, 0.3).mapColor(0x7E7E7E).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome windsweptSavanna = register(Biome.create("minecraft:windswept_savanna", "Windswept Savanna", 2, 0).mapColor(0x7E7E7E).defaultColors(0xBFB755, 0xAEA42A));
  private static final Biome sparseJungle = register(Biome.create("minecraft:sparse_jungle", "Sparse Jungle", 0.95, 0.8).mapColor(0x7E7E7E).defaultColors(0x64C73F, 0x3EB80F));
  private static final Biome woodedBadlands = register(Biome.create("minecraft:wooded_badlands", "Wooded Badlands", 2, 0).mapColor(0xD94515).badlands());
  private static final Biome meadow = register(Biome.create("minecraft:meadow", "Meadow", 0.5, 0.8).mapColor(0x7E7E7E).defaultColors(0x83BB6D, 0x64A948));
  private static final Biome grove = register(Biome.create("minecraft:grove", "Grove", -0.2, 0.8).mapColor(0x7E7E7E).defaultColors(0x80B497, 0x60A17B));
  private static final Biome snowySlopes = register(Biome.create("minecraft:snowy_slopes", "Snowy Slopes", -0.3, 0.9).mapColor(0x7E7E7E).defaultColors(0x80B497, 0x60A17B));
  private static final Biome frozenPeaks = register(Biome.create("minecraft:frozen_peaks", "Frozen Peaks", -0.7, 0.9).mapColor(0x7E7E7E).defaultColors(0x80B497, 0x60A17B));
  private static final Biome jaggedPeaks = register(Biome.create("minecraft:jagged_peaks", "Jagged Peaks", -0.7, 0.9).mapColor(0x7E7E7E).defaultColors(0x80B497, 0x60A17B));
  private static final Biome stonyPeaks = register(Biome.create("minecraft:stony_peaks", "Stony Peaks", 1, 0.3).mapColor(0x7E7E7E).defaultColors(0x9ABE4B, 0x82AC1E));
  private static final Biome stonyShore = register(Biome.create("minecraft:stony_shore", "Stony Shore", 0.2, 0.3).mapColor(0x7E7E7E).defaultColors(0x8AB689, 0x6DA36B));
  private static final Biome lushCaves = register(Biome.create("minecraft:lush_caves", "Lush Caves", 0.5, 0.5).mapColor(0x7E7E7E).defaultColors(0x8EB971, 0x71A74D));
  private static final Biome deepDark = register(Biome.create("minecraft:deep_dark", "Deep Dark", 0.8, 0.4).mapColor(0x7E7E7E).defaultColors(0x91BD59, 0x77AB2F));
  private static final Biome mangroveSwamp = register(Biome.create("minecraft:mangrove_swamp", "Mangrove Swamp", 0.8, 0.9).defaultColors(0x6A7039, 0x8DB127).waterColor(0x3A7A6A).mapColor(0x07F9B2).swamp());
  private static final Biome cherryGrove = register(Biome.create("minecraft:cherry_grove", "Cherry Grove", 0.5, 0.8).mapColor(0xFCCBE7).grassColor(0xB6DB61).foliageColor(0xB6DB61));
  private static final Biome paleGarden = register(Biome.create("minecraft:pale_garden", "Pale Garden", 0.7, 0.8).mapColor(0xB9B9B9).grassColor(0x778272).foliageColor(0x878D76).dryFoliageColor(0xA0A69C).waterColor(0x76889D));

  /**
   * Pre-1.18 biomes, i.e. before the biomes palette was introduced.
   */
  public static final Biome[] biomesPrePalette = {
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
    minecraftBiomes.addAll(biomes);

    int i = 0;
    for (Biome biome : biomesPrePalette) {
      biomeIDsByResourceLocation.put(biome.resourceLocation, i);
      ++i;
    }
  }

  public static void reset() {
    biomesByResourceLocation.clear();
    biomes.clear();

    for (Biome biome : minecraftBiomes) {
      register(biome);
    }
  }

  @PluginApi
  public static boolean contains(String resourceLocation) {
    return biomesByResourceLocation.containsKey(resourceLocation);
  }

  @PluginApi
  public static Biome register(Biome biome) {
    if (!contains(biome.resourceLocation)) {
      biomes.add(biome);
      biomesByResourceLocation.put(biome.resourceLocation, biome);
    }

    return biome;
  }

  @PluginApi
  public static Biome register(BiomeBuilder biomeBuilder) {
    return register(biomeBuilder.build());
  }

  /**
   * Loads grass colors from a grass color texture.
   */
  public static void loadGrassColors(BitmapImage texture) {
    for (Biome biome : biomes) {
      if (biome.grassColorMode == Biome.GrassColorMode.SWAMP) {
        biome.grassColor = 0x6A7039;
      } else if (biome.grassColorMode != Biome.GrassColorMode.FIXED_COLOR) {
        biome.grassColor = getFoliageOrGrassColor(texture, biome);
        if (biome.grassColorMode == Biome.GrassColorMode.DARK_FOREST) {
          // Dark forest biomes' grass color is retrieved normally, then bit masked with 0xFEFEFE averaged with 0x28340A to produce the final color
          float[] color = new float[3];
          ColorUtil.getRGBComponents(biome.grassColor & 0xFEFEFE, color);
          color[0] += 40 / 255.f;
          color[1] += 52 / 255.f;
          color[2] += 10 / 255.f;
          biome.grassColor = ColorUtil.getRGB(color[0] / 2, color[1] / 2, color[2] / 2);
        }
      }
      biome.grassColorLinear = getRGBAComponentsGammaCorrected(biome.grassColor);
    }
  }

  /**
   * Loads foliage colors from a foliage color texture.
   */
  public static void loadFoliageColors(BitmapImage texture) {
    for (Biome biome : biomes) {
      if (biome.foliageColorMode == Biome.FoliageColorMode.SWAMP) {
        biome.foliageColor = 0x6A7039;
      } else if (biome.foliageColorMode == Biome.FoliageColorMode.DEFAULT) {
        biome.foliageColor = getFoliageOrGrassColor(texture, biome);
      }
      biome.foliageColorLinear = getRGBAComponentsGammaCorrected(biome.foliageColor);
    }
  }

  /**
   * Loads dry foliage colors from a dry foliage color texture.
   */
  public static void loadDryFoliageColors(BitmapImage texture) {
    for (Biome biome : biomes) {
      biome.dryFoliageColor = getFoliageOrGrassColor(texture, biome);
      biome.dryFoliageColorLinear = getRGBAComponentsGammaCorrected(biome.dryFoliageColor);
    }
  }

  private static int getFoliageOrGrassColor(BitmapImage texture, Biome biome) {
    double temp = QuickMath.clamp(biome.temperature, 0, 1);
    double rain = QuickMath.clamp(biome.rain, 0, 1);
    rain *= temp;
    return texture.getPixel((int) ((1 - temp) * 255), (int) ((1 - rain) * 255));
  }
}
