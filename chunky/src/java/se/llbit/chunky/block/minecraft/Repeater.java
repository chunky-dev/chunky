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
import se.llbit.chunky.model.minecraft.RedstoneRepeaterModel;
import se.llbit.chunky.model.minecraft.RedstoneRepeaterModel1212;
import se.llbit.chunky.resources.Texture;

public class Repeater extends AbstractModelBlock {
  private final String facing;
  private final String description;

  public Repeater(int delay, String facing, boolean powered, boolean locked) {
    super("repeater", Texture.redstoneRepeaterOn);
    description = String.format("delay=%d, facing=%s, powered=%s, locked=%s", delay, facing, powered, locked);
    this.facing = facing;
    model = System.getProperty("chunky.blockModels.redstoneTorch", "1.21.2").equals("pre-1.21.2")
      ? new RedstoneRepeaterModel(facing, delay, powered, locked)
      : new RedstoneRepeaterModel1212(facing, delay, powered, locked);
  }

  public int getFacing() {
    return switch (facing) {
      case "south" -> 0;
      case "west" -> 1;
      case "east" -> 3;
      default -> 2;
    };
  }

  @Override
  public String description() {
    return description;
  }
}
