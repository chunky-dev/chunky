/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import org.apache.log4j.Logger;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.Messages;
import se.llbit.chunky.world.World;

/**
 * The world selection dialog box. It displays a list of worlds
 * and a browse button for loading a world outside the standard
 * save directory.
 * 
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings("serial")
public class WorldSelector extends JDialog implements ListSelectionListener {

	@SuppressWarnings("unused")
	private static final Logger logger =
			Logger.getLogger(WorldSelector.class);
	
	private Chunky chunky;
	private DefaultListModel listModel;
	private JList worldList;

	/**
	 * Constructor 
	 * @param chunky
	 */
	public WorldSelector(Chunky chunky) {
		super(chunky.getFrame());
		this.chunky = chunky;
		
		initComponents();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.MODELESS);
		
		setTitle(Messages.getString("WorldSelector.0")); //$NON-NLS-1$
		pack();
		setLocationRelativeTo(chunky.getFrame());
		setVisible(true);
		
		fillWorldList();
	}
	
	private void fillWorldList() {
		fillWorldList(WorldDirectoryPicker.getWorldDirectory(chunky.getFrame()));
	}

	private void fillWorldList(File worldSavesDir) {
		listModel.clear();
		File[] worldDirs = null;
		if (worldSavesDir != null) {
			worldDirs = worldSavesDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory();
				}
			});
		}
		if (worldDirs != null) {
			for (File worldDir : worldDirs) {
				if (World.isWorldDir(worldDir)) {
					listModel.addElement(new World(worldDir, false));
				}
			}
		}
		pack();
	}

	private void initComponents() {

		JLabel selectWorldLbl = new JLabel();
		JButton selectWorldDirBtn = new JButton("Select World Directory");
		selectWorldDirBtn.setToolTipText("Select the directory where your Minecraft worlds are saved");
		selectWorldDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				WorldDirectoryPicker dialog = new WorldDirectoryPicker(chunky.getFrame());
				dialog.setVisible(true);
				if (dialog.isAccepted()) {
					fillWorldList(dialog.getSelectedDirectory());
				}
			}
		});
		JButton browseBtn = new JButton();
		JSeparator sep1 = new JSeparator();

		listModel = new DefaultListModel();
		worldList = new JList(listModel);
		worldList.setVisibleRowCount(15);
		worldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		worldList.addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(worldList);
		
		selectWorldLbl.setText(Messages.getString("WorldSelector.1")); //$NON-NLS-1$

		browseBtn.setText(Messages.getString("WorldSelector.2")); //$NON-NLS-1$
		browseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(Chunky.getSavesDirectory());
				fileChooser.setDialogTitle(Messages.getString("WorldSelector.0")); //$NON-NLS-1$
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					chunky.loadWorld(new World(fileChooser.getSelectedFile(), false));
					WorldSelector.this.dispose();
				}
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(selectWorldLbl, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
					.addComponent(browseBtn, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
					.addComponent(sep1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
					.addComponent(selectWorldDirBtn, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
				)
				.addContainerGap()
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(selectWorldLbl)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(sep1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(browseBtn)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(selectWorldDirBtn)
				.addContainerGap()
			)
		);

		pack();
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		int selected = event.getFirstIndex();
		World world = (World) listModel.get(selected);
		chunky.loadWorld(world);
		WorldSelector.this.dispose();
	}
}
