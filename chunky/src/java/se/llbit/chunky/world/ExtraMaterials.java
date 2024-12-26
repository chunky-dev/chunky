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
import se.llbit.chunky.block.minecraft.OpenEyeblossom;
import se.llbit.chunky.entity.CalibratedSculkSensorAmethyst;
import se.llbit.chunky.entity.Campfire;
import se.llbit.chunky.entity.SporeBlossom;
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
    idMap.put("spore_blossom (base)", SporeBlossom.baseMaterial);
    idMap.put("spore_blossom (blossom)", SporeBlossom.blossomMaterial);
    idMap.put("open_eyeblossom (emissive)", OpenEyeblossom.emissiveMaterial);
  }

  public static void loadDefaultMaterialProperties() {
    CloudMaterial.INSTANCE.restoreDefaults();

    Candle.flameMaterial.restoreDefaults();
    Candle.flameMaterial.emittance = 1.0f;

    Campfire.flameMaterial.restoreDefaults();
    Campfire.flameMaterial.emittance = 1.0f;

    Campfire.soulFlameMaterial.restoreDefaults();
    Campfire.soulFlameMaterial.emittance = 0.6f;

    CalibratedSculkSensorAmethyst.activeMaterial.restoreDefaults();
    CalibratedSculkSensorAmethyst.activeMaterial.emittance = 1.0f / 15;

    CalibratedSculkSensorAmethyst.inactiveMaterial.restoreDefaults();

    SporeBlossom.blossomMaterial.restoreDefaults();
    SporeBlossom.baseMaterial.restoreDefaults();

    OpenEyeblossom.emissiveMaterial.restoreDefaults();
    OpenEyeblossom.emissiveMaterial.emittance = 1.0f / 15;
  }
}
