package se.llbit.chunky.renderer.postprocessing;

import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.FastMath;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * Implementation of Mike Day's filmic tonemapping curve.
 * <a
 * href=https://d3cw3dd2w32x2b.cloudfront.net/wp-content/uploads/2012/09/an-efficient-and-user-friendly-tone-mapping-operator.pdf>link</a>
 */
public class DayFilmicFilter extends PostProcessingFilter {

  private float gamma = Scene.DEFAULT_GAMMA;
  private float w = 10f;
  private float b = 0.1f;
  private float t = 0.7f;
  private float s = 0.8f;
  private float c = 2f;
  private boolean autoExposure = true;

  private static double getMeanLuminance(int width, int height, double[] sampleBuffer) {
    double meanLuminance = 0;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int pixelIndex = (y * width + x) * 3;
        meanLuminance += luminance(sampleBuffer[pixelIndex],
            sampleBuffer[pixelIndex + 1], sampleBuffer[pixelIndex + 2]);
      }
    }
    return meanLuminance / (width * height);
  }

  private static double luminance(double r, double g, double b) {
    return r * 0.212671 + g * 0.715160 + b * 0.072169;
  }

  private double curve(double x, float k) {
    if (x < c) {
      return k * (1 - t) * (x - b) / (c - (1 - t) * b - t * x);
    } else {
      return (1 - k) * (x - c) / (s * x + (1 - s) * w - c) + k;
    }
  }

  @Override
  public void processFrame(int width, int height, double[] input) {
    // Code adapted from Tizian Zeltner's implementation of the tonemapping curve.
    // https://github.com/tizian/tonemapper/blob/fe100b9052e91d034927779d22a5afed9bedc1e3/src/operators/DayFilmicOperator.cpp#L71

    double lAvg = autoExposure ? getMeanLuminance(width, height, input) : 0.5;
    float k = (1f - t) * (c - b) / ((1f - s) * (w - c) + (1f - t) * (c - b));
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int pixelIndex = (y * width + x) * 3;
        for (int i = 0; i < 3; i++) {
          input[pixelIndex + i] /= lAvg;
          input[pixelIndex + i] = curve(input[pixelIndex + i], k);
          input[pixelIndex + i] = FastMath.pow(input[pixelIndex + i], 1f / gamma);
        }
      }
    }
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").floatValue(gamma);
    w = json.get("w").floatValue(w);
    b = json.get("b").floatValue(b);
    t = json.get("t").floatValue(t);
    s = json.get("s").floatValue(s);
    c = json.get("c").floatValue(c);
    autoExposure = json.get("autoExposure").boolValue(autoExposure);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("gamma", gamma);
    json.add("w", w);
    json.add("b", b);
    json.add("t", t);
    json.add("s", s);
    json.add("c", c);
    json.add("autoExposure", autoExposure);
  }

  @Override
  public void reset() {
    gamma = Scene.DEFAULT_GAMMA;
    w = 10f;
    b = 0.1f;
    t = 0.7f;
    s = 0.8f;
    c = 2f;
    autoExposure = true;
  }

  @Override
  public String getName() {
    return "Day Filmic";
  }

  @Override
  public String getId() {
    return "DAY_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Mike Day's filmic tonemapping curve.\n"
        + "https://d3cw3dd2w32x2b.cloudfront.net/wp-content/uploads/2012/09/an-efficient-and-user-friendly-tone-mapping-operator.pdf";
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster gamma = new DoubleAdjuster();
    DoubleAdjuster w = new DoubleAdjuster();
    DoubleAdjuster b = new DoubleAdjuster();
    DoubleAdjuster t = new DoubleAdjuster();
    DoubleAdjuster s = new DoubleAdjuster();
    DoubleAdjuster c = new DoubleAdjuster();
    ToggleSwitch autoExposure = new ToggleSwitch();

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

    w.setName("White point");
    w.setTooltip("Smallest value that is mapped to 1");
    w.setRange(0, 20);
    w.clampMin();
    w.set(this.w);
    w.onValueChange(value -> {
      this.w = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    b.setName("Black point");
    b.setTooltip("Largest value that is mapped to 0");
    b.setRange(0, 2);
    b.clampMin();
    b.set(this.b);
    b.onValueChange(value -> {
      this.b = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    t.setName("Toe strength");
    t.setRange(0, 1);
    t.set(this.t);
    t.onValueChange(value -> {
      this.t = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    s.setName("Shoulder strength");
    s.setRange(0, 1);
    s.set(this.s);
    s.onValueChange(value -> {
      this.s = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    c.setName("Crossover point");
    c.setRange(0, 10);
    c.set(this.c);
    c.onValueChange(value -> {
      this.c = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    autoExposure.setText("Auto exposure");
    autoExposure.setSelected(this.autoExposure);
    autoExposure.selectedProperty().addListener(((observable, oldValue, newValue) -> {
      this.autoExposure = newValue;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    }));

    return new VBox(6, gamma, w, b, t, s, c, autoExposure);
  }
}
