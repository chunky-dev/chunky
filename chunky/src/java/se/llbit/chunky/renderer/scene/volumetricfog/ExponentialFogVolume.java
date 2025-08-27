package se.llbit.chunky.renderer.scene.volumetricfog;

import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.FastMath;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;

import java.util.Random;
import se.llbit.math.Vector3i;

public class ExponentialFogVolume extends ContinuousFogVolume {
  public static final double DEFAULT_SCALE_HEIGHT = 20;
  public static final double DEFAULT_Y_OFFSET = 0;

  private double scaleHeight = DEFAULT_SCALE_HEIGHT;
  private double yOffset = DEFAULT_Y_OFFSET;

  private boolean useUpperBounds = false;
  private boolean useLowerBounds = false;
  private double upperBounds = 82;
  private double lowerBounds = 42;

  @Override
  public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    double actualLowerBounds = (useLowerBounds) ? lowerBounds : Double.NEGATIVE_INFINITY;
    double actualUpperBounds = (useUpperBounds) ? upperBounds : Double.POSITIVE_INFINITY;

    Vector3i origin = scene.getOrigin();

    double tLower = (actualLowerBounds - ray.o.y - origin.y) / ray.d.y;
    double tUpper = (actualUpperBounds - ray.o.y - origin.y) / ray.d.y;

    double distance;
    double distanceLimit;

