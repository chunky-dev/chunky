package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.FlameParticles;
import se.llbit.chunky.model.CakeWithCandleModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

import java.util.Random;

public class CakeWithCandle extends AbstractModelBlock {

  private final boolean lit;
  private final FlameParticles entity;

  public CakeWithCandle(String name, Texture candle, Texture candleLit, boolean lit) {
    super(name, Texture.cakeTop);
    this.lit = lit;
    this.model = new CakeWithCandleModel(lit ? candleLit : candle);
    this.entity = new FlameParticles(this, new Vector3[] {
            new Vector3(0, 15 / 16.0, 0)
    });
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
    return new FlameParticles(position, entity);
  }

  @Override
  public int faceCount() {
    return entity.faceCount();
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    entity.sample(face, loc, rand);
  }

  @Override
  public double surfaceArea(int face) {
    return entity.surfaceArea(face);
  }
}
