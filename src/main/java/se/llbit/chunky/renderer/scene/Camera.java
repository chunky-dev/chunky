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
import org.apache.commons.math3.util.FastMath;

import java.util.Random;

import org.apache.log4j.Logger;

import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.World;
import se.llbit.math.Matrix3d;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.nbt.StringTag;

/**
 * Camera model for 3D rendering.
 *
 * The camera space has x as right vector and z as up vector.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 * @author TOGoS (projection code)
 */
public class Camera {

	private static final Logger logger = Logger.getLogger(Camera.class);

	/**
	 * @param fov Field of view, in degrees. Maximum 180.
	 * @return tan(fov/2)
	 */
	public static double clampedFovTan(double fov) {
		double clampedFoV = QuickMath.clamp(fov, 0, 180);
		return 2 * FastMath.tan(QuickMath.degToRad(clampedFoV / 2));
	}

	/**
	 * Projectors project the view ray using different projection models.
	 */
	interface Projector {
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

	/**
	 * Casts parallel rays from different origin points on a plane
	 */
	static class ParallelProjector implements Projector {
		final double worldWidth;
		final double fov;

		public ParallelProjector(double worldWidth, double fov) {
			this.worldWidth = worldWidth;
			this.fov = fov;
		}

		@Override
		public void apply(double x, double y, Random random, Vector3d o,
				Vector3d d) {
			o.set(fov * x, fov * y, 0);
			d.set(0, 0, 1);
		}

		@Override
		public double getMinRecommendedFoV() {
			return 0.01;
		}

		@Override
		public double getMaxRecommendedFoV() {
			return worldWidth;
		}

		@Override
		public double getDefaultFoV() {
			return worldWidth / 2;
		}
	};

	/**
	 * Casts rays like a pinhole camera
	 */
	static class PinholeProjector implements Projector {
		final double fovTan;

		public PinholeProjector(double fov) {
			this.fovTan = clampedFovTan(fov);
		}

		@Override
		public void apply(double x, double y, Random random, Vector3d o,
				Vector3d d) {
			o.set(0, 0, 0);
			d.set(fovTan * x, fovTan * y, 1);
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
			return 70;
		}
	}

	static class FisheyeProjector implements Projector {
		final double fov;

		public FisheyeProjector(double fov) {
			this.fov = fov;
		}

		@Override
		public void apply(double x, double y, Random random, Vector3d o,
				Vector3d d) {
			double ay = y * fov * FastMath.PI / 180;
			double ax = x * fov * FastMath.PI / 180;
			double avSquared = ay * ay + ax * ax;
			double angleFromCenter = FastMath.sqrt(avSquared);
			double dz = FastMath.cos(angleFromCenter);
			double dv = FastMath.sin(angleFromCenter);
			double dy, dx;
			if (angleFromCenter == 0) {
				dx = dy = 0;
			} else {
				dx = dv * (ax / angleFromCenter);
				dy = dv * (ay / angleFromCenter);
			}
			o.set(0, 0, 0);
			d.set(dx, dy, dz);
		}

		@Override
		public double getMinRecommendedFoV() {
			return 1;
		}

		@Override
		public double getMaxRecommendedFoV() {
			return 180;
		}

		@Override
		public double getDefaultFoV() {
			return 120;
		}
	}

	/**
	 * Panoramic equirectangular projector. x is mapped to yaw, y is mapped to
	 * pitch.
	 */
	static class PanoramicProjector implements Projector {
		final double fov;

		public PanoramicProjector(double fov) {
			this.fov = fov;
		}

		@Override
		public void apply(double x, double y, Random random, Vector3d o,
				Vector3d d) {
			double ay = y * fov * FastMath.PI / 180;
			double ax = x * fov * FastMath.PI / 180;

			double vv = FastMath.cos(ay);

			o.set(0, 0, 0);
			d.set(vv * FastMath.sin(ax), FastMath.sin(ay), vv * FastMath.cos(ax));
		}

		@Override
		public double getMinRecommendedFoV() {
			return 1;
		}

		@Override
		public double getMaxRecommendedFoV() {
			return 180;
		}

		@Override
		public double getDefaultFoV() {
			return 120;
		}
	}

	/**
	 * Behaves like a pinhole camera in the vertical direction, but like a
	 * spherical one in the horizontal direction.
	 */
	static class PanoramicSlotProjector implements Projector {
		final double fov;
		final double fovTan;

		public PanoramicSlotProjector(double fov) {
			this.fov = fov;
			this.fovTan = clampedFovTan(fov);
		}

		@Override
		public void apply(double x, double y, Random random, Vector3d o,
				Vector3d d) {
			double ax = x * fov * FastMath.PI / 180;
			double dz = FastMath.cos(ax);
			double dx = FastMath.sin(ax);
			double dy = fovTan * y;

			o.set(0, 0, 0);
			d.set(dx, dy, dz);
		}

		@Override
		public double getMinRecommendedFoV() {
			return 1;
		}

		@Override
		public double getMaxRecommendedFoV() {
			return 90;
		}

		@Override
		public double getDefaultFoV() {
			return 90;
		}
	}

	/**
	 * Simulates a non-point aperture to produce a depth-of-field effect.
	 * Delegates calculation of base offset/direction to another projector. If
	 * apertureSize is 0 this will still work, but it will not have any effect.
	 * In that case you should use the wrapped Projector directly.
	 */
	static class ApertureProjector implements Projector {
		final Projector wrapped;
		final double aperture;
		final double subjectDistance;

