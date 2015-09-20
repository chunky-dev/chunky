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

	// UV coordinates for different parts of the entity.
	public final Vector4d headFront = new Vector4d();
	public final Vector4d headBack = new Vector4d();
	public final Vector4d headTop = new Vector4d();
	public final Vector4d headBottom = new Vector4d();
	public final Vector4d headRight = new Vector4d();
	public final Vector4d headLeft = new Vector4d();
}
