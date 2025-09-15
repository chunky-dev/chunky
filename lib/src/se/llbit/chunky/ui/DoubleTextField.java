package se.llbit.chunky.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DoubleTextField extends NumericTextField<DoubleProperty> {

  public DoubleTextField() {
    super(new SimpleDoubleProperty());
  }

  public void setMaximumFractionDigits(int maximumFractionDigits) {
    this.converter.setMaximumFractionDigits(maximumFractionDigits);
  }
}
