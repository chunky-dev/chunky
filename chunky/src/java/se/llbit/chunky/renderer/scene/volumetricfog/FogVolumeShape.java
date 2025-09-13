package se.llbit.chunky.renderer.scene.volumetricfog;

public enum FogVolumeShape {
  EXPONENTIAL("Exponential"), LAYER("Layer"), SPHERE("Sphere"), CUBOID("Cuboid");

  private final String displayName;

  FogVolumeShape(String displayName) {
    this.displayName = displayName;
  }

  public String toString() {
    return this.displayName;
  }
}
