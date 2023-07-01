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
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import static java.lang.Math.PI;

/**
 * Nishita sky model based on the code presented in <a href="https://www.scratchapixel.com/lessons/procedural-generation-virtual-worlds/simulating-sky/simulating-colors-of-the-sky">Scratchapixel 2.0</a>.
 */
public class NishitaSky implements SimulatedSky {
  // Atmospheric constants
  private static final double EARTH_RADIUS = 6360e3;
  private static final double ATM_THICKNESS = 100e3;

  private static final double RAYLEIGH_SCALE = 8e3;
  private static final double MIE_SCALE = 1.2e3;

  private static final Vector3 BETA_R = new Vector3(3.8e-6, 13.5e-6, 33.1e-6);
  private static final Vector3 BETA_M = new Vector3(21e-6, 21e-6, 21e-6);

  private static final int SAMPLES = 16;
  private static final int SAMPLES_LIGHT = 8;

  // Sun position vector. Final to prevent unnecessary reallocation
  private final Vector3 sunPosition = new Vector3(0, 1, 0);
  private double sunIntensity = 1;
  private double horizonOffset = 0;

  // Sun position in spherical form for faster update checking
  private double theta;
  private double phi;

  /**
   * Create a new sky renderer.
   */
  public NishitaSky() {
  }

