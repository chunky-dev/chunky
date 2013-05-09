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
package se.llbit.chunky.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.main.BlockTypeListCellRenderer;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.Messages;
import se.llbit.chunky.renderer.ui.SceneDirectoryPicker;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.MiscImages;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.resources.TexturePackLoader.TextureLoadingError;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;

/**
 * This is the toolbox that's shown in the left part of the main Chunky GUI.
 *
 * It has a tabbed pane which currently contains three tabs; View, Edit, Highlight
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings("serial")
public class Controls extends JPanel {

	private static final Logger logger =
			Logger.getLogger(Controls.class);

	private static final int WIDTH_BIG = 300;

	private static final int RENDER_TAB_INDEX = 4;

	private final Chunky chunky;
	private JTextField scaleField;
	private JSlider scaleSlider;
	private JTextField layerField;
	private JSlider layerSlider;
	private JButton deleteChunksBtn;
	private final ImageIcon faceIcon;
	private final JButton clearSelectionBtn1 = new JButton();
	private final JButton clearSelectionBtn2 = new JButton();
	private ProgressPanel progressPanel;
	private final Minimap minimap;
	private JButton exportZipBtn;
	private JRadioButton overworldBtn;
	private JRadioButton netherBtn;
	private JRadioButton endBtn;
	private final JCheckBox followPlayer = new JCheckBox("follow player");

	private WorldSelector worldSelector = null;

	private JTextField xField;

	private JTextField zField;

	private JTabbedPane tabbedPane;

	/**
	 * @param chunky
	 * @param minimap
	 */
	public Controls(Chunky chunky, Minimap minimap) {
		this.chunky = chunky;
		this.minimap = minimap;

		faceIcon = new ImageIcon(MiscImages.face);

		initComponents();
	}

	/**
	 * Set up the Swing GUI.
	 */
	private void initComponents() {
		setMinimumSize(new Dimension(257, 500));

		tabbedPane = new JTabbedPane();

		addTab("Map View", Icon.map.imageIcon(), buildViewPanel(), "Change map view");
		addTab("Chunks", Icon.mapSelected.imageIcon(), buildEditPanel(), "Chunk operations");
		addTab("Highlight", Icon.redTorchOn.imageIcon(), buildHighlightPanel(), "Change block highlight settings");
		addTab("Options", Icon.wrench.imageIcon(), buildOptionsPanel(), "Configure Chunky");
		addTab("3D Render", Icon.sky.imageIcon(), buildRenderPanel(), "Render chunks in 3D");
		addTab("About", Texture.unknown.imageIcon(), buildAboutPanel(), "About Chunky");

		JButton selectWorldBtn = new JButton();
		selectWorldBtn.setText(Messages.getString("Controls.SelectWorld_lbl")); //$NON-NLS-1$
		selectWorldBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openWorldSelector();
			}
		});

		JButton reloadWorldBtn = new JButton("Reload");
		reloadWorldBtn.setIcon(Icon.reload.imageIcon());
		reloadWorldBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.reloadWorld();
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(minimap, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
						.addComponent(selectWorldBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG/2, Short.MAX_VALUE)
						.addComponent(reloadWorldBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG/2, Short.MAX_VALUE))
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(minimap, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGroup(layout.createParallelGroup()
				.addComponent(selectWorldBtn, GroupLayout.PREFERRED_SIZE, 33, 33)
				.addComponent(reloadWorldBtn, GroupLayout.PREFERRED_SIZE, 33, 33)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addContainerGap()
		);
	}

	/**
	 * Add a tab and ensure that the icon is to the left of the text in the
	 * tab label.
	 *
	 * @param title
	 * @param icon
	 * @param component
	 * @param tip
	 */
	private void addTab(String title, ImageIcon icon, Component component, String tip) {
		int index = tabbedPane.getTabCount();

		tabbedPane.add(component);

		JLabel lbl = new JLabel(title, icon, SwingConstants.RIGHT);
		lbl.setIconTextGap(5);

		tabbedPane.setTabComponentAt(index, lbl);
		tabbedPane.setToolTipTextAt(index, tip);
	}

	/**
	 * Build the highlight tab.
	 * This is where the user selects which block type to highlight
	 * and the color of the highlight.
	 * @return the highlight tab component
	 */
	private JComponent buildHighlightPanel() {

		JLabel highlightLbl = new JLabel(
				"<html>Highlights blocks of the selected type.<br>" +
				"Only blocks in the current layer are highlighted!");

		JComponent highlightPanel = new JPanel();

		JButton colorChooserBtn = new JButton();

		colorChooserBtn.setText("Change Color");
		colorChooserBtn.setToolTipText("Change the highlight color");
		colorChooserBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(chunky.getFrame(),
						"Choose Highlight Color", chunky.getHighlightColor());
				if (color != null)
					chunky.setHighlightColor(color);
			}
		});

		JCheckBox enableHighlight = new JCheckBox();
		enableHighlight.setText("Enable Highlight");
		enableHighlight.setSelected(chunky.isHighlightEnabled());
		enableHighlight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox s = (JCheckBox) e.getSource();
				chunky.setHighlightEnable(s.isSelected());
			}
		});

		Block[] highlightBlocks = new Block[] {
				Block.DIRT,
				Block.GRASS,
				Block.STONE,
				Block.COBBLESTONE,
				Block.MOSSSTONE,
				Block.IRONORE,
				Block.COALORE,
				Block.REDSTONEORE,
				Block.DIAMONDORE,
				Block.GOLDORE,
				Block.MONSTERSPAWNER,
				Block.BRICKS,
				Block.CLAY,
				Block.LAPISLAZULIORE,
				Block.EMERALDORE,
				Block.NETHERQUARTZORE
			};
		JComboBox blockTypeBox = new JComboBox(highlightBlocks);// BLAWK TYPE BAWKS
		blockTypeBox.setSelectedItem(chunky.getHighlightBlock());
		blockTypeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox btb = (JComboBox) e.getSource();
				chunky.highlightBlock((Block) btb.getSelectedItem());
			}
		});
		blockTypeBox.setRenderer(new BlockTypeListCellRenderer());

		GroupLayout layout = new GroupLayout(highlightPanel);
		highlightPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(highlightLbl)
				.addComponent(enableHighlight, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
				.addComponent(blockTypeBox, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
				.addComponent(colorChooserBtn))
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(highlightLbl)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(enableHighlight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(blockTypeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(colorChooserBtn)
			.addContainerGap(10, 10)
		);

		return highlightPanel;
	}

	private JComponent buildOptionsPanel() {

		JComponent optionsPanel = new JPanel();

		JButton loadTexturePackBtn = new JButton("Load Resource Pack");
		loadTexturePackBtn.setIcon(Icon.load.imageIcon());
		loadTexturePackBtn.setToolTipText("Load a custom resource pack");
		loadTexturePackBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(chunky.getFrame(),
								"Load Resource Pack", FileDialog.LOAD);
				fileDialog.setDirectory(MinecraftFinder
						.getResourcePacksDirectory().getAbsolutePath());
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".zip");
							}
						});
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile();
				if (selectedFile != null) {
					try {
						TexturePackLoader.loadTexturePack(selectedFile, true);
						chunky.reloadWorld();
					} catch (TextureLoadingError e1) {
						System.err.println("Error: failed to load default texture pack!");
					}
				}
			}
		});

		JButton loadDefaultTexturesBtn = new JButton("Load Default Textures");
		loadDefaultTexturesBtn.setToolTipText("Attempt to load the default Minecraft textures");
		loadDefaultTexturesBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TexturePackLoader.loadTexturePack(MinecraftFinder.getMinecraftJar(), true);
					chunky.reloadWorld();
				} catch (TextureLoadingError e1) {
					System.err.println("Error: failed to load default texture pack!");
				}
			}
		});

		JCheckBox showLauncher = new JCheckBox("Show launcher when starting Chunky");
		LauncherSettings settings = new LauncherSettings();
		settings.load();
		showLauncher.setSelected(settings.showLauncher);
		showLauncher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				LauncherSettings settings = new LauncherSettings();
				settings.load();
				settings.showLauncher = source.isSelected();
				settings.save();
			}
		});

		GroupLayout layout = new GroupLayout(optionsPanel);
		optionsPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(loadTexturePackBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
					.addComponent(loadDefaultTexturesBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
					.addComponent(showLauncher)
				)
				.addContainerGap()
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
		.addGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(loadTexturePackBtn)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(loadDefaultTexturesBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(showLauncher)
			.addContainerGap())
		);

		return optionsPanel;
	}

	private JComponent buildRenderPanel() {

		JComponent renderPanel = new JPanel();

		JButton newSceneBtn = new JButton();
		newSceneBtn.setText("New Scene");
		newSceneBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.open3DView();
			}
		});

		JButton testCLBtn = new JButton("Test OpenCL");
		testCLBtn.setVisible(chunky.options.openCLEnabled);
		testCLBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.openCLTestRenderer();
			}
		});

		JButton benchmarkBtn = new JButton("Benchmark Renderer");
		benchmarkBtn.setToolTipText("Benchmark the renderer.");
		benchmarkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.runBenchmark();
			}
		});

		JButton loadSceneBtn = new JButton("Load Scene");
		loadSceneBtn.setIcon(Icon.load.imageIcon());
		loadSceneBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chunky.loadScene();
			}
		});

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();

		clearSelectionBtn1.setText(Messages.getString("Controls.ClearSelected_lbl")); //$NON-NLS-1$
		clearSelectionBtn1.setEnabled(false);
		clearSelectionBtn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.clearSelectedChunks();
			}
		});

		JButton openSceneDirBtn = new JButton("Open Scene Directory");
		openSceneDirBtn.setVisible(Desktop.isDesktopSupported());
		openSceneDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Desktop.isDesktopSupported()) {
						File sceneDir = SceneDirectoryPicker.getCurrentSceneDirectory();
						if (sceneDir != null) {
							Desktop.getDesktop().open(sceneDir);
						}
					}
				} catch (IOException e) {
					logger.warn("Failed to open scene directory", e);
				}
			}
		});

		JButton changeSceneDirBtn = new JButton("Change Scene Directory");
		changeSceneDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SceneDirectoryPicker.changeSceneDirectory(chunky.getFrame());
			}
		});

		GroupLayout layout = new GroupLayout(renderPanel);
		renderPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(clearSelectionBtn1)
				.addComponent(sep1)
				.addComponent(newSceneBtn)
				.addComponent(loadSceneBtn)
				.addComponent(sep2)
				.addComponent(testCLBtn)
				.addComponent(benchmarkBtn)
				.addComponent(openSceneDirBtn)
				.addComponent(changeSceneDirBtn)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(clearSelectionBtn1)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(newSceneBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(loadSceneBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(testCLBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(benchmarkBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(openSceneDirBtn)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(changeSceneDirBtn)
			.addContainerGap()
		);

		return renderPanel;
	}

	private JComponent buildAboutPanel() {

		JComponent aboutPanel = new JPanel();

		JLabel aboutLbl = new JLabel();
		aboutLbl.setText(Messages.getString("Controls.AboutText")); //$NON-NLS-1$

		JButton visitWikiBtn = new JButton("Visit the Wiki");
		visitWikiBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("http://chunky.llbit.se"));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JButton visitDevBlogBtn = new JButton("Visit Online Community");
		visitDevBlogBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("http://www.reddit.com/r/chunky"));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JButton showCreditsBtn = new JButton("View Credits");
		showCreditsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Credits credits = new Credits(chunky.getFrame());
				credits.setVisible(true);
			}
		});

		GroupLayout layout = new GroupLayout(aboutPanel);
		aboutPanel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(aboutLbl, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						.addComponent(visitWikiBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						.addComponent(visitDevBlogBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						.addComponent(showCreditsBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE))
					.addContainerGap())
			);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(aboutLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(visitWikiBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(visitDevBlogBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(showCreditsBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap())
		);

		return aboutPanel;
	}

	/**
	 * Build the view tab.
	 * @return the view tab component
	 */
	private JComponent buildViewPanel() {

		JComponent viewPanel = new JPanel();

		ButtonGroup mapModeGroup = new ButtonGroup();
		JLabel viewLabel = new JLabel();
		JRadioButton autoModeBtn = new JRadioButton();
		JRadioButton layerModeBtn = new JRadioButton();
		JRadioButton surfaceModeBtn = new JRadioButton();
		JRadioButton caveModeBtn = new JRadioButton();
		JRadioButton biomeModeBtn = new JRadioButton();
		JSeparator sep1 = new JSeparator();
		JLabel scaleLabel = new JLabel();
		scaleField = new JFormattedTextField(NumberFormat.getInstance());
		scaleSlider = new ImprovedSlider(Chunky.BLOCK_SCALE_MIN, Chunky.BLOCK_SCALE_MAX);
		scaleSlider.setValue(chunky.getScale());
		JLabel layerLabel = new JLabel();
		layerField = new JFormattedTextField(NumberFormat.getInstance());
		layerSlider = new ImprovedSlider(0, Chunk.Y_MAX);
		JLabel dimLabel = new JLabel();
		followPlayer.setSelected(PersistentSettings.getFollowPlayer());
		followPlayer.addActionListener(followPlayerListener);

		ButtonGroup buttonGroup2 = new ButtonGroup();
		overworldBtn = new JRadioButton();
		netherBtn = new JRadioButton();
		endBtn = new JRadioButton();

		dimLabel.setText(Messages.getString("Controls.Dimension_lbl")); //$NON-NLS-1$

		buttonGroup2.add(overworldBtn);
		overworldBtn.setText(Messages.getString("Controls.Earth_lbl")); //$NON-NLS-1$
		overworldBtn.setSelected(true);
		overworldBtn.setEnabled(false);
		overworldBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setDimension(World.OVERWORLD_DIMENSION);
			}
		});

		buttonGroup2.add(netherBtn);
		netherBtn.setText(Messages.getString("Controls.Nether_lbl")); //$NON-NLS-1$
		netherBtn.setEnabled(false);
		netherBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setDimension(World.NETHER_DIMENSION);
			}
		});

		buttonGroup2.add(endBtn);
		endBtn.setText(Messages.getString("Controls.End_lbl")); //$NON-NLS-1$
		endBtn.setEnabled(false);
		endBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setDimension(World.END_DIMENSION);
			}
		});

		viewLabel.setText(Messages.getString("Controls.ViewMode_lbl")); //$NON-NLS-1$

		autoModeBtn.setText("Auto");
		autoModeBtn.setSelected(true);
		autoModeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setRenderer(Chunk.autoRenderer);
			}
		});
		mapModeGroup.add(autoModeBtn);

		layerModeBtn.setText(Messages.getString("Controls.LayerMode_lbl")); //$NON-NLS-1$
		layerModeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setRenderer(Chunk.layerRenderer);
			}
		});
		mapModeGroup.add(layerModeBtn);

		surfaceModeBtn.setText(Messages.getString("Controls.SurfaceMode_lbl")); //$NON-NLS-1$
		surfaceModeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setRenderer(Chunk.surfaceRenderer);
			}
		});
		mapModeGroup.add(surfaceModeBtn);

		caveModeBtn.setText(Messages.getString("Controls.CaveMode_lbl")); //$NON-NLS-1$
		caveModeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setRenderer(Chunk.caveRenderer);
			}
		});
		mapModeGroup.add(caveModeBtn);

		biomeModeBtn.setText("Biomes");
		biomeModeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.setRenderer(Chunk.biomeRenderer);
			}
		});
		mapModeGroup.add(biomeModeBtn);

		scaleLabel.setText(Messages.getString("Controls.Scale_lbl")); //$NON-NLS-1$

		setScale(chunky.getScale());
		scaleField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = scaleField.getText();
				try {
					chunky.setScale(Integer.parseInt(text));
				} catch (NumberFormatException e1) {
				}
			}
		});

		scaleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				chunky.setScale(source.getValue());
			}
		});

		layerLabel.setText(Messages.getString("Controls.Layer_lbl")); //$NON-NLS-1$

		layerField.setText(""+chunky.getLayer());
		layerField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = layerField.getText();
				chunky.setLayer(Integer.parseInt(text));
			}
		});

		layerSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				chunky.setLayer(source.getValue());
			}
		});

		JLabel coordinatesLbl = new JLabel("Coordinates:");
		JLabel xLbl = new JLabel("X=");
		JLabel zLbl = new JLabel("Z=");
		xField = new JTextField(5);
		xField.addActionListener(coordinateActionListener);
		zField = new JTextField(5);
		zField.addActionListener(coordinateActionListener);

		GroupLayout layout = new GroupLayout(viewPanel);
		viewPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup()
					.addComponent(sep1, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
							.addComponent(viewLabel)
							.addComponent(autoModeBtn)
							.addComponent(layerModeBtn)
							.addComponent(surfaceModeBtn)
							.addComponent(caveModeBtn)
							.addComponent(biomeModeBtn))
						.addGap(18)
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
							.addComponent(dimLabel)
							.addComponent(overworldBtn)
							.addComponent(netherBtn)
							.addComponent(endBtn)))
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(scaleField, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(scaleLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scaleSlider, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(layerField, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(layerLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(layerSlider, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
					.addComponent(coordinatesLbl)
					.addGroup(layout.createSequentialGroup()
						.addComponent(xLbl)
						.addComponent(xField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(zLbl)
						.addComponent(zField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(followPlayer)
					)
				)
				.addContainerGap()
			)
		);
		layout.setVerticalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(viewLabel)
						.addComponent(dimLabel))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(autoModeBtn)
						.addComponent(overworldBtn))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(layerModeBtn)
						.addComponent(netherBtn))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(surfaceModeBtn)
						.addComponent(endBtn))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(caveModeBtn)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(biomeModeBtn)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(sep1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.TRAILING)
					.addGroup(layout.createSequentialGroup()
						.addComponent(scaleLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scaleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addComponent(scaleSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(Alignment.TRAILING)
					.addGroup(layout.createSequentialGroup()
						.addComponent(layerLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(layerField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addComponent(layerSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(coordinatesLbl)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(xLbl)
					.addComponent(xField)
					.addComponent(zLbl)
					.addComponent(zField)
					.addComponent(followPlayer)
				)
				.addContainerGap()
			)
		);

		return viewPanel;
	}

	/**
	 * Initialize the edit tab.
	 * @param editPanel
	 */
	private JComponent buildEditPanel() {
		JComponent editPanel = new JPanel();
		deleteChunksBtn = new JButton();
		progressPanel = new ProgressPanel(chunky.getFrame());

		exportZipBtn = new JButton("Export Chunks to ZIP");
		exportZipBtn.setEnabled(false);
		exportZipBtn.setToolTipText("Export selected chunks or the entire world to a Zip file");
		exportZipBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(chunky.getFrame(), "Export to ZIP", FileDialog.SAVE);
				fileDialog.setDirectory(System.getProperty("user.dir"));
				fileDialog.setFile(chunky.getWorldName()+".zip");
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".zip");
							}
						});
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile(".zip");
				if (selectedFile != null) {
					chunky.exportZip(selectedFile, progressPanel);
				}
			}
		});

		JButton renderViewBtn = new JButton();
		renderViewBtn.setText("Render View to PNG");
		renderViewBtn.setToolTipText(
				"Saves the current view as a PNG image");
		renderViewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(chunky.getFrame(),
								"Render View", FileDialog.SAVE);
				fileDialog.setDirectory(System.getProperty("user.dir"));
				fileDialog.setFile(chunky.getWorldName()+".png");
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".png");
							}
						});
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile(".png");
				if (selectedFile != null) {
					chunky.renderView(selectedFile, progressPanel);
				}
			}
		});

		/*JButton renderDimensionBtn = new JButton();
		renderDimensionBtn.setText("Render Dimension to PNG");
		renderDimensionBtn.setToolTipText(
				"Exports the map for the current dimension as a PNG image");
		renderDimensionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(chunky.getFrame(),
								"Render Dimension", FileDialog.SAVE);
				fileDialog.setDirectory(System.getProperty("user.dir"));
				fileDialog.setFile(chunky.getWorldDirectory().getName()+".png");
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".png");
							}
						});
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile(".png");
				if (selectedFile != null)
					chunky.renderDimension(selectedFile, progressPanel);
			}
		});*/

		deleteChunksBtn.setText(Messages.getString("Controls.DeleteSelected_lbl")); //$NON-NLS-1$
		deleteChunksBtn.setEnabled(false);
		deleteChunksBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.deleteSelectedChunks();
			}
		});

		clearSelectionBtn2.setText(Messages.getString("Controls.ClearSelected_lbl")); //$NON-NLS-1$
		clearSelectionBtn2.setEnabled(false);
		clearSelectionBtn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chunky.clearSelectedChunks();
			}
		});

		GroupLayout layout = new GroupLayout(editPanel);
		editPanel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(clearSelectionBtn2)
						.addComponent(deleteChunksBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						.addComponent(progressPanel, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						.addComponent(exportZipBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						.addComponent(renderViewBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)
						/*.addComponent(renderDimensionBtn, GroupLayout.DEFAULT_SIZE, WIDTH_BIG, Short.MAX_VALUE)*/)
					.addContainerGap())
			);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(clearSelectionBtn2)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(deleteChunksBtn, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(exportZipBtn, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(renderViewBtn, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
				/*.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(renderDimensionBtn, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)*/
				.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
				.addComponent(progressPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(10, 10))
		);

		return editPanel;
	}

	/**
	 * Set the current layer
	 * @param y
	 */
	public void setLayer(int y) {
		layerField.setText(""+y);
		layerSlider.setValue(y);
	}

	/**
	 * Set the map view position
	 * @param cx
	 * @param y
	 * @param cz
	 */
	public void setPosition(double cx, int y, double cz) {
		setLayer(y);
		xField.removeActionListener(coordinateActionListener);
		zField.removeActionListener(coordinateActionListener);
		xField.setText(""+(int) (cx*16));
		zField.setText(""+(int) (cz*16));
		xField.addActionListener(coordinateActionListener);
		zField.addActionListener(coordinateActionListener);
	}

	/**
	 * Set the map scale
	 * @param scale
	 */
	public void setScale(int scale) {
		scaleField.setText(""+scale);
		scaleSlider.setValue(scale);
	}

	/**
	 * Called when the chunk selection has changed to notify
	 * the toolbox if chunks are selected or not
	 * @param selected
	 */
	public void setChunksSelected(boolean selected) {
		deleteChunksBtn.setEnabled(selected);
		exportZipBtn.setEnabled(selected);
		clearSelectionBtn1.setEnabled(selected);
		clearSelectionBtn2.setEnabled(selected);
		if (selected && tabbedPane.getSelectedIndex() == 0) {
			tabbedPane.setSelectedIndex(RENDER_TAB_INDEX);
		}
	}

	/**
	 * Set the player Y position
	 * @param layer
	 */
	public void setPlayerY(int layer) {
		if (layer >= 0) {
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(layer, new JLabel(faceIcon));
			layerSlider.setLabelTable(labelTable);
			layerSlider.setPaintLabels(true);
		} else {
			layerSlider.setLabelTable(null);
			layerSlider.setPaintLabels(false);
		}
	}

	/**
	 * @return The progress panel
	 */
	public ProgressPanel getProgressPanel() {
		return progressPanel;
	}

	/**
	 * Enable given dimension's check box
	 * @param i
	 * @param haveDimension
	 */
	public void enableDimension(int i, boolean haveDimension) {
		switch (i) {
		case World.OVERWORLD_DIMENSION:
			overworldBtn.setEnabled(haveDimension);
			break;
		case World.NETHER_DIMENSION:
			netherBtn.setEnabled(haveDimension);
			break;
		case World.END_DIMENSION:
			endBtn.setEnabled(haveDimension);
			break;
		}
	}

	/**
	 * Open the World Selector dialog.
	 */
	public void openWorldSelector() {

		if (worldSelector == null || !worldSelector.isDisplayable()) {
			worldSelector = new WorldSelector(chunky);
		} else if (worldSelector != null) {
			worldSelector.requestFocus();
			worldSelector.toFront();
		}
	}

	final ActionListener coordinateActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int x = Integer.parseInt(xField.getText());
				int z = Integer.parseInt(zField.getText());
				chunky.setView(x/16.0, z/16.0);
				stopFollowingPlayer();
			} catch (NumberFormatException e1) {
			}
		}
	};

	final ActionListener followPlayerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			boolean follow = source.isSelected();
			PersistentSettings.setFollowPlayer(follow);
			if (follow) {
				chunky.goToPlayer();
			}
		}
	};

	public void stopFollowingPlayer() {
		if (followPlayer.isSelected()) {
			followPlayer.removeActionListener(followPlayerListener);
			followPlayer.setSelected(false);
			PersistentSettings.setFollowPlayer(false);
			followPlayer.addActionListener(followPlayerListener);
		}
	}
}
