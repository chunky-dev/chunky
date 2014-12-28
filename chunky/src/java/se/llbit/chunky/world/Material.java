/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
	 * Block name
	 */
	protected final String name;

	/**
	 * Index of refraction.
	 * Default value is equal to the IoR for air.
	 */
	public float ior = 1.000293f;

	/**
	 * True if there is a specific local intersection model
	 * for this block
	 */
	public boolean localIntersect = false;

	/**
	 * A block is opaque if it occupies an entire voxel
	 * and no light can pass through it.
	 *
	 * @return {@code true} if the block is solid
	 */
	public boolean isOpaque = false;

	/**
	 * A block is solid if the block occupies an entire voxel.
	 */
	public boolean isSolid = true;

	/**
	 * A block is shiny if it has a specular reflection.
	 */
	public boolean isShiny = false;

	/**
	 * Invisible blocks are not added to the voxel octree, and thus
	 * they are not rendered. This is only used for special blocks
	 * that either have been replaced by specialized rendering,
	 * such as the lily pad, or are not implemented.
	 */
	public boolean isInvisible = false;

	/**
	 * Emitter blocks emit light.
	 */
	public boolean isEmitter = false;

	public double emittance = 0.0;

	/**
	 * Subsurface scattering property.
	 */
	public boolean subSurfaceScattering = false;

	protected final Texture texture;

	public Material(String name, Texture texture) {
		this.name = name;
		this.texture = texture;
	}

	/**
	 * Retrieves the texture dependent on the block data
	 * @param blockData [0,16]
	 * @return the selected texture
	 */
	public Texture getTexture(int blockData) {
		return texture;
	}

	public void getColor(Ray ray) {
		texture.getColor(ray);
	}

	public JsonValue toJson() {
		return new JsonString("mat:" + name);
	}

	public static Material fromJson(JsonValue json) {
		throw new UnsupportedOperationException("TODO");
	}
}
