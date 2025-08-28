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
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * Implementation of John Hable's filmic tonemapping curve (i.e. Uncharted 2)
 * <a href="http://filmicworlds.com/blog/filmic-tonemapping-operators/">blog post</a>
 * <a href="https://www.gdcvault.com/play/1012351/Uncharted-2-HDR">GDC talk</a>
 */
public class HableFilmicFilter extends SimplePixelPostProcessingFilter {
  public enum Preset {
    /**
     * Parameters from <a href="http://filmicworlds.com/blog/filmic-tonemapping-operators/">John Hable's blog post</a>
     */
    FILMIC_WORLDS,

    /**
     * Parameters from <a href="https://www.gdcvault.com/play/1012351/Uncharted-2-HDR">John Hable's GDC talk</a>
     */
    GDC
  }

  private float hA;
  private float hB;
  private float hC;
  private float hD;
  private float hE;
  private float hF;
  private float hW;
  private float whiteScale;
  private float gamma;

  public HableFilmicFilter() {
    reset();
  }

  private void recalculateWhiteScale() {
    whiteScale = 1.0f / (((hW * (hA * hW + hC * hB) + hD * hE) / (hW * (hA * hW + hB) + hD * hF)) - hE / hF);
  }

  public float getShoulderStrength() {
    return hA;
  }

  public void setShoulderStrength(float hA) {
    this.hA = hA;
    recalculateWhiteScale();
  }

  public float getLinearStrength() {
    return hB;
  }

  public void setLinearStrength(float hB) {
    this.hB = hB;
    recalculateWhiteScale();
  }

  public float getLinearAngle() {
    return hC;
  }

  public void setLinearAngle(float hC) {
    this.hC = hC;
    recalculateWhiteScale();
  }

  public float getToeStrength() {
    return hD;
  }

  public void setToeStrength(float hD) {
    this.hD = hD;
    recalculateWhiteScale();
  }

  public float getToeNumerator() {
    return hE;
  }

  public void setToeNumerator(float hE) {
    this.hE = hE;
    recalculateWhiteScale();
  }

  public float getToeDenominator() {
    return hF;
  }

  public void setToeDenominator(float hF) {
    this.hF = hF;
    recalculateWhiteScale();
  }

  public float getLinearWhitePointValue() {
    return hW;
  }

  public void setLinearWhitePointValue(float hW) {
    this.hW = hW;
    recalculateWhiteScale();
  }

  public float getGamma() {
    return gamma;
  }

  public void setGamma(float gamma) {
    this.gamma = gamma;
  }

  public void reset() {
    applyPreset(Preset.FILMIC_WORLDS);
  }

  public void applyPreset(Preset preset) {
    switch (preset) {
      case FILMIC_WORLDS:
        hA = 0.15f;
        hB = 0.50f;
        hC = 0.10f;
        hD = 0.20f;
        hE = 0.02f;
        hF = 0.30f;
        hW = 11.2f;
        gamma = Scene.DEFAULT_GAMMA;
        break;
      case GDC:
        hA = 0.22f;
        hB = 0.30f;
        hC = 0.10f;
        hD = 0.20f;
        hE = 0.01f;
        hF = 0.30f;
        hW = 11.2f;
        gamma = Scene.DEFAULT_GAMMA;
        break;
    }
    recalculateWhiteScale();
  }

  @Override
  public void processPixel(double[] pixel) {
    for (int i = 0; i < 3; ++i) {
      pixel[i] *= 2; // exposure bias
      pixel[i] = ((pixel[i] * (hA * pixel[i] + hC * hB) + hD * hE) / (pixel[i] * (hA * pixel[i] + hB) + hD * hF)) - hE / hF;
      pixel[i] *= whiteScale;
      pixel[i] = FastMath.pow(pixel[i], 1 / gamma);
    }
  }

  @Override
  public String getName() {
    return "Hable Filmic";
  }

  @Override
  public String getId() {
    return "TONEMAP3";
  }

  @Override
  public String getDescription() {
    return "John Hable's filmic tonemapping curve, with presets from his blog post and from his "
        + "GDC talk.\n"
        + "- Blog post: http://filmicworlds.com/blog/filmic-tonemapping-operators/\n"
        + "- GDC talk: https://www.gdcvault.com/play/1012351/Uncharted-2-HDR";
  }

