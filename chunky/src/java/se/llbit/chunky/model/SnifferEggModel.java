package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SnifferEggModel extends QuadModel {
  //region Textures
  private static final Texture[][] textures = new Texture[][]{
    new Texture[]{
      Texture.snifferEggNotCrackedTop, Texture.snifferEggNotCrackedBottom,
      Texture.snifferEggNotCrackedWest, Texture.snifferEggNotCrackedEast,
      Texture.snifferEggNotCrackedNorth, Texture.snifferEggNotCrackedSouth
    },
    new Texture[]{
      Texture.snifferEggSlightlyCrackedTop, Texture.snifferEggSlightlyCrackedBottom,
      Texture.snifferEggSlightlyCrackedWest, Texture.snifferEggSlightlyCrackedEast,
      Texture.snifferEggSlightlyCrackedNorth, Texture.snifferEggSlightlyCrackedSouth
    },
    new Texture[]{
      Texture.snifferEggVeryCrackedTop, Texture.snifferEggVeryCrackedBottom,
      Texture.snifferEggVeryCrackedWest, Texture.snifferEggVeryCrackedEast,
      Texture.snifferEggVeryCrackedNorth, Texture.snifferEggVeryCrackedSouth
    },
  };
  //endregion

  //region Quads
  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector4(0 / 16.0, 14 / 16.0, 4 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(0 / 16.0, 14 / 16.0, 4 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector4(12 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(15 / 16.0, 16 / 16.0, 2 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 2 / 16.0),
      new Vector4(14 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(1 / 16.0, 16 / 16.0, 14 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 14 / 16.0),
      new Vector4(14 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };
  //endregion

  private final int age;

  public SnifferEggModel(int age) {
    this.age = QuickMath.clamp(age, 0, 2);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures[age];
  }
}
