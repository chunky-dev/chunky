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
package se.llbit.chunky.renderer;

import java.text.DecimalFormat;

/**
 * Prints the render progress to the console.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ConsoleRenderListener implements RenderStatusListener {

	private boolean first = true;
	private final DecimalFormat decimalFormat;

	/**
	 * Constructor
	 */
	public ConsoleRenderListener() {
		decimalFormat = new DecimalFormat();
		decimalFormat.setGroupingSize(3);
		decimalFormat.setGroupingUsed(true);
	}

	@Override
	public void setProgress(String task, int done, int start, int target) {
		if (!first) {
			System.out.print("\r");
		}
		first = false;
		System.out.print(String.format("%s: %.1f%% (%s of %s)",
				task, 100 * done / (float) target,
				decimalFormat.format(done), decimalFormat.format(target)));

		if (done == target) {
			System.out.println();
			System.out.flush();
			first = true;
		}
	}

	@Override
	public void setProgress(String task, int done, int start, int target, String eta) {
		if (!first) {
			System.out.print("\r");
		}
		first = false;
		System.out.print(String.format("%s: %.1f%% (%s of %s) [ETA=%s]",
				task, 100 * done / (float) target,
				decimalFormat.format(done), decimalFormat.format(target), eta));

		if (done == target) {
			System.out.println();
			System.out.flush();
			first = true;
		}
	}

	@Override
	public void taskAborted(String task) {
		if (!first) {
			System.out.print("\r");
		}
		System.out.println(task + ": ABORTED       ");
		System.out.flush();
		first = true;
	}

	@Override
	public void taskFailed(String task) {
		if (!first) {
			System.out.print("\r");
		}
		System.out.println(task + ": FAILED        ");
		System.out.flush();
		first = true;
	}

	@Override
	public void chunksLoaded() {
	}

	@Override
	public void setRenderTime(long time) {
	}

	@Override
	public void setSamplesPerSecond(int sps) {
	}

	@Override
	public void setSPP(int spp) {
	}

	@Override
	public void sceneSaved() {
	}

	@Override
	public void sceneLoaded() {
	}

	@Override
	public void renderStateChanged(boolean pathTrace, boolean paused) {
	}

	@Override
	public void renderJobFinished(long time, int sps) {
		System.out.println("Render job finished.");
		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / 60000) % 60);
		int hours = (int) (time / 3600000);
		System.out.println(String.format(
				"Total rendering time: %d hours, %d minutes, %d seconds",
				hours, minutes, seconds));
		System.out.println("Average samples per second (SPS): " + sps);
	}

	@Override
	public void renderResetPrevented() {
		System.out.println("Render reset prevented!");
	}
}
