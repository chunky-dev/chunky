/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Version;
import se.llbit.chunky.renderer.BenchmarkManager;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderStatusListener;

/**
 * Benchmark dialog.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class BenchmarkDialog extends JDialog implements RenderStatusListener {

	private static final Logger logger =
			Logger.getLogger(BenchmarkDialog.class);

	private final JProgressBar progressBar = new JProgressBar();
	private final JButton startBtn = new JButton();
	private final JButton stopBtn = new JButton();
	private BenchmarkManager benchmark = null;
	private final RenderContext context;
	private final JLabel statusLbl = new JLabel();
	private final JLabel scoreLbl = new JLabel();
	private final DecimalFormat decimalFormat;

	/**
	 * Constructor
	 * @param parent
	 * @param context
	 */
	public BenchmarkDialog(JFrame parent, RenderContext context) {
		super(parent, "Benchmark");

		decimalFormat = new DecimalFormat();
		decimalFormat.setGroupingSize(3);
		decimalFormat.setGroupingUsed(true);

		this.context = context;

		setModalityType(ModalityType.MODELESS);

		startBtn.setText("Start");
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startBenchmark();
			}
		});

		stopBtn.setText("Stop");
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopBenchmark();
			}
		});
		stopBtn.setEnabled(false);

		setStatus("not started");
		setScore("N/A");

		progressBar.setPreferredSize(new Dimension(400, 25));

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(statusLbl)
				.addComponent(scoreLbl)
				.addComponent(progressBar)
				.addGroup(layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(startBtn)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(stopBtn)
				)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(statusLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scoreLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(progressBar)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(layout.createParallelGroup()
				.addComponent(startBtn)
				.addComponent(stopBtn)
			)
			.addContainerGap()
		);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);

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
				stopBenchmark();
				if (benchmark != null)
					benchmark.interrupt();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}

	private synchronized void startBenchmark() {
		if (benchmark == null) {
			benchmark = new BenchmarkManager(context, BenchmarkDialog.this);
			benchmark.start();
			setStatus("running");
			setScore("N/A");
			startBtn.setEnabled(false);
			stopBtn.setEnabled(true);
		}
	}

	private synchronized void stopBenchmark() {
		if (benchmark != null) {
			benchmark.interrupt();
			benchmark = null;
			setStatus("interrupted");
			setScore("N/A");
			startBtn.setEnabled(true);
			stopBtn.setEnabled(false);
			progressBar.setValue(0);
		}
	}

	private void setStatus(String statusText) {
		statusLbl.setText("Status: " + statusText);
	}

	private void setScore(String scoreText) {
		scoreLbl.setText("Score: " + scoreText);
	}

	@Override
	public synchronized void setProgress(String task, int done, int start, int target) {
		progressBar.setMaximum(target);
		progressBar.setMinimum(start);
		progressBar.setValue(done);
		if (done == target && benchmark != null) {
			String benchmarkScene = benchmark.getSceneName();
			int score = benchmark.getScore();
			benchmark = null;
			startBtn.setEnabled(true);
			stopBtn.setEnabled(false);
			setStatus("completed");
			setScore(decimalFormat.format(score));

			recordBenchmarkScore(benchmarkScene, score);
		} else {
			setStatus("running (" + ((done*100)/target) + "%)");
			setScore("N/A");
		}
	}

	/**
	 * Record the benchmark score in the benchmark log file
	 * @param benchmarkScene
	 * @param score
	 */
	public static void recordBenchmarkScore(String benchmarkScene, int score) {
		try {
			// append score to benchmark log file
			File benchmarkFile = new File(PersistentSettings.getSettingsDirectory(), "benchmark.log");
			PrintStream out = new PrintStream(new FileOutputStream(benchmarkFile, true));
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
			out.println(String.format("%s,%s,%s,%d",
					fmt.format(new Date()), Version.getVersion(),
					benchmarkScene, score));
			out.close();
		} catch (IOException e) {
			logger.warn("Failed to append benchmark result to benchmark log file", e);
		}
	}

	@Override
	public void setProgress(String task, int done, int start, int target,
			String eta) {
		setProgress(task, done, start, target);
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
	public void chunksLoaded() {
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
	public void renderStateChanged(boolean pathTrace, boolean paused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderJobFinished(long time, int sps) {
		// TODO Auto-generated method stub
	}

	@Override
	public void renderResetPrevented() {
		// TODO Auto-generated method stub

	}
}
