package se.llbit.chunky.block;

import se.llbit.chunky.model.CakeWithCandleModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class CakeWithCandle extends MinecraftBlockTranslucent {

  private final Texture candle;
  private final boolean lit;

  public CakeWithCandle(String name, Texture candle, boolean lit) {
    super(name, Texture.cakeTop);
    this.candle = candle;
    this.lit = lit;
    localIntersect = true;
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CakeWithCandleModel.intersect(ray, candle, isLit());
  }

  @Override
  public String description() {
    return "lit=" + isLit();
  }
}
