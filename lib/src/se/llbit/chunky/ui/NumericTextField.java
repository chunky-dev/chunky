package se.llbit.chunky.ui;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.control.TextField;

/**
 * A {@link TextField} that automatically converts the value to a number.
 * <p>
 * If the text content is not a number, the <code>invalid</code> style class is added to this
 * field.
 */
public class NumericTextField<T extends Property<Number>> extends TextField {

  private final T value;
  protected ValidatingNumberStringConverter converter = new ValidatingNumberStringConverter();

  public NumericTextField(T value) {
    this.value = value;
    getStyleClass().add("numeric-text-field");
    textProperty().bindBidirectional(value, converter);
    validProperty().addListener(observable -> updateStyleClasses());
  }

  private void updateStyleClasses() {
    if (isValid()) {
      getStyleClass().remove("invalid");
    } else {
      getStyleClass().add("invalid");
    }
  }

  /**
   * @return The numeric content of this text field.
   */
  public T valueProperty() {
    return value;
  }

  public ValidatingNumberStringConverter getConverter() {
    return converter;
  }

  /**
   * @return A property that indicates whether the value is a correct number
   */
  public ReadOnlyBooleanProperty validProperty() {
    return converter.validProperty();
  }

  public boolean isValid() {
    return validProperty().get();
  }

  public void triggerRefresh() {
    textProperty().set(converter.toString(value.getValue()));
  }
}