  @Override
  public boolean updateSun(Sun sun, double horizonOffset) {
    if (sunIntensity != sun.getIntensity() || theta != sun.getAzimuth() || phi != sun.getAltitude() || this.horizonOffset != horizonOffset) {
      theta = sun.getAzimuth();
      phi = sun.getAltitude();
      double r = QuickMath.abs(FastMath.cos(phi));
      sunPosition.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);
      sunIntensity = sun.getIntensity();

      this.horizonOffset = horizonOffset;
      sunPosition.y += horizonOffset;
      sunPosition.normalize();

      return true;
    }
    return false;
  }

  @Override
  public String getName() {
    return "Nishita";
  }

  @Override
  public String getDescription() {
    return "A slower, more realistic and flexible sky model.";
  }

  @Override
  public Vector3 calcIncidentLight(Vector3 d) {
    // Render from just above the surface of "earth"
    Vector3 origin = new Vector3(0, EARTH_RADIUS + 1, 0);
    Vector3 direction = d;
    direction.y += horizonOffset;
    direction.normalize();

    // Calculate the distance from the origin to the edge of the atmosphere
    double distance = sphereIntersect(origin, direction, EARTH_RADIUS + ATM_THICKNESS);
    if (distance == -1) {
      // No intersection, black
      return new Vector3(0, 0, 0);
    }

    // Ray march segment length
    double segmentLength = distance / SAMPLES;
    double currentDist = 0;

    double optDepthR = 0;
    double optDepthM = 0;

    double mu = direction.dot(sunPosition);
    double phaseR = (3 / (16 * PI)) * (1 + mu*mu);
    double g = 0.76;
    double phaseM = 3 / (8 * PI) * ((1 - g*g) * (1 + mu*mu)) / ((2 + g*g) * FastMath.pow(1 + g*g - 2*g*mu, 1.5));

    Vector3 sumR = new Vector3(0, 0, 0);
    Vector3 sumM = new Vector3(0, 0, 0);

    // Primary sample values
    Vector3 samplePosition = new Vector3();
    double height, hr, hm;

    // Sun sampling values
    Vector3 sunSamplePosition = new Vector3();
    double sunLength, sunSegment, sunCurrent, optDepthSunR, optDepthSunM, sunHeight;

    Vector3 tau = new Vector3();
    Vector3 attenuation = new Vector3();

    // Primary ray march out towards space
    for (int i = 0; i < SAMPLES; i++) {
      samplePosition.set(
          origin.x + (currentDist + segmentLength/2) * direction.x,
          origin.y + (currentDist + segmentLength/2) * direction.y,
          origin.z + (currentDist + segmentLength/2) * direction.z
      );
      height = samplePosition.length() - EARTH_RADIUS;

      hr = FastMath.exp(-height / RAYLEIGH_SCALE) * segmentLength;
      hm = FastMath.exp(-height / MIE_SCALE) * segmentLength;
      optDepthR += hr;
      optDepthM += hm;

      // Calculate the distance from the current point to the atmosphere in the direction of the sun
      sunLength = sphereIntersect(samplePosition, sunPosition, EARTH_RADIUS + ATM_THICKNESS);
      sunSegment = sunLength / SAMPLES_LIGHT;
      sunCurrent = 0;

      optDepthSunR = 0;
      optDepthSunM = 0;

      // Ray march towards the sun
      boolean flag = false;
      for (int j = 0; j < SAMPLES_LIGHT; j++) {
        sunSamplePosition.set(
            samplePosition.x + (sunCurrent + sunSegment/2) * sunPosition.x,
            samplePosition.y + (sunCurrent + sunSegment/2) * sunPosition.y,
            samplePosition.z + (sunCurrent + sunSegment/2) * sunPosition.z
        );
        sunHeight = sunSamplePosition.length() - EARTH_RADIUS;
        if (sunHeight < 0) {
          flag = true;
          break;
        }

        optDepthSunR += FastMath.exp(-sunHeight / RAYLEIGH_SCALE) * sunSegment;
        optDepthSunM += FastMath.exp(-sunHeight / MIE_SCALE) * sunSegment;

        sunCurrent += sunSegment;
      }

      // Only execute if we successfully march out of the atmosphere
      if (!flag) {
        tau.set(
            BETA_R.x * (optDepthR + optDepthSunR) + BETA_M.x * 1.1 * (optDepthM + optDepthSunM),
            BETA_R.y * (optDepthR + optDepthSunR) + BETA_M.y * 1.1 * (optDepthM + optDepthSunM),
            BETA_R.z * (optDepthR + optDepthSunR) + BETA_M.z * 1.1 * (optDepthM + optDepthSunM)
        );

        attenuation.set(
            FastMath.exp(-1 * tau.x),
            FastMath.exp(-1 * tau.y),
            FastMath.exp(-1 * tau.z)
        );

        sumR.add(
            attenuation.x * hr,
            attenuation.y * hr,
            attenuation.z * hr
        );

        sumM.add(
            attenuation.x * hm,
            attenuation.y * hm,
            attenuation.z * hm
        );
      }

      currentDist += segmentLength;
    }

    Vector3 color = new Vector3(
        (sumR.x* BETA_R.x*phaseR + sumM.x* BETA_M.x*phaseM) * sunIntensity * 5,
        (sumR.y* BETA_R.y*phaseR + sumM.y* BETA_M.y*phaseM) * sunIntensity * 5,
        (sumR.z* BETA_R.z*phaseR + sumM.z* BETA_M.z*phaseM) * sunIntensity * 5
    );

    // Tone-mapping function for more realistic colors
    color.set(
        color.x < 1.413 ? FastMath.pow(color.x * 0.38317, 1.0/2.2) : 1.0 - FastMath.exp(-color.x),
        color.y < 1.413 ? FastMath.pow(color.y * 0.38317, 1.0/2.2) : 1.0 - FastMath.exp(-color.y),
        color.z < 1.413 ? FastMath.pow(color.z * 0.38317, 1.0/2.2) : 1.0 - FastMath.exp(-color.z)
    );

    return color;
  }

  /** Calculate the distance from <code>origin</code> to the edge of a sphere centered at (0, 0, 0) in <code>direction</code>.*/
  private double sphereIntersect(Vector3 origin, Vector3 direction, double sphere_radius) {
    double a = direction.lengthSquared();
    double b = 2 * direction.dot(origin);
    double c = origin.lengthSquared() - sphere_radius*sphere_radius;

    if (b == 0) {
      if (a == 0) {
        // No intersection
        return -1;
      }

      return FastMath.sqrt(-c / a);
    }

    double disc = b*b - 4*a*c;

    if (disc < 0) {
      // No intersection
      return -1;
    }
    return (-b + FastMath.sqrt(disc)) / (2*a);
  }
}
