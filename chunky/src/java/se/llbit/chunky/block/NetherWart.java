package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class NetherWart extends MinecraftBlockTranslucent implements ModelBlock {
  private static final Texture[] texture = {
      Texture.netherWart0, Texture.netherWart1, Texture.netherWart1, Texture.netherWart2
  };

  private final CropsModel model;
  private final int age;

  public NetherWart(int age) {
    super("nether_wart", Texture.netherWart2);
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
