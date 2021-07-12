package se.llbit.chunky.renderer.renderdump;

import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.LongConsumer;

public class RawDump extends DumpFormat {
  public static final DumpFormat INSTANCE = new RawDump();

  private RawDump() {
  }

  @Override
  public void readSamples(DataInputStream inputStream, Scene scene, PixelConsumer consumer, LongConsumer pixelProgress)
      throws IOException {
    if (scene.getSampleBuffer() == null)
      scene.initBuffers();
    final SampleBuffer sb = scene.getSampleBuffer();
    for (long index = 0; index < (long) sb.height*sb.width; index++) {
      pixelProgress.accept(index);
      consumer.consume(index, inputStream.readDouble(), inputStream.readDouble(), inputStream.readDouble());
    }
    sb.setGlobalSpp(scene.spp);
  }

  @Override
  public void writeSamples(DataOutputStream outputStream, Scene scene, LongConsumer pixelProgress) throws IOException {
    final SampleBuffer sb = scene.getSampleBuffer();
    for (int y = 0; y < sb.height; y++)
      for (int x = 0; x < sb.width; x++) {
        pixelProgress.accept((long)sb.width*y+x);
        outputStream.writeDouble(sb.get(x, y, 0));
        outputStream.writeDouble(sb.get(x, y, 1));
        outputStream.writeDouble(sb.get(x, y, 2));
      }
  }
}
