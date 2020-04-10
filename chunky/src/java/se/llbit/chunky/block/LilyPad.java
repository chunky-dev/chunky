package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.LilyPadEntity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class LilyPad extends MinecraftBlockTranslucent {
  public LilyPad() {
    super("lily_pad", Texture.lilyPad);
    invisible = true;
    opaque = false;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity toEntity(Vector3 position) {
    return new LilyPadEntity(position);
  }
}
