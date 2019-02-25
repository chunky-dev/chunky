package se.llbit.chunky.block;

import se.llbit.chunky.model.TerracottaModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class GlazedTerracotta extends MinecraftBlock {
  private final int facing;

  public GlazedTerracotta(String name, Texture texture, int facing) {
    super(name, texture);
    this.facing = facing;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TerracottaModel.intersect(ray, texture, facing);
  }
}
