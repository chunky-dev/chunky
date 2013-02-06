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
package se.llbit.chunky.renderer.ui;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.llbit.chunky.renderer.RenderContext;

/**
 * The scene selector dialog.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class SceneSelector extends JDialog implements ListSelectionListener {

	private DefaultListModel listModel;
	private JList sceneList;
	private RenderControls controls;
	protected boolean sceneLoaded = false;
	private final RenderContext context;
	private boolean accepted = false;
	private String selectedScene;

	/**
	 * Creates a new scene selector dialog.
	 * If the controls parameter is <code>null</code> the dialog
	 * will be application modal.
	 * @param controls 
	 * @param context 
	 */
	public SceneSelector(RenderControls controls, RenderContext context) {
		super(controls, "Load Scene");
		
		this.controls = controls;
		this.context = context;
		
		initComponents();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		if (controls != null) {
			setModalityType(Dialog.ModalityType.MODELESS);
		} else {
			setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		}
		
		pack();
		setLocationRelativeTo(controls);
		setVisible(true);
		requestFocus();
	}

	private void initComponents() {

		JLabel listDescription = new JLabel();
		JButton cancelBtn = new JButton("Cancel");

		listModel = new DefaultListModel();
		sceneList = new JList(listModel);
		sceneList.setVisibleRowCount(15);
		sceneList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sceneList.addListSelectionListener(this);
		File[] sceneFiles = context.getSceneDirectory()
				.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".cvf");
			}
		});
		List<File> fileList = new ArrayList<File>(sceneFiles.length);
		for (File file: sceneFiles)
			fileList.add(file);
		Collections.sort(fileList);
		for (File sceneFile : fileList) {
			String name = sceneFile.getName();
			listModel.addElement(name.substring(0, name.length()-4));
		}
		JScrollPane scrollPane = new JScrollPane(sceneList);
		
		listDescription.setText("Select 3D scene:");

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SceneSelector.this.dispose();
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(listDescription, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
					.addComponent(cancelBtn, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(listDescription)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(cancelBtn)
				.addContainerGap())
		);

		pack();
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		int selected = event.getFirstIndex();
		selectedScene = (String) listModel.get(selected);
		accepted = true;
		dispose();
		if (controls != null) {
			controls.loadScene(selectedScene);
		}
	}

	/**
	 * @return <code>true</code> if a scene was selected
	 */
	public boolean isAccepted() {
		return accepted ;
	}

	/**
	 * @return The selected scene name
	 */
	public String getSelectedScene() {
		return selectedScene;
	}
}
