package se.llbit.chunky.renderer.scene.volumetricfog;

import se.llbit.chunky.renderer.HasPrimitives;
import se.llbit.math.primitive.Primitive;

/**
 * A {@link se.llbit.chunky.renderer.scene.volumetricfog.FogVolume} that has finite bounds.
 */
public abstract class DiscreteFogVolume extends FogVolume implements Primitive, HasPrimitives {
  @Override
  public boolean isDiscrete() {
    return true;
  }
}
