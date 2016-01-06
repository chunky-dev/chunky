/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.ui.tabs;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.renderer.ui.SceneSelector;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.Icon;
import se.llbit.log.Log;
import se.llbit.ui.Adjuster;
import se.llbit.ui.ErrorLabel;

public class GeneralTab extends RenderControlsTab {
	private static final long serialVersionUID = -1L;

	private static final int[] dumpFrequencies = { 50, 100, 500, 1000, 2500, 5000 };

	private final JComboBox canvasSizeCB = new JComboBox();
	private final JButton loadSceneBtn = new JButton();
	private final JButton openSceneDirBtn = new JButton();
	private final JCheckBox loadPlayersCB = new JCheckBox();
	private final JCheckBox biomeColorsCB = new JCheckBox();
	private final JCheckBox saveDumpsCB = new JCheckBox();
	private final JComboBox dumpFrequencyCB = new JComboBox();
	private final JCheckBox saveSnapshotsCB = new JCheckBox("Save snapshot for each dump");
	private final JLabel dumpFrequencyLbl = new JLabel(" frames");

	private final Adjuster yCutoff = new Adjuster(
			"Y cutoff",
			"Blocks below the Y cutoff are not loaded",
			0, Chunk.Y_MAX) {
		public ErrorLabel advisory;

		@Override
		public void setUp() {
			super.setUp();
			advisory = new ErrorLabel(getField());
			advisory.setVisible(false);
		}
		@Override
		public void valueChanged(double newValue) {
			int value = (int) newValue;
			PersistentSettings.setYCutoff(value);
			advisory.setText("This takes effect after the next time chunks are reloaded!");
			advisory.setVisible(true);
		}
		@Override
		public void update() {
			set(PersistentSettings.getYCutoff());
		}
	};

