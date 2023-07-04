/* Copyright (c) 2014-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.Ray;

public abstract class Material {

  /**
   * Index of refraction of air.
   */
  private static final float DEFAULT_IOR = 1.000293f;

  /**
   * The name of this material.
   */
  public final String name;

  /**
   * Index of refraction. Default value is equal to the IoR for air.
   */
  public float ior = DEFAULT_IOR;

  /**
   * A block is opaque if it occupies an entire voxel and no light can pass through it.
   */
  public boolean opaque = false;

  /**
   * The solid property controls various block behaviours like if the block connects to fences,
   * gates, walls, etc.
   */
  public boolean solid = true;

  /**
   * The specular coefficient controlling how shiny the block appears.
   */
  public float specular = 0;

  /**
   * The amount of light the material emits.
   */
  public float emittance = 0;

  /**
   * Multiplier for the apparent brightness of the material when acting as an emitter.
   */
  public float apparentBrightnessModifier = 1;

  /**
   * The (linear) roughness controlling how rough a shiny block appears. A value of 0 makes the
   * surface perfectly specular, a value of 1 makes it diffuse.
   */
  public float roughness = 0f;

  /**
   * The metalness value controls how metal-y a block appears. In reality this is a boolean value
   * but in practice usually a float is used in PBR to allow adding dirt or scratches on metals
   * without increasing the texture resolution.
   * Metals only do specular reflection for certain wavelengths (effectively tinting the reflection)
   * and have no diffuse reflection. The albedo color is used for tinting.
   */
  public float metalness = 0;

  /**
   * Subsurface scattering property.
   */
  public boolean subSurfaceScattering = false;

  /**
   * Base texture.
   */
  public final Texture texture;

  public boolean refractive = false;

  public boolean waterlogged = false;

  public Material(String name, Texture texture) {
    this.name = name;
    this.texture = texture;
  }

  /**
   * Restore the default material properties.
   */
  public void restoreDefaults() {
    ior = DEFAULT_IOR;
    opaque = false;
    solid = true;
    specular = 0;
    emittance = 0;
    apparentBrightnessModifier = 1;
    roughness = 0;
    subSurfaceScattering = false;
  }

  public void getColor(Ray ray) {
    texture.getColor(ray);
  }

  public float[] getColor(double u, double v) {
    return texture.getColor(u, v);
  }

  public JsonValue toJson() {
    return new JsonString("mat:" + name);
  }

  public void loadMaterialProperties(JsonObject json) {
    ior = json.get("ior").floatValue(ior);
    specular = json.get("specular").floatValue(specular);
    emittance = json.get("emittance").floatValue(emittance);
    apparentBrightnessModifier = json.get("apparentBrightnessModifier").floatValue(apparentBrightnessModifier);
    roughness = json.get("roughness").floatValue(roughness);
    metalness = json.get("metalness").floatValue(metalness);
  }

  public boolean isWater() {
    return false;
  }

  public boolean isWaterFilled() {
    return waterlogged || isWater();
  }

  public boolean isSameMaterial(Material other) {
    return other == this;
  }

  public double getPerceptualSmoothness() {
    return 1 - Math.sqrt(roughness);
  }

  public void setPerceptualSmoothness(double perceptualSmoothness) {
    roughness = (float) Math.pow(1 - perceptualSmoothness, 2);
  }
}
