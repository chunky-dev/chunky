package se.llbit.chunky.ui;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * An integer adjuster stores integer values.
 */
public class PositiveIntegerAdjuster extends SliderAdjuster<Integer> {
  private int min = Integer.MIN_VALUE;
  private int max = Integer.MAX_VALUE;

  public PositiveIntegerAdjuster() {
    super(new SimpleIntegerProperty());
    this.valueField.getConverter().setParseIntegerOnly(true);
    this.valueField.getConverter().setParseNonNegativeOnly(true);
    this.valueField.triggerRefresh();
  }

  public void setRange(double min, double max) {
    super.setRange(min, max);
    this.min = (int) Math.max(0, min); // PositiveIntegerAdjuster can't have negative bounds
    this.max = (int) Math.max(0, max);
  }

  @Override protected Integer clamp(Number value) {
    int result = value.intValue();
    if (clampMax) {
      result = Math.min(result, max);
    }
    if (clampMin) {
      result = Math.max(result, min);
    }
    return result;
  }
}
