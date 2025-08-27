package se.llbit.chunky.renderer.postprocessing;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * Implementation of the Unreal Engine 4 Filmic Tone Mapper.
 *
 * @link
 * https://docs.unrealengine.com/4.26/en-US/RenderingAndGraphics/PostProcessEffects/ColorGrading/
 * @link https://www.desmos.com/calculator/h8rbdpawxj?lang=de
 */
public class UE4FilmicFilter extends SimplePixelPostProcessingFilter {

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

  private float gamma;

  public UE4FilmicFilter() {
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

  public float getGamma() {
    return gamma;
  }

  public void setGamma(float gamma) {
    this.gamma = gamma;
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
        gamma = Scene.DEFAULT_GAMMA;
        break;
      case LEGACY_UE4:
        saturation = 1f;
        slope = 0.98f;
        toe = 0.3f;
        shoulder = 0.22f;
        blackClip = 0.0f;
        whiteClip = 0.025f;
        gamma = Scene.DEFAULT_GAMMA;
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
      return (float) (saturation * (1 + whiteClip - (2 * (1 + whiteClip - shoulder)) / (1
          + Math.exp(((2 * slope) / (1 + whiteClip - shoulder)) * (logc - sa)))));
    }
    // if (logc < ta) {
    return (float) (saturation * ((2 * (1 + blackClip - toe)) / (1 + Math.exp(
        -((2 * slope) / (1 + blackClip - toe)) * (logc - ta))) - blackClip));
    // }
  }

  @Override
  public void processPixel(double[] pixel) {
    for (int i = 0; i < 3; ++i) {
      pixel[i] = QuickMath.max(QuickMath.min(processComponent((float) pixel[i] * 1.25f), 1), 0);
      pixel[i] = FastMath.pow(pixel[i], 1 / gamma);
    }
  }

  @Override
  public String getName() {
    return "Unreal Engine 4 Filmic";
  }

  @Override
  public String getId() {
    return "UE4_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Unreal Engine 4 Filmic Tone Mapper with two presets.\n"
        + "https://docs.unrealengine.com/4.26/en-US/RenderingAndGraphics/PostProcessEffects/ColorGrading/";
  }

  @Override
  public void fromJson(JsonObject json) {
    reset();
    saturation = json.get("saturation").floatValue(saturation);
    slope = json.get("slope").floatValue(slope);
    toe = json.get("toe").floatValue(toe);
    shoulder = json.get("shoulder").floatValue(shoulder);
    blackClip = json.get("blackClip").floatValue(blackClip);
    whiteClip = json.get("whiteClip").floatValue(whiteClip);
    gamma = json.get("gamma").floatValue(gamma);
    recalculateConstants();
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("saturation", saturation);
    json.add("slope", slope);
    json.add("toe", toe);
    json.add("shoulder", shoulder);
    json.add("blackClip", blackClip);
    json.add("whiteClip", whiteClip);
    json.add("gamma", gamma);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    MenuButton presetChooser = new MenuButton();
    DoubleAdjuster gamma = new DoubleAdjuster();
    DoubleAdjuster saturation = new DoubleAdjuster();
    DoubleAdjuster slope = new DoubleAdjuster();
    DoubleAdjuster toe = new DoubleAdjuster();
    DoubleAdjuster shoulder = new DoubleAdjuster();
    DoubleAdjuster blackClip = new DoubleAdjuster();
    DoubleAdjuster whiteClip = new DoubleAdjuster();

    presetChooser.setText("Load preset");
    for (UE4FilmicFilter.Preset preset : UE4FilmicFilter.Preset.values()) {
      MenuItem menuItem = new MenuItem(preset.toString());
      menuItem.setOnAction(e -> {
        applyPreset(preset);
        gamma.set(getGamma());
        saturation.set(getSaturation());
        slope.set(getSlope());
        toe.set(getToe());
        shoulder.set(getShoulder());
        blackClip.set(getBlackClip());
        whiteClip.set(getWhiteClip());
        if (scene.getMode() == RenderMode.PREVIEW) {
          // Don't interrupt the render if we are currently rendering.
          scene.refresh();
        }
        scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
        controller.getCanvas().forceRepaint();
      });
      presetChooser.getItems().add(menuItem);
    }

    gamma.setName("Gamma correction value");
    gamma.setRange(0.001, 5);
    gamma.clampMin();
    gamma.set(getGamma());
    gamma.onValueChange(value -> {
      setGamma(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    saturation.setName("Saturation");
    saturation.setRange(0, 2);
    saturation.set(getSaturation());
    saturation.onValueChange(value -> {
      setSaturation(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    slope.setName("Slope");
    slope.setRange(0, 1);
    slope.set(getSlope());
    slope.onValueChange(value -> {
      setSlope(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    toe.setName("Toe");
    toe.setRange(0, 1);
    toe.set(getToe());
    toe.onValueChange(value -> {
      setToe(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    shoulder.setName("Shoulder");
    shoulder.setRange(0, 1);
    shoulder.set(getShoulder());
    shoulder.onValueChange(value -> {
      setShoulder(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    blackClip.setName("Black clip");
    blackClip.setRange(0, 1);
    blackClip.set(getBlackClip());
    blackClip.onValueChange(value -> {
      setBlackClip(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    whiteClip.setName("White clip");
    whiteClip.setRange(0, 1);
    whiteClip.set(getWhiteClip());
    whiteClip.onValueChange(value -> {
      setWhiteClip(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(6, presetChooser, gamma, saturation, slope, toe, shoulder, blackClip,
        whiteClip);
  }
}