  @Override
  public void fromJson(JsonObject json) {
    reset();
    hA = json.get("shoulderStrength").floatValue(hA);
    hB = json.get("linearStrength").floatValue(hB);
    hC = json.get("linearAngle").floatValue(hC);
    hD = json.get("toeStrength").floatValue(hD);
    hE = json.get("toeNumerator").floatValue(hE);
    hF = json.get("toeDenominator").floatValue(hF);
    hW = json.get("linearWhitePointValue").floatValue(hW);
    gamma = json.get("gamma").floatValue(gamma);
    recalculateWhiteScale();
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("shoulderStrength", hA);
    json.add("linearStrength", hB);
    json.add("linearAngle", hC);
    json.add("toeStrength", hD);
    json.add("toeNumerator", hE);
    json.add("toeDenominator", hF);
    json.add("linearWhitePointValue", hW);
    json.add("gamma", gamma);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    RenderControlsFxController controller = parent.getController();

    MenuButton presetChooser = new MenuButton();
    DoubleAdjuster gamma = new DoubleAdjuster();
    DoubleAdjuster shoulderStrength = new DoubleAdjuster();
    DoubleAdjuster linearStrength = new DoubleAdjuster();
    DoubleAdjuster linearAngle = new DoubleAdjuster();
    DoubleAdjuster toeStrength = new DoubleAdjuster();
    DoubleAdjuster toeNumerator = new DoubleAdjuster();
    DoubleAdjuster toeDenominator = new DoubleAdjuster();
    DoubleAdjuster linearWhitePointValue = new DoubleAdjuster();

    presetChooser.setText("Load preset");
    for (HableFilmicFilter.Preset preset : HableFilmicFilter.Preset.values()) {
      MenuItem menuItem = new MenuItem(preset.toString());
      menuItem.setOnAction(e -> {
        applyPreset(preset);
        gamma.set(getGamma());
        shoulderStrength.set(getShoulderStrength());
        linearStrength.set(getLinearStrength());
        linearAngle.set(getLinearAngle());
        toeStrength.set(getToeStrength());
        toeNumerator.set(getToeNumerator());
        toeDenominator.set(getToeDenominator());
        linearWhitePointValue.set(getLinearWhitePointValue());
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

    shoulderStrength.setName("Shoulder strength");
    shoulderStrength.setRange(0, 10);
    shoulderStrength.set(getShoulderStrength());
    shoulderStrength.onValueChange(value -> {
      setShoulderStrength(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    linearStrength.setName("Linear strength");
    linearStrength.setRange(0, 1);
    linearStrength.set(getLinearStrength());
    linearStrength.onValueChange(value -> {
      setLinearStrength(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    linearAngle.setName("Linear angle");
    linearAngle.setRange(0, 1);
    linearAngle.set(getLinearAngle());
    linearAngle.onValueChange(value -> {
      setLinearAngle(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    toeStrength.setName("Toe strength");
    toeStrength.setRange(0, 1);
    toeStrength.set(getToeStrength());
    toeStrength.onValueChange(value -> {
      setToeStrength(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    toeNumerator.setName("Toe numerator");
    toeNumerator.setRange(0, 1);
    toeNumerator.set(getToeNumerator());
    toeNumerator.onValueChange(value -> {
      setToeNumerator(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    toeDenominator.setName("Toe denominator");
    toeDenominator.setRange(0, 1);
    toeDenominator.set(getToeDenominator());
    toeDenominator.onValueChange(value -> {
      setToeDenominator(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    linearWhitePointValue.setName("Linear white point value");
    linearWhitePointValue.setRange(0, 20);
    linearWhitePointValue.set(getLinearWhitePointValue());
    linearWhitePointValue.onValueChange(value -> {
      setLinearWhitePointValue(value.floatValue());
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(
      6,
      presetChooser,
      gamma,
      shoulderStrength,
      linearStrength,
      linearAngle,
      toeStrength,
      toeNumerator,
      toeDenominator,
      linearWhitePointValue
    );
  }
}
