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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import se.llbit.log.Log;

/**
 * Reference to a texture file in a Minecraft texture pack
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class TextureRef {

	/**
	 * Default constructor
	 */
	protected TextureRef() {
	}

	/**
	 * Attempt to load a texture from a texture pack
	 * @param texturePack Reference to the texture pack zip file
	 * @return <code>true</code> if the texture was successfully loaded
	 */
	public abstract boolean load(ZipFile texturePack);

	/**
	 * Attempt to load a texture from a texture pack
	 * @param file Path of texture in texture pack
	 * @param texturePack Reference to the texture pack zip file
	 * @return <code>true</code> if the texture was successfully loaded
	 */
	protected boolean load(String file, ZipFile texturePack) {
		try {
			InputStream in = texturePack.getInputStream(
					new ZipEntry(file + ".png"));
			if (in != null) {
				return load(in);
			}
		} catch (TextureFormatError e) {
			Log.info(e.getMessage());
		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * Load this texture from the terrain spritemap
	 * @param terrain
	 * @return <code>true</code> if the texture was successfully loaded
	 */
	public boolean loadFromTerrain(BufferedImage[] terrain) {
		return false;
	}

	protected abstract boolean load(InputStream imageStream)
			throws IOException, TextureFormatError;
}

