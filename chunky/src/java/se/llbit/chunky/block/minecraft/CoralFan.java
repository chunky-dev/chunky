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

import se.llbit.chunky.entity.CoralFanEntity;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

public class CoralFan extends EmptyModelBlock {

  private final String coralType;

  public CoralFan(String name, String coralType) {
    super(name, coralTexture(coralType));
    this.coralType = coralType;
    invisible = true;
  }

  public static Texture coralTexture(String coralType) {
    switch (coralType) {
      default:
      case "tube":
        return Texture.tubeCoralFan;
      case "brain":
        return Texture.brainCoralFan;
      case "horn":
        return Texture.hornCoralFan;
      case "bubble":
        return Texture.bubbleCoralFan;
      case "fire":
        return Texture.fireCoralFan;
      case "dead_tube":
        return Texture.deadTubeCoralFan;
      case "dead_brain":
        return Texture.deadBrainCoralFan;
      case "dead_horn":
        return Texture.deadHornCoralFan;
      case "dead_bubble":
        return Texture.deadBubbleCoralFan;
      case "dead_fire":
        return Texture.deadFireCoralFan;
    }
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity[] toEntity(Vector3 position) {
    return new CoralFanEntity[] {new CoralFanEntity(position, coralType)};
  }
}
