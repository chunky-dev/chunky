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
import se.llbit.chunky.entity.HeadEntity;
import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.entity.SkullEntity.Kind;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class WallHead extends MinecraftBlockTranslucent {
  private final String description;
  private final int facing;
  private final SkullEntity.Kind type;

  public WallHead(String name, Texture texture, SkullEntity.Kind type, String facing) {
    super(name, texture);
    localIntersect = true;
    invisible = true;
    description = "facing=" + facing;
    this.type = type;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
        break;
    }
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public boolean isEntity() {
    return type != Kind.PLAYER;
  }

  @Override
  public Collection<Entity> toEntity(Vector3 position) {
    return Collections.singleton(new SkullEntity(position, type, 0, facing));
  }

  @Override
  public boolean isBlockEntity() {
    return true;//return type == Kind.PLAYER;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    if (type == Kind.PLAYER) {
      try {
        String textureUrl = Head.getTextureUrl(entityTag);
        return textureUrl != null ? new HeadEntity(position, textureUrl, 0, facing)
          : new SkullEntity(position, type, 0, facing);
      } catch (IOException e) {
        Log.warn("Could not download skin", e);
      }
    }
    return null;
  }
}
