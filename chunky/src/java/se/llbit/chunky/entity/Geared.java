/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.entity;

import se.llbit.json.JsonObject;

/**
 * Interface for entities that can equip armor and hold items.
 */
public interface Geared {

  /**
   * @return an array of the names of gear slots for this entity.
   */
  String[] gearSlots();

  JsonObject getGear();

  /**
   * Get the pose for one body part.
   */
  default JsonObject getGear(String slot) {
    return getGear().get(slot).object();
  }
}
