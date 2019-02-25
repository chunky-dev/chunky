package se.llbit.chunky.block;

import se.llbit.chunky.model.BedModel;
import se.llbit.chunky.model.TerracottaModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Bed extends MinecraftBlock {
  private final int head, facing;

  public Bed(String name, Texture texture, int head, int facing) {
    super(name, texture);
    this.head = head;
    this.facing = facing;
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return BedModel.intersect(ray, texture, head, facing);
  }
}
