package se.llbit.chunky.entity;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.util.JsonUtil;

import java.util.Collection;
import java.util.LinkedList;

public class Lectern extends Entity {
    private static final Quad[] quadsNorth = new Quad[]{
            new Quad(
                    new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 10 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 10 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 10 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 2 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 15 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 2 / 16.0, 4 / 16.0),
                    new Vector4(15 / 16.0, 2 / 16.0, 8 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 2 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 15 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 2 / 16.0, 12 / 16.0),
                    new Vector4(15 / 16.0, 2 / 16.0, 0 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 15 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 15 / 16.0, 4 / 16.0),
                    new Vector3(4 / 16.0, 2 / 16.0, 4 / 16.0),
                    new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 3 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 15 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 15 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 2 / 16.0, 12 / 16.0),
                    new Vector4(16 / 16.0, 8 / 16.0, 13 / 16.0, 0 / 16.0)
            )
    };

    private static final Quad[] topQuadsNorth = Model.rotateX(new Quad[]{
            new Quad(
                    new Vector3(15.99 / 16.0, 16 / 16.0, 3 / 16.0),
                    new Vector3(0.01 / 16.0, 16 / 16.0, 3 / 16.0),
                    new Vector3(15.99 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 2 / 16.0, 15 / 16.0)
            ),
            new Quad(
                    new Vector3(0.01 / 16.0, 12 / 16.0, 3 / 16.0),
                    new Vector3(15.99 / 16.0, 12 / 16.0, 3 / 16.0),
                    new Vector3(0.01 / 16.0, 12 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 3 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0.01 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(0.01 / 16.0, 16 / 16.0, 3 / 16.0),
                    new Vector3(0.01 / 16.0, 12 / 16.0, 16 / 16.0),
                    new Vector4(13 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(15.99 / 16.0, 16 / 16.0, 3 / 16.0),
                    new Vector3(15.99 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(15.99 / 16.0, 12 / 16.0, 3 / 16.0),
                    new Vector4(13 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            ),
            new Quad(
                    new Vector3(0.01 / 16.0, 16 / 16.0, 3 / 16.0),
                    new Vector3(15.99 / 16.0, 16 / 16.0, 3 / 16.0),
                    new Vector3(0.01 / 16.0, 12 / 16.0, 3 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(15.99 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(0.01 / 16.0, 16 / 16.0, 16 / 16.0),
                    new Vector3(15.99 / 16.0, 12 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
            )
    }, Math.toRadians(-22.5));

    static final Quad[][] orientedQuads = new Quad[4][];

    static final Quad[][] orientedTopQuads = new Quad[4][];

    static {
        orientedQuads[0] = quadsNorth;
        orientedQuads[1] = Model.rotateY(orientedQuads[0]);
        orientedQuads[2] = Model.rotateY(orientedQuads[1]);
        orientedQuads[3] = Model.rotateY(orientedQuads[2]);

        orientedTopQuads[0] = topQuadsNorth;
        orientedTopQuads[1] = Model.rotateY(orientedTopQuads[0]);
        orientedTopQuads[2] = Model.rotateY(orientedTopQuads[1]);
        orientedTopQuads[3] = Model.rotateY(orientedTopQuads[2]);
    }

    public static final Texture[] tex = {
            Texture.lecternBase, Texture.oakPlanks,
            Texture.lecternBase, Texture.lecternBase, Texture.lecternBase, Texture.lecternBase,

            Texture.lecternSides, Texture.lecternSides, Texture.lecternFront, Texture.lecternFront,

            Texture.lecternTop, Texture.oakPlanks,
            Texture.lecternSides, Texture.lecternSides, Texture.lecternSides, Texture.lecternSides
    };

    private final String facing;

    public Lectern(Vector3 position, String facing) {
        super(position);
        this.facing = facing;
    }

    public Lectern(JsonObject json) {
        super(JsonUtil.vec3FromJsonObject(json.get("position")));
        this.facing = json.get("facing").stringValue("north");
    }

    @Override
    public Collection<Primitive> primitives(Vector3 offset) {
        Collection<Primitive> faces = new LinkedList<>();
        Transform transform = Transform.NONE
                .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
        int facing = getOrientationIndex(this.facing);
        for (int i = 0; i < orientedQuads[facing].length; i++) {
            orientedQuads[facing][i].addTriangles(faces, new TextureMaterial(tex[i]), transform);
        }
        for (int i = 0; i < orientedTopQuads[facing].length; i++) {
            orientedTopQuads[facing][i].addTriangles(faces, new TextureMaterial(tex[i + orientedQuads[facing].length]), transform);
        }
        return faces;
    }

    @Override
    public JsonValue toJson() {
        JsonObject json = new JsonObject();
        json.add("kind", "lectern");
        json.add("position", position.toJson());
        json.add("facing", facing);
        return json;
    }

    public static Entity fromJson(JsonObject json) {
        return new Lectern(json);
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
