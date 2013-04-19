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
package se.llbit.chunky.renderer.scene;

import java.util.Random;

import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.World;
import se.llbit.math.Matrix3d;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.nbt.StringTag;

/**
 * Camera model for 3D rendering
 * @author Jesper Öqvist <jesper@llbit.se>
 * @author TOGoS (projection code) 
 */
public class Camera
{
	static double clamp( double min, double v, double max ) {
		return v < min ? min : v > max ? max : v;
	}
	
	static double fovTan( double fov ) {
		if( fov < 0 || fov >= 180 ) {
			throw new RuntimeException("FoV should have been clamped between [0,180), but is outside that range: "+fov);
		}
		return 2 * (Math.tan((fov / 360) * Math.PI));
	}
	
	static double clampedFovTan( double fov ) {
		return fovTan( clamp(0, fov, 175) );
	}
	
	//// Projectors ////
	
	interface Projector {
		/**
		 * @param x pixel X coordinate, where 0 = center and +-0.5 = edges
		 * @param y pixel Y coordinate, where 0 = center and +-0.5 = edges
		 * @param pos will be populated with camera-relative ray origin position
		 * @param direction will be populated with camera-relative ray direction (not necessarily normalized)
		 */
		public void apply( double x, double y, Random random, Vector3d pos, Vector3d direction );
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
		
		public ParallelProjector( double worldWidth, double fov ) {
			this.worldWidth = worldWidth;
			this.fov = fov;
		}
		
		@Override
		public void apply(
			double x, double y, Random random, Vector3d o, Vector3d d
		) {
			d.set( 0, -1, 0 );
			o.set( fov * y, 0, fov * x );
		}
		
		public double getMinRecommendedFoV() {  return 0.01;  }
		public double getMaxRecommendedFoV() {  return worldWidth;  }
		public double getDefaultFoV() {  return worldWidth/2;  }
	};
	
	/**
	 * Casts rays like a pinhole camera
	 */
	static class PinholeProjector implements Projector {
		final double fovTan;
		public PinholeProjector( double fov ) {
			this.fovTan = clampedFovTan(fov);
		}
		
		public void apply( double x, double y, Random random, Vector3d pos, Vector3d direction ) {
			pos.set(0, 0, 0);
			direction.set(fovTan * y, -1, fovTan * x);
		}
		
		public double getMinRecommendedFoV() {   return 1; }
		public double getMaxRecommendedFoV() { return 175; }
		public double getDefaultFoV() { return 70; }
	}
	
	static class FisheyeProjector implements Projector {
		final double fov;
		public FisheyeProjector( double fov ) {
			this.fov = fov;
		}
		
        public void apply( double x, double y, Random random, Vector3d pos, Vector3d direction ) {
			double ay = y * fov * Math.PI / 180;
			double ax = x * fov * Math.PI / 180;
			double avSquared = ay*ay + ax*ax;
			double angleFromCenter = Math.sqrt(avSquared);
			double dz = Math.cos(angleFromCenter);
			double dv = Math.sin(angleFromCenter);
			double dy, dx;
			if( angleFromCenter == 0 ) {
				dx = dy = 0;
			} else {
				dx = dv * (ax/angleFromCenter);
				dy = dv * (ay/angleFromCenter);
			}
			pos.set( 0, 0, 0 );
			direction.set( dy, -dz, dx );
        }
		
		public double getMinRecommendedFoV() { return   1; }
		public double getMaxRecommendedFoV() { return 180; }
		public double getDefaultFoV() { return 120; }
	}
	
	/**
	 * Panoramic equirectangular projector.
	 * x is mapped to yaw, y is mapped to pitch.
	 */
	static class PanoramicProjector implements Projector {
		final double fov;
		public PanoramicProjector( double fov ) {
			this.fov = fov;
		}
		
        public void apply( double x, double y, Random random, Vector3d pos, Vector3d direction ) {
			double ay = y * fov * Math.PI / 180;
			double ax = x * fov * Math.PI / 180;
			
			double vv = Math.cos(ay);
			
        	pos.set( 0, 0, 0 );
        	direction.set( Math.sin(ay), -vv*Math.cos(ax), vv*Math.sin(ax) );
        }
		
		public double getMinRecommendedFoV() { return   1; }
		public double getMaxRecommendedFoV() { return 180; }
		public double getDefaultFoV() { return 120; }
	}

	/**
	 * Behaves like a pinhole camera in the vertical direction,
	 * but like a spherical one in the horizontal direction.
	 */
	static class PanoramicSlotProjector implements Projector {
		final double fov;
		final double fovTan;
		public PanoramicSlotProjector( double fov ) {
			this.fov = fov;
			this.fovTan = clampedFovTan(fov);
		}
		
