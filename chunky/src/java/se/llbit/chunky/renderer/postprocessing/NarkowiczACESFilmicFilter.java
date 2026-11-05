package se.llbit.chunky.renderer.postprocessing;

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
 * Implementation of Krzysztof Narkowicz's ACES filmic tonemapping curve
 * <a href="https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/">link</a>
 */
public class NarkowiczACESFilmicFilter extends SimplePixelPostProcessingFilter {
  private static final float aces_a = 2.51f;
  private static final float aces_b = 0.03f;
  private static final float aces_c = 2.43f;
  private static final float aces_d = 0.59f;
  private static final float aces_e = 0.14f;
  private double gamma = Scene.DEFAULT_GAMMA;
  
  @Override
  public void processPixel(double[] pixel) {
    for(int i = 0; i < 3; ++i) {
      pixel[i] = QuickMath.max(QuickMath.min((pixel[i] * (aces_a * pixel[i] + aces_b)) / (pixel[i] * (aces_c * pixel[i] + aces_d) + aces_e), 1), 0);
      pixel[i] = FastMath.pow(pixel[i], 1 / gamma);
    }
  }

  @Override
  public String getName() {
    return "Narkowicz ACES Filmic";
  }

  @Override
  public String getId() {
    return "TONEMAP2";
  }

  @Override
  public String getDescription() {
    return "Krzysztof Narkowicz's ACES filmic tonemapping curve.\n"
        + "https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/";
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").doubleValue(gamma);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("gamma", gamma);
  }

  @Override
  public void reset() {
    gamma = Scene.DEFAULT_GAMMA;
  }

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
