package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;

/**
 * Implementation of Hable tone mapping
 *
 * @link http://filmicworlds.com/blog/filmic-tonemapping-operators/
 * @link https://www.gdcvault.com/play/1012351/Uncharted-2-HDR
 */
public class HableToneMappingFilter extends SimplePixelPostProcessingFilter {
  private static final float hA = 0.15f;
  private static final float hB = 0.50f;
  private static final float hC = 0.10f;
  private static final float hD = 0.20f;
  private static final float hE = 0.02f;
  private static final float hF = 0.30f;
  private static final float hW = 11.2f;
  private static final float whiteScale = 1.0f / (((hW * (hA * hW + hC * hB) + hD * hE) / (hW * (hA * hW + hB) + hD * hF)) - hE / hF);
  
  @Override
  public void processPixel(double[] pixel) {
    for (int i = 0; i < 3; ++i) {
      pixel[i] *= 2; // exposure bias
      pixel[i] = ((pixel[i] * (hA * pixel[i] + hC * hB) + hD * hE) / (pixel[i] * (hA * pixel[i] + hB) + hD * hF)) - hE / hF;
      pixel[i] *= whiteScale;
      pixel[i] = FastMath.pow(pixel[i], 1 / Scene.DEFAULT_GAMMA);
    }
  }

  @Override
  public String getName() {
    return "Hable tone mapping";
  }

  @Override
  public String getId() {
    return "TONEMAP3";
  }
}
