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
		if (!first)
			System.out.print("\r");
		first = false;
		System.out.print(String.format("%s: %.1f%% (%s of %s)",
				task, 100 * done / (float) target,
				decimalFormat.format(done), decimalFormat.format(target)));

		if (done == target) {
			System.out.println();
			first = true;
		}
	}

	@Override
	public void setProgress(String task, int done, int start, int target, String eta) {
		if (!first)
			System.out.print("\r");
		first = false;
		System.out.print(String.format("%s: %s of %s (ETA=%s)",
				task, done / (float) target,
				decimalFormat.format(done), decimalFormat.format(target), eta));

		if (done == target) {
			System.out.println();
			first = true;
		}
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

}
