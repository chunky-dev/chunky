package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;

public class GammaCorrectionFilter extends SimplePixelPostProcessingFilter {
  public static final String ID = "GAMMA";

  @Override
  public void processPixel(double[] pixel) {
    for(int i = 0; i < 3; ++i) {
      pixel[i] = FastMath.pow(pixel[i], 1 / Scene.DEFAULT_GAMMA);
    }
  }

  @Override
  public String getName() {
    return "Gamma correction";
  }

  @Override
  public String getId() {
    return ID;
  }
}
