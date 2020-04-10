/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.material;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;

// TODO: fetch color from biome?
public class LilyPadMaterial extends Material {

  private static final int COLOR = 0x009218;
  private static final float[] lilyPadColor = new float[4];

  static {
    ColorUtil.getRGBAComponents(COLOR, lilyPadColor);
  }

  public LilyPadMaterial() {
    super("lily_pad", Texture.lilyPad);
  }

  @Override public void getColor(Ray ray) {
    super.getColor(ray);
    if (ray.color.w > Ray.EPSILON) {
      ray.color.x *= lilyPadColor[0];
      ray.color.y *= lilyPadColor[1];
      ray.color.z *= lilyPadColor[2];
    }
  }
}
