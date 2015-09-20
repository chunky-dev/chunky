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
package se.llbit.chunky.resources.texturepack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import se.llbit.chunky.resources.EntityTexture;

/**
 * Helper to load entity textures, i.e. creeper, zombie, skeleton etc. textures.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EntityTextureRef extends TextureRef {
	private final String file;
	private final EntityTexture texture;

	public EntityTextureRef(String file, EntityTexture texture) {
		this.file = file;
		this.texture = texture;
	}

	@Override
	protected boolean load(InputStream imageStream) throws IOException,
			TextureFormatError {

		BufferedImage image = ImageIO.read(imageStream);

		if (image.getWidth() != image.getHeight() && image.getWidth() != 2 * image.getHeight()) {
			throw new TextureFormatError("Entity texture should be 64x64 or 64x32 pixels, "
					+ "or a multiple of those dimensions.");
		}

		texture.setTexture(image);

		boolean extended = image.getHeight() == image.getWidth();
		if (extended) {
			texture.headFront.set(8/64., 16/64., 48/64., 56/64.);
			texture.headBack.set(24/64., 32/64., 48/64., 56/64.);
			texture.headTop.set(8/64., 16/64., 56/64., 1);
			texture.headBottom.set(16/64., 24/64., 56/64., 1);
			texture.headRight.set(0, 8/64., 48/64., 56/64.);
			texture.headLeft.set(16/64., 24/64., 48/64., 56/64.);
		} else {
			texture.headFront.set(8/64., 16/64., 16/32., 24/32.);
			texture.headBack.set(24/64., 32/64., 16/32., 24/32.);
			texture.headTop.set(8/64., 16/64., 24/32., 1);
			texture.headBottom.set(16/64., 24/64., 24/32., 1);
			texture.headRight.set(0, 8/64., 16/32., 24/32.);
			texture.headLeft.set(16/64., 24/64., 16/32., 24/32.);
		}
		return true;
	}

	@Override
	public boolean load(ZipFile texturePack) {
		return load(file, texturePack);
	}

}

