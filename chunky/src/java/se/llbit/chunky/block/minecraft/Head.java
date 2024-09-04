/*
 * Copyright (c) 2020-2023 Chunky contributors
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
import se.llbit.nbt.Tag;
import se.llbit.util.UuidUtil;
import se.llbit.util.mojangapi.MinecraftSkin;
import se.llbit.util.mojangapi.MojangApi;

import java.io.IOException;
import java.util.Optional;

public class Head extends MinecraftBlockTranslucent {

  private final String description;
  private final int rotation;
  private final SkullEntity.Kind type;

  public Head(String name, Texture texture, SkullEntity.Kind type, int rotation) {
    super(name, texture);
    localIntersect = true;
    invisible = true;
    description = "rotation=" + rotation;
    this.type = type;
    this.rotation = rotation;
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
  public Entity toEntity(Vector3 position) {
    return new SkullEntity(position, type, rotation, 1);
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    if (type == Kind.PLAYER) {
      try {
        String textureUrl = getTextureUrl(entityTag);
        return textureUrl != null ? new HeadEntity(position, textureUrl, rotation, 1)
          : new SkullEntity(position, type, rotation, 1);
      } catch (IOException e) {
        Log.warn("Could not download skin", e);
      }
    }
    return null;
  }

  public static String getTextureUrl(CompoundTag entityTag) throws IOException {
    Tag ownerTag = entityTag.get("Owner"); // used by skulls
    if (!ownerTag.isCompoundTag()) {
      ownerTag = entityTag.get("SkullOwner"); // used by player heads (1.20 or earlier)
    }
    if (ownerTag.isCompoundTag()) {
      String textureBase64 = ownerTag.get("Properties").get("textures").get(0)
        .get("Value").stringValue();
      if (!textureBase64.isEmpty()) {
        return MinecraftSkin.getSkinFromEncodedTextures(textureBase64).map(MinecraftSkin::getSkinUrl).orElse(null);
      }
    }
    Tag profileTag = entityTag.get("profile");
    if (profileTag.isCompoundTag()) {
      // 24w10a (i.e. 1.21) or later
      return getSkinFromProfileTag(profileTag.asCompound()).map(MinecraftSkin::getSkinUrl).orElse(null);
    }
    return null;
  }

  public static Optional<MinecraftSkin> getSkinFromProfileTag(CompoundTag profileTag) throws IOException {
    Tag properties = profileTag.get("properties");
    if (properties.isCompoundTag()) {
      return MinecraftSkin.getSkinFromEncodedTextures(properties.get("value").stringValue());
    } else if (properties.isList()) {
      for (Tag property : properties.asList()) {
        if (property.get("name").stringValue("").equals("textures")) {
          String encodedTexture = property.get("value").stringValue("");
          if (!encodedTexture.isEmpty()) {
            return MinecraftSkin.getSkinFromEncodedTextures(encodedTexture);
          }
        }
      }
    }
    int[] uuidInts = profileTag.get("id").intArray();
    return MojangApi.fetchProfile(UuidUtil.intsToUuid(uuidInts).toString()).getSkin();
  }
}
