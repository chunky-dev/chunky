/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.Scene;
import se.llbit.math.Color;

/**
 * Biome constants and utility methods.
 * 
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biomes {
	private static final int[] grassColor = {
			0x75B646,// ocean
			0x8DB84A,// plains
			0x9BA863,// desert
			0x75B646,// extreme hills
			0x4A8F3A,// forest
			0x478852,// taiga
			0x3e5226,// swampland
			0x75B646,// river
			0x75B646,// hell
			0x75B646,// sky
			0x7A9C91,// frozen ocean
			0x7A9C91,// frozen river
			0x7A9C91,// ice plains
			0x7A9C91,// ice mountains
			0x939D88,// mushroom island
			0x939D88,// mushroom island shore
			0x75B646,// beach
			0x9BA863,// desert hills
			0x4A8F3A,// forest hills
			0x478852,// taiga hills
			0x75B646,// extreme hills edge
			0x3A8B25,// jungle
			0x3A8B25,// jungle hills
	};
	private static final int UNKNOWN_COLOR = 0x7E7E7E;
	private static final float[] UNKNOWN_COLOR_CORRECTED = new float[3];
	
	/**
	 * Global biome color flag
	 */
	public static boolean biomeColorsEnabled = true;// TODO: make non-global?
	
	private static final float[][] gammaCorrected = new float[grassColor.length][3];
	static {
		float[] frgb = new float[3];
		for (int i = 0; i < grassColor.length; ++i) {
			Color.getRGBComponents(grassColor[i], frgb);
			gammaCorrected[i][0] = (float) Math.pow(frgb[0], Scene.DEFAULT_GAMMA);
			gammaCorrected[i][1] = (float) Math.pow(frgb[1], Scene.DEFAULT_GAMMA);
			gammaCorrected[i][2] = (float) Math.pow(frgb[2], Scene.DEFAULT_GAMMA);
		}
		Color.getRGBComponents(UNKNOWN_COLOR, frgb);
		UNKNOWN_COLOR_CORRECTED[0] = (float) Math.pow(frgb[0], Scene.DEFAULT_GAMMA);
		UNKNOWN_COLOR_CORRECTED[1] = (float) Math.pow(frgb[1], Scene.DEFAULT_GAMMA);
		UNKNOWN_COLOR_CORRECTED[2] = (float) Math.pow(frgb[2], Scene.DEFAULT_GAMMA);
	}
	
	/**
	 * @param biomeId
	 * @return Biome color for given biome ID
	 */
	public static final int getColor(int biomeId) {
		if (biomeId > grassColor.length)
			return UNKNOWN_COLOR;
		return grassColor[biomeId];
	}
	
	/**
	 * @param biomeId
	 * @return Linear biome color for given biome ID
	 */
	public static final float[] getColorCorrected(int biomeId) {
		if (!biomeColorsEnabled)
			return gammaCorrected[0];
		if (biomeId > gammaCorrected.length)
			return UNKNOWN_COLOR_CORRECTED;
		return gammaCorrected[biomeId];
	}
}
