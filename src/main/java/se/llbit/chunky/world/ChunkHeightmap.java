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
package se.llbit.chunky.world;

/**
 * Chunk heightmap
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkHeightmap {
    
    short[] data = new short[4 * Chunk.X_MAX * Chunk.Z_MAX];
    
    /**
     * Create new heightmap
     */
    public ChunkHeightmap() {
        for (int i = 0; i < (Chunk.X_MAX * Chunk.Z_MAX); ++i)
            data[i] = World.SEA_LEVEL;
    }

    /**
     * Set height value y at (x, z)
     * @param y
     * @param x
     * @param z
     */
    public void set(int y, int x, int z) {
        data[x + 2 * z * Chunk.X_MAX] = (short) y;
    }
    
    /**
     * @param x
     * @param z
     * @return Height at (x, z)
     */
    public int get(int x, int z) {
        return data[x + 2 * z * Chunk.X_MAX];
    }

}
