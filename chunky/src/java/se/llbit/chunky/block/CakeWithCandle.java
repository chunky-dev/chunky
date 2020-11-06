package se.llbit.chunky.block;

import se.llbit.chunky.model.CakeWithCandleModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class CakeWithCandle extends MinecraftBlockTranslucent {

  private final Texture candle;

  public CakeWithCandle(String name, Texture candle) {
    super(name, Texture.cakeTop);
    this.candle = candle;
    localIntersect = true;
    // TODO find out whether the candle is lit
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CakeWithCandleModel.intersect(ray, candle);
  }
}
