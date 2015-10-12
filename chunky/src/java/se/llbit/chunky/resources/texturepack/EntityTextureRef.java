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
		double height = extended ? 64 : 32;

		texture.headFront.set(8/64., 16/64., (height - 16)/height, (height - 8)/height);
		texture.headBack.set(24/64., 32/64., (height - 16)/height, (height - 8)/height);
		texture.headTop.set(8/64., 16/64., (height - 8)/height, 1);
		texture.headBottom.set(16/64., 24/64., (height - 8)/height, 1);
		texture.headRight.set(0, 8/64., (height - 16)/height, (height - 8)/height);
		texture.headLeft.set(16/64., 24/64., (height - 16)/height, (height - 8)/height);

		texture.hatFront.set(32/64., 40/64., (height - 16)/height, (height - 8)/height);
		texture.hatBack.set(40/64., 48/64., (height - 16)/height, (height - 8)/height);
		texture.hatTop.set(48/64., 56/64., (height - 8)/height, 1);
		texture.hatBottom.set(56/64., 1, (height - 8)/height, 1);
		texture.hatRight.set(40/64., 48/64., (height - 16)/height, (height - 8)/height);
		texture.hatLeft.set(48/64., 56/64., (height - 16)/height, (height - 8)/height);

		texture.chestRight.set(16/64., 20/64., (height - 32)/height, (height - 20)/height);
		texture.chestFront.set(20/64., 28/64., (height - 32)/height, (height - 20)/height);
		texture.chestLeft.set(28/64., 32/64., (height - 32)/height, (height - 20)/height);
		texture.chestBack.set(32/64., 40/64., (height - 32)/height, (height - 20)/height);
		texture.chestTop.set(20/64., 28/64., (height - 20)/height, (height - 16)/height);
		texture.chestBottom.set(28/64., 36/64., (height - 20)/height, (height - 16)/height);

		texture.rightLegRight.set(0/64., 4/64., (height - 32)/height, (height - 20)/height);
		texture.rightLegFront.set(4/64., 8/64., (height - 32)/height, (height - 20)/height);
		texture.rightLegLeft.set(8/64., 12/64., (height - 32)/height, (height - 20)/height);
		texture.rightLegBack.set(12/64., 16/64., (height - 32)/height, (height - 20)/height);
		texture.rightLegTop.set(4/64., 8/64., (height - 20)/height, (height - 16)/height);
		texture.rightLegBottom.set(8/64., 12/64., (height - 20)/height, (height - 16)/height);

		texture.rightArmRight.set(40/64., 44/64., (height - 32)/height, (height - 20)/height);
		texture.rightArmFront.set(44/64., 48/64., (height - 32)/height, (height - 20)/height);
		texture.rightArmLeft.set(48/64., 52/64., (height - 32)/height, (height - 20)/height);
		texture.rightArmBack.set(52/64., 56/64., (height - 32)/height, (height - 20)/height);
		texture.rightArmTop.set(44/64., 48/64., (height - 20)/height, (height - 16)/height);
		texture.rightArmBottom.set(48/64., 52/64., (height - 20)/height, (height - 16)/height);

		if (extended) {
			texture.leftLegRight.set(16/64., 20/64., (height - 64)/height, (height - 52)/height);
			texture.leftLegFront.set(20/64., 24/64., (height - 64)/height, (height - 52)/height);
			texture.leftLegLeft.set(24/64., 28/64., (height - 64)/height, (height - 52)/height);
			texture.leftLegBack.set(28/64., 32/64., (height - 64)/height, (height - 52)/height);
			texture.leftLegTop.set(20/64., 24/64., (height - 52)/height, (height - 48)/height);
			texture.leftLegBottom.set(24/64., 28/64., (height - 52)/height, (height - 48)/height);

			texture.leftArmRight.set(32/64., 36/64., (height - 64)/height, (height - 52)/height);
			texture.leftArmFront.set(36/64., 40/64., (height - 64)/height, (height - 52)/height);
			texture.leftArmLeft.set(40/64., 44/64., (height - 64)/height, (height - 52)/height);
			texture.leftArmBack.set(44/64., 48/64., (height - 64)/height, (height - 52)/height);
			texture.leftArmTop.set(36/64., 40/64., (height - 52)/height, (height - 48)/height);
			texture.leftArmBottom.set(40/64., 44/64., (height - 52)/height, (height - 48)/height);
		} else {
			texture.leftLegRight.set(texture.rightLegLeft);
			texture.leftLegRight.x = texture.rightLegLeft.y;
			texture.leftLegRight.y = texture.rightLegLeft.x;
			texture.leftLegFront.set(texture.rightLegFront);
			texture.leftLegFront.x = texture.rightLegFront.y;
			texture.leftLegFront.y = texture.rightLegFront.x;
			texture.leftLegLeft.set(texture.rightLegRight);
			texture.leftLegLeft.x = texture.rightLegRight.y;
			texture.leftLegLeft.y = texture.rightLegRight.x;
			texture.leftLegBack.set(texture.rightLegBack);
			texture.leftLegBack.x = texture.rightLegBack.y;
			texture.leftLegBack.y = texture.rightLegBack.x;
			texture.leftLegTop.set(texture.rightLegTop);
			texture.leftLegBottom.set(texture.rightLegBottom);
			texture.leftArmRight.set(texture.rightArmRight);
			texture.leftArmFront.set(texture.rightArmFront);
			texture.leftArmLeft.set(texture.rightArmLeft);
			texture.leftArmBack.set(texture.rightArmBack);
			texture.leftArmTop.set(texture.rightArmTop);
			texture.leftArmBottom.set(texture.rightArmBottom);
		}
		return true;
	}

	@Override
	public boolean load(ZipFile texturePack) {
		return load(file, texturePack);
	}

}

