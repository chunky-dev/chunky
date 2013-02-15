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

import org.apache.log4j.Logger;

import se.llbit.chunky.resources.TexturePackLoader;

/**
 * Reference to a texture file in a Minecraft texture pack
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class TextureRef {
	private static final Logger logger =
			Logger.getLogger(TexturePackLoader.class);
	private String file;
	
	/**
	 * Constructor
	 * @param file The path to the texture file (excluding extension)
	 */
	public TextureRef(String file) {
		this.file = file + ".png";
	}
	
	/**
	 * Attempt to load a texture from a texture pack
	 * @param texturePack Reference to the texture pack zip file
	 * @param texPack Description of the texture pack
	 * @return <code>true</code> if the texture was successfully loaded
	 */
	public boolean load(ZipFile texturePack, String texPack) {
		try {
			InputStream in = texturePack.getInputStream(new ZipEntry(file));
			if (in == null) {
				logger.info("Could not load " + file + " from " + texPack + "!");
				return false;
			} else {
				return load(in);
			}
		} catch (IOException e) {
			// TODO
			logger.info("Failed to load " + texPack);
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
	
	abstract boolean load(InputStream imageStream) throws IOException;
}