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
import se.llbit.chunky.model.minecraft.LeafModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.biome.Biome;

public class Leaves extends AbstractModelBlock {
  private final int tint;

  public Leaves(String name, Texture texture) {
    super(name, texture);
    solid = false;
    this.model = new LeafModel(texture);
    this.tint = -1;
  }

  public Leaves(String name, Texture texture, int tint) {
    super(name, texture);
    solid = false;
    this.model = new LeafModel(texture, tint);
    this.tint = tint;
  }

  @Override
  public int getMapColor(Biome biome) {
    if (this.tint >= 0) {
      // this leave type has a blending color that is independent from the biome (eg. spruce or birch leaves)
      return this.tint | 0xFF000000;
    }
    return biome.foliageColor | 0xFF000000;
  }
}
