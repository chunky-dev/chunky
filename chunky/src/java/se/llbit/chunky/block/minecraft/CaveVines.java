/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class CaveVines extends SpriteBlock {

  private final boolean berries;

  public CaveVines(String name, boolean berries, boolean body) {
    super(name, getTexture(body, berries));
    this.berries = berries;
  }

  private static AbstractTexture getTexture(boolean body, boolean lit) {
    if (body) {
      return lit ? Texture.caveVinesPlantLit : Texture.caveVinesPlant;
    }
    return lit ? Texture.caveVinesLit : Texture.caveVines;
  }

  public boolean hasBerries() {
    return berries;
  }

  @Override
  public String description() {
    return "berries=" + berries;
  }
}
