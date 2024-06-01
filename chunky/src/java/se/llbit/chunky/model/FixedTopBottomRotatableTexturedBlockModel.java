package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;

/**
 * A quad-based BlockModel which can be rotated horizontally (around y-axis) using a given facing.
 * Unlike TopBottomOrientedTexturedBlockModel, this ONLY rotates the sides - the top and bottom are not rotated.
 * Defaults to a full block, but can be customized by providing own quads.
 */
public class FixedTopBottomRotatableTexturedBlockModel extends QuadModel {

  protected final Quad[] quads;
  protected final Texture[] textures;

  /**
   * @param facing accepted values: "north", "south", "west", "east"
   * @param quads structured like: [north, south, west, east, top, bottom]
   * @param textures structured like: [north, south, west, east, top, bottom]
   */
  public FixedTopBottomRotatableTexturedBlockModel(
    String facing,
    Quad[] quads,
    Texture[] textures
  ) {
    this.quads = rotateToFacing(facing, quads);
    this.textures = textures;
  }

  public FixedTopBottomRotatableTexturedBlockModel(String facing,
                                                   Texture north, Texture east, Texture south, Texture west, Texture top, Texture bottom) {
    this(facing, FULL_BLOCK_QUADS, new Texture[]{north, south, west, east, top, bottom});
  }

  public static Quad[] rotateToFacing(String facing, Quad[] quads) {
    // if either top or bottom is missing, ignore
    Quad top = quads.length > 4 ? quads[4] : null;
    Quad bottom = quads.length > 5 ? quads[5] : null;
    switch (facing) {
      case "north":
        break;
      case "south":
        quads = Model.rotateY(Model.rotateY(quads));
        break;
      case "east":
        quads = Model.rotateY(quads, -Math.toRadians(90));
        break;
      case "west":
        quads = Model.rotateNegY(quads);
        break;
      default:
        throw new IllegalArgumentException("Invalid facing: " + facing);
    }
    if(top != null) {
      quads[4] = top;
    }
    if(bottom != null) {
      quads[5] = bottom;
    }
    return quads;
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
