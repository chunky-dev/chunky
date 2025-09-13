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
 * Implementation of Stephen Hill's ACES filmic tonemapping curve.
 * <a href="https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl">link</a>
 */
public class HillACESFilmicFilter extends SimplePixelPostProcessingFilter {
  private float gamma = Scene.DEFAULT_GAMMA;

  @Override
  public void processPixel(double[] pixel) {
    double a = 0.59719 * pixel[0] + 0.35458 * pixel[1] + 0.04823 * pixel[2];
    double b = 0.07600 * pixel[0] + 0.90834 * pixel[1] + 0.01566 * pixel[2];
    double c = 0.02840 * pixel[0] + 0.13383 * pixel[1] + 0.83777 * pixel[2];
    pixel[0] = a;
    pixel[1] = b;
    pixel[2] = c;

    for (int i = 0; i < 3; i++) {
      a = pixel[i] * (pixel[i] + 0.0245786) - 0.000090537;
      b = pixel[i] * (0.983729 * pixel[i] + 0.4329510) + 0.238081;
      pixel[i] = a / b;
    }

    a =  1.60475 * pixel[0] - 0.53108 * pixel[1] - 0.07367 * pixel[2];
    b = -0.10208 * pixel[0] + 1.10813 * pixel[1] - 0.00605 * pixel[2];
    c = -0.00327 * pixel[0] - 0.07276 * pixel[1] + 1.07602 * pixel[2];
    pixel[0] = a;
    pixel[1] = b;
    pixel[2] = c;

    for (int i = 0; i < 3; i++) {
      pixel[i] = FastMath.pow(pixel[i], 1f / gamma);
    }
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").floatValue(gamma);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("gamma", gamma);
  }

  @Override
  public void reset() {
    gamma = Scene.DEFAULT_GAMMA;
  }

  @Override
  public String getName() {
    return "Hill ACES Filmic";
  }

  @Override
  public String getId() {
    return "HILL_ACES_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Stephen Hill's ACES filmic tonemapping curve.\n"
        + "https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl";
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster gamma = new DoubleAdjuster();
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

    return new VBox(6, gamma);
  }
}
