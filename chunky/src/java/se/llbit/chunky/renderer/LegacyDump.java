package se.llbit.chunky.renderer;

import java.io.DataInput;
import java.io.IOException;

/**
 * Holds data stored in a legacy render dump.
 */
public class LegacyDump {
  public int width, height;
  public int spp;
  public long renderTime;
  public double[] samples;

  public static LegacyDump from(DataInput inputStream, int width) throws IOException {
    int height = inputStream.readInt();
    int spp = inputStream.readInt();
    long renderTime = inputStream.readLong();

    double[] samples = new double[width * height * 3];

    for (int index = 0; index < samples.length; ++index) {
      samples[index] = inputStream.readDouble();
    }

    LegacyDump dump = new LegacyDump();
    dump.width = width;
    dump.height = height;
    dump.spp = spp;
    dump.renderTime = renderTime;
    dump.samples = samples;

    return dump;
  }
}
