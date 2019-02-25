package se.llbit.chunky.block;

import se.llbit.chunky.model.BeaconModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.math.Ray;

public class Beacon extends MinecraftBlock {
  public Beacon() {
    super("beacon", Texture.beacon);
    localIntersect = true;
    opaque = false;
    solid = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return BeaconModel.intersect(ray);
  }
}
