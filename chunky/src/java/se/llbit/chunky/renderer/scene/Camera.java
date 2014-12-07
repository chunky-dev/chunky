/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene;

import java.util.Random;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.renderer.projection.ApertureProjector;
import se.llbit.chunky.renderer.projection.FisheyeProjector;
import se.llbit.chunky.renderer.projection.ForwardDisplacementProjector;
import se.llbit.chunky.renderer.projection.PanoramicProjector;
import se.llbit.chunky.renderer.projection.PanoramicSlotProjector;
import se.llbit.chunky.renderer.projection.ParallelProjector;
import se.llbit.chunky.renderer.projection.PinholeProjector;
import se.llbit.chunky.renderer.projection.Projector;
import se.llbit.chunky.renderer.projection.SphericalApertureProjector;
import se.llbit.chunky.renderer.projection.StereographicProjector;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.World;
import se.llbit.json.JsonObject;
import se.llbit.math.Matrix3d;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.util.JSONifiable;

/**
 * Camera model for 3D rendering.
 *
 * The camera space has x as right vector and z as up vector.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 * @author TOGoS (projection code)
 */
public class Camera implements JSONifiable {

	private static final Logger logger = Logger.getLogger(Camera.class);

	private static final double HALF_PI = Math.PI/2;
	private static final double TWO_PI = Math.PI*2;

	/**
	 * @param fov Field of view, in degrees. Maximum 180.
	 * @return tan(fov/2)
	 */
	public static double clampedFovTan(double fov) {
		double clampedFoV = Math.max(0, Math.min(180, fov));
		return 2 * FastMath.tan(QuickMath.degToRad(clampedFoV / 2));
	}

	/**
	 * Minimum DoF
	 */
	public static final double MIN_DOF = .5;

	/**
	 * Maximum DoF
	 */
	public static final double MAX_DOF = 5000;

	/**
	 * Minimum recommended subject distance
	 */
	public static final double MIN_SUBJECT_DISTANCE = 0.01;

	/**
	 * Maximum recommended subject distance
	 */
	public static final double MAX_SUBJECT_DISTANCE = 1000;

	private final Refreshable scene;

	Vector3d pos = new Vector3d(0, 0, 0);

	/**
	 * Scratch vector
	 * NB: protected by synchronized methods (no concurrent modification)
	 */
	private final Vector3d u = new Vector3d();

	/**
	 * Yaw angle. Down = 0, forward = -PI/2, up = -PI.
	 */
	private double yaw = - HALF_PI;

	/**
	 * Pitch angle. Pitch = 0 corresponds to the camera pointing along the z axis,
	 * pitch = PI/2 corresponds to the negative x axis, etc.
	 */
	private double pitch = 0;

	/**
	 * Camera roll.
	 */
	private double roll = 0;

	/**
	 * Transform to rotate from camera space to world space (not including
	 * translation).
	 */
	private final Matrix3d transform = new Matrix3d();

	private final Matrix3d tmpTransform = new Matrix3d();

	private ProjectionMode projectionMode = ProjectionMode.PINHOLE;
	private Projector projector = createProjector();

	private double dof = Double.POSITIVE_INFINITY;
	private double fov = projector.getDefaultFoV();

	/**
	 * Maximum diagonal width of the world. Recalculated when world is loaded.
	 */
	private double worldWidth = 100;

	private double subjectDistance = 2;

	/**
	 * Create a new camera
	 * @param sceneDescription The scene which the camera should be attached to
	 */
	public Camera(Refreshable sceneDescription) {
		this.scene = sceneDescription;
		transform.setIdentity();
		initProjector();
		updateTransform();
	}

	/**
	 * Copy camera configuration from another camera
	 * @param other the camera to copy configuration from
	 */
	public void set(Camera other) {
		pos.set(other.pos);
		yaw = other.yaw;
		pitch = other.pitch;
		roll = other.roll;
		dof = other.dof;
		projectionMode = other.projectionMode;
		fov = other.fov;
		subjectDistance = other.subjectDistance;
		worldWidth = other.worldWidth;
		initProjector();
		updateTransform();
	}

	private Projector applyDoF(Projector p, double subjectDistance) {
		return infiniteDoF() ? p : new ApertureProjector(p,
				subjectDistance/dof, subjectDistance);
	}

	private Projector applySphericalDoF(Projector p) {
		return infiniteDoF() ? p : new SphericalApertureProjector(p,
				subjectDistance/dof, subjectDistance);
	}

