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
package se.llbit.chunky.renderer.scene;

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
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
  public static final double DEFAULT_INTENSITY = 1.25;

  /**
   * Maximum sun intensity
   */
  public static final double MAX_INTENSITY = 50;

  /**
   * Minimum sun intensity
   */
  public static final double MIN_INTENSITY = 0.1;

  public static final double MIN_APPARENT_BRIGHTNESS = 0.01;

  public static final double MAX_APPARENT_BRIGHTNESS = 50;

  private static final double xZenithChroma[][] =
      {{0.00166, -0.00375, 0.00209, 0}, {-0.02903, 0.06377, -0.03203, 0.00394},
          {0.11693, -0.21196, 0.06052, 0.25886},};
  private static final double yZenithChroma[][] =
      {{0.00275, -0.00610, 0.00317, 0}, {-0.04214, 0.08970, -0.04153, 0.00516},
          {0.15346, -0.26756, 0.06670, 0.26688},};
  private static final double mdx[][] =
      {{-0.0193, -0.2592}, {-0.0665, 0.0008}, {-0.0004, 0.2125}, {-0.0641, -0.8989},
          {-0.0033, 0.0452}};
  private static final double mdy[][] =
      {{-0.0167, -0.2608}, {-0.0950, 0.0092}, {-0.0079, 0.2102}, {-0.0441, -1.6537},
          {-0.0109, 0.0529}};
  private static final double mdY[][] =
      {{0.1787, -1.4630}, {-0.3554, 0.4275}, {-0.0227, 5.3251}, {0.1206, -2.5771},
          {-0.0670, 0.3703}};

  private static double turb = 2.5;
  private static double turb2 = turb * turb;
  private static Vector3 A = new Vector3();
  private static Vector3 B = new Vector3();
  private static Vector3 C = new Vector3();
  private static Vector3 D = new Vector3();
  private static Vector3 E = new Vector3();

  /**
   * Sun texture
   */
  public static Texture texture = new Texture();

  static {
    A.x = mdx[0][0] * turb + mdx[0][1];
    B.x = mdx[1][0] * turb + mdx[1][1];
    C.x = mdx[2][0] * turb + mdx[2][1];
    D.x = mdx[3][0] * turb + mdx[3][1];
    E.x = mdx[4][0] * turb + mdx[4][1];

    A.y = mdy[0][0] * turb + mdy[0][1];
    B.y = mdy[1][0] * turb + mdy[1][1];
    C.y = mdy[2][0] * turb + mdy[2][1];
    D.y = mdy[3][0] * turb + mdy[3][1];
    E.y = mdy[4][0] * turb + mdy[4][1];

    A.z = mdY[0][0] * turb + mdY[0][1];
    B.z = mdY[1][0] * turb + mdY[1][1];
    C.z = mdY[2][0] * turb + mdY[2][1];
    D.z = mdY[3][0] * turb + mdY[3][1];
    E.z = mdY[4][0] * turb + mdY[4][1];
  }

  private double zenith_Y;
  private double zenith_x;
  private double zenith_y;
  private double f0_Y;
  private double f0_x;
  private double f0_y;

  private final Refreshable scene;

  /**
   * Sun radius
   */
  public static final double RADIUS = .03;
  public static final double RADIUS_COS = FastMath.cos(RADIUS);
  public static final double RADIUS_SIN = FastMath.sin(RADIUS);

  private static final double AMBIENT = .3;

  private double intensity = DEFAULT_INTENSITY;

  private double luminosity = 100;
  private double luminosityPdf = 1.0 / luminosity;

  private double apparentBrightness = DEFAULT_INTENSITY;
  private Vector3 apparentTextureBrightness = new Vector3(1, 1, 1);
  private boolean enableTextureModification = false;

  private double azimuth = Math.PI / 2.5;
  private double altitude = Math.PI / 3;

  // Support vectors.
  private final Vector3 su = new Vector3();
  private final Vector3 sv = new Vector3();

  /**
   * Location of the sun in the sky.
   */
  private final Vector3 sw = new Vector3();

  protected final Vector3 emittance = new Vector3(1, 1, 1);

  // final to ensure that we don't do a lot of redundant re-allocation
  private final Vector3 color = new Vector3(1, 1, 1);

  private boolean drawTexture = true;

  private double chroma(double turb, double turb2, double sunTheta, double[][] matrix) {

    double t1 = sunTheta;
    double t2 = t1 * t1;
    double t3 = t1 * t2;

    return turb2 * (matrix[0][0] * t3 + matrix[0][1] * t2 + matrix[0][2] * t1 + matrix[0][3]) +
        turb * (matrix[1][0] * t3 + matrix[1][1] * t2 + matrix[1][2] * t1 + matrix[1][3]) +
        (matrix[2][0] * t3 + matrix[2][1] * t2 + matrix[2][2] * t1 + matrix[2][3]);
  }

  private static double perezF(double cosTheta, double gamma, double cos2Gamma, double A, double B,
      double C, double D, double E) {

    return (1 + A * FastMath.exp(B / cosTheta)) * (1 + C * FastMath.exp(D * gamma) + E * cos2Gamma);
  }

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
    intensity = other.intensity;
    luminosity = other.luminosity;
    apparentBrightness = other.apparentBrightness;
    enableTextureModification = other.enableTextureModification;
    luminosityPdf = other.luminosityPdf;
    initSun();
  }

  private void initSun() {
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

    emittance.set(color);
    emittance.scale(FastMath.pow(intensity, Scene.DEFAULT_GAMMA));

    if (enableTextureModification) {
      apparentTextureBrightness.set(color);
    } else {
      apparentTextureBrightness.set(1, 1, 1);
    }
    apparentTextureBrightness.scale(FastMath.pow(apparentBrightness, Scene.DEFAULT_GAMMA));

    Sky sky = ((Scene) scene).sky();
    if (sky.getSkyMode() == Sky.SkyMode.SIMULATED) {
      sky.updateSimulatedSky(this);
    }

    updateSkylightValues();
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
  public boolean intersect(Ray ray) {
    if (!drawTexture || ray.d.dot(sw) < .5) {
      return false;
    }

    double WIDTH = RADIUS * 4;
    double WIDTH2 = WIDTH * 2;
    double a;
    a = Math.PI / 2 - FastMath.acos(ray.d.dot(su)) + WIDTH;
    if (a >= 0 && a < WIDTH2) {
      double b = Math.PI / 2 - FastMath.acos(ray.d.dot(sv)) + WIDTH;
      if (b >= 0 && b < WIDTH2) {
        texture.getColor(a / WIDTH2, b / WIDTH2, ray.color);
        ray.color.x *= apparentTextureBrightness.x * 10;
        ray.color.y *= apparentTextureBrightness.y * 10;
        ray.color.z *= apparentTextureBrightness.z * 10;
        return true;
      }
    }

    return false;
  }

  /**
   * Used with <code>SSS: OFF</code> and <code>SSS: HIGH_QUALITY</code>.
   */
  public boolean intersectDiffuse(Ray ray) {
    if (ray.d.dot(sw) < .5) {
      return false;
    }

    double WIDTH = RADIUS * 4;
    double WIDTH2 = WIDTH * 2;
    double a;
    a = Math.PI / 2 - FastMath.acos(ray.d.dot(su)) + WIDTH;
    if (a >= 0 && a < WIDTH2) {
      double b = Math.PI / 2 - FastMath.acos(ray.d.dot(sv)) + WIDTH;
      if (b >= 0 && b < WIDTH2) {
        texture.getColor(a / WIDTH2, b / WIDTH2, ray.color);
        ray.color.x *= color.x * 10;
        ray.color.y *= color.y * 10;
        ray.color.z *= color.z * 10;
        return true;
      }
    }

    return false;
  }

  /**
   * Calculate flat shading for ray.
   */
  public void flatShading(Ray ray) {
    Vector3 n = ray.getNormal();
    double shading = n.x * sw.x + n.y * sw.y + n.z * sw.z;
    shading = QuickMath.max(AMBIENT, shading);
    ray.color.x *= emittance.x * shading;
    ray.color.y *= emittance.y * shading;
    ray.color.z *= emittance.z * shading;
  }

  public void setColor(Vector3 newColor) {
    this.color.set(newColor);
    initSun();
    scene.refresh();
  }

  private void updateSkylightValues() {
    double sunTheta = Math.PI / 2 - altitude;
    double cosTheta = FastMath.cos(sunTheta);
    double cos2Theta = cosTheta * cosTheta;
    double chi = (4.0 / 9.0 - turb / 120.0) * (Math.PI - 2 * sunTheta);
    zenith_Y = (4.0453 * turb - 4.9710) * Math.tan(chi) - 0.2155 * turb + 2.4192;
    zenith_Y = (zenith_Y < 0) ? -zenith_Y : zenith_Y;
    zenith_x = chroma(turb, turb2, sunTheta, xZenithChroma);
    zenith_y = chroma(turb, turb2, sunTheta, yZenithChroma);
    f0_x = 1 / perezF(1, sunTheta, cos2Theta, A.x, B.x, C.x, D.x, E.x);
    f0_y = 1 / perezF(1, sunTheta, cos2Theta, A.y, B.y, C.y, D.y, E.y);
    f0_Y = 1 / perezF(1, sunTheta, cos2Theta, A.z, B.z, C.z, D.z, E.z);
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

  public void setLuminosity(double value) {
    luminosity = value;
    luminosityPdf = 1 / value;
    scene.refresh();
  }

  public double getLuminosity() {
    return luminosity;
  }

  public double getLuminosityPdf() {
    return luminosityPdf;
  }

  public void setApparentBrightness(double value) {
    apparentBrightness = value;
    initSun();
    scene.refresh();
  }

  public double getApparentBrightness() {
    return apparentBrightness;
  }

  public void setEnableTextureModification(boolean value) {
    enableTextureModification = value;
    initSun();
    scene.refresh();
  }

  public boolean getEnableTextureModification() {
    return enableTextureModification;
  }

  /**
   * Point ray in random direction within sun solid angle
   */
  public void getRandomSunDirection(Ray reflected, Random random) {
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double cos_a = 1 - x1 + x1 * RADIUS_COS;
    double sin_a = FastMath.sqrt(1 - cos_a * cos_a);
    double phi = 2 * Math.PI * x2;

    Vector3 u = new Vector3(su);
    Vector3 v = new Vector3(sv);
    Vector3 w = new Vector3(sw);

    u.scale(FastMath.cos(phi) * sin_a);
    v.scale(FastMath.sin(phi) * sin_a);
    w.scale(cos_a);

    reflected.d.add(u, v);
    reflected.d.add(w);
    reflected.d.normalize();
  }

  @Override public JsonObject toJson() {
    JsonObject sun = new JsonObject();
    sun.add("altitude", altitude);
    sun.add("azimuth", azimuth);
    sun.add("intensity", intensity);
    sun.add("luminosity", luminosity);
    sun.add("apparentBrightness", apparentBrightness);
    sun.add("modifySunTexture", enableTextureModification);
    JsonObject colorObj = new JsonObject();
    colorObj.add("red", color.x);
    colorObj.add("green", color.y);
    colorObj.add("blue", color.z);
    sun.add("color", colorObj);
    sun.add("drawTexture", drawTexture);
    return sun;
  }

  public void importFromJson(JsonObject json) {
    azimuth = json.get("azimuth").doubleValue(azimuth);
    altitude = json.get("altitude").doubleValue(altitude);
    intensity = json.get("intensity").doubleValue(intensity);
    setLuminosity(json.get("luminosity").doubleValue(luminosity));
    apparentBrightness = json.get("apparentBrightness").doubleValue(apparentBrightness);
    enableTextureModification = json.get("modifySunTexture").boolValue(enableTextureModification);

    if (json.get("color").isObject()) {
      JsonObject colorObj = json.get("color").object();
      color.x = colorObj.get("red").doubleValue(1);
      color.y = colorObj.get("green").doubleValue(1);
      color.z = colorObj.get("blue").doubleValue(1);
    }

    drawTexture = json.get("drawTexture").boolValue(drawTexture);

    initSun();
  }

  /**
   * @return sun color
   */
  public Vector3 getColor() {
    return new Vector3(color);
  }

  public void setDrawTexture(boolean value) {
    if (value != drawTexture) {
      drawTexture = value;
      scene.refresh();
    }
  }

  public boolean drawTexture() {
    return drawTexture;
  }
}
