package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.CopperGolemEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

public class CopperGolemStatue extends MinecraftBlockTranslucent {
  private final String facing;
  private final String pose;

  public CopperGolemStatue(String name, Texture texture, String facing, String pose) {
    super(name, texture);
    this.facing = facing;
    this.pose = pose;
    invisible = true;
    opaque = false;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public boolean isEntity() {
    return true;
  }

  @Override
  public Entity toEntity(Vector3 position) {
    position = new Vector3(position);
    position.add(0.5, 0, 0.5);
    CopperGolemEntity entity = new CopperGolemEntity(position);
    entity.applyFacing(facing);
    return entity;
  }
}
