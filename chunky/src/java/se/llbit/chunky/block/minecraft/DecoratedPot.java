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
import se.llbit.chunky.model.minecraft.DecoratedPotModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.entity.Entity;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DecoratedPot extends AbstractModelBlock {

  private final String facing;
  private final String description;

  public DecoratedPot(String facing, boolean waterlogged, String[] sherds) {
    super("decorated_pot", Texture.decoratedPotSide);
    this.waterlogged = waterlogged;
    this.facing = facing;
    description = "waterlogged=" + waterlogged
      + ", facing=" + facing
      + ", sherds=" + Arrays.stream(sherds)
      .map(sherd -> sherd == null ? "minecraft:brick" : sherd)
      .collect(Collectors.joining(", ", "[", "]"));
    model = new DecoratedPotModel(facing, sherds);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public boolean isModifiedByBlockEntity() {
    return true;
  }

  @Override
  public boolean isReplacedByEntities() {
    return false;
  }

  @Override
  public Entity createBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new DecoratedPotModel.DecoratedPotSpoutEntity(position, facing);
  }

  @Override
  public Tag getNewTagWithBlockEntity(Tag blockTag, CompoundTag entityTag) {
    CompoundTag newBlockTag = new CompoundTag();
    // create a copy of the block tag
    StreamSupport.stream(blockTag.asCompound().spliterator(), false)
      .filter(namedTag -> !namedTag.name.equals("Properties"))
      .forEach(newBlockTag::add);
    CompoundTag properties = new CompoundTag();
    newBlockTag.add("Properties", properties); // because set is not implemented
    blockTag.get("Properties").asCompound().iterator().forEachRemaining(properties::add);

    properties.add("blockEntity#sherds", entityTag.get("sherds").asList());
    properties.add("chunky#debugCoordinates", new StringTag(
      entityTag.get("x").intValue() + " " + entityTag.get("z").intValue()
    ));
    return newBlockTag;
  }
}
