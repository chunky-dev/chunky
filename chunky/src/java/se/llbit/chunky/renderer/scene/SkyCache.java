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

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import static java.lang.Math.PI;

public class SkyCache {
  // Sky texture array, HSV colors
  private double[][][] skyTexture;

  // Default resolution is 1024x1024. Should be more than enough for any simulated sky.
  private int skyResolution = 1024;

  private SimulatedSky simSky;
  private Sky sky;

  private int version = 0;

  /**
   * An on-the-fly sky cache. Automatically calculates sky colors as they are requested and caches them. Any repeat
   * requests will pull from the cache.
   *
   * Default cache size is 1024x1024 with bilinear interpolation which seems to work well for all simulated sky
   * modes.
   *
   * @param sky Sky object to pull sky renderer from.
   */
  public SkyCache(Sky sky) {
    this.sky = sky;
    skyTexture = null;
  }

  /** Sync this cache with another cache */
  public void syncCache(SkyCache cache) {
    if (this.skyResolution != cache.skyResolution) {
      setSkyResolution(cache.skyResolution);
    }

    if (this.simSky != cache.simSky) {
      this.simSky = cache.simSky;
      skyTexture = null;
    }

    if (this.version != cache.version) {
      this.version = cache.version;
      skyTexture = null;
    }
  }

  /** Reset the sky cache */
  public void reset(Sky sky) {
    simSky = sky.getSimulatedSky();
    version += 1;

    // Sky has not yet been initialized. No need to reset.
    if (skyTexture == null) {
      return;
    }

    for (int i = 0; i < skyResolution+1; i++) {
      for (int j = 0; j < skyResolution+1; j++) {
        for (int k = 0; k < 3; k++) {
          skyTexture[i][j][k] = -1;
        }
      }
    }
  }

  /** Adjust the sky resolution and reset the cache */
  public void setSkyResolution(int skyResolution) {
    this.skyResolution = skyResolution;
    skyTexture = null;
  }

  /** Get the current sky resolution */
  public int getSkyResolution() {
    return this.skyResolution;
  }

  /**
   * Calculate the incident light. Will automatically pull from the cache or calculate new values. Cache values are
   * bilinearly interpolated.
   */
  public Vector3 calcIncidentLight(Ray ray) {
    if (skyTexture == null) {
      skyTexture = new double[skyResolution+1][skyResolution+1][3];
      reset(sky);
    }

    double theta = FastMath.atan2(ray.d.z, ray.d.x);
    theta /= PI*2;
    theta = ((theta % 1) + 1) % 1;
    double phi = (FastMath.asin(QuickMath.clamp(ray.d.y, -1, 1)) + PI/2) / PI;

    Vector3 color = getColorInterpolated(theta, phi);
    ColorUtil.RGBfromHSL(color, color.x, color.y, color.z);
    return color;
  }

  // Linear interpolation between 2 points in 1 dimension
  private double interp1D(double x, double x0, double x1, double y0, double y1) {
    return y0 + (x - x0)*(y1-y0)/(x1-x0);
  }

  // Calculate the bilinearly interpolated value from the cache.
  private Vector3 getColorInterpolated(double normX, double normY) {
    double x = normX * skyResolution;
    double y = normY * skyResolution;
    int floorX = (int) QuickMath.clamp(x, 0, skyResolution-1);
    int floorY = (int) QuickMath.clamp(y, 0, skyResolution-1);

    if (skyTexture[floorX][floorY][0] < 0) bake(floorX, floorY);
    if (skyTexture[floorX][floorY+1][0] < 0) bake(floorX, floorY+1);
    if (skyTexture[floorX+1][floorY][0] < 0) bake(floorX+1, floorY);
    if (skyTexture[floorX+1][floorY+1][0] < 0) bake(floorX+1, floorY+1);

    double[] color = new double[3];
    for (int i = 0; i < 3; i++) {
      double y0 = interp1D(x, floorX, floorX + 1, skyTexture[floorX][floorY][i], skyTexture[floorX+1][floorY][i]);
      double y1 = interp1D(x, floorX, floorX + 1, skyTexture[floorX][floorY+1][i], skyTexture[floorX+1][floorY+1][i]);
      color[i] = interp1D(y, floorY, floorY + 1, y0, y1);
    }

    return new Vector3(color[0], color[1], color[2]);
  }

  // Calculate the sky color for a pixel on the cache.
  private void bake(int x, int y) {
    Ray ray = new Ray();

    double theta = ((double) x / skyResolution) * 2 * PI;
    double phi = ((double) y / skyResolution) * PI - PI/2;
    double r = FastMath.cos(phi);
    ray.d.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);

    Vector3 color = simSky.calcIncidentLight(ray);
    ColorUtil.RGBtoHSL(color, color.x, color.y, color.z);
    skyTexture[x][y][0] = color.x;
    skyTexture[x][y][1] = color.y;
    skyTexture[x][y][2] = color.z;
  }
}
