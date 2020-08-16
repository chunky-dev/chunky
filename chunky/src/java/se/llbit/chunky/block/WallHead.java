package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.HeadEntity;
import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.entity.SkullEntity.Kind;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class WallHead extends MinecraftBlockTranslucent {

  private final String description;
  private final int facing;
  private final SkullEntity.Kind type;

  public WallHead(String name, EntityTexture texture, SkullEntity.Kind type, String facing) {
    super(name, texture);
    localIntersect = true;
    invisible = true;
    description = "facing=" + facing;
    this.type = type;
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

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public boolean isEntity() {
    return type != Kind.PLAYER;
  }

  @Override
  public Entity toEntity(Vector3 position) {
    return new SkullEntity(position, type, 0, facing);
  }

  @Override
  public boolean isBlockEntity() {
    return type == Kind.PLAYER;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    String textureUrl = Head.getTextureUrl(entityTag);
    return textureUrl != null ? new HeadEntity(position, textureUrl, 0, facing)
        : new SkullEntity(position, type, 0, facing);
  }
}
