package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.HangingSignEntity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class HangingSign extends MinecraftBlockTranslucent {
  private final String material;
  private final int rotation;
  private final boolean attached;

  public HangingSign(String name, String material, int rotation, boolean attached) {
    super(name, SignEntity.textureFromMaterial(material));
    this.material = material;
    this.rotation = rotation;
    this.attached = attached;
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
    return new HangingSignEntity(position, entityTag, rotation, attached, material);
  }
}
