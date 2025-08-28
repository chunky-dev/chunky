package se.llbit.chunky.renderer.scene.volumetricfog;

public abstract class ContinuousFogVolume extends FogVolume {
  @Override
  public boolean isDiscrete() {
    return false;
  }
}
