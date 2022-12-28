package se.llbit.chunky.renderer.postprocessing;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.QuickMath;

/**
 * Implementation of the Unreal Engine 4 Filmic Tone Mapper.
 *
 * @link https://docs.unrealengine.com/4.26/en-US/RenderingAndGraphics/PostProcessEffects/ColorGrading/
 * @link https://www.desmos.com/calculator/h8rbdpawxj?lang=de
 */
public class UE4ToneMappingFilter extends SimplePixelPostProcessingFilter {
  public enum Preset {
    /**
     * ACES curve parameters
     **/
    ACES,
    /**
     * UE4 legacy tone mapping style
     **/
    LEGACY_UE4
  }

  private float saturation;
  private float slope; // ga
  private float toe; // t0
  private float shoulder; // s0
  private float blackClip; // t1
  private float whiteClip; // s1

  private float ta;
  private float sa;

  public UE4ToneMappingFilter() {
    reset();
  }

  private void recalculateConstants() {
    ta = (1f - toe - 0.18f) / slope - 0.733f;
    sa = (shoulder - 0.18f) / slope - 0.733f;
  }

  public float getSaturation() {
    return saturation;
  }

  public void setSaturation(float saturation) {
    this.saturation = saturation;
  }

  public float getSlope() {
    return slope;
  }

  public void setSlope(float slope) {
    this.slope = slope;
    this.recalculateConstants();
  }

  public float getToe() {
    return toe;
  }

  public void setToe(float toe) {
    this.toe = toe;
    recalculateConstants();
  }

  public float getShoulder() {
    return shoulder;
  }

  public void setShoulder(float shoulder) {
    this.shoulder = shoulder;
    recalculateConstants();
  }

  public float getBlackClip() {
    return blackClip;
  }

  public void setBlackClip(float blackClip) {
    this.blackClip = blackClip;
  }

  public float getWhiteClip() {
    return whiteClip;
  }

  public void setWhiteClip(float whiteClip) {
    this.whiteClip = whiteClip;
  }

  public void applyPreset(Preset preset) {
    switch (preset) {
      case ACES:
        saturation = 1f;
        slope = 0.88f;
        toe = 0.55f;
        shoulder = 0.26f;
        blackClip = 0.0f;
        whiteClip = 0.04f;
        break;
      case LEGACY_UE4:
        saturation = 1f;
        slope = 0.98f;
        toe = 0.3f;
        shoulder = 0.22f;
        blackClip = 0.0f;
        whiteClip = 0.025f;
        break;
    }
    recalculateConstants();
  }

  public void reset() {
    applyPreset(Preset.ACES);
  }

  private float processComponent(float c) {
    float logc = (float) Math.log10(c);

    if (logc >= ta && logc <= sa) {
      return (float) (saturation * (slope * (logc + 0.733) + 0.18));
    }
    if (logc > sa) {
      return (float) (saturation * (1 + whiteClip - (2 * (1 + whiteClip - shoulder)) / (1 + Math.exp(((2 * slope) / (1 + whiteClip - shoulder)) * (logc - sa)))));
    }
    // if (logc < ta) {
    return (float) (saturation * ((2 * (1 + blackClip - toe)) / (1 + Math.exp(-((2 * slope) / (1 + blackClip - toe)) * (logc - ta))) - blackClip));
    // }
  }

  @Override
  public void processPixel(double[] pixel) {
    for (int i = 0; i < 3; ++i) {
      pixel[i] = QuickMath.max(QuickMath.min(processComponent((float) pixel[i] * 1.25f), 1), 0);
      pixel[i] = FastMath.pow(pixel[i], 1 / Scene.DEFAULT_GAMMA);
    }
  }

  @Override
  public String getName() {
    return "Unreal Engine 4 Filmic tone mapping";
  }

  @Override
  public String getId() {
    return "UE4_FILMIC";
  }
}
