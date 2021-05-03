package se.llbit.chunky.ui;

import java.text.ParseException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.converter.NumberStringConverter;

/**
 * A {@link NumberStringConverter} that does not throw a RuntimeException if parsing fails but just
 * treats invalid input as empty input.
 */
public class SilentNumberStringConverter extends NumberStringConverter {

  private final BooleanProperty isValid = new SimpleBooleanProperty(true);

  public SilentNumberStringConverter() {
  }

  public SilentNumberStringConverter(ChangeListener<Boolean> listener) {
    isValid.addListener(listener);
  }

  @Override
  public Number fromString(String value) {
    try {
      Number number = super.fromString(value);
      isValid.setValue(number != null);
      return number;
    } catch (RuntimeException e) {
      if (e.getCause() instanceof ParseException) {
        isValid.setValue(false);
        return null;
      } else {
        throw e;
      }
    }
  }

  @Override
  public String toString(Number value) {
    isValid.setValue(value != null);
    return super.toString(value);
  }

  public BooleanProperty validProperty() {
    return this.isValid;
  }
}