		@Override
		public void apply( double x, double y, Random random, Vector3d position, Vector3d direction ) {
			double ax = x * fov * Math.PI / 180;
			double dz = Math.cos(ax);
			double dx = Math.sin(ax);
			double dy = fovTan * y;
			
			position.set( 0, 0, 0 );
			direction.set( dy, -dz, dx );
		}
		
		public double getMinRecommendedFoV() { return 1;  }
		public double getMaxRecommendedFoV() { return 90; }
		public double getDefaultFoV() { return 90; }
	}
	
	/**
	 * Simulates a non-point aperture to produce depth-of-focus effects.
	 * Delegates calculation of base offset/direction to another projector.
	 * If apertureSize is 0 this will still work, but it will not have any
	 * effect.  In that case you should use the wrapped Projector directly.
	 */
	static class ApertureProjector implements Projector {
		final Projector wrapped;
		final double apertureSize;
		final double focalOffset;
		
		ThreadLocal<Vector3d> apertureScrachVectorVar = new ThreadLocal<Vector3d>() {
			@Override protected Vector3d initialValue() {
				return new Vector3d();
			}
		};
		
		public ApertureProjector( Projector wrapped, double apertureSize, double focalOffset ) {
			this.wrapped = wrapped;
			this.apertureSize = apertureSize;
			this.focalOffset = focalOffset;
		}
		
		/**
		 * Find a random point within a circular aperture and put it in dest.x, z
		 */
		protected static void randomAperturePoint( Random random, double apertureSize, Vector3d dest ) {
			double rx, rz;
			while (true) {
				rx = 2 * random.nextDouble() - 1;
				rz = 2 * random.nextDouble() - 1;
				double s = rx*rx + rz*rz;
				if (s > Ray.EPSILON && s <= 1) {
					dest.set( rx *= apertureSize, 0, rz *= apertureSize );
					return;
				}
			}
		}
		
		public void apply(
			double x, double y, Random random, Vector3d o, Vector3d d
		) {
			wrapped.apply( x, y, random, o, d );
			d.normalize();
			
			Vector3d apertureScratchVector = apertureScrachVectorVar.get();
			
			randomAperturePoint( random, apertureSize, apertureScratchVector );
			o.add( apertureScrachVectorVar.get() ); // Shift ray origin
			
			d.scale( focalOffset );
			d.sub( apertureScratchVector ); // Change direction of d to compensate for origin shift
		}
		
		public double getMinRecommendedFoV() { return wrapped.getMinRecommendedFoV(); }
		public double getMaxRecommendedFoV() { return wrapped.getMaxRecommendedFoV(); }
		public double getDefaultFoV() { return wrapped.getDefaultFoV(); }
	}
	
	/**
	 * Moves the ray origin forward (if displacement is positive) along
	 * the direction vector.
	 */
	static class ForwardDisplacementProjector implements Projector {
		final Projector wrapped;
		final double displacement;
		
		public ForwardDisplacementProjector( Projector wrapped, double displacement ) {
			this.wrapped = wrapped;
			this.displacement = displacement;
		}
		
		public void apply(
			double x, double y, Random random, Vector3d o, Vector3d d
		) {
			wrapped.apply( x, y, random, o, d );

			// Rather than use a separate scratch vector, scale d
			// and then revert it (if it's been reversed) when done
			
			d.normalize();
			d.scale( displacement );
			o.add( d );
			
			if( displacement < 0 ) d.scale( -1 );
		}
		
		public double getMinRecommendedFoV() { return wrapped.getMinRecommendedFoV(); }
		public double getMaxRecommendedFoV() { return wrapped.getMaxRecommendedFoV(); }
		public double getDefaultFoV() { return wrapped.getDefaultFoV(); }
	}

	////
	
	public enum ProjectionMode {
		PARALLEL("Parallel"),
		PINHOLE("Pinhole"),
		FISHEYE("Fisheye"),
		PANORAMIC("Panoramic (equirectangular)"),
		PANORAMIC_SLOT("Panoramic (slot)");
		
		public final String niceName;
		private ProjectionMode( String niceName ) {
			this.niceName = niceName;
		}
	}
	
	////
	
	/**
	 * Minimum DoF
	 */
	public static final double MIN_DOF = .5;

	/**
	 * Maximum DoF
	 */
	public static final double MAX_DOF = 500;

	/**
	 * Minimum Focal Offset
	 */
	public static final double MIN_FOCAL_OFFSET = 0.01;

