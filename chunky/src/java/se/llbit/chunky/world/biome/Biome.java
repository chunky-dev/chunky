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

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.ColorUtil;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biome {
  public final String resourceLocation;
  public final String name;
  public final float temp;
  public final float rain;
  public final int mapColor;

  /**
   * Default grass color before loading from resource pack.
   */
  public int grassColor;
  /**
   * Default foliage color before loading from resource pack.
   */
  public int foliageColor;
  public int waterColor;

  public float[] grassColorLinear;
  public float[] foliageColorLinear;
  public float[] waterColorLinear;

  Biome(String resourceLocation, String name, double temp, double rain, int mapColor, int grassColor, int foliageColor) {
    this(resourceLocation, name, temp, rain, mapColor, grassColor, foliageColor, 0x3f76e4);
  }

  Biome(String resourceLocation, String name, double temp, double rain, int mapColor, int grassColor, int foliageColor, int waterColor) {
    this.resourceLocation = resourceLocation;
    this.name = name;
    this.temp = (float) temp;
    this.rain = (float) rain;
    this.mapColor = 0xFF000000 | mapColor;
    this.grassColor = grassColor;
    this.foliageColor = foliageColor;
    this.waterColor = waterColor;
    this.grassColorLinear = gammaCorrectColor(grassColor);
    this.foliageColorLinear = gammaCorrectColor(foliageColor);
    this.waterColorLinear = gammaCorrectColor(waterColor);
  }

  protected static float[] gammaCorrectColor(int src) {
    float[] frgb = new float[3];
    ColorUtil.getRGBComponents(src, frgb);
    frgb[0] = (float) FastMath.pow(frgb[0], Scene.DEFAULT_GAMMA);
    frgb[1] = (float) FastMath.pow(frgb[1], Scene.DEFAULT_GAMMA);
    frgb[2] = (float) FastMath.pow(frgb[2], Scene.DEFAULT_GAMMA);
    return frgb;
  }
}