		public ApertureProjector(Projector wrapped, double apertureSize,
				double subjectDistance) {
			this.wrapped = wrapped;
			this.aperture = apertureSize;
			this.subjectDistance = subjectDistance;
		}

		@Override
		public void apply(double x, double y, Random random, Vector3d o,
				Vector3d d) {
			wrapped.apply(x, y, random, o, d);

			d.scale(subjectDistance/d.z);

			// find random point in aperture
			double rx, ry;
			while (true) {
				rx = 2 * random.nextDouble() - 1;
				ry = 2 * random.nextDouble() - 1;
				double s = rx * rx + ry * ry;
				if (s > Ray.EPSILON && s <= 1) {
					rx *= aperture;
					ry *= aperture;
					break;
				}
			}

			d.sub(rx, ry, 0);
			o.add(rx, ry, 0);
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

	/**
	 * Moves the ray origin forward (if displacement is positive) along the
	 * direction vector.
	 */
	static class ForwardDisplacementProjector implements Projector {
		final Projector wrapped;
		final double displacementValue;
		final double displacementSign;

		public ForwardDisplacementProjector(Projector wrapped,
				double displacement) {
			this.wrapped = wrapped;
			this.displacementValue = FastMath.abs(displacement);
			this.displacementSign = FastMath.signum(displacement);
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

	/**
	 * Projection mode enumeration
	 */
	@SuppressWarnings("javadoc")
	public enum ProjectionMode {
		PINHOLE("Standard"),
		PARALLEL("Parallel"),
		FISHEYE("Fisheye"),
		PANORAMIC("Panoramic (equirectangular)"),
		PANORAMIC_SLOT("Panoramic (slot)");

		private final String niceName;

		private ProjectionMode(String niceName) {
			this.niceName = niceName;
		}

		@Override
		public String toString() {
			return niceName;
		}
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

	private double yaw = - FastMath.PI / 2;
	private double pitch = 0;

	/**
	 * Transform to rotate from camera space to world space (not including
	 * translation).
	 */
	private final Matrix3d transform = new Matrix3d();

	private final Matrix3d tmpTransform = new Matrix3d();

	private ProjectionMode projectionMode = ProjectionMode.PINHOLE;
	private Projector projector = createProjector();

	private double dof = 8;
	private double fov = projector.getDefaultFoV();

	/**
	 * Maximum diagonal width of the world
	 */
	private double worldWidth = 100;

	private double subjectDistance = 2;
	private boolean infDof = true;

	/**
	 * Create a new camera
	 * @param scene The scene which the camera should be attached to
	 */
	public Camera(Refreshable scene) {
		this.scene = scene;
		transform.setIdentity();
		initProjector();
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
		projectionMode = other.projectionMode;
		fov = other.fov;
		subjectDistance = other.subjectDistance;
		infDof = other.infDof;
		worldWidth = other.worldWidth;
		initProjector();
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
		camera.addItem("projectionMode", new StringTag(projectionMode.name()));
		camera.addItem("fov", new DoubleTag(fov));
		camera.addItem("dof", new DoubleTag(dof));
		camera.addItem("infDof", new ByteTag(infDof ? 1 : 0));
		camera.addItem("focalOffset", new DoubleTag(subjectDistance));
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
		subjectDistance = tag.get("focalOffset").doubleValue();
		try {
			projectionMode = ProjectionMode.valueOf(
					tag.get("projectionMode").stringValue());
		} catch (IllegalArgumentException e) {
			projectionMode = tag.get("parallel").byteValue() != 0 ?
					ProjectionMode.PARALLEL : ProjectionMode.PINHOLE;
		}
		infDof = tag.get("infDof").byteValue() != 0;
		initProjector();
		updateTransform();
	}

	private Projector applyDoF(Projector p, double subjectDistance) {
		return infDof ? p : new ApertureProjector(p,
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
			return applyDoF(new FisheyeProjector(fov), subjectDistance);
		case PANORAMIC_SLOT:
			return applyDoF(new PanoramicSlotProjector(fov), subjectDistance);
		case PANORAMIC:
			return applyDoF(new PanoramicProjector(fov), subjectDistance);
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
		double fovRad = (fov / 360) * FastMath.PI;
		this.yaw += yaw * fovRad;
		this.pitch += pitch * fovRad;

		this.pitch = FastMath.min(0, this.pitch);
		this.pitch = FastMath.max(-Math.PI, this.pitch);

		if (this.yaw > FastMath.PI * 2) {
			this.yaw -= FastMath.PI * 2;
		} else if (this.yaw < -Math.PI * 2) {
			this.yaw += FastMath.PI * 2;
		}

		updateTransform();
	}

	/**
	 * Set the view direction
	 * @param yaw Yaw in radians
	 * @param pitch Pitch in radians
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
		tmpTransform.rotX(FastMath.PI/2 - pitch);
		transform.rotY(FastMath.PI/2 + yaw);
		transform.mul(tmpTransform);

		scene.refresh();
	}

	/**
	 * Attempt to move the camera to the player position.
	 * @param world
	 */
	public void moveToPlayer(World world) {
		if (world != null && world.havePlayerPos()) {
			pitch = (FastMath.PI / 2) * ( (world.playerPitch() / 90) - 1);
			yaw = (FastMath.PI / 2) * ( -(world.playerYaw() / 90) + 1);
			pos.x = world.playerPosX();
			pos.y = world.playerPosY() + 1.6;
			pos.z = world.playerPosZ();
			updateTransform();
			scene.refresh();
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
	 * @param size World size
	 */
	public void setWorldSize(double size) {
		worldWidth = FastMath.sqrt(size*size + Chunk.Y_MAX*Chunk.Y_MAX);
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
}
