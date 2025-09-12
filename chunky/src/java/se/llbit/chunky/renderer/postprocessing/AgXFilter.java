package se.llbit.chunky.renderer.postprocessing;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Matrix3;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

public class AgXFilter extends SimplePixelPostProcessingFilter {
  private enum Preset {
    DEFAULT, GOLDEN, PUNCHY
  }

  private static void matrixMultiplyByVector(Matrix3 m, Vector3 v) {
    v.set(
        m.m11 * v.x + m.m21 * v.y + m.m31 * v.z,
        m.m12 * v.x + m.m22 * v.y + m.m32 * v.z,
        m.m13 * v.x + m.m23 * v.y + m.m33 * v.z
    );
  }

  private static final Matrix3 LINEAR_REC2020_TO_LINEAR_SRGB = new Matrix3(
      1.6605, -0.1246, -0.0182,
      -0.5876, 1.1329, -0.1006,
      -0.0728, -0.0083, 1.1187
  );

  private static final Matrix3 LINEAR_SRGB_TO_LINEAR_REC2020 = new Matrix3(
      0.6274, 0.0691, 0.0164,
      0.3293, 0.9195, 0.0880,
      0.0433, 0.0113, 0.8956
  );

  /**
   * Converted to column major from blender: <a href="https://github.com/blender/blender/blob/fc08f7491e7eba994d86b610e5ec757f9c62ac81/release/datafiles/colormanagement/config.ocio#L358">...</a>
   */
  private static final Matrix3 AGX_INSET_MATRIX = new Matrix3(
      0.856627153315983, 0.137318972929847, 0.11189821299995,
      0.0951212405381588, 0.761241990602591, 0.0767994186031903,
      0.0482516061458583, 0.101439036467562, 0.811302368396859
  );

  /**
   * Converted to column major and inverted from <a href="https://github.com/EaryChow/AgX_LUT_Gen/blob/ab7415eca3cbeb14fd55deb1de6d7b2d699a1bb9/AgXBaseRec2020.py#L25">...</a>
   * <a href="https://github.com/google/filament/blob/bac8e58ee7009db4d348875d274daf4dd78a3bd1/filament/src/ToneMapper.cpp#L273-L278">...</a>
   */
  private static final Matrix3 AGX_OUTSET_MATRIX = new Matrix3(
      1.1271005818144368, -0.1413297634984383, -0.14132976349843826,
      -0.11060664309660323, 1.157823702216272, -0.11060664309660294,
      -0.016493938717834573, -0.016493938717834257, 1.2519364065950405
  );

  private static final double AGX_MIN_EV = -12.47393;
  private static final double AGX_MAX_EV = 4.026069;

  private final Vector3 offset = new Vector3(1);
  private double offsetMagnitude = 0;
  private final Vector3 slope = new Vector3(1);
  private double slopeMagnitude = 1;
  private final Vector3 power = new Vector3(1);
  private double powerMagnitude = 1;
  private double saturation = 1;
  private double gamma = Scene.DEFAULT_GAMMA;

  public AgXFilter() {
    reset();
  }

  private void agxAscCdl(Vector3 color) {
    final Vector3 lw = new Vector3(0.2126, 0.7152, 0.0722);
    double luma = color.dot(lw);
    Vector3 c = new Vector3(
        FastMath.pow(color.x * slope.x * slopeMagnitude + offset.x * offsetMagnitude, power.x * powerMagnitude),
        FastMath.pow(color.y * slope.y * slopeMagnitude + offset.y * offsetMagnitude, power.y * powerMagnitude),
        FastMath.pow(color.z * slope.z * slopeMagnitude + offset.z * offsetMagnitude, power.z * powerMagnitude)
    );
    color.set(
        luma + saturation * (c.x - luma),
        luma + saturation * (c.y - luma),
        luma + saturation * (c.z - luma)
    );
  }

