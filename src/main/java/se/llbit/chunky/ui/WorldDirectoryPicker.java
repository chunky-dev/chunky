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
package se.llbit.chunky.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import se.llbit.chunky.main.Chunky;
import se.llbit.util.ProgramProperties;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class WorldDirectoryPicker extends JDialog {
	
	private static final Logger logger =
			Logger.getLogger(WorldDirectoryPicker.class);
	
	private boolean accepted = false;
	
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
		
		JLabel lbl = new JLabel("Please select the directory where your Minecraft worlds are stored:");
		
		File initialDirectory = null;
		if (ProgramProperties.containsKey("worldDirectory")) {
			initialDirectory = new File(ProgramProperties.getProperty("worldDirectory"));
		}
		
		if (!isValidSelection(initialDirectory)) {
			initialDirectory = Chunky.getSavesDirectory();
		}
		pathField.setText(initialDirectory.getAbsolutePath());
		
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
					pathField.setText(selectedDirectory.getAbsolutePath());
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
		
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isValidSelection(getSelectedDirectory())) {
					logger.warn("Please select a valid directory!");
				} else {
					ProgramProperties.setProperty("worldDirectory",
							getSelectedDirectory().getAbsolutePath());
					ProgramProperties.saveProperties();
					accepted = true;
					WorldDirectoryPicker.this.dispose();
				}
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
						.addComponent(browseBtn))
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
					.addComponent(pathField)
					.addComponent(browseBtn))
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

	protected void closeDialog() {
		dispose();
	}

	/**
	 * @return The selected world saves directory
	 */
	public File getSelectedDirectory() {
		return new File(pathField.getText());
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
    	File worldDir;
    	if (ProgramProperties.containsKey("worldDirectory")) {
    		worldDir = new File(ProgramProperties.getProperty("worldDirectory"));
    	} else {
			worldDir = Chunky.getSavesDirectory();
    	}
    	
    	if (!isValidSelection(worldDir)) {
    		WorldDirectoryPicker sceneDirPicker =
    				new WorldDirectoryPicker(parent);
    		sceneDirPicker.setVisible(true);
    		if (!sceneDirPicker.isAccepted())
    			return null;
    		worldDir = sceneDirPicker.getSelectedDirectory();
    	}
    	
    	if (isValidSelection(worldDir))
    		return worldDir;
    	else
    		return null;
	}
    
    private static boolean isValidSelection(File worldDir) {
    	return worldDir != null && worldDir.exists() && worldDir.isDirectory();
    }

}
