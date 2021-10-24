package se.llbit.chunky.renderer.renderdump;

import se.llbit.chunky.renderer.scene.Scene;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;

public class UncompressedDumpFormat extends AbstractDumpFormat {
  public static final UncompressedDumpFormat INSTANCE = new UncompressedDumpFormat();

  private UncompressedDumpFormat() {}

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
  protected void readSamples(DataInputStream inputStream, Scene scene,
                             PixelConsumer consumer, IntConsumer pixelProgress)
      throws IOException {
    int numPixels = scene.getSampleBuffer().length / 3;
    for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
      double r = inputStream.readDouble();
      double g = inputStream.readDouble();
      double b = inputStream.readDouble();
      consumer.consume(pixelIndex, r, g, b);
      pixelProgress.accept(pixelIndex);
    }
  }

  @Override
  protected void writeSamples(DataOutputStream outputStream, Scene scene,
                              IntConsumer pixelProgress)
      throws IOException {
    double[] samples = scene.getSampleBuffer();
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
