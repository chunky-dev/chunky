package se.llbit.chunky.ui;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;

/**
 * A {@link TextField} that automatically converts the value to a number.
 * <p>
 * If the text content is not a number, the <code>invalid</code> style class is added to this
 * field.
 */
public class NumericTextField<T extends Property<Number>> extends TextField {

  private final T value;
  protected ValidatingNumberStringConverter converter = new ValidatingNumberStringConverter();

  private int maximumCharacterInputLength = 54; // pretty much at the max of double precision

  public NumericTextField(T value) {
    this.value = value;
    getStyleClass().add("numeric-text-field");

    // this is intentional, because otherwise input which changes decimal groupings crashes the event handler
    // e.g. backspace in these states: 1,0|00 or 1,00|0 or 1,000|
    // cause is that backspace wants to replace parts of the text which then get changed by the number converter
    // so that the caret is outside of text edit bounds (?)
    // TODO: fixme
    converter.getDecimalFormat().setGroupingUsed(false);

    this.value.addListener((observable, oldValue, newValue) -> {
      if (!this.focusedProperty().get()) {
        triggerRefresh(); // Change while not focused
      }
    });
    this.setOnKeyPressed(event -> {
      if (event.getCode().equals(KeyCode.ENTER)) {
        triggerRefresh(); // Enter pressed
      }
    });
    this.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        triggerRefresh(); // Focus lost
      }
    });

    textProperty().addListener(observable -> {
      Number result = converter.fromString(textProperty().get());
      if(result != null && isValid()) {
        value.setValue(result);
      }
    });
    setTextFormatter(new TextFormatter<>(change -> {
      if(change.getControlNewText().length() > maximumCharacterInputLength) {
        return null;
      }
      return change;
    }));
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

  public void setMaximumCharacterInputLength(int maximumCharacterInputLength) {
    this.maximumCharacterInputLength = maximumCharacterInputLength;
  }

  public int getMaximumCharacterInputLength() {
    return maximumCharacterInputLength;
  }
}
