package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.HeadEntity;
import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.LinkedList;

public class Head extends MinecraftBlockTranslucent {
  private final String description;
  private final int rotation;
  private final SkullEntity.Kind type;

  public Head(String name, EntityTexture texture, SkullEntity.Kind type, int rotation) {
    super(name, texture);
    localIntersect = true;
    invisible = true;
    description = "rotation=" + rotation;
    this.type = type;
    this.rotation = rotation;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public String description() {
    return description;
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity toEntity(Vector3 position) {
    return new SkullEntity(position, type, rotation, 1);
  }
}
