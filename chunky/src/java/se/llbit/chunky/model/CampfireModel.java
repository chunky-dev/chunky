package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

public class CampfireModel {
    private static final Texture log = Texture.campfireLog;
    private static final Texture litlog = Texture.campfireLogLit;
    private static final Texture fire = Texture.campfireFire;

    private static final Texture[] tex = new Texture[]{
            log, log, log, log, log, log, log, log, log, log, log, log, log, log, log,
            log, log, log, log, log, log, log, log, log, log, log, log, log,
    };

    private static final Texture[] texLit = new Texture[]{
            log, log, log, litlog, log, log, log, litlog, log, log, litlog, litlog, log, log, litlog,
            log, log, log, log, litlog, log, log, litlog, litlog, litlog, log, log, log, fire, fire, fire, fire
    };

    private static final Quad[] quads = new Quad[]{
            new Quad(
                    new Vector3(5 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(5 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(1 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(5 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(1 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(1 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(1 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(1 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(5 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(5 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 15 / 16.0, 11 / 16.0)
            ),
            new Quad(
                    new Vector3(1 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(5 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(1 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(5 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(1 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(5 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector3(0 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector3(16 / 16.0, 7 / 16.0, 15 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 3 / 16.0, 11 / 16.0),
                    new Vector3(16 / 16.0, 3 / 16.0, 11 / 16.0),
                    new Vector3(0 / 16.0, 3 / 16.0, 15 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 8 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 7 / 16.0, 15 / 16.0),
                    new Vector3(0 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector3(0 / 16.0, 3 / 16.0, 15 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector3(16 / 16.0, 7 / 16.0, 15 / 16.0),
                    new Vector3(16 / 16.0, 3 / 16.0, 11 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector3(16 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector3(0 / 16.0, 3 / 16.0, 11 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 7 / 16.0, 15 / 16.0),
                    new Vector3(0 / 16.0, 7 / 16.0, 15 / 16.0),
                    new Vector3(16 / 16.0, 3 / 16.0, 15 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(15 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(11 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(15 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(11 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(11 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(11 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 15 / 16.0, 11 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(15 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(15 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(11 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(15 / 16.0, 4 / 16.0, 0 / 16.0),
                    new Vector3(11 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(15 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(11 / 16.0, 4 / 16.0, 16 / 16.0),
                    new Vector3(15 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 7 / 16.0, 1 / 16.0),
                    new Vector3(0 / 16.0, 7 / 16.0, 1 / 16.0),
                    new Vector3(16 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 3 / 16.0, 1 / 16.0),
                    new Vector3(16 / 16.0, 3 / 16.0, 1 / 16.0),
                    new Vector3(0 / 16.0, 3 / 16.0, 5 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 8 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(0 / 16.0, 7 / 16.0, 1 / 16.0),
                    new Vector3(0 / 16.0, 3 / 16.0, 5 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 7 / 16.0, 1 / 16.0),
                    new Vector3(16 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(16 / 16.0, 3 / 16.0, 1 / 16.0),
                    new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 7 / 16.0, 1 / 16.0),
                    new Vector3(16 / 16.0, 7 / 16.0, 1 / 16.0),
                    new Vector3(0 / 16.0, 3 / 16.0, 1 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(0 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(16 / 16.0, 3 / 16.0, 5 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(11 / 16.0, 1 / 16.0, 16 / 16.0),
                    new Vector3(11 / 16.0, 1 / 16.0, 0 / 16.0),
                    new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 2 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(11 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 2 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(5 / 16.0, 1 / 16.0, 0 / 16.0),
                    new Vector3(11 / 16.0, 1 / 16.0, 0 / 16.0),
                    new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(6 / 16.0, 0 / 16.0, 1 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(11 / 16.0, 1 / 16.0, 16 / 16.0),
                    new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
                    new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 10 / 16.0, 1 / 16.0, 0 / 16.0)
            ),
            rotateFire(new Quad(
                    new Vector3(0.8 / 16.0, 17 / 16.0, 8 / 16.0),
                    new Vector3(15.2 / 16.0, 17 / 16.0, 8 / 16.0),
                    new Vector3(0.8 / 16.0, 1 / 16.0, 8 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
            )),
            rotateFire(new Quad(
                    new Vector3(15.2 / 16.0, 17 / 16.0, 8 / 16.0),
                    new Vector3(0.8 / 16.0, 17 / 16.0, 8 / 16.0),
                    new Vector3(15.2 / 16.0, 1 / 16.0, 8 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
            )),
            rotateFire(new Quad(
                    new Vector3(8 / 16.0, 17 / 16.0, 15.2 / 16.0),
                    new Vector3(8 / 16.0, 17 / 16.0, 0.8 / 16.0),
                    new Vector3(8 / 16.0, 1 / 16.0, 15.2 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
            )),
            rotateFire(new Quad(
                    new Vector3(8 / 16.0, 17 / 16.0, 0.8 / 16.0),
                    new Vector3(8 / 16.0, 17 / 16.0, 15.2 / 16.0),
                    new Vector3(8 / 16.0, 1 / 16.0, 0.8 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
            ))
    };

    static final Quad[][] orientedQuads = new Quad[4][];

    static {
        orientedQuads[0] = quads;
        orientedQuads[1] = Model.rotateY(orientedQuads[0]);
        orientedQuads[2] = Model.rotateY(orientedQuads[1]);
        orientedQuads[3] = Model.rotateY(orientedQuads[2]);
    }

    public static boolean intersect(Ray ray, String facing, boolean lit) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        Quad[] quads = orientedQuads[getOrientationIndex(facing)];
        int n = lit ? quads.length : quads.length - 4;
        Texture[] textures = lit ? texLit : tex;
        for (int i = 0; i < n; ++i) {
            Quad quad = quads[i];
            if (quad.intersect(ray)) {
                float[] color = textures[i].getColor(ray.u, ray.v);
                if (color[3] > Ray.EPSILON) {
                    ray.color.set(color);
                    ray.t = ray.tNext;
                    ray.n.set(quad.n);
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

    private static Quad rotateFire(Quad quad) {
        double rotatedWidth = 14.4 * Math.cos(Math.toRadians(45));
        return new Quad(quad, Transform.NONE.rotateY(Math.toRadians(45)));
    }
}
