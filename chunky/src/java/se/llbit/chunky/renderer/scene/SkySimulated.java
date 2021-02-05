package se.llbit.chunky.renderer.scene;

import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * Interface for simulated skies.
 */
public interface SkySimulated {
  /**
   * Update the sky with a new sun object.
   */
  void updateSun(Sun sun);

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
