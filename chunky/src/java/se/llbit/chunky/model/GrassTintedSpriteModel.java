/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.model.model.SpriteModel;
import se.llbit.chunky.resources.Texture;

public class GrassTintedSpriteModel extends SpriteModel {

  private static final Tint[] tinting = new Tint[]{
    Tint.BIOME_GRASS, Tint.BIOME_GRASS, Tint.BIOME_GRASS, Tint.BIOME_GRASS
  };

  public GrassTintedSpriteModel(Texture texture) {
    super(texture);
  }

  @Override
  public Tint[] getTints() {
    return tinting;
  }
}
