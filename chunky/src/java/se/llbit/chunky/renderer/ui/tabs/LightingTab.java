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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.renderer.ui.ColorListener;
import se.llbit.chunky.renderer.ui.ColorPicker;
import se.llbit.chunky.renderer.ui.RenderControls;
import se.llbit.chunky.world.Icon;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3d;
import se.llbit.ui.Adjuster;

public class LightingTab extends RenderControlsTab {

	private final JButton changeSunColorBtn = new JButton("Change Sun Color");
	private final JCheckBox enableEmitters = new JCheckBox();
	private final JCheckBox directLight = new JCheckBox();

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
			setSliderMin(0.01);
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

	public LightingTab(RenderControls renderControls) {
		super(renderControls);

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

		JLabel bulbIcon = new JLabel(Icon.light.imageIcon());
		JLabel sunIcon = new JLabel(Icon.sun.imageIcon());

		enableEmitters.setText("enable emitters");
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		enableEmitters.addActionListener(emittersListener);

		emitterIntensity.update();

		sunIntensity.update();

		skyLight.update();

		sunAzimuth.update();

		sunAltitude.update();

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(bulbIcon)
					.addComponent(enableEmitters)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(sunIcon)
					.addComponent(directLight)
				)
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
			.addGroup(layout.createParallelGroup()
				.addComponent(bulbIcon)
				.addComponent(enableEmitters)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(emitterIntensity.verticalGroup(layout))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(sunIcon)
				.addComponent(directLight)
			)
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
	}

	@Override
	public void refreshSceneData() {
		emitterIntensity.update();
		skyLight.update();
		sunIntensity.update();
		sunAzimuth.update();
		sunAltitude.update();
		enableEmitters.setSelected(renderMan.scene().getEmittersEnabled());
		directLight.setSelected(renderMan.scene().getDirectLight());
	}

}
