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

import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;

/**
 * Moves the ray origin forward (if displacement is positive) along the
 * direction vector.
 */
public class ForwardDisplacementProjector implements Projector {
	protected final Projector wrapped;
	protected final double displacementValue;
	protected final double displacementSign;

	public ForwardDisplacementProjector(Projector wrapped,
			double displacement) {
		this.wrapped = wrapped;
		this.displacementValue = QuickMath.abs(displacement);
		this.displacementSign = QuickMath.signum(displacement);
	}

	@Override
	public void apply(double x, double y, Random random, Vector3d o,
			Vector3d d) {
		wrapped.apply(x, y, random, o, d);

		d.normalize();
		d.scale(displacementValue);
		o.scaleAdd(displacementSign, d, o);
	}

	@Override
	public double getMinRecommendedFoV() {
		return wrapped.getMinRecommendedFoV();
	}

	@Override
	public double getMaxRecommendedFoV() {
		return wrapped.getMaxRecommendedFoV();
	}

	@Override
	public double getDefaultFoV() {
		return wrapped.getDefaultFoV();
	}
}