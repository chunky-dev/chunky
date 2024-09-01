package se.llbit.chunky.model;

import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;

/**
 * A quad-based BlockModel which can be rotated horizontally (around y-axis) using a given facing.
 * Defaults to a full block, but can be customized by providing own quads.
 */
public class TopBottomOrientedTexturedBlockModel extends QuadModel {

  protected final Quad[] quads;
  protected final AbstractTexture[] textures;

  /**
   * @param facing accepted values: "north", "south", "west", "east"
   * @param quads structured like: [north, south, west, east, top, bottom]
   * @param textures structured like: [north, south, west, east, top, bottom]
   */
  public TopBottomOrientedTexturedBlockModel(
    String facing,
    Quad[] quads,
    AbstractTexture[] textures
  ) {
    this.quads = rotateToFacing(facing, quads);
    this.textures = textures;
  }

  public TopBottomOrientedTexturedBlockModel(String facing,
                                             AbstractTexture north, AbstractTexture east, AbstractTexture south, AbstractTexture west, AbstractTexture top, AbstractTexture bottom) {
    this(facing, FULL_BLOCK_QUADS, new AbstractTexture[]{north, south, west, east, top, bottom});
  }

  public static Quad[] rotateToFacing(String facing, Quad[] quads) {
    switch (facing) {
      case "north":
        return quads;
      case "south":
        return Model.rotateY(Model.rotateY(quads));
      case "east":
        return Model.rotateY(quads, -Math.toRadians(90));
      case "west":
        return Model.rotateNegY(quads);
      default:
        throw new IllegalArgumentException("Invalid facing: " + facing);
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
