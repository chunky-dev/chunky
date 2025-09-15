package se.llbit.chunky.renderer.scene;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.elements.TextFieldLabelWrapper;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.math.SimplexNoise;
import se.llbit.math.Vector3;
import se.llbit.util.Configurable;
import se.llbit.util.HasControls;

/**
 * Wraps a {@link se.llbit.math.SimplexNoise} instance and provides controls for the noise.
 */
public class SimplexNoiseConfig implements Configurable, HasControls {
  private long seed;
  private int iterations = 5;
  private float gain = 0.5f;
  private float lacunarity = 2;
  private float amplitude = 1;
  private float frequency = 0.01f;
  private final Vector3 scale = new Vector3(1);
  private final Vector3 offset = new Vector3();
  private SimplexNoise simplexNoise;

  public SimplexNoiseConfig() {
    this(0);
  }

  public SimplexNoiseConfig(long seed) {
    setSeed(seed);
  }

  public void setSeed(long seed) {
    this.seed = seed;
    this.simplexNoise = new SimplexNoise(seed);
  }

  public long getSeed() {
    return this.seed;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public int getIterations() {
    return this.iterations;
  }

  public void setAmplitude(float amplitude) {
    this.amplitude = amplitude;
  }

  public float getAmplitude() {
    return this.amplitude;
  }

  public void setFrequency(float frequency) {
    this.frequency = frequency;
  }

  public float getFrequency() {
    return this.frequency;
  }

  public void setGain(float gain) {
    this.gain = gain;
  }

  public float getGain() {
    return this.gain;
  }

  public void setLacunarity(float lacunarity) {
    this.lacunarity = lacunarity;
  }

  public float getLacunarity() {
    return this.lacunarity;
  }

  public void setScaleX(double value) {
    this.scale.x = value;
  }

  public void setScaleY(double value) {
    this.scale.y = value;
  }

  public void setScaleZ(double value) {
    this.scale.z = value;
  }

  public double getScaleX() {
    return this.scale.x;
  }

  public double getScaleY() {
    return this.scale.y;
  }

  public double getScaleZ() {
    return this.scale.z;
  }

  public void setOffsetX(double value) {
    this.offset.x = value;
  }

  public void setOffsetY(double value) {
    this.offset.y = value;
  }

  public void setOffsetZ(double value) {
    this.offset.z = value;
  }

  public double getOffsetX() {
    return this.offset.x;
  }

  public double getOffsetY() {
    return this.offset.y;
  }

  public double getOffsetZ() {
    return this.offset.z;
  }

  public float calculate(float x, float y, float z) {
    float value = 0;
    float amplitude = this.amplitude;
    float frequency = this.frequency;
    for (int i = 0; i < this.iterations; i++) {
      value += this.simplexNoise.calculate(
        (x + (float) this.offset.x) / (float) this.scale.x * frequency,
        (y + (float) this.offset.y) / (float) this.scale.y * frequency,
        (z + (float) this.offset.z) / (float) this.scale.z * frequency
      ) * amplitude;

      frequency *= lacunarity;
      amplitude *= gain;
    }
    return value;
  }

  public SimplexNoise getSimplexNoise() {
    return this.simplexNoise;
  }

  @Override
  public void fromJson(JsonObject json) {
    seed = json.get("seed").longValue(seed);
    iterations = json.get("iterations").intValue(iterations);
    amplitude = json.get("amplitude").floatValue(amplitude);
    frequency = json.get("frequency").floatValue(frequency);
    gain = json.get("gain").floatValue(gain);
    lacunarity = json.get("lacunarity").floatValue(lacunarity);
    scale.fromJson(json.get("scale").asObject());
    offset.fromJson(json.get("offset").asObject());
    setSeed(seed);
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("seed", seed);
    json.add("iterations", iterations);
    json.add("amplitude", amplitude);
    json.add("frequency", frequency);
    json.add("gain", gain);
    json.add("lacunarity", lacunarity);
    json.add("scale", scale.toJson());
    json.add("offset", offset.toJson());
    return json;
  }

  @Override
  public void reset() {

  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    IntegerAdjuster iterationsAdjuster = new IntegerAdjuster();
    DoubleAdjuster amplitudeAdjuster = new DoubleAdjuster();
    DoubleAdjuster frequencyAdjuster = new DoubleAdjuster();
    DoubleAdjuster gainAdjuster = new DoubleAdjuster();
    DoubleAdjuster lacunarityAdjuster = new DoubleAdjuster();
    DoubleTextField xScale = new DoubleTextField();
    DoubleTextField yScale = new DoubleTextField();
    DoubleTextField zScale = new DoubleTextField();
    DoubleTextField xOffset = new DoubleTextField();
    DoubleTextField yOffset = new DoubleTextField();
    DoubleTextField zOffset = new DoubleTextField();

    TextFieldLabelWrapper x1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper y1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper z1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper x2Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper y2Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper z2Text = new TextFieldLabelWrapper();

    x1Text.setTextField(xScale);
    y1Text.setTextField(yScale);
    z1Text.setTextField(zScale);
    x2Text.setTextField(xOffset);
    y2Text.setTextField(yOffset);
    z2Text.setTextField(zOffset);

    x1Text.setLabelText("x:");
    y1Text.setLabelText("y:");
    z1Text.setLabelText("z:");
    x2Text.setLabelText("x:");
    y2Text.setLabelText("y:");
    z2Text.setLabelText("z:");

    iterationsAdjuster.setName("Iterations");
    iterationsAdjuster.setRange(1, 25);
    iterationsAdjuster.clampMin();
    iterationsAdjuster.set(this.iterations);
    iterationsAdjuster.onValueChange(value -> {
      this.iterations = value;
      scene.refresh();
    });

    amplitudeAdjuster.setName("Amplitude");
    amplitudeAdjuster.setRange(0.001, 2);
    amplitudeAdjuster.clampMin();
    amplitudeAdjuster.set(this.amplitude);
    amplitudeAdjuster.onValueChange(value -> {
      this.amplitude = value.floatValue();
      scene.refresh();
    });

    frequencyAdjuster.setName("Frequency");
    frequencyAdjuster.setRange(0.001, 1);
    frequencyAdjuster.clampMin();
    frequencyAdjuster.set(this.frequency);
    frequencyAdjuster.onValueChange(value -> {
      this.frequency = value.floatValue();
      scene.refresh();
    });

    gainAdjuster.setName("Gain");
    gainAdjuster.setRange(0.001, 10);
    gainAdjuster.set(this.gain);
    gainAdjuster.onValueChange(value -> {
      this.gain = value.floatValue();
      scene.refresh();
    });

    lacunarityAdjuster.setName("Lacunarity");
    lacunarityAdjuster.setRange(0.001, 10);
    lacunarityAdjuster.set(this.lacunarity);
    lacunarityAdjuster.onValueChange(value -> {
      this.lacunarity = value.floatValue();
      scene.refresh();
    });

    xScale.valueProperty().setValue(this.scale.x);
    xScale.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.scale.x = newValue.doubleValue();
      scene.refresh();
    });

