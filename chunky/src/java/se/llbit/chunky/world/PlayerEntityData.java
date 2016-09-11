/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.nbt.AnyTag;

public class PlayerEntityData {
  public final double x;
  public final double y;
  public final double z;
  public final double yaw;
  public final double pitch;
  public final int dimension;
  public final long uuidLo;
  public final long uuidHi;

  public final String uuid;

  public PlayerEntityData(AnyTag player) {
    AnyTag pos = player.get("Pos");
    AnyTag rotation = player.get("Rotation");

    uuidLo = player.get("UUIDLeast").longValue(-1);
    uuidHi = player.get("UUIDMost").longValue(-1);
    x = pos.get(0).doubleValue();
    y = pos.get(1).doubleValue();
    z = pos.get(2).doubleValue();
    yaw = rotation.get(0).floatValue();
    pitch = rotation.get(1).floatValue();
    dimension = player.get("Dimension").intValue();

    uuid = String.format("%016X%016X", uuidHi, uuidLo);
  }

  @Override public String toString() {
    return String.format("%d: %d, %d, %d", dimension, (int) x, (int) y, (int) z);
  }

  @Override public int hashCode() {
    return uuid.hashCode();
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof PlayerEntityData) {
      return ((PlayerEntityData) obj).uuid.equals(uuid);
    }
    return false;
  }
}
