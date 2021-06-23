package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.FlameParticles;
import se.llbit.chunky.model.CakeWithCandleModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

public class CakeWithCandle extends AbstractModelBlock {

  private final boolean lit;

  public CakeWithCandle(String name, Texture candle, Texture candleLit, boolean lit) {
    super(name, Texture.cakeTop);
    this.lit = lit;
    this.model = new CakeWithCandleModel(lit ? candleLit : candle);
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return "lit=" + isLit();
  }

  @Override
  public boolean isEntity() {
    return isLit();
  }

  @Override
  public boolean isBlockWithEntity() {
    return true;
  }

  @Override
  public Entity toEntity(Vector3 position) {
    return new FlameParticles(position, new Vector3[]{new Vector3(0, 15 / 16.0, 0)});
  }
}
