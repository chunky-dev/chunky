package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;

public class Leaves extends MinecraftBlock implements ModelBlock {
  private final LeafModel model;

  public Leaves(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    opaque = false;
    this.model = new LeafModel(texture);
  }

  public Leaves(String name, Texture texture, int tint) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    opaque = false;
    this.model = new LeafModel(texture, tint);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
