package se.llbit.chunky.renderer.postprecessing;

public class HableToneMappingFilter extends IndependentPostProcessingFilter {
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
    // http://filmicworlds.com/blog/filmic-tonemapping-operators/
    // This adjusts the exposure by a factor of 16 so that the resulting exposure approximately matches the other
    // post-processing methods. Without this, the image would be very dark.
    for(int i = 0; i < 3; ++i) {
      pixel[i] *= 16;
      pixel[i] = ((pixel[i] * (hA * pixel[i] + hC * hB) + hD * hE) / (pixel[i] * (hA * pixel[i] + hB) + hD * hF)) - hE / hF;
      pixel[i] *= whiteScale;
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
