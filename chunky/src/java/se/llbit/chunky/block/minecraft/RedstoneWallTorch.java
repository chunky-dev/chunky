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

import static se.llbit.chunky.block.minecraft.WallTorch.E0;
import static se.llbit.chunky.block.minecraft.WallTorch.E1;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.RedstoneWallTorchModel;
import se.llbit.chunky.model.minecraft.TorchModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;

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

  @Override
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    if (super.intersect(ray, intersectionRecord, scene)) {
      double px = ray.o.x - Math.floor(ray.o.x + ray.d.x * Constants.OFFSET) + ray.d.x * intersectionRecord.distance;
      double py = ray.o.y - Math.floor(ray.o.y + ray.d.y * Constants.OFFSET) + ray.d.y * intersectionRecord.distance;
      double pz = ray.o.z - Math.floor(ray.o.z + ray.d.z * Constants.OFFSET) + ray.d.z * intersectionRecord.distance;
      return !(px < E0) && !(px > E1) && !(py < E0) && !(py > E1) && !(pz < E0) && !(pz > E1);
    }
    return false;
  }
}
