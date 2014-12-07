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

import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.math.Vector3d;

/**
 * Stereographic projection maps points on the sphere to a plane.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class StereographicProjector implements Projector {

	private final double scale;

	public StereographicProjector(double fov) {
		scale = Camera.clampedFovTan(fov);
	}

	@Override
	public void apply(double x, double y, Random random, Vector3d o, Vector3d d) {
		y *= scale;
		x *= scale;
		double xx_yy = x * x + y * y;
		double r = 1 / (1 + xx_yy);
		d.set(2 * x * r, 2 * y * r, -(-1 + xx_yy) * r);
		o.set(0, 0, 0);
	}

	@Override
	public double getMinRecommendedFoV() {
		return 1;
	}

	@Override
	public double getMaxRecommendedFoV() {
		return 175;
	}

	@Override
	public double getDefaultFoV() {
		return 95;
	}
}
