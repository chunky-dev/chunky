package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;
import se.llbit.json.JsonObject;

public class PreviewFilter extends SimplePixelPostProcessingFilter {
  public static final PreviewFilter INSTANCE = new PreviewFilter();

  @Override
  public void processPixel(double[] pixel) {
    for(int i = 0; i < 3; ++i) {
      pixel[i] = FastMath.sqrt(pixel[i]);
    }
  }

  @Override
  public String getName() {
    return "Preview filter";
  }

  @Override
  public String getId() {
    return "PREVIEW";
  }

  @Override
  public String getDescription() {
    return "Tonemapping curve used by the PreviewRenderer.";
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
