/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.world.Icon;
import se.llbit.log.Log;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class SceneDirectoryPicker extends JDialog {

	private final JLabel warning = new JLabel("Selected directory does not exist.");
	private final JCheckBox mkdirs = new JCheckBox("Create this directory");
	private final JButton okBtn = new JButton("OK");
	private final JTextField pathField = new JTextField(40);
	private File selectedDirectory;
	private boolean accepted = false;

	/**
	 * Constructor
	 * @param parent
	 */
	public SceneDirectoryPicker(JFrame parent) {
		super(parent, "Scene Directory Picker");

		setModalityType(ModalityType.APPLICATION_MODAL);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		JLabel lbl = new JLabel("Please select a directory where Chunky should store "
				+ "scene description files and renders:");

		warning.setIcon(Icon.failed.imageIcon());

		mkdirs.setSelected(true);
		mkdirs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSelectedDirectory(selectedDirectory);
			}
		});

		updatePathField(PersistentSettings.getSceneDirectory());
		updateSelectedDirectory(PersistentSettings.getSceneDirectory());

		JButton browseBtn = new JButton("Browse...");
		browseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(selectedDirectory);
				fileChooser.setDialogTitle("Select Scene Directory");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					updateSelectedDirectory(selectedFile);
					updatePathField(selectedFile);
				}
			}
		});

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SceneDirectoryPicker.this.dispose();
			}
		});

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (mkdirs.isSelected()) {
					tryCreateSceneDir(selectedDirectory);
				}
				accepted = true;
				SceneDirectoryPicker.this.dispose();
			}
		});

		pathField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			void update() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateSelectedDirectory(new File(pathField.getText()));
					}
				});
			}
		});


		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(lbl)
					.addGroup(layout.createSequentialGroup()
						.addComponent(pathField)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(browseBtn))
					.addComponent(mkdirs)
					.addGroup(layout.createSequentialGroup()
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(warning)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(okBtn)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cancelBtn)))
				.addContainerGap()
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(lbl)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(pathField)
					.addComponent(browseBtn))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(mkdirs)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(warning)
					.addComponent(okBtn)
					.addComponent(cancelBtn))
				.addContainerGap())
		);
		setContentPane(panel);
		pack();

		setLocationRelativeTo(parent);
	}

	protected void updateSelectedDirectory(File path) {
		selectedDirectory = path;
		boolean valid = mkdirs.isSelected() || isValidDirectory(path);
		if (warning.isVisible() != !valid) {
			warning.setVisible(!valid);
		}
		if (okBtn.isEnabled() != valid) {
			okBtn.setEnabled(valid);
		}
	}

	private static boolean isValidDirectory(File path) {
		return path.isDirectory() && path.canWrite();
	}

	protected void updatePathField(File path) {
		pathField.setText(path.getAbsolutePath());
	}

	protected void closeDialog() {
		dispose();
	}

	/**
	 * @return The selected scene directory
	 */
	public File getSelectedDirectory() {
		return selectedDirectory;
	}

	/**
	 * @return <code>true</code> if the OK button was clicked
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * @return A reference to a file object representing the current scene directory,
	 * or <code>null</code> if the scene directory was not set or could not be opened
	 */
	public static File getCurrentSceneDirectory() {
		if (PersistentSettings.containsKey("sceneDirectory")) {
			File sceneDir = PersistentSettings.getSceneDirectory();
			if (tryCreateSceneDir(sceneDir)) {
				return sceneDir;
			}
		}
		return null;
	}

	/**
	 * Ask user for the directory to place scene descriptions and
	 * render dumps in.
	 * @param parent
	 * @return The selected scene directory
	 */
	public static File getSceneDirectory(JFrame parent) {
		File sceneDir = getCurrentSceneDirectory();
		if (sceneDir != null) {
			return sceneDir;
		} else {
			return changeSceneDirectory(parent);
		}
	}

	/**
	 * Opens a dialog asking the user to specify a new scene directory
	 * @param parent
	 * @return The file representing the selected directory
	 */
	public static File changeSceneDirectory(JFrame parent) {
		while (true) {
			SceneDirectoryPicker sceneDirPicker = new SceneDirectoryPicker(parent);
			sceneDirPicker.setVisible(true);
			if (!sceneDirPicker.isAccepted()) {
				return null;
			}
			File sceneDir = sceneDirPicker.getSelectedDirectory();
			if (isValidDirectory(sceneDir)) {
				PersistentSettings.setSceneDirectory(sceneDir);
				return sceneDir;
			}
		}
	}

	private static boolean tryCreateSceneDir(File sceneDir) {
		if (!sceneDir.exists()) {
			sceneDir.mkdirs();
		}
		if (!isValidDirectory(sceneDir)) {
			Log.warningfmt("Could not open or create the scene directory %s",
					sceneDir.getAbsolutePath());
			return false;
		} else {
			return true;
		}
	}
}
