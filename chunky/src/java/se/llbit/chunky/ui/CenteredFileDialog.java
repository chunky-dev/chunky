/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import javax.swing.JOptionPane;

import se.llbit.chunky.main.Messages;

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
			File selectedFile;
			boolean extensionAdded = false;
			if (getMode() == FileDialog.SAVE && fileName.lastIndexOf('.') == -1) {
				fileName = fileName + extension;
				extensionAdded = true;
			}
			if (getDirectory() != null) {
				selectedFile =  new File(getDirectory(), fileName);
			} else {
				selectedFile =  new File(fileName);
			}

			if (extensionAdded && selectedFile.exists()) {
				// confirm overwrite of file (extension added - FileDialog checks for regular overwrite)
				Object[] options = {
						Messages.getString("Chunky.Cancel_lbl"), //$NON-NLS-1$
						Messages.getString("Chunky.AcceptOverwrite_lbl")}; //$NON-NLS-1$
				int n = JOptionPane.showOptionDialog(null,
						String.format(Messages.getString("Chunky.Confirm_overwrite_msg"), //$NON-NLS-1$
								selectedFile.getName()),
						Messages.getString("Chunky.Confirm_overwrite_title"), //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options[0]);
				if (n != 1) {
					return null;
				}
			}
			return selectedFile;
		} else {
			return null;
		}
	}

}
