package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.entity.WallHangingSignEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class WallHangingSign extends MinecraftBlockTranslucent {
  private final String material;
  private final Facing facing;

  public WallHangingSign(String name, String material, String facing) {
    super(name, SignEntity.textureFromMaterial(material));
    this.material = material;
    this.facing = Facing.fromString(facing);
    invisible = true;
    solid = false;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new WallHangingSignEntity(position, entityTag, facing, material);
  }

  public enum Facing {
    NORTH, EAST, SOUTH, WEST;

    public static Facing fromString(String facing) {
      switch (facing) {
        case "east":
          return EAST;
        case "south":
          return SOUTH;
        case "west":
          return WEST;
        case "north":
        default:
          return NORTH;
      }
    }

    @Override
    public String toString() {
      switch (this) {
        case EAST:
          return "east";
        case SOUTH:
          return "south";
        case WEST:
          return "west";
        case NORTH:
        default:
          return "north";
      }
    }
  }
}
