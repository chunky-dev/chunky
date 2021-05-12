package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

public class ComposterModel {
    private static final Texture top = Texture.composterTop;
    private static final Texture bottom = Texture.composterBottom;
    private static final Texture side = Texture.composterSide;
    private static final Texture inside = Texture.composterBottom;
    private static final Texture[] tex = new Texture[]{
            inside, bottom, top, side, side, side, side, top, side, side, side, side, top, side, side, top, side, side
    };

    private static final Quad[] quads = new Quad[]{
            new Quad(
                    new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 1 - 2 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 0 / 16.0, 16 / 16.0)
            )
    };


    private static final Texture[] contentTex = new Texture[]{

            null, Texture.composterCompost, Texture.composterCompost, Texture.composterCompost, Texture.composterCompost,
            Texture.composterCompost, Texture.composterCompost, Texture.composterCompost, Texture.composterReady
    };

    private static final Quad[] content = {
            null,
            new Quad(
                    new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 5 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 5 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 5 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 7 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 7 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 7 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 9 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 9 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 9 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 11 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 11 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 11 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 13 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 13 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 13 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 15 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 15 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 15 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 15 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 15 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 15 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
            )
    };

    public static boolean intersect(Ray ray, int level) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        for (int i = 0; i < quads.length; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray)) {
                float[] color = tex[i].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ray.color.set(color);
                    ray.t = ray.tNext;
                    ray.setN(quad.n);
                    hit = true;
                }
            }
        }

        if (level > 0) {
            Quad quad = content[level];
            if (quad.intersect(ray)) {
                float[] color = contentTex[level].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ray.color.set(color);
                    ray.t = ray.tNext;
                    ray.setN(quad.n);
                    hit = true;
                }
            }
        }

        if (hit) {
            ray.distance += ray.t;
            ray.o.scaleAdd(ray.t, ray.d);
        }
        return hit;
    }
}
