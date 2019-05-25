package se.llbit.chunky.block;

import se.llbit.chunky.entity.CoralFanEntity;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CoralFan extends MinecraftBlockTranslucent {

  public CoralFan(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    invisible = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity toEntity(Vector3 position) {
    return new CoralFanEntity(position);
  }
}
