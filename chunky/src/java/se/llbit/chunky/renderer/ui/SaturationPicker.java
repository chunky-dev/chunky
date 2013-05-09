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

import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("serial")
public class SaturationPicker extends GradientPicker {

	private final ColorPicker colorPicker;

	public SaturationPicker(final ColorPicker colorPicker) {
		super(colorPicker);
		this.colorPicker = colorPicker;

		gradient.add(new Vector4d(0, 0, 0, 0.00));
		gradient.add(new Vector4d(1, 1, 1, 1.00));
	}

	@Override
	protected void onMarkerMoved() {
		colorPicker.setSaturation(getPickerValue());
	}

	private final Vector3d tmp = new Vector3d();
	public void setHueLight(double hue, double lightness) {
		se.llbit.math.Color.RGBfromHSL(tmp, hue, 0, lightness);
		gradient.get(0).set(tmp.x, tmp.y, tmp.z, 0);
		se.llbit.math.Color.RGBfromHSL(tmp, hue, 1, lightness);
		gradient.get(1).set(tmp.x, tmp.y, tmp.z, 1);
		updateGradient();
	}

	public void setSaturation(double value) {
		setMarkerAt(value);
	}

}
