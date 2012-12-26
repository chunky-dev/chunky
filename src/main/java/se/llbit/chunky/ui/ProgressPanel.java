/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

/**
 * A panel to display job progress.
 * 
 * <p>Jobs that use the progress panel can be interrupted
 * if they use the isInterrupted method to check the interrupted
 * status of the progress panel.
 * 
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings({"serial", "javadoc"})
public class ProgressPanel extends JPanel {
	
	private JProgressBar progress;
	private TitledBorder border;
	private JButton cancelBtn;
	private boolean interrupted = false;
	private boolean busy = false;
	private Container window;

	/**
	 * @param parent
	 */
	public ProgressPanel(Container parent) {
		this.window = parent;
		initComponents();
	}

	private void initComponents() {
		border = BorderFactory.createTitledBorder("Job progress:");
		border.setTitleColor(Color.gray);
	    setBorder(border);
	    
		progress = new JProgressBar(0, 1);
		progress.setValue(1);
		progress.setEnabled(false);
		
	    cancelBtn = new JButton();
	    cancelBtn.setText("Cancel");
	    cancelBtn.setEnabled(false);
	    cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setInterrupted();
			}
		});
	    
	    setLayout(new BorderLayout());
		add(progress, BorderLayout.NORTH);
		add(cancelBtn, BorderLayout.SOUTH);
	}
	
	protected synchronized void setInterrupted() {
		interrupted = true;
	}

	public synchronized boolean isInterrupted() {
		return interrupted;
	}
	
	public synchronized void setJobName(String jobName) {
		border.setTitle(jobName);
		repaint();
	}
	
	public synchronized void setJobSize(int size) {
		progress.setMaximum(size);
	}

	public synchronized void setProgress(int value) {
		progress.setValue(value);
	}

	public synchronized boolean tryStartJob() {
		if (busy)
			return false;
		interrupted = false;
		busy = true;
		cancelBtn.setEnabled(true);
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		return true;
	}

	public synchronized void finishJob() {
		busy = false;
		border.setTitle("Job progress:");
		progress.setValue(progress.getMaximum());
		progress.setEnabled(false);
		cancelBtn.setEnabled(false);
		repaint();
		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public synchronized boolean isBusy() {
		return busy;
	}
}
