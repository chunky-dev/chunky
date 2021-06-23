/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.world;

import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class PlayerEntityData {

  public final double x;
  public final double y;
  public final double z;
  public final double rotation;
  public final double pitch;
  public final int dimension;
  public Tag feet = new CompoundTag();
  public Tag legs = new CompoundTag();
  public Tag head = new CompoundTag();
  public Tag chestplate = new CompoundTag();
  public Tag shield = new CompoundTag();
  public Tag mainHand = new CompoundTag();
  public final String uuid;

  public PlayerEntityData(Tag player) {
    Tag pos = player.get("Pos");
    Tag rotation = player.get("Rotation");

    long uuidHi;
    long uuidLo;
    if (player.get("UUID").isIntArray(4)) {
      // since 20w12a (1.16) the UUID is saved in four 32-bit integers, ordered from most significant to least significant
      int[] uuid = player.get("UUID").intArray();
      uuidHi = (((long) uuid[0]) << 32) | (uuid[1] & 0xffffffffL);
      uuidLo = (((long) uuid[2]) << 32) | (uuid[3] & 0xffffffffL);
    } else {
      // before 20w12a, the UUID was saved as two longs (64-bit)
      uuidLo = player.get("UUIDLeast").longValue(-1);
      uuidHi = player.get("UUIDMost").longValue(-1);
    }
    uuid = String.format("%016X%016X", uuidHi, uuidLo);
    x = pos.get(0).doubleValue();
    y = pos.get(1).doubleValue();
    z = pos.get(2).doubleValue();
    this.rotation = rotation.get(0).floatValue();
    pitch = rotation.get(1).floatValue();
    dimension = player.get("Dimension").intValue();

    int selectedItem = player.get("SelectedItemSlot").intValue(0);

    for (Tag item : player.get("Inventory").asList()) {
      int slot = item.get("Slot").byteValue(0);
      switch (slot) {
        case -106:
          shield = item;
          break;
        case 100:
          feet = item;
          break;
        case 101:
          legs = item;
          break;
        case 102:
          chestplate = item;
          break;
        case 103:
          head = item;
          break;
      }
      if (slot == selectedItem) {
        mainHand = item;
      }
    }
  }

  @Override
  public String toString() {
    return String.format("%d: %d, %d, %d", dimension, (int) x, (int) y, (int) z);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PlayerEntityData) {
      return ((PlayerEntityData) obj).uuid.equals(uuid);
    }
    return false;
  }
}
