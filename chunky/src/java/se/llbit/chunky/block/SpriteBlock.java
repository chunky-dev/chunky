package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SpriteBlock extends MinecraftBlockTranslucent implements ModelBlock {

  private final SpriteModel model;
  protected String facing;

  public SpriteBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    model = new SpriteModel(texture);
  }

  public SpriteBlock(String name, Texture texture, String facing) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    model = new SpriteModel(texture, facing);
    this.facing = facing;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    if (facing != null) {
      return "facing=" + facing;
    }
    return super.description();
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
