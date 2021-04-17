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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Quad;
import se.llbit.math.Ray;

import java.util.ArrayList;
import java.util.Arrays;

public class WallModel extends AABBModel {
    private static final AABB post = new AABB(4 / 16.0, 12 / 16.0, 0, 1, 4 / 16.0, 12 / 16.0);

    private static final AABB[] plank = {
            new AABB(5 / 16.0, 11 / 16.0, 0, 13 / 16.0, 0, .5), // north
            new AABB(.5, 1, 0, 13 / 16.0, 5 / 16.0, 11 / 16.0), // east
            new AABB(5 / 16.0, 11 / 16.0, 0, 13 / 16.0, .5, 1), // south
            new AABB(0, .5, 0, 13 / 16.0, 5 / 16.0, 11 / 16.0)  // west
    };

    private static final AABB[] plankTall = {
            new AABB(5 / 16.0, 11 / 16.0, 0, 1, 0, .5), // north
            new AABB(.5, 1, 0, 1, 5 / 16.0, 11 / 16.0), // east
            new AABB(5 / 16.0, 11 / 16.0, 0, 1, .5, 1), // south
            new AABB(0, .5, 0, 1, 5 / 16.0, 11 / 16.0)  // west
    };

    private final AABB[] boxes;
    private final Texture[][] textures;

    public WallModel(Texture texture, int[] connections, boolean midsection) {
        ArrayList<AABB> boxes = new ArrayList<>();
        if (midsection)
            boxes.add(post);
        for (int i = 0; i < 4; i++) {
            if (connections[i] == 1) {
                boxes.add(plank[i]);
            } else if (connections[i] == 2) {
                boxes.add(plankTall[i]);
            }
        }
        this.boxes = boxes.toArray(new AABB[0]);
        Texture[] tex = new Texture[6];
        Arrays.fill(tex, texture);
        this.textures = new Texture[this.boxes.length][];
        Arrays.fill(this.textures, tex);
    }

    @Override
    public AABB[] getBoxes() {
        return boxes;
    }

    @Override
    public Texture[][] getTextures() {
        return textures;
    }
}
