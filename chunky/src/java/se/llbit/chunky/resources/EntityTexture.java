/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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

import se.llbit.chunky.resources.texture.BitmapTexture;
import se.llbit.math.Vector4;

/**
 * Stores additional UV coordinates used for entity textures.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EntityTexture extends BitmapTexture {

  private static final UVMap UV = new UVMap(false); // e.g. skeleton
  private static final UVMap UV_EXTENDED = new UVMap(true); // e.g. zombie

  public UVMap getUV() {
    return width == height ? UV_EXTENDED : UV;
  }

  public static class UVMap {

    public Vector4 headFront = new Vector4();
    public Vector4 headBack = new Vector4();
    public Vector4 headTop = new Vector4();
    public Vector4 headBottom = new Vector4();
    public Vector4 headRight = new Vector4();
    public Vector4 headLeft = new Vector4();

    protected UVMap(boolean extended) {
      double height = extended ? 64 : 32;

      // Head texture
      headFront.set(8 / 64., 16 / 64., (height - 16) / height, (height - 8) / height);
      headBack.set(24 / 64., 32 / 64., (height - 16) / height, (height - 8) / height);
      headTop.set(8 / 64., 16 / 64., (height - 8) / height, 1);
      headBottom.set(16 / 64., 24 / 64., (height - 8) / height, 1);
      headRight.set(0, 8 / 64., (height - 16) / height, (height - 8) / height);
      headLeft.set(16 / 64., 24 / 64., (height - 16) / height, (height - 8) / height);
    }
  }
}
