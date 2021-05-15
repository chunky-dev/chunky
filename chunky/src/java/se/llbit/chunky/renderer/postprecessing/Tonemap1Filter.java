package se.llbit.chunky.renderer.postprecessing;

import se.llbit.math.QuickMath;

public class Tonemap1Filter extends IndependentPostProcessingFilter {
  @Override
  protected void processPixel(double[] pixel) {
    // http://filmicworlds.com/blog/filmic-tonemapping-operators/
    for(int i = 0; i < 3; ++i) {
      pixel[i] = QuickMath.max(0, pixel[i] - 0.004);
      pixel[i] = (pixel[i] * (6.2 * pixel[i] + .5)) / (pixel[i] * (6.2 * pixel[i] + 1.7) + 0.06);
    }
  }

  @Override
  public String getName() {
    return "Tonemap operator 1";
  }

  @Override
  public String getId() {
    return "TONEMAP1";
  }
}
