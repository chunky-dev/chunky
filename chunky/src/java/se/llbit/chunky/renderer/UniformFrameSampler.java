package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Scene;

import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * A frame sampler that takes the same amount of samples for each pixel.
 */
public class UniformFrameSampler extends FrameSampler {

  // TODO Document all the member values:
  private int spp;

  private final int width;
  private final int height;
  private final double[] samples;
  private final int sppPerFrame;
  private volatile RenderTask[] tiles;
  private final int tileWidth;

  /**
   * Creates a UniformFrameSampler from a scene.
   * @param scene the scene that defines how large the buffers must be
   * @param tileWidth the size of tiles for render workers
   * @param sppPerFrame number of samples per pixels to take each frame
   */
  public UniformFrameSampler(Scene scene, int tileWidth, int sppPerFrame) {
    int width = scene.width;
    int height = scene.height;

    this.spp = 0;

    this.width = width;
    this.height = height;
    this.samples = new double[width * height * 3];
    this.sppPerFrame = sppPerFrame;
    this.tileWidth = tileWidth;

    initializeTiles();
  }

  /**
   * Reads a UniformFrameSampler from the given input stream.
   * The format must conform with UniformFrameSampler.write().
   * @param inStream the data stream from which the uniform sample is read
   * @throws IOException
   */
  public UniformFrameSampler(DataInput inStream) throws IOException {
    this.width = inStream.readInt();
    this.height = inStream.readInt();
    this.spp = inStream.readInt();
    this.tileWidth = inStream.readInt();
    this.sppPerFrame = inStream.readInt();

    this.samples = new double[width * height];
    for (int index = 0; index < samples.length; ++index) {
      this.samples[index] = inStream.readDouble();
    }

    initializeTiles();
  }

  /**
   * Creates a UniformFrameSampler from a legacy render dump.
   * @param legacyDump the legacy dump from which data will be extracted
   * @param tileWidth the size of tiles for render workers
   * @param sppPerFrame number of samples per pixels to take each frame
   */
  public UniformFrameSampler(LegacyDump legacyDump, int tileWidth, int sppPerFrame) {
    this.width = legacyDump.width;
    this.height = legacyDump.height;
    this.spp = legacyDump.spp;
    this.samples = legacyDump.samples;

    this.sppPerFrame = sppPerFrame;
    this.tileWidth = tileWidth;

    initializeTiles();
  }

  /**
   * Assign render jobs to tiles of the canvas.
   */
  private void initializeTiles() {
    int xjobs = (width + (tileWidth - 1)) / tileWidth;
    int yjobs = (height + (tileWidth - 1)) / tileWidth;
    int numJobs = xjobs * yjobs;
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
  public void addSample(int x, int y, double r, double g, double b, int sampleCount) {
    int index = (x + y * width) * 3;

    samples[index + 0] = (samples[index + 0] * spp + r * sampleCount) / (spp + sampleCount);
    samples[index + 1] = (samples[index + 1] * spp + g * sampleCount) / (spp + sampleCount);
    samples[index + 2] = (samples[index + 2] * spp + b * sampleCount) / (spp + sampleCount);
  }

  @Override
  protected void gatherRadiance(int x, int y, double[] result) {
    int index = (x + y * width) * 3;

    result[0] = samples[index + 0];
    result[1] = samples[index + 1];
    result[2] = samples[index + 2];
  }

  @Override
  public void mergeWith(FrameSampler otherSampler) throws IllegalArgumentException {
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

    for (int index = 0; index < samples.length; ++index) {
      this.samples[index] = this.samples[index] * thisWeight + other.samples[index] * otherWeight;
    }

    spp += other.spp;
  }

  @Override
  public void write(DataOutput outStream) throws IOException {
    outStream.writeByte(Type.UNIFORM.asByte());

    outStream.writeInt(width);
    outStream.writeInt(height);
    outStream.writeInt(spp);
    outStream.writeInt(tileWidth);
    outStream.writeInt(sppPerFrame);

    for (double value : samples) {
      outStream.writeDouble(value);
    }
  }
}
