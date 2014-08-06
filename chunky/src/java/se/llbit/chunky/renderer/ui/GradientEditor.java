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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.ui.TextInputDialog;
import se.llbit.chunky.ui.TextInputListener;
import se.llbit.chunky.ui.TextOutputDialog;
import se.llbit.chunky.world.Icon;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.math.Color;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * An editor for color gradients
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class GradientEditor extends JPanel implements GradientListener, TextInputListener {

	private final GradientUI gradientUI;
	private final JButton prev = new JButton("<");
	private final JButton next = new JButton(">");
	private final JButton add = new JButton("+");
	private final JButton del = new JButton("-");
	private final JButton importBtn = new JButton();
	private final JButton exportBtn = new JButton();
	private final JTextField colorEdit = new JTextField(8);
	private final JButton colorBtn = new JButton();
	private final JTextField posEdit = new JTextField(8);
	private final DocumentListener documentListener = new DocumentListener() {

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
			String text = colorEdit.getText();
			try {
				Vector3d color = new Vector3d();
				Color.fromString(text, 16, color);
				setColor(color);
			} catch (NumberFormatException e) {
			}
		}
	};

	public GradientEditor() {
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);


		Collection<Vector4d> gradient = new LinkedList<Vector4d>();
		Sky.makeDefaultGradient(gradient);
		gradientUI = new GradientUI(gradient);
		gradientUI.addGradientListener(this);
		stopSelected(gradientUI.getSelctedIndex());

		colorBtn.setIcon(Icon.colors.imageIcon());
		colorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPicker picker = new ColorPicker(colorBtn, getCurrentColor());
				picker.addColorListener(new ColorListener() {
					@Override
					public void onColorPicked(Vector3d color) {
						setColor(color);
					}
				});
			}
		});

		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gradientUI.setSelectedIndex(gradientUI.getSelctedIndex()+1);
			}
		});

		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gradientUI.setSelectedIndex(gradientUI.getSelctedIndex()-1);
			}
		});

		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gradientUI.addStop();
			}
		});

		del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gradientUI.removeStop();
			}
		});

		colorEdit.getDocument().addDocumentListener(documentListener);

		importBtn.setIcon(Icon.save.imageIcon());
		importBtn.setToolTipText("Import a gradient");
		importBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new TextInputDialog(
						"Import Gradient", "Gradient data:",
						GradientEditor.this);
				dialog.setVisible(true);
			}
		});
		exportBtn.setIcon(Icon.load.imageIcon());
		exportBtn.setToolTipText("Export current gradient");
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new TextOutputDialog(
						"Export Gradient", "Gradient data:",
						Sky.gradientJson(gradientUI.getGradient()).toCompactString());
				dialog.setVisible(true);
			}
		});

		posEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField) e.getSource();
				String text = source.getText();
				double position = Double.parseDouble(text);
				gradientUI.setPosition(position);
			}
		});

		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addComponent(posEdit)
				.addComponent(colorEdit)
				.addComponent(colorBtn)
			)
			.addGroup(layout.createSequentialGroup()
				.addComponent(prev)
				.addComponent(next)
				.addComponent(del)
				.addComponent(add)
				.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(importBtn)
				.addComponent(exportBtn)
			)
			.addComponent(gradientUI)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(posEdit)
				.addComponent(colorEdit)
				.addComponent(colorBtn)
			)
			.addComponent(gradientUI)
			.addGroup(layout.createParallelGroup()
				.addComponent(prev)
				.addComponent(next)
				.addComponent(del)
				.addComponent(add)
				.addComponent(importBtn)
				.addComponent(exportBtn)
			)
		);
	}

	protected void setColor(Vector3d newColor) {
		gradientUI.setColor(newColor);
	}

	protected Vector3d getCurrentColor() {
		int index = gradientUI.getSelctedIndex();
		Vector4d stop = gradientUI.getStop(index);
		return new Vector3d(stop.x, stop.y, stop.z);
	}

	public void addGradientListener(GradientListener listener) {
		gradientUI.addGradientListener(listener);
	}

	public void removeGradientListener(GradientListener listener) {
		gradientUI.removeGradientListener(listener);
	}

	@Override
	public void gradientChanged(List<Vector4d> newGradient) {
		del.setEnabled(newGradient.size()>2);
	}

	@Override
	public void stopSelected(int index) {
		Vector4d stop = gradientUI.getStop(index);
		updateColorHex(stop.x, stop.y, stop.z);
		posEdit.setText(""+stop.w);
		prev.setEnabled(index > 0);
		next.setEnabled(index < gradientUI.getNumStop()-1);
	}

	@Override
	public void stopModified(int index, Vector4d stop) {
		colorEdit.setText(Color.toString(stop.x, stop.y, stop.z));
		posEdit.setText(""+stop.w);
	}

	private void updateColorHex(double x, double y, double z) {
		colorEdit.getDocument().removeDocumentListener(documentListener);
		colorEdit.setText(Color.toString(x, y, z));
		colorEdit.getDocument().addDocumentListener(documentListener);
	}

	@Override
	public void onTextInput(String data) {
		JsonParser parser = new JsonParser(new ByteArrayInputStream(data.getBytes()));
		try {
			List<Vector4d> newGradient = Sky.gradientFromJson(parser.parse().array());
			if (newGradient != null) {
				gradientUI.setGradient(newGradient);
			}
		} catch (IOException e) {
		} catch (SyntaxError e) {
		}
	}

	public void setGradient(List<Vector4d> newGradient) {
		gradientUI.setGradient(newGradient);

	}

}
