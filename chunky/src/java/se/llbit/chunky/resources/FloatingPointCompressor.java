package se.llbit.chunky.resources;

import java.io.*;

/**
 * Implementation of the FPC algorithm
 * (compression algorithm for double precision floating pointer number)
 * http://cs.txstate.edu/~burtscher/papers/tr06.pdf
 */
public class FloatingPointCompressor {

  private static class EncoderDecoder {
    private static final int TABLE_SIZE = 1 << 10; // Must be a power of 2

    private final long[] fcm = new long[TABLE_SIZE];
    private final long[] dfcm = new long[TABLE_SIZE];
    private int fcm_hash = 0;
    private int dfcm_hash = 0;
    private long current = 0;
    private long previous = 0;
    private final byte[] smallBuffer = new byte[17]; // In the worst case, a double pair takes 17 bytes
    private int bytesWritten;

    private long predictFcm() {
      long prediction = fcm[fcm_hash];
      fcm[fcm_hash] = current;
      fcm_hash = (int) (((fcm_hash << 6) ^ (current >>> 48)) & (TABLE_SIZE - 1));
      return prediction;
    }

    private long predictDfcm() {
      long prediction = dfcm[dfcm_hash] + previous;
      dfcm[dfcm_hash] = current - previous;
      dfcm_hash = (int) (((dfcm_hash << 2) ^ ((current - previous) >>> 40)) & (TABLE_SIZE - 1));
      previous = current;
      return prediction;
    }

    private static int zeroBytes(long value) {
      return Long.numberOfLeadingZeros(value) / 8;
    }

    private static final double LN_2 = Math.log(2);

    private byte encodeSingle(double d) {
      //double storedValue = Math.log(d) / LN_2;
      long bits = Double.doubleToRawLongBits(d);

      long xoredValue = predictFcm() ^ bits;
      long dfcmXoredValue = predictDfcm() ^ bits;
      current = bits;

      int zeroBytesCount = zeroBytes(xoredValue);
      int dfcmZeroBytesCount = zeroBytes(dfcmXoredValue);

      byte choiceBit = 0;

      if(dfcmZeroBytesCount > zeroBytesCount) {
        // Choose the prediction with the most leading zero bytes
        xoredValue = dfcmXoredValue;
        zeroBytesCount = dfcmZeroBytesCount;
        choiceBit = 1;
      }

      // We use 3 bits to represent numbers between 0 and 8 inclusive (ie 9 values)
      // One of them cannot be represented, and is excluded
      // According to the paper, it is best to exclude 4
      if(zeroBytesCount == 4)
        zeroBytesCount = 3;

      // Encode the number of 0 bytes to be between 0 and 7
      byte encodedZeroBytes = (byte) zeroBytesCount;
      if(encodedZeroBytes > 4)
        --encodedZeroBytes;

      byte header = (byte) ((encodedZeroBytes << 1) | choiceBit);

      for(int byteNo = 7 - zeroBytesCount; byteNo >= 0; --byteNo) {
        smallBuffer[bytesWritten] = (byte) (xoredValue >>> (byteNo * 8));
        ++bytesWritten;
      }

      return header;
    }

    public void encodePair(double first, double second, BufferedOutputStream output) throws IOException {
      bytesWritten = 1;
      byte headerFirst = encodeSingle(first);
      byte headerSecond = encodeSingle(second);
      byte header = (byte) ((headerFirst << 4) | headerSecond);
      smallBuffer[0] = header;

      output.write(smallBuffer, 0, bytesWritten);
    }

    public void encodeSingleWithOddTerminator(double val, BufferedOutputStream output) throws IOException {
      bytesWritten = 1;
      byte headerFirst = encodeSingle(val);
      byte headerSecond = 0b1100; // Pretend we have 7 zero bytes but the 8th byte is 0 as well
      smallBuffer[bytesWritten] = 0;
      ++bytesWritten;
      byte header = (byte) ((headerFirst << 4) | headerSecond);
      smallBuffer[0] = header;

      output.write(smallBuffer, 0, bytesWritten);
    }

    public double decodeSingle(byte header, BufferedInputStream input) throws IOException {
      long prediction = predictFcm();
      long dfcmPrediction = predictDfcm();

      byte choiceBit = (byte) (header & 1);
      byte encodedZeroBytes = (byte) (header >>> 1);

      if(choiceBit == 1)
        prediction = dfcmPrediction;

      int zeroBytes = encodedZeroBytes;
      if(zeroBytes > 3)
        ++zeroBytes;

      long difference = 0;
      int byteToRead = 8 - zeroBytes;
      for(int i = 0; i < byteToRead; ++i) {
        difference = (difference << 8) | input.read();
      }

      long bits = prediction ^ difference;
      current = bits;

      return Double.longBitsToDouble(bits);
    }
  }

