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
import se.llbit.chunky.model.minecraft.RedstoneWallTorchModel;
import se.llbit.chunky.model.minecraft.TorchModel;
import se.llbit.chunky.resources.Texture;

public class RedstoneWallTorch extends AbstractModelBlock {
  private final boolean lit;
  private final String facing;

  public RedstoneWallTorch(String facing, boolean lit) {
    super("redstone_wall_torch", lit ? Texture.redstoneTorchOn : Texture.redstoneTorchOff);
    this.lit = lit;
    this.facing = facing;
    model = System.getProperty("chunky.blockModels.redstoneTorch", "1.21.2").equals("pre-1.21.2")
      ? new TorchModel(texture, facing)
      : new RedstoneWallTorchModel(lit, facing);
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return "facing=" + facing + ", lit=" + lit;
  }
}
