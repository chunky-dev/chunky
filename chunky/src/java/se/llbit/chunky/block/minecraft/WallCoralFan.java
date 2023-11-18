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

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.WallCoralFanEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;

public class WallCoralFan extends MinecraftBlockTranslucent {

  private final String coralType;
  private final String facing;

  public WallCoralFan(String name, String coralType, String facing) {
    super(name, CoralFan.coralTexture(coralType));
    this.coralType = coralType;
    this.facing = facing;
    localIntersect = true;
    solid = false;
    invisible = true;
  }

  @Override public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    return false;
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity toEntity(Vector3 position) {
    return new WallCoralFanEntity(position, coralType, facing);
  }
}
