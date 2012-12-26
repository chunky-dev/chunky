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
package se.llbit.chunky.renderer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import se.llbit.util.ProgramProperties;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class SceneDirectoryPicker extends JDialog {
	
	private static final Logger logger =
			Logger.getLogger(SceneDirectoryPicker.class);
	
	private File selectedDirectory;
	private boolean accepted = false;
	
	/**
	 * Constructor
	 * @param parent 
	 */
	public SceneDirectoryPicker(JFrame parent) {
		super(parent, "Scene Directory Picker");
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		JLabel lbl = new JLabel("Please select a directory where Chunky should store scene description files and renders:");
		
		final JTextField scenePath = new JTextField(40);
		selectedDirectory = ProgramProperties.getPreferredSceneDirectory();
		scenePath.setText(selectedDirectory.getAbsolutePath());
		
		final JCheckBox nopester =
				new JCheckBox("Use this as the default and do not ask again");
		
		JButton browseBtn = new JButton("Browse...");
		browseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(selectedDirectory);
				fileChooser.setDialogTitle("Select Scene Directory");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedDirectory = fileChooser.getSelectedFile();
					scenePath.setText(selectedDirectory.getAbsolutePath());
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
		
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedDirectory = new File(scenePath.getText());
				if (nopester.isSelected()) {
					ProgramProperties.setProperty("sceneDirectory",
							selectedDirectory.getAbsolutePath());
					ProgramProperties.saveProperties();
				}
				accepted = true;
				SceneDirectoryPicker.this.dispose();
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
						.addComponent(scenePath)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(browseBtn))
					.addComponent(nopester)
					.addGroup(layout.createSequentialGroup()
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
					.addComponent(scenePath)
					.addComponent(browseBtn))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(nopester)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(okBtn)
					.addComponent(cancelBtn))
				.addContainerGap())
		);
		setContentPane(panel);
		pack();
		
		setLocationRelativeTo(parent);
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
     * Ask user for the directory to place scene descriptions and
     * render dumps in.
     * @param parent
     * @return The selected scene directory
     */
    public static File getSceneDirectory(JFrame parent) {
    	File sceneDir;
    	if (ProgramProperties.containsKey("sceneDirectory")) {
    		sceneDir = new File(ProgramProperties.getProperty("sceneDirectory"));
    	} else {
    		// ask user for scene directory
    		SceneDirectoryPicker sceneDirPicker =
    				new SceneDirectoryPicker(parent);
    		sceneDirPicker.setVisible(true);
    		if (!sceneDirPicker.isAccepted())
    			return null;
    		sceneDir = sceneDirPicker.getSelectedDirectory();
    	}
    	
    	while (true) {
	    	if (!sceneDir.exists())
	    		sceneDir.mkdir();
	    	if (sceneDir.exists() && sceneDir.isDirectory() && sceneDir.canWrite()) {
	    		return sceneDir;
	    	} else {
	    		logger.warn("Could not open or create selected Scene directory");
	    		SceneDirectoryPicker sceneDirPicker =
	    				new SceneDirectoryPicker(parent);
	    		sceneDirPicker.setVisible(true);
	    		if (!sceneDirPicker.isAccepted())
	    			return null;
	    		sceneDir = sceneDirPicker.getSelectedDirectory();
	    	}
    	}
	}

}
