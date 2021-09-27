package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BambooModel {
    private static final Texture[] tex = new Texture[]{
            Texture.bambooStalk, Texture.bambooStalk, Texture.bambooStalk, Texture.bambooStalk,
            Texture.bambooStalk, Texture.bambooStalk
    };

    private static final Quad[][] stemQuads = new Quad[][]{
            // age = 0
            new Quad[]{
                    new Quad(
                            new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
                            new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
                            new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
                            new Vector4(13 / 16.0, 15 / 16.0, 1 - 0 / 16.0, 1 - 2 / 16.0)
                    ),
                    new Quad(
                            new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                            new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
                            new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                            new Vector4(13 / 16.0, 15 / 16.0, 4 / 16.0, 6 / 16.0)
                    ),
                    new Quad(
                            new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                            new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                            new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
                            new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
                    ),
                    new Quad(
                            new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
                            new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
                            new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
                            new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
                    ),
                    new Quad(
                            new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
                            new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                            new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
                            new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
                    ),
                    new Quad(
                            new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                            new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
                            new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
                            new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
                    )
            },

            // age=1
            new Quad[]{
                    new Quad(
                            new Vector3(6.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
                            new Vector3(9.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
                            new Vector3(6.5 / 16.0, 16 / 16.0, 6.5 / 16.0),
                            new Vector4(13 / 16.0, 16 / 16.0, 1 - 0 / 16.0, 1 - 3 / 16.0)
                    ),
                    new Quad(
                            new Vector3(6.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
                            new Vector3(9.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
                            new Vector3(6.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
                            new Vector4(13 / 16.0, 16 / 16.0, 4 / 16.0, 7 / 16.0)
                    ),
                    new Quad(
                            new Vector3(6.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
                            new Vector3(6.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
                            new Vector3(6.5 / 16.0, 16 / 16.0, 6.5 / 16.0),
                            new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
                    ),
                    new Quad(
                            new Vector3(9.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
                            new Vector3(9.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
                            new Vector3(9.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
                            new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
                    ),
                    new Quad(
                            new Vector3(9.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
                            new Vector3(6.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
                            new Vector3(9.5 / 16.0, 16 / 16.0, 6.5 / 16.0),
                            new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
                    ),
                    new Quad(
                            new Vector3(6.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
                            new Vector3(9.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
                            new Vector3(6.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
                            new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
                    )
            }
    };

    private static final Texture[] smallLeavesTex = new Texture[]{
            Texture.bambooSmallLeaves, Texture.bambooSmallLeaves, Texture.bambooSmallLeaves, Texture.bambooSmallLeaves,
    };

    private static final Quad[] smallLeaves = new Quad[]{
            new Quad(
                    new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(15.2 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(0.8 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
                    new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
                    new Vector3(8 / 16.0, 16 / 16.0, 0.8 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
                    new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
                    new Vector3(8 / 16.0, 16 / 16.0, 15.2 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            )
    };

    private static final Texture[] largeLeavesTex = new Texture[]{
            Texture.bambooLargeLeaves, Texture.bambooLargeLeaves, Texture.bambooLargeLeaves, Texture.bambooLargeLeaves
    };

    private static final Quad[] largeLeaves = new Quad[]{
            new Quad(
                    new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(15.2 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
                    new Vector3(0.8 / 16.0, 16 / 16.0, 8 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
                    new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
                    new Vector3(8 / 16.0, 16 / 16.0, 0.8 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
                    new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
                    new Vector3(8 / 16.0, 16 / 16.0, 15.2 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            )
    };

    public static boolean intersect(Ray ray, int age, String leaves) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        Quad[] quads = stemQuads[age];
        for (int i = 0; i < quads.length; ++i) {
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

        if ("small".equals(leaves)) {
            for (int i = 0; i < smallLeaves.length; ++i) {
                Quad quad = smallLeaves[i];
                if (quad.intersect(ray)) {
                    float[] color = smallLeavesTex[i].getColor(ray.u, ray.v);
                    if (color[3] > Ray.EPSILON) {
                        ray.color.set(color);
                        ray.t = ray.tNext;
                        ray.setNormal(quad.n);
                        hit = true;
                    }
                }
            }
        } else if ("large".equals(leaves)) {
            for (int i = 0; i < largeLeaves.length; ++i) {
                Quad quad = largeLeaves[i];
                if (quad.intersect(ray)) {
                    float[] color = largeLeavesTex[i].getColor(ray.u, ray.v);
                    if (color[3] > Ray.EPSILON) {
                        ray.color.set(color);
                        ray.t = ray.tNext;
                        ray.setNormal(quad.n);
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
}
