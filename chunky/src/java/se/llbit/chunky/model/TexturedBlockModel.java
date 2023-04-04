/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;
import se.llbit.math.AABB;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.annotation.Nullable;

/**
 * A textured block. This can either be statically textured or be oriented.
 */
public class TexturedBlockModel extends AABBModel {
  /**
   * A block orientation. Orientations with the {@code SIDE_} prefix do not rotate
   * the top and bottom faces of the block.
   */
  public enum Orientation {
    NORTH(false, null), SOUTH(false, null), EAST(false, null), WEST(false, null),
    SIDE_NORTH(true, NORTH), SIDE_SOUTH(true, SOUTH), SIDE_EAST(true, EAST), SIDE_WEST(true, WEST),

    ;
    public static Orientation fromFacing(String facing, boolean side) {
      switch (facing) {
        case "north":
          return side ? SIDE_NORTH : NORTH;
        case "south":
          return side ? SIDE_SOUTH : SOUTH;
        case "east":
          return side ? SIDE_EAST : EAST;
        case "west":
          return side ? SIDE_WEST : WEST;
        default:
          throw new IllegalArgumentException("Invalid facing: " + facing);
      }
    }

    /** Only rotate the side faces */
    public final boolean side;
    private final Orientation reduced;
    Orientation(boolean side, @Nullable Orientation reduced) {
      this.side = side;
      this.reduced = reduced;
    }

    public Orientation reduce() {
      return reduced == null ? this : reduced;
    }
  }

  protected static final AABB[] boxes = { new AABB(0, 1, 0, 1, 0, 1) };
  protected final Texture[][] textures;
  protected final UVMapping[][] mappings;
  protected final Tint[][] tints;

  public TexturedBlockModel(Texture north, Texture east, Texture south, Texture west, Texture top, Texture bottom) {
    this(new Texture[] { north, east, south, west, top, bottom }, null, null);
  }

  public TexturedBlockModel(Orientation orientation, Texture north, Texture east, Texture south, Texture west,
                            Texture top, Texture bottom, @Nullable Tint[] tints) {
    this(
      mapTextures(orientation, north, east, south, west, top, bottom),
      mapUV(orientation),
      tints
    );
  }

  public TexturedBlockModel(@NotNull Texture[] textures, @Nullable UVMapping[] mappings, @Nullable Tint[] tints) {
    this.textures = new Texture[][] { textures };
    this.mappings = mappings == null ? null : new UVMapping[][] { mappings };
    this.tints = tints == null ? null : new Tint[][] { tints };
  }

  private static Texture[] mapTextures(Orientation orientation, Texture north, Texture east, Texture south,
                                       Texture west, Texture top, Texture bottom) {
    switch (orientation.reduce()) {
      default:
        Log.warn("Unknown orientation: " + orientation);
      case NORTH:
        return new Texture[] { north, east, south, west, top, bottom };
      case SOUTH:
        return new Texture[] { south, west, north, east, top, bottom };
      case EAST:
        return new Texture[] { west, north, east, south, top, bottom };
      case WEST:
        return new Texture[] { east, south, west, north, top, bottom };
    }
  }

  private static UVMapping[] mapUV(Orientation orientation) {
    if (orientation.side) {
      return null;
    }
    switch (orientation) {
      default:
        Log.warn("Unknown orientation: " + orientation);
      case NORTH:
        return null;
      case SOUTH:
        return new UVMapping[] {null, null, null, null, UVMapping.ROTATE_180, UVMapping.ROTATE_180};
      case EAST:
        return new UVMapping[] {null, null, null, null, UVMapping.ROTATE_90, UVMapping.ROTATE_90};
      case WEST:
        return new UVMapping[] {null, null, null, null, UVMapping.ROTATE_270, UVMapping.ROTATE_270};
    }
  }

  @Override
  public final AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public final Texture[][] getTextures() {
    return textures;
  }

  @Override
  public final UVMapping[][] getUVMapping() {
    return mappings;
  }

  @Override
  public final Tint[][] getTints() {
    return tints;
  }
}
