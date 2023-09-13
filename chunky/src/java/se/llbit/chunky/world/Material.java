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

import se.llbit.chunky.renderer.EmitterMappingType;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

import java.util.ArrayList;

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
   * Offset to apply to the global emitter mapping exponent (the resulting value will be constrained to be >= 0).
   */
  public float emitterMappingOffset = 0;

  /**
   * Overrides the global emitter mapping type unless set to NONE.
   */
  public EmitterMappingType emitterMappingType = EmitterMappingType.NONE;

  /**
   * (x, y, z): The color to use for the REFERENCE_COLORS emitter mapping type.
   * w: The range surrounding the specified color to apply full brightness.
   */
  public ArrayList<Vector4> emitterMappingReferenceColors = new ArrayList<>();

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
    emitterMappingOffset = 0;
    emitterMappingType = EmitterMappingType.NONE;
    emitterMappingReferenceColors = new ArrayList<>();
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
    roughness = json.get("roughness").floatValue(roughness);
    metalness = json.get("metalness").floatValue(metalness);
    emittance = json.get("emittance").floatValue(emittance);
    emitterMappingOffset = json.get("emitterMappingOffset").floatValue(emitterMappingOffset);
    emitterMappingType = EmitterMappingType.valueOf(json.get("emitterMappingType").asString(emitterMappingType.toString()));
    JsonArray referenceColors = json.get("emitterMappingReferenceColors").array();
    // Overwrite existing reference colors, but only if any are specified
    if(referenceColors.size() > 0) {
      emitterMappingReferenceColors = new ArrayList<>();
    }
    for(JsonValue refColorJson : referenceColors.elements) {
      Vector4 refColor = new Vector4();
      refColor.x = refColorJson.object().get("red").floatValue(0);
      refColor.y = refColorJson.object().get("green").floatValue(0);
      refColor.z = refColorJson.object().get("blue").floatValue(0);
      refColor.w = refColorJson.object().get("range").floatValue(0);
      emitterMappingReferenceColors.add(refColor);
    }
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

  /**
   * Set the emittance based on a Minecraft light level
   * @param level The light level from 0 to 15
   */
  public void setLightLevel(float level) {
    emittance = level / 15;
  }

  public void addRefColorGammaCorrected(float r, float g, float b, float delta) {
    emitterMappingReferenceColors.add(new Vector4(Math.pow(r/255, Scene.DEFAULT_GAMMA), Math.pow(g/255, Scene.DEFAULT_GAMMA), Math.pow(b/255, Scene.DEFAULT_GAMMA), delta));
  }
}
