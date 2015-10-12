/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources;

import se.llbit.math.Vector4d;

/**
 * Stores additional UV coordinates used for entity textures.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EntityTexture extends Texture {

	// Head layer coordinates.
	public final Vector4d headFront = new Vector4d();
	public final Vector4d headBack = new Vector4d();
	public final Vector4d headTop = new Vector4d();
	public final Vector4d headBottom = new Vector4d();
	public final Vector4d headRight = new Vector4d();
	public final Vector4d headLeft = new Vector4d();

	// Hat layer coordinates.
	public final Vector4d hatFront = new Vector4d();
	public final Vector4d hatBack = new Vector4d();
	public final Vector4d hatTop = new Vector4d();
	public final Vector4d hatBottom = new Vector4d();
	public final Vector4d hatRight = new Vector4d();
	public final Vector4d hatLeft = new Vector4d();

	public final Vector4d chestFront = new Vector4d();
	public final Vector4d chestBack = new Vector4d();
	public final Vector4d chestTop = new Vector4d();
	public final Vector4d chestBottom = new Vector4d();
	public final Vector4d chestRight = new Vector4d();
	public final Vector4d chestLeft = new Vector4d();

	public final Vector4d rightLegFront = new Vector4d();
	public final Vector4d rightLegBack = new Vector4d();
	public final Vector4d rightLegTop = new Vector4d();
	public final Vector4d rightLegBottom = new Vector4d();
	public final Vector4d rightLegRight = new Vector4d();
	public final Vector4d rightLegLeft = new Vector4d();

	public final Vector4d leftLegFront = new Vector4d();
	public final Vector4d leftLegBack = new Vector4d();
	public final Vector4d leftLegTop = new Vector4d();
	public final Vector4d leftLegBottom = new Vector4d();
	public final Vector4d leftLegRight = new Vector4d();
	public final Vector4d leftLegLeft = new Vector4d();

	public final Vector4d rightArmFront = new Vector4d();
	public final Vector4d rightArmBack = new Vector4d();
	public final Vector4d rightArmTop = new Vector4d();
	public final Vector4d rightArmBottom = new Vector4d();
	public final Vector4d rightArmRight = new Vector4d();
	public final Vector4d rightArmLeft = new Vector4d();

	public final Vector4d leftArmFront = new Vector4d();
	public final Vector4d leftArmBack = new Vector4d();
	public final Vector4d leftArmTop = new Vector4d();
	public final Vector4d leftArmBottom = new Vector4d();
	public final Vector4d leftArmRight = new Vector4d();
	public final Vector4d leftArmLeft = new Vector4d();
}
