/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import se.llbit.math.QuickMath;

/**
 * Adjusts a rendering parameter.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class Adjuster implements ChangeListener, ActionListener,
		DocumentListener {
	private final JLabel lbl;
	private final JLabel errorLbl = new JLabel();
	private final JSlider slider;
	private final JTextField textField;
	private double min;
	private double max;
	private final boolean integerMode;
	private final Color errBGColor = new Color(0xfff5bc);

	/**
	 * Logarithmic mode flag.
	 */
	private boolean logarithmic = false;

	/**
	 * Clamp values to minimum
	 */
	private boolean clampMin = true;

	/**
	 * Clamp values to maximum
	 */
	private boolean clampMax = true;

	/**
	 * Number format for current locale.
	 */
	private static final NumberFormat numberFormat =
			NumberFormat.getInstance();

	/**
	 * Create new double value adjuster
	 * @param label
	 * @param tip
	 * @param min
	 * @param max
	 */
	public Adjuster(String label, String tip, double min, double max) {
		this.min = min;
		this.max = max;
		lbl = new JLabel(label + ":");
		slider = new JSlider(1, 100);
		slider.setToolTipText(tip);
		slider.addChangeListener(this);
		textField = new JTextField(5);
		textField.addActionListener(this);
		textField.getDocument().addDocumentListener(this);
		integerMode = false;
		errorLbl.setForeground(Color.red);
		errorLbl.setBackground(errBGColor);
		errorLbl.setOpaque(true);
		errorLbl.setVisible(false);
	}

	/**
	 * Create new integer value adjuster
	 * @param label
	 * @param tip
	 * @param min
	 * @param max
	 */
	public Adjuster(String label, String tip, int min, int max) {
		this.min = min;
		this.max = max;
		lbl = new JLabel(label + ":");
		slider = new JSlider(min, max);
		slider.setToolTipText(tip);
		slider.addChangeListener(this);
		textField = new JTextField(5);
		textField.addActionListener(this);
		textField.getDocument().addDocumentListener(this);
		integerMode = false;
		errorLbl.setForeground(Color.red);
		errorLbl.setBackground(errBGColor);
		errorLbl.setOpaque(true);
		errorLbl.setVisible(false);
	}

	/**
	 * Set logarithmic mode for the slider
	 */
	public void setLogarithmicMode() {
		logarithmic = true;
	}

	/**
	 * Select clamping mode
	 * @param mode <code>true</code> means clamping is enabled
	 */
	public void setClampMin(boolean mode) {
		clampMin = mode;
	}

	/**
	 * Select clamping mode
	 * @param mode <code>true</code> means clamping is enabled
	 */
	public void setClampMax(boolean mode) {
		clampMax = mode;
	}

	/**
	 * @param layout
	 * @return horizontal layout group
	 */
	public Group horizontalGroup(GroupLayout layout) {
		return layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(lbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(slider)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField)
					.addPreferredGap(ComponentPlacement.RELATED)
				)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(errorLbl)
				)
				;
	}

	/**
	 * @param layout
	 * @return vertical layout group
	 */
	public Group verticalGroup(GroupLayout layout) {
		return layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lbl)
						.addComponent(slider)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(errorLbl);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		onTextEdit(true);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (onTextEdit(false)) {
			setError("Warning: value out of bounds!");
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (onTextEdit(false)) {
			setError("Warning: value out of bounds!");
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (onTextEdit(false)) {
			setError("Warning: value out of bounds!");
		}
	}

	/**
	 * @param update whether to update the text after parsing to reflect the
	 * current state
	 * @return {@code true} if the value was clamped
	 */
	private boolean onTextEdit(boolean update) {
		try {
			double value = numberFormat.parse(textField.getText()).doubleValue();
			double sliderValue = QuickMath.clamp(value, min, max);
			boolean clamped = false;
			if (clampMin && value < min) {
				value = min;
				clamped = true;
			}
			if (clampMax && value > max) {
				value = max;
				clamped = true;
			}
			setSlider(sliderValue);
			valueChanged(value);
			if (update && clamped) {
				if (integerMode) {
					textField.setText("" + (int) value);
				} else {
					textField.setText("" + value);
				}
			}
			setError("");
			errorLbl.setVisible(false);
			return clamped;
		} catch (NumberFormatException ex) {
		} catch (ParseException ex) {
			setError("Not a valid number!");
		}
		return false;
	}

	private void setError(String message) {
		if (message.isEmpty()) {
			errorLbl.setVisible(false);
		} else {
			errorLbl.setText(message);
			errorLbl.setVisible(true);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// slider value changed
		JSlider source = (JSlider) e.getSource();
		double value;
		if (logarithmic) {
			value = (double) (source.getValue() - source.getMinimum())
					/ (source.getMaximum() - source.getMinimum());
			double logMin = Math.log(min);
			double logMax = Math.log(max);
			double scale = logMax - logMin;
			value = Math.pow(Math.E, value * scale + logMin);
		} else {
			double scale = (max - min) / (source.getMaximum() - source.getMinimum());
			value = (source.getValue() - source.getMinimum()) * scale + min;
		}
		setTextField(value);
		valueChanged(value);
	}

	/**
	 * Set the parameter value
	 * @param value
	 */
	public void set(double value) {
		setSlider(value);
		setTextField(value);
	}

	/**
	 * Set parameter value and new min/max limits
	 * @param value
	 * @param min New minimum value
	 * @param max New maximum value
	 */
	public void set(double value, double min, double max) {
		this.min = min;
		this.max = max;
		setSlider(value);
		setTextField(value);
	}

	private void setSlider(double value) {
		int sliderValue;
		if (logarithmic) {
			double logMin = Math.log(min);
			double logMax = Math.log(max);
			double logValue = (Math.log(value) - logMin) / (logMax - logMin);
			double scale = slider.getMaximum() - slider.getMinimum();
			sliderValue = (int) (logValue * scale + slider.getMinimum());
		} else {
			double scale = (slider.getMaximum() - slider.getMinimum()) / (max - min);
			sliderValue = (int) ((value - min) * scale + 0.5 + slider.getMinimum());
		}
		setSliderValue(sliderValue);
	}

	protected void setSliderValue(int value) {
		slider.removeChangeListener(this);
		slider.setValue(value);
		slider.addChangeListener(this);
	}

	/**
 	 * Update the text field
 	 * @param value new value
 	 */
	private void setTextField(double value) {
		if (integerMode) {
			setTextFieldText("" + (int) value);
		} else {
			setTextFieldText(String.format("%.2f", value));
		}
	}

	protected void setTextFieldText(String text) {
		textField.removeActionListener(this);
		textField.setText(text);
		textField.addActionListener(this);
	}

	/**
	 * Handles value changes
	 * @param newValue
	 */
	public abstract void valueChanged(double newValue);

	/**
	 * Update the adjuster with the current value
	 */
	public abstract void update();

	/**
	 * @return The label
	 */
	public JLabel getLabel() {
		return lbl;
	}

	/**
	 * @return The slider
	 */
	public JSlider getSlider() {
		return slider;
	}

	/**
	 * @return The text field
	 */
	public JTextField getField() {
		return textField;
	}

	/**
	 * Enable or disable the controls of this adjuster
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		lbl.setEnabled(enabled);
		slider.setEnabled(enabled);
		textField.setEnabled(enabled);
	}
}
