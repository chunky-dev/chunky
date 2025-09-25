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

import se.llbit.nbt.Tag;

public class PlayerEntityData {

  public final double x;
  public final double y;
  public final double z;
  public final int dimension;
  public final String uuid;

  public final Tag player;

  public PlayerEntityData(Tag player) {
    this.player = player;

    Tag pos = player.get("Pos");
    x = pos.get(0).doubleValue();
    y = pos.get(1).doubleValue();
    z = pos.get(2).doubleValue();

    this.uuid = loadUUID(player);

    dimension = player.get("Dimension").intValue();
  }

  public static String loadUUID(Tag tag) {
    long uuidHi;
    long uuidLo;
    if (tag.get("UUID").isIntArray(4)) {
      // since 20w12a (1.16) the UUID is saved in four 32-bit integers, ordered from most significant to least significant
      int[] uuid = tag.get("UUID").intArray();
      uuidHi = (((long) uuid[0]) << 32) | (uuid[1] & 0xffffffffL);
      uuidLo = (((long) uuid[2]) << 32) | (uuid[3] & 0xffffffffL);
    } else {
      // before 20w12a, the UUID was saved as two longs (64-bit)
      uuidLo = tag.get("UUIDLeast").longValue(-1);
      uuidHi = tag.get("UUIDMost").longValue(-1);
    }
    return String.format("%016X%016X", uuidHi, uuidLo);
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
