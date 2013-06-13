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
package se.llbit.math;

/**
 * A transformation.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class Transform {

	/**
	 * Apply the rotation part of this transform to a vector
	 * @param o The vector to rotate
	 */
	public abstract void rotate(Vector3d o);

	/**
	 * Apply the translation part of this transform to a vector
	 * @param o The vector to translate
	 */
	public void translate(Vector3d o) {
	}

	/**
	 * Rotation by 90 degrees around the Y axis
	 */
	public static final Transform rotateY = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        double tmp = o.x;
	        o.x = -o.z;
	        o.z = tmp;
	    }
	};

	/**
	 * Rotation by 90 degrees around the X axis
	 */
	public static final Transform rotateX = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        double tmp = o.y;
	        o.y = -o.z;
	        o.z = tmp;
	    }
	};

	/**
	 * Rotation by 90 degrees around the negative X axis
	 */
	public static final Transform rotateNegX = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        double tmp = o.y;
	        o.y = o.z;
	        o.z = -tmp;
	    }
	};

	/**
	 * Rotation by 90 degrees around the Z axis
	 */
	public static final Transform rotateZ = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        double tmp = o.x;
	        o.x = -o.y;
	        o.y = tmp;
	    }
	};

	/**
	 * Rotation by 90 degrees around the negative Z axis
	 */
	public static final Transform rotateNegZ = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        double tmp = o.x;
	        o.x = o.y;
	        o.y = -tmp;
	    }
	};

	/**
	 * Mirror in Y axis
	 */
	public static final Transform flipY = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        o.x = -o.x;
	        o.y = -o.y;
	    }
	};

	/**
	 * Mirror in X axis
	 */
	public static final Transform mirrorX = new Transform() {
		@Override
		public void rotate(Vector3d o) {
	        o.x = -o.x;
	        o.z = -o.z;
	    }
	};
}
