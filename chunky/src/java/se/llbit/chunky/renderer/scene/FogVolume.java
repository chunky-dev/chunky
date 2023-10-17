package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.VolumeMaterial;
import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.util.JsonSerializable;

import java.util.Random;

public abstract class FogVolume implements JsonSerializable {
  protected FogVolumeType type;
  protected Vector3 color;
  protected double density;
  protected VolumeMaterial material = new ParticleFogMaterial();

  public abstract boolean intersect(Scene scene, Ray ray, Random random);

  public void setRandomNormal(Ray ray, Random random) {
    material.setRandomSphericalNormal(ray, random);
  }

  public void setRayMaterialAndColor(Ray ray) {
    ray.setCurrentMaterial(material);
    ray.color.set(color.x, color.y, color.z, 1);
  }

  public Material getMaterial() {
    return material;
  }

  public double getEmittance() {
    return this.material.emittance;
  }

  public void setEmittance(float value) {
    this.material.emittance = value;
  }

  public double getAnisotropy() {
    return this.material.anisotropy;
  }

  public void setAnisotropy(float value) {
    this.material.anisotropy = value;
  }

  public void setDensity(double value) {
    this.density = value;
  }

  public double getDensity() {
    return density;
  }

  public void setColor(Vector3 value) {
    this.color.set(value);
  }

  public Vector3 getColor() {
    return new Vector3(color);
  }

  public FogVolumeType getType() {
    return type;
  }

  protected abstract JsonObject volumeSpecificPropertiesToJson();

  @Override
  public JsonObject toJson() {
    JsonObject properties = volumeSpecificPropertiesToJson();
    properties.add("type", type.name());
    properties.add("color", ColorUtil.rgbToJson(color));
    properties.add("density", density);
    properties.add("materialProperties", materialPropertiesToJson());
    return properties;
  }

  protected JsonObject materialPropertiesToJson() {
    JsonObject materialProperties = new JsonObject();
    materialProperties.add("emittance", material.emittance);
    materialProperties.add("anisotropy", material.anisotropy);
    return materialProperties;
  }

  protected abstract void importVolumeSpecificProperties(JsonObject jsonObject);

  public void importFromJson(JsonObject json) {
    color.set(ColorUtil.jsonToRGB(json.get("color").asObject()));
    density = json.get("density").doubleValue(Scene.DEFAULT_FOG_DENSITY);
    JsonObject materialProperties = json.get("materialProperties").object();
    importMaterialProperties(materialProperties);
    importVolumeSpecificProperties(json);
  }

  protected void importMaterialProperties(JsonObject json) {
    material.emittance = json.get("emittance").floatValue(material.emittance);
    material.anisotropy = json.get("anisotropy").floatValue(material.anisotropy);
  }
}