	/**
	 * Creates, but does not otherwise use, a projector object
	 * based on the current camera settings.
	 */
	private Projector createProjector() {
		switch (projectionMode) {
		default:
			logger.error("Unknown projection mode: "
					+ projectionMode + ", using standard mode");
		case PINHOLE:
			return applyDoF(new PinholeProjector(fov), subjectDistance);
		case PARALLEL:
			return applyDoF(new ForwardDisplacementProjector(
					new ParallelProjector(worldWidth, fov),
					-worldWidth), subjectDistance+worldWidth);
		case FISHEYE:
			return applySphericalDoF(new FisheyeProjector(fov));
		case PANORAMIC_SLOT:
			return applySphericalDoF(new PanoramicSlotProjector(fov));
		case PANORAMIC:
			return applySphericalDoF(new PanoramicProjector(fov));
		case STEREOGRAPHIC:
			return new StereographicProjector();
		}
	}

	private void initProjector() {
		projector = createProjector();
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
		if (dof != value) {
			dof = value;
			scene.refresh();
		}
	}

	/**
	 * @return Current Depth of Field
	 */
	public double getDof() {
		return dof;
	}

	/**
	 * @return <code>true</code> if infinite DoF is active
	 */
	public boolean infiniteDoF() {
		return dof == Double.POSITIVE_INFINITY;
	}

	/**
	 * @return the projection mode
	 */
	public ProjectionMode getProjectionMode() {
		return projectionMode;
	}

	/**
	 * Set the projection mode
	 * @param mode
	 */
	public synchronized void setProjectionMode(ProjectionMode mode) {
		if (projectionMode != mode) {
			projectionMode = mode;
			initProjector();
			fov = projector.getDefaultFoV();
			scene.refresh();
		}
	}

	/**
	 * Set field of view in degrees.
	 *
	 * @param value
	 */
	public synchronized void setFoV(double value) {
		fov = value;
		initProjector();
		scene.refresh();
	}

	/**
	 * @return Current field of view
	 */
	public double getFoV() {
		return fov;
	}

	/**
	 * Set the subject distance
	 * @param value
	 */
	public synchronized void setSubjectDistance(double value) {
		subjectDistance = value;
		scene.refresh();
	}

	/**
	 * @return Current subject distance
	 */
	public double getSubjectDistance() {
		return subjectDistance;
	}

	/**
	 * Move camera forward
	 * @param v
	 */
	public synchronized void moveForward(double v) {
		if (projectionMode != ProjectionMode.PARALLEL) {
			u.set(0, 0, 1);
		} else {
			u.set(0, -1, 0);
		}
		transform.transform(u);
		pos.scaleAdd(v, u, pos);
		scene.refresh();
	}

	/**
	 * Move camera backward
	 * @param v
	 */
	public synchronized void moveBackward(double v) {
		if (projectionMode != ProjectionMode.PARALLEL) {
			u.set(0, 0, 1);
		} else {
			u.set(0, -1, 0);
		}
		transform.transform(u);
		pos.scaleAdd(-v, u, pos);
		scene.refresh();
	}

	/**
	 * Move camera up
	 * @param v
	 */
	public synchronized void moveUp(double v) {
		u.set(0, 1, 0);
		pos.scaleAdd(v, u, pos);
		scene.refresh();
	}

	/**
	 * Move camera down
	 * @param v
	 */
	public synchronized void moveDown(double v) {
		u.set(0, 1, 0);
		pos.scaleAdd(-v, u, pos);
		scene.refresh();
	}

	/**
	 * Strafe camera left
	 * @param v
	 */
	public synchronized void strafeLeft(double v) {
		u.set(1, 0, 0);
		transform.transform(u);
		pos.scaleAdd(-v, u, pos);
		scene.refresh();
	}

	/**
	 * Strafe camera right
	 * @param v
	 */
	public synchronized void strafeRight(double v) {
		u.set(1, 0, 0);
		transform.transform(u);
		pos.scaleAdd(v, u, pos);
		scene.refresh();
	}

	/**
	 * Rotate the camera
	 * @param yaw
	 * @param pitch
	 */
	public synchronized void rotateView(double yaw, double pitch) {
		double fovRad = QuickMath.degToRad(fov / 2);
		this.yaw += yaw * fovRad;
		this.pitch += pitch * fovRad;

		this.pitch = QuickMath.min(0, this.pitch);
		this.pitch = QuickMath.max(-Math.PI, this.pitch);

		if (this.yaw > TWO_PI) {
			this.yaw -= TWO_PI;
		} else if (this.yaw < -TWO_PI) {
			this.yaw += TWO_PI;
		}

		updateTransform();
	}

