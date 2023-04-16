/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

import static se.llbit.math.ColorUtil.getRGBAComponentsGammaCorrected;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biome {
  public static final int DEFAULT_WATER_COLOR = 0x3f76e4;
  public final String resourceLocation;
  public final String name;
  public final float temperature;
  public final float rain;
  public final int mapColor;

  public int grassColor;
  public GrassColorMode grassColorMode;
  public int foliageColor;
  public FoliageColorMode foliageColorMode;
  public int waterColor;

  public float[] grassColorLinear;
  public float[] foliageColorLinear;
  public float[] waterColorLinear;

  Biome(String resourceLocation, String name, double temperature, double rain, int mapColor, int grassColor,
        GrassColorMode grassColorMode, int foliageColor, FoliageColorMode foliageColorMode, int waterColor) {
    this.resourceLocation = resourceLocation;
    this.name = name;
    this.temperature = (float) temperature;
    this.rain = (float) rain;
    this.mapColor = 0xFF000000 | mapColor;
    this.grassColor = grassColor;
    this.grassColorMode = grassColorMode;
    this.foliageColor = foliageColor;
    this.foliageColorMode = foliageColorMode;
    this.waterColor = waterColor;

    this.grassColorLinear = getRGBAComponentsGammaCorrected(grassColor);
    this.foliageColorLinear = getRGBAComponentsGammaCorrected(foliageColor);
    this.waterColorLinear = getRGBAComponentsGammaCorrected(waterColor);
  }

  public static BiomeBuilder create(String resourceLocation, String name, double temperature, double rain) {
    return new BiomeBuilder(resourceLocation, name, temperature, rain);
  }

  enum GrassColorMode {
    /**
     * The grass color depends on temperature and humidity.
     */
    DEFAULT,
    /**
     * The grass color is fixed.
     */
    FIXED_COLOR,
    /**
     * The grass color depends on temperature and humidity and is also averaged with 0x28340A.
     */
    DARK_FOREST,
    /**
     * In Java Edition, the grass color would be perlin noise with two colors.
     * Since we don't support this, the grass color is 0x6A7039 (as in Bedrock Edition).
     */
    SWAMP
  }

  enum FoliageColorMode {
    /**
     * The foliage color depends on temperature and humidity.
     */
    DEFAULT,
    /**
     * The foliage color is fixed.
     */
    FIXED_COLOR,
    /**
     * In Java Edition, the foliage color would be perlin noise with two colors.
     * Since we don't support this, the foliage color is 0x6A7039 (as in Bedrock Edition).
     */
    SWAMP
  }
}
