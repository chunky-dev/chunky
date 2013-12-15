/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;
import java.awt.image.BufferedImage;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Color;
import se.llbit.math.QuickMath;

/**
 * Biome constants and utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biomes {
	private static final int SWAMP_ID = 6;
	private static final Biome biomes[] = {
		new Biome("ocean", 0.5, 0.5, 0x000070, 0x75B646),
		new Biome("plains", 0.8, 0.4, 0x8DB360, 0x8DB84A),
		new Biome("desert", 1.0, 0.0, 0xFA9418, 0x9BA863),
		new Biome("extreme hills", 0.2, 0.3, 0x606060, 0x75B646),
		new Biome("forest", 0.7, 0.8, 0x056621, 0x4A8F3A),
		new Biome("taiga", 0.05, 0.8, 0x00DD2D, 0x478852),
		new Biome("swampland", 0.8, 0.9, 0x07F9B2, 0x3e5226),
		new Biome("river", 0.5, 0.5, 0x0000FF, 0x75B646),
		new Biome("hell", 1.0, 0.0, 0xFF0000, 0x75B646),
		new Biome("sky", 0.5, 0.5, 0x8080FF, 0x75B646),
		new Biome("frozen ocean", 0.0, 0.5, 0x9090A0, 0x7A9C91),
		new Biome("frozen river", 0.0, 0.5, 0xA0A0FF, 0x7A9C91),
		new Biome("ice plains", 0.0, 0.5, 0xFFFFFF, 0x7A9C91),
		new Biome("ice mountains", 0.0, 0.5, 0xA0A0A0, 0x7A9C91),
		new Biome("mushroom island", 0.9, 1.0, 0xFF00FF, 0x939D88),
		new Biome("mushroom island shore", 0.9, 1.0, 0xA000FF, 0x939D88),
		new Biome("beach", 0.8, 0.4, 0xFADE55, 0x75B646),
		new Biome("desert hills", 1.0, 0.0, 0xD25F12, 0x9BA863),
		new Biome("forest hills", 0.7, 0.8, 0x22551C, 0x4A8F3A),
		new Biome("taiga hills", 0.05,0.8, 0x163933, 0x478852),
		new Biome("extreme hills edge", 0.2, 0.3, 0x72789A, 0x75B646),
		new Biome("jungle", 1.0, 0.9, 0x537B09, 0x3A8B25),
		new Biome("jungle hills", 1.0, 0.9, 0x2C4205, 0x3A8B25),
		new Biome("jungle edge", 0.95, 0.8, 0x628B17, 0x3EB80F),
		new Biome("deep ocean", 0.5, 0.5, 0x000030, 0x71A74D),
		new Biome("stone beach", 0.2, 0.3, 0xA2A284, 0x6DA36B),
		new Biome("cold beach", 0.05, 0.3, 0xFAF0C0, 0x64A278),
		new Biome("birch forest", 0.7, 0.8, 0x307444, 0x59AE30),
		new Biome("birch forest hills", 0.7, 0.8, 0x1F5F32, 0x59AE30),
		new Biome("roofed forest", 0.7, 0.8, 0x40511A, 0x59AE30),
		new Biome("cold taiga", -0.5, 0.4, 0x31554A, 0x60A17B),
		new Biome("cold taiga hills", -0.5, 0.4, 0x243F36, 0x60A17B),
		new Biome("mega taiga", 0.3, 0.8, 0x596651, 0x68A55F),
		new Biome("mega taiga hills", 0.3, 0.8, 0x454F3E, 0x68A55F),
		new Biome("extreme hills+", 0.2, 0.3, 0x507050, 0x6DA36B),
		new Biome("savanna", 1.2, 0.0, 0xBDB25F, 0xAEA42A),
		new Biome("savanna plateau", 1.0, 0.0, 0xA79D64, 0xAEA42A),
		new Biome("mesa", 2.0, 0.0, 0xD94515, 0xAEA42A),
		new Biome("mesa plateau f", 2.0, 0.0, 0xB09765, 0xAEA42A),
		new Biome("mesa plateau", 2.0, 0.0, 0xCA8C65, 0xAEA42A),
	};

	private static int[] grassColor = new int[256];
	private static int[] foliageColor = new int[256];
	private static float[][] grassColorLinear = new float[grassColor.length][3];
	private static float[][] foliageColorLinear = new float[grassColor.length][3];
	private static final int UNKNOWN_COLOR = 0x7E7E7E;

	static {
		for (int i = 0; i < biomes.length; ++i) {
			grassColor[i] = biomes[i].grassColor;
		}
		for (int i = biomes.length; i < 256; ++i) {
			grassColor[i] = UNKNOWN_COLOR;
		}

		gammaCorrectColors(grassColor, grassColorLinear);

		for (int i = 0; i < 256; ++i) {
			foliageColor[i] = grassColor[i];
			foliageColorLinear[i][0] = grassColorLinear[i][0];
			foliageColorLinear[i][1] = grassColorLinear[i][1];
			foliageColorLinear[i][2] = grassColorLinear[i][2];
		}

	}

	/**
	 * @param biomeId
	 * @return Biome color for given biome ID
	 */
	public static final int getColor(int biomeId) {
		if (biomeId >= biomes.length)
			return UNKNOWN_COLOR;
		return biomes[biomeId].mapColor;
	}

	/**
	 * Loads grass colors from a grass color texture
	 * @param texture
	 */
	public static void loadGrassColors(BufferedImage texture) {
		loadColorsFromTexture(grassColor, texture);
		gammaCorrectColors(grassColor, grassColorLinear);
	}

	/**
	 * Loads foliage colors from a grass color texture
	 * @param texture
	 */
	public static void loadFoliageColors(BufferedImage texture) {
		loadColorsFromTexture(foliageColor, texture);
		gammaCorrectColors(foliageColor, foliageColorLinear);
	}

	private static void loadColorsFromTexture(int[] dest, BufferedImage texture) {
		for (int i = 0; i < biomes.length; ++i) {
			double temp = QuickMath.clamp(biomes[i].temp, 0, 1);
			double rain = QuickMath.clamp(biomes[i].rain, 0, 1);
			rain *= temp;
			int	color = texture.getRGB((int) ((1-temp) * 255), (int) ((1-rain) * 255));
			dest[i] = color;
		}
		// swamp get special treatment
		dest[SWAMP_ID] = ((dest[SWAMP_ID] & 0xFEFEFE) + 0x4E0E4E) / 2;
	}

	private static void gammaCorrectColors(int[] src, float[][] dest) {
		float[] frgb = new float[3];
		for (int i = 0; i < src.length; ++i) {
			Color.getRGBComponents(src[i], frgb);
			dest[i][0] = (float) FastMath.pow(frgb[0], Scene.DEFAULT_GAMMA);
			dest[i][1] = (float) FastMath.pow(frgb[1], Scene.DEFAULT_GAMMA);
			dest[i][2] = (float) FastMath.pow(frgb[2], Scene.DEFAULT_GAMMA);
		}
	}

	/**
	 * @param biomeId
	 * @return Grass color for the given biome ID
	 */
	public static final int getGrassColor(int biomeId) {
		return grassColor[0xFF & biomeId];
	}

	/**
	 * @param biomeId
	 * @return Foliage color for the given biome ID
	 */
	public static final int getFoliageColor(int biomeId) {
		return foliageColor[0xFF & biomeId];
	}

	/**
	 * @param biomeId Must be in the range [0,255]
	 * @return Linear biome color for the given biome ID
	 */
	public static final float[] getGrassColorLinear(int biomeId) {
		return grassColorLinear[biomeId];
	}

	/**
	 * @param biomeId Must be in the range [0,255]
	 * @return Linear foliage color for the given biome ID
	 */
	public static final float[] getFoliageColorLinear(int biomeId) {
		return foliageColorLinear[biomeId];
	}
}
