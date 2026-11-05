package se.llbit.chunky.renderer.scene.volumetricfog;

/**
 * A {@link se.llbit.chunky.renderer.scene.volumetricfog.FogVolume} that has no bounds.
 */
public abstract class ContinuousFogVolume extends FogVolume {
  @Override
  public boolean isDiscrete() {
    return false;
  }
}
