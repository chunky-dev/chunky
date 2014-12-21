/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;

/**
 * Work in progress.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkIndex {

	int[] blocks = new int[Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX];

	private final int blockId = 3;

	public ChunkIndex() {
		for (int i = 0; i < Chunk.X_MAX; ++i) {
			for (int j = 0; j < Chunk.Y_MAX; ++i) {
				for (int k = 0; k < Chunk.Z_MAX; ++k) {
					int index = i*Chunk.Y_MAX*Chunk.Z_MAX + j*Chunk.Z_MAX + k;
					blocks[index] = index;
				}
			}
		}
	}

	public boolean intersect(Scene scene, Ray ray) {
		double[] nearfar = new double[2];
		enterBlock(ray, nearfar);
		double tNear = nearfar[0];
		double tFar = nearfar[1];

		ray.color.set(1, 1, 1, 1);

		if (tNear <= tFar && tFar >= 0) {
			ray.o.scaleAdd(tNear, ray.d);
			ray.distance += tNear;

			ray.setPrevMat(Block.AIR, 0);
			ray.setMat(blockId);
			return Block.get(blockId).intersect(ray, scene);
		}
		return false;
	}

	private void enterBlock(Ray ray, double[] nearfar) {
		int level = 0;
		double t1, t2;
		double tNear = Double.NEGATIVE_INFINITY;
		double tFar = Double.POSITIVE_INFINITY;
		Vector3d d = ray.d;
		Vector3d o = ray.o;

		if (d.x != 0) {
			t1 = -o.x / d.x;
			t2 = ((1<<level) - o.x) / d.x;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
			}

			if (t1 > tNear) tNear = t1;
			if (t2 < tFar) tFar = t2;
		}

		if (d.y != 0) {
			t1 = -o.y / d.y;
			t2 = ((1<<level) - o.y) / d.y;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
			}

			if (t1 > tNear) tNear = t1;
			if (t2 < tFar) tFar = t2;
		}

		if (d.z != 0) {
			t1 = -o.z / d.z;
			t2 = ((1<<level) - o.z) / d.z;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
			}

			if (t1 > tNear) tNear = t1;
			if (t2 < tFar) tFar = t2;
		}

		nearfar[0] = tNear;
		nearfar[1] = tFar;
	}

}
