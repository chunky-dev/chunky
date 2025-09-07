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
import se.llbit.chunky.entity.BeaconBeam;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.minecraft.BeaconModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class Beacon extends AbstractModelBlock {

  public Beacon() {
    super("beacon", Texture.beacon);
    localIntersect = true;
    solid = false;
    this.model = new BeaconModel();
  }

  @Override
  public boolean isReplacedByEntities() {
    return false;
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity createBlockEntity(Vector3 position, CompoundTag entityTag) {
    if (entityTag.get("Levels").intValue(0) > 0) {
      return new BeaconBeam(position);
    }
    return null;
  }
}
