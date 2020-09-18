/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

public enum OutputMode {
  /**
   * Standard PNG with 8-bit color channels.
   */
  PNG {
    @Override public String toString() {
      return "PNG";
    }

    @Override public String getExtension() {
      return ".png";
    }
  },

  /**
   * TIFF with 32-bit color channels.
   */
  TIFF_32 {
    @Override public String toString() {
      return "TIFF, 32-bit floating point";
    }

    @Override public String getExtension() {
      return ".tiff";
    }
  };

  public static final OutputMode DEFAULT = PNG;

  public static OutputMode get(String name) {
    try {
      return OutputMode.valueOf(name);
    } catch (IllegalArgumentException e) {
      return DEFAULT;
    }
  }

  public static OutputMode fromExtension(String extension) {
    switch (extension) {
      case ".png":
        return PNG;
      case ".tiff":
        return TIFF_32;
      default:
        return DEFAULT;
    }
  }

  public abstract String getExtension();
}
