package se.llbit.util.io;

import it.unimi.dsi.fastutil.io.MeasurableStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;

import java.io.DataOutput;
import java.io.IOException;

/**
 * A repositionable {@link DataOutput} stream whose size can be measured.
  */
public interface RepositionableMeasurableDataOutput
  extends RepositionableStream, MeasurableStream, DataOutput {

  /**
   * Skips n bytes (should be equivalent to writing n 0x00 bytes).
   */
  void skip(int byteCount) throws IOException;
}
