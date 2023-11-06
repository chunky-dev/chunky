package se.llbit.imageformats.tiff;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public enum CompressionType {
  NONE(0x0001),
  DEFLATE(0x80B2);

  final short id;

  CompressionType(int id) {
    this.id = (short) id;
  }

  void writePixelData(
    FinalizableBFCOutputStream out,
    ImageFileDirectory.PixelDataWriter writer
  ) throws IOException {
    switch (this) {
      case NONE:
        writer.writePixelData(out);
        out.flush();
        break;

      case DEFLATE:
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, false);
        DeflaterOutputStream deflOut = new DeflaterOutputStream(out, deflater, 16 * 1024, true);
        writer.writePixelData(new DataOutputStream(deflOut));
        deflOut.finish();
        deflater.end();
        break;
    }
  }
}
