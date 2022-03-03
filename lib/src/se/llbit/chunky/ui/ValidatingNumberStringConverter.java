package se.llbit.chunky.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * Converts a String to a Number (Long if `parseIntegerOnly` otherwise Double).
 * Updates its isValid state for each conversion and returns null if the parsing failed.
 */
public class ValidatingNumberStringConverter extends StringConverter<Number> {

  private final BooleanProperty isValid = new SimpleBooleanProperty(true);

  private final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
  private boolean parseNonNegativeOnly = false;

  private final int maximumFractionDigits = decimalFormat.getMaximumFractionDigits();

  public ValidatingNumberStringConverter() {
    this(false, false);
  }

  public ValidatingNumberStringConverter(boolean parseIntegerOnly) {
    this(parseIntegerOnly, false);
  }

  public ValidatingNumberStringConverter(boolean parseIntegerOnly, boolean parseNonNegativeOnly) {
    setParseIntegerOnly(parseIntegerOnly);
    setParseNonNegativeOnly(parseNonNegativeOnly);
  }

  public DecimalFormat getDecimalFormat() {
    return decimalFormat;
  }

  public void setParseIntegerOnly(boolean parseIntegerOnly) {
    if (parseIntegerOnly) {
      decimalFormat.setMinimumFractionDigits(0);
      decimalFormat.setMaximumFractionDigits(0);
    } else {
      decimalFormat.setMinimumFractionDigits(1);
      decimalFormat.setMaximumFractionDigits(maximumFractionDigits);
    }
    decimalFormat.setParseIntegerOnly(parseIntegerOnly);
  }

  public boolean shouldParseIntegerOnly() {
    return decimalFormat.isParseIntegerOnly();
  }

  public void setParseNonNegativeOnly(boolean parseNonNegativeOnly) {
    this.parseNonNegativeOnly = parseNonNegativeOnly;
  }

  public boolean shouldParseNonNegativeOnly() {
    return parseNonNegativeOnly;
  }

  @Override
  public Number fromString(String value) {
    if (value == null) {
      return invalidState();
    }

    value = value.trim();

    if (value.length() <= 0) {
      return invalidState();
    }

    ParsePosition position = new ParsePosition(0);
    Number number = decimalFormat.parse(value, position);

    if (number == null || position.getIndex() < value.length()) {
      return invalidState();
    }

    if(parseNonNegativeOnly && number.doubleValue() < 0.0) {
      return invalidState();
    }

    isValid.setValue(true);
    return number;
  }

  private Number invalidState() {
    isValid.set(false);
    return null;
  }

  @Override
  public String toString(Number value) {
    isValid.set(value != null);

    return (value == null)
      ? ""
      : decimalFormat.format(value);
  }

  public BooleanProperty validProperty() {
    return this.isValid;
  }
}
