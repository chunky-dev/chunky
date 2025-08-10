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
import se.llbit.chunky.model.minecraft.ChestModel;
import se.llbit.chunky.resources.Texture;

public class Chest extends AbstractModelBlock {
  public enum Kind {
    NORMAL,
    TRAPPED,
    ENDER,
    COPPER,
    EXPOSED_COPPER,
    OXIDIZED_COPPER,
    WEATHERED_COPPER
  }

  public enum Type {
    SINGLE,
    LEFT,
    RIGHT
  }

  private final String description;

  public Chest(String name, String typeString, String facingString, Kind kind) {
    super(name, switch (kind) {
      case TRAPPED -> Texture.trappedChest.front;
      case ENDER -> Texture.enderChest.front;
      case COPPER -> Texture.copperChest.front;
      default -> Texture.chest.front;
    });
    this.description = String.format("type=%s, facing=%s", typeString, facingString);
    Type type = switch (typeString) {
      case "left" -> Type.LEFT;
      case "right" -> Type.RIGHT;
      default -> Type.SINGLE;
    };
    int facing = switch (facingString) {
      case "north" -> 2;
      case "south" -> 3;
      case "west" -> 4;
      case "east" -> 5;
      default -> 2;
    };
    model = new ChestModel(type, facing, kind);
  }

  @Override
  public String description() {
    return description;
  }
}
