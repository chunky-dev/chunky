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
package se.llbit.chunky.renderer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;
import se.llbit.util.ProgramProperties;

/**
 * Render Controls dialog.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class RenderControls extends JDialog implements ViewListener,
	RenderStatusListener {

	private static final Logger logger =
			Logger.getLogger(RenderControls.class);

	private static final int[] dumpFrequencies = { 50, 100, 500, 1000 };

	private final RenderManager renderMan;
	private final SceneManager sceneMan;
	private final Chunk3DView view;
	private final Chunky chunky;

	/**
	 * Number format for current locale.
	 */
	private final NumberFormat numberFormat =
			NumberFormat.getInstance();

	private final JSlider skyRotationSlider = new JSlider();
	private final JSlider focalOffsetSlider = new JSlider();
	private final JButton loadSkymapBtn = new JButton();
	private final JCheckBox mirrorSkyCB = new JCheckBox();
	private final JTextField widthField = new JTextField();
	private final JTextField heightField = new JTextField();
	private final JComboBox projectionMode = new JComboBox();
	private final JTextField focalOffsetField = new JTextField();
	private final JButton startRenderBtn = new JButton();
	private final JCheckBox enableEmitters = new JCheckBox();
	private final JCheckBox directLight = new JCheckBox();
	private final JButton saveSceneBtn = new JButton();
	private final JButton loadSceneBtn = new JButton();
	private final JButton saveFrameBtn = new JButton();
	private final JCheckBox stillWaterCB = new JCheckBox();
	private final JTextField sceneNameField = new JTextField();
	private final JLabel sceneNameLbl = new JLabel();
	private final JCheckBox biomeColorsCB = new JCheckBox();
	private final JButton stopRenderBtn = new JButton();
	private final JCheckBox clearWaterCB = new JCheckBox();
	private final JCheckBox atmosphereEnabled = new JCheckBox();
	private final JCheckBox volumetricFogEnabled = new JCheckBox();
	private final JCheckBox cloudsEnabled = new JCheckBox();
	private final JSlider exposureSlider = new JSlider();
	private final JTextField exposureField = new JTextField();
	private final RenderContext context;
	private final JButton showPreviewBtn = new JButton();
	private final JLabel renderTimeLbl = new JLabel();
	private final JLabel samplesPerSecondLbl = new JLabel();
	private final JLabel sppLbl = new JLabel();
	private final JProgressBar progressBar;
	private final JLabel progressLbl = new JLabel();
	private final JComboBox postprocessCB = new JComboBox();
	private final JComboBox skyModeCB = new JComboBox();
	private final JButton changeSunColorBtn = new JButton();
	private final JButton changeGroundColorBtn = new JButton();
	private final JLabel etaLbl = new JLabel();
	private final JCheckBox waterWorldCB = new JCheckBox();
	private final JTextField waterHeightField = new JTextField();
	private final DecimalFormat decimalFormat = new DecimalFormat();
	private final JCheckBox saveDumpsCB = new JCheckBox();
	private final JComboBox dumpFrequency = new JComboBox();
	private final JTextField sppTargetField = new JTextField();
	private final JTextField cameraX = new JTextField();
	private final JTextField cameraY = new JTextField();
	private final JTextField cameraZ = new JTextField();
	private final JTextField cameraYaw = new JTextField();
	private final JTextField cameraPitch = new JTextField();

	private Adjuster rayDepth;
	private Adjuster cloudHeight;
	private Adjuster emitterIntensity;
	private Adjuster sunIntensity;
	private Adjuster sunAzimuth;
	private Adjuster sunAltitude;
	private Adjuster fov;
	private Adjuster dof;

	/**
	 * Create a new Render Controls dialog.
	 * @param chunkyInstance
	 * @param renderContext
	 */
	public RenderControls(Chunky chunkyInstance, RenderContext renderContext) {

		super(chunkyInstance.getFrame());

		decimalFormat.setGroupingSize(3);
		decimalFormat.setGroupingUsed(true);

		context = renderContext;
		chunky = chunkyInstance;

		view = new Chunk3DView(this, chunkyInstance.getFrame());

		renderMan = new RenderManager(
				view.getCanvas(), renderContext, this);
		renderMan.start();

		view.setRenderer(renderMan);

		sceneMan = new SceneManager(renderMan);
		sceneMan.start();

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModalityType(ModalityType.MODELESS);

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				sceneMan.interrupt();
				RenderControls.this.dispose();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				// halt rendering
				renderMan.interrupt();

				// dispose of the 3D view
				view.setVisible(false);
				view.dispose();
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

		updateTitle();

		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("General", buildGeneralPane());
		tabbedPane.addTab("Lighting", buildLightingPane());
		tabbedPane.addTab("Sky", buildSkyPane());
		tabbedPane.addTab("Camera", buildCameraPane());
		tabbedPane.addTab("Post-processing", buildPostProcessingPane());
		tabbedPane.addTab("Advanced", buildAdvancedPane());

		JLabel sppTargetLbl = new JLabel("SPP Target: ");
		sppTargetLbl.setToolTipText("The render will be paused at this SPP count");

		JButton setDefaultBtn = new JButton("Set Default");
		setDefaultBtn.setToolTipText("Make the current SPP target the default");
		setDefaultBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProgramProperties.setProperty("sppTargetDefault",
						"" + renderMan.scene().getTargetSPP());
			}
		});

		sppTargetField.setColumns(10);
		sppTargetField.getDocument().addDocumentListener(sppTargetListener);

		updateSPPTargetField();

		JLabel renderLbl = new JLabel("Render: ");

		setViewVisible(false);
		showPreviewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (view.isVisible()) {
					view.setVisible(false);
				} else {
					show3DView();
				}
			}
		});

		startRenderBtn.setText("START");
		startRenderBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!renderMan.scene().pathTrace()) {
					renderMan.scene().startRender();
					startRenderBtn.setText("PAUSE");
					startRenderBtn.repaint();
				} else {
					if (renderMan.scene().isPaused()) {
						renderMan.scene().resumeRender();
						startRenderBtn.setText("PAUSE");
						startRenderBtn.repaint();
					} else {
						renderMan.scene().pauseRender();
						startRenderBtn.setText("RESUME");
						startRenderBtn.repaint();
					}
				}
				stopRenderBtn.setEnabled(true);
			}
		});

		stopRenderBtn.setText("HALT");
		stopRenderBtn.setToolTipText("<html>Warning: this will discard the " +
				"current rendered image!<br>Make sure to save your image " +
				"before stopping the renderer!");
		stopRenderBtn.setForeground(Color.red);
		stopRenderBtn.setEnabled(false);
		stopRenderBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().haltRender();
				startRenderBtn.setText("START");
				startRenderBtn.repaint();
				stopRenderBtn.setEnabled(false);
			}
		});

		saveFrameBtn.setText("Save Current Frame");
		saveFrameBtn.addActionListener(saveFrameListener);

		samplesPerSecondLbl.setToolTipText("Samples Per Second");
		sppLbl.setToolTipText("Samples Per Pixel");

		setRenderTime(0);
		setSamplesPerSecond(0);
		setSPP(0);
		setProgress("Progress:", 0, 0, 1);

		progressBar = new JProgressBar();

		progressLbl.setText("Progress:");

		etaLbl.setText("ETA:");

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(tabbedPane)
				.addComponent(showPreviewBtn)
				.addGroup(layout.createSequentialGroup()
					.addComponent(sppTargetLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sppTargetField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(setDefaultBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(renderLbl)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(startRenderBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(stopRenderBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(saveFrameBtn)
				)
				.addComponent(renderTimeLbl)
				.addComponent(samplesPerSecondLbl)
				.addComponent(sppLbl)
				.addGroup(layout.createSequentialGroup()
					.addComponent(progressLbl)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(etaLbl)
				)
				.addComponent(progressBar))
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(tabbedPane)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(showPreviewBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(sppTargetLbl)
				.addComponent(sppTargetField)
				.addComponent(setDefaultBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(renderLbl)
				.addComponent(startRenderBtn)
				.addComponent(stopRenderBtn)
				.addComponent(saveFrameBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(renderTimeLbl)
			.addComponent(samplesPerSecondLbl)
			.addComponent(sppLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(progressLbl)
				.addComponent(etaLbl)
			)
			.addComponent(progressBar)
			.addContainerGap()
		);
		setContentPane(panel);

		pack();

		setLocationRelativeTo(chunky.getFrame());

		setVisible(true);
	}

	private Component buildAdvancedPane() {
		rayDepth = new Adjuster("Ray depth",
				"Sets the recursive ray depth",
				1, 25) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().setRayDepth((int) newValue);
			}
			@Override
			public void update() {
				rayDepth.set(renderMan.scene().getRayDepth());
			}
		};
		rayDepth.update();

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();

		JLabel waterWorldLbl = new JLabel(
				"Note: All chunks will be reloaded after changing the water world options!");
		JLabel waterHeightLbl = new JLabel("Water height: ");
		waterHeightField.setColumns(5);
		waterHeightField.setText("" + World.SEA_LEVEL);
		waterHeightField.setEnabled(false);
		waterHeightField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField) e.getSource();
				renderMan.scene().setWaterHeight(Integer.parseInt(source.getText()));
				sceneMan.reloadChunks();
				updateWaterHeight();
			}
		});

		waterWorldCB.setText("Water World Mode");
		waterWorldCB.setSelected(false);
		waterWorldCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				if (source.isSelected()) {
					renderMan.scene().setWaterHeight(
							Integer.parseInt(waterHeightField.getText()));
				} else {
					renderMan.scene().setWaterHeight(0);
				}
				sceneMan.reloadChunks();
				updateWaterHeight();
			}
		});

		JButton mergeDumpBtn = new JButton("Merge Render Dump");
		mergeDumpBtn.setToolTipText(
				"Merge an existing render dump with the current render");
		mergeDumpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CenteredFileDialog fileDialog =
						new CenteredFileDialog(null,
								"Select Render Dump", FileDialog.LOAD);
				fileDialog.setFilenameFilter(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".dump");
							}
						});
				fileDialog.setDirectory(ProgramProperties.
						getSceneDirectory().getAbsolutePath());
				fileDialog.setVisible(true);
				File selectedFile = fileDialog.getSelectedFile();
				if (selectedFile != null) {
					sceneMan.mergeRenderDump(selectedFile);
				}
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(rayDepth.horizontalGroup(layout))
				.addComponent(sep1)
				.addComponent(waterWorldLbl)
				.addComponent(waterWorldCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(waterHeightLbl)
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(waterHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(sep2)
				.addComponent(mergeDumpBtn)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(rayDepth.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(waterWorldLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(waterWorldCB)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(waterHeightLbl)
				.addComponent(waterHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(mergeDumpBtn)
			.addContainerGap()
		);
		return panel;
	}

	private Component buildPostProcessingPane() {
		JLabel exposureLbl = new JLabel("exposure: ");

		exposureField.setColumns(5);
		exposureField.addActionListener(exposureFieldListener);
		updateExposureField();

		exposureSlider.setMinimum(1);
		exposureSlider.setMaximum(100);
		exposureSlider.addChangeListener(exposureListener);
		updateExposureSlider();

		JLabel postprocessDescLbl = new JLabel("<html>Post processing affects rendering performance<br>when the preview window is visible");
		JLabel postprocessLbl = new JLabel("Post-processing mode:");
		for (Postprocess pp : Postprocess.values) {
			postprocessCB.addItem("" + pp);
		}
		updatePostprocessCB();
		postprocessCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox source = (JComboBox) e.getSource();
				renderMan.scene().setPostprocess(
						Postprocess.get(source.getSelectedIndex()));
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(exposureLbl)
					.addComponent(exposureSlider)
					.addComponent(exposureField)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(postprocessLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(postprocessCB)
				)
				.addComponent(postprocessDescLbl)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(exposureLbl)
				.addComponent(exposureSlider)
				.addComponent(exposureField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(postprocessLbl)
				.addComponent(postprocessCB)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(postprocessDescLbl)
			.addContainerGap()
		);
		return panel;
	}

	private Component buildGeneralPane() {
		JLabel widthLbl = new JLabel("Canvas width: ");
		JLabel heightLbl = new JLabel("Canvas height: ");

		widthField.setColumns(10);
		widthField.addActionListener(canvasSizeListener);
		heightField.setColumns(10);
		heightField.addActionListener(canvasSizeListener);

		updateWidthField();
		updateHeightField();

		saveSceneBtn.setText("Save Scene");
		saveSceneBtn.addActionListener(saveSceneListener);

		loadSceneBtn.setText("Load Scene");
		loadSceneBtn.addActionListener(loadSceneListener);

		JButton loadSelectedChunksBtn = new JButton("Load Selected Chunks");
		loadSelectedChunksBtn.setToolTipText("Load the chunks that are currently selected in the map view");
		loadSelectedChunksBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadChunks(chunky.getWorld(), chunky.getSelectedChunks());
			}
		});

		JButton reloadChunksBtn = new JButton("Reload Chunks");
		reloadChunksBtn.setToolTipText("Reload all chunks in the scene");
		reloadChunksBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneMan.reloadChunks();
			}
		});

		JButton openSceneDirBtn = new JButton("Open Scene Directory");
		openSceneDirBtn.setToolTipText("Open the directory where Chunky stores scene descriptions and renders");
		openSceneDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(context.getSceneDirectory());
					}
				} catch (IOException e) {
					logger.warn("Failed to open scene directory", e);
				}
			}
		});
		openSceneDirBtn.setVisible(Desktop.isDesktopSupported());

		loadSceneBtn.setToolTipText("This replaces the current scene!");
		JButton setCanvasSizeBtn = new JButton("Set Canvas Size");
		setCanvasSizeBtn.addActionListener(canvasSizeListener);
		JButton halveCanvasSizeBtn = new JButton("Halve");
		halveCanvasSizeBtn.setToolTipText("Halve the canvas size");
		halveCanvasSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int width = renderMan.scene().canvasWidth() / 2;
				int height = renderMan.scene().canvasHeight() / 2;
				setCanvasSize(width, height);
			}
		});
		JButton doubleCanvasSizeBtn = new JButton("Double");
		halveCanvasSizeBtn.setToolTipText("Double the canvas size");
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
				ProgramProperties.setProperty("3dcanvas.width",
						"" + renderMan.scene().canvasWidth());
				ProgramProperties.setProperty("3dcanvas.height",
						"" + renderMan.scene().canvasHeight());
			}
		});

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();

		sceneNameLbl.setText("Scene name: ");
		sceneNameField.setColumns(15);
		sceneNameField.getDocument().addDocumentListener(sceneNameListener);
		updateSceneNameField();

		stillWaterCB.setText("still water");
		stillWaterCB.addActionListener(stillWaterListener);
		updateStillWater();

		clearWaterCB.setText("clear water");
		stillWaterCB.addActionListener(clearWaterListener);
		updateClearWater();

		biomeColorsCB.setText("enable biome colors");
		updateBiomeColorsCB();

		saveDumpsCB.setText("save dump once every ");
		saveDumpsCB.addActionListener(saveDumpsListener);
		updateSaveDumpsCheckBox();

		JLabel dumpFrequencyLbl = new JLabel(" frame");
		String[] frequencyStrings = new String[dumpFrequencies.length];
		for (int i = 0; i < dumpFrequencies.length; ++i)
			frequencyStrings[i] = dumpFrequencies[i] + "th";
		dumpFrequency.setModel(new DefaultComboBoxModel(frequencyStrings));
		dumpFrequency.setEditable(false);
		dumpFrequency.addActionListener(dumpFrequencyListener);
		updateDumpFrequencyField();

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addComponent(sceneNameLbl)
							.addComponent(sceneNameField))
						.addGroup(layout.createSequentialGroup()
							.addComponent(saveSceneBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(loadSceneBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(openSceneDirBtn))
						.addGroup(layout.createSequentialGroup()
							.addComponent(loadSelectedChunksBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(reloadChunksBtn))
						.addComponent(sep1)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(widthLbl)
								.addComponent(heightLbl))
							.addGroup(layout.createParallelGroup()
								.addComponent(widthField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(heightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(layout.createSequentialGroup()
							.addComponent(setCanvasSizeBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(halveCanvasSizeBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(doubleCanvasSizeBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(makeDefaultBtn))
						.addComponent(sep2)
						.addComponent(stillWaterCB)
						.addComponent(clearWaterCB)
						.addComponent(biomeColorsCB)
						.addGroup(layout.createSequentialGroup()
							.addComponent(saveDumpsCB)
							.addComponent(dumpFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(dumpFrequencyLbl)
							.addGap(0, 0, Short.MAX_VALUE)))
					.addContainerGap());
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(sceneNameLbl)
					.addComponent(sceneNameField))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(saveSceneBtn)
					.addComponent(loadSceneBtn)
					.addComponent(openSceneDirBtn))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(loadSelectedChunksBtn)
					.addComponent(reloadChunksBtn))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(widthLbl)
					.addComponent(widthField))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(heightLbl)
					.addComponent(heightField))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(setCanvasSizeBtn)
					.addComponent(halveCanvasSizeBtn)
					.addComponent(doubleCanvasSizeBtn)
					.addComponent(makeDefaultBtn))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(stillWaterCB)
				.addComponent(clearWaterCB)
				.addComponent(biomeColorsCB)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(saveDumpsCB)
					.addComponent(dumpFrequency)
					.addComponent(dumpFrequencyLbl))
				.addContainerGap());
		return panel;
	}

	private Component buildLightingPane() {

		changeSunColorBtn.setText("Change Sun Color");
		changeSunColorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				java.awt.Color newColor = JColorChooser.showDialog(
						RenderControls.this, "Choose Sun Color",
						renderMan.scene().sun().getAwtColor());
				if (newColor != null) {
					renderMan.scene().sun().setColor(newColor);
				}
			}
		});

		directLight.setText("enable sunlight");
		directLight.setSelected(renderMan.scene().getDirectLight());
		directLight.addActionListener(directLightListener);

		enableEmitters.setText("enable emitters");
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		enableEmitters.addActionListener(emittersListener);

		emitterIntensity = new Adjuster("Emitter intensity",
				"Light intensity modifier for emitters",
				Scene.MIN_EMITTER_INTENSITY,
				Scene.MAX_EMITTER_INTENSITY) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().setEmitterIntensity(newValue);
			}
			@Override
			public void update() {
				emitterIntensity.set(renderMan.scene().getEmitterIntensity());
			}
		};
		emitterIntensity.setLogarithmicMode(true);
		emitterIntensity.update();

		sunIntensity = new Adjuster("Sun intensity",
				"Light intensity modifier for sun",
				Sun.MIN_INTENSITY,
				Sun.MAX_INTENSITY) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().sun().setIntensity(newValue);
			}
			@Override
			public void update() {
				sunIntensity.set(renderMan.scene().sun().getIntensity());
			}
		};
		sunIntensity.setLogarithmicMode(true);
		sunIntensity.update();

		sunAzimuth = new Adjuster("Sun azimuth",
				"The angle towards the sun from north",
				0.0, 360.0) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().sun().setAzimuth(
						QuickMath.degToRad(newValue));
			}
			@Override
			public void update() {
				sunAzimuth.set(QuickMath.radToDeg(renderMan.scene().sun().getAzimuth()));
			}
		};
		sunAzimuth.update();

		sunAltitude = new Adjuster("Sun altitude",
				"The angle of the sun above the horizon",
				0.0, 90.0) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().sun().setAltitude(
						QuickMath.degToRad(newValue));
			}
			@Override
			public void update() {
				sunAltitude.set(QuickMath.radToDeg(renderMan.scene().sun().getAltitude()));
			}
		};
		sunAltitude.update();

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(directLight)
				.addComponent(enableEmitters)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(emitterIntensity.getLabel())
						.addComponent(sunIntensity.getLabel())
						.addComponent(sunAzimuth.getLabel())
						.addComponent(sunAltitude.getLabel())
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(emitterIntensity.getSlider())
						.addComponent(sunIntensity.getSlider())
						.addComponent(sunAzimuth.getSlider())
						.addComponent(sunAltitude.getSlider())
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(emitterIntensity.getField())
						.addComponent(sunIntensity.getField())
						.addComponent(sunAzimuth.getField())
						.addComponent(sunAltitude.getField())
					)
				)
				.addComponent(changeSunColorBtn)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(enableEmitters)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(emitterIntensity.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(directLight)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(sunIntensity.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(sunAzimuth.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(sunAltitude.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(changeSunColorBtn)
			.addContainerGap()
		);
		return panel;
	}

	private Component buildSkyPane() {

		@SuppressWarnings("unused")
		JLabel skyModeLbl = new JLabel("Sky mode:");
		skyModeCB.addItem("gradient");
		skyModeCB.addItem("simulated sky");
		skyModeCB.addItem("skymap");
		skyModeCB.addItem("skybox");
		skyModeCB.addActionListener(skyModeListener);
		updateSkyMode();

		JLabel skyRotationLbl = new JLabel("Skymap rotation:");
		skyRotationSlider.setMinimum(1);
		skyRotationSlider.setMaximum(100);
		skyRotationSlider.addChangeListener(skyRotationListener);
		skyRotationSlider.setToolTipText("Controls the horizontal rotational offset for the skymap");
		updateSkyRotation();

		loadSkymapBtn.setText("Load Skymap");
		loadSkymapBtn.setToolTipText("Use a panoramic skymap");
		loadSkymapBtn.addActionListener(loadSkymapListener);

		JSeparator sep1 = new JSeparator();

		mirrorSkyCB.setText("Mirror sky at horizon");
		mirrorSkyCB.addActionListener(mirrorSkyListener);
		updateMirroSkyCB();

		changeGroundColorBtn.setText("Change Ground Color");
		changeGroundColorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				java.awt.Color newColor = JColorChooser.showDialog(
						RenderControls.this, "Choose Ground Color",
						renderMan.scene().sky().getGroundColor());
				if (newColor != null) {
					renderMan.scene().sky().setGroundColor(newColor);
				}
			}
		});

		JButton unloadSkymapBtn = new JButton("Unload Skymap");
		unloadSkymapBtn.setToolTipText("Use the default sky");
		unloadSkymapBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().sky().unloadSkymap();
			}
		});

		atmosphereEnabled.setText("enable atmosphere");
		atmosphereEnabled.addActionListener(atmosphereListener);
		updateAtmosphereCheckBox();

		volumetricFogEnabled.setText("enable volumetric fog");
		volumetricFogEnabled.addActionListener(volumetricFogListener);
		updateVolumetricFogCheckBox();

		cloudsEnabled.setText("enable clouds");
		cloudsEnabled.addActionListener(cloudsEnabledListener);
		updateCloudsEnabledCheckBox();

		cloudHeight = new Adjuster("cloud height", "The height of the cloud layer",
				-128, 512) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().setCloudHeight((int) newValue);
			}
			@Override
			public void update() {
				cloudHeight.set(renderMan.scene().getCloudHeight());
			}
		};
		cloudHeight.update();

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				// TODO
				/*.addGroup(layout.createSequentialGroup()
					.addComponent(skyModeLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(skyModeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)*/
				.addGroup(layout.createSequentialGroup()
					.addComponent(skyRotationLbl)
					.addComponent(skyRotationSlider)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(loadSkymapBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(unloadSkymapBtn)
				)
				.addComponent(mirrorSkyCB)
				.addComponent(changeGroundColorBtn)
				.addComponent(sep1)
				.addComponent(atmosphereEnabled)
				.addComponent(volumetricFogEnabled)
				.addComponent(cloudsEnabled)
				.addGroup(cloudHeight.horizontalGroup(layout))
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			// TODO
			/*.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(skyModeLbl)
				.addComponent(skyModeCB)
			)*/
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(loadSkymapBtn)
				.addComponent(unloadSkymapBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(skyRotationLbl)
				.addComponent(skyRotationSlider)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(mirrorSkyCB)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(changeGroundColorBtn)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(atmosphereEnabled)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(volumetricFogEnabled)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(cloudsEnabled)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cloudHeight.verticalGroup(layout))
			.addContainerGap()
		);
		return panel;
	}

	private Component buildCameraPane() {
		JLabel projectionModeLbl = new JLabel("Projection");
		JLabel focalOffsetLbl = new JLabel("Focal Offset: ");

		fov = new Adjuster(
				"Field of View (zoom)",
				"Field of View",
				1.0,
				180.0) {
			@Override
			public void valueChanged(double newValue) {
				renderMan.scene().camera().setFoV(newValue);
			}
			@Override
			public void update() {
				Camera camera = renderMan.scene().camera();
				fov.set(camera.getFoV(), camera.getMinFoV(), camera.getMaxFoV());
			}
		};
		fov.update();

		dof = new DoFAdjuster(renderMan);
		dof.update();

		focalOffsetSlider.setMinimum(1);
		focalOffsetSlider.setMaximum(1000);
		focalOffsetSlider.addChangeListener(focalOffsetListener);
		updateFocalOffsetSlider();

		Camera.ProjectionMode[] projectionModes = Camera.ProjectionMode.class.getEnumConstants();
		String[] projectionModeNames = new String[projectionModes.length];
		for( int i=0; i<projectionModes.length; ++i ) {
			projectionModeNames[i] = projectionModes[i].niceName;
		}
		projectionMode.setModel(new DefaultComboBoxModel(projectionModeNames));
		projectionMode.addActionListener(projectionModeListener);
		updateProjectionModeField();

		focalOffsetField.setColumns(5);
		focalOffsetField.addActionListener(focalOffsetFieldListener);
		updateFocalOffsetField();

		JButton autoFocusBtn = new JButton("Autofocus");
		autoFocusBtn.setToolTipText("Focuses on the object right in the center, under the crosshairs");
		autoFocusBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().autoFocus();
				dof.update();
				updateFocalOffsetField();
				updateFocalOffsetSlider();
			}
		});

		JButton cameraToPlayerBtn = new JButton("Camera to player");
		cameraToPlayerBtn.setToolTipText("Move camera to player position");
		cameraToPlayerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().moveCameraToPlayer();
			}
		});

		JLabel posLbl = new JLabel("Position:");
		cameraX.setColumns(10);
		cameraX.setHorizontalAlignment(JTextField.RIGHT);
		cameraX.addActionListener(cameraPositionListener);
		cameraY.setColumns(10);
		cameraY.setHorizontalAlignment(JTextField.RIGHT);
		cameraY.addActionListener(cameraPositionListener);
		cameraZ.setColumns(10);
		cameraZ.setHorizontalAlignment(JTextField.RIGHT);
		cameraZ.addActionListener(cameraPositionListener);
		updateCameraPosition();

		JLabel dirLbl = new JLabel("Direction:");
		cameraYaw.setColumns(10);
		cameraYaw.setHorizontalAlignment(JTextField.RIGHT);
		cameraYaw.addActionListener(cameraDirectionListener);
		cameraPitch.setColumns(10);
		cameraPitch.setHorizontalAlignment(JTextField.RIGHT);
		cameraPitch.addActionListener(cameraDirectionListener);
		updateCameraDirection();

		JLabel lookAtLbl = new JLabel("Skybox views:");
		JButton xposBtn = new JButton("East");
		xposBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(Math.PI, -Math.PI/2);
				fov.update();
				updateCameraDirection();
			}
		});

		JButton xnegBtn = new JButton("West");
		xnegBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(0, -Math.PI/2);
				fov.update();
				updateCameraDirection();
			}
		});

		JButton yposBtn = new JButton("Up");
		yposBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(-Math.PI/2, Math.PI);
				fov.update();
				updateCameraDirection();
			}
		});

		JButton ynegBtn = new JButton("Down");
		ynegBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(-Math.PI/2, 0);
				fov.update();
				updateCameraDirection();
			}
		});

		JButton zposBtn = new JButton("South");
		zposBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(Math.PI/2, -Math.PI/2);
				fov.update();
				updateCameraDirection();
			}
		});

		JButton znegBtn = new JButton("North");
		znegBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(-Math.PI/2, -Math.PI/2);
				fov.update();
				updateCameraDirection();
			}
		});

		JButton centerCameraBtn = new JButton("Center camera");
		centerCameraBtn.setToolTipText("Center camera above loaded chunks");
		centerCameraBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().moveCameraToCenter();
			}
		});

		JLabel isometricLbl = new JLabel("Isometric views:");

		JButton isoWNBtn = new JButton("west-north");
		isoWNBtn.setIcon(new ImageIcon(Icon.isoWN.getImage()));
		isoWNBtn.setToolTipText("Set isometric view");
		isoWNBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setView(-Math.PI/4, -Math.PI/4);
				camera.setProjectionMode(Camera.ProjectionMode.PARALLEL);
				updateProjectionModeField();
				fov.update();
				updateCameraDirection();
			}
		});

		JButton isoNEBtn = new JButton("north-east");
		isoNEBtn.setIcon(new ImageIcon(Icon.isoNE.getImage()));
		isoNEBtn.setToolTipText("Set isometric view");
		isoNEBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setView(-3*Math.PI/4, -Math.PI/4);
				camera.setProjectionMode(Camera.ProjectionMode.PARALLEL);
				updateProjectionModeField();
				fov.update();
				updateCameraDirection();
			}
		});

		JButton isoESBtn = new JButton("east-south");
		isoESBtn.setIcon(new ImageIcon(Icon.isoES.getImage()));
		isoESBtn.setToolTipText("Set isometric view");
		isoESBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setView(-5*Math.PI/4, -Math.PI/4);
				camera.setProjectionMode(Camera.ProjectionMode.PARALLEL);
				updateProjectionModeField();
				fov.update();
				updateCameraDirection();
			}
		});

		JButton isoSWBtn = new JButton("south-west");
		isoSWBtn.setIcon(new ImageIcon(Icon.isoSW.getImage()));
		isoSWBtn.setToolTipText("Set isometric view");
		isoSWBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setView(-7*Math.PI/4, -Math.PI/4);
				camera.setProjectionMode(Camera.ProjectionMode.PARALLEL);
				updateProjectionModeField();
				fov.update();
				updateCameraDirection();
			}
		});

		JSeparator sep2 = new JSeparator();
		JSeparator sep1 = new JSeparator();

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(posLbl)
						.addComponent(dirLbl)
					)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(cameraX, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cameraYaw, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(cameraY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cameraPitch, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(cameraZ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(cameraToPlayerBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(centerCameraBtn)
				)
				.addComponent(lookAtLbl)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(xposBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(xnegBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(yposBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(ynegBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(zposBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(znegBtn)
				)
				.addComponent(sep1)
				.addComponent(isometricLbl)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(isoWNBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(isoNEBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(isoESBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(isoSWBtn)
				)
				.addComponent(sep2)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(projectionModeLbl)
						.addComponent(fov.getLabel())
						.addComponent(dof.getLabel())
						.addComponent(focalOffsetLbl))
					.addGroup(layout.createParallelGroup()
						.addComponent(projectionMode)
						.addComponent(fov.getSlider())
						.addComponent(dof.getSlider())
						.addComponent(focalOffsetSlider))
					.addGroup(layout.createParallelGroup()
						.addComponent(fov.getField())
						.addComponent(dof.getField())
						.addComponent(focalOffsetField)))
				.addComponent(autoFocusBtn))
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(posLbl)
				.addComponent(cameraX)
				.addComponent(cameraY)
				.addComponent(cameraZ)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(dirLbl)
				.addComponent(cameraYaw)
				.addComponent(cameraPitch)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(cameraToPlayerBtn)
				.addComponent(centerCameraBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lookAtLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(xposBtn)
				.addComponent(xnegBtn)
				.addComponent(yposBtn)
				.addComponent(ynegBtn)
				.addComponent(zposBtn)
				.addComponent(znegBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(isometricLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(isoWNBtn)
				.addComponent(isoNEBtn)
				.addComponent(isoESBtn)
				.addComponent(isoSWBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(projectionModeLbl)
				.addComponent(projectionMode))
			.addGroup(fov.verticalGroup(layout))
			.addGroup(dof.verticalGroup(layout))
			.addGroup(layout.createParallelGroup()
				.addComponent(focalOffsetLbl)
				.addComponent(focalOffsetSlider)
				.addComponent(focalOffsetField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(autoFocusBtn)
			.addContainerGap()
		);
		return panel;
	}

	protected void updateStillWater() {
		stillWaterCB.removeActionListener(stillWaterListener);
		stillWaterCB.setSelected(renderMan.scene().stillWaterEnabled());
		stillWaterCB.addActionListener(stillWaterListener);
	}

	protected void updateClearWater() {
		clearWaterCB.removeActionListener(clearWaterListener);
		clearWaterCB.setSelected(renderMan.scene().getClearWater());
		clearWaterCB.addActionListener(clearWaterListener);
	}

	protected void updateBiomeColorsCB() {
		biomeColorsCB.removeActionListener(biomeColorsCBListener);
		biomeColorsCB.addActionListener(biomeColorsCBListener);
		biomeColorsCB.setSelected(renderMan.scene().biomeColorsEnabled());
	}

	protected void updateAtmosphereCheckBox() {
		atmosphereEnabled.removeActionListener(atmosphereListener);
		atmosphereEnabled.setSelected(renderMan.scene().atmosphereEnabled());
		atmosphereEnabled.addActionListener(atmosphereListener);
	}

	protected void updateMirroSkyCB() {
		mirrorSkyCB.removeActionListener(mirrorSkyListener);
		mirrorSkyCB.setSelected(renderMan.scene().sky().isMirrored());
		mirrorSkyCB.addActionListener(mirrorSkyListener);
	}

	protected void updateVolumetricFogCheckBox() {
		volumetricFogEnabled.removeActionListener(volumetricFogListener);
		volumetricFogEnabled.setSelected(renderMan.scene().volumetricFogEnabled());
		volumetricFogEnabled.addActionListener(volumetricFogListener);
	}

	protected void updateCloudsEnabledCheckBox() {
		cloudsEnabled.removeActionListener(cloudsEnabledListener);
		cloudsEnabled.setSelected(renderMan.scene().cloudsEnabled());
		cloudsEnabled.addActionListener(cloudsEnabledListener);
	}

	private void updateTitle() {
		setTitle("Render Controls - " + renderMan.scene().name());
	}

	private final ActionListener dumpFrequencyListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int index = dumpFrequency.getSelectedIndex();
				index = Math.max(0, index);
				index = Math.min(dumpFrequencies.length-1, index);
				renderMan.scene().setDumpFrequency(dumpFrequencies[index]);
			} catch (NumberFormatException e1) {
			}
			updateDumpFrequencyField();
		}
	};

	private final ActionListener saveDumpsListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean enabled = saveDumpsCB.isSelected();
			renderMan.scene().setSaveDumps(enabled);
			dumpFrequency.setEnabled(enabled);
		}
	};

	private final ActionListener canvasSizeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int width = Integer.parseInt(widthField.getText());
				int height = Integer.parseInt(heightField.getText());
				setCanvasSize(width, height);
			} catch (NumberFormatException e1) {
				logger.info("Failed to set canvas size: invalid dimensions!");
			}
		}
	};

	private final DocumentListener sceneNameListener = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) {
			updateName(e);
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateName(e);
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			updateName(e);
		}
		private void updateName(DocumentEvent e) {
			try {
				Document d = e.getDocument();
				renderMan.scene().setName(d.getText(0, d.getLength()));
				updateTitle();
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	};

	private final DocumentListener sppTargetListener = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) {
			updateName(e);
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateName(e);
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			updateName(e);
		}
		private void updateName(DocumentEvent e) {
			try {
				Document d = e.getDocument();
				String value = d.getText(0, d.getLength());
				renderMan.scene().setTargetSPP(Integer.parseInt(value));
				updateTitle();
			} catch (NumberFormatException e1) {
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	};

	private final ActionListener saveSceneListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			sceneMan.saveScene(sceneNameField.getText());
		}
	};
	private final ActionListener saveFrameListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.saveFrame(RenderControls.this);
		}
	};
	private final ActionListener loadSceneListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			new SceneSelector(RenderControls.this, context);
		}
	};
	private final ActionListener loadSkymapListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			CenteredFileDialog fileDialog =
					new CenteredFileDialog(null, "Open Skymap", FileDialog.LOAD);
			fileDialog.setDirectory(System.getProperty("user.dir"));
			fileDialog.setFilenameFilter(
					new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".png")
									|| name.toLowerCase().endsWith(".jpg");
						}
					});
			fileDialog.setVisible(true);
			File selectedFile = fileDialog.getSelectedFile();
			if (selectedFile != null) {
				renderMan.scene().sky().loadSkyMap(selectedFile.getAbsolutePath());
			}
		}
	};
	private final ChangeListener skyRotationListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			double value = (double) (source.getValue() - source.getMinimum())
					/ (source.getMaximum() - source.getMinimum());
			double rotation = value * 2 * Math.PI;
			renderMan.scene().sky().setRotation(rotation);
			ProgramProperties.setProperty("skymapRotation", Double.toString(rotation));
		}
	};
	private final ActionListener projectionModeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Camera.ProjectionMode m = Camera.ProjectionMode.class.getEnumConstants()[projectionMode.getSelectedIndex()];
			renderMan.scene().camera().setProjectionMode(m);
			updateProjectionModeField();
			fov.update();
		}
	};
	private final ActionListener skyModeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	};
	private final ChangeListener exposureListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			double value = (double) (source.getValue() - source.getMinimum())
					/ (source.getMaximum() - source.getMinimum());
			double logMin = Math.log10(Scene.MIN_EXPOSURE);
			double logMax = Math.log10(Scene.MAX_EXPOSURE);
			double scale = logMax - logMin;
			renderMan.scene().setExposure(
					Math.pow(10, value * scale + logMin));
			updateExposureField();
		}
	};
	private final ActionListener exposureFieldListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField source = (JTextField) e.getSource();
			try {
				double value = numberFormat.parse(source.getText()).doubleValue();
				value = Math.max(value, Scene.MIN_EXPOSURE);
				value = Math.min(value, Scene.MAX_EXPOSURE);
				renderMan.scene().setExposure(value);
				updateExposureSlider();
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
		}
	};
	private final ChangeListener focalOffsetListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			double value = (double) (source.getValue() - source.getMinimum())
					/ (source.getMaximum() - source.getMinimum());
			double scale = Camera.MAX_SUBJECT_DISTANCE - Camera.MIN_SUBJECT_DISTANCE;
			renderMan.scene().camera().setSubjectDistance(value * scale + Camera.MIN_SUBJECT_DISTANCE);
			updateFocalOffsetField();
		}
	};
	private final ActionListener cameraPositionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Vector3d pos = new Vector3d(renderMan.scene().camera().getPosition());
			try {
				pos.x = numberFormat.parse(cameraX.getText()).doubleValue();
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			try {
				pos.y = numberFormat.parse(cameraY.getText()).doubleValue();
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			try {
				pos.z = numberFormat.parse(cameraZ.getText()).doubleValue();
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			renderMan.scene().camera().setPosition(pos);
			updateCameraPosition();
		}
	};
	private final ActionListener cameraDirectionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			double yaw = renderMan.scene().camera().getYaw();
			double pitch = renderMan.scene().camera().getPitch();
			try {
				double value = numberFormat.parse(cameraPitch.getText()).doubleValue();
				pitch = QuickMath.degToRad(value);
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			try {
				yaw = QuickMath.degToRad(numberFormat.parse(cameraYaw.getText()).doubleValue());
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			renderMan.scene().camera().setView(yaw, pitch);
			updateCameraDirection();
		}
	};
	private final ActionListener stillWaterListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setStillWater(stillWaterCB.isSelected());
		}
	};
	private final ActionListener clearWaterListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setClearWater(clearWaterCB.isSelected());
		}
	};
	private final ActionListener atmosphereListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setAtmosphereEnabled(source.isSelected());
		}
	};
	private final ActionListener volumetricFogListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setVolumetricFogEnabled(source.isSelected());
		}
	};
	private final ActionListener cloudsEnabledListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setCloudsEnabled(source.isSelected());
		}
	};
	private final ActionListener mirrorSkyListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().sky().setMirrored(source.isSelected());
		}
	};
	private final ActionListener biomeColorsCBListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setBiomeColorsEnabled(source.isSelected());
		}
	};
	private final ActionListener emittersListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setEmittersEnabled(enableEmitters.isSelected());
		}
	};
	private final ActionListener directLightListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setDirectLight(directLight.isSelected());
		}
	};
	private final ActionListener focalOffsetFieldListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField source = (JTextField) e.getSource();
			try {
				double value = numberFormat.parse(source.getText()).doubleValue();
				value = Math.max(value, Camera.MIN_SUBJECT_DISTANCE);
				renderMan.scene().camera().setSubjectDistance(value);
				updateFocalOffsetSlider();
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
		}
	};

	protected void updateWaterHeight() {
		int height = renderMan.scene().getWaterHeight();
		boolean waterWorld = height > 0;
		if (waterWorld) {
			waterHeightField.setText("" + height);
		}
		waterWorldCB.setSelected(height > 0);
		waterHeightField.setEnabled(height > 0);
	}

	protected void updateFocalOffsetField() {
		focalOffsetField.removeActionListener(focalOffsetFieldListener);
		focalOffsetField.setText(String.format("%.2f", renderMan.scene().camera().getSubjectDistance()));
		focalOffsetField.addActionListener(focalOffsetFieldListener);
	}

	protected void updateSkyRotation() {
		skyRotationSlider.removeChangeListener(skyRotationListener);
		skyRotationSlider.setValue((int) Math.round(
				100 * renderMan.scene().sky().getRotation() / (2 * Math.PI)));
		skyRotationSlider.addChangeListener(skyRotationListener);
	}

	protected void updateFocalOffsetSlider() {
		focalOffsetSlider.removeChangeListener(focalOffsetListener);
		double value = (renderMan.scene().camera().getSubjectDistance() - Camera.MIN_SUBJECT_DISTANCE)
				/ (Camera.MAX_SUBJECT_DISTANCE - Camera.MIN_SUBJECT_DISTANCE);
		double scale = focalOffsetSlider.getMaximum() - focalOffsetSlider.getMinimum();
		focalOffsetSlider.setValue((int) (value * scale + focalOffsetSlider.getMinimum()));
		focalOffsetSlider.addChangeListener(focalOffsetListener);
	}

	protected void updateProjectionModeField() {
		projectionMode.removeActionListener(projectionModeListener);
		projectionMode.setSelectedIndex( renderMan.scene().camera().getProjectionMode().ordinal() );
		projectionMode.addActionListener(projectionModeListener);
	}

	protected void updateSkyMode() {
		skyModeCB.removeActionListener(skyModeListener);
		// TODO
		skyModeCB.addActionListener(skyModeListener);
	}

	protected void updateExposureField() {
		exposureField.removeActionListener(exposureFieldListener);
		exposureField.setText(String.format("%.2f", renderMan.scene().getExposure()));
		exposureField.addActionListener(exposureFieldListener);
	}

	protected void updateExposureSlider() {
		exposureSlider.removeChangeListener(exposureListener);
		double logMin = Math.log10(Scene.MIN_EXPOSURE);
		double logMax = Math.log10(Scene.MAX_EXPOSURE);
		double value = (Math.log10(renderMan.scene().getExposure()) -
				logMin) / (logMax - logMin);
		double scale = exposureSlider.getMaximum() - exposureSlider.getMinimum();
		exposureSlider.setValue((int) (value * scale + exposureSlider.getMinimum()));
		exposureSlider.addChangeListener(exposureListener);
	}

	protected void updateWidthField() {
		widthField.setText("" + renderMan.scene().canvasWidth());
	}

	protected void updateHeightField() {
		heightField.setText("" + renderMan.scene().canvasHeight());
	}

	protected void updateSaveDumpsCheckBox() {
		saveDumpsCB.removeActionListener(saveDumpsListener);
		saveDumpsCB.setSelected(renderMan.scene().saveDumps());
		saveDumpsCB.addActionListener(saveDumpsListener);
	}

	protected void updateDumpFrequencyField() {
		dumpFrequency.removeActionListener(dumpFrequencyListener);
		dumpFrequency.setEnabled(renderMan.scene().saveDumps());
		int frequency = renderMan.scene().getDumpFrequency();
		dumpFrequency.setSelectedIndex(0);
		for (int i = 0; i < dumpFrequencies.length; ++i) {
			if (frequency == dumpFrequencies[i]) {
				dumpFrequency.setSelectedIndex(i);
				break;
			}
		}
		dumpFrequency.addActionListener(dumpFrequencyListener);
	}

	protected void updateSceneNameField() {
		sceneNameField.getDocument().removeDocumentListener(sceneNameListener);
		sceneNameField.setText(renderMan.scene().name());
		sceneNameField.getDocument().addDocumentListener(sceneNameListener);
	}

	protected void updateSPPTargetField() {
		sppTargetField.getDocument().removeDocumentListener(sppTargetListener);
		sppTargetField.setText("" + renderMan.scene().getTargetSPP());
		sppTargetField.getDocument().addDocumentListener(sppTargetListener);
	}

	protected void updatePostprocessCB() {
		postprocessCB.setSelectedIndex(renderMan.scene().getPostprocess().ordinal());
	}

	protected void updateCameraPosition() {
		cameraX.removeActionListener(cameraPositionListener);
		cameraY.removeActionListener(cameraPositionListener);
		cameraZ.removeActionListener(cameraPositionListener);

		Vector3d pos = renderMan.scene().camera().getPosition();
		cameraX.setText(decimalFormat.format(pos.x));
		cameraY.setText(decimalFormat.format(pos.y));
		cameraZ.setText(decimalFormat.format(pos.z));

		cameraX.addActionListener(cameraPositionListener);
		cameraY.addActionListener(cameraPositionListener);
		cameraZ.addActionListener(cameraPositionListener);
	}

	protected void updateCameraDirection() {
		cameraPitch.removeActionListener(cameraDirectionListener);
		cameraYaw.removeActionListener(cameraDirectionListener);

		double pitch = QuickMath.radToDeg(renderMan.scene().camera().getPitch());
		cameraPitch.setText(decimalFormat.format(pitch));
		cameraYaw.setText(decimalFormat.format(
				QuickMath.radToDeg(renderMan.scene().camera().getYaw())));

		cameraPitch.addActionListener(cameraDirectionListener);
		cameraYaw.addActionListener(cameraDirectionListener);
	}

	/**
	 * Load the scene with the given name
	 * @param sceneName The name of the scene to load
	 */
	public void loadScene(String sceneName) {
		sceneMan.loadScene(sceneName);
	}

	/**
	 * Called when the current scene has been saved
	 */
	@Override
	public void sceneSaved() {
		updateTitle();
	}

	@Override
	public void onStrafeLeft() {
        renderMan.scene().camera().strafeLeft(
        		chunky.getShiftModifier() ? .1 : 1);
		updateCameraPosition();
	}

	@Override
	public void onStrafeRight() {
        renderMan.scene().camera().strafeRight(
        		chunky.getShiftModifier() ? .1 : 1);
		updateCameraPosition();
	}

	@Override
	public void onMoveForward() {
        renderMan.scene().camera().moveForward(
        		chunky.getShiftModifier() ? .1 : 1);
		updateCameraPosition();
	}

	@Override
	public void onMoveBackward() {
        renderMan.scene().camera().moveBackward(
        		chunky.getShiftModifier() ? .1 : 1);
		updateCameraPosition();
	}

	@Override
	public void onMoveForwardFar() {
	    renderMan.scene().camera().moveForward(100);
		updateCameraPosition();
	}

	@Override
	public void onMoveBackwardFar() {
        renderMan.scene().camera().moveBackward(100);
		updateCameraPosition();
	}

	@Override
	public void onMoveUp() {
        renderMan.scene().camera().moveUp(
        		chunky.getShiftModifier() ? .1 : 1);
		updateCameraPosition();
	}

	@Override
	public void onMoveDown() {
        renderMan.scene().camera().moveDown(
        		chunky.getShiftModifier() ? .1 : 1);
		updateCameraPosition();
	}

	@Override
	public void onMouseDragged(int dx, int dy) {
        renderMan.scene().camera().rotateView(
                - (Math.PI / 250) * dx,
                (Math.PI / 250) * dy);
        updateCameraDirection();
	}

	/**
	 * Set the name of the current scene
	 * @param sceneName
	 */
	public void setSceneName(String sceneName) {
		renderMan.scene().setName(sceneName);
		sceneNameField.setText(renderMan.scene().name());
		updateTitle();
	}

	/**
	 * Load the given chunks.
	 * @param world
	 * @param chunks
	 */
	public void loadChunks(World world, Collection<ChunkPosition> chunks) {
		sceneMan.loadChunks(world, chunks);
	}

	/**
	 * Update the Show/Hide 3D view button.
	 * @param visible
	 */
	@Override
	public void setViewVisible(boolean visible) {
		if (visible) {
			showPreviewBtn.setText("Hide Preview");
			showPreviewBtn.setToolTipText("Hide the preview window");
		} else {
			showPreviewBtn.setText("Show Preview");
			showPreviewBtn.setToolTipText("Show the preview window");
		}
	}

	protected void setCanvasSize(int width, int height) {
		renderMan.scene().setCanvasSize(width, height);
		int canvasWidth = renderMan.scene().canvasWidth();
		int canvasHeight = renderMan.scene().canvasHeight();
		widthField.setText("" + canvasWidth);
		heightField.setText("" + canvasHeight);
		view.setCanvasSize(canvasWidth, canvasHeight);
	}

	/**
	 * Method to notify the render controls dialog that a scene has been loaded.
	 * Causes canvas size to be updated.
	 */
	@Override
	public synchronized void sceneLoaded() {
		dof.update();
		fov.update();
		updateFocalOffsetField();
		updateProjectionModeField();
		updateSkyMode();
		updateFocalOffsetSlider();
		updateWidthField();
		updateHeightField();
		emitterIntensity.update();
		sunIntensity.update();
		sunAzimuth.update();
		sunAltitude.update();
		updateStillWater();
		updateClearWater();
		updateSkyRotation();
		updateMirroSkyCB();
		updateBiomeColorsCB();
		updateAtmosphereCheckBox();
		updateVolumetricFogCheckBox();
		updateCloudsEnabledCheckBox();
		updateTitle();
		updateExposureField();
		updateExposureSlider();
		updateSaveDumpsCheckBox();
		updateDumpFrequencyField();
		updateSPPTargetField();
		updateSceneNameField();
		updatePostprocessCB();
		cloudHeight.update();
		rayDepth.update();
		updateCameraDirection();
		updateCameraPosition();
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		directLight.setSelected(renderMan.scene().getDirectLight());
		startRenderBtn.setText("RESUME");
		stopRenderBtn.setEnabled(true);

		show3DView();
	}

	/**
	 * Update render time status label
	 * @param time Total render time in milliseconds
	 */
	@Override
	public void setRenderTime(long time) {
		if (renderTimeLbl == null)
			return;

		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / 60000) % 60);
		int hours = (int) (time / 3600000);
		renderTimeLbl.setText(String.format(
				"Render time: %d hours, %d minutes, %d seconds",
				hours, minutes, seconds));
	}

	/**
	 * Update samples per second status label
	 * @param sps Samples per second
	 */
	@Override
	public void setSamplesPerSecond(int sps) {
		if (samplesPerSecondLbl == null)
			return;

		samplesPerSecondLbl.setText("SPS: " + decimalFormat.format(sps));
	}

	/**
	 * Update SPP status label
	 * @param spp Samples per pixel
	 */
	@Override
	public void setSPP(int spp) {
		if (sppLbl == null)
			return;

		sppLbl.setText("SPP: " + decimalFormat.format(spp));
	}

	@Override
	public void setProgress(String task, int done, int start, int target) {
		if (progressBar != null && progressLbl != null && etaLbl != null) {
			progressLbl.setText(String.format("%s: %s of %s",
					task, decimalFormat.format(done), decimalFormat.format(target)));
			progressBar.setMinimum(start);
			progressBar.setMaximum(target);
			progressBar.setValue(Math.min(target, done));
			etaLbl.setText("ETA: N/A");
		}
	}

	@Override
	public void setProgress(String task, int done, int start, int target, String eta) {
		if (progressBar != null && progressLbl != null && etaLbl != null) {
			setProgress(task, done, start, target);
			etaLbl.setText("ETA: " + eta);
		}
	}

	/**
	 * Show the 3D view window
	 */
	public void show3DView() {
		view.setCanvasSize(renderMan.scene().canvasWidth(),
				renderMan.scene().canvasHeight());
		view.displayRightOf(this);
	}

	@Override
	public void onZoom(int diff) {
		Camera camera = renderMan.scene().camera();
		double value = renderMan.scene().camera().getFoV();
		double scale = camera.getMaxFoV() - camera.getMinFoV();
		value = value + diff * scale/20;
		renderMan.scene().camera().setFoV(value);
		fov.update();
	}

	/**
	 * @return The render context for this Render Controls dialog
	 */
	public RenderContext getContext() {
		return context;
	}

	@Override
	public void renderStateChanged(boolean pathTrace, boolean paused) {
		if (pathTrace) {
			if (paused) {
				startRenderBtn.setText("RESUME");
			} else {
				startRenderBtn.setText("PAUSE");
			}
			stopRenderBtn.setEnabled(true);
		} else {
			startRenderBtn.setText("START");
			stopRenderBtn.setEnabled(false);
		}
	}

	@Override
	public void chunksLoaded() {
		updateCameraPosition();
		show3DView();
	}

	@Override
	public void renderJobFinished(long time, int sps) {
	}
}
