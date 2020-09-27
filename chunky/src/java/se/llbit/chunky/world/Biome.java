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
package se.llbit.chunky.world;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biome {
  public String name;
  public float temp;
  public float rain;
  public int mapColor;

  /**
   * Default grass/foliage color before loading from resource pack.
   */
  public int grassColor;

  public int waterColor;

  public Biome(String name, double temp, double rain, int mapColor, int grassColor) {
    this(name, temp, rain, mapColor, grassColor, 0x3f76e4);
  }

  public Biome(String name, double temp, double rain, int mapColor, int grassColor, int waterColor) {
    this.name = name;
    this.temp = (float) temp;
    this.rain = (float) rain;
    this.mapColor = 0xFF000000 | mapColor;
    this.grassColor = grassColor;
    this.waterColor = waterColor;
  }
}
