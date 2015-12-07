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
package se.llbit.chunky.renderer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.RenderState;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.renderer.ui.tabs.AdvancedTab;
import se.llbit.chunky.renderer.ui.tabs.CameraTab;
import se.llbit.chunky.renderer.ui.tabs.GeneralTab;
import se.llbit.chunky.renderer.ui.tabs.HelpTab;
import se.llbit.chunky.renderer.ui.tabs.LightingTab;
import se.llbit.chunky.renderer.ui.tabs.PostProcessingTab;
import se.llbit.chunky.renderer.ui.tabs.RenderControlsTab;
import se.llbit.chunky.renderer.ui.tabs.SkyTab;
import se.llbit.chunky.renderer.ui.tabs.WaterTab;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.math.Ray;
import se.llbit.math.Vector2d;
import se.llbit.math.Vector3d;
import se.llbit.ui.Adjuster;

/**
 * Render Controls dialog.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class RenderControls extends JDialog implements ViewListener, RenderStatusListener {

	private static final double CHUNK_SELECT_RADIUS = -8 * 1.4142;

	private int spp = 0;
	private int sps = 0;

	private final RenderManager renderMan;
	private final SceneManager sceneMan;
	private final Chunk3DView view;
	private final Chunky chunky;

	private final JButton startRenderBtn = new JButton();
	private final JButton saveSceneBtn = new JButton();


	private final JButton saveFrameBtn = new JButton();
	private final JTextField sceneNameField = new JTextField();
	private final JLabel sceneNameLbl = new JLabel();

	private final JButton stopRenderBtn = new JButton();
	private final RenderContext context;
	private final JButton showPreviewBtn = new JButton();
	private final JLabel renderTimeLbl = new JLabel();
	private final JLabel sppLbl = new JLabel();
	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel progressLbl = new JLabel();
	private final JLabel etaLbl = new JLabel();
	private final DecimalFormat decimalFormat = new DecimalFormat();

	private final JTabbedPane tabbedPane = new JTabbedPane();

	private final Adjuster targetSPP = new Adjuster(
			"Target SPP",
			"The target Samples Per Pixel",
			100, 100000) {

		{
			setClampMax(false);
			setClampMin(false);
			setLogarithmicMode();
		}

		@Override
		public void valueChanged(double newValue) {
			int value = (int) newValue;
			renderMan.setTargetSPP(value);
			if (renderMan.scene().getRenderState() != RenderState.PREVIEW) {
				startRenderBtn.setEnabled(renderMan.getCurrentSPP() < value);
			}
		}

		@Override
		public void update() {
			set(renderMan.scene().getTargetSPP());
		}
	};

	private final RenderControlsTab generalTab;
	private final RenderControlsTab lightingTab;
	private final RenderControlsTab skyTab;
	private final RenderControlsTab waterTab;
	private final CameraTab cameraTab;
	private final RenderControlsTab postProcessingTab;
	private final AdvancedTab advancedTab;
	private final RenderControlsTab helpTab;

	/**
	 * Create a new Render Controls dialog.
	 * @param chunkyInstance parent window
	 * @param renderContext render context
	 */
	public RenderControls(Chunky chunkyInstance, RenderContext renderContext) {

		super(chunkyInstance.getFrame());

		decimalFormat.setGroupingSize(3);
		decimalFormat.setGroupingUsed(true);

		context = renderContext;
		chunky = chunkyInstance;

		view = new Chunk3DView(chunkyInstance.getFrame());
		view.addViewListener(this);

		renderMan = new RenderManager(view.getCanvas(), renderContext, this);

		generalTab = new GeneralTab(this);
		lightingTab = new LightingTab(this);
		skyTab = new SkyTab(this);
		waterTab = new WaterTab(this);
		cameraTab = new CameraTab(this);
		postProcessingTab = new PostProcessingTab(this);
		advancedTab = new AdvancedTab(this);
		helpTab = new HelpTab(this);

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RenderControlsTab tab = (RenderControlsTab) tabbedPane.getSelectedComponent();
				tab.refreshSettings();
			}
		});

		buildUI();

		renderMan.start();

		view.addViewListener(cameraTab);
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

		addTab("General", Icon.wrench, generalTab);
		addTab("Lighting", Icon.light, lightingTab);
		addTab("Sky & Fog", Icon.sky, skyTab);
		addTab("Water", Icon.water, waterTab);
		addTab("Camera", Icon.eye, cameraTab);
		addTab("Post-processing", Icon.gear, postProcessingTab);
		addTab("Advanced", Icon.advanced, advancedTab);
		addTab("Help", Icon.question, helpTab);

		JLabel sppTargetLbl = new JLabel("SPP Target: ");
		sppTargetLbl.setToolTipText("The render will be paused at this SPP count");

		JButton setDefaultBtn = new JButton("Make Default");
		setDefaultBtn.setToolTipText("Make the current SPP target the default");
		setDefaultBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PersistentSettings.setSppTargetDefault(renderMan.scene().getTargetSPP());
			}
		});

		targetSPP.update();

		JLabel renderLbl = new JLabel("Render: ");

		setViewVisible(false);
		showPreviewBtn.setIcon(Icon.eye.imageIcon());
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
				switch (renderMan.scene().getRenderState()) {
				case PAUSED:
					renderMan.scene().resumeRender();
					break;
				case PREVIEW:
					renderMan.scene().startRender();
					break;
				case RENDERING:
					renderMan.scene().pauseRender();
					break;
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
		saveFrameBtn.setIcon(Icon.photo.imageIcon());
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
					.addGroup(targetSPP.horizontalGroup(layout))
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
				.addGroup(targetSPP.verticalGroup(layout))
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
		final JScrollPane scrollPane = new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setContentPane(scrollPane);

		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			private boolean resized = false;
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!resized && scrollPane.getVerticalScrollBar().isVisible()) {
					Dimension vsbPrefSize = new JScrollPane().getVerticalScrollBar().getPreferredSize();
					Dimension size = getSize();
					setSize(size.width + vsbPrefSize.width, size.height);
					resized = true;
				}
			}
		});

		pack();

		setLocationRelativeTo(chunky.getFrame());

		setVisible(true);
	}

	/**
	 * Add a tab and ensure that the icon is to the left of the text in the
	 * tab label.
	 *
	 * @param title tab title
	 * @param icon tab icon
	 * @param component tab component
	 */
	private void addTab(String title, Texture icon, Component component) {
		addTab(title, icon == null ? null : icon.imageIcon(), component);
	}

	private void addTab(String title, ImageIcon icon, Component component) {
		int index = tabbedPane.getTabCount();

		tabbedPane.add(title, component);

		if (icon != null) {
			JLabel lbl = new JLabel(title, icon, SwingConstants.RIGHT);
			lbl.setIconTextGap(5);
			tabbedPane.setTabComponentAt(index, lbl);
		}
	}

	private void updateTitle() {
		setTitle("Render Controls - " + renderMan.scene().name());
	}

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

	protected void updateSceneNameField() {
		sceneNameField.getDocument().removeDocumentListener(sceneNameListener);
		sceneNameField.removeActionListener(sceneNameActionListener);
		sceneNameField.setText(renderMan.scene().name());
		sceneNameField.getDocument().addDocumentListener(sceneNameListener);
		sceneNameField.addActionListener(sceneNameActionListener);
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
		renderMan.scene().camera().strafeLeft(chunky.getShiftModifier() ? .1 : 1);
	}

	@Override
	public void onStrafeRight() {
		renderMan.scene().camera().strafeRight(chunky.getShiftModifier() ? .1 : 1);
	}

	@Override
	public void onMoveForward() {
		renderMan.scene().camera().moveForward(chunky.getShiftModifier() ? .1 : 1);
	}

	@Override
	public void onMoveBackward() {
		renderMan.scene().camera().moveBackward(chunky.getShiftModifier() ? .1 : 1);
	}

	@Override
	public void onMoveForwardFar() {
		renderMan.scene().camera().moveForward(100);
	}

	@Override
	public void onMoveBackwardFar() {
		renderMan.scene().camera().moveBackward(100);
	}

	@Override
	public void onMoveUp() {
		renderMan.scene().camera().moveUp(chunky.getShiftModifier() ? .1 : 1);
	}

	@Override
	public void onMoveDown() {
		renderMan.scene().camera().moveDown(chunky.getShiftModifier() ? .1 : 1);
	}

	@Override
	public void onMouseDragged(int dx, int dy) {
		renderMan.scene().camera().rotateView(
				- (Math.PI / 250) * dx,
				(Math.PI / 250) * dy);
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

	/**
	 * Method to notify the render controls dialog that a scene has been loaded.
	 * Causes canvas size to be updated. Can be called from outside EDT.
	 */
	@Override
	public void sceneLoaded() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				refreshSettings();

				showPreviewWindow();
			}
		});
	}

	protected void refreshSettings() {
		updateTitle();
		targetSPP.update();
		updateSceneNameField();
		stopRenderBtn.setEnabled(true);

		RenderControlsTab tab = (RenderControlsTab) tabbedPane.getSelectedComponent();
		tab.refreshSettings();
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
		// TODO(llbit): add abort notice.
		etaLbl.setText("ETA: N/A");
	}

	@Override
	public void taskFailed(String task) {
		// TODO(llbit): add abort notice.
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
	}

	/**
	 * @return The render context for this Render Controls dialog
	 */
	public RenderContext getContext() {
		return context;
	}

	@Override
	public void renderStateChanged(RenderState state) {
		switch (state) {
		case PAUSED:
			startRenderBtn.setText("RESUME");
			startRenderBtn.setIcon(Icon.play.imageIcon());
			stopRenderBtn.setEnabled(true);
			stopRenderBtn.setForeground(Color.red);
			startRenderBtn.setEnabled(renderMan.getCurrentSPP() < renderMan.scene().getTargetSPP());
			break;
		case PREVIEW:
			startRenderBtn.setText("START");
			startRenderBtn.setIcon(Icon.play.imageIcon());
			startRenderBtn.setEnabled(true);
			stopRenderBtn.setEnabled(false);
			stopRenderBtn.setForeground(Color.black);
			break;
		case RENDERING:
			startRenderBtn.setText("PAUSE");
			startRenderBtn.setIcon(Icon.pause.imageIcon());
			startRenderBtn.setEnabled(true);
			stopRenderBtn.setEnabled(true);
			stopRenderBtn.setForeground(Color.red);
			break;
		}
	}

	@Override
	public void chunksLoaded() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showPreviewWindow();
			}
		});
		cameraTab.chunksLoaded();
	}

	@Override
	public void renderJobFinished(long time, int sps) {
		if (advancedTab.shutdownAfterCompletedRender()) {
			new ShutdownAlert(this);
		}
	}

	protected AtomicBoolean resetConfirmMutex = new AtomicBoolean(false);

	@Override
	public void renderResetRequested() {
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
							refreshSettings();
							resetConfirmMutex.set(false);
						}
					});
				}
			}
		});
	}

	public void drawViewBounds(Graphics g, ChunkView cv) {
		Camera camera = renderMan.scene().camera();
		int width = renderMan.scene().canvasWidth();
		int height = renderMan.scene().canvasHeight();

		double halfWidth = width/(2.0*height);

		Ray ray = new Ray();

		Vector3d[] corners = new Vector3d[4];
		Vector2d[] bounds = new Vector2d[4];

		camera.calcViewRay(ray, -halfWidth, -0.5);
		corners[0] = new Vector3d(ray.d);
		bounds[0] = findMapPos(ray, cv);

		camera.calcViewRay(ray, -halfWidth, 0.5);
		corners[1] = new Vector3d(ray.d);
		bounds[1] = findMapPos(ray, cv);

		camera.calcViewRay(ray, halfWidth, 0.5);
		corners[2] = new Vector3d(ray.d);
		bounds[2] = findMapPos(ray, cv);

		camera.calcViewRay(ray, halfWidth, -0.5);
		corners[3] = new Vector3d(ray.d);
		bounds[3] = findMapPos(ray, cv);

		g.setColor(Color.YELLOW);
		for (int i = 0; i < 4; ++i) {
			int j = (i+1)%4;
			if (bounds[i] != null && bounds[j] != null) {
				drawLine(g, bounds[i], bounds[j]);
			} else if (bounds[i] != null && bounds[j] == null) {
				drawExtended(g, cv, bounds, corners, i, j);
			} else if (bounds[j] != null && bounds[i] == null) {
				drawExtended(g, cv, bounds, corners, j, i);
			}
		}

		int ox = (int) (cv.scale * (ray.o.x/16 - cv.x0));
		int oy = (int) (cv.scale * (ray.o.z/16 - cv.z0));
		g.drawLine(ox-5, oy, ox+5, oy);
		g.drawLine(ox, oy-5, ox, oy+5);

		camera.calcViewRay(ray, 0, 0);
		Vector3d o = new Vector3d(ray.o);
		o.x /= 16;
		o.z /= 16;
		o.scaleAdd(1, ray.d);
		int x = (int) (cv.scale * (o.x - cv.x0));
		int y = (int) (cv.scale * (o.z - cv.z0));
		g.drawLine(ox, oy, x, y);

	}

	private void drawExtended(Graphics g, ChunkView cv, Vector2d[] bounds,
			Vector3d[] corners, int i, int j) {
		Vector3d c = new Vector3d();
		c.cross(corners[i], corners[j]);
		Vector2d c2 = new Vector2d();
		c2.x = c.z;
		c2.y = -c.x;
		c2.normalize();
		if (corners[i].y > 0 ) {
			c2.scale(-1);
		}
		double tNear = Double.POSITIVE_INFINITY;
		double t = - bounds[i].x / c2.x;
		if (t > 0 && t < tNear) {
			tNear = t;
		}
		t = (cv.scale * (cv.x1 - cv.x0) - bounds[i].x) / c2.x;
		if (t > 0 && t < tNear) {
			tNear = t;
		}
		t = - bounds[i].y / c2.y;
		if (t > 0 && t < tNear) {
			tNear = t;
		}
		t = (cv.scale * (cv.z1 - cv.z0) - bounds[i].y) / c2.y;
		if (t > 0 && t < tNear) {
			tNear = t;
		}
		if (tNear != Double.POSITIVE_INFINITY) {
			Vector2d p = new Vector2d(bounds[i]);
			p.scaleAdd(tNear, c2);
			drawLine(g, p, bounds[i]);
		}
	}

	private void drawLine(Graphics g, Vector2d v1, Vector2d v2) {
		int x1 = (int) v1.x;
		int y1 = (int) v1.y;
		int x2 = (int) v2.x;
		int y2 = (int) v2.y;
		g.drawLine(x1, y1, x2, y2);
	}

	/**
	 * Find the point where the ray intersects the ground (y=63)
	 * @param ray
	 * @param cv
	 */
	private Vector2d findMapPos(Ray ray, ChunkView cv) {
		if (ray.d.y < 0 && ray.o.y > 63 || ray.d.y > 0 && ray.o.y < 63) {
			// ray intersects ground
			double d = (63 - ray.o.y) / ray.d.y;
			Vector3d pos = new Vector3d();
			pos.scaleAdd(d, ray.d, ray.o);

			return new Vector2d(
					cv.scale * (pos.x/16 - cv.x0),
					cv.scale * (pos.z/16 - cv.z0));
		} else {
			return null;
		}

	}

	public void selectVisibleChunks(ChunkView cv, Chunky chunky) {
		Camera camera = renderMan.scene().camera();
		int width = renderMan.scene().canvasWidth();
		int height = renderMan.scene().canvasHeight();

		double halfWidth = width/(2.0*height);

		Vector3d o = new Vector3d(camera.getPosition());

		Ray ray = new Ray();
		Vector3d[] corners = new Vector3d[4];

		camera.calcViewRay(ray, -halfWidth, -0.5);
		corners[0] = new Vector3d(ray.d);
		camera.calcViewRay(ray, -halfWidth, 0.5);
		corners[1] = new Vector3d(ray.d);
		camera.calcViewRay(ray, halfWidth, 0.5);
		corners[2] = new Vector3d(ray.d);
		camera.calcViewRay(ray, halfWidth, -0.5);
		corners[3] = new Vector3d(ray.d);

		Vector3d[] norm = new Vector3d[4];
		norm[0] = new Vector3d();
		norm[0].cross(corners[1], corners[0]);
		norm[0].normalize();
		norm[1] = new Vector3d();
		norm[1].cross(corners[2], corners[1]);
		norm[1].normalize();
		norm[2] = new Vector3d();
		norm[2].cross(corners[3], corners[2]);
		norm[2].normalize();
		norm[3] = new Vector3d();
		norm[3].cross(corners[0], corners[3]);
		norm[3].normalize();

		for (int x = cv.px0; x <= cv.px1; ++x) {
			for (int z = cv.pz0; z <= cv.pz1; ++z) {
				Vector3d pos = new Vector3d((x+0.5)*16, 63, (z+0.5)*16);// chunk top center position
				pos.sub(o);
				if (norm[0].dot(pos) > CHUNK_SELECT_RADIUS &&
						norm[1].dot(pos) > CHUNK_SELECT_RADIUS &&
						norm[2].dot(pos) > CHUNK_SELECT_RADIUS &&
						norm[3].dot(pos) > CHUNK_SELECT_RADIUS) {
					chunky.selectChunk(x, z);
				}
			}
		}
	}

	public RenderManager getRenderManager() {
		return renderMan;
	}

	public SceneManager getSceneManager() {
		return sceneMan;
	}

	public Chunky getChunky() {
		return chunky;
	}

	public Chunk3DView getView() {
		return view;
	}

	public void panToCamera() {
		cameraTab.panToCamera();
	}

	public void moveCameraTo(double x, double z) {
		cameraTab.moveCameraTo(x, z);
	}
}
