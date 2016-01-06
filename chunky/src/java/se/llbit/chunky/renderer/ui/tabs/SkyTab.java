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

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sky.SkyMode;
import se.llbit.chunky.renderer.ui.ColorListener;
import se.llbit.chunky.renderer.ui.ColorPicker;
import se.llbit.chunky.renderer.ui.GradientEditor;
import se.llbit.chunky.renderer.ui.GradientListener;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.ui.CenteredFileDialog;
import se.llbit.chunky.world.Icon;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;
import se.llbit.ui.Adjuster;

public class SkyTab extends RenderControlsTab {
	private static final long serialVersionUID = -1L;

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
	private final JComboBox skyModeCB = new JComboBox();
	private final JButton fogColorBtn = new JButton("Edit Fog Color");
	private final JCheckBox transparentSky = new JCheckBox();
	private final JCheckBox cloudsEnabled = new JCheckBox();
	private final JRadioButton v90Btn = new JRadioButton("90");
	private final JRadioButton v180Btn = new JRadioButton("180");

	private GradientEditor gradientEditor;

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

	private final Adjuster fogDensity = new Adjuster(
			"Fog density",
			"Alters the volumetric fog",
			0.0, 2.0) {
		{
			setClampMax(false);
		}
		@Override
		public void valueChanged(double newValue) {
			renderMan.scene().setFogDensity(newValue);
		}

		@Override
		public void update() {
			set(renderMan.scene().getFogDensity());
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

	private final ActionListener skyModeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox) e.getSource();
			renderMan.scene().sky().setSkyMode((SkyMode) source.getSelectedItem());
			updateSkyMode();
			renderControls.pack();
		}
	};

	private final ActionListener transparentSkyListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			renderMan.scene().setTransparentSky(source.isSelected());
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

	public SkyTab(RenderControls renderControls) {
		super(renderControls);

		JLabel skyModeLbl = new JLabel("Sky Mode:");
		skyModeCB.setModel(new DefaultComboBoxModel(Sky.SkyMode.values()));
		skyModeCB.addActionListener(skyModeListener);

		// Need to hide these to not warp the Swing layout:
		simulatedSkyPanel.setVisible(false);
		skymapPanel.setVisible(false);
		lightProbePanel.setVisible(false);
		skyGradientPanel.setVisible(false);
		skyboxPanel.setVisible(false);

		JLabel skymapRotationLbl = new JLabel("Skymap rotation:");
		skymapRotationSlider.setMinimum(1);
		skymapRotationSlider.setMaximum(100);
		skymapRotationSlider.addChangeListener(skyRotationListener);
		skymapRotationSlider.setToolTipText(
				"Controls the horizontal rotational offset for the skymap");
		JLabel lightProbeRotationLbl = new JLabel("Skymap rotation:");
		lightProbeRotationSlider.setMinimum(1);
		lightProbeRotationSlider.setMaximum(100);
		lightProbeRotationSlider.addChangeListener(skyRotationListener);
		lightProbeRotationSlider.setToolTipText(
				"Controls the horizontal rotational offset for the skymap");
		JLabel skyboxRotationLbl = new JLabel("Skybox rotation:");
		skyboxRotationSlider.setMinimum(1);
		skyboxRotationSlider.setMaximum(100);
		skyboxRotationSlider.addChangeListener(skyRotationListener);
		skyboxRotationSlider.setToolTipText(
				"Controls the horizontal rotational offset for the skymap");

		JLabel verticalResolutionLbl = new JLabel("Vertical resolution (degrees):");
		ButtonGroup verticalResolution = new ButtonGroup();
		v90Btn.setSelected(true);
		v180Btn.setSelected(false);
		verticalResolution.add(v90Btn);
		verticalResolution.add(v180Btn);

		v90Btn.addActionListener(v90Listener);
		v180Btn.addActionListener(v180Listener);

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

		transparentSky.setText("transparent sky");
		transparentSky.setToolTipText("Disables rendering the skybox");
		transparentSky.addActionListener(transparentSkyListener);

		JLabel fogColorLbl = new JLabel("<html>Hint: set fog density &gt; 0.1 for thick fog,<br>set it &lt; 0.1 for haze/atmosphere effect");
		fogColorBtn.setIcon(Icon.colors.imageIcon());
		fogColorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPicker picker = new ColorPicker(fogColorBtn, renderMan.scene().getFogColor());
				picker.addColorListener(new ColorListener() {
					@Override
					public void onColorPicked(Vector3d color) {
						renderMan.scene().setFogColor(color);
					}
				});
			}
		});

		cloudsEnabled.setText("enable clouds");
		cloudsEnabled.addActionListener(cloudsEnabledListener);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
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
				.addComponent(transparentSky)
				.addComponent(cloudsEnabled)
				.addGroup(cloudSize.horizontalGroup(layout))
				.addGroup(cloudXOffset.horizontalGroup(layout))
				.addGroup(cloudYOffset.horizontalGroup(layout))
				.addGroup(cloudZOffset.horizontalGroup(layout))
				.addGroup(fogDensity.horizontalGroup(layout))
				.addGroup(layout.createSequentialGroup()
					.addComponent(fogColorBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(fogColorLbl)
				)
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
				.addComponent(transparentSky)
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
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(fogDensity.verticalGroup(layout))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup()
					.addComponent(fogColorBtn)
					.addComponent(fogColorLbl)
				)
				.addContainerGap()
		);
	}

	@Override
	public void refreshSettings() {
		skyHorizonOffset.update();
		updateSkyGradient();
		updateSkyMode();
		updateSkyRotation();
		updateVerticalResolution();
		updateTransparentSky();
		fogDensity.update();
		updateCloudsEnabledCheckBox();
		cloudSize.update();
		cloudXOffset.update();
		cloudYOffset.update();
		cloudZOffset.update();
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

	protected void updateTransparentSky() {
		transparentSky.removeActionListener(transparentSkyListener);
		transparentSky.setSelected(renderMan.scene().transparentSky());
		transparentSky.addActionListener(transparentSkyListener);
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

	protected void updateCloudsEnabledCheckBox() {
		cloudsEnabled.removeActionListener(cloudsEnabledListener);
		cloudsEnabled.setSelected(renderMan.scene().sky().cloudsEnabled());
		cloudsEnabled.addActionListener(cloudsEnabledListener);
	}
}
