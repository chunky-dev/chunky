/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;
import org.apache.commons.math3.util.FastMath;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Dedicated error reporting dialog.
 *
 * Used to display critical errors in a nicer way.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class ChunkyErrorDialog extends JDialog {

	private JTabbedPane tabbedPane;
	private int errorCount = 0;

	/**
	 * Initialize the error dialog.
	 */
	public ChunkyErrorDialog() {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Error Summary");
		setLocationRelativeTo(null);

		tabbedPane = new JTabbedPane();
		JPanel panel = new JPanel();
		JLabel lbl = new JLabel("<html>Oops! An unexpected error occurred.<br><br>" +
				"Below is a detailed description of what went wrong.");

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup()
							.addComponent(lbl)
							.addComponent(tabbedPane))
					.addContainerGap());
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(lbl)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(tabbedPane)
				.addContainerGap());

		getContentPane().add(panel);
		pack();
	}

	/**
	 * Add a log record to be displayed by this error dialog.
	 * @param event
	 */
	public synchronized void addRecord(LoggingEvent event) {
		errorCount += 1;

		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		final JTextArea textArea = new JTextArea(10, 60);
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		textArea.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				Action[] actions = textArea.getActions();
				for (Action action: actions) {
					if (action.getValue(Action.NAME).equals(
							DefaultEditorKit.selectAllAction))

						action.actionPerformed(null);
				}
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

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(byteOut);
		out.println(event.getMessage());
		out.println();

		ThrowableInformation throwableInfo = event.getThrowableInformation();
		if (throwableInfo != null) {
			Throwable thrown = throwableInfo.getThrowable();
			thrown.printStackTrace(out);
		}
		out.close();

		textArea.setText(new String(byteOut.toByteArray()));

		final JScrollPane textScrollPane = new JScrollPane(textArea);

		JButton dismissBtn = new JButton("Dismiss");
		dismissBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.remove(panel);
				if (tabbedPane.getTabCount() == 0)
					ChunkyErrorDialog.this.dispose();
			}
		});

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
							.addComponent(textScrollPane)
							.addComponent(dismissBtn))
					.addContainerGap());
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(textScrollPane)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(dismissBtn)
				.addContainerGap());

		tabbedPane.addTab("Error " + errorCount, panel);
		tabbedPane.setSelectedComponent(panel);
		pack();

		setVisible(true);
		textArea.requestFocus();
	}

}
