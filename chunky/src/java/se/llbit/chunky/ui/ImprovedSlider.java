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
package se.llbit.chunky.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Clicking in the slider track sets the thumb to that position.
 */
@SuppressWarnings("serial")
public class ImprovedSlider extends JSlider {

	public ImprovedSlider(int i, int yMax) {
		super(i, yMax);
		initListener();
	}

	private void initListener() {
		// http://stackoverflow.com/questions/518471/jslider-question-position-after-leftclick
		// http://stackoverflow.com/a/936725/1250278
		for (MouseListener li: getMouseListeners()) {
			removeMouseListener(li);
		}
		final BasicSliderUI ui = (BasicSliderUI) getUI();
		MouseListener li = ui.new TrackListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				followCursor(e);
			}
			@Override
			public boolean shouldScroll(int direction) {
				return false;
			}

			private void followCursor(MouseEvent e) {
				Point p = e.getPoint();
				setValue(ui.valueForXPosition(p.x));
			}
		};
		addMouseListener(li);

	}

}
