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
import se.llbit.chunky.entity.StandingBanner;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Point3;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

// Note: Mojang changed the ID values for banner colors in Minecraft 1.13,
// for backward compatibility we need some way of mapping the old color IDs to the
// new color IDs. This would require tracking the world format version somewhere.
public class Banner extends MinecraftBlockTranslucent {
  private final int rotation, color;

  public Banner(String name, Texture texture, int rotation, int color) {
    super(name, texture);
    invisible = true;
    opaque = false;
    localIntersect = true;
    this.rotation = rotation % 16;
    this.color = color;
  }

  @Override public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    return false;
  }

  @Override public boolean isBlockEntity() {
    return true;
  }

  @Override public Entity toBlockEntity(Point3 position, CompoundTag entityTag) {
    JsonObject design = StandingBanner.parseDesign(entityTag);
    design.set("base", Json.of(color)); // Base color is not included in the entity tag in Minecraft 1.13+.
    return new StandingBanner(position, rotation, design);
  }
}
