package se.llbit.chunky.block;

import se.llbit.chunky.model.BarrelModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Barrel extends MinecraftBlock implements ModelBlock {
  private final BarrelModel model;
  private final String description;

  public Barrel(String facing, String open) {
    super("barrel", Texture.barrelSide);
    localIntersect = true;
    this.description = "facing=" + facing + ", open=" + open;
    this.model = new BarrelModel(facing, open);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
