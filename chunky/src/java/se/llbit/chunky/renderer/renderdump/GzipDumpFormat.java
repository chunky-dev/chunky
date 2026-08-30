package se.llbit.chunky.renderer.renderdump;

import se.llbit.util.io.IsolatedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipDumpFormat extends AbstractDumpFormat {
  public static final GzipDumpFormat INSTANCE = new GzipDumpFormat();

  private GzipDumpFormat() {
  }

  @Override
  public int getVersion() {
    return 4;
  }

  @Override
  public String getName() {
    return "GZIP Compressed Dump";
  }

  @Override
  public String getDescription() {
    return "GZIP dump format.";
  }

  @Override
  public String getId() {
    return "GzipDumpFormat";
  }

  @Override
  protected void readSamples(DataInputStream inputStream, DumpMetadata header, PixelConsumer consumer, IntConsumer pixelProgress) throws IOException {
    DataInputStream in = new DataInputStream(new GZIPInputStream(inputStream));

    int numPixels = header.width() * header.height();
    for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
      double r = in.readDouble();
      double g = in.readDouble();
      double b = in.readDouble();
      consumer.consume(pixelIndex, r, g, b);
      pixelProgress.accept(pixelIndex);
    }
  }

  @Override
  protected void writeSamples(DataOutputStream outputStream, RenderDump dump, IntConsumer pixelProgress) throws IOException {
    double[] samples = dump.getSampleBuffer();
    try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new IsolatedOutputStream(outputStream)))) {
      int numPixels = samples.length / 3;
      for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
        int offset = pixelIndex * 3;
        out.writeDouble(samples[offset + 0]);
        out.writeDouble(samples[offset + 1]);
        out.writeDouble(samples[offset + 2]);
        pixelProgress.accept(pixelIndex);
      }
    }
  }
}
