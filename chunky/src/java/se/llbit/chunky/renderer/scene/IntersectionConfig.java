package se.llbit.chunky.renderer.scene;

public class IntersectionConfig {
  public final boolean cloudIntersect;
  public final boolean fogIntersect;
  public final boolean waterPlaneIntersect;
  public final boolean sceneIntersect;

  public IntersectionConfig(boolean cloudIntersect, boolean fogIntersect, boolean waterPlaneIntersect, boolean sceneIntersect) {
    this.cloudIntersect = cloudIntersect;
    this.fogIntersect = fogIntersect;
    this.waterPlaneIntersect = waterPlaneIntersect;
    this.sceneIntersect = sceneIntersect;
  }

  public static IntersectionConfig defaultIntersect(Scene scene, boolean isRenderPreview) {
    return new IntersectionConfig(scene.sky().cloudsEnabled(), !isRenderPreview || scene.getPreviewParticleFog(), scene.isWaterPlaneEnabled(), true);
  }
}
