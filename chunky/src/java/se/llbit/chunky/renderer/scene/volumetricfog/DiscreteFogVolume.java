package se.llbit.chunky.renderer.scene.volumetricfog;

import se.llbit.chunky.renderer.HasPrimitives;
import se.llbit.math.primitive.Primitive;

public abstract class DiscreteFogVolume extends FogVolume implements Primitive, HasPrimitives {
  @Override
  public boolean isDiscrete() {
    return true;
  }
}
