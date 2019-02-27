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
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.Ray;

public abstract class Material {

  /**
   * The name of this material.
   */
  public final String name;

  /**
   * Index of refraction.
   * Default value is equal to the IoR for air.
   */
  public float ior = 1.000293f;

  /**
   * Set to true if there is a local intersection model
   * for this block.
   */
  public boolean localIntersect = false;

  /**
   * A block is opaque if it occupies an entire voxel
   * and no light can pass through it.
   */
  public boolean opaque = false;

  /**
   * The solid property controls various block behaviours like
   * if the block connects to fences, gates, walls, etc.
   */
  public boolean solid = true;

  /**
   * The specular coefficient controlling how shiny the block appears.
   */
  public float specular = 0;

  /**
   * Invisible blocks are not rendered as regular voxels
   * (they are not added to the voxel octree).
   * This is used for blocks that are rendered as entities,
   * and blocks that are not implemented yet.
   */
  public boolean invisible = false;

  /**
   * The amount of light the material emits.
   */
  public float emittance = 0;

  /**
   * Subsurface scattering property.
   */
  public boolean subSurfaceScattering = false;

  /** Base texture. */
  public final Texture texture;

  public Material(String name, Texture texture) {
    this.name = name;
    this.texture = texture;
  }

  /**
   * Retrieves the texture based on the block data.
   *
   * @param blockData [0,16]
   * @return the selected texture
   */
  public Texture getTexture(int blockData) {
    return texture;
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

  public static Material fromJson(JsonValue json) {
    // TODO: implement this?
    throw new UnsupportedOperationException("Can not export material as JSON.");
  }

  public boolean isWater() {
    return false;
  }

  public boolean isSameMaterial(Material other) {
    return other == this;
  }
}
