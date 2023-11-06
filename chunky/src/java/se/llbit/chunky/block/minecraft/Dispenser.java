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
import se.llbit.chunky.model.minecraft.DispenserModel;
import se.llbit.chunky.resources.Texture;

/**
 * The dispenser behaves almost like a TopBottomOrientedTexturedBlock. If it's facing up or down, it
 * has different textures (and thus different texture orientation logic).
 */
public class Dispenser extends AbstractModelBlock {

  private final String description;

  public Dispenser(String facing) {
    this("dispenser", facing, Texture.dispenserFront, Texture.dispenserFrontVertical,
        Texture.furnaceSide, Texture.furnaceTop);
  }

  public Dispenser(String name, String facing, Texture front, Texture frontVertical, Texture side,
      Texture back) {
    super(name, front);
    opaque = true;
    solid = true;
    this.model = new DispenserModel(facing, front, frontVertical, side, back);
    this.description = "facing=" + facing;
  }

  @Override
  public String description() {
    return description;
  }
}
