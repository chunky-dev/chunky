/* Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for texture lookups for infrequently used textures.
 */
public class TextureCache {
  private static Map<Object, Texture> map = new HashMap<>();

  public static Texture get(Object key) {
    return map.get(key);
  }

  public static Texture put(Object key, Texture texture) {
    return map.put(key, texture);
  }

  public static void reset() {
    // TODO: make thread safe.
    map.clear();
  }
}
