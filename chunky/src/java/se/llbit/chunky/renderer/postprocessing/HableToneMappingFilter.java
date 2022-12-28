package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;

/**
 * Implementation of Hable (i.e. Uncharted 2) tone mapping
 *
 * @link http://filmicworlds.com/blog/filmic-tonemapping-operators/
 * @link https://www.gdcvault.com/play/1012351/Uncharted-2-HDR
 */
public class HableToneMappingFilter extends SimplePixelPostProcessingFilter {
  public enum Preset {
    /**
     * Parameters from <a href="http://filmicworlds.com/blog/filmic-tonemapping-operators/">John Hable's blog post</a>
     */
    FILMIC_WORLDS,

    /**
     * Parameters from <a href="https://www.gdcvault.com/play/1012351/Uncharted-2-HDR">John Hable's GDC talk</a>
     */
    GDC
  }

  private float hA;
  private float hB;
  private float hC;
  private float hD;
  private float hE;
  private float hF;
  private float hW;
  private float whiteScale;

  public HableToneMappingFilter() {
    reset();
  }

  private void recalculateWhiteScale() {
    whiteScale = 1.0f / (((hW * (hA * hW + hC * hB) + hD * hE) / (hW * (hA * hW + hB) + hD * hF)) - hE / hF);
  }

  public float getShoulderStrength() {
    return hA;
  }

  public void setShoulderStrength(float hA) {
    this.hA = hA;
    recalculateWhiteScale();
  }

  public float getLinearStrength() {
    return hB;
  }

  public void setLinearStrength(float hB) {
    this.hB = hB;
    recalculateWhiteScale();
  }

  public float getLinearAngle() {
    return hC;
  }

  public void setLinearAngle(float hC) {
    this.hC = hC;
    recalculateWhiteScale();
  }

  public float getToeStrength() {
    return hD;
  }

  public void setToeStrength(float hD) {
    this.hD = hD;
    recalculateWhiteScale();
  }

  public float getToeNumerator() {
    return hE;
  }

  public void setToeNumerator(float hE) {
    this.hE = hE;
    recalculateWhiteScale();
  }

  public float getToeDenominator() {
    return hF;
  }

  public void setToeDenominator(float hF) {
    this.hF = hF;
    recalculateWhiteScale();
  }

  public float getLinearWhitePointValue() {
    return hW;
  }

  public void setLinearWhitePointValue(float hW) {
    this.hW = hW;
    recalculateWhiteScale();
  }

  public void reset() {
    applyPreset(Preset.FILMIC_WORLDS);
  }

  public void applyPreset(Preset preset) {
    switch (preset) {
      case FILMIC_WORLDS:
        hA = 0.15f;
        hB = 0.50f;
        hC = 0.10f;
        hD = 0.20f;
        hE = 0.02f;
        hF = 0.30f;
        hW = 11.2f;
        break;
      case GDC:
        hA = 0.22f;
        hB = 0.30f;
        hC = 0.10f;
        hD = 0.20f;
        hE = 0.01f;
        hF = 0.30f;
        hW = 11.2f;
        break;
    }
    recalculateWhiteScale();
  }

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
