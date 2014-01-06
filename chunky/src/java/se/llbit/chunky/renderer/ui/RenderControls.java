/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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
import java.util.HashSet;
import java.util.Set;

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
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.RenderConstants;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.Camera.ProjectionMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sky.SkyMode;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;
import se.llbit.ui.Adjuster;

/**
 * Render Controls dialog.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class RenderControls extends JDialog implements ViewListener,
	RenderStatusListener {

	private static final Logger logger =
			Logger.getLogger(RenderControls.class);

	private static final int[] dumpFrequencies = { 50, 100, 500, 1000, 5000 };

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
	private final JButton loadSkymapBtn = new JButton();
	private final JCheckBox mirrorSkyCB = new JCheckBox();
	private final JTextField widthField = new JTextField();
	private final JTextField heightField = new JTextField();
	private final JComboBox projectionMode = new JComboBox();
	private final JButton startRenderBtn = new JButton();
	private final JCheckBox enableEmitters = new JCheckBox();
	private final JCheckBox directLight = new JCheckBox();
	private final JButton saveSceneBtn = new JButton();
	private final JButton loadSceneBtn = new JButton();
	private final JButton openSceneDirBtn = new JButton();
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
	private final JCheckBox autoLock = new JCheckBox();
	private final JButton lockBtn = new JButton();
	private final RenderContext context;
	private final JButton showPreviewBtn = new JButton();
	private final JLabel renderTimeLbl = new JLabel();
	private final JLabel samplesPerSecondLbl = new JLabel();
	private final JLabel sppLbl = new JLabel();
	private final JProgressBar progressBar = new JProgressBar();
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
	private final JComboBox dumpFrequencyCB = new JComboBox();
	private final JCheckBox saveSnapshotsCB = new JCheckBox("Save snapshot for each dump");
	private final JLabel dumpFrequencyLbl = new JLabel(" frames");
	private final JTextField sppTargetField = new JTextField();
	private final JTextField cameraX = new JTextField();
	private final JTextField cameraY = new JTextField();
	private final JTextField cameraZ = new JTextField();
	private final JTextField cameraYaw = new JTextField();
	private final JTextField cameraPitch = new JTextField();
	private final JTextField cameraRoll = new JTextField();
	private final JButton mergeDumpBtn = new JButton("Merge Render Dump");

	private boolean controlsLocked = false;

	private JPanel generalPane;
	private JPanel cameraPane;
	private JPanel lightingPane;
	private JPanel skyPane;
	private JPanel advancedPane;

	private final Adjuster numThreads = new Adjuster(
			"Render threads",
			"Number of rendering threads",
			RenderConstants.NUM_RENDER_THREADS_MIN,
			20) {
		@Override
		public void valueChanged(double newValue) {
			int value = (int) newValue;
			PersistentSettings.setNumThreads(value);
			renderMan.setNumThreads(value);
		}

		@Override
		public void update() {
			set(PersistentSettings.getNumThreads());
		}
	};

	private final Adjuster cpuLoad = new Adjuster(
			"CPU load",
			"CPU load percentage",
			1, 100) {
		@Override
		public void valueChanged(double newValue) {
			int value = (int) newValue;
			PersistentSettings.setCPULoad(value);
			renderMan.setCPULoad(value);
		}

		@Override
		public void update() {
			set(PersistentSettings.getCPULoad());
		}
	};

	private final Adjuster rayDepth = new Adjuster(
			"Ray depth",
			"Sets the recursive ray depth",
			1, 25) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setRayDepth((int) newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getRayDepth());
		}
	};
	private final Adjuster cloudHeight = new Adjuster(
			"Cloud Height",
			"Height of the cloud layer", -128, 512) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setCloudHeight((int) newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getCloudHeight());
		}
	};
	private final Adjuster emitterIntensity = new Adjuster(
			"Emitter intensity",
			"Light intensity modifier for emitters",
			Scene.MIN_EMITTER_INTENSITY,
			Scene.MAX_EMITTER_INTENSITY) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setEmitterIntensity(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getEmitterIntensity());
		}
	};
	private final Adjuster sunIntensity = new Adjuster(
			"Sun intensity",
			"Light intensity modifier for sun",
			Sun.MIN_INTENSITY,
			Sun.MAX_INTENSITY) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sun().setIntensity(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sun().getIntensity());
		}
	};
	private final Adjuster sunAzimuth = new Adjuster(
			"Sun azimuth",
			"The angle towards the sun from north",
			0.0, 360.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sun().setAzimuth(QuickMath.degToRad(newValue));
		}

		@Override
		public void update() {
			set(QuickMath.radToDeg(renderMan.scene().sun().getAzimuth()));
		}
	};
	private final Adjuster sunAltitude = new Adjuster(
			"Sun altitude",
			"Angle of the sun above the horizon",
			0.0, 90.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sun().setAltitude(QuickMath.degToRad(newValue));
		}

		@Override
		public void update() {
			set(QuickMath.radToDeg(renderMan.scene().sun().getAltitude()));
		}
	};
	private final Adjuster fov = new Adjuster(
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
			set(camera.getFoV(), camera.getMinFoV(), camera.getMaxFoV());
		}
	};
	private Adjuster dof;
	private final Adjuster subjectDistance = new Adjuster(
			"Subject Distance",
			"Distance to focal plane",
			Camera.MIN_SUBJECT_DISTANCE,
			Camera.MAX_SUBJECT_DISTANCE) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().camera().setSubjectDistance(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().camera().getSubjectDistance());
		}
	};
	private final Adjuster exposure = new Adjuster(
			"exposure",
			"exposure",
			Scene.MIN_EXPOSURE,
			Scene.MAX_EXPOSURE) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setExposure(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getExposure());
		}
	};

	Set<Component> safeComponents = new HashSet<Component>();

	{
		// initialize safe component set
		safeComponents.add(saveSceneBtn);
		safeComponents.add(sceneNameField);
		safeComponents.add(sceneNameLbl);
		safeComponents.add(openSceneDirBtn);
		safeComponents.add(rayDepth.getLabel());
		safeComponents.add(rayDepth.getSlider());
		safeComponents.add(rayDepth.getField());
		safeComponents.add(mergeDumpBtn);
		safeComponents.add(saveDumpsCB);
		safeComponents.add(dumpFrequencyCB);
		safeComponents.add(dumpFrequencyLbl);
		safeComponents.add(numThreads.getLabel());
		safeComponents.add(numThreads.getSlider());
		safeComponents.add(numThreads.getField());
		safeComponents.add(cpuLoad.getLabel());
		safeComponents.add(cpuLoad.getSlider());
		safeComponents.add(cpuLoad.getField());
	}

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

		buildUI();

		renderMan.start();

		view.setRenderer(renderMan);

		sceneMan = new SceneManager(renderMan);
		sceneMan.start();
	}

	private void buildUI() {
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

		tabbedPane.addTab("General", generalPane = buildGeneralPane());
		tabbedPane.addTab("Lighting", lightingPane = buildLightingPane());
		tabbedPane.addTab("Sky", skyPane = buildSkyPane());
		tabbedPane.addTab("Camera", cameraPane = buildCameraPane());
		tabbedPane.addTab("Post-processing", buildPostProcessingPane());
		tabbedPane.addTab("Advanced", advancedPane = buildAdvancedPane());
		tabbedPane.addTab("Help", buildHelpPane());

		JLabel sppTargetLbl = new JLabel("SPP Target: ");
		sppTargetLbl.setToolTipText("The render will be paused at this SPP count");

		JButton setDefaultBtn = new JButton("Set Default");
		setDefaultBtn.setToolTipText("Make the current SPP target the default");
		setDefaultBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PersistentSettings.setSppTargetDefault(renderMan.scene().getTargetSPP());
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
				if (view.isViewVisible()) {
					view.hideView();
				} else {
					showPreviewWindow();
				}
			}
		});

		startRenderBtn.setText("START");
		startRenderBtn.setIcon(Icon.play.createIcon());
		startRenderBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!renderMan.scene().pathTrace()) {
					renderMan.scene().startRender();
				} else {
					if (renderMan.scene().isPaused()) {
						renderMan.scene().resumeRender();
					} else {
						renderMan.scene().pauseRender();
					}
				}
				stopRenderBtn.setEnabled(true);
			}
		});

		autoLock.setText("auto lock");
		autoLock.setSelected(PersistentSettings.getAutoLock());
		autoLock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				PersistentSettings.setAutoLock(source.isSelected());
			}
		});

		lockBtn.setIcon(Icon.lock.createIcon());
		lockBtn.setToolTipText("Lock or unlock the render controls");
		lockBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controlsLocked) {
					unlockControls();
				} else {
					lockControls();
				}
			}
		});

		stopRenderBtn.setText("RESET");
		stopRenderBtn.setIcon(Icon.stop.createIcon());
		stopRenderBtn.setToolTipText("<html>Warning: this will discard the " +
				"current rendered image!<br>Make sure to save your image " +
				"before stopping the renderer!");
		stopRenderBtn.setEnabled(false);
		stopRenderBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().haltRender();
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

		progressLbl.setText("Progress:");

		etaLbl.setText("ETA:");

		sceneNameLbl.setText("Scene name: ");
		sceneNameField.setColumns(15);
		AbstractDocument document = (AbstractDocument) sceneNameField.getDocument();
		document.setDocumentFilter(new SceneNameFilter());
		document.addDocumentListener(sceneNameListener);
		sceneNameField.addActionListener(sceneNameActionListener);
		updateSceneNameField();

		saveSceneBtn.setText("Save");
		saveSceneBtn.setIcon(Icon.disk.createIcon());
		saveSceneBtn.addActionListener(saveSceneListener);

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(sceneNameLbl)
					.addComponent(sceneNameField)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(saveSceneBtn))
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
					.addComponent(autoLock)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lockBtn)
				)
				.addComponent(saveFrameBtn)
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
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(sceneNameLbl)
				.addComponent(sceneNameField)
				.addComponent(saveSceneBtn))
			.addPreferredGap(ComponentPlacement.UNRELATED)
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
				.addComponent(autoLock)
				.addComponent(lockBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(saveFrameBtn)
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

	private JPanel buildAdvancedPane() {
		rayDepth.update();

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();
		JSeparator sep3 = new JSeparator();

		numThreads.setClampMax(false);
		numThreads.update();

		cpuLoad.update();

		JLabel waterWorldLbl = new JLabel(
				"Note: All chunks will be reloaded after changing the water world options!");
		JLabel waterHeightLbl = new JLabel("Water height: ");
		waterHeightField.setColumns(5);
		waterHeightField.setText("" + World.SEA_LEVEL);
		waterHeightField.setEnabled(renderMan.scene().getWaterHeight() != 0);
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
				fileDialog.setDirectory(PersistentSettings.
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
				.addGroup(numThreads.horizontalGroup(layout))
				.addGroup(cpuLoad.horizontalGroup(layout))
				.addComponent(sep1)
				.addGroup(rayDepth.horizontalGroup(layout))
				.addComponent(sep2)
				.addComponent(waterWorldLbl)
				.addComponent(waterWorldCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(waterHeightLbl)
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(waterHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(sep3)
				.addComponent(mergeDumpBtn)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(numThreads.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cpuLoad.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(rayDepth.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
			.addComponent(sep3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(mergeDumpBtn)
			.addContainerGap()
		);
		return panel;
	}

	private JPanel buildPostProcessingPane() {
		exposure.setLogarithmicMode();
		exposure.update();

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
				.addGroup(exposure.horizontalGroup(layout))
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
			.addGroup(exposure.verticalGroup(layout))
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

	private JPanel buildGeneralPane() {
		JLabel widthLbl = new JLabel("Canvas width: ");
		JLabel heightLbl = new JLabel("Canvas height: ");
		JLabel canvasSizeLbl = new JLabel("<html>Note: Actual image size may not be<br>the same as the window size!");

		widthField.setColumns(10);
		widthField.addActionListener(canvasSizeListener);
		heightField.setColumns(10);
		heightField.addActionListener(canvasSizeListener);

		updateWidthField();
		updateHeightField();

		loadSceneBtn.setText("Load Scene");
		loadSceneBtn.addActionListener(loadSceneListener);

		JButton loadSelectedChunksBtn = new JButton("Load Selected Chunks");
		loadSelectedChunksBtn.setToolTipText("Load the chunks that are currently selected in the map view");
		loadSelectedChunksBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneMan.loadChunks(chunky.getWorld(), chunky.getSelectedChunks());
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
				PersistentSettings.set3DCanvasSize(
						renderMan.scene().canvasWidth(),
						renderMan.scene().canvasHeight());
			}
		});

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();

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

		String[] frequencyStrings = new String[dumpFrequencies.length];
		for (int i = 0; i < dumpFrequencies.length; ++i) {
			frequencyStrings[i] = Integer.toString(dumpFrequencies[i]);
		}
		dumpFrequencyCB.setModel(new DefaultComboBoxModel(frequencyStrings));
		dumpFrequencyCB.setEditable(true);
		dumpFrequencyCB.addActionListener(dumpFrequencyListener);
		updateDumpFrequencyField();

		saveSnapshotsCB.addActionListener(saveSnapshotListener);
		updateSaveSnapshotCheckBox();

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
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
					.addGroup(layout.createParallelGroup()
						.addComponent(widthLbl)
						.addComponent(heightLbl)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(widthField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(heightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(canvasSizeLbl)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(setCanvasSizeBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(halveCanvasSizeBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(doubleCanvasSizeBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(makeDefaultBtn)
				)
				.addComponent(sep2)
				.addComponent(stillWaterCB)
				.addComponent(clearWaterCB)
				.addComponent(biomeColorsCB)
				.addGroup(layout.createSequentialGroup()
					.addComponent(saveDumpsCB)
					.addComponent(dumpFrequencyCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(dumpFrequencyLbl)
					.addGap(0, 0, Short.MAX_VALUE)
				)
				.addComponent(saveSnapshotsCB)
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
			.addGroup(layout.createParallelGroup()
				.addComponent(canvasSizeLbl)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(widthLbl)
						.addComponent(widthField)
					)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(heightLbl)
						.addComponent(heightField)
					)
				)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(setCanvasSizeBtn)
				.addComponent(halveCanvasSizeBtn)
				.addComponent(doubleCanvasSizeBtn)
				.addComponent(makeDefaultBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(stillWaterCB)
			.addComponent(clearWaterCB)
			.addComponent(biomeColorsCB)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(saveDumpsCB)
				.addComponent(dumpFrequencyCB)
				.addComponent(dumpFrequencyLbl)
			)
			.addComponent(saveSnapshotsCB)
			.addContainerGap()
		);
		return panel;
	}

	private JPanel buildLightingPane() {

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

		emitterIntensity.setLogarithmicMode();
		emitterIntensity.update();

		sunIntensity.setLogarithmicMode();
		sunIntensity.update();

		sunAzimuth.update();

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

	private JPanel buildSkyPane() {

		JLabel skyModeLbl = new JLabel("Sky Mode:");
		skyModeCB.setModel(new DefaultComboBoxModel(Sky.SkyMode.values()));
		skyModeCB.addActionListener(skyModeListener);
		// TODO implement sky modes
		skyModeLbl.setVisible(false);
		skyModeCB.setVisible(false);
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

		cloudHeight.update();

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(skyModeLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(skyModeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
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
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(skyModeLbl)
				.addComponent(skyModeCB)
			)
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

	private JPanel buildCameraPane() {
		JLabel projectionModeLbl = new JLabel("Projection");

		fov.setClampMax(false);
		fov.update();

		dof = new DoFAdjuster(renderMan);
		dof.update();

		subjectDistance.setLogarithmicMode();
		subjectDistance.update();

		ProjectionMode[] projectionModes = ProjectionMode.values();
		projectionMode.setModel(new DefaultComboBoxModel(projectionModes));
		projectionMode.addActionListener(projectionModeListener);
		updateProjectionMode();

		JButton autoFocusBtn = new JButton("Autofocus");
		autoFocusBtn.setToolTipText("Focuses on the object right in the center, under the crosshairs");
		autoFocusBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().autoFocus();
				dof.update();
				subjectDistance.update();
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
		cameraRoll.setColumns(10);
		cameraRoll.setHorizontalAlignment(JTextField.RIGHT);
		cameraRoll.addActionListener(cameraDirectionListener);
		updateCameraDirection();

		JLabel lookAtLbl = new JLabel("Skybox views:");
		JButton xposBtn = new JButton("East");
		xposBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = renderMan.scene().camera();
				camera.setFoV(90);
				camera.setView(Math.PI, -Math.PI/2, 0);
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
				camera.setView(0, -Math.PI/2, 0);
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
				camera.setView(-Math.PI/2, Math.PI, 0);
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
				camera.setView(-Math.PI/2, 0, 0);
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
				camera.setView(Math.PI/2, -Math.PI/2, 0);
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
				camera.setView(-Math.PI/2, -Math.PI/2, 0);
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
				camera.setView(-Math.PI/4, -Math.PI/4, 0);
				camera.setProjectionMode(ProjectionMode.PARALLEL);
				updateProjectionMode();
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
				camera.setView(-3*Math.PI/4, -Math.PI/4, 0);
				camera.setProjectionMode(ProjectionMode.PARALLEL);
				updateProjectionMode();
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
				camera.setView(-5*Math.PI/4, -Math.PI/4, 0);
				camera.setProjectionMode(ProjectionMode.PARALLEL);
				updateProjectionMode();
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
				camera.setView(-7*Math.PI/4, -Math.PI/4, 0);
				camera.setProjectionMode(ProjectionMode.PARALLEL);
				updateProjectionMode();
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
						.addComponent(cameraRoll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
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
						.addComponent(subjectDistance.getLabel()))
					.addGroup(layout.createParallelGroup()
						.addComponent(projectionMode)
						.addComponent(fov.getSlider())
						.addComponent(dof.getSlider())
						.addComponent(subjectDistance.getSlider()))
					.addGroup(layout.createParallelGroup()
						.addComponent(fov.getField())
						.addComponent(dof.getField())
						.addComponent(subjectDistance.getField())))
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
				.addComponent(cameraRoll)
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
			.addGroup(subjectDistance.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(autoFocusBtn)
			.addContainerGap()
		);
		return panel;
	}

	private JPanel buildHelpPane() {
		JLabel helpLbl = new JLabel(
			"<html>Render Preview Controls:<br>" +
			"<b>W</b> move camera forward<br>" +
			"<b>S</b> move camera backward<br>" +
			"<b>A</b> strafe camera left<br>" +
			"<b>D</b> strafe camera right<br>" +
			"<b>R</b> move camera up<br>" +
			"<b>F</b> move camera down<br>" +
			"<b>U</b> toggle fullscreen mode<br>" +
			"<b>K</b> move camera forward x100<br>" +
			"<b>J</b> move camera backward x100<br>" +
			"<br>" +
			"Holding <b>SHIFT</b> makes the basic movement keys so move 1/10th of the normal distance."
		);

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(helpLbl)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(helpLbl)
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

	private final ActionListener sceneNameActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField source = (JTextField) e.getSource();
			renderMan.scene().setName(source.getText());
			updateTitle();
			sceneMan.saveScene();
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

	private final ActionListener saveSnapshotListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setSaveSnapshots(source.isSelected());
		}
	};
	private final ActionListener saveSceneListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			renderMan.scene().setName(sceneNameField.getText());
			sceneMan.saveScene();
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
		}
	};
	private final ActionListener projectionModeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox) e.getSource();
			Object selected = source.getSelectedItem();
			if (selected != null) {
				renderMan.scene().camera().setProjectionMode(
						(ProjectionMode) selected);
				updateProjectionMode();
				fov.update();
			}
		}
	};
	private final ActionListener skyModeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox) e.getSource();
			renderMan.scene().sky().setSkyMode((SkyMode) source.getSelectedItem());
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
			double roll = renderMan.scene().camera().getRoll();
			try {
				double value = numberFormat.parse(cameraPitch.getText()).doubleValue();
				pitch = QuickMath.degToRad(value);
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			try {
				double value = numberFormat.parse(cameraYaw.getText()).doubleValue();
				yaw = QuickMath.degToRad(value);
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			try {
				double value = numberFormat.parse(cameraRoll.getText()).doubleValue();
				roll = QuickMath.degToRad(value);
			} catch (NumberFormatException ex) {
			} catch (ParseException ex) {
			}
			renderMan.scene().camera().setView(yaw, pitch, roll);
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

	protected void updateWaterHeight() {
		int height = renderMan.scene().getWaterHeight();
		boolean waterWorld = height > 0;
		if (waterWorld) {
			waterHeightField.setText("" + height);
		}
		waterWorldCB.setSelected(height > 0);
		waterHeightField.setEnabled(height > 0);
	}

	protected void updateSkyRotation() {
		skyRotationSlider.removeChangeListener(skyRotationListener);
		skyRotationSlider.setValue((int) FastMath.round(
				100 * renderMan.scene().sky().getRotation() / (2 * Math.PI)));
		skyRotationSlider.addChangeListener(skyRotationListener);
	}

	protected void updateProjectionMode() {
		projectionMode.removeActionListener(projectionModeListener);
		ProjectionMode mode = renderMan.scene().camera().getProjectionMode();
		projectionMode.setSelectedItem(mode);
		projectionMode.addActionListener(projectionModeListener);
	}

	protected void updateSkyMode() {
		skyModeCB.removeActionListener(skyModeListener);
		skyModeCB.setSelectedItem(renderMan.scene().sky().getSkyMode());
		skyModeCB.addActionListener(skyModeListener);
	}

	protected void updateWidthField() {
		widthField.setText("" + renderMan.scene().canvasWidth());
	}

	protected void updateHeightField() {
		heightField.setText("" + renderMan.scene().canvasHeight());
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

	protected void updateSceneNameField() {
		sceneNameField.getDocument().removeDocumentListener(sceneNameListener);
		sceneNameField.removeActionListener(sceneNameActionListener);
		sceneNameField.setText(renderMan.scene().name());
		sceneNameField.getDocument().addDocumentListener(sceneNameListener);
		sceneNameField.addActionListener(sceneNameActionListener);
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
		cameraRoll.removeActionListener(cameraDirectionListener);
		cameraPitch.removeActionListener(cameraDirectionListener);
		cameraYaw.removeActionListener(cameraDirectionListener);

		double roll = QuickMath.radToDeg(renderMan.scene().camera().getRoll());
		double pitch = QuickMath.radToDeg(renderMan.scene().camera().getPitch());
		double yaw = QuickMath.radToDeg(renderMan.scene().camera().getYaw());

		cameraRoll.setText(decimalFormat.format(roll));
		cameraPitch.setText(decimalFormat.format(pitch));
		cameraYaw.setText(decimalFormat.format(yaw));

		cameraRoll.addActionListener(cameraDirectionListener);
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
		if (!controlsLocked) {
	        renderMan.scene().camera().strafeLeft(
	        		chunky.getShiftModifier() ? .1 : 1);
			updateCameraPosition();
		}
	}

	@Override
	public void onStrafeRight() {
		if (!controlsLocked) {
	        renderMan.scene().camera().strafeRight(
	        		chunky.getShiftModifier() ? .1 : 1);
			updateCameraPosition();
		}
	}

	@Override
	public void onMoveForward() {
		if (!controlsLocked) {
	        renderMan.scene().camera().moveForward(
	        		chunky.getShiftModifier() ? .1 : 1);
			updateCameraPosition();
		}
	}

	@Override
	public void onMoveBackward() {
		if (!controlsLocked) {
	        renderMan.scene().camera().moveBackward(
	        		chunky.getShiftModifier() ? .1 : 1);
			updateCameraPosition();
		}
	}

	@Override
	public void onMoveForwardFar() {
		if (!controlsLocked) {
		    renderMan.scene().camera().moveForward(100);
			updateCameraPosition();
		}
	}

	@Override
	public void onMoveBackwardFar() {
		if (!controlsLocked) {
	        renderMan.scene().camera().moveBackward(100);
			updateCameraPosition();
		}
	}

	@Override
	public void onMoveUp() {
		if (!controlsLocked) {
	        renderMan.scene().camera().moveUp(
	        		chunky.getShiftModifier() ? .1 : 1);
			updateCameraPosition();
		}
	}

	@Override
	public void onMoveDown() {
		if (!controlsLocked) {
	        renderMan.scene().camera().moveDown(
	        		chunky.getShiftModifier() ? .1 : 1);
			updateCameraPosition();
		}
	}

	@Override
	public void onMouseDragged(int dx, int dy) {
		if (!controlsLocked) {
	        renderMan.scene().camera().rotateView(
	                - (Math.PI / 250) * dx,
	                (Math.PI / 250) * dy);
	        updateCameraDirection();
		}
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
	 * Load the given chunks and center the camera.
	 * @param world
	 * @param chunks
	 */
	public void loadFreshChunks(World world, Collection<ChunkPosition> chunks) {
		sceneMan.loadFreshChunks(world, chunks);
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
		subjectDistance.update();
		updateProjectionMode();
		updateSkyMode();
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
		exposure.update();
		updateSaveDumpsCheckBox();
		updateSaveSnapshotCheckBox();
		updateDumpFrequencyField();
		updateSPPTargetField();
		updateSceneNameField();
		updatePostprocessCB();
		cloudHeight.update();
		rayDepth.update();
		updateWaterHeight();
		updateCameraDirection();
		updateCameraPosition();
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		directLight.setSelected(renderMan.scene().getDirectLight());
		stopRenderBtn.setEnabled(true);

		showPreviewWindow();
	}

	/**
	 * Make sure the preview window is visible
	 */
	public void showPreviewWindow() {
		view.showView(renderMan.scene().canvasWidth(),
				renderMan.scene().canvasHeight(),
				this);
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

	@Override
	public void taskAborted(String task) {
		// TODO add abort notice
		etaLbl.setText("ETA: N/A");
	}

	@Override
	public void taskFailed(String task) {
		// TODO add abort notice
		etaLbl.setText("ETA: N/A");
	}

	@Override
	public void onZoom(int diff) {
		if (!controlsLocked) {
			Camera camera = renderMan.scene().camera();
			double value = renderMan.scene().camera().getFoV();
			double scale = camera.getMaxFoV() - camera.getMinFoV();
			value = value + diff * scale/20;
			renderMan.scene().camera().setFoV(value);
			fov.update();
		}
	}

	/**
	 * @return The render context for this Render Controls dialog
	 */
	public RenderContext getContext() {
		return context;
	}

	@Override
	public void renderStateChanged(boolean pathTrace, boolean paused) {
		boolean lock = false;
		if (pathTrace) {
			lock = true;
			if (paused) {
				startRenderBtn.setText("RESUME");
				startRenderBtn.setIcon(Icon.play.createIcon());
			} else {
				startRenderBtn.setText("PAUSE");
				startRenderBtn.setIcon(Icon.pause.createIcon());
			}
			stopRenderBtn.setEnabled(true);
			stopRenderBtn.setForeground(Color.red);
		} else {
			startRenderBtn.setText("START");
			startRenderBtn.setIcon(Icon.play.createIcon());
			stopRenderBtn.setEnabled(false);
			stopRenderBtn.setForeground(Color.black);
		}
		if (lock && autoLock.isSelected()) {
			lockControls();
		} else if (!lock) {
			unlockControls();
		}
	}

	private void lockControls() {
		lockPane(generalPane);
		lockPane(cameraPane);
		lockPane(skyPane);
		lockPane(lightingPane);
		lockPane(advancedPane);
		lockBtn.setIcon(Icon.key.createIcon());
		controlsLocked = true;
	}

	private void unlockControls() {
		unlockPane(generalPane);
		unlockPane(cameraPane);
		unlockPane(skyPane);
		unlockPane(lightingPane);
		unlockPane(advancedPane);
		lockBtn.setIcon(Icon.lock.createIcon());
		controlsLocked = false;
	}

	private void lockPane(JPanel pane) {
		for (Component component: pane.getComponents()) {
			if (!safeComponents.contains(component)) {
				component.setEnabled(false);
			}
		}
	}

	private void unlockPane(JPanel pane) {
		for (Component component: pane.getComponents()) {
			if (!safeComponents.contains(component)) {
				component.setEnabled(true);
			}
		}
	}

	@Override
	public void chunksLoaded() {
		updateCameraPosition();
		showPreviewWindow();
	}

	@Override
	public void renderJobFinished(long time, int sps) {
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
