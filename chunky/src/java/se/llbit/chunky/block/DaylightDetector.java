package se.llbit.chunky.block;

import se.llbit.chunky.model.DaylightSensorModel;
import se.llbit.chunky.resources.Texture;

public class DaylightDetector extends AbstractModelBlock {
  private final boolean inverted;

  public DaylightDetector(boolean inverted) {
    super("daylight_detector",
        inverted ? Texture.daylightDetectorInvertedTop : Texture.daylightDetectorTop);
    this.inverted = inverted;
    this.model = new DaylightSensorModel(
        inverted ? Texture.daylightDetectorInvertedTop : Texture.daylightDetectorTop);
  }

  @Override
  public String description() {
    return "inverted=" + inverted;
  }
}
