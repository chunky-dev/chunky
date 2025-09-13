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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.FastMath;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.math.*;

import static java.lang.Math.PI;

/**
 * Nishita sky model based on the code presented in <a href="https://www.scratchapixel.com/lessons/procedural-generation-virtual-worlds/simulating-sky/simulating-colors-of-the-sky">Scratchapixel 2.0</a>.
 */
public class NishitaSky implements SimulatedSky {
  // Atmospheric constants
  private static final double DEFAULT_EARTH_RADIUS = 6360e3;
  private static final double DEFAULT_ATM_THICKNESS = 60e3;

  private static final int DEFAULT_HORIZON_OFFSET = 0;
  private static final int DEFAULT_ALTITUDE = 1;

  private double earthRadius = DEFAULT_EARTH_RADIUS;
  private double atmosphereThickness = DEFAULT_ATM_THICKNESS;

  private static final double DEFAULT_RAYLEIGH_SCALE = 7.994e3;
  private static final double DEFAULT_MIE_SCALE = 1.2e3;
  private static final double DEFAULT_OZONE_SCALE = 10000;

  private double rayleighScale = DEFAULT_RAYLEIGH_SCALE;
  private double mieScale = DEFAULT_MIE_SCALE;
  private double ozoneScale = DEFAULT_OZONE_SCALE;

  private static final int DEFAULT_ANISOTROPY = 1;
  private static final double DEFAULT_OZONE_DENSITY = 0.00000022512612292678;

  private static final Vector3 DEFAULT_BETA_R = new Vector3(5.8e-6, 13.0e-6, 22.4e-6);
  private static final Vector3 DEFAULT_BETA_M = new Vector3(21e-6);
  private static final Vector3 DEFAULT_BETA_O = new Vector3(3.808e-6);

  private final Vector3 betaR = new Vector3(DEFAULT_BETA_R);
  private final Vector3 betaM = new Vector3(DEFAULT_BETA_M);
  private final Vector3 betaO = new Vector3(DEFAULT_BETA_O);

  private static final int DEFAULT_VIEW_SAMPLES = 16;
  private static final int DEFAULT_LIGHT_SAMPLES = 8;

  private int viewSamples = DEFAULT_VIEW_SAMPLES;
  private int lightSamples = DEFAULT_LIGHT_SAMPLES;

  private static final double MIN_ANISOTROPY = 0.1;
  private static final double MAX_ANISOTROPY = 1.315;

  // Sun position vector. Final to prevent unnecessary reallocation
  private final Vector3 sunPosition = new Vector3(0, 1, 0);
  private double sunIntensity = 1;
  private double horizonOffset = DEFAULT_HORIZON_OFFSET;

  // Sun position in spherical form for faster update checking
  private double theta;
  private double phi;
  private double altitude = DEFAULT_ALTITUDE;
  private double ozoneDensity = DEFAULT_OZONE_DENSITY;
  private double anisotropy = DEFAULT_ANISOTROPY;

  private boolean useToneMap = true;

  /**
   * Create a new sky renderer.
   */
  public NishitaSky() {
  }

