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

import se.llbit.math.transform.Translation;

/**
 * A quad.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Quad {

	protected Vector3d o = new Vector3d();
	protected Vector3d xv = new Vector3d();
	protected Vector3d yv = new Vector3d();
	protected Vector4d uv = new Vector4d();
	
	/**
	 * Normal vector
	 */
	public Vector3d n = new Vector3d();
	
	protected double d, xvl, yvl;
	
	/**
	 * Create new Quad
	 * @param other
	 * @param t
	 */
	public Quad(Quad other, Transform t) {
		o.set(other.o);
		o.x -= .5;
		o.y -= .5;
		o.z -= .5;
		t.rotate(o);
		o.x += .5;
		o.y += .5;
		o.z += .5;
		t.translate(o);
		xv.set(other.xv);
		yv.set(other.yv);
		n.set(other.n);
		t.rotate(xv);
		t.rotate(yv);
		t.rotate(n);
		xvl = other.xvl;
		yvl = other.yvl;
		d = - n.dot(o);
		uv.set(other.uv);
	}
	
	/**
	 * Create transformed Quad
	 * @param other
	 * @param t
	 */
	public Quad(Quad other, Matrix3d t) {
		o.set(other.o);
		o.x -= .5;
		o.y -= .5;
		o.z -= .5;
		t.transform(o);
		o.x += .5;
		o.y += .5;
		o.z += .5;
		xv.set(other.xv);
		yv.set(other.yv);
		n.set(other.n);
		t.transform(xv);
		t.transform(yv);
		t.transform(n);
		xvl = other.xvl;
		yvl = other.yvl;
		d = - n.dot(o);
		uv.set(other.uv);
	}

	/**
	 * Create new quad
	 * @param v0 Bottom left vector
	 * @param v1 Top right vector
	 * @param v2 Bottom right vector
	 * @param uv Minimum and maximum U/V texture coordinates
	 */
	public Quad(Vector3d v0, Vector3d v1, Vector3d v2, Vector4d uv) {
		o.set(v0);
		xv.sub(v1, v0);
		xvl = 1 / xv.lengthSquared();
		yv.sub(v2, v0);
		yvl = 1 / yv.lengthSquared();
		n.cross(xv, yv);
		n.normalize();
		d = - n.dot(o);
		this.uv.set(uv);
		this.uv.y -= uv.x;
		this.uv.w -= uv.z;
	}
	
	/**
	 * Find intersection between the given ray and this quad
	 * @param ray
	 * @return <code>true</code> if the ray intersects this quad
	 */
	public boolean intersect(Ray ray) {
		double u, v;
		
		double ix = ray.x.x - QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET);
		double iy = ray.x.y - QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET);
		double iz = ray.x.z - QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET);
		
		// test that the ray is heading toward the plane
		double denom = ray.d.dot(n);
		if (denom < -Ray.EPSILON) {
			
			// test for intersection with the plane at origin
			double t = - (ix*n.x + iy*n.y + iz*n.z + d) / denom;
			if (t > -Ray.EPSILON && t < ray.t) {
				
				// plane intersection confirmed
				// translate to get hit point relative to the quad origin
				ix = ix + ray.d.x * t - o.x;
				iy = iy + ray.d.y * t - o.y;
				iz = iz + ray.d.z * t - o.z;
				u = ix*xv.x + iy*xv.y + iz*xv.z;
				u *= xvl;
				v = ix*yv.x + iy*yv.y + iz*yv.z;
				v *= yvl;
				if (u >= 0 && u <= 1 && v >= 0 && v <= 1) {
					ray.u = uv.x + u*uv.y;
					ray.v = uv.z + v*uv.w;
					ray.tNear = t;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return A copy of this quad, rotated around the Y axis
	 */
	public Quad getYRotated() {
		return new Quad(this, Transform.rotateY);
	}
	
	/**
	 * @return A copy of this quad, rotated around the X axis
	 */
	public Quad getXRotated() {
		return new Quad(this, Transform.rotateX);
	}
	
	/**
	 * @return A copy of this quad, rotated around the negative X axis
	 */
	public Quad getNegXRotated() {
		return new Quad(this, Transform.rotateNegX);
	}
	
	/**
	 * @return A copy of this quad, rotated around the Z axis
	 */
	public Quad getZRotated() {
		return new Quad(this, Transform.rotateZ);
	}
	
	/**
	 * @return A copy of this quad, rotated around the negative Z axis
	 */
	public Quad getNegZRotated() {
		return new Quad(this, Transform.rotateNegZ);
	}
	
	/**
	 * @param angle 
	 * @return A copy of this quad, rotated around the X axis by some angle
	 */
	public Quad getXRotated(double angle) {
		Matrix3d transform = new Matrix3d();
		transform.rotX(angle);
		return new Quad(this, transform);
	}
	
	/**
	 * @param angle 
	 * @return A copy of this quad, rotated around the Y axis by some angle
	 */
	public Quad getYRotated(double angle) {
		Matrix3d transform = new Matrix3d();
		transform.rotY(angle);
		return new Quad(this, transform);
	}
	
	/**
	 * @param angle 
	 * @return A copy of this quad, rotated around the Z axis by some angle
	 */
	public Quad getZRotated(double angle) {
		Matrix3d transform = new Matrix3d();
		transform.rotZ(angle);
		return new Quad(this, transform);
	}
	
	/**
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return A translated copy of this quad
	 */
	public Quad getTranslated(double x, double y, double z) {
		return new Quad(this, new Translation(x, y, z));
	}
	
	/**
	 * @return A flipped copy of this quad
	 */
	public Quad getFlipped() {
		return new Quad(this, Transform.flipY);
	}
	
	/**
	 * @return A mirrored copy of this quad
	 */
	public Quad getMirrored() {
		return new Quad(this, Transform.mirrorX);
	}

	/**
	 * @param scale
	 * @return Scaled copy of this quad
	 */
	public Quad getScaled(double scale) {
		Matrix3d transform = new Matrix3d();
		transform.scale(scale);
		return new Quad(this, transform);
	}
}
