package se.llbit.chunky.entity;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Straw bed frills extend outside their block's voxel and thus need to be handled as an entity
 * and put into the BVH tree.
 */
public class StrawBedFrills extends Entity {
  private static final Quad[] headQuads = new Quad[]{
    new Quad(
      new Vector3(-2 / 16.0, 0.065 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 0.065 / 16.0, 16 / 16.0),
      new Vector3(-2 / 16.0, 0.065 / 16.0, 8 / 16.0),
      new Vector4(1.5 / 16.0, 1 / 16.0, 9.75 / 16.0, 7.75 / 16.0)
    ),
    new Quad(
      new Vector3(-2 / 16.0, 0.065 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 0.065 / 16.0, 8 / 16.0),
      new Vector3(-2 / 16.0, 0.065 / 16.0, 16 / 16.0),
      new Vector4(2 / 16.0, 1.5 / 16.0, 7.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(-2 / 16.0, 4.96 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 4.96 / 16.0, 16 / 16.0),
      new Vector3(-2 / 16.0, 4.96 / 16.0, 8 / 16.0),
      new Vector4(10.5 / 16.0, 10 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(-2 / 16.0, 4.96 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 4.96 / 16.0, 8 / 16.0),
      new Vector3(-2 / 16.0, 4.96 / 16.0, 16 / 16.0),
      new Vector4(11 / 16.0, 10.5 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(-2 / 16.0, 4.95 / 16.0, 8.025 / 16.0),
      new Vector3(0 / 16.0, 4.95 / 16.0, 8.025 / 16.0),
      new Vector3(-2 / 16.0, 0.065 / 16.0, 8.025 / 16.0),
      new Vector4(1.5 / 16.0, 1 / 16.0, 7.75 / 16.0, 6.518750000000001 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4.95 / 16.0, 8.025 / 16.0),
      new Vector3(-2 / 16.0, 4.95 / 16.0, 8.025 / 16.0),
      new Vector3(0 / 16.0, 0.065 / 16.0, 8.025 / 16.0),
      new Vector4(1.5 / 16.0, 2 / 16.0, 7.75 / 16.0, 6.518750000000001 / 16.0)
    ),
    new Quad(
      new Vector3(-2 / 16.0, 4.95 / 16.0, 15.985 / 16.0),
      new Vector3(0 / 16.0, 4.95 / 16.0, 15.985 / 16.0),
      new Vector3(-2 / 16.0, 0.065 / 16.0, 15.985 / 16.0),
      new Vector4(10.5 / 16.0, 10 / 16.0, 12.75 / 16.0, 11.51875 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4.95 / 16.0, 15.985 / 16.0),
      new Vector3(-2 / 16.0, 4.95 / 16.0, 15.985 / 16.0),
      new Vector3(0 / 16.0, 0.065 / 16.0, 15.985 / 16.0),
      new Vector4(11 / 16.0, 10.5 / 16.0, 12.75 / 16.0, 11.51875 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0.05 / 16.0, 16 / 16.0),
      new Vector3(18 / 16.0, 0.05 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0.05 / 16.0, 8 / 16.0),
      new Vector4(0.5 / 16.0, 0 / 16.0, 9.75 / 16.0, 7.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0.05 / 16.0, 8 / 16.0),
      new Vector3(18 / 16.0, 0.05 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0.05 / 16.0, 16 / 16.0),
      new Vector4(0.5 / 16.0, 1 / 16.0, 7.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4.975 / 16.0, 16 / 16.0),
      new Vector3(18 / 16.0, 4.975 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector4(0.5 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector3(18 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 4.975 / 16.0, 16 / 16.0),
      new Vector4(0.5 / 16.0, 1 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector3(18 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0.05000000000000001 / 16.0, 8 / 16.0),
      new Vector4(0.5 / 16.0, 0 / 16.0, 7.75 / 16.0, 6.518750000000001 / 16.0)
    ),
    new Quad(
      new Vector3(18 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 4.975 / 16.0, 8 / 16.0),
      new Vector3(18 / 16.0, 0.05000000000000001 / 16.0, 8 / 16.0),
      new Vector4(0.5 / 16.0, 1 / 16.0, 7.75 / 16.0, 6.518750000000001 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4.975 / 16.0, 15.975 / 16.0),
      new Vector3(18 / 16.0, 4.975 / 16.0, 15.975 / 16.0),
      new Vector3(16 / 16.0, 0.05000000000000001 / 16.0, 15.975 / 16.0),
      new Vector4(0.5 / 16.0, 0 / 16.0, 12.75 / 16.0, 11.51875 / 16.0)
    ),
    new Quad(
      new Vector3(18 / 16.0, 4.975 / 16.0, 15.975 / 16.0),
      new Vector3(16 / 16.0, 4.975 / 16.0, 15.975 / 16.0),
      new Vector3(18 / 16.0, 0.05000000000000001 / 16.0, 15.975 / 16.0),
      new Vector4(0.5 / 16.0, 1 / 16.0, 12.75 / 16.0, 11.51875 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0.05 / 16.0, 8 / 16.0),
      new Vector3(19 / 16.0, 0.05 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0.05 / 16.0, 0 / 16.0),
      new Vector4(12.75 / 16.0, 12 / 16.0, 11.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0.05 / 16.0, 0 / 16.0),
      new Vector3(19 / 16.0, 0.05 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0.05 / 16.0, 8 / 16.0),
      new Vector4(13.5 / 16.0, 12.75 / 16.0, 9.75 / 16.0, 11.75 / 16.0)
    ),
    new Quad(
      new Vector3(-3 / 16.0, 0.065 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 0.065 / 16.0, 8 / 16.0),
      new Vector3(-3 / 16.0, 0.065 / 16.0, 0 / 16.0),
      new Vector4(14.25 / 16.0, 13.5 / 16.0, 11.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(-3 / 16.0, 0.065 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0.065 / 16.0, 0 / 16.0),
      new Vector3(-3 / 16.0, 0.065 / 16.0, 8 / 16.0),
      new Vector4(15 / 16.0, 14.25 / 16.0, 9.75 / 16.0, 11.75 / 16.0)
    )
  };
  private static final Quad[] footQuads = new Quad[]{
    new Quad(
      new Vector3(16 / 16.0, 0.125 / 16.0, -3 / 16.0),
      new Vector3(0 / 16.0, 0.125 / 16.0, -3 / 16.0),
      new Vector3(16 / 16.0, 0.125 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 4 / 16.0, 4 / 16.0, 4.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0.125 / 16.0, -3 / 16.0),
      new Vector3(16 / 16.0, 0.125 / 16.0, -3 / 16.0),
      new Vector3(0 / 16.0, 0.125 / 16.0, 0 / 16.0),
      new Vector4(4 / 16.0, 0 / 16.0, 3.25 / 16.0, 4 / 16.0)
    ),
    new Quad(
      new Vector3(19 / 16.0, 0.105 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0.105 / 16.0, 0 / 16.0),
      new Vector3(19 / 16.0, 0.105 / 16.0, 16 / 16.0),
      new Vector4(11.999999999999998 / 16.0, 12.749999999999998 / 16.0, 5.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0.105 / 16.0, 0 / 16.0),
      new Vector3(19 / 16.0, 0.105 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0.105 / 16.0, 16 / 16.0),
      new Vector4(13.499999999999998 / 16.0, 12.749999999999998 / 16.0, 5.75 / 16.0, 9.75 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0.115 / 16.0, 0 / 16.0),
      new Vector3(-3 / 16.0, 0.115 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0.115 / 16.0, 16 / 16.0),
      new Vector4(13.499999999999998 / 16.0, 14.249999999999998 / 16.0, 9.75 / 16.0, 5.75 / 16.0)
    ),
    new Quad(
      new Vector3(-3 / 16.0, 0.115 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0.115 / 16.0, 0 / 16.0),
      new Vector3(-3 / 16.0, 0.115 / 16.0, 16 / 16.0),
      new Vector4(14.999999999999998 / 16.0, 14.249999999999998 / 16.0, 9.75 / 16.0, 5.75 / 16.0)
    )
  };
  private static final Material material = new TextureMaterial(Texture.strawBed);

  private final String part;
  private final String facing;

  public StrawBedFrills(Vector3 position, String part, String facing) {
    super(position);
    this.part = part;
    this.facing = facing;
  }

  public StrawBedFrills(JsonObject json) {
    this(JsonUtil.vec3FromJsonObject(json.get("position")),
      json.get("part").stringValue("foot"), json.get("facing").stringValue("north"));
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE.translate(position).translate(offset);
    int direction = switch (facing) {
      case "east" -> 3;
      case "west" -> 1;
      case "south" -> 0;
      default -> 2;
    };
    Quad[] quads = Model.rotateY(part.equals("head") ? headQuads : footQuads,
      -Math.toRadians(90 * direction));
    for (Quad quad : quads) {
      quad.addTriangles(faces, material, transform);
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "strawBedFrills");
    json.add("position", position.toJson());
    json.add("part", part);
    json.add("facing", facing);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new StrawBedFrills(json);
  }
}
