package se.llbit.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream that has no state and does not propagate the close.
 */
public class IsolatedOutputStream extends FilterOutputStream {
  public IsolatedOutputStream(OutputStream out) {
    super(out);
  }

  @Override
  public void close() throws IOException {
    out.flush();
  }
}
