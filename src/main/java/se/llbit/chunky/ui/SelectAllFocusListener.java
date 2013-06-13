/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A JTextField listener used to automatically select all text.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SelectAllFocusListener implements FocusListener {

	@Override
	public void focusGained(FocusEvent e) {
		final JTextField source = (JTextField) e.getSource();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				source.selectAll();
			}
		});
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

}