  public static void compress(double[] input, OutputStream output) throws IOException {
    try(BufferedOutputStream out = new BufferedOutputStream(output)) {
      if(input.length % 3 != 0)
        throw new RuntimeException("Dump doesn't have a multiple of 3 values");
      int pixels = input.length/3;
      int size = pixels-1;
      EncoderDecoder rEncoder = new EncoderDecoder();
      EncoderDecoder gEncoder = new EncoderDecoder();
      EncoderDecoder bEncoder = new EncoderDecoder();
      for(int i = 0; i < size; i += 2) {
        int idx = 3*i;
        rEncoder.encodePair(input[idx], input[idx+3], out);
        gEncoder.encodePair(input[idx+1], input[idx+4], out);
        bEncoder.encodePair(input[idx+2], input[idx+5], out);
      }

      // Add the last one and a special terminator if there is an odd number
      if(pixels % 2 == 1) {
        int idx = 3*size;
        rEncoder.encodeSingleWithOddTerminator(input[idx], out);
        gEncoder.encodeSingleWithOddTerminator(input[idx+1], out);
        bEncoder.encodeSingleWithOddTerminator(input[idx+2], out);
      }
    }
  }

  public static void decompress(double[] output, InputStream input) throws IOException {
    try(BufferedInputStream in = new BufferedInputStream(input)) {
      if(output.length % 3 != 0)
        throw new RuntimeException("Dump doesn't have a multiple of 3 values");
      int pixels = output.length/3;
      int size = pixels-1;
      EncoderDecoder rDecoder = new EncoderDecoder();
      EncoderDecoder gDecoder = new EncoderDecoder();
      EncoderDecoder bDecoder = new EncoderDecoder();
      for(int i = 0; i < size; i += 2) {
        int idx = 3*i;

        byte rGroupedHeader = (byte) in.read();
        byte rFirstHeader = (byte) (rGroupedHeader >>> 4);
        byte rSecondHeader = (byte) (rGroupedHeader & 0x0F);
        output[idx] = rDecoder.decodeSingle(rFirstHeader, in);
        output[idx+3] = rDecoder.decodeSingle(rSecondHeader, in);

        byte gGroupedHeader = (byte) in.read();
        byte gFirstHeader = (byte) (gGroupedHeader >>> 4);
        byte gSecondHeader = (byte) (gGroupedHeader & 0x0F);
        output[idx+1] = gDecoder.decodeSingle(gFirstHeader, in);
        output[idx+4] = gDecoder.decodeSingle(gSecondHeader, in);

        byte bGroupedHeader = (byte) in.read();
        byte bFirstHeader = (byte) (bGroupedHeader >>> 4);
        byte bSecondHeader = (byte) (bGroupedHeader & 0x0F);
        output[idx+2] = bDecoder.decodeSingle(bFirstHeader, in);
        output[idx+5] = bDecoder.decodeSingle(bSecondHeader, in);
      }

      // Add the last one and a special terminator if there is an odd number
      if(pixels % 2 == 1) {
        int idx = 3*size;
        byte rGroupedHeader = (byte) in.read();
        byte rFirstHeader = (byte) (rGroupedHeader >>> 4);
        byte rSecondHeader = (byte) (rGroupedHeader & 0x0F);
        output[idx] = rDecoder.decodeSingle(rFirstHeader, in);
        rDecoder.decodeSingle(rSecondHeader, in); // discard

        byte gGroupedHeader = (byte) in.read();
        byte gFirstHeader = (byte) (gGroupedHeader >>> 4);
        byte gSecondHeader = (byte) (gGroupedHeader & 0x0F);
        output[idx+1] = gDecoder.decodeSingle(gFirstHeader, in);
        gDecoder.decodeSingle(gSecondHeader, in); // discard

        byte bGroupedHeader = (byte) in.read();
        byte bFirstHeader = (byte) (bGroupedHeader >>> 4);
        byte bSecondHeader = (byte) (bGroupedHeader & 0x0F);
        output[idx+2] = bDecoder.decodeSingle(bFirstHeader, in);
        bDecoder.decodeSingle(bSecondHeader, in); // discard
      }
    }
  }
}
