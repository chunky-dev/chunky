package se.llbit.imageformats.tiff;

import se.llbit.util.io.BufferedFileChannelOutputStream;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;

class FinalizableBFCOutputStream extends BufferedFileChannelOutputStream {

  private static final Deque<UnfinalizedData<?>> finalizationQueue = new ArrayDeque<>();

  public FinalizableBFCOutputStream(FileChannel fileChannel) {
    super(fileChannel);
  }

  void ensureAlignment() throws IOException {
    if ((position() & 0b1) != 0)
      write((byte) 0);
  }

  UnfinalizedData.Int writeUnfinalizedInt() throws IOException {
    return writeUnfinalized(new UnfinalizedData.Int((int) position()), 4);
  }
  private <T extends UnfinalizedData<?>> T writeUnfinalized(T ud, int byteCount) throws IOException {
    finalizationQueue.add(ud);
    skip(byteCount);
    return ud;
  }

  /**
   * writes remaining unfinalized data
   */
  public void doFinalization() throws IOException {
    for(UnfinalizedData<?> data : finalizationQueue) {
      data.write(this);
    }
    finalizationQueue.clear();
  }

  /**
   * does finalization, then closes the output stream
   */
  @Override
  public void close() throws IOException {
    doFinalization();
    super.close();
  }

  static abstract class UnfinalizedData<T> {
    final long position;
    protected T data;

    UnfinalizedData(long position) {
      this.position = position;
    }

    public void setData(T data) {
      this.data = data;
    }

    public T getData() {
      return data;
    }

    public void write(FinalizableBFCOutputStream out) throws IOException {
      out.position(position);
      if(data != null) {
        writeData(out);
      }
    }

    abstract void writeData(FinalizableBFCOutputStream out) throws IOException;

    static class Int extends UnfinalizedData<Integer> {
      Int(long position) {
        super(position);
      }

      @Override
      void writeData(FinalizableBFCOutputStream out) throws IOException {
        out.writeInt(data);
      }
    }
  }
}