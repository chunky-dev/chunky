package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class GrindstoneModel {
    private static final Texture pivot = Texture.grindstonePivot;
    private static final Texture round = Texture.grindstoneRound;
    private static final Texture side = Texture.grindstoneSide;
    private static final Texture leg = Texture.darkOakWood;
    private static final Texture[] tex = new Texture[]{
            leg, leg, leg, leg, leg,
            leg, leg, leg, leg, leg,
            pivot, pivot, pivot, pivot, pivot,
            pivot, pivot, pivot, pivot, pivot,
            round, round, side, side, round, round
    };

    private static final Quad[] quadsFloorNorth = new Quad[]{
            new Quad(
                    new Vector3(12 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector3(12 / 16.0, 0 / 16.0, 10 / 16.0),
                    new Vector4(12 / 16.0, 14 / 16.0, 6 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(12 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(12 / 16.0, 0 / 16.0, 10 / 16.0),
                    new Vector4(10 / 16.0, 6 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(14 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 7 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(14 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(12 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector4(4 / 16.0, 2 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(12 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(14 / 16.0, 0 / 16.0, 10 / 16.0),
                    new Vector4(14 / 16.0, 12 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector3(4 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 10 / 16.0),
                    new Vector4(2 / 16.0, 4 / 16.0, 6 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(2 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 10 / 16.0),
                    new Vector4(10 / 16.0, 6 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(4 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(4 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 7 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(4 / 16.0, 7 / 16.0, 6 / 16.0),
                    new Vector3(2 / 16.0, 0 / 16.0, 6 / 16.0),
                    new Vector4(14 / 16.0, 12 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(2 / 16.0, 7 / 16.0, 10 / 16.0),
                    new Vector3(4 / 16.0, 0 / 16.0, 10 / 16.0),
                    new Vector4(4 / 16.0, 2 / 16.0, 7 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(14 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(12 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(14 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector3(14 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(14 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector4(6 / 16.0, 0 / 16.0, 16 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector3(14 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector3(12 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(14 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(12 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(14 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(4 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(2 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(4 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector3(2 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector4(8 / 16.0, 10 / 16.0, 10 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(2 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector3(2 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector4(6 / 16.0, 0 / 16.0, 16 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(2 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector3(4 / 16.0, 13 / 16.0, 5 / 16.0),
                    new Vector3(2 / 16.0, 7 / 16.0, 5 / 16.0),
                    new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(2 / 16.0, 13 / 16.0, 11 / 16.0),
                    new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
                    new Vector4(8 / 16.0, 6 / 16.0, 16 / 16.0, 10 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector4(0 / 16.0, 8 / 16.0, 4 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 4 / 16.0, 2 / 16.0),
                    new Vector3(12 / 16.0, 4 / 16.0, 2 / 16.0),
                    new Vector3(4 / 16.0, 4 / 16.0, 14 / 16.0),
                    new Vector4(0 / 16.0, 8 / 16.0, 4 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(4 / 16.0, 4 / 16.0, 14 / 16.0),
                    new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(12 / 16.0, 4 / 16.0, 2 / 16.0),
                    new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 2 / 16.0),
                    new Vector3(4 / 16.0, 4 / 16.0, 2 / 16.0),
                    new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 14 / 16.0),
                    new Vector3(12 / 16.0, 4 / 16.0, 14 / 16.0),
                    new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 4 / 16.0)
            )
    };

    static final Quad[][][] orientedQuads = new Quad[3][4][];

    static {
        // face=floor
        orientedQuads[0][0] = quadsFloorNorth;
        orientedQuads[0][1] = Model.rotateY(orientedQuads[0][0]);
        orientedQuads[0][2] = Model.rotateY(orientedQuads[0][1]);
        orientedQuads[0][3] = Model.rotateY(orientedQuads[0][2]);

        // face=wall
        orientedQuads[1][0] = Model.rotateX(quadsFloorNorth, Math.toRadians(-90));
        orientedQuads[1][1] = Model.rotateY(orientedQuads[1][0]);
        orientedQuads[1][2] = Model.rotateY(orientedQuads[1][1]);
        orientedQuads[1][3] = Model.rotateY(orientedQuads[1][2]);

        // face=ceiling
        orientedQuads[2][2] = Model.rotateX(quadsFloorNorth, Math.toRadians(180));
        orientedQuads[2][3] = Model.rotateY(orientedQuads[2][2]);
        orientedQuads[2][0] = Model.rotateY(orientedQuads[2][3]);
        orientedQuads[2][1] = Model.rotateY(orientedQuads[2][0]);
    }

    public static boolean intersect(Ray ray, String face, String facing) {
        boolean hit = false;
        ray.t = Double.POSITIVE_INFINITY;

        Quad[] quads = orientedQuads[getFaceIndex(face)][getOrientationIndex(facing)];
        for (int i = 0; i < quads.length; ++i) {
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

        if (hit) {
            ray.distance += ray.t;
            ray.o.scaleAdd(ray.t, ray.d);
        }
        return hit;
    }

    private static int getFaceIndex(String face) {
        switch (face) {
            case "floor":
                return 0;
            case "wall":
                return 1;
            case "ceiling":
                return 2;
            default:
                return 0;
        }
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
