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

import se.llbit.chunky.resources.Texture;

/**
 * A simple texture
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SimpleTexture extends TextureRef {

	private final String file;
	protected Texture texture;

	/**
	 * Constructor
	 * @param file
	 * @param texture
	 */
	public SimpleTexture(String file, Texture texture) {
		this.file = file;
		this.texture = texture;
	}

	@Override
	protected boolean load(InputStream imageStream) throws IOException {
		BufferedImage image = ImageIO.read(imageStream);

		if (image.getHeight() > image.getWidth()) {
			// Assuming this to be an animated texture.
			// Just grab the first frame..
			int frameW = image.getWidth();

			BufferedImage frame0 = new BufferedImage(frameW, frameW,
						BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < frameW; ++y) {
				for (int x = 0; x < frameW; ++x) {
					frame0.setRGB(x, y, image.getRGB(x, y));
				}
			}
			image = frame0;
		}

		texture.setTexture(image);
		return true;
	}

	@Override
	public boolean load(ZipFile texturePack) {
		return load(file, texturePack);
	}

}
