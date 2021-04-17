package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Beetroots extends MinecraftBlockTranslucent implements ModelBlock {
  private static final Texture[] texture = {
      Texture.beets0, Texture.beets1, Texture.beets2, Texture.beets3
  };

  private final CropsModel model;
  private final int age;

  public Beetroots(int age) {
    super("beetroots", Texture.beets3);
    localIntersect = true;
    this.age = age & 3;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "age=" + age;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
