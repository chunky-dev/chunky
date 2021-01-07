package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.entity.WallSignEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class WallSign extends MinecraftBlockTranslucent {
  private final int facing;
  private final String material;

  public WallSign(String name, String material, String facing) {
    super(name, SignEntity.textureFromMaterial(material));
    invisible = true;
    opaque = false;
    localIntersect = true;
    this.material = material;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isBlockEntity(CompoundTag entityTag) {
    return true;
  }

  @Override public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new WallSignEntity(position, entityTag, facing, material);
  }
}
