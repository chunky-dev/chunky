/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import se.llbit.math.Color;
import se.llbit.math.Constants;
import se.llbit.math.Vector3d;

@SuppressWarnings("serial")
public class ColorPicker extends JDialog {

	private static final int NUM_SWATCHES = 10;

	private static final Vector3d[] staticHistory = new Vector3d[NUM_SWATCHES];
	static {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < NUM_SWATCHES; ++i) {
			staticHistory[i] = new Vector3d();
			getStochasticColor(r, staticHistory[i],
					(i+0.5)/10.0, 1, 0.5);
		}
	}

	private final JPanel currentSwatch = new JPanel();
	private final JPanel[] swatches = new JPanel[NUM_SWATCHES];
	private final JPanel[] history = new JPanel[NUM_SWATCHES];
	private final Vector3d[] swatchColors = new Vector3d[NUM_SWATCHES];
	private final Vector3d[] historyColors = new Vector3d[NUM_SWATCHES];
	private final JTextField colorHex = new JTextField(6);
	private double hue = 0;
	private double saturation = 1;
	private double lightness = 0.5;
	private final HuePicker huePicker;
	private final LightnessPicker lightnessPicker;
	private final SaturationPicker saturationPicker;
	private final Vector3d currentColor = new Vector3d();
	private final Random r = new Random(System.currentTimeMillis());
	private final DocumentListener hexColorListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
			private void update() {
				String text = colorHex.getText();
				try {
					int rgb = Integer.parseInt(text, 16);
					Vector3d color = new Vector3d();
					Color.getRGBAComponents(rgb, color);
					setColorNoHexUpdate(color);
				} catch (NumberFormatException e) {
				}
			}
		};

	private final Collection<ColorListener> listeners = new ArrayList<ColorListener>();

	public ColorPicker(JComponent parent, Vector3d color) {
		huePicker = new HuePicker(this);
		lightnessPicker = new LightnessPicker(this);
		saturationPicker = new SaturationPicker(this);

		for (int i = 0; i < NUM_SWATCHES; ++i) {
			swatchColors[i] = new Vector3d();
			swatches[i] = new JPanel();
			swatches[i].setPreferredSize(new Dimension(25, 25));
			final int index = i;
			swatches[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
				@Override
				public void mousePressed(MouseEvent e) {
					setColor(swatchColors[index]);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}

			});
		}

		for (int i = 0; i < NUM_SWATCHES; ++i) {
			historyColors[i] = new Vector3d();
			history[i] = new JPanel();
			history[i].setPreferredSize(new Dimension(25, 25));
			final int index = i;
			history[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
				@Override
				public void mousePressed(MouseEvent e) {
					setColor(historyColors[index]);
					onColorEditFinished();
				}
				@Override
				public void mouseReleased(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}

			});
		}

		setColor(color);
		onColorEditFinished();
		initHistory();
		onColorChanged();

		currentSwatch.setPreferredSize(new Dimension(300, 300));
		currentSwatch.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				onColorPicked();
				closeDialog();
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		JButton doneBtn = new JButton("Done");
		doneBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onColorPicked();
				closeDialog();
			}
		});
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				closeDialog();
			}
			@Override
			public void windowGainedFocus(WindowEvent e) {
			}
		});

		colorHex.getDocument().addDocumentListener(hexColorListener);

		setUndecorated(true);
		setAlwaysOnTop(true);
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Color Picker"));
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		Group swatchesParallel = layout.createParallelGroup();
		for (JPanel swatch: swatches) {
			swatchesParallel.addComponent(swatch);
		}
		Group swatchesSequential = layout.createSequentialGroup();
		boolean first = true;
		for (JPanel swatch: swatches) {
			if (!first) swatchesSequential.addGap(5);
			first = false;
			swatchesSequential.addComponent(swatch);
		}
		Group historyParallel = layout.createParallelGroup();
		for (JPanel swatch: history) {
			historyParallel.addComponent(swatch);
		}
		Group historySequential = layout.createSequentialGroup();
		first = true;
		for (JPanel swatch: history) {
			if (!first) historySequential.addGap(5);
			first = false;
			historySequential.addComponent(swatch);
		}
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(huePicker)
					.addComponent(lightnessPicker)
					.addComponent(saturationPicker)
					.addGroup(swatchesSequential)
					.addGroup(historySequential)
				)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(currentSwatch)
			)
			.addGroup(layout.createSequentialGroup()
				.addComponent(colorHex, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(cancelBtn)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(doneBtn)
			)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(huePicker)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lightnessPicker)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(saturationPicker)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(swatchesParallel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(historyParallel)
				)
				.addComponent(currentSwatch)
			)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(colorHex)
				.addComponent(cancelBtn)
				.addComponent(doneBtn)
			)
		);
		setContentPane(panel);
		pack();

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		getRootPane().setDefaultButton(doneBtn);

		Dimension parentSize = parent.getSize();
		Dimension size = getPreferredSize();
		Point loc = SwingUtilities.convertPoint(parent,
				getLocation(), this);
		int x = loc.x;
		int y = loc.y+parentSize.height;
		setBounds(x, y, size.width, size.height);
		setVisible(true);
	}

	protected void onColorPicked() {
		updateHistory();
		Vector3d color = new Vector3d();
		se.llbit.math.Color.RGBfromHSL(color, hue, saturation, lightness);
		for (ColorListener listener: listeners) {
			listener.onColorPicked(color);
		}
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}

	protected void setHue(double value) {
		this.hue = value;
		saturationPicker.setHueLight(hue, lightness);
		lightnessPicker.setHueSat(hue, saturation);
		Color.RGBfromHSL(currentColor, hue, saturation, lightness);
		updateHexColor();
		onColorChanged();
	}

	protected void setSaturation(double value) {
		this.saturation = value;
		lightnessPicker.setHueSat(hue, saturation);
		Color.RGBfromHSL(currentColor, hue, saturation, lightness);
		updateHexColor();
		onColorChanged();
	}

	protected void setLightness(double value) {
		this.lightness = value;
		saturationPicker.setHueLight(hue, lightness);
		Color.RGBfromHSL(currentColor, hue, saturation, lightness);
		updateHexColor();
		onColorChanged();
	}

	private void updateHexColor() {
		colorHex.getDocument().removeDocumentListener(hexColorListener);
		colorHex.setText(Color.toString(currentColor));
		colorHex.getDocument().addDocumentListener(hexColorListener);
	}

	private void onColorChanged() {
		currentSwatch.setBackground(new java.awt.Color((float)currentColor.x, (float)currentColor.y, (float)currentColor.z));
		currentSwatch.repaint();
	}

	private void setColor(Vector3d color) {
		colorHex.setText(Color.toString(color));
		setColorNoHexUpdate(color);
	}
	private void setColorNoHexUpdate(Vector3d color) {
		currentColor.set(color);
		double r = color.x;
		double g = color.y;
		double b = color.z;
		double alpha = 0.5 * (2*r - g - b);
		double beta = Math.sqrt(3) * 0.5 * (g - b);
		double angle = Math.atan2(beta, alpha);
		if (angle < 0) {
			angle = Constants.TAU + angle;
		}
		hue = angle / Constants.TAU;
		double c = Math.sqrt(alpha*alpha + beta*beta);
		lightness = 0.5*Math.max(r, Math.max(g, b)) + 0.5*Math.min(r, Math.min(g, b));
		double den = 1 - Math.abs(2*lightness - 1);
		if (den > 0) {
			saturation = Math.max(0, Math.min(1, c / den));
		} else {
			saturation = 0;
		}
		saturationPicker.setHueLight(hue, lightness);
		lightnessPicker.setHueSat(hue, saturation);
		lightnessPicker.setLightness(lightness);
		saturationPicker.setSaturation(saturation);
		huePicker.setHue(hue);
		onColorChanged();
	}

	private void initHistory() {
		synchronized (ColorPicker.class) {
			for (int i = 0; i < NUM_SWATCHES; ++i) {
				historyColors[i].set(staticHistory[i]);
			}
		}
		for (int i = 0; i < NUM_SWATCHES; ++i) {
			history[i].setBackground(Color.toAWT(historyColors[i]));
			history[i].repaint();
		}
	}

	private void updateHistory() {
		int rgb = Color.getRGB(currentColor);
		int end = NUM_SWATCHES-1;
		for (int i = 0; i < NUM_SWATCHES; ++i) {
			if (Color.getRGB(historyColors[i]) == rgb) {
				end = i;
				break;
			}
		}
		for (int i = end; i >= 1; --i) {
			historyColors[i].set(historyColors[i-1]);
		}
		historyColors[0].set(currentColor);
		synchronized (ColorPicker.class) {
			for (int i = 0; i < NUM_SWATCHES; ++i) {
				staticHistory[i].set(historyColors[i]);
			}
		}
	}

	protected void onColorEditFinished() {
		for (int i = 0; i < NUM_SWATCHES; ++i) {
			getStochasticColor(r, swatchColors[i], hue, saturation, lightness);
			swatches[i].setBackground(Color.toAWT(swatchColors[i]));
			swatches[i].repaint();
		}
	}

	private static void getStochasticColor(Random r, Vector3d color,
			double hue, double saturation, double lightness) {
		double x = r.nextDouble()*.25;
		double y = r.nextDouble()*.75;
		double z = r.nextDouble()*.25;
		int sx = r.nextInt(3)-1;
		int sy = r.nextInt(3)-1;
		int sz = r.nextInt(3)-1;
		double h = hue + sx*x*x;
		double s = Math.max(0, Math.min(1, saturation + sy*y*y));
		double l = Math.max(0, Math.min(1, lightness + sz*z*z));
		if (h > 1) h -= 1;
		else if (h < 0) h += 1;
		se.llbit.math.Color.RGBfromHSL(color, h, s, l);
	}

	public void addColorListener(ColorListener listener) {
		listeners.add(listener);
	}
}
