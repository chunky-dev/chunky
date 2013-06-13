/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

/**
 * A sublcass of FileDialog that centers the dialog.
 */
@SuppressWarnings("serial")
public class CenteredFileDialog extends FileDialog {

	/**
	 * @param parent
	 * @param title
	 * @param mode
	 */
	public CenteredFileDialog(Frame parent, String title, int mode) {
		super(parent, title, mode);

		setLocationRelativeTo(parent);
	}

	/**
	 * @return The selected File
	 */
	public File getSelectedFile() {
		String fileName = getFile();
		if (fileName != null) {
			if (getDirectory() != null)
				return new File(getDirectory(), fileName);
			else
				return new File(fileName);
		} else {
			return null;
		}
	}

	/**
	 * @param extension
	 * @return Enforce an extension for the selected file
	 */
	public File getSelectedFile(String extension) {
		String fileName = getFile();
		if (fileName != null) {
			if (!fileName.endsWith(extension))
				fileName = fileName + extension;
			if (getDirectory() != null)
				return new File(getDirectory(), fileName);
			else
				return new File(fileName);
		} else {
			return null;
		}
	}

}
