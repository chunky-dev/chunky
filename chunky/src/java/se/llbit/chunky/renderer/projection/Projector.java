/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.projection;

import java.util.Random;

import se.llbit.math.Vector3d;

/**
 * Projectors project the view ray using different projection models.
 */
public interface Projector {
	/**
	 * @param x pixel X coordinate, where 0 = center and +-0.5 = edges
	 * @param y pixel Y coordinate, where 0 = center and +-0.5 = edges
	 * @param random Random number stream
	 * @param pos will be populated with camera-relative ray origin position
	 * @param direction will be populated with camera-relative ray direction
	 *            (not necessarily normalized)
	 */
	public void apply(double x, double y, Random random, Vector3d pos,
			Vector3d direction);

	public double getMinRecommendedFoV();

	public double getMaxRecommendedFoV();

	public double getDefaultFoV();
}