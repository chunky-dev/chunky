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

import java.util.Random;

import org.apache.log4j.Logger;

import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Ray.RayPool;
import se.llbit.util.VectorPool;

/**
 * Performs rendering work.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderWorker extends Thread {

	private static final Logger logger =
			Logger.getLogger(RenderWorker.class);

	private final int id;
	private final AbstractRenderManager manager;

	private final Ray ray;
	private final RayPool rayPool;
	private final VectorPool vectorPool;
	private final Random random;

	/**
	 * Create a new render worker, slave to a given render manager.
	 * @param manager
	 * @param id
	 * @param seed
	 */
	public RenderWorker(AbstractRenderManager manager, int id, long seed) {
		super("3D Render Worker " + id);

		this.manager = manager;
		this.id = id;
		vectorPool = new VectorPool();
		rayPool = new RayPool();
		ray = rayPool.get();
		random = new Random(seed);
	}

	@Override
	public void run() {
		try {
			try {
				while (!isInterrupted()) {
					work(manager.getNextJob());
					manager.jobDone();
				}
			} catch (InterruptedException e) {
			}
		} catch (Throwable e) {
			logger.error("Render worker " + id +
					" crashed with uncaught exception.", e);
		}
	}

	/**
	 * Perform work
	 * @param jobId
	 */
	private final void work(int jobId) {

		Scene scene = manager.bufferedScene();

		int canvasWidth = scene.canvasWidth();
		int canvasHeight = scene.canvasHeight();

		// calculate pixel bounds for this job
		int xjobs = (canvasWidth+(manager.tileWidth-1))/manager.tileWidth;
		int x0 = manager.tileWidth * (jobId % xjobs);
		int x1 = Math.min(x0 + manager.tileWidth, canvasWidth);
		int y0 = manager.tileWidth * (jobId / xjobs);
		int y1 = Math.min(y0 + manager.tileWidth, canvasHeight);

		double aspect = canvasWidth / (double) canvasHeight;
		double[][][] samples = scene.getSampleBuffer();
		final Camera cam = scene.camera();

		if (scene.pathTrace()) {

			// this is intentionally incorrectly indented for readability
			for (int x = x0; x < x1; ++x) {
			for (int y = y0; y < y1; ++y) {

			double sr = 0;
			double sg = 0;
			double sb = 0;

			for (int i = 0; i < RenderManager.SPP; ++i) {
				double oy = random.nextDouble();
				double ox = random.nextDouble();

				cam.calcViewRay(ray,
						random, aspect,
						( .5 - (x + ox) / canvasWidth ),
						(-.5 + (y + oy) / canvasHeight));

				scene.pathTrace(ray, rayPool, vectorPool, random);

				sr += ray.color.x;
				sg += ray.color.y;
				sb += ray.color.z;
			}
			double sinv = 1.0 / (scene.spp + RenderManager.SPP);
			samples[x][y][0] = (samples[x][y][0] *
					scene.spp + sr) * sinv;
			samples[x][y][1] = (samples[x][y][1] *
					scene.spp + sg) * sinv;
			samples[x][y][2] = (samples[x][y][2] *
					scene.spp + sb) * sinv;

			if (scene.finalizeBuffer())
				scene.finalizePixel(x, y);
			}}

		} else {

			// this is intentionally incorrectly indented for readability
			for (int x = x0; x < x1; ++x) {
			for (int y = y0; y < y1; ++y) {

			boolean firstFrame = scene.previewCount > 1;
			if (firstFrame) {
				if (((x+y)%2) == 0) {
					continue;
				}
			} else {
				if (((x+y)%2) != 0) {
					scene.finalizePixel(x, y);
					continue;
				}
			}

			cam.calcViewRay(ray,
					random, aspect,
					( .5 - (double)x / canvasWidth ),
					(-.5 + (double)y / canvasHeight));

			scene.quickTrace(ray, rayPool);

			samples[x][y][0] = ray.color.x;
			samples[x][y][1] = ray.color.y;
			samples[x][y][2] = ray.color.z;

			scene.finalizePixel(x, y);

			if (firstFrame) {
				if (y%2 == 0 && x < (canvasWidth-1)) {
					// copy forward
					scene.copyPixel(x + y * canvasWidth, 1);
				} else if (y%2 != 0 && x > 0) {
					// copy backward
					scene.copyPixel(x + y * canvasWidth, -1);
				}
			}

			}}
		}

	}

}
