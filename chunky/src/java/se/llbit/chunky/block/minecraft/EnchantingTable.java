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
import se.llbit.chunky.entity.Book;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.minecraft.EnchantmentTableModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

import java.util.Collection;
import java.util.Collections;

public class EnchantingTable extends AbstractModelBlock {

  public EnchantingTable() {
    super("enchanting_table", Texture.enchantmentTableSide);
    solid = false;
    localIntersect = true;
    this.model = new EnchantmentTableModel();
  }

  @Override
  public boolean isEntity() {
    return true;
  }

  @Override
  public boolean isBlockWithEntity() {
    return true;
  }

  @Override
  public Collection<Entity> toEntity(Vector3 position) {
    Vector3 newPosition = new Vector3(position);
    newPosition.add(0, 0.35, 0);
    Book book = new Book(
        newPosition,
        Math.PI - Math.PI / 16,
        Math.toRadians(30),
        Math.toRadians(180 - 30));
    book.setPitch(Math.toRadians(80));
    book.setYaw(Math.toRadians(45));
    return Collections.singletonList(book);
  }
}
