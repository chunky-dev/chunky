package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class Sign extends MinecraftBlockTranslucent {
  private final int rotation;

  public Sign(String name, Texture texture, int rotation) {
    super(name, texture);
    invisible = true;
    opaque = false;
    localIntersect = true;
    this.rotation = rotation % 16;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isBlockEntity() {
    return true;
  }

  @Override public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new SignEntity(position, entityTag, rotation, texture);
  }
}
