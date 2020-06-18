package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.util.concurrent.BlockingQueue;

/**
 * This frame sampler takes the same amount of samples for each pixel.
 */
public class UniformFrameSampler extends FrameSampler {

  // TODO Document all the member values:
  private int spp;

  private final int tileWidth;
  private final int sppPerFrame;
  private final int width;
  private final int height;
  private final double[] samples;

  private volatile RenderTask[] tiles = new RenderTask[0];
  private int numJobs = 0;

  public UniformFrameSampler(Scene scene, int tileWidth, int sppPerFrame) {
    int width = scene.width;
    int height = scene.height;

    this.spp = 0;

    this.tileWidth = tileWidth;
    this.sppPerFrame = sppPerFrame;
    this.width = width;
    this.height = height;
    this.samples = new double[width * height * 3];

    initializeTiles();
  }

  /**
   * Assign render jobs to tiles of the canvas.
   */
  private void initializeTiles() {
    int xjobs = (width + (tileWidth - 1)) / tileWidth;
    int yjobs = (height + (tileWidth - 1)) / tileWidth;
    numJobs = xjobs * yjobs;
    if (tiles.length != numJobs) {
      tiles = new RenderTask[numJobs];
    }
    for (int job = 0; job < numJobs; ++job) {
      // Calculate pixel bounds for this job.
      int x0 = tileWidth * (job % xjobs);
      int x1 = Math.min(x0 + tileWidth, width);
      int y0 = tileWidth * (job / xjobs);
      int y1 = Math.min(y0 + tileWidth, height);
      tiles[job] = new RenderTask(x0, x1, y0, y1);
    }
  }

  @Override
  public void sampleFrame(BlockingQueue<RenderTask> jobQueue) throws InterruptedException {
    for (RenderTask task : tiles) {
      jobQueue.put(task);
    }
  }

  @Override
  public void onFrameFinish() {
    spp += sppPerFrame;
  }

  @Override
  public void addSample(int x, int y, double r, double g, double b, int sample_count) {
    int index = (x + y * width) * 3;

    samples[index + 0] = (samples[index + 0] * spp + r * sample_count) / (spp + sample_count);
    samples[index + 1] = (samples[index + 1] * spp + g * sample_count) / (spp + sample_count);
    samples[index + 2] = (samples[index + 2] * spp + b * sample_count) / (spp + sample_count);
  }

  @Override
  protected void gatherRadiance(int x, int y, double[] result) {
    int index = (x + y * width) * 3;

    result[0] = samples[index + 0];
    result[1] = samples[index + 1];
    result[2] = samples[index + 2];
  }

  @Override
  public void mergeWith(FrameSampler otherSampler, TaskTracker taskTracker) throws IllegalArgumentException {
    try (TaskTracker.Task task = taskTracker.task("Merge dumps")) {
      if (!(otherSampler instanceof UniformFrameSampler)) {
        throw new IllegalArgumentException(
                "A UniformFrameSampler can only merge with another UniformFrameSampler"
        );
      }

      UniformFrameSampler other = (UniformFrameSampler) otherSampler;
      if (other.width != this.width || other.height != this.height) {
        throw new IllegalArgumentException("Sizes differ, cannot merge");
      }

      double thisWeight = (double)spp / (spp + other.spp);
      double otherWeight = 1 - thisWeight;

      for (int y = 0; y < height; ++y) {
        task.update(height, y + 1);
        for (int x = 0; x < width; ++x) {
          int index = (x + y * width) * 3;
          this.samples[index + 0] = this.samples[index + 0] * thisWeight
                  + other.samples[index + 0] * otherWeight;
          this.samples[index + 1] = this.samples[index + 1] * thisWeight
                  + other.samples[index + 1] * otherWeight;
          this.samples[index + 2] = this.samples[index + 2] * thisWeight
                  + other.samples[index + 2] * otherWeight;
        }
      }

      spp += other.spp;
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }
}
