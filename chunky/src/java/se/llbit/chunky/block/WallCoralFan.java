package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.WallCoralFanEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Collection;
import java.util.Collections;

public class WallCoralFan extends MinecraftBlockTranslucent {

  private final String coralType;
  private final String facing;

  public WallCoralFan(String name, String coralType, String facing) {
    super(name, CoralFan.coralTexture(coralType));
    this.coralType = coralType;
    this.facing = facing;
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

  @Override public Collection<Entity> toEntity(Vector3 position) {
    return Collections.singletonList(new WallCoralFanEntity(position, coralType, facing));
  }
}
