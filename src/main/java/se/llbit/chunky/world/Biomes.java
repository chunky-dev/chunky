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
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Color;

/**
 * Biome constants and utility methods.
 * 
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Biomes {
	/**
	 * Temperature and rain values, clamped to [0,1]
	 */
	private static final float[][] tempAndRain = {
			{0.5f, 0.5f},// ocean
			{0.8f, 0.4f},// plains
			{1.0f, 0.0f},// desert
			{0.2f, 0.3f},// extreme hills
			{0.7f, 0.8f},// forest
			{0.05f,0.8f},// taiga
			{0.8f, 0.9f},// swampland
			{0.5f, 0.5f},// river
			{1.0f, 0.0f},// hell
			{0.5f, 0.5f},// sky
			{0.0f, 0.5f},// frozen ocean
			{0.0f, 0.5f},// frozen river
			{0.0f, 0.5f},// ice plains
			{0.0f, 0.5f},// ice mountains
			{0.9f, 1.0f},// mushroom island
			{0.9f, 1.0f},// mushroom island shore
			{0.8f, 0.4f},// beach
			{1.0f, 0.0f},// desert hills
			{0.7f, 0.8f},// forest hills
			{0.05f,0.8f},// taiga hills
			{0.2f, 0.3f},// extreme hills edge
			{1.0f, 0.9f},// jungle
			{1.0f, 0.9f},// jungle hills
	};
	private static final int[] biomeColor = {
			0x000070,// ocean
			0x8DB360,// plains
			0xFA9418,// desert
			0x606060,// extreme hills
			0x056621,// forest
			0x00DD2D,// taiga
			0x07F9B2,// swampland
			0x0000FF,// river
			0xFF0000,// hell
			0x8080FF,// sky
			0x9090A0,// frozen ocean
			0xA0A0FF,// frozen river
			0xFFFFFF,// ice plains
			0xA0A0A0,// ice mountains
			0xFF00FF,// mushroom island
			0xA000FF,// mushroom island shore
			0xFADE55,// beach
			0xD25F12,// desert hills
			0x22551C,// forest hills
			0x163933,// taiga hills
			0x72789A,// extreme hills edge
			0x537B09,// jungle
			0x2C4205,// jungle hills
	};
	
	private static int[] grassColor = new int[256];
	private static float[][] grassColorCorrected = new float[grassColor.length][3];
	private static final int UNKNOWN_COLOR = 0x7E7E7E;
	
	static {
		grassColor[0] = 0x75B646;// ocean
		grassColor[1] = 0x8DB84A;// plains
		grassColor[2] = 0x9BA863;// desert
		grassColor[3] = 0x75B646;// extreme hills
		grassColor[4] = 0x4A8F3A;// forest
		grassColor[5] = 0x478852;// taiga
		grassColor[6] = 0x3e5226;// swampland
		grassColor[7] = 0x75B646;// river
		grassColor[8] = 0x75B646;// hell
		grassColor[9] = 0x75B646;// sky
		grassColor[10] = 0x7A9C91;// frozen ocean
		grassColor[11] = 0x7A9C91;// frozen river
		grassColor[12] = 0x7A9C91;// ice plains
		grassColor[13] = 0x7A9C91;// ice mountains
		grassColor[14] = 0x939D88;// mushroom island
		grassColor[15] = 0x939D88;// mushroom island shore
		grassColor[16] = 0x75B646;// beach
		grassColor[17] = 0x9BA863;// desert hills
		grassColor[18] = 0x4A8F3A;// forest hills
		grassColor[19] = 0x478852;// taiga hills
		grassColor[20] = 0x75B646;// extreme hills edge
		grassColor[21] = 0x3A8B25;// jungle
		grassColor[22] = 0x3A8B25;// jungle hills
		
		for (int i = 23; i < 256; ++i)
			grassColor[i] = UNKNOWN_COLOR;
		
		gammaCorrectColors();
	}
	
	/**
	 * Global biome color flag
	 */
	public static boolean biomeColorsEnabled = true;// TODO: make non-global?
	
	/**
	 * @param biomeId
	 * @return Biome color for given biome ID
	 */
	public static final int getColor(int biomeId) {
		return biomeColor[0xFF & biomeId];
	}
	
	/**
	 * Loads grass colors from a grass color texture
	 * @param texture
	 */
	public static void loadGrassColors(Texture texture) {
		double	temp, rain;
		float[]	rgba;
		for (int i = 0; i < tempAndRain.length; ++i) {
			temp = tempAndRain[i][0];
			rain = tempAndRain[i][1];
			rain *= temp;
			rgba = texture.getColor(1.0-temp, rain);
			grassColorCorrected[i][0] = rgba[0];
			grassColorCorrected[i][1] = rgba[1];
			grassColorCorrected[i][2] = rgba[2];
			grassColor[i] = Color.getRGB(grassColorCorrected[i]);
		}
	}
	
	private static void gammaCorrectColors() {
		float[] frgb = new float[3];
		for (int i = 0; i < grassColor.length; ++i) {
			Color.getRGBComponents(grassColor[i], frgb);
			grassColorCorrected[i][0] = (float) Math.pow(frgb[0], Scene.DEFAULT_GAMMA);
			grassColorCorrected[i][1] = (float) Math.pow(frgb[1], Scene.DEFAULT_GAMMA);
			grassColorCorrected[i][2] = (float) Math.pow(frgb[2], Scene.DEFAULT_GAMMA);
		}
	}

	/**
	 * @param biomeId
	 * @return Biome color for given biome ID
	 */
	public static final int getGrassColor(int biomeId) {
		return grassColor[0xFF & biomeId];
	}
	
	/**
	 * @param biomeId
	 * @return Linear biome color for given biome ID
	 */
	public static final float[] getGrassColorCorrected(int biomeId) {
		if (!biomeColorsEnabled)
			return grassColorCorrected[0];
		return grassColorCorrected[biomeId];
	}
}
