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
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class Sign extends MinecraftBlockTranslucent {
  private final int rotation;
  private final String material;

  public Sign(String name, String material, int rotation) {
    super(name, SignEntity.textureFromMaterial(material));
    solid = false;
    this.rotation = rotation % 16;
    this.material = material;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isBlockEntity() {
    return true;
  }

  @Override public Entity createBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new SignEntity(position, entityTag, rotation, material);
  }
}