	private final ActionListener canvasSizeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String size = (String) canvasSizeCB.getSelectedItem();
			try {
				Pattern regex = Pattern.compile("([0-9]+)[xX.*]([0-9]+)");
				Matcher matcher = regex.matcher(size);
				if (matcher.matches()) {
					int width = Integer.parseInt(matcher.group(1));
					int height = Integer.parseInt(matcher.group(2));
					setCanvasSize(width, height);
				} else {
					Log.info("Failed to set canvas size: format must be WIDTHxHEIGHT!");
				}
			} catch (NumberFormatException e1) {
				Log.info("Failed to set canvas size: invalid dimensions!");
			}
		}
	};

	private final ActionListener loadSceneListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			new SceneSelector(renderControls, context);
		}
	};

	private final ActionListener loadPlayersCBListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			PersistentSettings.setLoadPlayers(source.isSelected());
		}
	};

	private final ActionListener biomeColorsCBListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setBiomeColorsEnabled(source.isSelected());
		}
	};

	private final ActionListener dumpFrequencyListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				renderMan.scene().setDumpFrequency(getDumpFrequency());
			} catch (NumberFormatException e1) {
			}
			updateDumpFrequencyField();
		}
	};

	private final ActionListener saveDumpsListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean enabled = saveDumpsCB.isSelected();
			if (enabled) {
				renderMan.scene().setDumpFrequency(getDumpFrequency());
			} else {
				renderMan.scene().setDumpFrequency(0);
			}
			dumpFrequencyCB.setEnabled(enabled);
			saveSnapshotsCB.setEnabled(enabled);
		}
	};

	private final ActionListener saveSnapshotListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setSaveSnapshots(source.isSelected());
		}
	};

	public GeneralTab(final RenderControls renderControls) {
		super(renderControls);

		JLabel canvasSizeLbl = new JLabel("Canvas size:");
		canvasSizeLbl.setIcon(Icon.scale.imageIcon());

		canvasSizeCB.setEditable(true);
		canvasSizeCB.addItem("400x400");
		canvasSizeCB.addItem("1024x768");
		canvasSizeCB.addItem("960x540");
		canvasSizeCB.addItem("1920x1080");
		canvasSizeCB.addActionListener(canvasSizeListener);
		final JTextField canvasSizeEditor = (JTextField) canvasSizeCB.getEditor().getEditorComponent();
		canvasSizeEditor.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			@Override
			public void focusGained(FocusEvent e) {
				canvasSizeEditor.selectAll();
			}
		});

		loadSceneBtn.setText("Load Scene");
		loadSceneBtn.setIcon(Icon.load.imageIcon());
		loadSceneBtn.addActionListener(loadSceneListener);

		JButton loadSelectedChunksBtn = new JButton("Load Selected Chunks");
		loadSelectedChunksBtn.setToolTipText("Load the chunks that are currently selected in the map view");
		loadSelectedChunksBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderControls.getSceneManager().loadChunks(renderControls.getChunky().getWorld(),
						renderControls.getChunky().getSelectedChunks());
			}
		});

		JButton reloadChunksBtn = new JButton("Reload Chunks");
		reloadChunksBtn.setIcon(Icon.reload.imageIcon());
		reloadChunksBtn.setToolTipText("Reload all chunks in the scene");
		reloadChunksBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderControls.getSceneManager().reloadChunks();
			}
		});

		openSceneDirBtn.setText("Open Scene Directory");
		openSceneDirBtn.setToolTipText("Open the directory where Chunky stores scene descriptions and renders");
		openSceneDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(context.getSceneDirectory());
					}
				} catch (IOException e) {
					Log.warn("Failed to open scene directory", e);
				}
			}
		});
		openSceneDirBtn.setVisible(Desktop.isDesktopSupported());

		loadSceneBtn.setToolTipText("This replaces the current scene!");
		JButton setCanvasSizeBtn = new JButton("Apply");
		setCanvasSizeBtn.setToolTipText("Set the canvas size to the value in the field");
		setCanvasSizeBtn.addActionListener(canvasSizeListener);

		JButton halveCanvasSizeBtn = new JButton("Halve");
		halveCanvasSizeBtn.setToolTipText("Halve the canvas width and height");
		halveCanvasSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int width = renderMan.scene().canvasWidth() / 2;
				int height = renderMan.scene().canvasHeight() / 2;
				setCanvasSize(width, height);
			}
		});
		JButton doubleCanvasSizeBtn = new JButton("Double");
		doubleCanvasSizeBtn.setToolTipText("Double the canvas width and height");
		doubleCanvasSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int width = renderMan.scene().canvasWidth() * 2;
				int height = renderMan.scene().canvasHeight() * 2;
				setCanvasSize(width, height);
			}
		});

		JButton makeDefaultBtn = new JButton("Make Default");
		makeDefaultBtn.setToolTipText("Make the current canvas size the default");
		makeDefaultBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PersistentSettings.set3DCanvasSize(
						renderMan.scene().canvasWidth(),
						renderMan.scene().canvasHeight());
			}
		});

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();

		loadPlayersCB.setText("Load players");
		loadPlayersCB.setToolTipText("Enable/disable player entity loading. "
				+ "Reload the chunks after changing this option.");

		biomeColorsCB.setText("Enable biome colors");

		saveDumpsCB.setText("Save dump once every ");
		saveDumpsCB.addActionListener(saveDumpsListener);

		String[] frequencyStrings = new String[dumpFrequencies.length];
		for (int i = 0; i < dumpFrequencies.length; ++i) {
			frequencyStrings[i] = Integer.toString(dumpFrequencies[i]);
		}
		dumpFrequencyCB.setModel(new DefaultComboBoxModel(frequencyStrings));
		dumpFrequencyCB.setEditable(true);
		dumpFrequencyCB.addActionListener(dumpFrequencyListener);

		saveSnapshotsCB.addActionListener(saveSnapshotListener);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(loadSceneBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(openSceneDirBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(loadSelectedChunksBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(reloadChunksBtn)
				)
				.addComponent(sep1)
				.addGroup(layout.createSequentialGroup()
					.addComponent(canvasSizeLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(canvasSizeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(setCanvasSizeBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(makeDefaultBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(halveCanvasSizeBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(doubleCanvasSizeBtn)
				)
				.addComponent(sep2)
				.addComponent(loadPlayersCB)
				.addComponent(biomeColorsCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(saveDumpsCB)
					.addComponent(dumpFrequencyCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(dumpFrequencyLbl)
					.addGap(0, 0, Short.MAX_VALUE)
				)
				.addComponent(saveSnapshotsCB)
				.addGroup(yCutoff.horizontalGroup(layout))
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(loadSceneBtn)
				.addComponent(openSceneDirBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(loadSelectedChunksBtn)
				.addComponent(reloadChunksBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(canvasSizeLbl)
				.addComponent(canvasSizeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(setCanvasSizeBtn)
				.addComponent(makeDefaultBtn)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(halveCanvasSizeBtn)
				.addComponent(doubleCanvasSizeBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(loadPlayersCB)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(biomeColorsCB)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(saveDumpsCB)
				.addComponent(dumpFrequencyCB)
				.addComponent(dumpFrequencyLbl)
			)
			.addComponent(saveSnapshotsCB)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(yCutoff.verticalGroup(layout))
			.addContainerGap()
		);
	}

	@Override
	public void refreshSettings() {
		updateCanvasSizeField();
		updateLoadPlayersCB();
		updateBiomeColorsCB();
		updateSaveDumpsCheckBox();
		updateSaveSnapshotCheckBox();
		updateDumpFrequencyField();
	}

	protected void updateCanvasSizeField() {
		canvasSizeCB.removeActionListener(canvasSizeListener);
		canvasSizeCB.setSelectedItem("" + renderMan.scene().canvasWidth() +
				"x" + renderMan.scene().canvasHeight());
		canvasSizeCB.addActionListener(canvasSizeListener);
	}

	protected void setCanvasSize(int width, int height) {
		renderMan.scene().setCanvasSize(width, height);
		int canvasWidth = renderMan.scene().canvasWidth();
		int canvasHeight = renderMan.scene().canvasHeight();
		canvasSizeCB.setSelectedItem("" + canvasWidth + "x" + canvasHeight);
		renderControls.getView().setCanvasSize(canvasWidth, canvasHeight);
	}

	protected void updateLoadPlayersCB() {
		loadPlayersCB.removeActionListener(loadPlayersCBListener);
		loadPlayersCB.setSelected(PersistentSettings.getLoadPlayers());
		loadPlayersCB.addActionListener(loadPlayersCBListener);
	}

	protected void updateBiomeColorsCB() {
		biomeColorsCB.removeActionListener(biomeColorsCBListener);
		biomeColorsCB.setSelected(renderMan.scene().biomeColorsEnabled());
		biomeColorsCB.addActionListener(biomeColorsCBListener);
	}

	protected void updateSaveDumpsCheckBox() {
		saveDumpsCB.removeActionListener(saveDumpsListener);
		saveDumpsCB.setSelected(renderMan.scene().shouldSaveDumps());
		saveDumpsCB.addActionListener(saveDumpsListener);
	}

	protected void updateDumpFrequencyField() {
		dumpFrequencyCB.removeActionListener(dumpFrequencyListener);
		try {
			dumpFrequencyCB.setEnabled(renderMan.scene().shouldSaveDumps());
			saveSnapshotsCB.setEnabled(renderMan.scene().shouldSaveDumps());
			int frequency = renderMan.scene().getDumpFrequency();
			for (int i = 0; i < dumpFrequencies.length; ++i) {
				if (frequency == dumpFrequencies[i]) {
					dumpFrequencyCB.setSelectedIndex(i);
					return;
				}
			}
			dumpFrequencyCB.setSelectedItem(Integer.toString(frequency));
		} finally {
			dumpFrequencyCB.addActionListener(dumpFrequencyListener);
		}
	}

	protected void updateSaveSnapshotCheckBox() {
		saveSnapshotsCB.removeActionListener(saveSnapshotListener);
		try {
			saveSnapshotsCB.setSelected(renderMan.scene().shouldSaveSnapshots());
		} finally {
			saveSnapshotsCB.addActionListener(saveSnapshotListener);
		}
	}

	protected int getDumpFrequency() {
		int index = dumpFrequencyCB.getSelectedIndex();
		if (index != -1) {
			index = Math.max(0, index);
			index = Math.min(dumpFrequencies.length-1, index);
			return dumpFrequencies[index];
		} else {
			try {
				return Integer.valueOf((String) dumpFrequencyCB.getSelectedItem());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}
}
