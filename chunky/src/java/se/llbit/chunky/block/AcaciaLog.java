package se.llbit.chunky.block;

import se.llbit.chunky.model.WoodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class AcaciaLog extends MinecraftBlock {
  public static final AcaciaLog INSTANCE = new AcaciaLog();

  private static final Texture[] texture = {
      Texture.acaciaWood, Texture.acaciaWoodTop
  };

  private AcaciaLog() {
    super("acacia_log", Texture.acaciaWood);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return WoodModel.intersect(ray, texture);
  }
}
