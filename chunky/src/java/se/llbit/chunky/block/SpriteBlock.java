package se.llbit.chunky.block;

import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SpriteBlock extends MinecraftBlockTranslucent {

  protected String facing;

  public SpriteBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
  }

  public SpriteBlock(String name, Texture texture, String facing) {
    this(name, texture);
    this.facing = facing;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    if (facing != null) {
      return SpriteModel.intersect(ray, texture, facing);
    }
    return SpriteModel.intersect(ray, texture);
  }

  @Override
  public String description() {
    if (facing != null) {
      return "facing=" + facing;
    }
    return super.description();
  }
}
