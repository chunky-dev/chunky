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
package se.llbit.math.transform;

import se.llbit.math.Transform;
import se.llbit.math.Vector3d;

/**
 * Translation-only transformation
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Translation extends Transform {

	private double x;
	private double y;
	private double z;

	/**
	 * Construct a new translation transform
	 * @param x
	 * @param y
	 * @param z
	 */
	public Translation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void rotate(Vector3d o) {
	}
	
	@Override
	public void translate(Vector3d o) {
		o.x += x;
		o.y += y;
		o.z += z;
	}

}
