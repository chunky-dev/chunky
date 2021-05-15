package se.llbit.chunky.renderer.postprecessing;

import org.apache.commons.math3.util.FastMath;

public class PreviewFilter extends IndependentPostProcessingFilter {
  public static final PreviewFilter Instance = new PreviewFilter();

  @Override
  protected void processPixel(double[] pixel) {
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
