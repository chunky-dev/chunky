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
  private boolean allowZero = true;

  private final int maximumFractionDigits = decimalFormat.getMaximumFractionDigits();

  public enum AllowedRange {
    FULL,
    NON_NEGATIVE,
    POSITIVE;
  }

  public ValidatingNumberStringConverter() {
    this(false);
  }

  public ValidatingNumberStringConverter(boolean parseIntegerOnly) {
    this(parseIntegerOnly, AllowedRange.FULL);
  }

  public ValidatingNumberStringConverter(boolean parseIntegerOnly, AllowedRange range) {
    setParseIntegerOnly(parseIntegerOnly);
    setRange(range);
  }

  public ValidatingNumberStringConverter(AllowedRange range) {
    setRange(range);
  }

  public DecimalFormat getDecimalFormat() {
    return decimalFormat;
  }

  public void setRange(AllowedRange range) {
    switch (range) {
      case POSITIVE:
        setAllowZero(false);
        setParseNonNegativeOnly(true);
        break;
      case NON_NEGATIVE:
        setAllowZero(true);
        setParseNonNegativeOnly(true);
        break;
      case FULL:
        setAllowZero(true);
        setParseNonNegativeOnly(false);
        break;
    }
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

  public void setAllowZero(boolean allowZero) {
    this.allowZero = allowZero;
  }

  public boolean shouldAllowZero() {
    return allowZero;
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

    if(!allowZero && number.intValue() == 0) {
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
