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

public class GammaCorrectionFilter extends SimplePixelPostProcessingFilter {
  private double gamma = Scene.DEFAULT_GAMMA;

  @Override
  public void processPixel(double[] pixel) {
    for(int i = 0; i < 3; ++i) {
      pixel[i] = FastMath.pow(pixel[i], 1 / gamma);
    }
  }

  @Override
  public String getName() {
    return "Gamma correction";
  }

  @Override
  public String getId() {
    return "GAMMA";
  }

  @Override
  public String getDescription() {
    return "Performs gamma correction only.";
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").doubleValue(Scene.DEFAULT_GAMMA);
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
      this.gamma = value;
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
