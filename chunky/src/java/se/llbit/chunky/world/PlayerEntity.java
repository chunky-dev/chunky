package se.llbit.chunky.world;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector3i;

public class PlayerEntity extends Entity {

	public PlayerEntity(Vector3d position) {
		super(position);
		System.out.println("player pos " + position);
	}

	@Override
	public boolean intersect(Scene scene, Ray ray) {
		double t1, t2;
		double tNear = Double.NEGATIVE_INFINITY;
		double tFar = Double.POSITIVE_INFINITY;
		Vector3d d = ray.d;
		Vector3d o = ray.x;
		double nx = 0;
		double ny = 0;
		double nz = 0;

		Vector3i wo = scene.getOrigin();

		double x0 = (position.x - wo.x) - .5;
		double x1 = (position.x - wo.x) + .5;

		if (d.x != 0) {
			double rx = 1 / d.x;
			t1 = (x0 - o.x) * rx;
			t2 = (x1 - o.x) * rx;

			int sign = -1;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
				sign = 1;
			}

			tNear = t1;
			nx = sign;
			ny = nz = 0;
			tFar = t2;
		}

		double y0 = (position.y - wo.y) - .5;
		double y1 = (position.y - wo.y) + .5;

		if (d.y != 0) {
			double ry = 1 / d.y;
			t1 = (y0 - o.y) * ry;
			t2 = (y1 - o.y) * ry;

			int sign = -1;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
				sign = 1;
			}

			if (t1 > tNear) {
				tNear = t1;
				ny = sign;
				nx = nz = 0;
			}
			if (t2 < tFar) {
				tFar = t2;
			}
		}

		double z0 = (position.z - wo.z) - .5;
		double z1 = (position.z - wo.z) + .5;

		if (d.z != 0) {
			double rz = 1 / d.z;
			t1 = (z0 - o.z) * rz;
			t2 = (z1 - o.z) * rz;

			int sign = -1;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
				sign = 1;
			}

			if (t1 > tNear) {
				tNear = t1;
				nz = sign;
				nx = ny = 0;
			}
			if (t2 < tFar) {
				tFar = t2;
			}
		}

		if (tNear < tFar + Ray.EPSILON && tNear >= 0) {
			if (tNear < ray.tNear) {
				ray.tNear = tNear;
				ray.currentMaterial = Block.STONE_ID;
				ray.color.set(1, 0, 0, 1);
				ray.n.set(nx, ny, nz);
				return true;
			}
		}
		return false;
	}
}
