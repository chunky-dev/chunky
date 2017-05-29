/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

/**
 * Quick math utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class QuickMath {

  public static final double HALF_PI = Math.PI / 2;
  public static final double TAU = Math.PI * 2;
  private static final double[] SINE_TABLE = new double[65536];

  /**
   * @return The sine of d
   */
  public static double sin(double d) {
    return SINE_TABLE[(int) (d * 10430.378) & 65535];
  }

  /**
   * @return The cosine of d
   */
  public static double cos(double d) {
    return SINE_TABLE[(int) (d * 10430.378 + 16384) & 65535];
  }

  /**
   * @return The floor of d
   */
  public static long floor(double d) {
    long i = (long) d;
    return d < i ? i - 1 : i;
  }

  /**
   * @return The ceil of d
   */
  public static long ceil(double d) {
    long i = (long) d;
    return d > i ? i + 1 : i;
  }

  /**
   * Get the next power of two.
   *
   * @return The next power of two
   */
  public static int nextPow2(int x) {
    x--;
    x |= x >> 1;
    x |= x >> 2;
    x |= x >> 4;
    x |= x >> 8;
    x |= x >> 16;
    return x + 1;
  }

  /**
   * @return 2-logarithm of x
   */
  public static int log2(int x) {
    int v = 0;
    while ((x >>>= 1) != 0) {
      v += 1;
    }
    return v;
  }

  /**
   * @return The sign of x
   */
  public static int signum(double x) {
    return x < 0 ? -1 : 1;
  }

  /**
   * @return The sign of x
   */
  public static int signum(float x) {
    return x < 0 ? -1 : 1;
  }

  /**
   * Convert radians to degrees
   *
   * @param rad Radians
   * @return Degrees
   */
  public static double radToDeg(double rad) {
    return 180 * (rad / Math.PI);
  }

  /**
   * @return Value modulo mod
   */
  public static double modulo(double value, double mod) {
    return ((value % mod) + mod) % mod;
  }

  /**
   * Convert degrees to radians
   *
   * @param deg Degrees
   * @return Radians
   */
  public static double degToRad(double deg) {
    return (deg * Math.PI) / 180;
  }

  /**
   * @return value clamped to min and max
   */
  public static double clamp(double value, double min, double max) {
    return value < min ? min : value > max ? max : value;
  }

  /**
   * NB not NaN-correct
   *
   * @return maximum value of a and b
   */
  public static double max(double a, double b) {
    return (a > b) ? a : b;
  }

  /**
   * NB not NaN-correct
   *
   * @return maximum value of a and b
   */
  public static float max(float a, float b) {
    return (a > b) ? a : b;
  }

  /**
   * NB disregards NaN. Don't use if a or b can be NaN
   *
   * @return minimum value of a and b
   */
  public static double min(double a, double b) {
    return (a < b) ? a : b;
  }

  /**
   * NB disregards NaN. Don't use if a or b can be NaN
   *
   * @return minimum value of a and b
   */
  public static float min(float a, float b) {
    return (a < b) ? a : b;
  }

  /**
   * NB disregards +-0
   *
   * @return absolute value of x
   */
  public static float abs(float x) {
    return (x < 0.f) ? -x : x;
  }

  /**
   * NB disregards +-0
   *
   * @return absolute value of x
   */
  public static double abs(double x) {
    return (x < 0.0) ? -x : x;
  }
  
  static
  {
    for (int i = 0; i < 65536; ++i) {
      SINE_TABLE[i] = Math.sin(i * Math.PI * 2 / 65536);
    }
  }
}
