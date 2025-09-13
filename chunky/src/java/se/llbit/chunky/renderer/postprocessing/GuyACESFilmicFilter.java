package se.llbit.chunky.renderer.postprocessing;

import se.llbit.json.JsonObject;

/**
 * Implementation of Romain Guy's ACES filmic tonemapping curve
 * <a href="https://www.shadertoy.com/view/llXyWr">link</a>
 */
public class GuyACESFilmicFilter extends SimplePixelPostProcessingFilter {

  @Override
  public void processPixel(double[] pixel) {
    for (int i = 0; i < 3; i++) {
      pixel[i] = pixel[i] / (pixel[i] + 0.155) * 1.019;
    }
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

  @Override
  public String getName() {
    return "Guy ACES Filmic";
  }

  @Override
  public String getId() {
    return "GUY_ACES_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Romain Guy's ACES filmic tonemapping curve.\n"
        + "https://www.shadertoy.com/view/llXyWr";
  }
}
