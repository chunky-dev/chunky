package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class ScaffoldingModel {
    private static final Texture top = Texture.scaffoldingTop;
    private static final Texture side = Texture.scaffoldingSide;
    private static final Texture bottom = Texture.scaffoldingBottom;
    private static final Texture[] tex = new Texture[]{
            top, top, bottom, side, side, side, side, bottom, side, side, side, side, bottom, side, side, side, side, bottom, side, side, side, side, bottom, side, side, bottom, side, side, bottom, side, side, bottom, side, side, top, top, bottom, side, side, bottom, side, side, bottom, side, side, bottom, side, side
    };

    private static final Quad[] quads = new Quad[]{
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 15.99 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 15.99 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 15.99 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 2 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 2 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 14 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 14 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 14 / 16.0, 0 / 16.0),
                    new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 14 / 16.0, 16 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 14 / 16.0, 16 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector3(16 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 14 / 16.0, 2 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(16 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(16 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector3(0 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 14 / 16.0, 2 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(0 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(0 / 16.0, 14 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 14 / 16.0, 2 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 1.99 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 1.99 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 1.99 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(14 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 2 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 2 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 2 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 2 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(2 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(14 / 16.0, 16 / 16.0, 14 / 16.0, 2 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 2 / 16.0, 14 / 16.0),
                    new Vector3(14 / 16.0, 2 / 16.0, 2 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 2 / 16.0, 2 / 16.0),
                    new Vector3(16 / 16.0, 2 / 16.0, 14 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(0 / 16.0, 2 / 16.0, 14 / 16.0, 2 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 2 / 16.0, 14 / 16.0),
                    new Vector3(0 / 16.0, 2 / 16.0, 2 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
                    new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 2 / 16.0, 2 / 16.0),
                    new Vector3(2 / 16.0, 2 / 16.0, 14 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
                    new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 14 / 16.0)
            )
    };

    public static boolean intersect(Ray ray, boolean bottom) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        int n = bottom ? quads.length : quads.length - 14;
        for (int i = 0; i < n; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray)) {
                float[] color = tex[i].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ray.color.set(color);
                    ray.t = ray.tNext;
                    ray.setNormal(quad.n);
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
