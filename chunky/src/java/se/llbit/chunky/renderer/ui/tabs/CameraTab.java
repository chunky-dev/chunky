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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderState;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.CameraPreset;
import se.llbit.chunky.renderer.ui.DoFAdjuster;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.renderer.ui.ViewListener;
import se.llbit.chunky.resources.MiscImages;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;
import se.llbit.ui.Adjuster;

public class CameraTab extends RenderControlsTab implements ViewListener, RenderStatusListener {

	/** Number format for current locale. */
	private final NumberFormat numberFormat = NumberFormat.getInstance();
	private final DecimalFormat decimalFormat = new DecimalFormat();

	private final JComboBox cameraPreset = new JComboBox();
	private final JComboBox customPreset = new JComboBox();
	private final JComboBox projectionMode = new JComboBox();
	private final JTextField cameraX = new JTextField();
	private final JTextField cameraY = new JTextField();
	private final JTextField cameraZ = new JTextField();
	private final JTextField cameraYaw = new JTextField();
	private final JTextField cameraPitch = new JTextField();
	private final JTextField cameraRoll = new JTextField();

	private Adjuster dof;

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

	public CameraTab(RenderControls renderControls) {
		super(renderControls);

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
				String name;
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
				String name;
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
				String name;
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
		cameraToPlayerBtn.setIcon(new ImageIcon(MiscImages.face));
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

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
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
	}

	@Override
	public void refreshSettings() {
		dof.update();
		fov.update();
		updateCameraDirection();
		updateCameraPosition();
		updateCustomPresets();
		subjectDistance.update();
		updateProjectionMode();
	}

	protected void updateProjectionMode() {
		projectionMode.removeActionListener(projectionModeListener);
		ProjectionMode mode = renderMan.scene().camera().getProjectionMode();
		projectionMode.setSelectedItem(mode);
		projectionMode.addActionListener(projectionModeListener);
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

		if (PersistentSettings.getFollowCamera()) {
			panToCamera();
		}

		onCameraStateChange();
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

		onCameraStateChange();
	}

	public void onCameraStateChange() {
		getChunky().getMap().repaint();
	}

	public void panToCamera() {
		Vector3d pos = renderMan.scene().camera().getPosition();
		getChunky().setView(pos.x / 16.0, pos.z / 16.0);
	}

	public void moveCameraTo(double x, double z) {
		Vector3d pos = new Vector3d(renderMan.scene().camera().getPosition());
		pos.x = x;
		pos.z = z;
		renderMan.scene().camera().setPosition(pos);
		updateCameraPosition();
	}

	@Override
	public void onStrafeLeft() {
		updateCameraPosition();
	}

	@Override
	public void onStrafeRight() {
		updateCameraPosition();
	}

	@Override
	public void onMoveForward() {
		updateCameraPosition();
	}

	@Override
	public void onMoveBackward() {
		updateCameraPosition();
	}

	@Override
	public void onMoveForwardFar() {
		updateCameraPosition();
	}

	@Override
	public void onMoveBackwardFar() {
		updateCameraPosition();
	}

	@Override
	public void onMoveUp() {
		updateCameraPosition();
	}

	@Override
	public void onMoveDown() {
		updateCameraPosition();
	}

	@Override
	public void onMouseDragged(int dx, int dy) {
		updateCameraDirection();
	}

	@Override
	public void setViewVisible(boolean visible) {
	}

	@Override
	public void onZoom(int diff) {
		fov.update();
		onCameraStateChange();
	}

	@Override
	public void chunksLoaded() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateCameraPosition();
			}
		});
	}

	@Override
	public void setProgress(String task, int done, int start, int target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProgress(String task, int done, int start, int target, String eta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void taskAborted(String task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void taskFailed(String task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRenderTime(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSamplesPerSecond(int sps) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSPP(int spp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sceneSaved() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sceneLoaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderStateChanged(RenderState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderJobFinished(long time, int sps) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderResetRequested() {
		// TODO Auto-generated method stub

	}
}
