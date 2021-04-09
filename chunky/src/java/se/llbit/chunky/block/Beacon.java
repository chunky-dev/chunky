package se.llbit.chunky.block;

import se.llbit.chunky.entity.BeaconBeam;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.BeaconModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class Beacon extends MinecraftBlockTranslucent implements ModelBlock {
  private static BeaconModel model = new BeaconModel();

  public Beacon() {
    super("beacon", Texture.beacon);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public boolean isBlockWithEntity() { return true; }

  @Override public boolean isBlockEntity() {
    return true;
  }

  @Override public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    if (entityTag.get("Levels").intValue(0) > 0) {
      return new BeaconBeam(position);
    }
    return null;
  }

  @Override
  public BlockModel getModel() {
    return null;
  }
}
