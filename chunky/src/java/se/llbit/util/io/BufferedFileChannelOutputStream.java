package se.llbit.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

/**
 * Buffered output stream which writes into a file channel.
 *
 * <p>Note: Buffering is implemented with assumption that no data larger that the buffer size
 * is written in a single operation. Otherwise a {@link java.nio.BufferOverflowException}
 * will be thrown.
 */
public class BufferedFileChannelOutputStream extends OutputStream
  implements RepositionableMeasurableDataOutput {

  private final FileChannel fileChannel;
  private final ByteBuffer buffer;

  public BufferedFileChannelOutputStream(
    FileChannel fileChannel,
    int bufferSize
  ) {
    this.fileChannel = fileChannel;
    assert bufferSize >= 8;
    buffer = ByteBuffer.allocate(bufferSize);
  }

  public BufferedFileChannelOutputStream(
    FileChannel fileChannel
  ) {
    this(fileChannel, 64 * 1024);
  }

  public FileChannel getChannel() {
    return fileChannel;
  }

  /**
   * Repositions the stream.
   * <p>Note: This method performs a {@link #flush()} before changing the underlying {@link FileChannel} position.
   */
  @Override
  public void position(long newPosition) throws IOException {
    flushBuffer();
    fileChannel.position(newPosition);
  }

  @Override
  public long position() throws IOException {
    return fileChannel.position() + buffer.position();
  }

  /**
   * Returns the length of the underlying {@link FileChannel}.
   * <p>Note: This method performs a {@link #flush()} before detecting the length.
   * @return the size of the underlying {@link FileChannel}.
   */
  @Override
  public long length() throws IOException {
    flush();
    return fileChannel.size();
  }

  @Override
  public void write(int b) throws IOException {
    if(!buffer.hasRemaining()) {
      flushBuffer();
    }
    buffer.put((byte) b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    if(buffer.remaining() < b.length) {
      flushBuffer();
    }
    buffer.put(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    Objects.checkFromIndexSize(off, len, b.length);
    if(buffer.remaining() < len) {
      flushBuffer();
    }
    buffer.put(b, off, len);
  }

  @Override
  public void skip(int byteCount) throws IOException {
    if(buffer.remaining() >= byteCount) {
      buffer.position(buffer.position() + byteCount);
    } else {
      flushBuffer();
      fileChannel.position(fileChannel.position() + byteCount);
    }
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    writeByte(v ? 1 : 0);
  }

  @Override
  public void writeByte(int v) throws IOException {
    if(!buffer.hasRemaining()) {
      flushBuffer();
    }
    buffer.put((byte) v);
  }

  @Override
  public void writeShort(int v) throws IOException {
    if(buffer.remaining() < 2) {
      flushBuffer();
    }
    buffer.putShort((short) v);
  }

  @Override
  public void writeChar(int v) throws IOException {
    if(buffer.remaining() < 2) {
      flushBuffer();
    }
    buffer.putChar((char) v);
  }

  @Override
  public void writeInt(int v) throws IOException {
    if(buffer.remaining() < 4) {
      flushBuffer();
    }
    buffer.putInt(v);
  }

  @Override
  public void writeLong(long v) throws IOException {
    if(buffer.remaining() < 8) {
      flushBuffer();
    }
    buffer.putLong(v);
  }

  @Override
  public void writeFloat(float v) throws IOException {
    if(buffer.remaining() < 4) {
      flushBuffer();
    }
    buffer.putFloat(v);
  }

  @Override
  public void writeDouble(double v) throws IOException {
    if(buffer.remaining() < 8) {
      flushBuffer();
    }
    buffer.putDouble(v);
  }

  @Override
  public void writeBytes(String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeChars(String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeUTF(String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  private void flushBuffer() throws IOException {
    buffer.flip();
    fileChannel.write(buffer);
    buffer.clear();
  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be written out to the storage device from which the {@link FileChannel} was created.
   * If this channel's file resides on a local storage device then when this method returns it is guaranteed that all changes made to the file will have been written to that device.
   */
  @Override
  public void flush() throws IOException {
    flushBuffer();
    fileChannel.force(false);
  }

  /**
   * {@link #flush()} this stream, then closes the associated {@link FileChannel} of this stream.
   * The closed stream cannot perform output operations and cannot be reopened.
   */
  @Override
  public void close() throws IOException {
    flush();
    fileChannel.close();
  }
}
