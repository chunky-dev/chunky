package se.llbit.math.primitive;

import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;

/**
 * A simple triangle primitive.
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Triangle implements Primitive {

	private static final double EPSILON = 0.000001;
	private final Vector3d e1 = new Vector3d(0, 0, 0);
	private final Vector3d e2 = new Vector3d(0, 0, 0);
	private final Vector3d o = new Vector3d(0, 0, 0);
	private final Vector3d n = new Vector3d(0, 0, 0);
	private final AABB bounds;

	/**
	 *
	 * @param c1 first corner
	 * @param c2 second corner
	 * @param c3 third corner
	 */
	public Triangle(Vector3d c1, Vector3d c2, Vector3d c3) {
		e1.sub(c2, c1);
		e2.sub(c3, c1);
		o.set(c1);
		n.cross(e2, e1);
		n.normalize();

		bounds = AABB.bounds(c1, c2, c3);
	}

	@Override
	public boolean intersect(Ray ray) {
		// Möller-Trumbore triangle intersection algorithm!
		double px=0,py=0,pz=0;
		double qx=0,qy=0,qz=0;
		double tx=0,ty=0,tz=0;

		px = ray.d.y * e2.z - ray.d.z * e2.y;
		py = ray.d.z * e2.x - ray.d.x * e2.z;
		pz = ray.d.x * e2.y - ray.d.y * e2.x;
		double det = e1.x*px + e1.y*py + e1.z*pz;
		if (det > -EPSILON && det < EPSILON) {
			return false;
		}
		double recip = 1 / det;

		tx = ray.x.x - o.x;
		ty = ray.x.y - o.y;
		tz = ray.x.z - o.z;

		double u = (tx*px + ty*py + tz*pz) * recip;

		if (u < 0 || u > 1) {
			return false;
		}

		qx = ty * e1.z - tz * e1.y;
		qy = tz * e1.x - tx * e1.z;
		qz = tx * e1.y - ty * e1.x;

		double v = (ray.d.x*qx + ray.d.y*qy + ray.d.z*qz) * recip;

		if (v < 0 || (u+v) > 1) {
			return false;
		}

		double t = (e2.x*qx + e2.y*qy + e2.z*qz) * recip;

		if (t > EPSILON && t < ray.tNear) {
			ray.tNear = t;
			ray.n.set(n);
			return true;
		}
		return false;
	}

	@Override
	public AABB bounds() {
		return bounds;
	}

	public double surfaceArea() {
		// projected on yz plane:
		Vector3d u = new Vector3d(e1);
		Vector3d v = new Vector3d(e2);
		u.scale(v.dot(u));
		v.sub(u);
		return Math.sqrt(v.lengthSquared()*u.lengthSquared())/2;
	}
}
