package se.llbit.chunky.block;

import se.llbit.chunky.model.DaylightSensorModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class DaylightDetector extends MinecraftBlock {
  private final boolean inverted;

  public DaylightDetector(boolean inverted) {
    super("daylight_detector",
        inverted ? Texture.daylightDetectorTop : Texture.daylightDetectorInvertedTop);
    this.inverted = inverted;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return DaylightSensorModel.intersect(ray, texture);
  }

  @Override public String description() {
    return "inverted=" + inverted;
  }
}
