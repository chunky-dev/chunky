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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneLoadingError;

/**
 * Benchmarks the renderer.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class BenchmarkManager extends AbstractRenderManager {
	/**
	 * Default number of worker threads.
	 * Is set to the number of available CPU cores.
	 */
	public static final int NUM_RENDER_THREADS_DEFAULT =
			Runtime.getRuntime().availableProcessors();

	private static final Logger logger =
			Logger.getLogger(BenchmarkManager.class);

	private final Thread[] workers;

	private final int numJobs;

	/**
 	 * The benchmark scene.
 	 */
	private final Scene scene;

	/**
	 * Next job on the job queue.
	 */
	private final AtomicInteger nextJob;

	/**
	 * Number of completed jobs.
	 */
	private final AtomicInteger finishedJobs;

	private final RenderStatusListener renderListener;

	private final static String BENCHMARK_NAME = "benchmark-1";

	private int score;

	/**
	 * Constructor
	 * @param context
	 * @param renderStatusListener
	 */
	public BenchmarkManager(RenderContext context,
			RenderStatusListener renderStatusListener) {

		super(context);

		context = new EmbeddedResourceContext(context);

		renderListener = renderStatusListener;

		scene = new Scene();
		try {
			scene.loadScene(context,
					renderStatusListener, BENCHMARK_NAME);
		} catch (IOException e) {
			logger.warn("Failed to load benchmark scene!", e);
		} catch (SceneLoadingError e) {
			logger.warn("Failed to load benchmark scene!", e);
		} catch (InterruptedException e) {
			logger.warn("Interrupted while loading benchmark scene");
		}

		scene.setBufferFinalization(false);

		int canvasWidth = scene.canvasWidth();
		int canvasHeight = scene.canvasHeight();
		numJobs = ((canvasWidth+(tileWidth-1)) / tileWidth) *
				((canvasHeight+(tileWidth-1)) / tileWidth);
		nextJob = new AtomicInteger(0);
		finishedJobs = new AtomicInteger(0);

		// start worker threads
		long seed = System.currentTimeMillis();
		workers = new Thread[numThreads];
		for (int i = 0; i < numThreads; ++i) {
			workers[i] = new RenderWorker(this, i, seed + i);
			workers[i].start();
		}
	}

	@Override
	public void run() {
		try {

			long millis;

			String task = "Benchmarking";
			renderListener.setProgress(task, 0, 0, 120);

			// warm up ten iterations with JIT enabled
			java.lang.Compiler.enable();
			scene.refresh();
			for (int i = 0; i < 10; ++i) {
				renderListener.setProgress(task, i, 0, 120);
				giveTickets();
				waitOnWorkers();
			}

			// warm up ten iterations with JIT disabled
			java.lang.Compiler.disable();
			scene.refresh();
			for (int i = 0; i < 10; ++i) {
				renderListener.setProgress(task, i+10, 0, 120);
				giveTickets();
				waitOnWorkers();
			}

			// time 100 iterations with JIT disabled
			millis = System.currentTimeMillis();
			scene.refresh();
			for (int i = 0; i < 100; ++i) {
				renderListener.setProgress(task, i+20, 0, 120);
				giveTickets();
				waitOnWorkers();
			}
			millis = System.currentTimeMillis() - millis;

			int canvasWidth = scene.canvasWidth();
			int canvasHeight = scene.canvasHeight();
			long pixelsPerFrame = canvasWidth * canvasHeight;
			score = (int) ((100 * pixelsPerFrame) / (millis / 1000.0));

			renderListener.setProgress(task, 120, 0, 120);

		} catch (InterruptedException e) {
			// 3D view was closed
		} catch (Throwable e) {
			logger.error("Uncaught exception in render manager", e);
		}

		// Halt all worker threads
		for (int i = 0; i < numThreads; ++i) {
			workers[i].interrupt();
		}
	}

	private synchronized void waitOnWorkers() throws InterruptedException {
		while (finishedJobs.get() < numJobs)
			wait();
	}

	private synchronized void giveTickets() {
		nextJob.set(0);
		finishedJobs.set(0);
		notifyAll();
	}

	@Override
	public int getNextJob() throws InterruptedException {
		int jobId = nextJob.getAndIncrement();
		if (jobId >= numJobs) {
			synchronized (this) {
				do {
					wait();
					jobId = nextJob.getAndIncrement();
				} while (jobId >= numJobs);
			}
		}
		return jobId;
	}

	@Override
	public void jobDone() {
		int finished = finishedJobs.incrementAndGet();
		if (finished >= numJobs) {
			synchronized (this) {
				notifyAll();
			}
		}
	}

	@Override
	public Scene bufferedScene() {
		return scene;
	}

	/**
	 * @return The benchmark score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return The name of the benchmark scene
	 */
	public String getSceneName() {
		return BENCHMARK_NAME;
	}
}
