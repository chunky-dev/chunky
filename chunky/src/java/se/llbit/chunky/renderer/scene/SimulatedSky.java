/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.scene;

import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * Interface for simulated skies.
 */
public interface SimulatedSky {
  /**
   * Update the sun
   */
  void updateSun(Sun sun);

  /**
   * Check if the sky needs an update with a new sun.
   */
  boolean needUpdate(Sun sun);

  /**
   * Calculate the sky color for a given ray.
   */
  Vector3 calcIncidentLight(Ray ray);

  /**
   * Get the friendly name.
   */
  String getName();

  /**
   * Get the sky renderer tooltip.
   */
  String getTooltip();
}
