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

import javax.imageio.ImageIO;

import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.resources.Texture;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SunTexture extends TextureRef {
	private final String file;

	/**
	 * Constructor
	 * @param file
	 */
	public SunTexture(String file) {
		this.file = file;
	}

	@Override
	protected boolean load(InputStream imageStream) throws IOException {
		BufferedImage image = ImageIO.read(imageStream);
		Sun.texture = new Texture(image);
		return true;
	}

	@Override
	public boolean load(ZipFile texturePack) {
		return load(file, texturePack);
	}
}

