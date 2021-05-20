package se.llbit.chunky.renderer.postprocessing;

import se.llbit.math.QuickMath;

/**
 * Implementation of the tone mapping operator from Jim Hejl and Richard Burgess-Dawson
 * @link http://filmicworlds.com/blog/filmic-tonemapping-operators/
 */
public class Tonemap1Filter extends SimplePixelPostProcessingFilter {
  @Override
  public void processPixel(double[] pixel) {
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
