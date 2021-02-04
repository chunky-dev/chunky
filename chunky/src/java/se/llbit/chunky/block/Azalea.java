package se.llbit.chunky.block;

import se.llbit.chunky.model.AzaleaModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Azalea extends MinecraftBlockTranslucent {

  private final Texture top;
  private final Texture side;

  public Azalea(String name, Texture top, Texture side) {
    super(name, top);
    this.top = top;
    this.side = side;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return AzaleaModel.intersect(ray, top, side);
  }
}
