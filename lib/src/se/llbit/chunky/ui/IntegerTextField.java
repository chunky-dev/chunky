package se.llbit.chunky.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class IntegerTextField extends NumericTextField<IntegerProperty> {

  public IntegerTextField() {
    this(0);
  }

  public IntegerTextField(int initialValue) {
    super(new SimpleIntegerProperty(initialValue));
    getConverter().setParseIntegerOnly(true);
    triggerRefresh();

    setMaximumCharacterInputLength(10); // Integer.MAX_VALUE = 2147483647
    // warning: this setting will not prevent integer overflow!
  }

  public int getValue() {
    return valueProperty().get();
  }
}