  @Override
  public boolean updateSun(Sun sun) {
    if (sunIntensity != sun.getIntensity() || theta != sun.getAzimuth() || phi != sun.getAltitude()) {
      theta = sun.getAzimuth();
      phi = sun.getAltitude();
      double r = QuickMath.abs(FastMath.cos(phi));
      sunPosition.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);
      sunIntensity = sun.getIntensity() * sun.radius * sun.radius;

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
  public Vector3 calcIncidentLight(Ray ray) {
    // Render from just above the surface of "earth"
    Vector3 origin = new Vector3(0, ray.o.y + earthRadius + altitude, 0);
    Vector3 direction = ray.d;
    direction.y += horizonOffset;
    direction.normalize();

    // Calculate the distance from the origin to the edge of the atmosphere
    double distance = sphereIntersect(origin, direction, earthRadius + atmosphereThickness);
    if (distance == -1) {
      // No intersection, black
      return new Vector3(0, 0, 0);
    }

    // Ray march segment length
    double segmentLength = distance / viewSamples;
    double currentDist = 0;

    double optDepthR = 0;
    double optDepthM = 0;
    double optDepthO = 0;

    double mu = direction.dot(sunPosition);
    double mu2 = mu * mu;
    double phaseR = (3 / (16 * PI)) * (1 + mu2);
    double g = 0.76 * QuickMath.clamp(anisotropy, MIN_ANISOTROPY, MAX_ANISOTROPY);
    double g2 = g * g;
    double phaseM = 3 / (8 * PI) * ((1 - g2) * (1 + mu * mu)) / ((2 + g2) * FastMath.pow(1 + g2 - 2 * g * mu, 1.5));

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
    for (int i = 0; i < viewSamples; i++) {
      samplePosition.set(
        origin.x + (currentDist + segmentLength / 2) * direction.x,
        origin.y + (currentDist + segmentLength / 2) * direction.y,
        origin.z + (currentDist + segmentLength / 2) * direction.z
      );
      height = samplePosition.length() - earthRadius;

      hr = FastMath.exp(-height / rayleighScale) * segmentLength;
      hm = FastMath.exp(-height / mieScale) * segmentLength;
      ho = FastMath.exp(-height / ozoneScale) * segmentLength;
      optDepthR += hr;
      optDepthM += hm;
      optDepthO += ho;

      // Calculate the distance from the current point to the atmosphere in the direction of the sun
      sunLength = sphereIntersect(samplePosition, sunPosition, earthRadius + atmosphereThickness);
      sunSegment = sunLength / lightSamples;
      sunCurrent = 0;

      optDepthSunR = 0;
      optDepthSunM = 0;
      optDepthSunO = 0;

      // Ray march towards the sun
      boolean flag = false;
      for (int j = 0; j < lightSamples; j++) {
        sunSamplePosition.set(
          samplePosition.x + (sunCurrent + sunSegment / 2) * sunPosition.x,
          samplePosition.y + (sunCurrent + sunSegment / 2) * sunPosition.y,
          samplePosition.z + (sunCurrent + sunSegment / 2) * sunPosition.z
        );
        sunHeight = sunSamplePosition.length() - earthRadius;
        if (sunHeight < 0) {
          flag = true;
          break;
        }

        optDepthSunR += FastMath.exp(-sunHeight / rayleighScale) * sunSegment;
        optDepthSunM += FastMath.exp(-sunHeight / mieScale) * sunSegment;
        optDepthSunO += FastMath.exp(-sunHeight / ozoneScale) * sunSegment;

        sunCurrent += sunSegment;
      }

      // Only execute if we successfully march out of the atmosphere
      if (!flag) {
        tau.set(
          betaR.x * (optDepthR + optDepthSunR) + betaM.x * 1.1 * (optDepthM + optDepthSunM) + betaO.x * (optDepthO + optDepthSunO),
          betaR.y * (optDepthR + optDepthSunR) + betaM.y * 1.1 * (optDepthM + optDepthSunM) + betaO.y * (optDepthO + optDepthSunO),
          betaR.z * (optDepthR + optDepthSunR) + betaM.z * 1.1 * (optDepthM + optDepthSunM) + betaO.z * (optDepthO + optDepthSunO)
        );

        attenuation.set(
          FastMath.exp(-tau.x),
          FastMath.exp(-tau.y),
          FastMath.exp(-tau.z)
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

        sumO.add(
          attenuation.x * ho,
          attenuation.y * ho,
          attenuation.z * ho
        );
      }

      currentDist += segmentLength;
    }

    Vector3 color = new Vector3(
      ((sumR.x * betaR.x * phaseR) + (sumM.x * betaM.x * phaseM) + (sumO.x * betaO.x * oD)),
      ((sumR.y * betaR.y * phaseR) + (sumM.y * betaM.y * phaseM) + (sumO.y * betaO.y * oD)),
      ((sumR.z * betaR.z * phaseR) + (sumM.z * betaM.z * phaseM) + (sumO.z * betaO.z * oD))
    );

    double scale = sunIntensity * 10;
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
  public void fromJson(JsonObject json) {
    betaR.fromJson(json.get("betaR").asObject());
    rayleighScale = json.get("rayleighScale").doubleValue(DEFAULT_RAYLEIGH_SCALE);
    betaM.fromJson(json.get("betaM").asObject());
    mieScale = json.get("mieScale").doubleValue(DEFAULT_MIE_SCALE);
    anisotropy = json.get("anisotropy").doubleValue(DEFAULT_ANISOTROPY);
    betaO.fromJson(json.get("betaO").asObject());
    ozoneScale = json.get("ozoneScale").doubleValue(DEFAULT_OZONE_SCALE);
    ozoneDensity = json.get("ozoneDensity").doubleValue(DEFAULT_OZONE_DENSITY);
    altitude = json.get("altitude").doubleValue(DEFAULT_ALTITUDE);
    horizonOffset = json.get("horizonOffset").doubleValue(DEFAULT_HORIZON_OFFSET);
    earthRadius = json.get("earthRadius").doubleValue(DEFAULT_EARTH_RADIUS);
    atmosphereThickness = json.get("atmosphereThickness").doubleValue(DEFAULT_ATM_THICKNESS);
    viewSamples = json.get("viewSamples").intValue(DEFAULT_VIEW_SAMPLES);
    lightSamples = json.get("lightSamples").intValue(DEFAULT_LIGHT_SAMPLES);
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("betaR", betaR.toJson());
    json.add("rayleighScale", rayleighScale);
    json.add("betaM", betaM.toJson());
    json.add("mieScale", mieScale);
    json.add("anisotropy", anisotropy);
    json.add("betaO", betaO.toJson());
    json.add("ozoneScale", ozoneScale);
    json.add("ozoneDensity", ozoneDensity);
    json.add("altitude", altitude);
    json.add("horizonOffset", horizonOffset);
    json.add("earthRadius", earthRadius);
    json.add("atmosphereThickness", atmosphereThickness);
    json.add("viewSamples", viewSamples);
    json.add("lightSamples", lightSamples);
    return json;
  }

  @Override
  public void reset() {
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    VBox controls = new VBox(6);

    ColumnConstraints labelConstraints = new ColumnConstraints();
    labelConstraints.setHgrow(Priority.NEVER);
    labelConstraints.setPrefWidth(90);
    ColumnConstraints posFieldConstraints = new ColumnConstraints();
    posFieldConstraints.setMinWidth(20);
    posFieldConstraints.setPrefWidth(90);

    // --------

    DoubleTextField betaRX = new DoubleTextField();
    DoubleTextField betaRY = new DoubleTextField();
    DoubleTextField betaRZ = new DoubleTextField();

    betaRX.setMaximumFractionDigits(12);
    betaRY.setMaximumFractionDigits(12);
    betaRZ.setMaximumFractionDigits(12);

    betaRX.valueProperty().setValue(betaR.x);
    betaRY.valueProperty().setValue(betaR.y);
    betaRZ.valueProperty().setValue(betaR.z);

    betaRX.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaR.x = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    betaRY.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaR.y = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    betaRZ.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaR.z = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    DoubleAdjuster rayleighScaleAdjuster = new DoubleAdjuster();
    rayleighScaleAdjuster.setName("Scale height");
    rayleighScaleAdjuster.setRange(1.0, atmosphereThickness);
    rayleighScaleAdjuster.clampBoth();
    rayleighScaleAdjuster.set(rayleighScale);
    rayleighScaleAdjuster.onValueChange(value -> {
      rayleighScale = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    Button rayleighDefaultsButton = new Button("Defaults");
    rayleighDefaultsButton.setOnAction(e -> {
      betaR.set(DEFAULT_BETA_R);
      betaRX.valueProperty().setValue(betaR.x);
      betaRY.valueProperty().setValue(betaR.y);
      betaRZ.valueProperty().setValue(betaR.z);

      rayleighScale = DEFAULT_RAYLEIGH_SCALE;
      rayleighScaleAdjuster.valueProperty().setValue(rayleighScale);

      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    GridPane rayleighPane = new GridPane();
    rayleighPane.getColumnConstraints().addAll(labelConstraints, posFieldConstraints, posFieldConstraints, posFieldConstraints);
    rayleighPane.setHgap(6);
    rayleighPane.addRow(0, new Label("Coefficient"), betaRX, betaRY, betaRZ);
    controls.getChildren().addAll(new HBox(6, new Label("Rayleigh scattering"), rayleighDefaultsButton), rayleighPane, rayleighScaleAdjuster, new Separator());

    // --------

    DoubleTextField betaMX = new DoubleTextField();
    DoubleTextField betaMY = new DoubleTextField();
    DoubleTextField betaMZ = new DoubleTextField();

    betaMX.setMaximumFractionDigits(12);
    betaMY.setMaximumFractionDigits(12);
    betaMZ.setMaximumFractionDigits(12);

    betaMX.valueProperty().setValue(betaM.x);
    betaMY.valueProperty().setValue(betaM.y);
    betaMZ.valueProperty().setValue(betaM.z);

    betaMX.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaM.x = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    betaMY.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaM.y = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    betaMZ.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaM.z = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    DoubleAdjuster mieScaleAdjuster = new DoubleAdjuster();
    mieScaleAdjuster.setName("Scale height");
    mieScaleAdjuster.setRange(1.0, atmosphereThickness);
    mieScaleAdjuster.clampBoth();
    mieScaleAdjuster.set(mieScale);
    mieScaleAdjuster.onValueChange(value -> {
      mieScale = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    DoubleAdjuster anisotropyAdjuster = new DoubleAdjuster();
    anisotropyAdjuster.setName("Anisotropy");
    anisotropyAdjuster.setRange(MIN_ANISOTROPY, MAX_ANISOTROPY);
    anisotropyAdjuster.clampBoth();
    anisotropyAdjuster.set(this.anisotropy);
    anisotropyAdjuster.onValueChange(value -> {
      this.anisotropy = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    Button mieDefaultsButton = new Button("Defaults");
    mieDefaultsButton.setOnAction(e -> {
      betaM.set(DEFAULT_BETA_M);
      betaMX.valueProperty().setValue(betaM.x);
      betaMY.valueProperty().setValue(betaM.y);
      betaMZ.valueProperty().setValue(betaM.z);

      anisotropy = 1;
      anisotropyAdjuster.valueProperty().setValue(anisotropy);
      mieScale = DEFAULT_MIE_SCALE;
      mieScaleAdjuster.valueProperty().setValue(mieScale);

      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    GridPane miePane = new GridPane();
    miePane.getColumnConstraints().addAll(labelConstraints, posFieldConstraints, posFieldConstraints, posFieldConstraints);
    miePane.setHgap(6);
    miePane.addRow(0, new Label("Coefficient"), betaMX, betaMY, betaMZ);
    controls.getChildren().addAll(new HBox(6, new Label("Mie scattering"), mieDefaultsButton), miePane, anisotropyAdjuster, mieScaleAdjuster, new Separator());

    // --------

    DoubleTextField betaOX = new DoubleTextField();
    DoubleTextField betaOY = new DoubleTextField();
    DoubleTextField betaOZ = new DoubleTextField();

    betaOX.setMaximumFractionDigits(12);
    betaOY.setMaximumFractionDigits(12);
    betaOZ.setMaximumFractionDigits(12);

    betaOX.valueProperty().setValue(betaO.x);
    betaOY.valueProperty().setValue(betaO.y);
    betaOZ.valueProperty().setValue(betaO.z);

    betaOX.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaO.x = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    betaOY.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaO.y = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    betaOZ.valueProperty().addListener((observable, oldValue, newValue) -> {
      betaO.z = newValue.doubleValue();
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    DoubleAdjuster ozoneScaleAdjuster = new DoubleAdjuster();
    ozoneScaleAdjuster.setName("Scale height");
    ozoneScaleAdjuster.setRange(1.0, atmosphereThickness);
    ozoneScaleAdjuster.clampBoth();
    ozoneScaleAdjuster.set(ozoneScale);
    ozoneScaleAdjuster.onValueChange(value -> {
      ozoneScale = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    DoubleAdjuster ozoneDensityAdjuster = new DoubleAdjuster();
    ozoneDensityAdjuster.setName("Ozone density");
    ozoneDensityAdjuster.setTooltip("Density of atmosphere ozone.");
    ozoneDensityAdjuster.setRange(0, 2);
    ozoneDensityAdjuster.setMaximumFractionDigits(20);
    ozoneDensityAdjuster.clampBoth();
    ozoneDensityAdjuster.set(this.ozoneDensity);
    ozoneDensityAdjuster.onValueChange(value -> {
      this.ozoneDensity = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    Button ozoneDefaultsButton = new Button("Defaults");
    ozoneDefaultsButton.setOnAction(e -> {
      betaO.set(DEFAULT_BETA_O);
      betaOX.valueProperty().setValue(betaO.x);
      betaOY.valueProperty().setValue(betaO.y);
      betaOZ.valueProperty().setValue(betaO.z);

      ozoneDensity = DEFAULT_OZONE_DENSITY;
      ozoneDensityAdjuster.valueProperty().setValue(ozoneDensity);
      ozoneScale = DEFAULT_OZONE_SCALE;
      ozoneScaleAdjuster.valueProperty().setValue(ozoneScale);

      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });

    GridPane ozonePane = new GridPane();
    ozonePane.getColumnConstraints().addAll(labelConstraints, posFieldConstraints, posFieldConstraints, posFieldConstraints);
    ozonePane.setHgap(6);
    ozonePane.addRow(0, new Label("Coefficient"), betaOX, betaOY, betaOZ);
    controls.getChildren().addAll(new HBox(6, new Label("Ozone"), ozoneDefaultsButton), ozonePane, ozoneDensityAdjuster, ozoneScaleAdjuster, new Separator());

    // --------

    controls.getChildren().add(new Label("Other"));

    DoubleAdjuster altitudeAdjuster = new DoubleAdjuster();
    altitudeAdjuster.setName("Altitude");
    altitudeAdjuster.setTooltip("Altitude of the simulated camera above the surface of the earth, in meters.");
    altitudeAdjuster.setRange(0.001, 10000);
    altitudeAdjuster.clampMin();
    altitudeAdjuster.set(this.altitude);
    altitudeAdjuster.onValueChange(value -> {
      this.altitude = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(altitudeAdjuster);

    DoubleAdjuster horizonOffsetAdjuster = new DoubleAdjuster();
    horizonOffsetAdjuster.setName("Horizon offset");
    horizonOffsetAdjuster.setRange(0, 1);
    horizonOffsetAdjuster.clampBoth();
    horizonOffsetAdjuster.set(this.horizonOffset);
    horizonOffsetAdjuster.onValueChange(value -> {
      this.horizonOffset = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(horizonOffsetAdjuster);

    DoubleAdjuster earthRadiusAdjuster = new DoubleAdjuster();
    earthRadiusAdjuster.setName("Earth radius");
    earthRadiusAdjuster.setRange(1, 10000);
    earthRadiusAdjuster.clampBoth();
    earthRadiusAdjuster.set(this.earthRadius / 1000);
    earthRadiusAdjuster.onValueChange(value -> {
      this.earthRadius = value * 1000;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(earthRadiusAdjuster);

    DoubleAdjuster atmosphereThicknessAdjuster = new DoubleAdjuster();
    atmosphereThicknessAdjuster.setName("Atmosphere thickness");
    atmosphereThicknessAdjuster.setRange(1, 10000);
    atmosphereThicknessAdjuster.clampBoth();
    atmosphereThicknessAdjuster.set(this.atmosphereThickness / 1000);
    atmosphereThicknessAdjuster.onValueChange(value -> {
      this.atmosphereThickness = value * 1000;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(atmosphereThicknessAdjuster);

    IntegerAdjuster viewSamplesAdjuster = new IntegerAdjuster();
    viewSamplesAdjuster.setName("View samples");
    viewSamplesAdjuster.setRange(1, 64);
    viewSamplesAdjuster.clampBoth();
    viewSamplesAdjuster.set(this.viewSamples);
    viewSamplesAdjuster.onValueChange(value -> {
      this.viewSamples = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(viewSamplesAdjuster);

    IntegerAdjuster lightSamplesAdjuster = new IntegerAdjuster();
    lightSamplesAdjuster.setName("Light samples");
    lightSamplesAdjuster.setRange(1, 64);
    lightSamplesAdjuster.clampBoth();
    lightSamplesAdjuster.set(this.lightSamples);
    lightSamplesAdjuster.onValueChange(value -> {
      this.lightSamples = value;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(lightSamplesAdjuster);

    ToggleSwitch useToneMap = new ToggleSwitch("Use tone map");
    useToneMap.setSelected(this.useToneMap);
    useToneMap.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.useToneMap = newValue;
      scene.sky().updateSimulatedSky(scene.sun());
      scene.refresh();
    });
    controls.getChildren().add(useToneMap);

    return controls;
  }
}