  @Override
  public void processPixel(double[] pixel) {
    Vector3 color = new Vector3(pixel[0], pixel[1], pixel[2]);

    matrixMultiplyByVector(LINEAR_SRGB_TO_LINEAR_REC2020, color);

    matrixMultiplyByVector(AGX_INSET_MATRIX, color);

    color.x = FastMath.max(color.x, 1e-10);
    color.y = FastMath.max(color.y, 1e-10);
    color.z = FastMath.max(color.z, 1e-10);

    color.x = QuickMath.clamp(FastMath.log(2, color.x), AGX_MIN_EV, AGX_MAX_EV);
    color.y = QuickMath.clamp(FastMath.log(2, color.y), AGX_MIN_EV, AGX_MAX_EV);
    color.z = QuickMath.clamp(FastMath.log(2, color.z), AGX_MIN_EV, AGX_MAX_EV);

    color.x = (color.x - AGX_MIN_EV) / (AGX_MAX_EV - AGX_MIN_EV);
    color.y = (color.y - AGX_MIN_EV) / (AGX_MAX_EV - AGX_MIN_EV);
    color.z = (color.z - AGX_MIN_EV) / (AGX_MAX_EV - AGX_MIN_EV);

    color.x = QuickMath.clamp(color.x, 0, 1);
    color.y = QuickMath.clamp(color.y, 0, 1);
    color.z = QuickMath.clamp(color.z, 0, 1);

    Vector3 x2 = color.rMultiplyEntrywise(color);
    Vector3 x4 = x2.rMultiplyEntrywise(x2);

    color.x = + 15.5     * x4.x * x2.x
              - 40.14    * x4.x * color.x
              + 31.96    * x4.x
              - 6.868    * x2.x * color.x
              + 0.4298   * x2.x
              + 0.1191   * color.x
              - 0.00232;
    color.y = + 15.5     * x4.y * x2.y
              - 40.14    * x4.y * color.y
              + 31.96    * x4.y
              - 6.868    * x2.y * color.y
              + 0.4298   * x2.y
              + 0.1191   * color.y
              - 0.00232;
    color.z = + 15.5     * x4.z * x2.z
              - 40.14    * x4.z * color.z
              + 31.96    * x4.z
              - 6.868    * x2.z * color.z
              + 0.4298   * x2.z
              + 0.1191   * color.z
              - 0.00232;

    agxAscCdl(color);

    matrixMultiplyByVector(AGX_OUTSET_MATRIX, color);

    color.x = FastMath.pow(FastMath.max(0, color.x), gamma);
    color.y = FastMath.pow(FastMath.max(0, color.y), gamma);
    color.z = FastMath.pow(FastMath.max(0, color.z), gamma);

    matrixMultiplyByVector(LINEAR_REC2020_TO_LINEAR_SRGB, color);

    color.x = QuickMath.clamp(color.x, 0, 1);
    color.y = QuickMath.clamp(color.y, 0, 1);
    color.z = QuickMath.clamp(color.z, 0, 1);

    pixel[0] = color.x;
    pixel[1] = color.y;
    pixel[2] = color.z;
  }

