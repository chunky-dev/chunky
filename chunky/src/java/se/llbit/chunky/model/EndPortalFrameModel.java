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
import se.llbit.math.*;

public class EndPortalFrameModel {
    private static final Quad[] endPortalQuadsNorth = new Quad[]{
            new Quad(
                    new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            )};

    private static final Quad[][] orientedEndPortalQuads = new Quad[4][];

    private static final Texture[] tex = new Texture[]{
            Texture.endPortalFrameTop, Texture.endStone,
            Texture.endPortalFrameSide, Texture.endPortalFrameSide, Texture.endPortalFrameSide, Texture.endPortalFrameSide
    };

    private static final Quad[] eyeOfEnderQuadsNorth = new Quad[]{
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector4(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(4 / 16.0, 13 / 16.0, 12 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 13 / 16.0, 4 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(4 / 16.0, 13 / 16.0, 4 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 13 / 16.0, 12 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            )
    };

    private static final Quad[][] orientedEyeOfEnderQuads = new Quad[4][];

    static {
        orientedEndPortalQuads[0] = endPortalQuadsNorth;
        orientedEndPortalQuads[1] = Model.rotateY(orientedEndPortalQuads[0]);
        orientedEndPortalQuads[2] = Model.rotateY(orientedEndPortalQuads[1]);
        orientedEndPortalQuads[3] = Model.rotateY(orientedEndPortalQuads[2]);

        orientedEyeOfEnderQuads[0] = eyeOfEnderQuadsNorth;
        orientedEyeOfEnderQuads[1] = Model.rotateY(orientedEyeOfEnderQuads[0]);
        orientedEyeOfEnderQuads[2] = Model.rotateY(orientedEyeOfEnderQuads[1]);
        orientedEyeOfEnderQuads[3] = Model.rotateY(orientedEyeOfEnderQuads[2]);
    }

    public static boolean intersect(Ray ray, boolean hasEye, String facing) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        Quad[] quads = orientedEndPortalQuads[getOrientationIndex(facing)];
        for (int i = 0; i < quads.length; i++) {
            Quad quad = quads[i];
            if (quad.intersect(ray)) {
                float[] color = tex[i].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ray.color.set(color);
                    ray.t = ray.tNext;
                    ray.n.set(quad.n);
                    hit = true;
                }
            }
        }

        if (hasEye) {
            for (Quad quad : orientedEyeOfEnderQuads[getOrientationIndex(facing)]) {
                if (quad.intersect(ray)) {
                    float[] color = Texture.eyeOfTheEnder.getColor(ray.u, ray.v);
                    if (color[3] > Ray.EPSILON) {
                        ray.color.set(color);
                        ray.t = ray.tNext;
                        ray.n.set(quad.n);
                        hit = true;
                    }
                }
            }
        }

        if (hit) {
            ray.distance += ray.t;
            ray.o.scaleAdd(ray.t, ray.d);
        }
        return hit;
    }

    private static int getOrientationIndex(String facing) {
        switch (facing) {
            case "north":
                return 0;
            case "east":
                return 1;
            case "south":
                return 2;
            case "west":
                return 3;
            default:
                return 0;
        }
    }
}
