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
package se.llbit.util;

import se.llbit.math.Vector3d;

/**
 * Pool for Vector objects to reduce GC pressure.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class VectorPool {
    private int limit = 10;
    private int size = 0;
    private Vector3d[] pool = new Vector3d[limit];

    /**
     * @param other
     * @return A new vector initialized to be equal to the supplied vector.
     */
    public final Vector3d get(Vector3d other) {
        Vector3d vec;
        if (size == 0) {
            vec = new Vector3d();
        } else {
            vec = pool[--size];
        }
        vec.set(other);
        return vec;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return A new vector initialized to be equal to the supplied vector.
     */
    public final Vector3d get(double x, double y, double z) {
        Vector3d vec;
        if (size == 0) {
            vec = new Vector3d();
        } else {
            vec = pool[--size];
        }
        vec.set(x, y, z);
        return vec;
    }

    /**
     * @return A new, uninitialized, vector.
     */
    public final Vector3d get() {
        if (size == 0) {
            Vector3d vec = new Vector3d();
            return vec;
        } else {
            return pool[--size];
        }
    }

    /**
     * Place a vector in the pool
     * @param vec
     */
    public final void dispose(Vector3d vec) {
        if (size == limit) {
            int newLimit = limit * 2;
            Vector3d[] newPool = new Vector3d[newLimit];
            System.arraycopy(pool, 0, newPool, 0, limit);
            limit = newLimit;
            pool = newPool;
        }
        pool[size++] = vec;
    }
}
