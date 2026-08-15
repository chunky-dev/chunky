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
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;

import java.util.Random;

public class Campfire extends MinecraftBlockTranslucent {
  private final se.llbit.chunky.entity.Campfire.Kind kind;
  private final String facing;
  public final boolean isLit;

  public Campfire(String name, se.llbit.chunky.entity.Campfire.Kind kind, String facing, boolean lit) {
    super(name, Texture.campfireLog);
    invisible = true;
    opaque = false;
    localIntersect = true;
    this.kind = kind;
    this.facing = facing;
    this.isLit = lit;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    ListTag items = entityTag.get("Items").asList();
    return new se.llbit.chunky.entity.Campfire(this.kind, position, this.facing, this.isLit, this,
      !items.isEmpty() ? items.get(0).get("id").stringValue(null) : null,
      items.size() >= 2 ? items.get(1).get("id").stringValue(null) : null,
      items.size() >= 3 ? items.get(2).get("id").stringValue(null) : null,
      items.size() >= 4 ? items.get(3).get("id").stringValue(null) : null
    );
  }

  @Override
  public int faceCount() {
    return se.llbit.chunky.entity.Campfire.faceCount();
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    se.llbit.chunky.entity.Campfire.sample(face, loc, rand);
  }

  @Override
  public double surfaceArea(int face) {
    return se.llbit.chunky.entity.Campfire.surfaceArea(face);
  }
}
