/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.ui;

import se.llbit.math.Vector4d;

@SuppressWarnings("serial")
public class HuePicker extends GradientPicker {

	private final ColorPicker colorPicker;

	public HuePicker(final ColorPicker colorPicker) {
		super(colorPicker);
		this.colorPicker = colorPicker;

		gradient.add(new Vector4d(1, 0, 0, 0.00));
		gradient.add(new Vector4d(1, 1, 0, 1/6.0));
		gradient.add(new Vector4d(0, 1, 0, 2/6.0));
		gradient.add(new Vector4d(0, 1, 1, 3/6.0));
		gradient.add(new Vector4d(0, 0, 1, 4/6.0));
		gradient.add(new Vector4d(1, 0, 1, 5/6.0));
		gradient.add(new Vector4d(1, 0, 0, 1.00));
	}

	public void setHue(double hue) {
		setMarkerAt(hue);
	}

	@Override
	protected void onMarkerMoved() {
		colorPicker.setHue(getPickerValue());
	}

}
