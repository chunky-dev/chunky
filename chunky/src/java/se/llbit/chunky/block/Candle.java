package se.llbit.chunky.block;

import se.llbit.chunky.model.CandleModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Candle extends MinecraftBlockTranslucent {

  private final Texture candle;

  public Candle(String name, Texture candle) {
    super(name, candle);
    this.candle = candle;
    localIntersect = true;
    // TODO find out whether the candle is lit
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CandleModel.intersect(ray, candle);
  }
}
