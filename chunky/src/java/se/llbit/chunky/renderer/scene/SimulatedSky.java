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
  Vector3 calcIncidentLight(Ray ray, double horizonOffset);

  /**
   * Get the friendly name.
   */
  String getName();

  /**
   * Get the sky renderer tooltip.
   */
  String getTooltip();
}
