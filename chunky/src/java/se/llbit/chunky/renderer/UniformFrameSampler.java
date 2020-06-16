package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Scene;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This frame sampler takes the same amount of samples for each pixel.
 */
public class UniformFrameSampler extends FrameSampler {

  // TODO Document all the member values:
  private int nextSPP;

  private final int tileWidth;
  private final int sppPerFrame;
  private final int width;
  private final double[] samples;

  private final Scene scene;
  private volatile RenderTile[] tileQueue = new RenderTile[0];
  private final Object jobMonitor = new Object();
  private int numJobs = 0, lastJob = 0;
  private final AtomicInteger nextJob = new AtomicInteger(0);

  public UniformFrameSampler(Scene scene, int tileWidth, int sppPerFrame) {
    int width = scene.width;
    int height = scene.height;

    this.nextSPP = 0;

    this.tileWidth = tileWidth;
    this.sppPerFrame = sppPerFrame;
    this.width = width;
    this.samples = new double[width * height * 3];

    this.scene = scene;

    initializeJobQueue();
  }

  /**
   * Assign render jobs to tiles of the canvas.
   */
  private void initializeJobQueue() {
    int canvasWidth = scene.canvasWidth();
    int canvasHeight = scene.canvasHeight();
    numJobs = ((canvasWidth + (tileWidth - 1)) / tileWidth)
            * ((canvasHeight + (tileWidth - 1)) / tileWidth);
    if (tileQueue.length != numJobs) {
      tileQueue = new RenderTile[numJobs];
    }
    int xjobs = (canvasWidth + (tileWidth - 1)) / tileWidth;
    for (int job = 0; job < numJobs; ++job) {
      // Calculate pixel bounds for this job.
      int x0 = tileWidth * (job % xjobs);
      int x1 = Math.min(x0 + tileWidth, canvasWidth);
      int y0 = tileWidth * (job / xjobs);
      int y1 = Math.min(y0 + tileWidth, canvasHeight);
      tileQueue[job] = new RenderTile(x0, x1, y0, y1);
    }
  }

  @Override
  public void startNewFrame() {
    nextSPP += sppPerFrame;
  }

  @Override
  public RenderTile getNextJob() throws InterruptedException {
    int job = nextJob.getAndIncrement();
    if (job >= lastJob) {
      synchronized (jobMonitor) {
        while (job >= lastJob) {
          jobMonitor.wait();
        }
      }
    }
    return tileQueue[lastJob - job - 1];
  }

  @Override
  public void addSample(int x, int y, double r, double g, double b, int sample_count) {
    int previousSPP = nextSPP - sample_count;
    int index = (x + y * width) * 3;

    samples[index + 0] = (samples[index + 0] * previousSPP + sample_count) / nextSPP;
    samples[index + 1] = (samples[index + 1] * previousSPP + sample_count) / nextSPP;
    samples[index + 2] = (samples[index + 2] * previousSPP + sample_count) / nextSPP;
  }

  @Override
  protected void gatherRadiance(int x, int y, double[] result) {
    int index = (x + y * width) * 3;

    result[0] = samples[index + 0];
    result[1] = samples[index + 1];
    result[2] = samples[index + 2];
  }
}
