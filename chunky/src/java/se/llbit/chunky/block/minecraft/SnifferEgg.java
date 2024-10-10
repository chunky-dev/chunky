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

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.SnifferEggModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class SnifferEgg extends AbstractModelBlock {

  private final String description;

  public SnifferEgg(String name, int hatch) {
    super(name, getTopTexture(hatch));
    this.description = "hatch=" + hatch;
    this.model = new SnifferEggModel(hatch);
  }

  @Override
  public String description() {
    return description;
  }

  private static AbstractTexture getTopTexture(int hatch) {
    switch (hatch) {
      case 1:
        return Texture.snifferEggSlightlyCrackedTop;
      case 2:
        return Texture.snifferEggVeryCrackedTop;
      default:
      case 0:
        return Texture.snifferEggNotCrackedTop;
    }
  }
}
