/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.ui;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * An integer adjuster stores integer values.
 */
public class IntegerAdjuster extends SliderAdjuster<Integer> {
  private int min = Integer.MIN_VALUE;
  private int max = Integer.MAX_VALUE;

  public IntegerAdjuster() {
    super(new SimpleIntegerProperty());
    this.valueField.getConverter().setParseIntegerOnly(true);
    this.valueField.triggerRefresh();
  }

  @Override
  public void setRange(double min, double max) {
    super.setRange(min, max);
    this.min = (int) min;
    this.max = (int) max;
  }

  @Override
  protected Integer clamp(Number value) {
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
