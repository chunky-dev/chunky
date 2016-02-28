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
package se.llbit.chunky.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
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
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.world.Icon;
import se.llbit.log.Log;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class WorldDirectoryPicker extends JDialog {

	private final JLabel warning = new JLabel("Can't use selected directory.");
	private final JButton okBtn = new JButton("OK");
	private boolean accepted = false;
	private File selectedDirectory;
	private final JTextField pathField = new JTextField(40);

	/**
	 * Constructor
	 * @param parent
	 */
	public WorldDirectoryPicker(JFrame parent) {
		super(parent, "World Directory Picker");

		setModalityType(ModalityType.APPLICATION_MODAL);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		warning.setIcon(Icon.failed.imageIcon());

		JLabel lbl = new JLabel("Please select the directory where your Minecraft worlds are stored:");

		File initialDirectory = PersistentSettings.getLastWorld();
		if (initialDirectory != null && initialDirectory.isDirectory()) {
			initialDirectory = initialDirectory.getParentFile();
		}

		if (!isValidSelection(initialDirectory)) {
			initialDirectory = MinecraftFinder.getSavesDirectory();
		}

		updatePathField(initialDirectory);
		updateSelectedDirectory(initialDirectory);

		JButton defaultBtn = new JButton("Default");
		defaultBtn.setToolTipText("Select the default world directory");
		defaultBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File worldDir = MinecraftFinder.getSavesDirectory();
				if (!isValidSelection(worldDir)) {
					Log.warn("Could not find Minecraft installation!");
				} else {
					pathField.setText(worldDir.getAbsolutePath());
				}
			}
		});

		JButton browseBtn = new JButton("Browse...");
		browseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(getSelectedDirectory());
				fileChooser.setDialogTitle("Select Scene Directory");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedDirectory = fileChooser.getSelectedFile();
					updateSelectedDirectory(selectedDirectory);
					updatePathField(selectedDirectory);
				}
			}
		});

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isValidSelection(getSelectedDirectory())) {
					Log.warn("Please select a valid directory!");
				} else {
					accepted = true;
					WorldDirectoryPicker.this.dispose();
				}
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
		getRootPane().setDefaultButton(okBtn);

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
						.addComponent(defaultBtn)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(browseBtn))
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
					.addComponent(defaultBtn)
					.addComponent(browseBtn))
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
		boolean valid = path.isDirectory();
		if (warning.isVisible() != !valid) {
			warning.setVisible(!valid);
		}
		if (okBtn.isEnabled() != valid) {
			okBtn.setEnabled(valid);
		}
	}

	protected void updatePathField(File path) {
		pathField.setText(path.getAbsolutePath());
	}

	protected void closeDialog() {
		dispose();
	}

	/**
	 * @return The selected world saves directory
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
	 * Ask user for the Minecraft world saves directory.
	 * @param parent
	 * @return The selected world directory, or <code>null</code> if the
	 * user did not pick a valid world directory.
	 */
	public static File getWorldDirectory(JFrame parent) {
		File worldDir = PersistentSettings.getLastWorld();
		if (worldDir == null || !worldDir.isDirectory()) {
			worldDir = MinecraftFinder.getSavesDirectory();
		} else {
			worldDir = worldDir.getParentFile();
		}

		if (isValidSelection(worldDir)) {
			return worldDir;
		} else {
			return null;
		}
	}

	private static boolean isValidSelection(File worldDir) {
		return worldDir != null && worldDir.exists() && worldDir.isDirectory();
	}

}
