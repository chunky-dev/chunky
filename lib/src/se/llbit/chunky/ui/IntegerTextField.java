package se.llbit.chunky.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class IntegerTextField extends NumericTextField<IntegerProperty> {

  public IntegerTextField() {
    super(new SimpleIntegerProperty());
  }
}
