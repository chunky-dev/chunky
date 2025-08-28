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

import se.llbit.chunky.block.FixedTopBottomRotatableTexturedBlock;
import se.llbit.chunky.resources.Texture;

public class ChiseledBookshelf extends FixedTopBottomRotatableTexturedBlock {

  private final String description;

  public ChiseledBookshelf(String facing, boolean slot0, boolean slot1, boolean slot2, boolean slot3, boolean slot4, boolean slot5) {
      super("chiseled_bookshelf", facing, Texture.chiseledBookshelfCombinations[(slot0?1:0) + (slot1?2:0) + (slot2?4:0) + (slot3?8:0) + (slot4?16:0) + (slot5?32:0)],
        Texture.chiseledBookshelfSide, Texture.chiseledBookshelfTop);
    this.description = String.format("facing=%s, slot0=%s, slot1=%s, slot2=%s, slot3=%s, slot4=%s, slot5=%s", facing, slot0, slot1, slot2, slot3, slot4, slot5);
  }

  @Override
  public String description() {
    return description;
  }
}
