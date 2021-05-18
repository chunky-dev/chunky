package se.llbit.chunky.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.control.TextField;

/**
 * A {@link TextField} that automatically converts the value to a number.
 * <p>
 * If the text content is not a number, the <code>invalid</code> style class is added to this
 * field.
 */
public class NumericTextField<T extends Property<Number>> extends TextField {

  private final T value;
  private final SilentNumberStringConverter converter;

  public NumericTextField(T value) {
    this.value = value;
    getStyleClass().add("numeric-text-field");
    converter = new SilentNumberStringConverter(
        (observable, oldValue, newVaue) -> {
          if (!newVaue) {
            getStyleClass().add("invalid");
          } else {
            getStyleClass().remove("invalid");
          }
        });
    textProperty().bindBidirectional(value, converter);
  }

  /**
   * @return The numeric content of this text field.
   */
  public T valueProperty() {
    return value;
  }

  /**
   * @return A property that indicates whether the value is a correct number
   */
  public BooleanProperty validProperty() {
    return converter.validProperty();
  }
}
