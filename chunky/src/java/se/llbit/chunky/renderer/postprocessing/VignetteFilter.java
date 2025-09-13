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
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

public class VignetteFilter extends PostProcessingFilter {
  private double vignetteFalloff = 1;
  private double vignetteStrength = 1;
  private double vignetteDesaturation = 0;
  private final Vector2 center = new Vector2(0.5, 0.5);
  private double aspectRatio = 1;

  @Override
  public void processFrame(int width, int height, double[] input) {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixelIndex = (y * width + x) * 3;
        Vector2 uv = new Vector2((double) x / width, (double) y / height);
        Vector2 fromCenter = new Vector2((center.x - uv.x) * aspectRatio, center.y - uv.y);
        double fromCenterLength = FastMath.sqrt(fromCenter.lengthSquared());
        double vignette = FastMath.pow(fromCenterLength, vignetteFalloff) * vignetteStrength;
        double lengthColor = new Vector3(input[pixelIndex], input[pixelIndex + 1], input[pixelIndex + 2]).length();
        for (int i = 0; i < 3; i++) {
          input[pixelIndex + i] *= QuickMath.clamp(1 - vignette, 0, 1);
          input[pixelIndex + i] = input[pixelIndex + i] + vignette * vignetteDesaturation * (lengthColor - input[pixelIndex + i]);
        }
      }
    }
  }

  @Override
  public String getName() {
    return "Vignette";
  }

  @Override
  public String getDescription() {
    return "";
  }

  @Override
  public String getId() {
    return "VIGNETTE";
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("vignetteFalloff", vignetteFalloff);
    json.add("vignetteStrength", vignetteStrength);
    json.add("vignetteDesaturation", vignetteDesaturation);
    json.add("center", center.toJson());
    json.add("aspectRatio", aspectRatio);
  }

  @Override
  public void fromJson(JsonObject json) {
    vignetteFalloff = json.get("vignetteFalloff").doubleValue(1);
    vignetteStrength = json.get("vignetteStrength").doubleValue(1);
    vignetteDesaturation = json.get("vignetteDesaturation").doubleValue(0);
    center.fromJson(json.get("center").asObject());
    aspectRatio = json.get("aspectRatio").doubleValue(1);
  }

  @Override
  public void reset() {
    vignetteFalloff = 1;
    vignetteStrength = 1;
    vignetteDesaturation = 0;
    center.set(0.5, 0.5);
    aspectRatio = 1;
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster vignetteFalloffAdjuster = new DoubleAdjuster();
    DoubleAdjuster vignetteStrengthAdjuster = new DoubleAdjuster();
    DoubleAdjuster vignetteDesaturationAdjuster = new DoubleAdjuster();
    DoubleAdjuster centerXAdjuster = new DoubleAdjuster();
    DoubleAdjuster centerYAdjuster = new DoubleAdjuster();
    DoubleAdjuster aspectRatioAdjuster = new DoubleAdjuster();

    vignetteFalloffAdjuster.setName("Vignette falloff");
    vignetteFalloffAdjuster.setRange(0, 5);
    vignetteFalloffAdjuster.clampMin();
    vignetteFalloffAdjuster.set(vignetteFalloff);
    vignetteFalloffAdjuster.onValueChange(value -> {
      vignetteFalloff = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    vignetteStrengthAdjuster.setName("Vignette strength");
    vignetteStrengthAdjuster.setRange(0, 5);
    vignetteStrengthAdjuster.clampMin();
    vignetteStrengthAdjuster.set(vignetteStrength);
    vignetteStrengthAdjuster.onValueChange(value -> {
      vignetteStrength = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    vignetteDesaturationAdjuster.setName("Vignette desaturation");
    vignetteDesaturationAdjuster.setRange(0, 1);
    vignetteDesaturationAdjuster.clampBoth();
    vignetteDesaturationAdjuster.set(vignetteDesaturation);
    vignetteDesaturationAdjuster.onValueChange(value -> {
      vignetteDesaturation = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    centerXAdjuster.setName("Center X");
    centerXAdjuster.setRange(0, 1);
    centerXAdjuster.clampBoth();
    centerXAdjuster.set(center.x);
    centerXAdjuster.onValueChange(value -> {
      center.x = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    centerYAdjuster.setName("Center Y");
    centerYAdjuster.setRange(0, 1);
    centerYAdjuster.clampBoth();
    centerYAdjuster.set(center.y);
    centerYAdjuster.onValueChange(value -> {
      center.y = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    aspectRatioAdjuster.setName("Aspect ratio");
    aspectRatioAdjuster.setRange(0.001, 5);
    aspectRatioAdjuster.clampMin();
    aspectRatioAdjuster.set(aspectRatio);
    aspectRatioAdjuster.onValueChange(value -> {
      aspectRatio = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(6, vignetteFalloffAdjuster, vignetteStrengthAdjuster, vignetteDesaturationAdjuster, centerXAdjuster, centerYAdjuster, aspectRatioAdjuster);
  }
}
