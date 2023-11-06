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
import se.llbit.chunky.entity.CalibratedSculkSensorAmethyst;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.minecraft.CalibratedSculkSensorModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

public class CalibratedSculkSensor extends AbstractModelBlock {
  private final String phase;
  private final String facing;

  public CalibratedSculkSensor(String phase, String facing) {
    super("calibrated_sculk_sensor", Texture.calibratedSculkSensorTop);
    this.phase = phase;
    this.facing = facing;
    this.model = new CalibratedSculkSensorModel(isActive(), facing);
  }

  public boolean isActive() {
    return phase.equals("active") || phase.equals("cooldown");
  }

  @Override
  public String description() {
    return "sculk_sensor_phase=" + phase + ", facing=" + facing;
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
  public Entity toEntity(Vector3 position) {
    return new CalibratedSculkSensorAmethyst(position, this.facing, isActive(), this);
  }
}
