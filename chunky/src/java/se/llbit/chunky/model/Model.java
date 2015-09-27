/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.UVTriangle;

/**
 * Utility methods for quads and triangles.
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Model {
	/**
	 * @param src source quads
	 * @return Quads rotated minus 90 degrees around the X axis
	 */
	public static final Quad[] rotateNegX(Quad[] src) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateNegX());
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @return Quads rotated 90 degrees around the X axis
	 */
	public static final Quad[] rotateX(Quad[] src) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateX());
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @param angle
	 * @return Quads rotated about the X axis by some angle
	 */
	public static final Quad[] rotateX(Quad[] src, double angle) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateX(angle));
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @return Quads rotated 90 degrees around the Y axis
	 */
	public static final Quad[] rotateY(Quad[] src) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateY());
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @return UVTriangles rotated about the Y axis
	 */
	public static final UVTriangle[] rotateY(UVTriangle[] src) {
		UVTriangle[] rot = new UVTriangle[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].getYRotated();
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @param angle
	 * @return Quads rotated about the Y axis by some angle
	 */
	public static final Quad[] rotateY(Quad[] src, double angle) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateY(angle));
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @return Quads rotated about the Z axis
	 */
	public static final Quad[] rotateZ(Quad[] src) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateZ());
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @param angle
	 * @return Quads rotated about the Z axis by some angle
	 */
	public static final Quad[] rotateZ(Quad[] src, double angle) {
		Quad[] rot = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			rot[i] = src[i].transform(Transform.NONE.rotateZ(angle));
		}
		return rot;
	}

	/**
	 * @param src source quads
	 * @param x Distance to translate along the X axis
	 * @param y Distance to translate along the Y axis
	 * @param z Distance to translate along the Z axis
	 * @return Translated copies of the source quads
	 */
	public static final Quad[] translate(Quad[] src, double x, double y, double z) {
		Quad[] out = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			out[i] = src[i].transform(Transform.NONE.translate(x, y, z));
		}
		return out;
	}

	/**
	 * @param src source quads
	 * @param scale
	 * @return Scaled copies of the source quads
	 */
	public static Quad[] scale(Quad[] src, double scale) {
		Quad[] out = new Quad[src.length];
		for (int i = 0; i < src.length; ++i) {
			out[i] = src[i].getScaled(scale);
		}
		return out;
	}

}
