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
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import se.llbit.chunky.resources.Texture;

/**
 * A texture that has an indexed position in terrain.pngggu
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class IndexedTexture extends TextureRef {

	private final int index;
	private final Texture texture;

	/**
	 * Constructor
	 * @param index Index of the texture in the terrain file
	 * @param texture The loaded image is written to this texture
	 * @param name The texture file name (excluding extension and directory
	 * parts)
	 */
	public IndexedTexture(int index, Texture texture) {
		this.index = index;
		this.texture = texture;
	}

	@Override
	public boolean loadFromTerrain(BufferedImage[] terrain) {
		texture.setTexture(terrain[index]);
		return true;
	}

	@Override
	public boolean load(ZipFile texturePack) {
		return false;
	}

	@Override
	protected boolean load(InputStream imageStream) throws IOException,
			TextureFormatError {
		return false;
	}

}
