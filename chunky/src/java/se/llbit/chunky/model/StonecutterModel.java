package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class StonecutterModel {
    private static final Texture bottom = Texture.stonecutterBottom;
    private static final Texture top = Texture.stonecutterTop;
    private static final Texture side = Texture.stonecutterSide;
    private static final Texture saw = Texture.stonecutterSaw;
    private static final Texture[] tex = new Texture[]{
            top, bottom, side, side, side, side, saw, saw
    };

    private static final Quad[] quadsNorth = new Quad[]{
            new Quad(
                    new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),

            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 9 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 9 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 9 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 9 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(1 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector3(15 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector3(1 / 16.0, 9 / 16.0, 8 / 16.0),
                    new Vector4(15 / 16.0, 1 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector3(1 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector3(15 / 16.0, 9 / 16.0, 8 / 16.0),
                    new Vector4(1 / 16.0, 15 / 16.0, 7 / 16.0, 0 / 16.0)
            )
    };

    static final Quad[][] orientedQuads = new Quad[4][];

    static {
        orientedQuads[0] = quadsNorth;
        orientedQuads[1] = Model.rotateY(orientedQuads[0]);
        orientedQuads[2] = Model.rotateY(orientedQuads[1]);
        orientedQuads[3] = Model.rotateY(orientedQuads[2]);
    }

    public static boolean intersect(Ray ray, String facing) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        Quad[] quads = orientedQuads[getOrientationIndex(facing)];
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
