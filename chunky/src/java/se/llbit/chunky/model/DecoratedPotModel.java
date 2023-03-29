package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Transform;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.Collection;
import java.util.LinkedList;

public class DecoratedPotModel extends OrientedQuadModel {

  private static final Vector4 BASE_UV_MAP = new Vector4(0 / 32., 14 / 32., 8 / 32., 22 / 32.);
  private static final Vector4 SIDE_UV_MAP = new Vector4(1 / 16., 15 / 16., 0., 1.);

  private static final Quad NORTH_SIDE = new Quad(
    new Vector3(15 / 16., 0, 1 / 16.),
    new Vector3(1 / 16., 0, 1 / 16.),
    new Vector3(15 / 16., 1, 1 / 16.),
    SIDE_UV_MAP);
  private static final Quad SOUTH_SIDE = new Quad(
    new Vector3(1 / 16., 0, 15 / 16.),
    new Vector3(15 / 16., 0, 15 / 16.),
    new Vector3(1 / 16., 1, 15 / 16.),
    SIDE_UV_MAP);
  private static final Quad WEST_SIDE = new Quad(
    new Vector3(1 / 16., 0, 1 / 16.),
    new Vector3(1 / 16., 0, 15 / 16.),
    new Vector3(1 / 16., 1, 1 / 16.),
    SIDE_UV_MAP);
  private static final Quad EAST_SIDE = new Quad(
    new Vector3(15 / 16., 0, 15 / 16.),
    new Vector3(15 / 16., 0, 1 / 16.),
    new Vector3(15 / 16., 1, 15 / 16.),
    SIDE_UV_MAP);
  private static final Quad TOP_SIDE = new Quad(
    new Vector3(1 / 16., 1, 15 / 16.),
    new Vector3(15 / 16., 1, 15 / 16.),
    new Vector3(1 / 16., 1, 1 / 16.),
    BASE_UV_MAP);
  private static final Quad BOTTOM_SIDE = new Quad(
    new Vector3(1 / 16., 0, 1 / 16.),
    new Vector3(15 / 16., 0, 1 / 16.),
    new Vector3(1 / 16., 0, 15 / 16.),
    BASE_UV_MAP);

  private static final Quad[] DEFAULT_QUADS = {
    NORTH_SIDE, SOUTH_SIDE,
    WEST_SIDE, EAST_SIDE,
    TOP_SIDE, BOTTOM_SIDE,
  };

  public static class DecoratedPotSpoutEntity extends Entity {

    private static final Quad[] QUADS = {
      new Quad( // north
        new Vector3(11 / 16., 1, 5 / 16.),
        new Vector3(5 / 16., 1, 5 / 16.),
        new Vector3(11 / 16., 20 / 16., 5 / 16.),
        new Vector4(18 / 32., 24 / 32., 22 / 32., 26 / 32.)),
      new Quad( // south
        new Vector3(5 / 16., 1, 11 / 16.),
        new Vector3(11 / 16., 1, 11 / 16.),
        new Vector3(5 / 16., 20 / 16., 11 / 16.),
        new Vector4(6 / 32., 12 / 32., 22 / 32., 26 / 32.)),
      new Quad( // west
        new Vector3(5 / 16., 1, 5 / 16.),
        new Vector3(5 / 16., 1, 11 / 16.),
        new Vector3(5 / 16., 20 / 16., 5 / 16.),
        new Vector4(0 / 32., 6 / 32., 22 / 32., 26 / 32.)),
      new Quad( // east
        new Vector3(11 / 16., 1, 11 / 16.),
        new Vector3(11 / 16., 1, 5 / 16.),
        new Vector3(11 / 16., 20 / 16., 11 / 16.),
        new Vector4(12 / 32., 18 / 32., 22 / 32., 26 / 32.)),
      new Quad( // top
        new Vector3(5 / 16., 20 / 16., 11 / 16.),
        new Vector3(11 / 16., 20 / 16., 11 / 16.),
        new Vector3(5 / 16., 20 / 16., 5 / 16.),
        new Vector4(6 / 32., 12 / 32., 26 / 32., 32 / 32.)),
    };

    private final Material material = new TextureMaterial(Texture.decoratedPotBase);
    private final String facing;

    public DecoratedPotSpoutEntity(Vector3 position, String facing) {
      super(position);
      this.facing = facing;
    }

    @Override
    public Collection<Primitive> primitives(Vector3 offset) {
      Collection<Primitive> primitives = new LinkedList<>();
      Transform transform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
      for (Quad quad : rotateToFacing(TexturedBlockModel.Orientation.fromFacing(facing, false), QUADS)) {
        quad.addTriangles(primitives, material, transform);
      }
      return primitives;
    }

    public static Entity fromJson(JsonObject json) {
      return new DecoratedPotSpoutEntity(
        JsonUtil.vec3FromJsonObject(json.get("position")),
        json.get("facing").stringValue("north")
      );
    }

    @Override
    public JsonValue toJson() {
      JsonObject json = new JsonObject();
      json.add("kind", "decoratedPotSpout");
      json.add("position", position.toJson());
      json.add("facing", facing);
      return json;
    }
  }

  public DecoratedPotModel(String facing, String[] shards) {
    super(TexturedBlockModel.Orientation.fromFacing(facing, false), DEFAULT_QUADS, new Texture[]{
      // shards[0] top crafting slot -> north
      getTextureForShard(shards[0]),
      // shards[3] bottom crafting slot -> south
      getTextureForShard(shards[3]),
      // shards[1] left crafting slot -> west
      getTextureForShard(shards[1]),
      // shards[2] right crafting slot -> east
      getTextureForShard(shards[2]),
      Texture.decoratedPotBase, // top
      Texture.decoratedPotBase  // bottom
    }, null);
  }

  private static Texture getTextureForShard(String shard) {
    if(shard == null)
      return Texture.decoratedPotSide;
    switch(shard) {
      case "minecraft:pottery_shard_skull":
        return Texture.decoratedPotPatternSkull;
      case "minecraft:pottery_shard_prize":
        return Texture.decoratedPotPatternPrize;
      case "minecraft:pottery_shard_archer":
        return Texture.decoratedPotPatternArcher;
      case "minecraft:pottery_shard_arms_up":
        return Texture.decoratedPotPatternArmsUp;
      default:
        Log.warn("Unknown pottery shard: " + shard);
        return Texture.decoratedPotSide;
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
