package se.llbit.chunky.renderer.postprocessing;

import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
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
 * Implementation of John Hable's filmic tonemapping  curve updated with improved controls.
 * <a href=http://filmicworlds.com/blog/filmic-tonemapping-with-piecewise-power-curves>link</a>
 */
public class HableUpdatedFilmicFilter extends SimplePixelPostProcessingFilter {
  private float gamma = Scene.DEFAULT_GAMMA;
  private float tStr = 0.5f;
  private float tLen = 0.5f;
  private float sStr = 2f;
  private float sLen = 0.5f;
  private float sAngle = 1f;

  private FloatFloatImmutablePair asSlopeIntercept(float x0, float x1, float y0, float y1) {
    float m;
    float b;
    float dy = y1 - y0;
    float dx = x1 - x0;
    if (dx == 0f) {
      m = 1f;
    } else {
      m = dy / dx;
    }
    b = y0 - x0 * m;
    return new FloatFloatImmutablePair(m, b);
  }

  private float evalDerivativeLinearGamma(float m, float b, float g, float x) {
    return g * m * (float) FastMath.pow(m * x + b, g - 1f);
  }

  private FloatFloatImmutablePair solveAB(float x0, float y0, float m) {
    float B = (m * x0) / y0;
    float lnA = (float) FastMath.log(y0) - B * (float) FastMath.log(x0);
    return new FloatFloatImmutablePair(lnA, B);
  }

  private float evalCurveSegment(float x, float offsetX, float offsetY,
      float scaleX, float scaleY, float lnA, float B) {
    float x0 = (x - offsetX) * scaleX;
    float y0 = 0f;
    if (x0 > 0f) {
      y0 = (float) FastMath.exp(lnA + B * FastMath.log(x0));
    }
    return y0 * scaleY + offsetY;
  }

  @Override
  public void processPixel(double[] pixel) {
    float tLen_ = (float) FastMath.pow(tLen, 2.2f);
    float x0 = 0.5f * tLen_;
    float y0 = (1.f - tStr) * x0;
    float remainingY = 1.f - y0;
    float initialW = x0 + remainingY;
    float y1Offset = (1.f - sLen) * remainingY;
    float x1 = x0 + y1Offset;
    float y1 = y0 + y1Offset;
    float extraW = (float) FastMath.pow(2.f, sStr) - 1.f;
    float W = initialW + extraW;
    float overshootX = (2.f * W) * sAngle * sStr;
    float overshootY = 0.5f * sAngle * sStr;
    float invGamma = 1.f / gamma;

    float curveWinv = 1.f / W;
    x0 /= W;
    x1 /= W;
    overshootX /= W;

    FloatFloatImmutablePair tmp = asSlopeIntercept(x0, x1, y0, y1);
    float m = tmp.firstFloat();
    float b = tmp.secondFloat();
    float g = invGamma;

    float midOffsetX = -(b / m);
    float midOffsetY = 0.f;
    float midScaleX  = 1.f;
    float midScaleY  = 1.f;
    float midLnA = g * (float) FastMath.log(m);
    float midB = g;

    float toeM = evalDerivativeLinearGamma(m, b, g, x0);
    float shoulderM = evalDerivativeLinearGamma(m, b, g, x1);

    y0 = (float) FastMath.max(1e-5f, FastMath.pow(y0, invGamma));
    y1 = (float) FastMath.max(1e-5f, FastMath.pow(y1, invGamma));
    overshootY = (float) FastMath.pow(1f + overshootY, invGamma) - 1f;

    tmp = solveAB(x0, y0, toeM);

    float toeOffsetX = 0.f;
    float toeOffsetY = 0.f;
    float toeScaleX = 1.f;
    float toeScaleY = 1.f;
    float toeLnA = tmp.firstFloat();
    float toeB = tmp.secondFloat();

    float shoulderX0 = (1.f + overshootX) - x1;
    float shoulderY0 = (1.f + overshootY) - y1;
    tmp = solveAB(shoulderX0, shoulderY0, shoulderM);

    float shoulderOffsetX =  1.f + overshootX;
    float shoulderOffsetY =  1.f + overshootY;
    float shoulderScaleX = -1.f;
    float shoulderScaleY = -1.f;
    float shoulderLnA = tmp.firstFloat();
    float shoulderB = tmp.secondFloat();

    float scale = evalCurveSegment(1f, shoulderOffsetX, shoulderOffsetY, shoulderScaleX,
        shoulderScaleY, shoulderLnA, shoulderB);
    float invScale = 1f / scale;
    toeOffsetY *= invScale;
    toeScaleY *= invScale;
    midOffsetY *= invScale;
    midScaleY *= invScale;
    shoulderOffsetY *= invScale;
    shoulderScaleY *= invScale;

    for (int i = 0; i < 3; i++) {
      float normX = (float) pixel[i] * curveWinv;
      float res;
      if (normX < x0) {
        res = evalCurveSegment(normX,
            toeOffsetX, toeOffsetY,
            toeScaleX, toeScaleY,
            toeLnA, toeB);
      } else if (normX < x1) {
        res = evalCurveSegment(normX,
            midOffsetX, midOffsetY,
            midScaleX, midScaleY,
            midLnA, midB);
      } else {
        res = evalCurveSegment(normX,
            shoulderOffsetX, shoulderOffsetY,
            shoulderScaleX, shoulderScaleY,
            shoulderLnA, shoulderB);
      }
      pixel[i] = res;
    }
  }

