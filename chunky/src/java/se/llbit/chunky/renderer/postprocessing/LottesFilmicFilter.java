package se.llbit.chunky.renderer.postprocessing;

import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * Implementation of Timothy Lottes' filmic tonemapping curve.
 * <a href=https://gdcvault.com/play/1023512/Advanced-Graphics-Techniques-Tutorial-Day>link</a>
 */
public class LottesFilmicFilter extends SimplePixelPostProcessingFilter {

  private float gamma = Scene.DEFAULT_GAMMA;
  private float contrast = 1.6f;
  private float shoulder = 0.977f;
  private float hdrMax = 8f;
  private float midIn = 0.18f;
  private float midOut = 0.267f;

  @Override
  public void processPixel(double[] pixel) {
    // Code adapted from Tizian Zeltner's implementation of the tonemapping curve.
    // https://github.com/tizian/tonemapper/blob/fe100b9052e91d034927779d22a5afed9bedc1e3/src/operators/LottesFilmicOperator.cpp#L64

    float a = contrast;
    float d = shoulder;
    float b = ((float) -FastMath.pow(midIn, a) + (float) FastMath.pow(hdrMax, a) * midOut) /
        (((float) FastMath.pow(hdrMax, a * d) - (float) FastMath.pow(midIn, a * d)) *
            midOut);
    float c = ((float) FastMath.pow(hdrMax, a * d) * (float) FastMath.pow(midIn, a) -
        (float) FastMath.pow(hdrMax, a) * (float) FastMath.pow(midIn, a * d) * midOut) /
        (((float) FastMath.pow(hdrMax, a * d) - (float) FastMath.pow(midIn, a * d)) *
            midOut);
    for (int i = 0; i < 3; i++) {
      pixel[i] = FastMath.pow(pixel[i], a) / (FastMath.pow(pixel[i], a * d) * b + c);
      pixel[i] = FastMath.pow(pixel[i], 1f / gamma);
    }
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").floatValue(gamma);
    contrast = json.get("contrast").floatValue(contrast);
    shoulder = json.get("shoulder").floatValue(shoulder);
    hdrMax = json.get("hdrMax").floatValue(hdrMax);
    midIn = json.get("midIn").floatValue(midIn);
    midOut = json.get("midOut").floatValue(midOut);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("gamma", gamma);
    json.add("contrast", contrast);
    json.add("shoulder", shoulder);
    json.add("hdrMax", hdrMax);
    json.add("midIn", midIn);
    json.add("midOut", midOut);
  }

  @Override
  public void reset() {
    gamma = Scene.DEFAULT_GAMMA;
    contrast = 1.6f;
    shoulder = 0.977f;
    hdrMax = 8f;
    midIn = 0.18f;
    midOut = 0.267f;
  }

  @Override
  public String getName() {
    return "Lottes Filmic";
  }

  @Override
  public String getId() {
    return "LOTTES_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Timothy Lottes' filmic tonemapping curve.\n"
        + "https://gdcvault.com/play/1023512/Advanced-Graphics-Techniques-Tutorial-Day";
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster gamma = new DoubleAdjuster();
    DoubleAdjuster contrast = new DoubleAdjuster();
    DoubleAdjuster shoulder = new DoubleAdjuster();
    DoubleAdjuster hdrMax = new DoubleAdjuster();
    DoubleAdjuster midIn = new DoubleAdjuster();
    DoubleAdjuster midOut = new DoubleAdjuster();

    gamma.setName("Gamma");
    gamma.setTooltip("Gamma correction value");
    gamma.setRange(0.001, 5);
    gamma.clampMin();
    gamma.set(this.gamma);
    gamma.onValueChange(value -> {
      this.gamma = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    contrast.setName("Contrast");
    contrast.setRange(1, 2);
    contrast.set(this.contrast);
    contrast.onValueChange(value -> {
      this.contrast = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    shoulder.setName("Shoulder");
    shoulder.setRange(0.01, 2);
    shoulder.set(this.shoulder);
    shoulder.onValueChange(value -> {
      this.shoulder = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    hdrMax.setName("Maximum HDR value");
    hdrMax.setRange(1, 10);
    hdrMax.set(this.hdrMax);
    hdrMax.onValueChange(value -> {
      this.hdrMax = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    midIn.setName("Input mid-level");
    midIn.setRange(0, 1);
    midIn.set(this.midIn);
    midIn.onValueChange(value -> {
      this.midIn = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    midOut.setName("Output mid-level");
    midOut.setRange(0, 1);
    midOut.set(this.midOut);
    midOut.onValueChange(value -> {
      this.midOut = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(6, gamma, contrast, shoulder, hdrMax, midIn, midOut);
  }
}
