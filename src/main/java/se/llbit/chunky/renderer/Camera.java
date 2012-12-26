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
package se.llbit.chunky.renderer;

import java.util.Random;

import se.llbit.chunky.world.World;
import se.llbit.math.Matrix3d;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;

/**
 * Camera model for 3D rendering
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Camera {

	/**
	 * Minimum DoF
	 */
	public static final double MIN_DOF = .5;
	
	/**
	 * Maximum DoF
	 */
	public static final double MAX_DOF = 500;
	
	/**
	 * Minimum FOV
	 */
	public static final double MIN_FOV = 1;
	
	/**
	 * Maximum FOV
	 */
	public static final double MAX_FOV = 110;
	
	/**
	 * Minimum Focal Offset
	 */
	public static final double MIN_FOCAL_OFFSET = 1;
	
	/**
	 * Maximum Focal Offset
	 */
	public static final double MAX_FOCAL_OFFSET = 1000;

	private final Refreshable scene;

	Vector3d pos = new Vector3d(0, 0, 0);
	private Vector3d up = new Vector3d(0, 1, 0);
	private Vector3d right = new Vector3d();
	private Vector3d d = new Vector3d();
	private double yaw = - Math.PI / 2;
	private double pitch = 0;
	private Matrix3d transform = new Matrix3d();
	private Matrix3d tmpTransform = new Matrix3d();

	private double dof = 8;
	private double fov = 70;
	
	/**
	 * Tangens of the FoV angle
	 */
	public double fovTan;
	
	private double focalOffset = 2;
	private boolean infDof = true;

	/**
	 * Create a new camera
	 * @param scene The scene which the camera should be attached to
	 */
	public Camera(Refreshable scene) {
		this.scene = scene;
		transform.setIdentity();
		calcFovTan();
		updateTransform();
	}
	
	/**
	 * Copy configuration from other camera
	 * @param other
	 */
	public void set(Camera other) {
		pos.set(other.pos);
		yaw = other.yaw;
		pitch = other.pitch;
		transform.set(other.transform);
		dof = other.dof;
		fov = other.fov;
		focalOffset = other.focalOffset;
		infDof = other.infDof;
		calcFovTan();
		updateTransform();
	}

	/**
	 * Serialize the camera object to a CompoundTag
	 * @return Serialized camera
	 */
	public CompoundTag store() {
		CompoundTag camera = new CompoundTag();
		camera.addItem("x", new DoubleTag(pos.x));
		camera.addItem("y", new DoubleTag(pos.y));
		camera.addItem("z", new DoubleTag(pos.z));
		camera.addItem("pitch", new DoubleTag(pitch));
		camera.addItem("yaw", new DoubleTag(yaw));
		camera.addItem("fov", new DoubleTag(fov));
		camera.addItem("dof", new DoubleTag(dof));
		camera.addItem("infDof", new ByteTag(infDof ? 1 : 0));
		camera.addItem("focalOffset", new DoubleTag(focalOffset));
		return camera;
	}
	
	/**
	 * Load the camera from a CompoundTag
	 * @param tag
	 */
	public void load(CompoundTag tag) {
		double x = tag.get("x").doubleValue();
		double y = tag.get("y").doubleValue();
		double z = tag.get("z").doubleValue();
		pos.set(x, y, z);
		pitch = tag.get("pitch").doubleValue();
		yaw = tag.get("yaw").doubleValue();
		dof = tag.get("dof").doubleValue();
		fov = tag.get("fov").doubleValue();
		focalOffset = tag.get("focalOffset").doubleValue();
		infDof = tag.get("infDof").byteValue() != 0;
		calcFovTan();
		updateTransform();
	}

	private void calcFovTan() {
		fovTan = 2 * (Math.tan((fov / 360) * Math.PI));
	}

	/**
	 * Set the camera position
	 * @param v
	 */
	public void setPosition(Vector3d v) {
		pos.set(v);
		scene.refresh();
	}

	/**
	 * Set depth of field.
	 * 
	 * @param value
	 */
	public synchronized void setDof(double value) {
		dof = value;
		scene.refresh();
	}
	
	/**
	 * @return Current Depth of Field
	 */
	public double getDof() {
		return dof;
	}
	
	/**
	 * Set infinite Depth of Field
	 * @param value
	 */
	public synchronized void setInfDof(boolean value) {
		if (value != infDof) {
			infDof = value;
			scene.refresh();
		}
	}

	/**
	 * @return <code>true</code> if infinite DoF is active
	 */
	public boolean getInfDof() {
		return infDof;
	}
	
	/**
	 * Set field of view in degrees.
	 * 
	 * @param value
	 */
	public synchronized void setFoV(double value) {
		fov = value;
		calcFovTan();
		scene.refresh();
	}
	
	/**
	 * @return Current field of view
	 */
	public double getFoV() {
		return fov;
	}
	
	/**
	 * Set the focal offset
	 * @param value
	 */
	public synchronized void setFocalOffset(double value) {
		focalOffset = value;
		scene.refresh();
	}
	
	/**
	 * @return Current focal offset
	 */
	public double getFocalOffset() {
		return focalOffset;
	}

	/**
	 * Move camera forward
	 * @param v
	 */
	public synchronized void moveForward(double v) {
		d.set(0, -1, 0);
		transform.transform(d);
		pos.scaleAdd(v, d, pos);
		
		scene.refresh();
	}
	
	/**
	 * Move camera backward
	 * @param v
	 */
	public synchronized void moveBackward(double v) {
		d.set(0, -1, 0);
		transform.transform(d);
		pos.scaleAdd(-v, d, pos);
		
		scene.refresh();
	}
	
	/**
	 * Move camera up
	 * @param v
	 */
	public synchronized void moveUp(double v) {
		pos.scaleAdd(v, up, pos);
		
		scene.refresh();
	}
	
	/**
	 * Move camera down
	 * @param v
	 */
	public synchronized void moveDown(double v) {
		pos.scaleAdd(-v, up, pos);
		
		scene.refresh();
	}
	
	/**
	 * Strafe camera left
	 * @param v
	 */
	public synchronized void strafeLeft(double v) {
		d.set(1, 0, 0);
		tmpTransform.rotY(yaw);
		tmpTransform.transform(d);
		right.cross(up, d);
		pos.scaleAdd(-v, right, pos);
		
		scene.refresh();
	}
	
	/**
	 * Strafe camera right
	 * @param v
	 */
	public synchronized void strafeRight(double v) {
		d.set(1, 0, 0);
		tmpTransform.rotY(yaw);
		tmpTransform.transform(d);
		right.cross(up, d);
		pos.scaleAdd(v, right, pos);
		
		scene.refresh();
	}
	
	/**
	 * Rotate the camera
	 * @param yaw
	 * @param pitch
	 */
	public synchronized void rotateView(double yaw, double pitch) {
		double fovRad = (fov / 360) * Math.PI;
		this.yaw += yaw * fovRad;
		this.pitch += pitch * fovRad;
		
		this.pitch = Math.min(0, this.pitch);
		this.pitch = Math.max(-Math.PI, this.pitch);
		
		if (this.yaw > Math.PI * 2)
			this.yaw -= Math.PI * 2;
		else if (this.yaw < -Math.PI * 2)
			this.yaw += Math.PI * 2;
		
		updateTransform();
	}

	/**
	 * Set the view direction
	 * @param yaw
	 * @param pitch
	 */
	public synchronized void setView(double yaw, double pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
		
		updateTransform();
	}

	/**
	 * Update the camera transformation matrix.
	 */
	synchronized void updateTransform() {
		tmpTransform.rotZ(pitch);
		transform.rotY(yaw);
		transform.mul(tmpTransform);
		
		scene.refresh();
	}

	/**
	 * Attempt to move the camera to the player position.
	 * @param world 
	 */
	public void moveToPlayer(World world) {
		if (world != null && world.havePlayerPos()) {
			pitch = (Math.PI / 2) * ( (world.playerPitch() / 90) - 1);
			yaw = (Math.PI / 2) * ( -(world.playerYaw() / 90) + 1);
			pos.x = world.playerPosX();
			pos.y = world.playerPosY() + 1.6;
			pos.z = world.playerPosZ();
			updateTransform();
			scene.refresh();
		}
	}


	/**
	 * Calculate a ray shooting out of the camera
	 * @param ray
	 * @param d
	 * @param o
	 * @param random
	 * @param aspect
	 * @param x
	 * @param y
	 */
	public void calcViewRay(Ray ray, Vector3d d, Vector3d o, Random random,
			double aspect, double x, double y) {
		
		if (infDof) {
			d.set(fovTan * y, -1, fovTan * aspect * x);
			d.normalize();
			transform.transform(d);
			ray.set(pos, d);
		} else {
			// image plane coordinates flipped to account for
			// the flipped image through lens simulation
			d.set(fovTan * y, -1, aspect * x);
			doLens(random, d, o, - fovTan * y, - aspect * fovTan * x);
			transform.transform(d);
			transform.transform(o);
			o.add(pos);
			ray.set(o, d);
		}
	}
	
	/**
	 * Simple lens simulation
	 * 
	 * @param d
	 * @param x x-coordinate of ray origin
	 * @param z z-coordinate of ray origin
	 */
	private void doLens(Random random, Vector3d d, Vector3d i, double x, double z) {
		d.set(-x, -1, -z);
		d.scale(focalOffset);
		
		// at the focal offset we want the distance to a CoC of X to be equal to dof
		double X = .01;
		double aperture = Math.sqrt((X / dof) * focalOffset);
		
		double rx, rz;
		while (true) {
			rx = 2 * random.nextDouble() - 1;
			rz = 2 * random.nextDouble() - 1;
			double s = rx*rx + rz*rz;
			if (s > Ray.EPSILON && s <= 1) {
				rx *= aperture;
				rz *= aperture;
				break;
			}
		}

		i.set(rx, 0, rz);
		d.sub(i);
		d.normalize();
	}

	/**
	 * Transform vector from camera space to world space
	 * @param d
	 */
	public void transform(Vector3d d) {
		transform.transform(d);
	}

	/**
	 * @return Current position
	 */
	public Vector3d getPosition() {
		return pos;
	}

	/**
	 * @return The current yaw angle
	 */
	public double getYaw() {
		return yaw;
	}
	
	/**
	 * @return The current pitch angle
	 */
	public double getPitch() {
		return pitch;
	}
}
