package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.FlameParticles;
import se.llbit.chunky.model.CandleModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

public class Candle extends MinecraftBlockTranslucent {

  public static final Material flameMaterial = new TextureMaterial(Texture.flameParticle);

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
    return CandleModel.intersect(ray, candle, candles);
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
    switch (candles) {
      case 1:
        return new FlameParticles(position, new Vector3[]{
            new Vector3(0, 7 / 16.0, 0)
        });
      case 2:
        return new FlameParticles(position, new Vector3[]{
            new Vector3(-2 / 16.0, 6 / 16.0, 0),
            new Vector3(2 / 16.0, 7 / 16.0, -1 / 16.0)
        });
      case 3:
        return new FlameParticles(position, new Vector3[]{
            new Vector3(0, 4 / 16.0, 2 / 16.0),
            new Vector3(-2 / 16.0, 6 / 16.0, 0),
            new Vector3(1 / 16.0, 7 / 16.0, -1 / 16.0)
        });
      case 4:
        return new FlameParticles(position, new Vector3[]{
            new Vector3(-1 / 16.0, 4 / 16.0, 1 / 16.0),
            new Vector3(2 / 16.0, 6 / 16.0, 1 / 16.0),
            new Vector3(-2 / 16.0, 6 / 16.0, -2 / 16.0),
            new Vector3(1 / 16.0, 7 / 16.0, -2 / 16.0)
        });
      default:
        return new FlameParticles(position, new Vector3[0]);
    }
  }

  @Override
  public String description() {
    return "candles=" + candles + ", lit=" + isLit();
  }
}
