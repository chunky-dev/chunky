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
import se.llbit.chunky.model.minecraft.SpriteModel;
import se.llbit.chunky.resources.Texture;

public class SpriteBlock extends AbstractModelBlock {

  protected String facing;

  public SpriteBlock(String name, Texture texture) {
    super(name, texture);
    solid = false;
    model = new SpriteModel(texture);
  }

  public SpriteBlock(String name, Texture texture, String facing) {
    super(name, texture);
    solid = false;
    model = new SpriteModel(texture, facing);
    this.facing = facing;
  }

  @Override
  public String description() {
    if (facing != null) {
      return "facing=" + facing;
    }
    return super.description();
  }
}
