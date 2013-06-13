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
import org.apache.commons.math3.util.FastMath;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * Reference to a texture file in a Minecraft texture pack
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class TextureRef {

	private static final Logger logger =
			Logger.getLogger(TextureRef.class);

	private String file;

	/**
	 * Constructor
	 * @param file The path to the texture file (excluding file extension)
	 */
	public TextureRef(String file) {
		this.file = file;
	}

	/**
	 * Attempt to load a texture from a texture pack
	 * @param texturePack Reference to the texture pack zip file
	 * @return <code>true</code> if the texture was successfully loaded
	 */
	public boolean load(ZipFile texturePack) {
		try {
			InputStream in = texturePack.getInputStream(
					new ZipEntry(file + ".png"));
			if (in != null) {
				return load(in);
			}
		} catch (TextureFormatError e) {
			logger.info(e.getMessage());
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

	abstract boolean load(InputStream imageStream) throws IOException,
			TextureFormatError;

	/**
	 * @return The symbolic name of this texture reference
	 */
	public String getName() {
		return file;
	}
}

