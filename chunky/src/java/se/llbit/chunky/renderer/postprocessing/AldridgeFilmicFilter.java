package se.llbit.chunky.renderer.postprocessing;

import javafx.scene.layout.VBox;
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
 * Implementation of Graham Aldridge's variation of the Hejl Burgess-Dawson curve.
 * <a href=https://iwasbeingirony.blogspot.com/2010/04/approximating-film-with-tonemapping.html>
 *   link</a>
 */
public class AldridgeFilmicFilter extends SimplePixelPostProcessingFilter {

  private float cutoff = 0.025f;

  @Override
  public void processPixel(double[] pixel) {
    for (int i = 0; i < 3; ++i) {
      double tmp = 2.0 * cutoff;
      double x =
          pixel[i] + (tmp - pixel[i]) * QuickMath.clamp(tmp - pixel[i], 0d, 1d) * (0.25 / cutoff)
              - cutoff;
      pixel[i] = x * (0.5 + 6.2 * x) / (0.06 + x * (1.7 + 6.2 * x));
    }
  }

  @Override
  public void fromJson(JsonObject json) {
    cutoff = json.get("cutoff").floatValue(cutoff);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("cutoff", cutoff);
  }

  @Override
  public void reset() {
    cutoff = 0.025f;
  }

  @Override
  public String getName() {
    return "Aldridge Filmic";
  }

  @Override
  public String getId() {
    return "ALDRIDGE_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Graham Aldridge's variation of the Hejl Burgess-Dawson filmic tonemapping curve.\n"
        + "https://iwasbeingirony.blogspot.com/2010/04/approximating-film-with-tonemapping.html";
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster cutoff = new DoubleAdjuster();
    cutoff.setName("Cutoff");
    cutoff.setTooltip("Transition into compressed blacks");
    cutoff.setRange(0.001, 0.5);
    cutoff.clampMin();
    cutoff.set(this.cutoff);
    cutoff.onValueChange(value -> {
      this.cutoff = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(6, cutoff);
  }
}
