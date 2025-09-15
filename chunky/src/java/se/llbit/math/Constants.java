/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.math;

public final class Constants {
  public static final double EPSILON = 5e-8;
  public static final double OFFSET = 1e-6;

  public static final double INV_4_PI = 1 / (4 * Math.PI);
  public static final double HALF_PI = Math.PI / 2;
  public static final double INV_PI = 1 / Math.PI;
  // TODO INV_TAU
  public static final double TAU = Math.PI * 2;
  public static final double SQRT_HALF = Math.sqrt(0.5);
  public static final double INV_SQRT_HALF = 1 / Math.sqrt(0.5);
  public static final double SQRT_2 = Math.sqrt(2);

  private Constants() {}
}
