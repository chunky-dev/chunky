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
import se.llbit.chunky.model.minecraft.TorchModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;

/**
 * A torch attached to a wall.
 */
public class WallTorch extends AbstractModelBlock {
  // Epsilons to clip ray intersections to the current block.
  public static final double E0 = -Constants.EPSILON;
  public static final double E1 = 1 + Constants.EPSILON;

  protected final String facing;

  public WallTorch(String name, Texture texture, String facing) {
    super(name, texture);
    this.facing = facing;
    solid = false;
    model = new TorchModel(texture, facing);
  }

  @Override
  public String description() {
    return "facing=" + facing;
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
