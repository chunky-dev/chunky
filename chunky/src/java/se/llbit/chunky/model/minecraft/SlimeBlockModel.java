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

public class SlimeBlockModel {
    private static final Quad[] quads = {
            new Quad(
                    new Vector3(13 / 16.0, 13 / 16.0, 3 / 16.0),
                    new Vector3(3 / 16.0, 13 / 16.0, 3 / 16.0),
                    new Vector3(13 / 16.0, 13 / 16.0, 13 / 16.0),
                    new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(3 / 16.0, 3 / 16.0, 3 / 16.0),
                    new Vector3(13 / 16.0, 3 / 16.0, 3 / 16.0),
                    new Vector3(3 / 16.0, 3 / 16.0, 13 / 16.0),
                    new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(13 / 16.0, 3 / 16.0, 13 / 16.0),
                    new Vector3(13 / 16.0, 3 / 16.0, 3 / 16.0),
                    new Vector3(13 / 16.0, 13 / 16.0, 13 / 16.0),
                    new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(3 / 16.0, 3 / 16.0, 3 / 16.0),
                    new Vector3(3 / 16.0, 3 / 16.0, 13 / 16.0),
                    new Vector3(3 / 16.0, 13 / 16.0, 3 / 16.0),
                    new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(13 / 16.0, 3 / 16.0, 3 / 16.0),
                    new Vector3(3 / 16.0, 3 / 16.0, 3 / 16.0),
                    new Vector3(13 / 16.0, 13 / 16.0, 3 / 16.0),
                    new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(3 / 16.0, 3 / 16.0, 13 / 16.0),
                    new Vector3(13 / 16.0, 3 / 16.0, 13 / 16.0),
                    new Vector3(3 / 16.0, 13 / 16.0, 13 / 16.0),
                    new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
    };

    private static final Texture[] tex = {
            Texture.slime, Texture.slime, Texture.slime, Texture.slime, Texture.slime, Texture.slime,

            Texture.slime, Texture.slime, Texture.slime, Texture.slime, Texture.slime, Texture.slime
    };

    public static boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord) {
        IntersectionRecord intersectionTest = new IntersectionRecord();
        boolean hit = false;
        Vector4 oldColor = new Vector4(intersectionRecord.color);
        for (int i = 0; i < 6; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray, intersectionTest)) {
                float[] color = tex[i].getColor(intersectionTest.uv.x, intersectionTest.uv.y);
                if (color[3] > Constants.EPSILON) {
                    ColorUtil.overlayColor(intersectionRecord.color, color);
                    intersectionRecord.setNormal(quad.n);
                    hit = true;
                }
            }
        }
        boolean innerHit = hit;
        Vector4 innerColor = hit ? new Vector4(intersectionRecord.color) : null;

        intersectionRecord.color.set(oldColor);
        hit = false;

        for (int i = 6; i < quads.length; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray, intersectionTest)) {
                float[] color = tex[i].getColor(intersectionTest.uv.x, intersectionTest.uv.y);
                if (color[3] > Constants.EPSILON) {
                    ColorUtil.overlayColor(intersectionRecord.color, color);
                    intersectionRecord.setNormal(quad.n);
                    hit = true;
                }
            }
        }
        if (hit) {
            intersectionRecord.distance += intersectionTest.distance;
            if (innerHit) {
                ColorUtil.overlayColor(intersectionRecord.color, innerColor);
            }
        }
        return hit;
    }
}
