/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources.texturepack;

import java.awt.image.BufferedImage;

import se.llbit.chunky.resources.Texture;

/**
 * A texture that has an indexed position in terrain.pngggu
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AnimatedIndexedTexture extends AnimatedTexture {
	
	private int index;

	/**
	 * Constructor
	 * @param name The texture file name (excluding extension and directory
	 * parts)
	 * @param texture 
	 * @param index Index of the texture in the terrain file
	 */
	public AnimatedIndexedTexture(String name, Texture texture, int index) {
		super(name, texture);
		
		this.index = index;
	}
	
	@Override
	public boolean loadFromTerrain(BufferedImage[] terrain) {
		texture.setTexture(terrain[index]);
		return true;
	}

}
