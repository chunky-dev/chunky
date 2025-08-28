package se.llbit.chunky.renderer.postprocessing;

import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;

/**
 * Implementation of Jim Hejl and Richard Burgess-Dawson's filmic tonemapping curve
 * <a href="http://filmicworlds.com/blog/filmic-tonemapping-operators/">link</a>
 */
public class HejlBurgessDawsonFilmicFilter extends SimplePixelPostProcessingFilter {
  @Override
  public void processPixel(double[] pixel) {
    for(int i = 0; i < 3; ++i) {
      pixel[i] = QuickMath.max(0, pixel[i] - 0.004);
      pixel[i] = (pixel[i] * (6.2 * pixel[i] + .5)) / (pixel[i] * (6.2 * pixel[i] + 1.7) + 0.06);
    }
  }

  @Override
  public String getName() {
    return "Hejl Burgess-Dawson Filmic";
  }

  @Override
  public String getId() {
    return "TONEMAP1";
  }

  @Override
  public String getDescription() {
    return "Jim Hejl and Richard Burgess-Dawson's filmic tonemapping curve.\n"
        + "http://filmicworlds.com/blog/filmic-tonemapping-operators/";
  }

  @Override
  public void fromJson(JsonObject json) {
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
  }

  @Override
  public void reset() {
  }
}
