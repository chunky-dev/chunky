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
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CakeModel extends QuadModel {
    private static final Quad[][] cake = new Quad[7][];

    static {
        int[] fromX = new int[] {1, 3, 5, 7, 9, 11, 13};
        for (int i = 0; i < 7; i++) {
            double xMin = fromX[i] / 16.0;
            cake[i] = new Quad[]{
                    // front
                    new Quad(new Vector3(.9375, 0, .0625), new Vector3(xMin, 0, .0625),
                            new Vector3(.9375, .5, .0625), new Vector4(.9375, xMin, 0, .5)),

                    // back
                    new Quad(new Vector3(xMin, 0, .9375), new Vector3(.9375, 0, .9375),
                            new Vector3(xMin, .5, .9375), new Vector4(xMin, .9375, 0, .5)),

                    // right
                    new Quad(new Vector3(xMin, 0, .0625), new Vector3(xMin, 0, .9375),
                            new Vector3(xMin, .5, .0625), new Vector4(0.0625, .9375, 0, .5)),

                    // left
                    new Quad(new Vector3(.9375, 0, .9375), new Vector3(.9375, 0, .0625),
                            new Vector3(.9375, .5, .9375), new Vector4(.9375, 0.0625, 0, .5)),

                    // top
                    new Quad(new Vector3(.9375, .5, .0625), new Vector3(xMin, .5, .0625),
                            new Vector3(.9375, .5, .9375), new Vector4(.9375, xMin, .9375, .0625)),

                    // bottom
                    new Quad(new Vector3(xMin, 0, .0625), new Vector3(.9375, 0, .0625),
                            new Vector3(xMin, 0, .9375), new Vector4(xMin, .9375, .0625, .9375))
            };
        }
    }

    private final Quad[] quads;
    private final Texture[] textures;

    public CakeModel(int bites) {
        this.quads = cake[bites];

        Texture top = Texture.cakeTop;
        Texture side = Texture.cakeSide;
        Texture bottom = Texture.cakeBottom;
        Texture inside = Texture.cakeInside;
        textures = new Texture[]{side, side, bites == 0 ? side : inside, side, top, bottom};
    }

    @Override
    public Quad[] getQuads() {
        return quads;
    }

    @Override
    public Texture[] getTextures() {
        return textures;
    }
}
