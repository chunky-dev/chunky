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
 * Quick math utility functions.
 *
 * <p>Note: some of these functions disregard important edge cases, like
 * when an input value is NaN. By disregarding NaNs we can get slightly improved
 * performance.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class QuickMath {

  public static final double HALF_PI = Math.PI / 2;
  public static final double TAU = Math.PI * 2;

  /**
   * The value of d rounded down to the nearest long.
   */
  public static long floor(double d) {
    long i = (long) d;
    return d < i ? i - 1 : i;
  }

  /**
   * The value of d rounded up to the nearest long.
   */
  public static long ceil(double d) {
    long i = (long) d;
    return d > i ? i + 1 : i;
  }

  /**
   * The next power of two from x.
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
   * The 2-logarithm of x, rounded down to the nearest integer.
   */
  public static int log2(int x) {
    int v = 0;
    while ((x >>>= 1) != 0) {
      v += 1;
    }
    return v;
  }

  /**
   * The sign of x.
   */
  public static int signum(double x) {
    return x < 0 ? -1 : 1;
  }

  /**
   * The sign of x.
   */
  public static int signum(float x) {
    return x < 0 ? -1 : 1;
  }

  /**
   * Convert radians to degrees.
   */
  public static double radToDeg(double rad) {
    return 180 * (rad / Math.PI);
  }

  /**
   * Gives the modulus of value and mod (positive).
   */
  public static double modulo(double value, double mod) {
    return ((value % mod) + mod) % mod;
  }

  /**
   * Convert degrees to radians.
   */
  public static double degToRad(double deg) {
    return (deg * Math.PI) / 180;
  }

  /**
   * Gives value clamped to {@code [min, max]}.
   */
  public static double clamp(double value, double min, double max) {
    return value < min ? min : value > max ? max : value;
  }

  public static int clamp(int value, int min, int max) {
    return Math.max(Math.min(value, max), min);
  }

  /**
   * Maximum of a and b.
   * <p>NB: not NaN-correct. Do not use if either a or b can be NaN.
   */
  public static double max(double a, double b) {
    return (a > b) ? a : b;
  }

  /**
   * Maximum of a and b.
   * <p>NB: not NaN-correct. Do not use if either a or b can be NaN.
   */
  public static float max(float a, float b) {
    return (a > b) ? a : b;
  }

  /**
   * Minimum of a and b.
   * <p>NB: not NaN-correct. Do not use if either a or b can be NaN.
   */
  public static double min(double a, double b) {
    return (a < b) ? a : b;
  }

  /**
   * Minimum of a and b.
   * <p>NB: not NaN-correct. Do not use if either a or b can be NaN.
   */
  public static float min(float a, float b) {
    return (a < b) ? a : b;
  }

  /**
   * Absolute value of x.
   * NB: disregards +-0.
   */
  public static float abs(float x) {
    return (x < 0.f) ? -x : x;
  }

  /**
   * Absolute value of x.
   * NB: disregards +-0.
   */
  public static double abs(double x) {
    return (x < 0.0) ? -x : x;
  }

  /**
   * Greatest Common Divisor of both inputs
   */
  public static int gcd(int a, int b) {
    while (b != 0) {
      int t = a;
      a = b;
      b = t % b;
    }
    return a;
  }

  /**
   * Previous multiple of 16 of val (Or given value, if a multiple of 16). Respects negative numbers.
   * <h4>Examples:</h4>
   * <ul>
   * <li>1 returns 0</li>
   * <li>34 returns 32</li>
   * <li>16 returns 16</li>
   * <li>-1 returns -16</li>
   * </ul>
   */
  public static int prevMul16(int val) {
    return val & ~0xf;
  }
}