  @Override
  public void fromJson(JsonObject json) {
    gamma = json.get("gamma").floatValue(gamma);
    tStr = json.get("tStr").floatValue(tStr);
    tLen = json.get("tLen").floatValue(tLen);
    sStr = json.get("sStr").floatValue(sStr);
    sLen = json.get("sLen").floatValue(sLen);
    sAngle = json.get("sAngle").floatValue(sAngle);
  }

  @Override
  public void filterSettingsToJson(JsonObject json) {
    json.add("gamma", gamma);
    json.add("tStr", tStr);
    json.add("tLen", tLen);
    json.add("sStr", sStr);
    json.add("sLen", sLen);
    json.add("sAngle", sAngle);
  }

  @Override
  public void reset() {
    gamma = Scene.DEFAULT_GAMMA;
    tStr = 0.5f;
    tLen = 0.5f;
    sStr = 2f;
    sLen = 0.5f;
    sAngle = 1f;
  }

  @Override
  public String getName() {
    return "Hable (Updated) Filmic";
  }

  @Override
  public String getId() {
    return "HABLE_UPDATED_FILMIC";
  }

  @Override
  public String getDescription() {
    return "John Hable's improved filmic tonemapping curve, based on the original,"
        + " but with improved controllability.\n"
        + "http://filmicworlds.com/blog/filmic-tonemapping-with-piecewise-power-curves/";
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();
    RenderControlsFxController controller = parent.getController();

    DoubleAdjuster gamma = new DoubleAdjuster();
    DoubleAdjuster tStr = new DoubleAdjuster();
    DoubleAdjuster tLen = new DoubleAdjuster();
    DoubleAdjuster sStr = new DoubleAdjuster();
    DoubleAdjuster sLen = new DoubleAdjuster();
    DoubleAdjuster sAngle = new DoubleAdjuster();

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

    tStr.setName("Toe strength");
    tStr.setRange(0, 1);
    tStr.set(this.tStr);
    tStr.onValueChange(value -> {
      this.tStr = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    tLen.setName("Toe length");
    tLen.setRange(0, 1);
    tLen.set(this.tLen);
    tLen.onValueChange(value -> {
      this.tLen = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    sStr.setName("Shoulder strength");
    sStr.setRange(0, 10);
    sStr.set(this.sStr);
    sStr.onValueChange(value -> {
      this.sStr = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    sLen.setName("Shoulder length");
    sLen.setRange(1e-5, 1 - 1e-5);
    sLen.setMaximumFractionDigits(5);
    sLen.set(this.sLen);
    sLen.onValueChange(value -> {
      this.sLen = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    sAngle.setName("Shoulder angle");
    sAngle.setRange(0, 1);
    sAngle.set(this.sAngle);
    sAngle.onValueChange(value -> {
      this.sAngle = value.floatValue();
      if (scene.getMode() == RenderMode.PREVIEW) {
        // Don't interrupt the render if we are currently rendering.
        scene.refresh();
      }
      scene.postProcessFrame(new TaskTracker(ProgressListener.NONE));
      controller.getCanvas().forceRepaint();
    });

    return new VBox(6, gamma, tStr, tLen, sStr, sLen, sAngle);
  }
}
