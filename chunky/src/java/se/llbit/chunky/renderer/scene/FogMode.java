package se.llbit.chunky.renderer.scene;

public enum FogMode {
  NONE, UNIFORM, LAYERED;

  public static FogMode get(String name) {
    try {
      return valueOf(name);
    } catch (IllegalArgumentException e) {
      return NONE;
    }
  }
}
