package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class DecoratedPotModel extends TopBottomOrientedTexturedBlockModel {

  private static final Vector4 BASE_UV_MAP = new Vector4(0.0, 14 / 32.0, 8 / 32.0, 22 / 32.0);

  private static final Quad NORTH_SIDE = new Quad(
    new Vector3(15 / 16.0, 0, 1 / 16.0),
    new Vector3(1 / 16.0, 0, 1 / 16.0),
    new Vector3(15 / 16.0, 1, 1 / 16.0),
    new Vector4(1 / 16.0, 15 / 16.0, 0.0, 1.0));
  private static final Quad SOUTH_SIDE = new Quad(
    new Vector3(1 / 16.0, 0, 15 / 16.0),
    new Vector3(15 / 16.0, 0, 15 / 16.0),
    new Vector3(1 / 16.0, 1, 15 / 16.0),
    new Vector4(1 / 16.0, 15 / 16.0, 0.0, 1.0));
  private static final Quad WEST_SIDE = new Quad(
    new Vector3(1 / 16.0, 0, 1 / 16.0),
    new Vector3(1 / 16.0, 0, 15 / 16.0),
    new Vector3(1 / 16.0, 1, 1 / 16.0),
    new Vector4(1 / 16.0, 15 / 16.0, 0.0, 1.0));
  private static final Quad EAST_SIDE = new Quad(
    new Vector3(15 / 16.0, 0, 15 / 16.0),
    new Vector3(15 / 16.0, 0, 1 / 16.0),
    new Vector3(15 / 16.0, 1, 15 / 16.0),
    new Vector4(1 / 16.0, 15 / 16.0, 0.0, 1.0));
  private static final Quad TOP_SIDE = new Quad(
    new Vector3(1 / 16.0, 1, 15 / 16.0),
    new Vector3(15 / 16.0, 1, 15 / 16.0),
    new Vector3(1 / 16.0, 1, 1 / 16.0),
    BASE_UV_MAP);
  private static final Quad BOTTOM_SIDE = new Quad(
    new Vector3(1 / 16.0, 0, 1 / 16.0),
    new Vector3(15 / 16.0, 0, 1 / 16.0),
    new Vector3(1 / 16.0, 0, 15 / 16.0),
    BASE_UV_MAP);

  private static final Quad[] DEFAULT_QUADS = {
    NORTH_SIDE, SOUTH_SIDE,
    WEST_SIDE, EAST_SIDE,
    TOP_SIDE, BOTTOM_SIDE
  };

  public DecoratedPotModel(String facing, String[] shards) {
    super(facing, DEFAULT_QUADS, new Texture[]{
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
    });
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
        throw new IllegalArgumentException("Unknown pottery shard: " + shard);
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
