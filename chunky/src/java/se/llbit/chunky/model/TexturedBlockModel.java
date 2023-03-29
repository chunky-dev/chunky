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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.annotation.Nullable;

/**
 * A textured block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturedBlockModel extends AABBModel {
  public enum Orientation {
    NORTH, SOUTH, EAST, WEST,
    SIDE_NORTH, SIDE_SOUTH, SIDE_EAST, SIDE_WEST,

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
  }

  protected static final AABB box = new AABB(0, 1, 0, 1, 0, 1);
  protected final Texture[] textures;
  protected final UVMapping[] mappings;
  protected final Tint[] tints;

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
    this.textures = textures;
    this.textures2 = new Texture[][] { textures };

    this.mappings = mappings;
    this.mappings2 = mappings == null ? null : new UVMapping[][] { mappings };

    this.tints = tints;
    this.tints2 = tints == null ? null : new Tint[][] { tints };
  }

  private static Texture[] mapTextures(Orientation orientation, Texture north, Texture east, Texture south,
                                       Texture west, Texture top, Texture bottom) {
    switch (orientation) {
      default:
        Log.warn("Unknown orientation: " + orientation);
      case NORTH:
      case SIDE_NORTH:
        return new Texture[] { north, east, south, west, top, bottom };
      case SOUTH:
      case SIDE_SOUTH:
        return new Texture[] { south, west, north, east, top, bottom };
      case EAST:
      case SIDE_EAST:
        return new Texture[] { west, north, east, south, top, bottom };
      case WEST:
      case SIDE_WEST:
        return new Texture[] { east, south, west, north, top, bottom };
    }
  }

  private static UVMapping[] mapUV(Orientation orientation) {
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
      case SIDE_EAST:
      case SIDE_SOUTH:
      case SIDE_NORTH:
      case SIDE_WEST:
        return null;
    }
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    // TODO: Optimize
    return super.intersect(ray, scene);
  }

  // This stuff is just to adhere to the interface.
  private static final AABB[] boxes = { box };
  private final Texture[][] textures2;
  private final UVMapping[][] mappings2;
  private final Tint[][] tints2;

  @Override
  public final AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public final Texture[][] getTextures() {
    return textures2;
  }

  @Override
  public final UVMapping[][] getUVMapping() {
    return mappings2;
  }

  @Override
  public final Tint[][] getTints() {
    return tints2;
  }
}
