package se.llbit.chunky.renderer.renderdump;

import se.llbit.util.io.IsolatedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class HuffmanDumpFormat extends AbstractDumpFormat {
  public static final HuffmanDumpFormat INSTANCE = new HuffmanDumpFormat();

  private HuffmanDumpFormat() {
  }

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
  protected void readSamples(DataInputStream inputStream, DumpMetadata header, PixelConsumer consumer, IntConsumer pixelProgress) throws IOException {
    DataInputStream in = new DataInputStream(new InflaterInputStream(inputStream));

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
    Deflater deflater = new Deflater(Deflater.HUFFMAN_ONLY);
    try (DataOutputStream out = new DataOutputStream(
      new DeflaterOutputStream(new IsolatedOutputStream(outputStream), deflater))) {
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
