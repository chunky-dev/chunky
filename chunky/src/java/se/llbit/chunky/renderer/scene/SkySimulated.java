package se.llbit.chunky.renderer.scene;

import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * Interface for simulated skies.
 */
public interface SkySimulated {
  /**
   * Calculate the sky color for a given ray.
   */
  Vector3 calcIncidentLight(Ray ray, Sun sun, double horizonOffset);

  /**
   * Get the friendly name.
   */
  String getName();

  /**
   * Get the sky renderer tooltip.
   */
  String getTooltip();
}
