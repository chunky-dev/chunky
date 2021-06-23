package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;

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
    return null;
  }

  @Override
  public String getId() {
    return null;
  }
}
