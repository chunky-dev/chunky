package se.llbit.chunky.renderer.renderdump;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.IsolatedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.zip.*;

public class HuffmanDumpFormat extends AbstractDumpFormat {
  public static final HuffmanDumpFormat INSTANCE = new HuffmanDumpFormat();

  private HuffmanDumpFormat() {}

  @Override
  public int getVersion() {
    return 3;
  }

  @Override
  public String getName() {
    return "Huffman Compressed Dump";
  }

  @Override
  public String getDescription() {
    return "Huffman compressed dump format.";
  }

  @Override
  public String getId() {
    return "HuffmanDumpFormat";
  }

  @Override
  protected void readSamples(DataInputStream inputStream, Scene scene,
                             PixelConsumer consumer, IntConsumer pixelProgress)
      throws IOException {
    DataInputStream in = new DataInputStream(new InflaterInputStream(inputStream));

    int numPixels = scene.getSampleBuffer().length / 3;
    for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
      double r = in.readDouble();
      double g = in.readDouble();
      double b = in.readDouble();
      consumer.consume(pixelIndex, r, g, b);
      pixelProgress.accept(pixelIndex);
    }
  }

  @Override
  protected void writeSamples(DataOutputStream outputStream, Scene scene,
                              IntConsumer pixelProgress)
      throws IOException {
    Deflater deflater = new Deflater(Deflater.HUFFMAN_ONLY);
    try (DataOutputStream out = new DataOutputStream(
        new DeflaterOutputStream(new IsolatedOutputStream(outputStream), deflater))) {
      double[] samples = scene.getSampleBuffer();
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
