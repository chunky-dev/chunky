/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import se.llbit.chunky.renderer.scene.SceneManager;

/**
 * Filters illegal scene name characters
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SceneNameFilter extends DocumentFilter {
	@Override
	public void insertString(FilterBypass fb, int offset, String string,
			javax.swing.text.AttributeSet attr) throws BadLocationException {
		String name = stripIllegalChars(string);
		if (!name.isEmpty()) {
			fb.insertString(offset, name, attr);
		}
	};

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text,
			javax.swing.text.AttributeSet attrs) throws BadLocationException {
		String name = stripIllegalChars(text);
		if (!name.isEmpty()) {
			fb.replace(offset, length, name, attrs);
		}
	};

	private String stripIllegalChars(String string) {
		StringBuilder sb = new StringBuilder();
		if (string != null) {
			for (int i = 0; i < string.length(); ++i) {
				char c = string.charAt(i);
				if (SceneManager.isValidSceneNameChar(string.charAt(i))) {
					sb.append(c);
				}
			}
		}
		return sb.toString().trim();
	}
}
