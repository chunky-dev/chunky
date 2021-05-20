package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.QuickMath;

/**
 * Implementation of ACES filmic tone mapping
 * @link https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/
 */
public class ACESFilmicFilter extends SimplePixelPostProcessingFilter {
  private static final float aces_a = 2.51f;
  private static final float aces_b = 0.03f;
  private static final float aces_c = 2.43f;
  private static final float aces_d = 0.59f;
  private static final float aces_e = 0.14f;
  
  @Override
  public void processPixel(double[] pixel) {
    for(int i = 0; i < 3; ++i) {
      pixel[i] = QuickMath.max(QuickMath.min((pixel[i] * (aces_a * pixel[i] + aces_b)) / (pixel[i] * (aces_c * pixel[i] + aces_d) + aces_e), 1), 0);
      pixel[i] = FastMath.pow(pixel[i], 1 / Scene.DEFAULT_GAMMA);
    }
  }

  @Override
  public String getName() {
    return "ACES filmic tone mapping";
  }

  @Override
  public String getId() {
    return "TONEMAP2";
  }
}
