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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.Messages;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;

/**
 * The world selection dialog box. It displays a list of worlds
 * and a browse button for loading a world outside the standard
 * save directory.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings("serial")
public class WorldSelector extends JDialog {

	private final List<World> worlds = new ArrayList<World>();
	private final Chunky chunky;
	private final DefaultTableModel tableModel;
	private final JTable worldTbl;

	/**
	 * Constructor
	 * @param chunky
	 */
	public WorldSelector(Chunky chunky) {
		super(chunky.getFrame());
		this.chunky = chunky;

		setTitle("Select World");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.MODELESS);

		tableModel = new DefaultTableModel(0, 3);
		tableModel.setColumnIdentifiers(new String[] { "World Name", "Directory", "Mode", "Seed" });
		worldTbl = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 3;
			}
		};

		initComponents();
		fillWorldList();

		pack();
		setLocationRelativeTo(chunky.getFrame());
		setVisible(true);
	}

	private void fillWorldList() {
		fillWorldList(WorldDirectoryPicker.getWorldDirectory(chunky.getFrame()));
	}

	private void fillWorldList(File worldSavesDir) {

		tableModel.setRowCount(0);

		worlds.clear();
		if (worldSavesDir != null) {
			for (File dir: worldSavesDir.listFiles()) {
				if (World.isWorldDir(dir)) {
					worlds.add(new World(dir, false));
				}
			}
		}
		Collections.sort(worlds);
		for (World world : worlds) {
			Object[] row = {
					world.levelName(),
					world.getWorldDirectory().getName(),
					world.gameMode(),
					world.getSeed() };
			tableModel.addRow(row);
		}

		pack();
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

		JLabel selectWorldLbl = new JLabel();
		JButton selectWorldDirBtn = new JButton("Change World Directory");
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

		final JButton loadSelectedBtn = new JButton("Load Selected World");
		loadSelectedBtn.setIcon(Icon.load.imageIcon());
		loadSelectedBtn.setEnabled(false);
		loadSelectedBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectWorld(worldTbl.getSelectedRow());
			}
		});

		worldTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		worldTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable source = (JTable) e.getSource();
					selectWorld(source.getSelectedRow());
				}
			}
		});
		worldTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				loadSelectedBtn.setEnabled(true);
			}
		});
		JScrollPane scrollPane = new JScrollPane(worldTbl);

		selectWorldLbl.setText("Select a world to load:");

		browseBtn.setText("Browse for Specific World");
		browseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(MinecraftFinder.getSavesDirectory());
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
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(selectWorldLbl)
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(selectWorldDirBtn)
					)
					.addGroup(layout.createSequentialGroup()
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(browseBtn)
					)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
					.addComponent(loadSelectedBtn, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				)
				.addContainerGap()
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(selectWorldLbl)
					.addComponent(selectWorldDirBtn)
				)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(browseBtn)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(loadSelectedBtn, GroupLayout.DEFAULT_SIZE, 40, 40)
				.addContainerGap()
			)
		);

		pack();
	}

	protected void selectWorld(int selected) {
		if (selected >= 0 && selected < worlds.size()) {
			chunky.loadWorld(worlds.get(selected));
			closeDialog();
		}
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}
}
