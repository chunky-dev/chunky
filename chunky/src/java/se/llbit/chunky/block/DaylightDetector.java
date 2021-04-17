package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.DaylightSensorModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class DaylightDetector extends MinecraftBlockTranslucent implements ModelBlock {
  private final DaylightSensorModel model;
  private final boolean inverted;

  public DaylightDetector(boolean inverted) {
    super("daylight_detector",
        inverted ? Texture.daylightDetectorInvertedTop : Texture.daylightDetectorTop);
    this.inverted = inverted;
    this.model = new DaylightSensorModel(inverted ? Texture.daylightDetectorInvertedTop : Texture.daylightDetectorTop);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "inverted=" + inverted;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
