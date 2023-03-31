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

package se.llbit.chunky.block.block;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.model.BrewingStandModel;
import se.llbit.chunky.resources.Texture;

public class BrewingStand extends AbstractModelBlock {
  private final String description;

  public BrewingStand(boolean bottle0, boolean bottle1, boolean bottle2) {
    super("brewing_stand", Texture.brewingStandBase);
    description = String.format("has_bottle_0=%s, has_bottle_1=%s, has_bottle_2=%s",
        bottle0, bottle1, bottle2);
    this.model = new BrewingStandModel(bottle0, bottle1, bottle2);
  }

  @Override
  public String description() {
    return description;
  }
}
