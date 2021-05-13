package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.Stairs;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyStairs extends UnfinalizedLegacyBlock {

  public LegacyStairs(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    int rotation = 0b0011 & data;
    boolean isTop = (0b0100 & data) != 0;

    switch (rotation) {
      case 0: {
        // east
        Material behind = state.getMaterial(1, 0, 0);
        Material front = state.getMaterial(-1, 0, 0);
        if (isStairs(behind) && isTop(behind) == isTop) {
          switch (getOrientation(behind)) {
            case 2:
              // south
              // outer s-e corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, 0, 0, -1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_right"));
                return;
              }
              break;
            case 3:
              // north
              // outer n-e corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, 0, 0, 1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_left"));
                return;
              }
              break;
          }
        } else if (isStairs(front) && isTop(front) == isTop) {
          switch (getOrientation(front)) {
            case 2:
              // south
              // inner s-e corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, 0, 0, 1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_right"));
                return;
              }
              break;
            case 3:
              // north
              // inner n-e corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, 0, 0, -1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_left"));
                return;
              }
              break;
          }
        }
        break;
      }
      case 1: {
        // west
        Material behind = state.getMaterial(-1, 0, 0);
        Material front = state.getMaterial(1, 0, 0);
        if (isStairs(behind) && isTop(behind) == isTop) {
          switch (getOrientation(behind)) {
            case 2:
              // south
              // outer s-w corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, 0, 0, -1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_left"));
                return;
              }
              break;
            case 3:
              // north
              // outer n-w corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, 0, 0, 1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_right"));
                return;
              }
              break;
          }
        } else if (isStairs(front) && isTop(front) == isTop) {
          switch (getOrientation(front)) {
            case 2:
              // south
              // inner s-w corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, 0, 0, 1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_right"));
                return;
              }
              break;
            case 3:
              // north
              // inner n-w corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, 0, 0, -1)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_left"));
                return;
              }
              break;
          }
        }
        break;
      }
      case 2: {
        // south
        Material behind = state.getMaterial(0, 0, 1);
        Material front = state.getMaterial(0, 0, -1);
        if (isStairs(behind) && isTop(behind) == isTop) {
          switch (getOrientation(behind)) {
            case 0:
              // east
              // outer s-e corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, -1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_left"));
                return;
              }
              break;
            case 1:
              // west
              // outer s-w corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, 1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_right"));
                return;
              }
              break;
          }
        } else if (isStairs(front) && isTop(front) == isTop) {
          switch (getOrientation(front)) {
            case 0:
              // east
              // inner s-e corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, 1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_left"));
                return;
              }
              break;
            case 1:
              // west
              // inner s-w corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, -1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_right"));
                return;
              }
              break;
          }
        }
        break;
      }
      case 3: {
        // north
        Material behind = state.getMaterial(0, 0, -1);
        Material front = state.getMaterial(0, 0, 1);
        if (isStairs(behind) && isTop(behind) == isTop) {
          switch (getOrientation(behind)) {
            case 0:
              // east
              // outer n-e corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, -1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_right"));
                return;
              }
              break;
            case 1:
              // west
              // outer n-w corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, 1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "outer_left"));
                return;
              }
              break;
          }
        } else if (isStairs(front) && isTop(front) == isTop) {
          switch (getOrientation(front)) {
            case 0:
              // east
              // inner n-e corner unless the stair to the left has the same orientation
              if (!isSameStairs(state, 1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_left"));
                return;
              }
              break;
            case 1:
              // west
              // inner n-w corner unless the stair to the right has the same orientation
              if (!isSameStairs(state, -1, 0, 0)) {
                state.replaceCurrentBlock(createTag(rotation, isTop, "inner_right"));
                return;
              }
              break;
          }
        }
        break;
      }
    }

    // straight stairs, unwrap
    state.replaceCurrentBlock(tag);
  }

  /**
   * Create a tag for a stairs block with the name of this block and the given properties.
   *
   * @param rotation Stairs-specific rotation
   * @param top      Whether the stairs block should be upside-down (i.e. half=top)
   * @param shape    The shape of the stairs block
   * @return Tag for a stairs block with the name of this block and the given properties
   */
  private CompoundTag createTag(int rotation, boolean top, String shape) {
    CompoundTag tag = LegacyBlocks.createTag(block.name);
    LegacyBlocks.stringTag(tag, "half", top ? "top" : "bottom");
    LegacyBlocks.stringTag(tag, "shape", shape);
    LegacyBlocks.stringTag(tag, "facing", getFacing(rotation).getName());
    return tag;
  }

  /**
   * Get the facing of this stairs block.
   *
   * @return Facing, i.e. north, south, east, west
   */
  public BlockFace getFacing() {
    return getFacing(data);
  }

  /**
   * Get the 1.13+ facing string from the given rotation.
   *
   * @param rotation Rotation data value
   * @return Facing
   */
  public static BlockFace getFacing(int rotation) {
    return new BlockFace[]{
        BlockFace.EAST,
        BlockFace.WEST,
        BlockFace.SOUTH,
        BlockFace.NORTH
    }[rotation & 0b11];
  }

  /**
   * Checks if this stairs block has the same orientation and half as an adjacent stairs block.
   *
   * @param state Finalization state
   * @param rx    Relative x coordinate
   * @param ry    Relative y coordinate
   * @param rz    Relative z coordinate
   * @return True if the adjacent block is a stairs block and has the same orientation and half as
   * this block, false otherwise
   */
  private boolean isSameStairs(FinalizationState state, int rx, int ry, int rz) {
    Material other = state.getMaterial(rx, ry, rz);
    if (other instanceof LegacyStairs) {
      return (((LegacyStairs) other).data & 0b111) == (data & 0b111);
    } else if (other instanceof Stairs) {
      return getOrientation(other) == (data & 0b11) && isTop(other) == ((data & 0b100) != 0);
    }
    return false;
  }

  /**
   * Get the stairs-specific orientation of the given block.
   *
   * @param material Block
   * @return Orientation, 3 (north) if the block isn't a stairs block
   */
  private static int getOrientation(Material material) {
    if (material instanceof LegacyStairs) {
      return 0b11 & ((LegacyStairs) material).data;
    } else if (material instanceof Stairs) {
      switch (((Stairs) material).getFacing()) {
        case EAST:
          return 0;
        case WEST:
          return 1;
        case SOUTH:
          return 2;
        case NORTH:
        default:
          return 3;
      }
    }
    return 3;
  }

  /**
   * Check if the given material is an upside-down stairs block (i.e. half=top).
   *
   * @param material Block
   * @return True if the given block is an upside-down stairs block, false otherwise
   */
  private boolean isTop(Material material) {
    if (material instanceof LegacyStairs) {
      return (0b100 & ((LegacyStairs) material).data) != 0;
    } else if (material instanceof Stairs) {
      return ((Stairs) material).getHalf().equals("top");
    }
    return false;
  }

  /**
   * Check if the given block is a stairs block.
   *
   * @param material Block
   * @return True if the given block is a stairs block, false otherwise
   */
  private static boolean isStairs(Material material) {
    return material.name.endsWith("_stairs");
  }
}
