package se.llbit.chunky.block;

import se.llbit.chunky.entity.BeaconBeam;
import se.llbit.chunky.entity.Book;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.model.BeaconModel;
import se.llbit.chunky.model.EnchantmentTableModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class Beacon extends MinecraftBlockTranslucent {

  public Beacon(boolean beamOn) {
    super("beacon", Texture.beacon);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return BeaconModel.intersect(ray);
  }

  @Override
  public boolean isBlockWithEntity() { return true; }

  @Override public boolean isBlockEntity(CompoundTag entityTag) {
    return entityTag.get("Levels").intValue(0) > 0;
  }

  @Override public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new BeaconBeam(position);
  }
}
