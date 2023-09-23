/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

public class HoneyBlockModel {
    private static final Quad[] quads = {
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0, 1, 0, 1)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0, 1, 0, 1)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0, 1, 0, 1)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0, 1, 0, 1)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0, 1, 0, 1)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0, 1, 0, 1)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 15 / 16.0, 1 / 16.0),
                    new Vector3(1 / 16.0, 15 / 16.0, 1 / 16.0),
                    new Vector3(15 / 16.0, 15 / 16.0, 15 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
            ),
            new Quad(
                    new Vector3(1 / 16.0, 1 / 16.0, 1 / 16.0),
                    new Vector3(15 / 16.0, 1 / 16.0, 1 / 16.0),
                    new Vector3(1 / 16.0, 1 / 16.0, 15 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 1 / 16.0, 15 / 16.0),
                    new Vector3(15 / 16.0, 1 / 16.0, 1 / 16.0),
                    new Vector3(15 / 16.0, 15 / 16.0, 15 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
            ),
            new Quad(
                    new Vector3(1 / 16.0, 1 / 16.0, 1 / 16.0),
                    new Vector3(1 / 16.0, 1 / 16.0, 15 / 16.0),
                    new Vector3(1 / 16.0, 15 / 16.0, 1 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 1 / 16.0, 1 / 16.0),
                    new Vector3(1 / 16.0, 1 / 16.0, 1 / 16.0),
                    new Vector3(15 / 16.0, 15 / 16.0, 1 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
            ),
            new Quad(
                    new Vector3(1 / 16.0, 1 / 16.0, 15 / 16.0),
                    new Vector3(15 / 16.0, 1 / 16.0, 15 / 16.0),
                    new Vector3(1 / 16.0, 15 / 16.0, 15 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
            ),
    };

    private static final Texture[] tex = {
            Texture.honeyBlockBottom, Texture.honeyBlockBottom, Texture.honeyBlockBottom,
            Texture.honeyBlockBottom, Texture.honeyBlockBottom, Texture.honeyBlockBottom,

            Texture.honeyBlockBottom, Texture.honeyBlockTop, Texture.honeyBlockSide,
            Texture.honeyBlockSide, Texture.honeyBlockSide, Texture.honeyBlockSide
    };

    public static boolean intersect(Ray ray) {
        ray.t = Double.POSITIVE_INFINITY;
        boolean hit = false;
        Vector4 oldColor = new Vector4(ray.color);
        for (int i = 6; i < quads.length; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray)) {
                float[] color = tex[i].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ColorUtil.overlayColor(ray.color, color);
                    ray.setNormal(quad.n);
                    ray.t = ray.tNext;
                    hit = true;
                }
            }
        }
        boolean innerHit = hit;
        Vector4 innerColor = hit ? new Vector4(ray.color) : null;

        ray.color.set(oldColor);
        hit = false;

        for (int i = 0; i < 6; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray)) {
                float[] color = tex[i].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ColorUtil.overlayColor(ray.color, color);
                    ray.setNormal(quad.n);
                    ray.t = ray.tNext;
                    hit = true;
                }
            }
        }
        if (hit) {
            ray.distance += ray.t;
            ray.o.scaleAdd(ray.t, ray.d);
            if (innerHit) {
                ColorUtil.overlayColor(ray.color, innerColor);
            }
        }
        return hit;
    }
}
