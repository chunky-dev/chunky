/* Copyright (c) 2012 - 2022 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012 - 2022 Chunky contributors
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

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.math.*;
import se.llbit.util.JsonSerializable;

/**
 * Sun model for ray tracing.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sun implements JsonSerializable {

  /**
   * Default sun intensity
   */
  public static final double DEFAULT_INTENSITY = 2_500;

  /**
   * Maximum sun intensity
   */
  public static final double MAX_INTENSITY = 1_000_000_000;

  /**
   * Minimum sun intensity
   */
  public static final double MIN_INTENSITY = 0.001;

  /**
   * Sun texture
   */
  public static Texture texture = new Texture();

  private final Refreshable scene;

  /**
   * Sun radius
   */
  public double radius = 0.041887902;
  public double radiusCos = FastMath.cos(radius);
  public double radiusSin = FastMath.sin(radius);

  private static final double AMBIENT = .3;

  private double intensity = DEFAULT_INTENSITY;

  private boolean useFlatTexture = true;

  private double azimuth = Math.PI / 2.5;
  private double altitude = Math.PI / 3;

  // Support vectors.
  private final Vector3 su = new Vector3();
  private final Vector3 sv = new Vector3();

  /**
   * Location of the sun in the sky.
   */
  private final Vector3 sw = new Vector3();

  // final to ensure that we don't do a lot of redundant re-allocation
  private final Vector3 color = new Vector3(1, 1, 1);

  private boolean drawTexture = true;

  /**
   * Create new sun model.
   */
  public Sun(Refreshable sceneDescription) {
    this.scene = sceneDescription;
    initSun();
  }

  /**
   * Set equal to other sun model.
   */
  public void set(Sun other) {
    azimuth = other.azimuth;
    altitude = other.altitude;
    color.set(other.color);
    drawTexture = other.drawTexture;
    useFlatTexture = other.useFlatTexture;
    intensity = other.intensity;
    radius = other.radius;
    initSun();
  }

  private void initSun() {
    radiusCos = FastMath.cos(radius);
    radiusSin = FastMath.sin(radius);

    double theta = azimuth;
    double phi = altitude;

    double r = QuickMath.abs(FastMath.cos(phi));

    sw.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);

    if (QuickMath.abs(sw.x) > .1) {
      su.set(0, 1, 0);
    } else {
      su.set(1, 0, 0);
    }
    sv.cross(sw, su);
    sv.normalize();
    su.cross(sv, sw);

    Sky sky = ((Scene) scene).sky();
    if (sky.getSkyMode() == Sky.SkyMode.SIMULATED) {
      sky.updateSimulatedSky(this);
    }
  }

  /**
   * Angle of the sun around the horizon, measured from north.
   */
  public void setAzimuth(double value) {
    azimuth = QuickMath.modulo(value, Math.PI * 2);
    initSun();
    scene.refresh();
  }

  /**
   * Sun altitude from the horizon.
   */
  public void setAltitude(double value) {
    altitude = value;
    initSun();
    scene.refresh();
  }

  /**
   * @return Zenith angle
   */
  public double getAltitude() {
    return altitude;
  }

  /**
   * @return Azimuth
   */
  public double getAzimuth() {
    return azimuth;
  }

  /**
   * Check if the ray intersects the sun.
   *
   * @return <code>true</code> if the ray intersects the sun model
   */
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord) {
    return (ray.d.dot(sw) > radiusCos);
  }

  public Vector4 getSunIntersectionColor(Ray ray) {
    final Vector4 sunColor = new Vector4(1, 1, 1, 1);
    if (intersect(ray, null) && drawTexture) {
      double width = radius * Constants.SQRT_2;
      double half_width = width / 2;
      double a = Math.PI / 2 - FastMath.acos(ray.d.dot(su)) + half_width;
      if (a >= 0 && a < width) {
        double b = Math.PI / 2 - FastMath.acos(ray.d.dot(sv)) + half_width;
        if (b >= 0 && b < width) {
          if (!useFlatTexture) {
            texture.getColor(a / width, b / width, sunColor);
          }
          sunColor.x *= color.x * intensity;
          sunColor.y *= color.y * intensity;
          sunColor.z *= color.z * intensity;
          return sunColor;
        }
      }
    }
    sunColor.set(0, 0, 0, 1);
    return sunColor;
  }

  /**
   * Calculate flat shading for ray.
   */
  public void flatShading(IntersectionRecord intersectionRecord) {
    Vector3 n = intersectionRecord.shadeN;
    double shading = n.x * sw.x + n.y * sw.y + n.z * sw.z;
    shading = QuickMath.max(AMBIENT, shading);
    intersectionRecord.color.scale(shading);
  }

  public void setColor(Vector3 newColor) {
    this.color.set(newColor);
    initSun();
    scene.refresh();
  }

  /**
   * Set the sun intensity
   */
  public void setIntensity(double value) {
    intensity = value;
    initSun();
    scene.refresh();
  }

  /**
   * @return The sun intensity
   */
  public double getIntensity() {
    return intensity;
  }

  /**
   * @param value Sun radius in radians.
   */
  public void setSunRadius(double value) {
    radius = value;
    initSun();
    scene.refresh();
  }

  /**
   * @return Sun radius in radians.
   */
  public double getSunRadius() {
    return radius;
  }

  /**
   * Point ray in random direction within sun solid angle
   */
  public void getRandomSunDirection(Vector3 d, Random random) {
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double cos_a = 1 - x1 + x1 * radiusCos;
    double sin_a = FastMath.sqrt(1 - cos_a * cos_a);
    double phi = 2 * Math.PI * x2;

    Vector3 u = new Vector3(su);
    Vector3 v = new Vector3(sv);
    Vector3 w = new Vector3(sw);

    u.scale(FastMath.cos(phi) * sin_a);
    v.scale(FastMath.sin(phi) * sin_a);
    w.scale(cos_a);

    d.add(u, v);
    d.add(w);
    d.normalize();
  }

  @Override public JsonObject toJson() {
    JsonObject sun = new JsonObject();
    sun.add("altitude", altitude);
    sun.add("azimuth", azimuth);
    sun.add("intensity", intensity);
    sun.add("radius", radius);
    sun.add("color", ColorUtil.rgbToJson(color));
    sun.add("drawTexture", drawTexture);
    sun.add("useFlatTexture", useFlatTexture);
    return sun;
  }

  public void importFromJson(JsonObject json) {
    azimuth = json.get("azimuth").doubleValue(azimuth);
    altitude = json.get("altitude").doubleValue(altitude);
    intensity = json.get("intensity").doubleValue(intensity);
    radius = json.get("radius").doubleValue(radius);
    color.set(ColorUtil.jsonToRGB(json.get("color").asObject()));
    drawTexture = json.get("drawTexture").boolValue(drawTexture);
    useFlatTexture = json.get("useFlatTexture").boolValue(useFlatTexture);
    initSun();
  }

  /**
   * @return sun color
   */
  public Vector3 getColor() {
    return color;
  }

  public void setDrawTexture(boolean value) {
    if (value != drawTexture) {
      drawTexture = value;
      scene.refresh();
    }
  }

  public boolean getDrawTexture() {
    return drawTexture;
  }

  public void setUseFlatTexture(boolean value) {
    if (value != useFlatTexture) {
      useFlatTexture = value;
      scene.refresh();
    }
  }

  public boolean getUseFlatTexture() {
    return useFlatTexture;
  }
}
