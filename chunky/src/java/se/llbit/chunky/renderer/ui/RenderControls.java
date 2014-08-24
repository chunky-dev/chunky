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
import java.awt.Dimension;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import se.llbit.chunky.renderer.scene.CameraPreset;
import se.llbit.chunky.renderer.scene.ProjectionMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sky.SkyMode;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;
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

	private final JSlider skymapRotationSlider = new JSlider();
	private final JSlider lightProbeRotationSlider = new JSlider();
	private final JSlider skyboxRotationSlider = new JSlider();
	private final JButton loadSkymapBtn = new JButton();
	private final JButton loadLightProbeBtn = new JButton();
	private final JPanel simulatedSkyPanel = new JPanel();
	private final JPanel skymapPanel = new JPanel();
	private final JPanel lightProbePanel = new JPanel();
	private final JPanel skyGradientPanel = new JPanel();
	private final JPanel skyboxPanel = new JPanel();
	private final JComboBox canvasSizeCB = new JComboBox();
	private final JComboBox cameraPreset = new JComboBox();
	private final JComboBox customPreset = new JComboBox();
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
	private final RenderContext context;
	private final JButton showPreviewBtn = new JButton();
	private final JLabel renderTimeLbl = new JLabel();
	private final JLabel sppLbl = new JLabel();
	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel progressLbl = new JLabel();
	private final JComboBox postprocessCB = new JComboBox();
	private final JComboBox skyModeCB = new JComboBox();
	private final JButton changeSunColorBtn = new JButton();
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
	private final JCheckBox shutdownWhenDoneCB = new JCheckBox("Shutdown computer when render completes");
	private final JRadioButton v90Btn = new JRadioButton("90");
	private final JRadioButton v180Btn = new JRadioButton("180");

	private final JTabbedPane tabbedPane = new JTabbedPane();

	private final Adjuster skyHorizonOffset = new Adjuster(
			"Horizon offset",
			"Moves the horizon below the actual horizon",
			0.0, 1.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sky().setHorizonOffset(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sky().getHorizonOffset());
		}
	};

	private final Adjuster numThreads = new Adjuster(
			"Render threads",
			"Number of rendering threads",
			RenderConstants.NUM_RENDER_THREADS_MIN,
			20) {
		{
			setClampMax(false);
		}
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
	private final Adjuster emitterIntensity = new Adjuster(
			"Emitter intensity",
			"Light intensity modifier for emitters",
			Scene.MIN_EMITTER_INTENSITY,
			Scene.MAX_EMITTER_INTENSITY) {
		{
			setLogarithmicMode();
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setEmitterIntensity(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getEmitterIntensity());
		}
	};
	private final Adjuster skyLight = new Adjuster(
			"Sky Light",
			"Sky light intensity modifier",
			Sky.MIN_INTENSITY,
			Sky.MAX_INTENSITY) {
		{
			setLogarithmicMode();
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sky().setSkyLight(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sky().getSkyLight());
		}
	};
	private final Adjuster sunIntensity = new Adjuster(
			"Sun Intensity",
			"Sunlight intensity modifier",
			Sun.MIN_INTENSITY,
			Sun.MAX_INTENSITY) {
		{
			setLogarithmicMode();
		}
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
		{
			setClampMax(false);
		}
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
		{
			setLogarithmicMode();
		}
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
		{
			setLogarithmicMode();
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setExposure(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getExposure());
		}
	};
	private final Adjuster cloudSize = new Adjuster(
			"Cloud Size",
			"Cloud Size",
			1.0, 128.0) {
		{
			setLogarithmicMode();
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sky().setCloudSize(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sky().cloudSize());
		}
	};
	private final Adjuster cloudXOffset = new Adjuster(
			"Cloud X",
			"Cloud X Offset",
			1.0, 100.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sky().setCloudXOffset(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sky().cloudXOffset());
		}
	};
	private final Adjuster cloudYOffset = new Adjuster(
			"Cloud Y",
			"Height of the cloud layer",
			-128.0, 512.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sky().setCloudYOffset(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sky().cloudYOffset());
		}
	};
	private final Adjuster cloudZOffset = new Adjuster(
			"Cloud Z",
			"Cloud Z Offset",
			1.0, 100.0) {
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().sky().setCloudZOffset(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().sky().cloudZOffset());
		}
	};

	private GradientEditor gradientEditor;

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

		if (!ShutdownAlert.canShutdown()) {
			// disable the computer shutdown checkbox if we can't shutdown
			shutdownWhenDoneCB.setEnabled(false);
		}

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

		addTab("General", Icon.wrench, buildGeneralPane());
		addTab("Lighting", Icon.colors, buildLightingPane());
		addTab("Sky", Icon.sky, buildSkyPane());
		addTab("Camera", Icon.camera, buildCameraPane());
		addTab("Post-processing", null, buildPostProcessingPane());
		addTab("Advanced", null, buildAdvancedPane());
		addTab("Help", Texture.unknown, buildHelpPane());

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
		startRenderBtn.setIcon(Icon.play.imageIcon());
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

		stopRenderBtn.setText("RESET");
		stopRenderBtn.setIcon(Icon.stop.imageIcon());
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

		sppLbl.setToolTipText("SPP = Samples Per Pixel, SPS = Samples Per Second");

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
		saveSceneBtn.setIcon(Icon.disk.imageIcon());
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
					.addComponent(saveSceneBtn)
				)
				.addComponent(tabbedPane)
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
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(saveFrameBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(showPreviewBtn)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(renderTimeLbl)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(sppLbl)
				)
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
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(saveFrameBtn)
				.addComponent(showPreviewBtn)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(renderTimeLbl)
				.addComponent(sppLbl)
			)
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

	/**
	 * Add a tab and ensure that the icon is to the left of the text in the
	 * tab label.
	 *
	 * @param title
	 * @param icon
	 * @param component
	 */
	private void addTab(String title, Texture icon, Component component) {
		int index = tabbedPane.getTabCount();

		tabbedPane.add(title, component);

		if (icon != null) {
			JLabel lbl = new JLabel(title, icon.imageIcon(), SwingConstants.RIGHT);
			lbl.setIconTextGap(5);
			tabbedPane.setTabComponentAt(index, lbl);
		}
	}

	private JPanel buildAdvancedPane() {
		rayDepth.update();

		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();
		JSeparator sep3 = new JSeparator();

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
				.addComponent(shutdownWhenDoneCB)
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
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(shutdownWhenDoneCB)
			.addContainerGap()
		);
		return panel;
	}

	private JPanel buildPostProcessingPane() {
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
		JLabel canvasSizeLbl = new JLabel("Canvas size:");
		JLabel canvasSizeAdvisory = new JLabel("Note: Actual image size may not be the same as the window size!");

		canvasSizeCB.setEditable(true);
		canvasSizeCB.addItem("400x400");
		canvasSizeCB.addItem("1024x768");
		canvasSizeCB.addItem("960x540");
		canvasSizeCB.addItem("1920x1080");
		canvasSizeCB.addActionListener(canvasSizeListener);

		updateCanvasSizeField();

		loadSceneBtn.setText("Load Scene");
		loadSceneBtn.setIcon(Icon.load.imageIcon());
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
		reloadChunksBtn.setIcon(Icon.reload.imageIcon());
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
		doubleCanvasSizeBtn.setToolTipText("Double the canvas size");
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
					.addComponent(canvasSizeLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(canvasSizeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
				)
				.addComponent(canvasSizeAdvisory)
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
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(canvasSizeLbl)
				.addComponent(canvasSizeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(canvasSizeAdvisory)
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
		changeSunColorBtn.setIcon(Icon.colors.imageIcon());
		changeSunColorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPicker picker = new ColorPicker(changeSunColorBtn, renderMan.scene().sun().getColor());
				picker.addColorListener(new ColorListener() {
					@Override
					public void onColorPicked(Vector3d color) {
						renderMan.scene().sun().setColor(color);
					}
				});
			}
		});

		directLight.setText("enable sunlight");
		directLight.setSelected(renderMan.scene().getDirectLight());
		directLight.addActionListener(directLightListener);

		enableEmitters.setText("enable emitters");
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		enableEmitters.addActionListener(emittersListener);

		emitterIntensity.update();

		sunIntensity.update();

		skyLight.update();

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
						.addComponent(skyLight.getLabel())
						.addComponent(emitterIntensity.getLabel())
						.addComponent(sunIntensity.getLabel())
						.addComponent(sunAzimuth.getLabel())
						.addComponent(sunAltitude.getLabel())
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(skyLight.getSlider())
						.addComponent(emitterIntensity.getSlider())
						.addComponent(sunIntensity.getSlider())
						.addComponent(sunAzimuth.getSlider())
						.addComponent(sunAltitude.getSlider())
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(skyLight.getField())
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
			.addGroup(skyLight.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
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
		updateSkyMode();

		JLabel skymapRotationLbl = new JLabel("Skymap rotation:");
		skymapRotationSlider.setMinimum(1);
		skymapRotationSlider.setMaximum(100);
		skymapRotationSlider.addChangeListener(skyRotationListener);
		skymapRotationSlider.setToolTipText("Controls the horizontal rotational offset for the skymap");
		JLabel lightProbeRotationLbl = new JLabel("Skymap rotation:");
		lightProbeRotationSlider.setMinimum(1);
		lightProbeRotationSlider.setMaximum(100);
		lightProbeRotationSlider.addChangeListener(skyRotationListener);
		lightProbeRotationSlider.setToolTipText("Controls the horizontal rotational offset for the skymap");
		JLabel skyboxRotationLbl = new JLabel("Skybox rotation:");
		skyboxRotationSlider.setMinimum(1);
		skyboxRotationSlider.setMaximum(100);
		skyboxRotationSlider.addChangeListener(skyRotationListener);
		skyboxRotationSlider.setToolTipText("Controls the horizontal rotational offset for the skymap");
		updateSkyRotation();

		skyHorizonOffset.update();
		cloudSize.update();
		cloudXOffset.update();
		cloudYOffset.update();
		cloudZOffset.update();

		JLabel verticalResolutionLbl = new JLabel("Vertical resolution (degrees):");
		ButtonGroup verticalResolution = new ButtonGroup();
		v90Btn.setSelected(true);
		v180Btn.setSelected(false);
		verticalResolution.add(v90Btn);
		verticalResolution.add(v180Btn);

		v90Btn.addActionListener(v90Listener);
		v180Btn.addActionListener(v180Listener);
		updateVerticalResolution();

		simulatedSkyPanel.setBorder(BorderFactory.createTitledBorder("Simulated Sky Settings"));
		GroupLayout simulatedSkyLayout = new GroupLayout(simulatedSkyPanel);
		simulatedSkyPanel.setLayout(simulatedSkyLayout);
		simulatedSkyLayout.setAutoCreateContainerGaps(true);
		simulatedSkyLayout.setAutoCreateGaps(true);
		simulatedSkyLayout.setHorizontalGroup(simulatedSkyLayout.createParallelGroup()
			.addGroup(skyHorizonOffset.horizontalGroup(simulatedSkyLayout))
		);
		simulatedSkyLayout.setVerticalGroup(simulatedSkyLayout.createSequentialGroup()
			.addGroup(skyHorizonOffset.verticalGroup(simulatedSkyLayout))
		);

		skymapPanel.setBorder(BorderFactory.createTitledBorder("Skymap Settings"));
		GroupLayout skymapLayout = new GroupLayout(skymapPanel);
		skymapPanel.setLayout(skymapLayout);
		skymapLayout.setAutoCreateContainerGaps(true);
		skymapLayout.setAutoCreateGaps(true);
		skymapLayout.setHorizontalGroup(skymapLayout.createParallelGroup()
			.addComponent(loadSkymapBtn)
			.addGroup(skymapLayout.createSequentialGroup()
				.addComponent(skymapRotationLbl)
				.addComponent(skymapRotationSlider)
			)
			.addGroup(skymapLayout.createSequentialGroup()
				.addComponent(verticalResolutionLbl)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(v90Btn)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(v180Btn)
			)
		);
		skymapLayout.setVerticalGroup(skymapLayout.createSequentialGroup()
			.addComponent(loadSkymapBtn)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(skymapLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(verticalResolutionLbl)
				.addComponent(v90Btn)
				.addComponent(v180Btn)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(skymapLayout.createParallelGroup()
				.addComponent(skymapRotationLbl)
				.addComponent(skymapRotationSlider)
			)
		);

		loadSkymapBtn.setText("Load Skymap");
		loadSkymapBtn.setToolTipText("Use a panoramic skymap");
		loadSkymapBtn.addActionListener(new SkymapTextureLoader(renderMan));

		lightProbePanel.setBorder(BorderFactory.createTitledBorder("Spherical Skymap Settings"));
		GroupLayout lightProbeLayout = new GroupLayout(lightProbePanel);
		lightProbePanel.setLayout(lightProbeLayout);
		lightProbeLayout.setAutoCreateContainerGaps(true);
		lightProbeLayout.setAutoCreateGaps(true);
		lightProbeLayout.setHorizontalGroup(lightProbeLayout.createParallelGroup()
			.addComponent(loadLightProbeBtn)
			.addGroup(lightProbeLayout.createSequentialGroup()
				.addComponent(lightProbeRotationLbl)
				.addComponent(lightProbeRotationSlider)
			)
		);
		lightProbeLayout.setVerticalGroup(lightProbeLayout.createSequentialGroup()
			.addComponent(loadLightProbeBtn)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(lightProbeLayout.createParallelGroup()
				.addComponent(lightProbeRotationLbl)
				.addComponent(lightProbeRotationSlider)
			)
		);

		loadLightProbeBtn.setText("Load Spherical Skymap");
		loadLightProbeBtn.setToolTipText("Select the spherical skymap to use");
		loadLightProbeBtn.addActionListener(new SkymapTextureLoader(renderMan));

		skyGradientPanel.setBorder(BorderFactory.createTitledBorder("Sky Gradient"));
		gradientEditor = new GradientEditor();
		gradientEditor.addGradientListener(gradientListener);
		updateSkyGradient();
		skyGradientPanel.add(gradientEditor);

		GroupLayout skyboxLayout = new GroupLayout(skyboxPanel);
		skyboxPanel.setLayout(skyboxLayout);
		skyboxPanel.setBorder(BorderFactory.createTitledBorder("Skybox"));

		JLabel skyboxLbl = new JLabel("Load skybox textures:");

		JButton loadUpTexture = new JButton("Up");
		loadUpTexture.setToolTipText("Load up texture");
		loadUpTexture.setIcon(Icon.skyboxUp.imageIcon());
		loadUpTexture.addActionListener(new SkyboxTextureLoader(renderMan, Sky.SKYBOX_UP));

		JButton loadDownTexture = new JButton("Down");
		loadDownTexture.setToolTipText("Load down texture");
		loadDownTexture.setIcon(Icon.skyboxDown.imageIcon());
		loadDownTexture.addActionListener(new SkyboxTextureLoader(renderMan, Sky.SKYBOX_DOWN));

		JButton loadFrontTexture = new JButton("Front");
		loadFrontTexture.setToolTipText("Load front (north) texture");
		loadFrontTexture.setIcon(Icon.skyboxFront.imageIcon());
		loadFrontTexture.addActionListener(new SkyboxTextureLoader(renderMan, Sky.SKYBOX_FRONT));

		JButton loadBackTexture = new JButton("Back");
		loadBackTexture.setToolTipText("Load back (south) texture");
		loadBackTexture.setIcon(Icon.skyboxBack.imageIcon());
		loadBackTexture.addActionListener(new SkyboxTextureLoader(renderMan, Sky.SKYBOX_BACK));

		JButton loadRightTexture = new JButton("Right");
		loadRightTexture.setToolTipText("Load right (east) texture");
		loadRightTexture.setIcon(Icon.skyboxRight.imageIcon());
		loadRightTexture.addActionListener(new SkyboxTextureLoader(renderMan, Sky.SKYBOX_RIGHT));

		JButton loadLeftTexture = new JButton("Left");
		loadLeftTexture.setToolTipText("Load left (west) texture");
		loadLeftTexture.setIcon(Icon.skyboxLeft.imageIcon());
		loadLeftTexture.addActionListener(new SkyboxTextureLoader(renderMan, Sky.SKYBOX_LEFT));

		skyboxLayout.setAutoCreateContainerGaps(true);
		skyboxLayout.setAutoCreateGaps(true);
		skyboxLayout.setHorizontalGroup(skyboxLayout.createParallelGroup()
			.addGroup(skyboxLayout.createSequentialGroup()
				.addComponent(skyboxLbl)
				.addGroup(skyboxLayout.createParallelGroup()
					.addComponent(loadUpTexture)
					.addComponent(loadFrontTexture)
					.addComponent(loadRightTexture)
				)
				.addGroup(skyboxLayout.createParallelGroup()
					.addComponent(loadDownTexture)
					.addComponent(loadBackTexture)
					.addComponent(loadLeftTexture)
				)
			)
			.addGroup(skyboxLayout.createSequentialGroup()
				.addComponent(skyboxRotationLbl)
				.addComponent(skyboxRotationSlider)
			)
		);
		skyboxLayout.setVerticalGroup(skyboxLayout.createSequentialGroup()
			.addComponent(skyboxLbl)
			.addGroup(skyboxLayout.createParallelGroup()
				.addComponent(loadUpTexture)
				.addComponent(loadDownTexture)
			)
			.addGroup(skyboxLayout.createParallelGroup()
				.addComponent(loadFrontTexture)
				.addComponent(loadBackTexture)
			)
			.addGroup(skyboxLayout.createParallelGroup()
				.addComponent(loadRightTexture)
				.addComponent(loadLeftTexture)
			)
			.addGroup(skyboxLayout.createParallelGroup()
				.addComponent(skyboxRotationLbl)
				.addComponent(skyboxRotationSlider)
			)
		);


		atmosphereEnabled.setText("enable atmosphere");
		atmosphereEnabled.addActionListener(atmosphereListener);
		updateAtmosphereCheckBox();

		volumetricFogEnabled.setText("enable volumetric fog");
		volumetricFogEnabled.addActionListener(volumetricFogListener);
		updateVolumetricFogCheckBox();

		cloudsEnabled.setText("enable clouds");
		cloudsEnabled.addActionListener(cloudsEnabledListener);
		updateCloudsEnabledCheckBox();

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
				.addComponent(simulatedSkyPanel)
				.addComponent(skymapPanel)
				.addComponent(lightProbePanel)
				.addComponent(skyGradientPanel)
				.addComponent(skyboxPanel)
				.addComponent(atmosphereEnabled)
				.addComponent(volumetricFogEnabled)
				.addComponent(cloudsEnabled)
				.addGroup(cloudSize.horizontalGroup(layout))
				.addGroup(cloudXOffset.horizontalGroup(layout))
				.addGroup(cloudYOffset.horizontalGroup(layout))
				.addGroup(cloudZOffset.horizontalGroup(layout))
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
			.addComponent(simulatedSkyPanel)
			.addComponent(skymapPanel)
			.addComponent(lightProbePanel)
			.addComponent(skyGradientPanel)
			.addComponent(skyboxPanel)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(atmosphereEnabled)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(volumetricFogEnabled)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(cloudsEnabled)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cloudSize.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cloudXOffset.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cloudYOffset.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(cloudZOffset.verticalGroup(layout))
			.addContainerGap()
		);
		return panel;
	}

	private JPanel buildCameraPane() {
		JLabel projectionModeLbl = new JLabel("Projection");

		fov.update();

		dof = new DoFAdjuster(renderMan);
		dof.update();

		subjectDistance.update();

		JLabel presetLbl = new JLabel("Preset:");
		CameraPreset[] presets = {
			CameraPreset.NONE,
			CameraPreset.ISO_WEST_NORTH, CameraPreset.ISO_NORTH_EAST,
			CameraPreset.ISO_EAST_SOUTH, CameraPreset.ISO_SOUTH_WEST,
			CameraPreset.SKYBOX_RIGHT, CameraPreset.SKYBOX_LEFT,
			CameraPreset.SKYBOX_UP, CameraPreset.SKYBOX_DOWN,
			CameraPreset.SKYBOX_FRONT, CameraPreset.SKYBOX_BACK,
		};
		cameraPreset.setModel(new DefaultComboBoxModel(presets));
		cameraPreset.setMaximumRowCount(presets.length);
		final int presetHeight = cameraPreset.getPreferredSize().height;
		final int presetWidth = cameraPreset.getPreferredSize().width;
		cameraPreset.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(
						list, value, index, isSelected, cellHasFocus);
				label.setPreferredSize(new Dimension(presetWidth, presetHeight));
				CameraPreset preset = (CameraPreset) value;
				label.setIcon(preset.getIcon());
				return label;
			}
		});
		cameraPreset.addActionListener(cameraPresetListener);

		JLabel customPresetLbl = new JLabel("Custom preset:");
		customPreset.setEditable(true);
		updateCustomPresets();
		JButton savePreset = new JButton("save");
		savePreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = "";
				int selected = customPreset.getSelectedIndex();
				if (selected == -1) {
					// select name
					name = (String) customPreset.getEditor().getItem();
					name = (name==null) ? "" : name.trim();
					if (name.isEmpty()) {
						// auto-assign name
						int nextIndex = customPreset.getItemCount() + 1;
						outer: while (true) {
							name = "custom-" + (nextIndex++);
							for (int i = 0; i < customPreset.getItemCount(); ++i) {
								String item = (String) customPreset.getItemAt(i);
								if (name.equals(item)) {
									continue outer;
								}
							}
							break;
						}
					} else {
						for (int i = 0; i < customPreset.getItemCount(); ++i) {
							String item = (String) customPreset.getItemAt(i);
							if (name.equals(item)) {
								selected = i;
								break;
							}
						}
					}
					if (selected == -1) {
						// add new preset
						selected = 	customPreset.getItemCount();
						customPreset.addItem(name);

					}
					customPreset.setSelectedIndex(selected);
				} else {
					name = (String) customPreset.getSelectedItem();
				}
				renderMan.scene().saveCameraPreset(name);
			}
		});
		JButton loadPreset = new JButton("load");
		loadPreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = "";
				int selected = customPreset.getSelectedIndex();
				if (selected == -1) {
					// select name
					name = (String) customPreset.getEditor().getItem();
					name = (name==null) ? "" : name.trim();
				} else {
					name = ((String) customPreset.getSelectedItem()).trim();
				}
				if (!name.isEmpty()) {
					renderMan.scene().loadCameraPreset(name);
				}
			}
		});
		JButton deletePreset = new JButton("delete");
		deletePreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = "";
				int selected = customPreset.getSelectedIndex();
				if (selected == -1) {
					// select name
					name = (String) customPreset.getEditor().getItem();
					name = (name==null) ? "" : name.trim();
				} else {
					name = ((String) customPreset.getSelectedItem()).trim();
				}
				if (!name.isEmpty()) {
					renderMan.scene().deleteCameraPreset(name);
					if (selected != -1) {
						customPreset.removeItemAt(selected);
					} else {
						for (int i = 0; i < customPreset.getItemCount(); ++i) {
							if (name.equals(customPreset.getItemAt(i))) {
								customPreset.removeItemAt(i);
								break;
							}
						}
					}
				}
			}
		});

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

		JButton centerCameraBtn = new JButton("Center camera");
		centerCameraBtn.setToolTipText("Center camera above loaded chunks");
		centerCameraBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderMan.scene().moveCameraToCenter();
			}
		});

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
					.addComponent(presetLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cameraPreset)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(customPresetLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(customPreset)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(savePreset)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(loadPreset)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(deletePreset)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(cameraToPlayerBtn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(centerCameraBtn)
				)
				.addComponent(sep1)
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
				.addComponent(presetLbl)
				.addComponent(cameraPreset)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(customPresetLbl)
				.addComponent(customPreset)
				.addComponent(savePreset)
				.addComponent(loadPreset)
				.addComponent(deletePreset)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
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
			.addComponent(sep1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(projectionModeLbl)
				.addComponent(projectionMode)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
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

	protected void updateVerticalResolution() {
		v90Btn.removeActionListener(v90Listener);
		v180Btn.removeActionListener(v180Listener);
		boolean mirror = renderMan.scene().sky().isMirrored();
		v90Btn.setSelected(mirror);
		v180Btn.setSelected(!mirror);
		v90Btn.addActionListener(v90Listener);
		v180Btn.addActionListener(v180Listener);
	}

	protected void updateVolumetricFogCheckBox() {
		volumetricFogEnabled.removeActionListener(volumetricFogListener);
		volumetricFogEnabled.setSelected(renderMan.scene().volumetricFogEnabled());
		volumetricFogEnabled.addActionListener(volumetricFogListener);
	}

	protected void updateCloudsEnabledCheckBox() {
		cloudsEnabled.removeActionListener(cloudsEnabledListener);
		cloudsEnabled.setSelected(renderMan.scene().sky().cloudsEnabled());
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
			String size = (String) canvasSizeCB.getSelectedItem();
			try {
				Pattern regex = Pattern.compile("([0-9]+)[xX.*]([0-9]+)");
				Matcher matcher = regex.matcher(size);
				if (matcher.matches()) {
					int width = Integer.parseInt(matcher.group(1));
					int height = Integer.parseInt(matcher.group(2));
					setCanvasSize(width, height);
				} else {
					logger.info("Failed to set canvas size: format must be WIDTHxHEIGHT!");
				}
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

	private final GradientListener gradientListener = new GradientListener() {
		@Override
		public void gradientChanged(List<Vector4d> newGradient) {
			renderMan.scene().sky().setGradient(newGradient);
		}
		@Override
		public void stopSelected(int index) {
		}
		@Override
		public void stopModified(int index, Vector4d marker) {
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
			new Thread() {
				@Override
				public void run() {
					renderMan.saveSnapshot(RenderControls.this);
				}
			}.start();
		}
	};
	private final ActionListener loadSceneListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			new SceneSelector(RenderControls.this, context);
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
			if (selected != null && selected instanceof ProjectionMode) {
				renderMan.scene().camera().setProjectionMode(
						(ProjectionMode) selected);
				updateProjectionMode();
				fov.update();
			}
		}
	};
	private final ActionListener cameraPresetListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox) e.getSource();
			Object selected = source.getSelectedItem();
			if (selected != null && selected instanceof CameraPreset) {
				CameraPreset preset = (CameraPreset) selected;
				preset.apply(renderMan.scene().camera());
				updateProjectionMode();
				fov.update();
				updateCameraDirection();
			}
		}
	};
	private final ActionListener skyModeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox) e.getSource();
			renderMan.scene().sky().setSkyMode((SkyMode) source.getSelectedItem());
			updateSkyMode();
			RenderControls.this.pack();
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
			renderMan.scene().sky().setCloudsEnabled(source.isSelected());
		}
	};
	private final ActionListener v90Listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (v90Btn.isSelected()) {
				renderMan.scene().sky().setMirrored(true);
			}
		}
	};
	private final ActionListener v180Listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (v180Btn.isSelected()) {
				renderMan.scene().sky().setMirrored(false);
			}
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

	private int spp = 0;
	private int sps = 0;

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
		skymapRotationSlider.removeChangeListener(skyRotationListener);
		skymapRotationSlider.setValue((int) FastMath.round(
				100 * renderMan.scene().sky().getRotation() / (2 * Math.PI)));
		skymapRotationSlider.addChangeListener(skyRotationListener);
		skyboxRotationSlider.removeChangeListener(skyRotationListener);
		skyboxRotationSlider.setValue((int) FastMath.round(
				100 * renderMan.scene().sky().getRotation() / (2 * Math.PI)));
		skyboxRotationSlider.addChangeListener(skyRotationListener);
	}

	private void updateSkyGradient() {
		gradientEditor.removeGradientListener(gradientListener);
		gradientEditor.setGradient(renderMan.scene().sky().getGradient());
		gradientEditor.addGradientListener(gradientListener);
	}

	protected void updateProjectionMode() {
		projectionMode.removeActionListener(projectionModeListener);
		ProjectionMode mode = renderMan.scene().camera().getProjectionMode();
		projectionMode.setSelectedItem(mode);
		projectionMode.addActionListener(projectionModeListener);
	}

	protected void updateSkyMode() {
		skyModeCB.removeActionListener(skyModeListener);
		SkyMode mode = renderMan.scene().sky().getSkyMode();
		skyModeCB.setSelectedItem(mode);
		simulatedSkyPanel.setVisible(mode == SkyMode.SIMULATED);
		skymapPanel.setVisible(mode == SkyMode.SKYMAP_PANORAMIC);
		lightProbePanel.setVisible(mode == SkyMode.SKYMAP_SPHERICAL);
		skyGradientPanel.setVisible(mode == SkyMode.GRADIENT);
		skyboxPanel.setVisible(mode == SkyMode.SKYBOX);
		skyModeCB.addActionListener(skyModeListener);
	}

	protected void updateCanvasSizeField() {
		canvasSizeCB.removeActionListener(canvasSizeListener);
		canvasSizeCB.setSelectedItem("" + renderMan.scene().canvasWidth() +
				"x" + renderMan.scene().canvasHeight());
		canvasSizeCB.addActionListener(canvasSizeListener);
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

	protected void updateCustomPresets() {
		customPreset.removeAllItems();
		JsonObject presets = renderMan.scene().getCameraPresets();
		for (JsonMember member : presets.getMemberList()) {
			String name = member.getName().trim();
			if (!name.isEmpty()) {
				customPreset.addItem(name);
			}
		}
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
			showPreviewBtn.setText("Hide Preview Window");
			showPreviewBtn.setToolTipText("Hide the preview window");
		} else {
			showPreviewBtn.setText("Show Preview Window");
			showPreviewBtn.setToolTipText("Show the preview window");
		}
	}

	protected void setCanvasSize(int width, int height) {
		renderMan.scene().setCanvasSize(width, height);
		int canvasWidth = renderMan.scene().canvasWidth();
		int canvasHeight = renderMan.scene().canvasHeight();
		canvasSizeCB.setSelectedItem("" + canvasWidth + "x" + canvasHeight);
		view.setCanvasSize(canvasWidth, canvasHeight);
	}

	/**
	 * Method to notify the render controls dialog that a scene has been loaded.
	 * Causes canvas size to be updated. Can be called from outside EDT.
	 */
	@Override
	public void sceneLoaded() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateAllSettings();

				showPreviewWindow();
			}
		});
	}

	protected void updateAllSettings() {
		skyHorizonOffset.update();
		dof.update();
		fov.update();
		subjectDistance.update();
		updateProjectionMode();
		updateSkyGradient();
		updateSkyMode();
		updateCanvasSizeField();
		emitterIntensity.update();
		skyLight.update();
		sunIntensity.update();
		sunAzimuth.update();
		sunAltitude.update();
		updateStillWater();
		updateClearWater();
		updateSkyRotation();
		updateVerticalResolution();
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
		cloudSize.update();
		cloudXOffset.update();
		cloudYOffset.update();
		cloudZOffset.update();
		rayDepth.update();
		updateWaterHeight();
		updateCameraDirection();
		updateCameraPosition();
		updateCustomPresets();
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		directLight.setSelected(renderMan.scene().getDirectLight());
		stopRenderBtn.setEnabled(true);
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
		this.sps = sps;
		updateSPPLbl();
	}

	/**
	 * Update SPP status label
	 * @param spp Samples per pixel
	 */
	@Override
	public void setSPP(int spp) {
		this.spp = spp;
		updateSPPLbl();
	}

	private void updateSPPLbl() {
		if (sppLbl != null) {
			sppLbl.setText(decimalFormat.format(spp) + " SPP, " +
					decimalFormat.format(sps) + " SPS");
		}
	}

	@Override
	public void setProgress(String task, int done, int start, int target) {
		if (progressBar != null && progressLbl != null && etaLbl != null) {
			progressLbl.setText(String.format("%s: %s of %s",
					task, decimalFormat.format(done), decimalFormat.format(target)));
			progressLbl.repaint();
			progressBar.setMinimum(start);
			progressBar.setMaximum(target);
			progressBar.setValue(Math.min(target, done));
			progressBar.repaint();
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
		Camera camera = renderMan.scene().camera();
		double value = renderMan.scene().camera().getFoV();
		double scale = camera.getMaxFoV() - camera.getMinFoV();
		double offset = value/scale;
		double newValue = scale*Math.exp(Math.log(offset) + 0.1*diff);
		if (!Double.isNaN(newValue) && !Double.isInfinite(newValue)) {
			renderMan.scene().camera().setFoV(newValue);
		}
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
				startRenderBtn.setIcon(Icon.play.imageIcon());
			} else {
				startRenderBtn.setText("PAUSE");
				startRenderBtn.setIcon(Icon.pause.imageIcon());
			}
			stopRenderBtn.setEnabled(true);
			stopRenderBtn.setForeground(Color.red);
		} else {
			startRenderBtn.setText("START");
			startRenderBtn.setIcon(Icon.play.imageIcon());
			stopRenderBtn.setEnabled(false);
			stopRenderBtn.setForeground(Color.black);
		}
	}

	@Override
	public void chunksLoaded() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateCameraPosition();
				showPreviewWindow();
			}
		});
	}

	@Override
	public void renderJobFinished(long time, int sps) {
		if (shutdownWhenDoneCB.isSelected()) {
			new ShutdownAlert(this);
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
	static class SkymapTextureLoader implements ActionListener {
		private final RenderManager renderMan;
		private static String defaultDirectory = System.getProperty("user.dir");
		public SkymapTextureLoader(RenderManager renderMan) {
			this.renderMan = renderMan;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			CenteredFileDialog fileDialog =
					new CenteredFileDialog(null, "Open Skymap", FileDialog.LOAD);
			String directory;
			synchronized (SkyboxTextureLoader.class) {
				directory = defaultDirectory;
			}
			fileDialog.setDirectory(directory);
			fileDialog.setFilenameFilter(
					new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".png")
									|| name.toLowerCase().endsWith(".jpg")
									|| name.toLowerCase().endsWith(".hdr")
									|| name.toLowerCase().endsWith(".pfm");
						}
					});
			fileDialog.setVisible(true);
			File selectedFile = fileDialog.getSelectedFile();
			if (selectedFile != null) {
				synchronized (SkyboxTextureLoader.class) {
					File parent = selectedFile.getParentFile();
					if (parent != null) {
						defaultDirectory = parent.getAbsolutePath();
					}
				}
				renderMan.scene().sky().loadSkymap(selectedFile.getAbsolutePath());
			}
		}
	};

	static class SkyboxTextureLoader implements ActionListener {
		private final RenderManager renderMan;
		private final int textureIndex;
		private static String defaultDirectory = System.getProperty("user.dir");
		public SkyboxTextureLoader(RenderManager renderMan, int textureIndex) {
			this.renderMan = renderMan;
			this.textureIndex = textureIndex;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			CenteredFileDialog fileDialog =
					new CenteredFileDialog(null, "Open Skybox Texture", FileDialog.LOAD);
			String directory;
			synchronized (SkyboxTextureLoader.class) {
				directory = defaultDirectory;
			}
			fileDialog.setDirectory(directory);
			fileDialog.setFilenameFilter(
					new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".png")
									|| name.toLowerCase().endsWith(".jpg")
									|| name.toLowerCase().endsWith(".hdr")
									|| name.toLowerCase().endsWith(".pfm");
						}
					});
			fileDialog.setVisible(true);
			File selectedFile = fileDialog.getSelectedFile();
			if (selectedFile != null) {
				synchronized (SkyboxTextureLoader.class) {
					File parent = selectedFile.getParentFile();
					if (parent != null) {
						defaultDirectory = parent.getAbsolutePath();
					}
				}
				renderMan.scene().sky().loadSkyboxTexture(selectedFile.getAbsolutePath(), textureIndex);
			}
		}
	};

	protected AtomicBoolean resetConfirmMutex = new AtomicBoolean(false);

	@Override
	public void renderResetPrevented() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (resetConfirmMutex.compareAndSet(false, true)) {
					new ConfirmResetPopup(RenderControls.this, new AcceptOrRejectListener() {
						@Override
						public void onAccept() {
							renderMan.scene().resetRender();
							resetConfirmMutex.set(false);
						}

						@Override
						public void onReject() {
							renderMan.revertPendingSceneChanges();
							updateAllSettings();
							resetConfirmMutex.set(false);
						}
					});
				}
			}
		});
	}
}
