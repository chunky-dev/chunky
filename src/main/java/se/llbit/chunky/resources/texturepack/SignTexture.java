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

import javax.imageio.ImageIO;

import se.llbit.chunky.resources.Texture;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SignTexture extends TextureRef {
	/**
	 * Constructor
	 * @param file
	 */
	public SignTexture(String file) {
		super(file);
	}

	@Override
	boolean load(InputStream imageStream) throws IOException {
		BufferedImage image = ImageIO.read(imageStream);
		Texture.signPost.setTexture(image);
		return true;
	}
}