    yScale.valueProperty().setValue(this.scale.y);
    yScale.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.scale.y = newValue.doubleValue();
      scene.refresh();
    });

    zScale.valueProperty().setValue(this.scale.z);
    zScale.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.scale.z = newValue.doubleValue();
      scene.refresh();
    });

    xOffset.valueProperty().setValue(this.offset.x);
    xOffset.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.offset.x = newValue.doubleValue();
      scene.refresh();
    });

    yOffset.valueProperty().setValue(this.offset.y);
    yOffset.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.offset.y = newValue.doubleValue();
      scene.refresh();
    });

    zOffset.valueProperty().setValue(this.offset.z);
    zOffset.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.offset.z = newValue.doubleValue();
      scene.refresh();
    });

    GridPane pane1 = new GridPane();
    pane1.setVgap(6);
    pane1.setHgap(6);

    pane1.addRow(0, iterationsAdjuster, amplitudeAdjuster);
    pane1.addRow(1, frequencyAdjuster, gainAdjuster);
    pane1.addRow(2, lacunarityAdjuster);

    ColumnConstraints labelConstraints = new ColumnConstraints();
    labelConstraints.setHgrow(Priority.NEVER);
    labelConstraints.setPrefWidth(90);
    ColumnConstraints posFieldConstraints = new ColumnConstraints();
    posFieldConstraints.setMinWidth(20);
    posFieldConstraints.setPrefWidth(90);

    GridPane pane2 = new GridPane();
    pane2.getColumnConstraints().addAll(labelConstraints, posFieldConstraints, posFieldConstraints, posFieldConstraints);
    pane2.setVgap(6);
    pane2.setHgap(6);

    pane2.addRow(0, new Label("Scale"), x1Text, y1Text, z1Text);
    pane2.addRow(1, new Label("Offset"), x2Text, y2Text, z2Text);

    return new VBox(
      6,
      pane1,
      pane2
    );
  }
}
