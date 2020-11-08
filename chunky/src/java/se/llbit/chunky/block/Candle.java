package se.llbit.chunky.block;

import se.llbit.chunky.model.CandleModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Candle extends MinecraftBlockTranslucent {

  private final Texture candle;
  private final int candles;
  private final boolean lit;

  public Candle(String name, Texture candle, int candles, boolean lit) {
    super(name, candle);
    this.candle = candle;
    this.candles = Math.max(1, Math.min(4, candles));
    this.lit = lit;
    localIntersect = true;
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CandleModel.intersect(ray, candle, candles, isLit());
  }

  @Override
  public String description() {
    return "candles=" + candle + ", lit=" + isLit();
  }
}
