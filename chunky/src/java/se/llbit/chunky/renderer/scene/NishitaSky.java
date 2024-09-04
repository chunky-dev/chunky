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

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.JsonObject;
import se.llbit.math.*;

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
  private static final double OZONE_SCALE = 10000;

  private static final double OZONE_DENSITY = 0.00000022512612292678;

  private static final Vector3 BETA_R = new Vector3(5.8e-6, 13.0e-6, 22.4e-6);
  private static final Vector3 BETA_M = new Vector3(20e-6, 20e-6, 20e-6);
  private static final Vector3 BETA_O = new Vector3(3.808e-6);

  private static final int SAMPLES = 16;
  private static final int SAMPLES_LIGHT = 8;

  private static final double MIN_SCATTERING = 0.1;
  private static final double MAX_SCATTERING = 1.315;

  // Sun position vector. Final to prevent unnecessary reallocation
  private final Vector3 sunPosition = new Vector3(0, 1, 0);
  private double sunIntensity = 1;
  private double horizonOffset = 0;

  // Sun position in spherical form for faster update checking
  private double theta;
  private double phi;
  private double altitude = 1;
  private double ozoneDensity = 0;
  private double scattering = 1;
  private final Vector3 skyTint = new Vector3(1);
  private final Vector3 sunTint = new Vector3(1);
  private boolean useToneMap = false;

  /**
   * Create a new sky renderer.
   */
  public NishitaSky() {
  }

  @Override
  public boolean updateSun(Sun sun) {
    if (sunIntensity != sun.getIntensity() || theta != sun.getAzimuth() || phi != sun.getAltitude() || this.horizonOffset != horizonOffset) {
      theta = sun.getAzimuth();
      phi = sun.getAltitude();
      double r = QuickMath.abs(FastMath.cos(phi));
      sunPosition.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);
      sunIntensity = sun.getIntensity();

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
  public Vector3 calcIncidentLight(Ray2 ray) {
    // Render from just above the surface of "earth"
    Vector3 origin = new Vector3(0, ray.o.y + EARTH_RADIUS + altitude, 0);
    Vector3 direction = ray.d;
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
    double optDepthO = 0;

    double mu = direction.dot(sunPosition);
    double phaseR = (3 / (16 * PI)) * (1 + mu * mu);
    double g = 0.76 * QuickMath.clamp(scattering, MIN_SCATTERING, MAX_SCATTERING);
    double phaseM = 3 / (8 * PI) * ((1 - g * g) * (1 + mu * mu)) / ((2 + g * g) * FastMath.pow(1 + g * g - 2 * g * mu, 1.5));

    Vector3 sumR = new Vector3(0, 0, 0);
    Vector3 sumM = new Vector3(0, 0, 0);
    Vector3 sumO = new Vector3(0, 0, 0);

    double oD = 0;
    if (ozoneDensity > Constants.EPSILON) {
      oD = (ozoneDensity * 1000 / -15000);
      oD = QuickMath.clamp(oD, -2000, 3000);
    }

    // Primary sample values
    Vector3 samplePosition = new Vector3();
    double height, hr, hm, ho;

    // Sun sampling values
    Vector3 sunSamplePosition = new Vector3();
    double sunLength, sunSegment, sunCurrent, optDepthSunR, optDepthSunM, optDepthSunO, sunHeight;

    Vector3 tau = new Vector3();
    Vector3 attenuation = new Vector3();

    // Primary ray march out towards space
    for (int i = 0; i < SAMPLES; i++) {
      samplePosition.set(
        origin.x + (currentDist + segmentLength / 2) * direction.x,
        origin.y + (currentDist + segmentLength / 2) * direction.y,
        origin.z + (currentDist + segmentLength / 2) * direction.z
      );
      height = samplePosition.length() - EARTH_RADIUS;

      hr = FastMath.exp(-height / RAYLEIGH_SCALE) * segmentLength;
      hm = FastMath.exp(-height / MIE_SCALE) * segmentLength;
      ho = FastMath.exp(-height / OZONE_SCALE) * segmentLength;
      optDepthR += hr;
      optDepthM += hm;
      optDepthO += ho;

      // Calculate the distance from the current point to the atmosphere in the direction of the sun
      sunLength = sphereIntersect(samplePosition, sunPosition, EARTH_RADIUS + ATM_THICKNESS);
      sunSegment = sunLength / SAMPLES_LIGHT;
      sunCurrent = 0;

      optDepthSunR = 0;
      optDepthSunM = 0;
      optDepthSunO = 0;

      // Ray march towards the sun
      boolean flag = false;
      for (int j = 0; j < SAMPLES_LIGHT; j++) {
        sunSamplePosition.set(
          samplePosition.x + (sunCurrent + sunSegment / 2) * sunPosition.x,
          samplePosition.y + (sunCurrent + sunSegment / 2) * sunPosition.y,
          samplePosition.z + (sunCurrent + sunSegment / 2) * sunPosition.z
        );
        sunHeight = sunSamplePosition.length() - EARTH_RADIUS;
        if (sunHeight < 0) {
          flag = true;
          break;
        }

        optDepthSunR += FastMath.exp(-sunHeight / RAYLEIGH_SCALE) * sunSegment;
        optDepthSunM += FastMath.exp(-sunHeight / MIE_SCALE) * sunSegment;
        optDepthSunO += FastMath.exp(-sunHeight / OZONE_SCALE) * sunSegment;

        sunCurrent += sunSegment;
      }

      // Only execute if we successfully march out of the atmosphere
      if (!flag) {
        tau.set(
          BETA_R.x * (optDepthR + optDepthSunR) + BETA_M.x * 1.1 * (optDepthM + optDepthSunM) + BETA_O.x * (optDepthO + optDepthSunO),
          BETA_R.y * (optDepthR + optDepthSunR) + BETA_M.y * 1.1 * (optDepthM + optDepthSunM) + BETA_O.y * (optDepthO + optDepthSunO),
          BETA_R.z * (optDepthR + optDepthSunR) + BETA_M.z * 1.1 * (optDepthM + optDepthSunM) + BETA_O.z * (optDepthO + optDepthSunO)
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

        sumO.add(attenuation.x * ho, attenuation.y * ho, attenuation.z * ho);
      }

      currentDist += segmentLength;
    }

    Vector3 color = new Vector3(
      ((sumR.x * BETA_R.x * phaseR * skyTint.x) + (sumM.x * BETA_M.x * phaseM * sunTint.x) + (sumO.x * BETA_O.x * oD * sunTint.x)),
      ((sumR.y * BETA_R.y * phaseR * skyTint.y) + (sumM.y * BETA_M.y * phaseM * sunTint.y) + (sumO.y * BETA_O.y * oD * sunTint.y)),
      ((sumR.z * BETA_R.z * phaseR * skyTint.z) + (sumM.z * BETA_M.z * phaseM * sunTint.z) + (sumO.z * BETA_O.z * oD * sunTint.z))
    );

    double scale = sunIntensity / Sun.DEFAULT_INTENSITY * 30;
    color.scale(scale);

    // Tone-mapping function for more realistic colors
    if (useToneMap) {
      color.set(
        color.x < 1.413 && color.x >= 0 ? FastMath.pow(color.x * 0.38317, 1.0 / Scene.DEFAULT_GAMMA) : 1.0 - FastMath.exp(-color.x),
        color.y < 1.413 && color.y >= 0 ? FastMath.pow(color.y * 0.38317, 1.0 / Scene.DEFAULT_GAMMA) : 1.0 - FastMath.exp(-color.y),
        color.z < 1.413 && color.z >= 0 ? FastMath.pow(color.z * 0.38317, 1.0 / Scene.DEFAULT_GAMMA) : 1.0 - FastMath.exp(-color.z)
      );
    }

    return color;
  }

  /**
   * Calculate the distance from <code>origin</code> to the edge of a sphere centered at (0, 0, 0) in <code>direction</code>.
   */
  private double sphereIntersect(Vector3 origin, Vector3 direction, double sphere_radius) {
    double a = direction.lengthSquared();
    double b = 2 * direction.dot(origin);
    double c = origin.lengthSquared() - sphere_radius * sphere_radius;

    if (b == 0) {
      if (a == 0) {
        // No intersection
        return -1;
      }

      return FastMath.sqrt(-c / a);
    }

    double disc = b * b - 4 * a * c;

    if (disc < 0) {
      // No intersection
      return -1;
    }
    return (-b + FastMath.sqrt(disc)) / (2 * a);
  }

  @Override
  public void loadConfiguration(JsonObject json) {
    altitude = json.get("altitude").doubleValue(altitude);
    ozoneDensity = json.get("ozoneDensity").doubleValue(ozoneDensity);
    scattering = json.get("scattering").doubleValue(scattering);
    horizonOffset = json.get("horizonOffset").doubleValue(horizonOffset);
    skyTint.set(ColorUtil.jsonToRGB(json.get("skyTint").asObject()));
    sunTint.set(ColorUtil.jsonToRGB(json.get("sunTint").asObject()));
    useToneMap = json.get("useToneMap").boolValue(useToneMap);
  }

  @Override
  public void storeConfiguration(JsonObject json) {
    json.add("altitude", altitude);
    json.add("ozoneDensity", ozoneDensity);
    json.add("scattering", scattering);
    json.add("horizonOffset", horizonOffset);
    json.add("skyTint", ColorUtil.rgbToJson(skyTint));
    json.add("sunTint", ColorUtil.rgbToJson(sunTint));
    json.add("useToneMap", useToneMap);
  }

  @Override
  public void reset() {
  }

  @Override
  public VBox getControls(Node parent, Scene scene) {
    VBox controls = new VBox();

    DoubleAdjuster altitudeAdjuster = new DoubleAdjuster();
    altitudeAdjuster.setName("Altitude");
    altitudeAdjuster.setTooltip("Altitude of the simulated camera above the surface of the earth, in meters.");
    altitudeAdjuster.setRange(0.001, 10000);
    altitudeAdjuster.clampMin();
    altitudeAdjuster.set(this.altitude);
    altitudeAdjuster.onValueChange(value -> {
      this.altitude = value;
      scene.sky.updateSimulatedSky(scene.sun);
      scene.refresh();
    });
    controls.getChildren().add(altitudeAdjuster);

    DoubleAdjuster ozoneDensityAdjuster = new DoubleAdjuster();
    ozoneDensityAdjuster.setName("Ozone density");
    ozoneDensityAdjuster.setTooltip("Density of atmosphere ozone.");
    ozoneDensityAdjuster.setRange(0, 2);
    ozoneDensityAdjuster.clampBoth();
    ozoneDensityAdjuster.set(this.ozoneDensity);
    ozoneDensityAdjuster.onValueChange(value -> {
      this.ozoneDensity = value;
      scene.sky.updateSimulatedSky(scene.sun);
      scene.refresh();
    });
    controls.getChildren().add(ozoneDensityAdjuster);

    DoubleAdjuster scatteringAdjuster = new DoubleAdjuster();
    scatteringAdjuster.setName("Scattering amount");
    scatteringAdjuster.setRange(MIN_SCATTERING, MAX_SCATTERING);
    scatteringAdjuster.clampBoth();
    scatteringAdjuster.set(this.scattering);
    scatteringAdjuster.onValueChange(value -> {
      this.scattering = value;
      scene.sky.updateSimulatedSky(scene.sun);
      scene.refresh();
    });
    controls.getChildren().add(scatteringAdjuster);

    DoubleAdjuster horizonOffsetAdjuster = new DoubleAdjuster();
    horizonOffsetAdjuster.setName("Horizon offset");
    horizonOffsetAdjuster.setRange(0, 1);
    horizonOffsetAdjuster.clampBoth();
    horizonOffsetAdjuster.set(this.horizonOffset);
    horizonOffsetAdjuster.onValueChange(value -> {
      this.horizonOffset = value;
      scene.sky.updateSimulatedSky(scene.sun);
      scene.refresh();
    });
    controls.getChildren().add(horizonOffsetAdjuster);

    Label skyTintLabel = new Label("Sky tint:");
    skyTintLabel.setTooltip(new Tooltip("Set the tint of the sky."));
    LuxColorPicker skyTintPicker = new LuxColorPicker();
    skyTintPicker.setColor(ColorUtil.toFx(this.skyTint));
    skyTintPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        Vector3 color = ColorUtil.fromFx(newValue);
        this.skyTint.set(color);
        scene.sky.updateSimulatedSky(scene.sun);
        scene.refresh();
      })
    );
    HBox skyTintPickerBox = new HBox(10, skyTintLabel, skyTintPicker);
    controls.getChildren().add(skyTintPickerBox);

    Label sunTintLabel = new Label("Sun tint:");
    sunTintLabel.setTooltip(new Tooltip("Set the tint of the sun."));
    LuxColorPicker sunTintPicker = new LuxColorPicker();
    sunTintPicker.setColor(ColorUtil.toFx(this.sunTint));
    sunTintPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        Vector3 color = ColorUtil.fromFx(newValue);
        this.sunTint.set(color);
        scene.sky.updateSimulatedSky(scene.sun);
        scene.refresh();
      })
    );
    HBox sunTintPickerBox = new HBox(10, sunTintLabel, sunTintPicker);
    controls.getChildren().add(sunTintPickerBox);

    CheckBox useToneMapSetter = new CheckBox();
    useToneMapSetter.setText("Use tone map");
    useToneMapSetter.setTooltip(new Tooltip("Use a tone mapping filter on the sky."));
    useToneMapSetter.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.useToneMap = newValue;
      scene.sky.updateSimulatedSky(scene.sun);
      scene.refresh();
    });
    controls.getChildren().add(useToneMapSetter);

    controls.setSpacing(10);

    return controls;
  }
}
