/* Copyright (c) 2010-2013 Jesper Öqvist <jesper@llbit.se>
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.scene.SceneDescription;

/**
 * The scene selector dialog.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class SceneSelector extends JDialog {

	private final DefaultTableModel tableModel;
	private final JTable sceneTable;
	private final RenderControls controls;
	private final List<SceneDescription> scenes = new ArrayList<SceneDescription>();
	protected boolean sceneLoaded = false;
	private final RenderContext context;
	private boolean accepted = false;
	private SceneDescription selectedScene;

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

		tableModel = new DefaultTableModel(0, 3);
		tableModel.setColumnIdentifiers(new String[] { "Name", "Size", "Current SPP", "Render Time" });
		sceneTable = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

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

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		JLabel listDescription = new JLabel();
		JButton cancelBtn = new JButton("Cancel");
		final JButton loadSelectedBtn = new JButton("Load Selected Scene");
		loadSelectedBtn.setEnabled(false);
		loadSelectedBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectScene(sceneTable.getSelectedRow());
			}
		});

		File[] sceneFiles = context.getSceneDirectory()
				.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(RenderContext.SCENE_DESCRIPTION_EXTENSION);
			}
		});
		List<File> fileList = new ArrayList<File>(sceneFiles.length);
		for (File file: sceneFiles) {
			fileList.add(file);
		}
		Collections.sort(fileList);
		for (File sceneFile : fileList) {
			String fileName = sceneFile.getName();
			try {
				SceneDescription desc = new SceneDescription();
				desc.loadDescription(context.getSceneFileInputStream(fileName));
				scenes.add(desc);
				int seconds = (int) ((desc.renderTime / 1000) % 60);
				int minutes = (int) ((desc.renderTime / 60000) % 60);
				int hours = (int) (desc.renderTime / 3600000);
				String renderTime = String.format("%d:%d:%d", hours, minutes, seconds);
				Object[] row = {
						desc.name,
						""+desc.width+"x"+desc.height,
						""+desc.spp, renderTime };
				tableModel.addRow(row);
			} catch (IOException e) {
			}
		}

		sceneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sceneTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable source = (JTable) e.getSource();
					selectScene(source.getSelectedRow());
				}
			}
		});
		sceneTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				loadSelectedBtn.setEnabled(true);
			}
		});
		JScrollPane scrollPane = new JScrollPane(sceneTable);

		listDescription.setText("Select 3D scene to load:");

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
				.addGroup(layout.createParallelGroup()
					.addComponent(listDescription, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
					.addComponent(cancelBtn, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
					.addComponent(loadSelectedBtn, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
				)
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(listDescription)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(loadSelectedBtn, GroupLayout.DEFAULT_SIZE, 40, 40)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(cancelBtn)
				.addContainerGap())
		);

		pack();
	}

	protected void selectScene(int selected) {
		if (selected >= 0 && selected < scenes.size()) {
			selectedScene = scenes.get(selected);
			accepted = true;
			setVisible(false);
			dispose();
			if (controls != null) {
				controls.loadScene(selectedScene.name);
			}
		}
	}

	/**
	 * @return <code>true</code> if a scene was selected
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * @return The selected scene name
	 */
	public String getSelectedScene() {
		return selectedScene.name;
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}
}
