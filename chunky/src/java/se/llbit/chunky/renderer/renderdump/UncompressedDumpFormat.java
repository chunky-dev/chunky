package se.llbit.chunky.renderer.renderdump;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;

public class UncompressedDumpFormat extends AbstractDumpFormat {
  public static final UncompressedDumpFormat INSTANCE = new UncompressedDumpFormat();

  private UncompressedDumpFormat() {
  }

  @Override
  public int getVersion() {
    return 2;
  }

  @Override
  public String getName() {
    return "Uncompressed Dump";
  }

  @Override
  public String getDescription() {
    return "Uncompressed dump format.";
  }

  @Override
  public String getId() {
    return "UncompressedDumpFormat";
  }

  @Override
  protected void readSamples(DataInputStream inputStream, DumpMetadata header, PixelConsumer consumer, IntConsumer pixelProgress) throws IOException {
    int numPixels = header.width() * header.height();
    for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
      double r = inputStream.readDouble();
      double g = inputStream.readDouble();
      double b = inputStream.readDouble();
      consumer.consume(pixelIndex, r, g, b);
      pixelProgress.accept(pixelIndex);
    }
  }

  @Override
  protected void writeSamples(DataOutputStream outputStream, RenderDump dump, IntConsumer pixelProgress) throws IOException {
    double[] samples = dump.getSampleBuffer();
    int numPixels = samples.length / 3;
    for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
      int offset = pixelIndex * 3;
      outputStream.writeDouble(samples[offset + 0]);
      outputStream.writeDouble(samples[offset + 1]);
      outputStream.writeDouble(samples[offset + 2]);
      pixelProgress.accept(pixelIndex);
    }
  }
}