    if (ray.o.y < actualLowerBounds - origin.y) {
      if (tLower < 0) {
        return false;
      }
      distance = tLower;
      distanceLimit = tUpper;
    } else if (ray.o.y < actualUpperBounds - origin.y) {
      distance = 0;
      distanceLimit = (ray.d.y != 0) ? Math.max(tLower, tUpper) : Double.POSITIVE_INFINITY;
    } else {
      if (tUpper < 0) {
        return false;
      }
      distance = tUpper;
      distanceLimit = tLower;
    }
    if (distance > intersectionRecord.distance) {
      return false;
    }
    Vector3 o = new Vector3(ray.o);
    o.scaleAdd(distance, ray.d);
    for (int i = 0; i < this.noiseConfig.marchSteps; i++) {
      // Amount of fog the ray should pass through before being scattered
      // Sampled from an exponential distribution
      double fogPenetrated = -FastMath.log(1 - random.nextDouble());
      double expHeightDiff = fogPenetrated * ray.d.y / (scaleHeight * this.material.volumeDensity);
      double expYfHs = FastMath.exp(-(o.y + origin.y - yOffset) / scaleHeight) - expHeightDiff;
      if (expYfHs <= 0) {
        // The ray does not encounter enough fog to be scattered - no intersection.
        return false;
      }
      double yf = -FastMath.log(expYfHs) * scaleHeight + yOffset;
      double dist = (yf - (o.y + origin.y)) / ray.d.y;
      if (dist + distance > intersectionRecord.distance || dist + distance > distanceLimit) {
        // The ray would have encountered enough fog to be scattered, but something is in the way.
        return false;
      }

      if (noiseConfig.useNoise) {
        o.scaleAdd(dist, ray.d);
        float noiseValue = noiseConfig.calculate((float) o.x, (float) o.y, (float) o.z);
        if (noiseConfig.cutoff) {
          if (noiseValue < noiseConfig.lowerThreshold || noiseValue > noiseConfig.upperThreshold) {
            distance += dist;
            continue;
          }
        } else {
          if (random.nextDouble(noiseConfig.lowerThreshold, noiseConfig.upperThreshold + Constants.EPSILON) > noiseValue) {
            distance += dist;
            continue;
          }
        }
      }
      intersectionRecord.distance = dist + distance;
      intersectionRecord.material = this.material;
      intersectionRecord.color.set(material.volumeColor.x, material.volumeColor.y, material.volumeColor.z, 1);
      intersectionRecord.flags |= IntersectionRecord.VOLUME_INTERSECT;
      return true;
    }
    return false;
  }

  @Override
  public FogVolumeShape getShape() {
    return FogVolumeShape.EXPONENTIAL;
  }

  @Override
  public JsonObject saveVolumeSpecificConfiguration() {
    JsonObject json = new JsonObject();
    json.add("scaleHeight", scaleHeight);
    json.add("yOffset", yOffset);
    json.add("upperBounds", upperBounds);
    json.add("lowerBounds", lowerBounds);
    json.add("useUpperBounds", useUpperBounds);
    json.add("useLowerBounds", useLowerBounds);
    return json;
  }

  @Override
  public void loadVolumeSpecificConfiguration(JsonObject json) {
    scaleHeight = json.get("scaleHeight").doubleValue(scaleHeight);
    yOffset = json.get("yOffset").doubleValue(yOffset);
    upperBounds = json.get("upperBounds").doubleValue(upperBounds);
    lowerBounds = json.get("lowerBounds").doubleValue(lowerBounds);
    useUpperBounds = json.get("useUpperBounds").boolValue(useUpperBounds);
    useLowerBounds = json.get("useLowerBounds").boolValue(useLowerBounds);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    DoubleAdjuster scaleHeight = new DoubleAdjuster();
    scaleHeight.setName("Height scale");
    scaleHeight.setTooltip("Scales the vertical distribution of the fog");
    scaleHeight.setRange(1, 50);
    scaleHeight.set(this.scaleHeight);
    scaleHeight.onValueChange(value -> {
      this.scaleHeight = value;
      scene.refresh();
    });

    DoubleAdjuster yOffset = new DoubleAdjuster();
    yOffset.setName("Y-offset");
    yOffset.setTooltip("Y-offset (altitude) of the distribution");
    yOffset.setRange(-100, 100);
    yOffset.set(this.yOffset);
    yOffset.onValueChange(value -> {
      this.yOffset = value;
      scene.refresh();
    });

    ToggleSwitch useLowerBoundsSwitch = new ToggleSwitch("Use lower bound");
    useLowerBoundsSwitch.setSelected(this.useLowerBounds);
    useLowerBoundsSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.useLowerBounds = newValue;
      scene.refresh();
    });

    ToggleSwitch useUpperBoundsSwitch = new ToggleSwitch("Use upper bound");
    useUpperBoundsSwitch.setSelected(this.useUpperBounds);
    useUpperBoundsSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.useUpperBounds = newValue;
      scene.refresh();
    });

    DoubleAdjuster lowerBoundsAdjuster = new DoubleAdjuster();
    DoubleAdjuster upperBoundsAdjuster = new DoubleAdjuster();

    lowerBoundsAdjuster.setName("Lower bound");
    lowerBoundsAdjuster.setRange(-64, 320);
    lowerBoundsAdjuster.set(this.lowerBounds);
    lowerBoundsAdjuster.onValueChange(value -> {
      if (value < upperBoundsAdjuster.get()) {
        lowerBoundsAdjuster.setInvalid(false);
        upperBoundsAdjuster.setInvalid(false);
        this.lowerBounds = value;
        this.upperBounds = upperBoundsAdjuster.get();
        scene.refresh();
      } else {
        lowerBoundsAdjuster.setInvalid(true);
        upperBoundsAdjuster.setInvalid(true);
      }
    });

    upperBoundsAdjuster.setName("Upper bound");
    upperBoundsAdjuster.setRange(-64, 320);
    upperBoundsAdjuster.set(this.upperBounds);
    upperBoundsAdjuster.onValueChange(value -> {
      if (value > lowerBoundsAdjuster.get()) {
        lowerBoundsAdjuster.setInvalid(false);
        upperBoundsAdjuster.setInvalid(false);
        this.upperBounds = value;
        this.lowerBounds = lowerBoundsAdjuster.get();
        scene.refresh();
      } else {
        lowerBoundsAdjuster.setInvalid(true);
        upperBoundsAdjuster.setInvalid(true);
      }
    });

    VBox noiseControls = this.noiseConfig.getControls(parent);

    return new VBox(6, scaleHeight, yOffset, lowerBoundsAdjuster, upperBoundsAdjuster, useLowerBoundsSwitch, useUpperBoundsSwitch, new Separator(), noiseControls);
  }
}
