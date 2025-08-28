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
 * Implementation of Hajime Uchimura's filmic tonemapping curve.
 * <a href=https://www.slideshare.net/nikuque/hdr-theory-and-practicce-jp>link</a>
 */
public class UchimuraFilmicFilter extends SimplePixelPostProcessingFilter {

  private float gamma = Scene.DEFAULT_GAMMA;
  private float P = 1f;
  private float a = 1f;
  private float m = 0.22f;
  private float l = 0.4f;
  private float c = 1.33f;
  private float b = 0f;

  private static double smoothstep(double edge0, double edge1, double x) {
    float t = (float) QuickMath.clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0f - 2.0f * t);
  }

  @Override
  public void processPixel(double[] pixel) {
    // Code adapted from Tizian Zeltner's implementation of the tonemapping curve.
    // https://github.com/tizian/tonemapper/blob/fe100b9052e91d034927779d22a5afed9bedc1e3/src/operators/UchimuraFilmicOperator.cpp#L78

    float l0 = ((P - m) * l) / a;
    float S0 = m + l0;
    float S1 = m + a * l0;
    float C2 = (a * P) / (P - S1);
    float CP = -C2 / P;

    for (int i = 0; i < 3; i++) {
      double w0 = 1.0 - smoothstep(0.0, m, pixel[i]);
      double w2 = smoothstep(m + l0, m + l0, pixel[i]);
      double w1 = 1.0 - w0 - w2;

      double T = m * FastMath.pow(pixel[i] / m, c) + b;
      double L = m + a * (pixel[i] - m);
      double S = P - (P - S1) * FastMath.exp(CP * (pixel[i] - S0));

      pixel[i] = T * w0 + L * w1 + S * w2;
      pixel[i] = FastMath.pow(pixel[i], 1d / gamma);
    }
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").floatValue(gamma);
    P = json.get("P").floatValue(P);
    a = json.get("a").floatValue(a);
    m = json.get("m").floatValue(m);
    l = json.get("l").floatValue(l);
    c = json.get("c").floatValue(c);
    b = json.get("b").floatValue(b);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("gamma", gamma);
    json.add("P", P);
    json.add("a", a);
    json.add("m", m);
    json.add("l", l);
    json.add("c", c);
    json.add("b", b);
  }

  @Override
  public void reset() {
    gamma = Scene.DEFAULT_GAMMA;
    P = 1f;
    a = 1f;
    m = 0.22f;
    l = 0.4f;
    c = 1.33f;
    b = 0f;
  }

  @Override
  public String getName() {
    return "Uchimura Filmic";
  }

  @Override
  public String getId() {
    return "UCHIMURA_FILMIC";
  }

  @Override
  public String getDescription() {
    return "Hajime Uchimura's filmic tonemapping curve.\n"
        + "https://www.slideshare.net/nikuque/hdr-theory-and-practicce-jp";
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster gamma = new DoubleAdjuster();
    DoubleAdjuster P = new DoubleAdjuster();
    DoubleAdjuster a = new DoubleAdjuster();
    DoubleAdjuster m = new DoubleAdjuster();
    DoubleAdjuster l = new DoubleAdjuster();
    DoubleAdjuster c = new DoubleAdjuster();
    DoubleAdjuster b = new DoubleAdjuster();

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

    P.setName("Maximum brightness");
    P.setRange(1, 100);
    P.set(this.P);
    P.onValueChange(value -> {
      this.P = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    a.setName("Contrast");
    a.setRange(0, 5);
    a.clampMin();
    a.set(this.a);
    a.onValueChange(value -> {
      this.a = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    m.setName("Linear section start");
    m.setRange(0, 1);
    m.set(this.m);
    m.onValueChange(value -> {
      this.m = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    l.setName("Linear section length");
    l.setRange(0.01, 0.99);
    l.clampBoth();
    l.set(this.l);
    l.onValueChange(value -> {
      this.l = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    c.setName("Black tightness shape");
    c.setRange(1, 3);
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

    b.setName("Black tightness offset");
    b.setRange(0, 1);
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

    return new VBox(6, gamma, P, a, m, l, c, b);
  }
}
