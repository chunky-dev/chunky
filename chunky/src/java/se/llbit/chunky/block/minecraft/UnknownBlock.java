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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

public class UnknownBlock extends SpriteBlock {
  public static final UnknownBlock UNKNOWN = new UnknownBlock("?");
  private final String description;

  public UnknownBlock(String name) {
    super(name, Texture.unknown);
    description = "";
  }

  public UnknownBlock(String name, Tag tag) {
    super(name, Texture.unknown);
    StringBuilder descriptionBuilder = new StringBuilder();
    for (NamedTag property : tag.get("Properties").asCompound()) {
      if (!descriptionBuilder.isEmpty()) {
        descriptionBuilder.append(", ");
      }
      descriptionBuilder.append(property.name);
      descriptionBuilder.append("=");
      descriptionBuilder.append(property.tag.stringValue("?"));
    }
    this.description = descriptionBuilder.toString();
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    if (scene.getHideUnknownBlocks()) {
      return false;
    }
    return super.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }
}
