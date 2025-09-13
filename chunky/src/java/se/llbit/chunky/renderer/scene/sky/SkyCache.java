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

package se.llbit.chunky.renderer.scene.sky;

import static java.lang.Math.PI;

import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.main.Chunky;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * A sky cache. Precalculates sky colors and them uses cached values with bilinear interpolation.
 * <p>
 * Default cache size is 128x128 which seems to work well for all simulated sky modes.
 */
public class SkyCache {

  // Sky texture array, HSV colors
  private double[][][] skyTexture;

  // Default resolution is 128x128. Should be enough for most simulated skies.
  private int skyResolution = 128;

  private SimulatedSky simSky;

  /**
   * Create a new sky cache for the given sky configuration and precalculate the sky texture.
   *
   * @param sky Sky object to pull sky renderer from
   */
  public SkyCache(Sky sky) {
    simSky = sky.getSimulatedSky();
    precalculateSky();
  }

  /**
   * Set this cache's content to the content of another cache.
   */
  public synchronized void set(SkyCache cache) {
    this.skyResolution = cache.skyResolution;
    this.simSky = cache.simSky;
    this.skyTexture = cache.skyTexture;
  }

  /**
   * Recalculate the cached sky texture.
   * <p>
   * The setters already invoke this method automatically so this only needs to be called manually
   * e.g. if the sun position changes.
   */
  public synchronized void precalculateSky() {
    double[][][] skyTexture = new double[skyResolution + 1][skyResolution + 1][3];

    try {
      Chunky.getCommonThreads().submit(() -> {
        IntStream.range(0, skyResolution + 1).parallel().forEach(i -> {
          for (int j = 0; j < skyResolution + 1; j++) {
            Vector3 c = getSkyColorAt(i, j);
            skyTexture[i][j][0] = c.x;
            skyTexture[i][j][1] = c.y;
            skyTexture[i][j][2] = c.z;
          }
        });
      }).get();
    } catch (InterruptedException e) {
      // Stopped
    } catch (ExecutionException e) {
      Log.error(e);
      return;
    }

    this.skyTexture = skyTexture;
  }

  /**
   * Adjust the sky resolution and update the precalculated sky.
   *
   * @param skyResolution New resolution (width and height) in pixels
   */
  public void setSkyResolution(int skyResolution) {
    this.skyResolution = skyResolution;
    precalculateSky();
  }

  /**
   * Get the current sky resolution.
   *
   * @return Sky resolution (width and height) in pixels
   */
  public int getSkyResolution() {
    return this.skyResolution;
  }

  /**
   * Set the sky simulation and update the precalculated sky.
   *
   * @param skyMode Simulated sky mode
   */
  public void setSimulatedSkyMode(SimulatedSky skyMode) {
    this.simSky = skyMode;
    precalculateSky();
  }

  /**
   * Calculate the incident light for the given ray. This uses bilinearly interpolated precalculated
   * values.
   *
   * @param ray Ray to calculate the incident light for
   * @return Incident light color (RGB)
   */
  public Vector3 calcIncidentLight(Ray ray) {
    double theta = FastMath.atan2(ray.d.z, ray.d.x);
    theta /= PI * 2;
    theta = ((theta % 1) + 1) % 1;
    double phi = (FastMath.asin(QuickMath.clamp(ray.d.y, -1, 1)) + PI / 2) / PI;

    return getColorInterpolated(theta, phi);
  }

  // Linear interpolation between 2 points in 1 dimension
  private static double interp1D(double x, double x0, double x1, double y0, double y1) {
    return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
  }

  /**
   * Calculate the bilinearly interpolated value from the cache.
   */
  private Vector3 getColorInterpolated(double normX, double normY) {
    double x = normX * skyResolution;
    double y = normY * skyResolution;
    int floorX = (int) QuickMath.clamp(x, 0, skyResolution - 1);
    int floorY = (int) QuickMath.clamp(y, 0, skyResolution - 1);

    double[] color = new double[3];
    for (int i = 0; i < 3; i++) {
      double y0 = interp1D(x, floorX, floorX + 1, skyTexture[floorX][floorY][i],
          skyTexture[floorX + 1][floorY][i]);
      double y1 = interp1D(x, floorX, floorX + 1, skyTexture[floorX][floorY + 1][i],
          skyTexture[floorX + 1][floorY + 1][i]);
      color[i] = interp1D(y, floorY, floorY + 1, y0, y1);
    }
    return new Vector3(color[0], color[1], color[2]);
  }

  /**
   * Calculate the sky color for a pixel on the cache.
   */
  private Vector3 getSkyColorAt(int x, int y) {
    Ray ray = new Ray();

    double theta = ((double) x / skyResolution) * 2 * PI;
    double phi = ((double) y / skyResolution) * PI - PI / 2;
    double r = FastMath.cos(phi);
    ray.d.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);

    return simSky.calcIncidentLight(ray);
  }
}
