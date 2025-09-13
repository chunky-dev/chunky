package se.llbit.chunky.ui.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class MaterialReferenceColorData {
  private final Vector4 referenceColor;
  private final SimpleStringProperty red;
  private final SimpleStringProperty green;
  private final SimpleStringProperty blue;
  private final SimpleStringProperty range;

  public MaterialReferenceColorData(Vector4 referenceColor) {
    this.referenceColor = referenceColor;
    this.red = new SimpleStringProperty(String.valueOf((int) (referenceColor.x * 255)));
    this.green = new SimpleStringProperty(String.valueOf((int) (referenceColor.y * 255)));
    this.blue = new SimpleStringProperty(String.valueOf((int) (referenceColor.z * 255)));
    this.range = new SimpleStringProperty(String.valueOf((int) (referenceColor.w * 255)));
  }

  public void setReferenceColor(Vector3 color) {
    this.referenceColor.x = color.x;
    this.referenceColor.y = color.y;
    this.referenceColor.z = color.z;

    this.red.set(String.valueOf((int) (referenceColor.x * 255)));
    this.green.set(String.valueOf((int) (referenceColor.y * 255)));
    this.blue.set(String.valueOf((int) (referenceColor.z * 255)));
  }

  public Vector4 getReferenceColor() {
    return referenceColor;
  }

  public void setRange(int value) {
    this.referenceColor.w = value / 255d;

    this.range.set(String.valueOf(value));
  }

  public ObservableValue<String> redProperty() {
    return this.red;
  }

  public ObservableValue<String> greenProperty() {
    return this.green;
  }

  public ObservableValue<String> blueProperty() {
    return this.blue;
  }

  public ObservableValue<String> rangeProperty() {
    return this.range;
  }
}
