package se.llbit.chunky.world.biome;

import se.llbit.chunky.plugin.PluginApi;

@PluginApi
public class BiomeBuilder {
  private final String resourceLocation;
  private final String name;
  private final double temperature;
  private final double rain;
  private int waterColor = Biome.DEFAULT_WATER_COLOR;
  private int mapColor = 0x7E7E7E;
  private int grassColor;
  private Biome.GrassColorMode grassColorMode = Biome.GrassColorMode.DEFAULT;
  private int foliageColor;
  private Biome.FoliageColorMode foliageColorMode = Biome.FoliageColorMode.DEFAULT;

  public BiomeBuilder(String resourceLocation, String name, double temperature, double rain) {
    this.resourceLocation = resourceLocation;
    this.name = name;
    this.temperature = temperature;
    this.rain = rain;
  }

  public BiomeBuilder(String resourceLocation, double temperature, double rain) {
    this(resourceLocation, resourceLocation, temperature, rain);
  }

  public BiomeBuilder mapColor(int mapColor) {
    this.mapColor = mapColor;
    return this;
  }

  public BiomeBuilder defaultColors(int grassColor, int foliageColor) {
    this.grassColor = grassColor;
    this.foliageColor = foliageColor;
    return this;
  }

  public BiomeBuilder grassColor(int grassColor) {
    this.grassColor = grassColor;
    this.grassColorMode = Biome.GrassColorMode.FIXED_COLOR;
    return this;
  }

  public BiomeBuilder foliageColor(int foliageColor) {
    this.foliageColor = foliageColor;
    this.foliageColorMode = Biome.FoliageColorMode.FIXED_COLOR;
    return this;
  }

  public BiomeBuilder waterColor(int waterColor) {
    this.waterColor = waterColor;
    return this;
  }

  public BiomeBuilder swamp() {
    this.grassColorMode = Biome.GrassColorMode.SWAMP;
    this.foliageColorMode = Biome.FoliageColorMode.SWAMP;
    return this;
  }

  public BiomeBuilder darkForest() {
    this.grassColorMode = Biome.GrassColorMode.DARK_FOREST;
    return this;
  }

  public BiomeBuilder badlands() {
    grassColor(0x90814D);
    foliageColor(0x9E814D);
    return this;
  }

  public Biome build() {
    return new Biome(
      resourceLocation, name,
      temperature, rain,
      mapColor,
      grassColor, grassColorMode,
      foliageColor, foliageColorMode,
      waterColor
    );
  }
}
