/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.block.minecraft.Candle;
import se.llbit.chunky.entity.CalibratedSculkSensorAmethyst;
import se.llbit.chunky.entity.Campfire;
import se.llbit.chunky.world.material.CloudMaterial;

import java.util.HashMap;
import java.util.Map;

public class ExtraMaterials {

  public static final Map<String, Material> collections = new HashMap<>();
  public static final Map<String, Material> idMap = new HashMap<>();

  static {
    idMap.put("cloud", CloudMaterial.INSTANCE);
    idMap.put("candle_flame", Candle.flameMaterial);
    idMap.put("campfire_flame", Campfire.flameMaterial);
    idMap.put("soul_campfire_flame", Campfire.soulFlameMaterial);
    idMap.put("calibrated_sculk_sensor_amethyst_active", CalibratedSculkSensorAmethyst.activeMaterial);
    idMap.put("calibrated_sculk_sensor_amethyst_inactive", CalibratedSculkSensorAmethyst.inactiveMaterial);
  }

  public static void loadDefaultMaterialProperties() {
    CloudMaterial.INSTANCE.restoreDefaults();

    Candle.flameMaterial.restoreDefaults();
    Candle.flameMaterial.setLightLevel(12);
    Candle.flameMaterial.emitterMappingOffset = -0.5f;

    Campfire.flameMaterial.restoreDefaults();
    Campfire.flameMaterial.setLightLevel(15);
    Campfire.flameMaterial.emitterMappingOffset = -0.5f;

    Campfire.soulFlameMaterial.restoreDefaults();
    Campfire.soulFlameMaterial.setLightLevel(10);
    Campfire.soulFlameMaterial.emitterMappingOffset = -0.5f;

    CalibratedSculkSensorAmethyst.activeMaterial.restoreDefaults();
    CalibratedSculkSensorAmethyst.activeMaterial.setLightLevel(1);

    CalibratedSculkSensorAmethyst.inactiveMaterial.restoreDefaults();
  }
}
