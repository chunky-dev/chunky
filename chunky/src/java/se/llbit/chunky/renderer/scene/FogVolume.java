package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.util.JsonSerializable;

import java.util.Random;

public abstract class FogVolume implements JsonSerializable {
  protected FogVolumeType type;
  protected Vector3 color;
  protected double density;
  protected Material material = new ParticleFogMaterial();

  public abstract boolean intersect(Ray ray, Scene scene, Random random);

  public void setRandomNormal(Ray ray, Random random) {
    Vector3 a1 = new Vector3();
    a1.cross(ray.d, new Vector3(0, 1, 0));
    a1.normalize();
    Vector3 a2 = new Vector3();
    a2.cross(ray.d, a1);
    // get random point on unit disk
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double r = FastMath.sqrt(x1);
    double theta = 2 * Math.PI * x2;
    double t1 = r * FastMath.cos(theta);
    double t2 = r * FastMath.sin(theta);
    a1.scale(t1);
    a1.scaleAdd(t2, a2);
    a1.scaleAdd(-Math.sqrt(1 - a1.lengthSquared()), ray.d);
    ray.setNormal(a1);
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

  public double getSpecular() {
    return this.material.specular;
  }

  public void setSpecular(float value) {
    this.material.specular = value;
  }

  public double getSmoothness() {
    return this.material.getPerceptualSmoothness();
  }

  public void setSmoothness(double value) {
    this.material.setPerceptualSmoothness(value);
  }

  public double getIor() {
    return this.material.ior;
  }

  public void setIor(float value) {
    this.material.ior = value;
  }

  public double getMetalness() {
    return this.material.metalness;
  }

  public void setMetalness(float value) {
    this.material.metalness = value;
  }

  public void setDensity(double value) {
    this.density = value;
  }

  public double getDensity() {
    return density;
  }

  public void setColor(Vector3 value) {
    this.color = new Vector3(value);
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
    JsonObject colorObj = new JsonObject();
    colorObj.add("red", color.x);
    colorObj.add("green", color.y);
    colorObj.add("blue", color.z);
    properties.add("color", colorObj);
    properties.add("density", density);
    properties.add("materialProperties", materialPropertiesToJson());
    return properties;
  }

  protected JsonObject materialPropertiesToJson() {
    JsonObject materialProperties = new JsonObject();
    materialProperties.add("emittance", material.emittance);
    materialProperties.add("specular", material.specular);
    materialProperties.add("roughness", material.roughness);
    materialProperties.add("ior", material.ior);
    materialProperties.add("metalness", material.metalness);
    return materialProperties;
  }

  protected abstract void importVolumeSpecificProperties(JsonObject jsonObject);

  public void importFromJson(JsonObject json) {
    JsonObject colorObj = json.get("color").object();
    color.x = colorObj.get("red").doubleValue(color.x);
    color.y = colorObj.get("green").doubleValue(color.y);
    color.z = colorObj.get("blue").doubleValue(color.z);
    density = json.get("density").doubleValue(Scene.DEFAULT_FOG_DENSITY);
    JsonObject materialProperties = json.get("materialProperties").object();
    importMaterialProperties(materialProperties);
    importVolumeSpecificProperties(json);
  }

  protected void importMaterialProperties(JsonObject json) {
    material.emittance = json.get("emittance").floatValue(material.emittance);
    material.specular = json.get("specular").floatValue(material.specular);
    material.roughness = json.get("roughness").floatValue(material.roughness);
    material.ior = json.get("ior").floatValue(material.ior);
    material.metalness = json.get("metalness").floatValue(material.metalness);
  }
}