	/**
	 * Maximum Focal Offset
	 */
	public static final double MAX_FOCAL_OFFSET = 1000;

	private final Refreshable scene;

	Vector3d pos = new Vector3d(0, 0, 0);
	private Vector3d up = new Vector3d(0, 1, 0);

	/**
	 * Scratch vector
	 */
	private Vector3d right = new Vector3d();

	/**
	 * Scratch vector
	 */
	private Vector3d d = new Vector3d();

	/**
	 * Scratch vector
	 */
	private Vector3d u = new Vector3d();

	private double yaw = - Math.PI / 2;
	private double pitch = 0;
	private Matrix3d transform = new Matrix3d();
	private Matrix3d tmpTransform = new Matrix3d();

	private ProjectionMode projectionMode = ProjectionMode.PINHOLE;
	private Projector projector = createProjector();

	private double dof = 8;
	private double fov = projector.getDefaultFoV();

	/**
	 * Maximum diagonal width of the world
	 */
	private double worldWidth = 100;

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
		focalOffset = other.focalOffset;
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
		try {
			projectionMode = ProjectionMode.valueOf( tag.get("projectionMode").stringValue() );
		} catch( IllegalArgumentException e ) {
			projectionMode = tag.get("parallel").byteValue() != 0 ? ProjectionMode.PARALLEL : ProjectionMode.PINHOLE;
		}
		infDof = tag.get("infDof").byteValue() != 0;
		initProjector();
		updateTransform();
	}

	private Projector applyDoF( Projector p ) {
		return infDof ? p : new ApertureProjector(p, focalOffset/dof, focalOffset);
	}
	
	/**
	 * Creates, but does not otherwise use, a projector object
	 * based on the current camera settings. 
	 */
	private Projector createProjector() {
		switch (projectionMode) {
		case PARALLEL:
			return new ForwardDisplacementProjector( applyDoF( new ParallelProjector( worldWidth, fov ) ), -worldWidth );
		case PINHOLE:
			return applyDoF( new PinholeProjector(fov) );
		case FISHEYE:
			return applyDoF( new FisheyeProjector(fov) );
		case PANORAMIC_SLOT:
			return applyDoF( new PanoramicSlotProjector(fov) );
		case PANORAMIC:
			return applyDoF( new PanoramicProjector(fov) );
		default:
			System.err.println("Error: Undefined projection mode: "+projectionMode+", defaulting to planar");
			return applyDoF( new PinholeProjector(fov) );
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
		if (projectionMode != ProjectionMode.PARALLEL) {
			d.set(0, -1, 0);
			transform.transform(d);
			pos.scaleAdd(v, d, pos);
		} else {
			d.set(1, 0, 0);
			tmpTransform.rotY(yaw);
			tmpTransform.transform(d);
			right.cross(up, d);
			d.set(0, -1, 0);
			transform.transform(d);
			u.cross(right, d);
			pos.scaleAdd(v, u, pos);
		}
		scene.refresh();
	}

	/**
	 * Move camera backward
	 * @param v
	 */
	public synchronized void moveBackward(double v) {
		if (projectionMode != ProjectionMode.PARALLEL) {
			d.set(0, -1, 0);
			transform.transform(d);
			pos.scaleAdd(-v, d, pos);
		} else {
			d.set(1, 0, 0);
			tmpTransform.rotY(yaw);
			tmpTransform.transform(d);
			right.cross(up, d);
			d.set(0, -1, 0);
			transform.transform(d);
			u.cross(right, d);
			pos.scaleAdd(-v, u, pos);
		}
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
	 * Calculate a ray shooting out of the camera.
	 *
	 * @param ray destination
	 * @param d scratch vector
	 * @param o scratch vector
	 * @param random random number generator to use for whatever
	 * @param aspect width / height of output image
	 * @param x point within the output image, from -0.5, to 0.5
	 * @param y point within the output image, from -0.5, to 0.5
	 */
	public void calcViewRay(
		Ray ray, Random random, double aspect, double x, double y
	) {
		projector.apply( x * aspect, y, random, ray.x, ray.d );
		
		ray.d.normalize();
		transform.transform(ray.d);
		transform.transform(ray.x);
		ray.x.add(pos);
		
		// Even though we've implicitly set ray.d and x, we need to
		// call ray.set(...) to reset its other values.
		ray.set(ray.x, ray.d);
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

	/**
	 * @param size World size
	 */
	public void setWorldSize(double size) {
		worldWidth = Math.sqrt(size*size + Chunk.Y_MAX*Chunk.Y_MAX);
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
