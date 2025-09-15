package se.llbit.chunky.renderer.scene.volumetricfog;

import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.scene.SimplexNoiseConfig;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Intersectable;
import se.llbit.util.Configurable;
import se.llbit.util.HasControls;

/**
 * A volume of volumetric fog.
 */
public abstract class FogVolume implements HasControls, Configurable, Intersectable {

  /**
   * Custom Simplex Noise Config for volumetric fog.
   */
  public static class NoiseConfig extends SimplexNoiseConfig {
    public boolean useNoise = false;
    public int marchSteps = 10;
    public double lowerThreshold = -10;
    public double upperThreshold = -0.5;
    public boolean cutoff = true;

    @Override
    public JsonObject toJson() {
      JsonObject json = super.toJson();
      json.add("useNoise", useNoise);
      json.add("marchSteps", marchSteps);
      json.add("lowerThreshold", lowerThreshold);
      json.add("upperThreshold", upperThreshold);
      json.add("cutoff", cutoff);
      return json;
    }

    @Override
    public void fromJson(JsonObject json) {
      super.fromJson(json);
      useNoise = json.get("useNoise").boolValue(useNoise);
      marchSteps = json.get("marchSteps").intValue(marchSteps);
      lowerThreshold = json.get("lowerThreshold").doubleValue(lowerThreshold);
      upperThreshold = json.get("upperThreshold").doubleValue(upperThreshold);
      cutoff = json.get("cutoff").boolValue(cutoff);
    }

    @Override
    public VBox getControls(RenderControlsTab parent) {
      Scene scene = parent.getChunkyScene();

      ToggleSwitch useNoiseSwitch = new ToggleSwitch("Use noise (experimental)");
      IntegerAdjuster marchStepsAdjuster = new IntegerAdjuster();
      DoubleAdjuster lowerThresholdAdjuster = new DoubleAdjuster();
      DoubleAdjuster upperThresholdAdjuster = new DoubleAdjuster();
      ToggleSwitch cutoffSwitch = new ToggleSwitch("Cutoff");
      VBox noiseControls = super.getControls(parent);
      noiseControls.setDisable(!this.useNoise);

      useNoiseSwitch.setSelected(this.useNoise);
      useNoiseSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
        this.useNoise = newValue;
        noiseControls.setDisable(!newValue);
        marchStepsAdjuster.setDisable(!newValue);
        lowerThresholdAdjuster.setDisable(!newValue);
        upperThresholdAdjuster.setDisable(!newValue);
        cutoffSwitch.setDisable(!newValue);
        scene.refresh();
      });

      marchStepsAdjuster.setName("March steps");
      marchStepsAdjuster.setRange(1, 100);
      marchStepsAdjuster.clampMin();
      marchStepsAdjuster.set(this.marchSteps);
      marchStepsAdjuster.onValueChange(value -> {
        this.marchSteps = value;
        scene.refresh();
      });
      marchStepsAdjuster.setDisable(!useNoise);

      lowerThresholdAdjuster.setName("Lower threshold");
      lowerThresholdAdjuster.setRange(-10, 10);
      lowerThresholdAdjuster.set(this.lowerThreshold);
      lowerThresholdAdjuster.onValueChange(value -> {
        if (value < upperThresholdAdjuster.get()) {
          lowerThresholdAdjuster.setInvalid(false);
          upperThresholdAdjuster.setInvalid(false);
          this.lowerThreshold = value;
          this.upperThreshold = upperThresholdAdjuster.get();
          scene.refresh();
        } else {
          lowerThresholdAdjuster.setInvalid(true);
          upperThresholdAdjuster.setInvalid(true);
        }
      });
      lowerThresholdAdjuster.setDisable(!this.useNoise);

      upperThresholdAdjuster.setName("Upper threshold");
      upperThresholdAdjuster.setRange(-10, 10);
      upperThresholdAdjuster.set(this.upperThreshold);
      upperThresholdAdjuster.onValueChange(value -> {
        if (value > lowerThresholdAdjuster.get()) {
          lowerThresholdAdjuster.setInvalid(false);
          upperThresholdAdjuster.setInvalid(false);
          this.upperThreshold = value;
          this.lowerThreshold = lowerThresholdAdjuster.get();
          scene.refresh();
        } else {
          lowerThresholdAdjuster.setInvalid(true);
          upperThresholdAdjuster.setInvalid(true);
        }
      });
      upperThresholdAdjuster.setDisable(!this.useNoise);

      cutoffSwitch.setSelected(this.cutoff);
      cutoffSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
        this.cutoff = newValue;
        scene.refresh();
      });
      cutoffSwitch.setDisable(!useNoise);

      return new VBox(6, useNoiseSwitch, noiseControls, marchStepsAdjuster, lowerThresholdAdjuster, upperThresholdAdjuster, cutoffSwitch);
    }
  }

  protected final Material material = new TextureMaterial(Texture.EMPTY_TEXTURE);
  protected final NoiseConfig noiseConfig = new NoiseConfig();

  public abstract boolean isDiscrete();

  public abstract FogVolumeShape getShape();

  public Material getMaterial() {
    return this.material;
  }

  protected abstract JsonObject saveVolumeSpecificConfiguration();

  protected abstract void loadVolumeSpecificConfiguration(JsonObject json);

  protected JsonObject saveMaterialProperties() {
    JsonObject json = new JsonObject();
    json.add("density", material.volumeDensity);
    json.add("anisotropy", material.volumeAnisotropy);
    json.add("emittance", material.volumeEmittance);
    json.add("color", ColorUtil.rgbToJson(material.volumeColor));
    return json;
  }

  protected void loadMaterialProperties(JsonObject json) {
    material.volumeDensity = json.get("density").floatValue(material.volumeDensity);
    material.volumeAnisotropy = json.get("anisotropy").floatValue(material.volumeAnisotropy);
    material.volumeEmittance = json.get("emittance").floatValue(material.volumeEmittance);
    material.volumeColor.set(ColorUtil.jsonToRGB(json.get("color").asObject(), material.volumeColor));
  }

  @Override
  public void fromJson(JsonObject json) {
    loadVolumeSpecificConfiguration(json.get("volumeSpecificConfiguration").asObject());
    loadMaterialProperties(json.get("materialProperties").asObject());
    noiseConfig.fromJson(json.get("noiseProperties").asObject());
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("shape", getShape().name());
    json.add("volumeSpecificConfiguration", saveVolumeSpecificConfiguration());
    json.add("materialProperties", saveMaterialProperties());
    json.add("noiseProperties", noiseConfig.toJson());
    return json;
  }

  @Override
  public void reset() {
  }
}
