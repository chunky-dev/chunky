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
  private final String material;

  public Sign(String name, String material, int rotation) {
    super(name, SignEntity.textureFromMaterial(material));
    invisible = true;
    solid = false;
    localIntersect = true;
    this.rotation = rotation % 16;
    this.material = material;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isBlockEntity() {
    return true;
  }

  @Override public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new SignEntity(position, entityTag, rotation, material);
  }
}
