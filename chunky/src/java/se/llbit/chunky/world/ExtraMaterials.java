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

import java.util.HashMap;
import java.util.Map;

import se.llbit.chunky.block.Beacon;
import se.llbit.chunky.block.Candle;
import se.llbit.chunky.entity.BeaconBeam;
import se.llbit.chunky.entity.Campfire;
import se.llbit.chunky.world.material.CloudMaterial;

public class ExtraMaterials {

  public static final Map<String, Material> collections = new HashMap<>();
  public static final Map<String, Material> idMap = new HashMap<>();

  static {
    idMap.put("cloud", CloudMaterial.INSTANCE);
    idMap.put("candle_flame", Candle.flameMaterial);
    idMap.put("campfire_flame", Campfire.flameMaterial);
    idMap.put("soul_campfire_flame", Campfire.soulFlameMaterial);
    idMap.put("beacon_beam", BeaconBeam.beaconBeamMaterial);
  }

  public static void loadDefaultMaterialProperties() {
    CloudMaterial.INSTANCE.restoreDefaults();

    Candle.flameMaterial.restoreDefaults();
    Candle.flameMaterial.emittance = 1.0f;

    Campfire.flameMaterial.restoreDefaults();
    Campfire.flameMaterial.emittance = 1.0f;

    Campfire.soulFlameMaterial.restoreDefaults();
    Campfire.soulFlameMaterial.emittance = 0.6f;

    BeaconBeam.beaconBeamMaterial.restoreDefaults();
    BeaconBeam.beaconBeamMaterial.emittance = 1.0f;
  }
}