	/**
	 * Set the view direction
	 * @param yaw Yaw in radians
	 * @param pitch Pitch in radians
	 * @param roll Roll in radians
	 */
	public synchronized void setView(double yaw, double pitch, double roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;

		updateTransform();
	}

	/**
	 * Update the camera transformation matrix.
	 */
	synchronized void updateTransform() {
		transform.setIdentity();

		// yaw (y axis rotation)
		tmpTransform.rotY(HALF_PI + yaw);
		transform.mul(tmpTransform);

		// pitch (x axis rotation)
		tmpTransform.rotX(HALF_PI - pitch);
		transform.mul(tmpTransform);

		// roll (z axis rotation)
		tmpTransform.rotZ(roll);
		transform.mul(tmpTransform);

		scene.refresh();
	}

	/**
	 * Attempt to move the camera to the player position.
	 * @param world
	 */
	public void moveToPlayer(World world) {
		if (world != null) {
			Vector3d playerPos = world.playerPos();
			if (playerPos != null) {
				pitch = HALF_PI * ( (world.playerPitch() / 90) - 1);
				yaw = HALF_PI * ( -(world.playerYaw() / 90) + 1);
				roll = 0;
				pos.x = playerPos.x;
				pos.y = playerPos.y + 1.6;
				pos.z = playerPos.z;
				updateTransform();
				scene.refresh();
			}
		}
	}

	/**
	 * Calculate a ray shooting out of the camera based on normalized
	 * image coordinates.
	 * @param ray result ray
	 * @param random random number stream
	 * @param x normalized image coordinate [-0.5, 0.5]
	 * @param y normalized image coordinate [-0.5, 0.5]
	 */
	public void calcViewRay(Ray ray, Random random, double x,
			double y) {

		// reset the ray properties - current material etc.
		ray.setDefault();

		projector.apply(x, y, random, ray.x, ray.d);

		ray.d.normalize();

		// from camera space to world space
		transform.transform(ray.d);
		transform.transform(ray.x);
		ray.x.add(pos);
	}

	/**
	 * Rotate vector from camera space to world space (does not translate
	 * the vector)
	 * @param d Vector to rotate
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

	/**
	 * @return The current roll angle
	 */
	public double getRoll() {
		return roll;
	}

	/**
	 * @param size World size
	 */
	public void setWorldSize(double size) {
		worldWidth = 2*Math.sqrt(2*size*size + Chunk.Y_MAX*Chunk.Y_MAX);
	}

	/**
	 * @return Minimum FoV value, depending on projection
	 */
	public double getMinFoV() {
		return projector.getMinRecommendedFoV();
	}

	/**
	 * @return Maximum FoV value, depending on projection
	 */
	public double getMaxFoV() {
		return projector.getMaxRecommendedFoV();
	}

	@Override
	public JsonObject toJson() {
		JsonObject camera = new JsonObject();

		camera.add("position", pos.toJson());

		JsonObject orientation = new JsonObject();
		orientation.add("roll", roll);
		orientation.add("pitch", pitch);
		orientation.add("yaw", yaw);
		camera.add("orientation", orientation);

		camera.add("projectionMode", projectionMode.name());
		camera.add("fov", fov);
		if (dof == Double.POSITIVE_INFINITY) {
			camera.add("dof", "Infinity");
		} else {
			camera.add("dof", dof);
		}
		camera.add("focalOffset", subjectDistance);
		return camera;
	}

	@Override
	public void fromJson(JsonObject obj) {
		pos.fromJson(obj.get("position").object());

		JsonObject orientation = obj.get("orientation").object();
		roll = orientation.get("roll").doubleValue(0);
		pitch = orientation.get("pitch").doubleValue(0);
		yaw = orientation.get("yaw").doubleValue(- HALF_PI);

		fov = obj.get("fov").doubleValue(0);
		subjectDistance = obj.get("focalOffset").doubleValue(0);
		try {
			projectionMode = ProjectionMode.fromJson(obj.get("projectionMode"));
		} catch (IllegalArgumentException e) {
			projectionMode = ProjectionMode.PINHOLE;
		}
		if (obj.get("infDof").boolValue(false)) {
			// legacy
			dof = Double.POSITIVE_INFINITY;
		} else {
			dof = obj.get("dof").doubleValue(Double.POSITIVE_INFINITY);
		}
		initProjector();
		updateTransform();
	}
}