  private void applyPreset(Preset preset) {
    switch (preset) {
      case GOLDEN:
        slope.set(1.0, 0.9, 0.5);
        slopeMagnitude = 1;
        offset.set(1);
        offsetMagnitude = 0;
        power.set(1);
        powerMagnitude = 0.8;
        saturation = 1.3;
        gamma = Scene.DEFAULT_GAMMA;
        break;
      case PUNCHY:
        slope.set(1);
        slopeMagnitude = 1;
        offset.set(1);
        offsetMagnitude = 0;
        power.set(1);
        powerMagnitude = 1.35;
        saturation = 1.4;
        gamma = Scene.DEFAULT_GAMMA;
        break;
      case DEFAULT:
      default:
        slope.set(1);
        slopeMagnitude = 1;
        offset.set(1);
        offsetMagnitude = 0;
        power.set(1);
        powerMagnitude = 1;
        saturation = 1;
        gamma = Scene.DEFAULT_GAMMA;
        break;
    }
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    RenderControlsFxController controller = parent.getController();

    MenuButton presetChooser = new MenuButton("Load preset");
    LuxColorPicker slopePicker = new LuxColorPicker();
    ChangeListener<Color> slopePickerListener = (observable, oldValue, newValue) -> {
      slope.set(ColorUtil.fromFx(newValue));
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    };
    DoubleAdjuster slopeMagnitudeAdjuster = new DoubleAdjuster();
    LuxColorPicker offsetPicker = new LuxColorPicker();
    ChangeListener<Color> offsetPickerListener = (observable, oldValue, newValue) -> {
      offset.set(ColorUtil.fromFx(newValue));
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    };
    DoubleAdjuster offsetMagnitudeAdjuster = new DoubleAdjuster();
    LuxColorPicker powerPicker = new LuxColorPicker();
    ChangeListener<Color> powerPickerListener = (observable, oldValue, newValue) -> {
      power.set(ColorUtil.fromFx(newValue));
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    };
    DoubleAdjuster powerMagnitudeAdjuster = new DoubleAdjuster();
    DoubleAdjuster saturationAdjuster = new DoubleAdjuster();
    DoubleAdjuster gammaAdjuster = new DoubleAdjuster();

    for (AgXFilter.Preset preset : AgXFilter.Preset.values()) {
      MenuItem menuItem = new MenuItem(preset.toString());
      menuItem.setOnAction(e -> {
        applyPreset(preset);
        slopePicker.colorProperty().removeListener(slopePickerListener);
        slopePicker.setColor(ColorUtil.toFx(slope));
        slopePicker.colorProperty().addListener(slopePickerListener);
        slopeMagnitudeAdjuster.set(slopeMagnitude);
        offsetPicker.colorProperty().removeListener(offsetPickerListener);
        offsetPicker.setColor(ColorUtil.toFx(offset));
        offsetPicker.colorProperty().addListener(offsetPickerListener);
        offsetMagnitudeAdjuster.set(offsetMagnitude);
        powerPicker.colorProperty().removeListener(powerPickerListener);
        powerPicker.setColor(ColorUtil.toFx(power));
        powerPicker.colorProperty().addListener(powerPickerListener);
        powerMagnitudeAdjuster.set(powerMagnitude);
        saturationAdjuster.set(saturation);
        gammaAdjuster.set(gamma);
        if (scene.getMode() == RenderMode.PREVIEW) {
          // Don't interrupt the render if we are currently rendering.
          scene.refresh();
        }
        scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
        controller.getCanvas().forceRepaint();
      });
      presetChooser.getItems().add(menuItem);
    }

    slopePicker.setText("Slope");
    slopePicker.setColor(ColorUtil.toFx(slope));
    slopePicker.colorProperty().addListener(slopePickerListener);

    slopeMagnitudeAdjuster.setName("Slope magnitude");
    slopeMagnitudeAdjuster.setRange(0, 10);
    slopeMagnitudeAdjuster.clampMin();
    slopeMagnitudeAdjuster.makeLogarithmic();
    slopeMagnitudeAdjuster.set(slopeMagnitude);
    slopeMagnitudeAdjuster.onValueChange(value -> {
      slopeMagnitude = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    offsetPicker.setText("Offset");
    offsetPicker.setColor(ColorUtil.toFx(offset));
    offsetPicker.colorProperty().addListener(offsetPickerListener);

    offsetMagnitudeAdjuster.setName("Offset magnitude");
    offsetMagnitudeAdjuster.setRange(0, 10);
    offsetMagnitudeAdjuster.clampMin();
    offsetMagnitudeAdjuster.makeLogarithmic();
    offsetMagnitudeAdjuster.set(offsetMagnitude);
    offsetMagnitudeAdjuster.onValueChange(value -> {
      offsetMagnitude = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    powerPicker.setText("Power");
    powerPicker.setColor(ColorUtil.toFx(power));
    powerPicker.colorProperty().addListener(powerPickerListener);

    powerMagnitudeAdjuster.setName("Power magnitude");
    powerMagnitudeAdjuster.setRange(0, 10);
    powerMagnitudeAdjuster.clampMin();
    powerMagnitudeAdjuster.makeLogarithmic();
    powerMagnitudeAdjuster.set(powerMagnitude);
    powerMagnitudeAdjuster.onValueChange(value -> {
      powerMagnitude = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    saturationAdjuster.setName("Saturation");
    saturationAdjuster.setRange(0, 10);
    saturationAdjuster.clampMin();
    saturationAdjuster.makeLogarithmic();
    saturationAdjuster.set(saturation);
    saturationAdjuster.onValueChange(value -> {
      saturation = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    gammaAdjuster.setName("Gamma");
    gammaAdjuster.setRange(0.001, 5);
    gammaAdjuster.clampMin();
    gammaAdjuster.set(gamma);
    gammaAdjuster.onValueChange(value -> {
      gamma = value;
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(6, presetChooser, slopePicker, slopeMagnitudeAdjuster, offsetPicker, offsetMagnitudeAdjuster, powerPicker, powerMagnitudeAdjuster, saturationAdjuster, gammaAdjuster);
  }

  @Override
  public String getName() {
    return "AgX Tone mapping";
  }

  @Override
  public String getId() {
    return "AGX";
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("offset", offset.toJson());
    json.add("offsetMagnitude", offsetMagnitude);
    json.add("slope", slope.toJson());
    json.add("slopeMagnitude", slopeMagnitude);
    json.add("power", power.toJson());
    json.add("powerMagnitude", powerMagnitude);
    json.add("saturation", saturation);
    json.add("gamma", gamma);
  }

  @Override
  public void fromJson(JsonObject json) {
    offset.fromJson(json.get("offset").asObject());
    offsetMagnitude = json.get("offsetMagnitude").doubleValue(0);
    slope.fromJson(json.get("slope").asObject());
    slopeMagnitude = json.get("slopeMagnitude").doubleValue(1);
    power.fromJson(json.get("power").asObject());
    powerMagnitude = json.get("powerMagnitude").doubleValue(1);
    saturation = json.get("saturation").doubleValue(1);
    gamma = json.get("gamma").doubleValue(Scene.DEFAULT_GAMMA);
  }

  @Override
  public void reset() {
    applyPreset(Preset.DEFAULT);
  }

  @Override
  public String getDescription() {
    return "";
  }
}
