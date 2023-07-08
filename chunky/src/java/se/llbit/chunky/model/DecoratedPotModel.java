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

public class DecoratedPotModel extends TopBottomOrientedTexturedBlockModel {

  private static final Vector4 TOP_UV_MAP = new Vector4(14 / 32., 28 / 32., 19 / 32., 5 / 32.);
  private static final Vector4 BOTTOM_UV_MAP = new Vector4(6.5 / 16, 6.5 / 16, 5.5 / 16, 5.5 / 16);
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
    TOP_UV_MAP);
  private static final Quad BOTTOM_SIDE = new Quad(
    new Vector3(1 / 16., 0, 1 / 16.),
    new Vector3(15 / 16., 0, 1 / 16.),
    new Vector3(1 / 16., 0, 15 / 16.),
    BOTTOM_UV_MAP);

  private static final Quad[] DEFAULT_QUADS = {
    NORTH_SIDE, SOUTH_SIDE,
    WEST_SIDE, EAST_SIDE,
    TOP_SIDE, BOTTOM_SIDE,
  };

  public static class DecoratedPotSpoutEntity extends Entity {

    private static final Quad[] QUADS = {
      // top section
      new Quad( // north
        new Vector3(12 / 16., 17 / 16., 4 / 16.),
        new Vector3(4 / 16., 17 / 16., 4 / 16.),
        new Vector3(12 / 16., 20 / 16., 4 / 16.),
        new Vector4(24 / 32., 32 / 32., 21 / 32., 24 / 32.)),
      new Quad( // south
        new Vector3(4 / 16., 17 / 16., 12 / 16.),
        new Vector3(12 / 16., 17 / 16., 12 / 16.),
        new Vector3(4 / 16., 20 / 16., 12 / 16.),
        new Vector4(8 / 32., 16 / 32., 21 / 32., 24 / 32.)),
      new Quad( // west
        new Vector3(4 / 16., 17 / 16., 4 / 16.),
        new Vector3(4 / 16., 17 / 16., 12 / 16.),
        new Vector3(4 / 16., 20 / 16., 4 / 16.),
        new Vector4(0 / 32., 8 / 32., 21 / 32., 24 / 32.)),
      new Quad( // east
        new Vector3(12 / 16., 17 / 16., 12 / 16.),
        new Vector3(12 / 16., 17 / 16., 4 / 16.),
        new Vector3(12 / 16., 20 / 16., 12 / 16.),
        new Vector4(16 / 32., 24 / 32., 21 / 32., 24 / 32.)),
      new Quad( // top
        new Vector3(4 / 16., 20 / 16., 12 / 16.),
        new Vector3(12 / 16., 20 / 16., 12 / 16.),
        new Vector3(4 / 16., 20 / 16., 4 / 16.),
        new Vector4(8 / 32., 16 / 32., 24 / 32., 32 / 32.)),
      new Quad( // bottom
        new Vector3(4 / 16., 17 / 16., 4 / 16.),
        new Vector3(12 / 16., 17 / 16., 4 / 16.),
        new Vector3(4 / 16., 17 / 16., 12 / 16.),
        new Vector4(16 / 32., 24 / 32., 32 / 32., 24 / 32.)),

      // throat
      new Quad( // north
        new Vector3(11 / 16., 1, 5 / 16.),
        new Vector3(5 / 16., 1, 5 / 16.),
        new Vector3(11 / 16., 17 / 16., 5 / 16.),
        new Vector4(18 / 32., 24 / 32., 20 / 32., 21 / 32.)),
      new Quad( // south
        new Vector3(5 / 16., 1, 11 / 16.),
        new Vector3(11 / 16., 1, 11 / 16.),
        new Vector3(5 / 16., 17 / 16., 11 / 16.),
        new Vector4(6 / 32., 12 / 32., 20 / 32., 21 / 32.)),
      new Quad( // west
        new Vector3(5 / 16., 1, 5 / 16.),
        new Vector3(5 / 16., 1, 11 / 16.),
        new Vector3(5 / 16., 17 / 16., 5 / 16.),
        new Vector4(0 / 32., 6 / 32., 20 / 32., 21 / 32.)),
      new Quad( // east
        new Vector3(11 / 16., 1, 11 / 16.),
        new Vector3(11 / 16., 1, 5 / 16.),
        new Vector3(11 / 16., 17 / 16., 11 / 16.),
        new Vector4(12 / 32., 18 / 32., 20 / 32., 21 / 32.)),
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
      for (Quad quad : rotateToFacing(facing, QUADS)) {
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

  public DecoratedPotModel(String facing, String[] sherds) {
    super(facing, DEFAULT_QUADS, new Texture[]{
      // sherds[0] top crafting slot -> north
      getTextureForsherd(sherds[0]),
      // sherds[3] bottom crafting slot -> south
      getTextureForsherd(sherds[3]),
      // sherds[1] left crafting slot -> west
      getTextureForsherd(sherds[1]),
      // sherds[2] right crafting slot -> east
      getTextureForsherd(sherds[2]),
      Texture.decoratedPotBase, // top
      Texture.decoratedPotSide  // bottom
    });
  }

  private static Texture getTextureForsherd(String sherd) {
    if(sherd == null) {
      return Texture.decoratedPotSide;
    }

    switch(sherd) {
      case "minecraft:angler_pottery_sherd":
        return Texture.decoratedPotPatternAngler;
      case "minecraft:archer_pottery_sherd":
        return Texture.decoratedPotPatternArcher;
      case "minecraft:arms_up_pottery_sherd":
        return Texture.decoratedPotPatternArmsUp;
      case "minecraft:blade_pottery_sherd":
        return Texture.decoratedPotPatternBlade;
      case "minecraft:brewer_pottery_sherd":
        return Texture.decoratedPotPatternBrewer;
      case "minecraft:burn_pottery_sherd":
        return Texture.decoratedPotPatternBurn;
      case "minecraft:danger_pottery_sherd":
        return Texture.decoratedPotPatternDanger;
      case "minecraft:explorer_pottery_sherd":
        return Texture.decoratedPotPatternExplorer;
      case "minecraft:friend_pottery_sherd":
        return Texture.decoratedPotPatternFriend;
      case "minecraft:heartbreak_pottery_sherd":
        return Texture.decoratedPotPatternHeartbreak;
      case "minecraft:heart_pottery_sherd":
        return Texture.decoratedPotPatternHeart;
      case "minecraft:howl_pottery_sherd":
        return Texture.decoratedPotPatternHowl;
      case "minecraft:miner_pottery_sherd":
        return Texture.decoratedPotPatternMiner;
      case "minecraft:moutner_pottery_sherd":
        return Texture.decoratedPotPatternMourner;
      case "minecraft:plenty_pottery_sherd":
        return Texture.decoratedPotPatternPlenty;
      case "minecraft:prize_pottery_sherd":
        return Texture.decoratedPotPatternPrize;
      case "minecraft:sheaf_pottery_sherd":
        return Texture.decoratedPotPatternSheaf;
      case "minecraft:shelter_pottery_sherd":
        return Texture.decoratedPotPatternShelter;
      case "minecraft:skull_pottery_sherd":
        return Texture.decoratedPotPatternSkull;
      case "minecraft:snort_pottery_sherd":
        return Texture.decoratedPotPatternSnort;
      default:
        Log.warn("Unknown pottery sherd: " + sherd);
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
